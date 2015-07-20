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
package com.bccriskadvisory.link.rest.form;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.ProjectService.GetProjectResult;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.sal.api.net.RequestFactory;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.connection.ConnectionService;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;
import com.bccriskadvisory.jira.user.UnifiedUserManager;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.connector.EdgescanConnectionException;
import com.bccriskadvisory.link.connector.EdgescanV1Connector;
import com.bccriskadvisory.link.rest.edgescan.Asset;
import com.bccriskadvisory.link.rest.form.components.FormStructure;
import com.bccriskadvisory.link.rest.form.components.HiddenInput;
import com.bccriskadvisory.link.rest.form.components.Select;
import com.bccriskadvisory.link.utility.AbstractLogSupported;

public class ProjectLinkForm extends AbstractLogSupported {
	
	private static final String DEFAULT_SELECT = "-1";
	private final ConnectionService connectionService;
	private final ProjectService projectService;
	private final UnifiedUserManager userManager;
	private final ConstantsManager constantsManager;
	private final RequestFactory<?> requestFactory;
	private final WorkflowManager workflowManager;
	
	private ProjectLink link;
	private Project project;
	
	public ProjectLinkForm(final JiraPluginContext pluginContext) {
		this.connectionService = pluginContext.getConnectionService();
		this.projectService = pluginContext.getProjectService();
		this.userManager = pluginContext.getUserManager();
		this.requestFactory = pluginContext.getRequestFactory();
		this.constantsManager = pluginContext.getConstantsManager();
		this.workflowManager = pluginContext.getWorkflowManager();
	}
	
	public ProjectLinkForm withLink(final ProjectLink link) {
		this.link = link;
		final String projectKey = checkNotNull(link.getProjectKey());
		final GetProjectResult projectResult = projectService.getProjectByKey(projectKey);
		
		if (projectResult.isValid()) {
			project = projectResult.getProject();
		} else {
			throw new IllegalArgumentException("Could not find project with key " + projectKey);
		}
		
		return this;
	}
	
	public FormStructure build() {
		checkNotNull(project, "project");
		
		final FormStructure form = new FormStructure("project-link-form")
			.withInput(new HiddenInput("ID"))
			.withInput(new HiddenInput("projectKey"))
			.withSubmit("Save")
			.withSection(connectionSubSection())
			.withSection(priorityMappingSubsection())
			.withSection(issueCreationSubsection());

		if (link.getID() > 0) { //link has been created already
			form.withCancel("cancel");
		}
		
		return form;
	}
	
	private FormStructure connectionSubSection() {
		final Map<String, String> connectionOptions = generateConnectionOptions();
		
		final FormStructure connectionSection = 
				new FormStructure("connection-subsection", "Edgescan Connection")
					.withInput(new Select("Connection","connectionId")
						.withOptions(connectionOptions))
					.withInput(assetSelect());
		
		return connectionSection;
	}
	
	private Select assetSelect() {
		final Select input = new Select("Edgescan Assets", "assets").multiple().size(5);
		final Integer connectionId = link.getConnectionId();
		final Connection connection = connectionId != null ? connectionService.find(connectionId) : null;
		
		if(connection != null) {
			try {
				input.withOptions(generateAssetOptions(connection));
			} catch (EdgescanConnectionException e) {
				getLog().error("Unable to get assets when building form for link for project " + link.getProjectKey());
				input.withOption("", "Edgescan Connection failed!")
					.disable();
			}
		} else {
			input.withOption("", "Select connection...")
				.disable();
		}
		
		return input;
	}
	
