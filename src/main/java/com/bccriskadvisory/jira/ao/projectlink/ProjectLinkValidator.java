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
package com.bccriskadvisory.jira.ao.projectlink;

import java.util.List;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.user.ApplicationUser;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.validation.ValidationResult;
import com.bccriskadvisory.link.JiraPluginContext;
import com.google.common.base.Strings;

public class ProjectLinkValidator {
	
	private JiraPluginContext pluginContext;
	private boolean isCreate;

	public ProjectLinkValidator(JiraPluginContext pluginContext, boolean isCreate) {
		this.pluginContext = pluginContext;
		this.isCreate = isCreate;
	}

	public ValidationResult validate(ProjectLink link) {
		final ValidationResult validationResult = new ValidationResult();
		
		validateUniqueness(link,validationResult);
		validateConnection(link, validationResult);
		validateAssets(link, validationResult);
		validateUser(link, validationResult);
		validateIssueType(link, validationResult);
		validateOpenStatus(link, validationResult);
		validateCloseStatus(link, validationResult);
		
		return validationResult;
	}
	
	private void validateUniqueness(ProjectLink link, ValidationResult validationResult) {
		if (isCreate) {
			final List<ProjectLink> links = pluginContext.getProjectLinkService().findBy("PROJECT_KEY = ?", link.getProjectKey());
			
			if (!links.isEmpty()) {
				validationResult.addMessage("A link already exists for this project.");
			}
		}
	}
	
	private void validateConnection(ProjectLink link, ValidationResult validationResult) {
		final Integer connectionId = link.getConnectionId();
		
		if (connectionId <= 0) {
			validationResult.addMessage("No Edgescan Connection selected");
		} else {
			Connection found = pluginContext.getConnectionService().find(connectionId);
			
			if (found == null) {
				validationResult.addMessage("No Edgescan Connection with ID " + connectionId);
			}
		}
	}
	
	private void validateAssets(ProjectLink link, ValidationResult validationResult) {
		if (link.getAssets() == null || link.getAssets().isEmpty()) {
			validationResult.addMessage("No Edgescan assets selected");
		}
	}

	private void validateUser(ProjectLink link, ValidationResult validationResult) {
		final String userKey = link.getUserKey();
		
		if (invalidKey(userKey)) {
			validationResult.addMessage("No Jira User selected");
		} else {
			ApplicationUser user = pluginContext.getUserManager().getUser(userKey);
			
			if (user == null) {
				validationResult.addMessage("No User exists with Key " + userKey);
			}
		}
	}
	
	private void validateIssueType(ProjectLink link, ValidationResult validationResult) {
		final String issueTypeId = link.getIssueTypeId();
		
		if (invalidKey(issueTypeId)) {
			validationResult.addMessage("No Issue Type selected");
		} else {
			IssueType issueType = pluginContext.getConstantsManager().getIssueTypeObject(issueTypeId);
			
			if (issueType == null) {
				validationResult.addMessage("No Issue Type exists with ID " + issueTypeId);
			}
		}
	}
	
	private void validateOpenStatus(ProjectLink link, ValidationResult validationResult) {
		final String openStatusId = link.getOpenStatusId();
		
		if (invalidKey(openStatusId)) {
			validationResult.addMessage("No Status on Open selected");
		} else {
			checkStatusExists(validationResult, openStatusId);
		}
	}
	
	private void validateCloseStatus(ProjectLink link, ValidationResult validationResult) {
		final String closeStatusId = link.getCloseStatusId();
		
		if (invalidKey(closeStatusId)) {
			validationResult.addMessage("No Status on Close selected");
		} else {
			checkStatusExists(validationResult, closeStatusId);
		}
	}
	
	private boolean invalidKey(final String key) {
		return Strings.isNullOrEmpty(key) || "-1".equals(key);
	}
	
	private void checkStatusExists(ValidationResult validationResult,
			final String openStatusId) {
		Status status = pluginContext.getConstantsManager().getStatusObject(openStatusId);
		
		if (status == null) {
			validationResult.addMessage("No status exists with ID " + openStatusId);
		}
	}
}