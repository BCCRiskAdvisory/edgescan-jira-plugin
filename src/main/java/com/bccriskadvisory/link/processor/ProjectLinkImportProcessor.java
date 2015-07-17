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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.atlassian.jira.bc.ServiceResult;
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

public class ProjectLinkImportProcessor extends AbstractLogSupported {
	
	public static final String UPDATE_MODE = "updated";
	public static final String ALL_MODE = "all";

	private JiraPluginContext pluginContext;
	
	private ProjectLink link;
	private Connection connection;
	private EdgescanV1Connector connector;
	private VulnerabilityDetailGenerator vulnerabilityDetailGenerator;

	private String importMode;
	private boolean testMode;
	private ImportResults importResults;
	private ZonedDateTime updatedAt;

	public ProjectLinkImportProcessor(JiraPluginContext pluginContext, String importMode, boolean testMode) {
		this.pluginContext = checkNotNull(pluginContext, "Plugin context");
		
		this.importMode = importMode;
		this.testMode = testMode;
		this.importResults = new ImportResults(importMode, testMode);
	}

	public ProjectLinkImportProcessor initWithLink(ProjectLink link) {
		this.link = link;
		this.connection = pluginContext.getConnectionService().find(link.getConnectionId());
		this.connector = new EdgescanV1Connector(pluginContext.getRequestFactory(), connection);
		this.vulnerabilityDetailGenerator = new VulnerabilityDetailGenerator(connector);

		return this;
	}

	public ImportResults processImport() {
		//Should the task take a significant amount of time, there's a risk that a vulnerability will be updated in edgescan while it's running.
		//We avoid missing such vulnerabilities by taking our update time to be *before* retrieving them.
		updatedAt = now();
		
		try {
			Optional<List<Vulnerability>> vulnerabilities = getVulnerabilitiesToImport();
			
			if (vulnerabilities.isPresent() && !vulnerabilities.get().isEmpty()) {
				processVulnerabilities(vulnerabilities.get());
				setProjectLinkLastUpdated();
			}
		} catch (EdgescanConnectionException e) {
			getLog().error("Unable to get vulnerabilities for import", e);
			importResults.addError(new PluginError(e));
		}
		return importResults;
	}

	private void processVulnerabilities(final List<Vulnerability> vulnerabilities) {
		try (TimedTask linkUpdateTask = new TimedTask("Import vulnerability data from edgescan for project: " + link)) {
			for (Vulnerability vulnerability : vulnerabilities) {
				Risk edgescanRisk = Risk.fromInt(vulnerability.getRisk());
				VulnerabilityImportProcessor processor = createProcessor(vulnerability);
				
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
		}
	}

	private void setProjectLinkLastUpdated() {
		if (!testMode) {
			link.setLastUpdated(updatedAt);
			pluginContext.getProjectLinkService().update(link);
		}
	}

	private void open(VulnerabilityImportProcessor processor, Risk edgescanRisk) {
		final Optional<ServiceResult> openResult = recordValidationErrors(processor.open());
		
		if (openResult.isPresent() && openResult.get().isValid()) {
			processor.link();
			importResults.forRisk(edgescanRisk).opened();
		} else {
			importResults.forRisk(edgescanRisk).failed();
		}
	}
	
	private void close(VulnerabilityImportProcessor processor, Risk edgescanRisk) {
		final Optional<ServiceResult> closeResult = recordValidationErrors(processor.close());
		
		if (closeResult.isPresent() && closeResult.get().isValid()) {
			processor.unlink();
			importResults.forRisk(edgescanRisk).closed();
		} else {
			importResults.forRisk(edgescanRisk).failed();
		}
	}

	private void update(VulnerabilityImportProcessor processor, Risk edgescanRisk) {
		final Optional<ServiceResult> updateResult = recordValidationErrors(processor.update());
		
		if (updateResult.isPresent() && updateResult.get().isValid()) {
			importResults.forRisk(edgescanRisk).updated();
		} else {
			importResults.forRisk(edgescanRisk).failed();
		}
	}
	
	private Optional<List<Vulnerability>> getVulnerabilitiesToImport() throws EdgescanConnectionException {
		final RequestBuilder vulnerabilityRequest = connector.vulnerabilities();
		vulnerabilityRequest.stringQuery("asset_id_in", Joiner.on(",").join(link.getAssets()));
		
		if (UPDATE_MODE.equals(importMode) && link.getLastUpdated().isPresent()) {
			vulnerabilityRequest.dateQuery("updated_at_after", link.getLastUpdated().get());
		}
		
		return vulnerabilityRequest.execute().getVulnerabilities();
	}
	
	private VulnerabilityImportProcessor createProcessor(Vulnerability vulnerability) {
		final VulnerabilityImportProcessor processor = new VulnerabilityImportProcessor(pluginContext, link, vulnerability, vulnerabilityDetailGenerator);
		processor.init(testMode);
		processor.tidyDanglingLink();
		return processor;
	}
	
	private Optional<ServiceResult> recordValidationErrors(Optional<ServiceResult> result) {
		if (result.isPresent() && !result.get().isValid()) {
			for (Entry<String, String> error : result.get().getErrorCollection().getErrors().entrySet()) {
				final String errorString = String.format("Import failed on [%s], because [%s]", error.getKey(), error.getValue());
				importResults.addError(new PluginError("Import failed", errorString));
				getLog().error(errorString);
			}
		}
		return result;
	}
}