	private FormStructure priorityMappingSubsection() {
		final Map<String, String> priorityOptions = generatePriorityOptions();
		
		return new FormStructure("priority-mappings", "Edgescan Risk Mappings")
			.withInput(new Select("Minimal", "minimalPriorityId")
				.withDefault(DEFAULT_SELECT, "Don't import")
				.withOptions(priorityOptions))
			.withInput(new Select("Low", "lowPriorityId")
				.withDefault(DEFAULT_SELECT, "Don't import")
				.withOptions(priorityOptions))
			.withInput(new Select("Medium", "mediumPriorityId")
				.withDefault(DEFAULT_SELECT, "Don't import")
				.withOptions(priorityOptions))
			.withInput(new Select("High", "highPriorityId")
				.withDefault(DEFAULT_SELECT, "Don't import")
				.withOptions(priorityOptions))
			.withInput(new Select("Critical", "criticalPriorityId")
				.withDefault(DEFAULT_SELECT, "Don't import")
				.withOptions(priorityOptions));
	}

	private FormStructure issueCreationSubsection() {
		final Map<String, String> userOptions = generateUserOptions();
		final Map<String, String> issueTypeOptions = generateIssueTypeOptions();
		final FormStructure issueSection = new FormStructure("issue-create-options", "Issue Creation")		
			.withInput(new Select("Create as User", "userKey")
				.withOptions(userOptions))
			.withInput(new Select("Create with Issue Type", "issueTypeId")
				.withOptions(issueTypeOptions));
				
		if (notSelected(link.getIssueTypeId())) {
			issueSection
				.withInput(new Select("Open with status", "openStatusId")
					.withDefault(DEFAULT_SELECT, "Select Issue Type...")
					.disable())
				.withInput(new Select("Status on Close", "closeStatusId")
					.withDefault(DEFAULT_SELECT, "Select Issue Type...")
					.disable());
		} else {
			final Map<String, String> statusOptions = generateStatusOptions(link.getIssueTypeId());
			issueSection
				.withInput(new Select("Open with status", "openStatusId")
					.withOptions(statusOptions))
				.withInput(new Select("Status on Close", "closeStatusId")
					.withOptions(statusOptions));
		}
		
		return issueSection;
	}

	private boolean notSelected(final String issueTypeId) {
		return issueTypeId == null || DEFAULT_SELECT.equals(issueTypeId);
	}

	private Map<String, String> generatePriorityOptions() {
		final Map<String, String> ret = new HashMap<>();
		
		for (final Priority priority : constantsManager.getPriorityObjects()) {
			ret.put(priority.getId(), priority.getName());
		}
		
		return ret;
	}
	
	private Map<String, String> generateIssueTypeOptions() {
		final Map<String, String> ret = new HashMap<>();

		for (final IssueType issueType : project.getIssueTypes()) {
			ret.put(issueType.getId(), issueType.getName());
		}
		
		return ret;
	}
	
	/**
	 * Dependent on the workflow (i.e combination of project and issue type)
	 */
	private Map<String, String> generateStatusOptions(final String issueTypeId) {
		final Map<String, String> ret = new HashMap<>();
		
		final JiraWorkflow projectWorkflow = workflowManager.getWorkflow(project.getId(), issueTypeId);
		
		for (final Status status : projectWorkflow.getLinkedStatusObjects()) {
			ret.put(status.getId(), status.getName());
		}
		
		return ret;
	}

	private Map<String, String> generateAssetOptions(final Connection connection) throws EdgescanConnectionException {
		final EdgescanV1Connector connector = new EdgescanV1Connector(requestFactory, connection);
		
		Optional<List<Asset>> assets = connector.assets().execute().getAssets();
		
		final Map<String, String> ret = new HashMap<>();
		
		if (assets.isPresent()) {
			for (final Asset asset : assets.get()) {
				ret.put(Integer.toString(asset.getId()), asset.getName());
			}
		}
		
		return ret;
	}
	
	private Map<String, String> generateUserOptions() {
		final Map<String, String> ret = new HashMap<>();
		
		for (final User user : userManager.getJiraUserManager().getAllUsers()) {
			if (user.isActive()) {
				ret.put(user.getName(), user.getDisplayName());
			}
		}
		
		return ret;
	}
	
	private Map<String, String> generateConnectionOptions() {
		final Map<String, String> ret = new HashMap<>();
		
		for (final Connection connection : connectionService.index()) {
			ret.put(Integer.toString(connection.getID()), connection.getName());
		}
		
		return ret;
	}
}