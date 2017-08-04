/**
 * Copyright (C) 2015 BCC Risk Advisory (info@bccriskadvisory.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bccriskadvisory.link.processor;

import static com.bccriskadvisory.link.utility.Utilities.now;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.atlassian.jira.bc.ServiceResult;
import com.atlassian.jira.bc.project.ProjectService.GetProjectResult;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.connector.EdgescanConnectionException;
import com.bccriskadvisory.link.connector.EdgescanV1Connector;
import com.bccriskadvisory.link.connector.EdgescanV1Connector.RequestBuilder;
import com.bccriskadvisory.link.rest.PluginError;
import com.bccriskadvisory.link.rest.edgescan.Risk;
import com.bccriskadvisory.link.rest.edgescan.Vulnerability;
import com.bccriskadvisory.link.utility.AbstractLogSupported;
import com.bccriskadvisory.link.utility.TimedTask;
import com.google.common.base.Joiner;

public abstract class AbstractProjectImportProcessor extends AbstractLogSupported implements ProjectImportProcessor {

	protected final JiraPluginContext pluginContext;

	private final ImportMode importMode;
	protected final boolean testMode;
	protected final ImportResults importResults;
	
	protected ProjectLink link;
	protected ApplicationUser user;
	
	private Project project;
	private EdgescanV1Connector connector;
	private VulnerabilityDetailGenerator vulnerabilityDetailGenerator;

	private DateTime importStartedAt;

	public AbstractProjectImportProcessor(final JiraPluginContext pluginContext, final ImportMode importMode, final boolean testMode) {
		this.pluginContext = checkNotNull(pluginContext, "Plugin context");
		
		this.importMode = importMode;
		this.testMode = testMode;
		this.importResults = new ImportResults(importMode, testMode);
	}

	@Override
	public ProjectImportProcessor initWithLink(final ProjectLink link) {
		this.link = link;
		Connection connection = pluginContext.getConnectionService().find(link.getConnectionId());
		this.connector = new EdgescanV1Connector(pluginContext.getRequestFactory(), connection);
		this.vulnerabilityDetailGenerator = new VulnerabilityDetailGenerator(connector);

		final UserManager jiraUserManager = pluginContext.getUserManager().getJiraUserManager();
		this.user = checkNotNull(jiraUserManager.getUserByKey(link.getUserKey()), "Could not find user with key " + link.getUserKey());
		
		GetProjectResult result = pluginContext.getProjectService().getProjectByKey(user, link.getProjectKey());
		this.project = checkNotNull(result.getProject(), "Could not find project with key " + link.getProjectKey());
		
		return this;
	}

	@Override
	public ImportResults processImport() {
		if (importLockAcquired()) {
			try {
				noteStartTime();
				
				final List<Vulnerability> vulnerabilities = getVulnerabilitiesToImport();
				
				if (!vulnerabilities.isEmpty()) {
					preProcess();
					try (TimedTask linkUpdateTask = new TimedTask("Import vulnerability data from edgescan for project: " + link)) {
						
						for (final Vulnerability vulnerability : vulnerabilities) {
							processVulnerability(vulnerability);
						}
					}
					postProcess();
				}
				
				setProjectLinkLastUpdated();
			} catch (final EdgescanConnectionException e) {
				getLog().error("Unable to get vulnerabilities for import", e);
				importResults.addError(new PluginError(e));
			} finally {
				releaseImportLock();
			}
		}
		
		return importResults;
	}

	protected abstract boolean importLockAcquired();
	protected abstract void releaseImportLock();

	private void noteStartTime() {
		//Should the task take a significant amount of time, there's a risk that a vulnerability will be updated in edgescan while it's running.
		//We avoid missing such vulnerabilities by taking our update time to be *before* retrieving them.
		importStartedAt = now();
	}

	private void setProjectLinkLastUpdated() {
		if (!testMode) {
			link.setLastUpdated(importStartedAt);
			pluginContext.getProjectLinkService().update(link);
		}
	}
	
	private List<Vulnerability> getVulnerabilitiesToImport() throws EdgescanConnectionException {
		final RequestBuilder vulnerabilityRequest = connector.vulnerabilities();
		vulnerabilityRequest.stringQuery("asset_id_in", Joiner.on(",").join(link.getAssets()));
		
		if (importMode.isUpdate() && link.getLastUpdated() != null) {
			vulnerabilityRequest.dateQuery("updated_at_after", link.getLastUpdated());
		}
		
		return vulnerabilityRequest.execute().getVulnerabilities();
	}
	
	protected abstract void preProcess();
	protected abstract void postProcess();
	
	protected void processVulnerability(Vulnerability vulnerability) {
		VulnerabilityImportProcessor processor = createProcessor(vulnerability);
		Risk edgescanRisk = Risk.fromInt(vulnerability.getRisk());
		
		if (processor.isLinked()) {
			if (link.riskIsMapped(edgescanRisk) && vulnerability.isOpen()) {
				update(processor, edgescanRisk);
			} else {
				close(processor, edgescanRisk);
			}
		} else {
			if (link.riskIsMapped(edgescanRisk) && vulnerability.isOpen()) {
				open(processor, edgescanRisk);
			} else {
				importResults.forRisk(edgescanRisk).ignored();
			}
		}
	}
	
	private VulnerabilityImportProcessor createProcessor(Vulnerability vulnerability) {
		final VulnerabilityImportProcessor processor = new VulnerabilityImportProcessor(pluginContext, vulnerabilityDetailGenerator, link, user, project, vulnerability);
		processor.init(testMode);
		return processor;
	}

	private void open(VulnerabilityImportProcessor processor, Risk edgescanRisk) {
		final ServiceResult openResult = recordValidationErrors(processor.open());
		
		if (openResult != null && openResult.isValid()) {
			processor.link();
			importResults.forRisk(edgescanRisk).opened();
		} else {
			importResults.forRisk(edgescanRisk).failed();
		}
	}
	
	private void close(VulnerabilityImportProcessor processor, Risk edgescanRisk) {
		final ServiceResult closeResult = recordValidationErrors(processor.close());
		
		if (closeResult != null && closeResult.isValid()) {
			processor.unlink();
			importResults.forRisk(edgescanRisk).closed();
		} else {
			importResults.forRisk(edgescanRisk).failed();
		}
	}

	private void update(VulnerabilityImportProcessor processor, Risk edgescanRisk) {
		final ServiceResult updateResult = recordValidationErrors(processor.update());
		
		if (updateResult != null && updateResult.isValid()) {
			importResults.forRisk(edgescanRisk).updated();
		} else {
			importResults.forRisk(edgescanRisk).failed();
		}
	}
	
	protected ServiceResult recordValidationErrors(ServiceResult result) {
		if (result != null && !result.isValid()) {
			for (Entry<String, String> error : result.getErrorCollection().getErrors().entrySet()) {
				final String errorString = String.format("Import failed on [%s], because [%s]", error.getKey(), error.getValue());
				importResults.addError(new PluginError("Import failed", errorString));
				getLog().error(errorString);
			}
		}
		return result;
	}
}