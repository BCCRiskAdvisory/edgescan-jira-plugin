package com.bccriskadvisory.jira.ao.projectlink;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.user.ApplicationUser;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.validation.ValidationResult;
import com.bccriskadvisory.link.JiraPluginContext;
import com.google.common.base.Strings;

public class ProjectLinkValidator {
	
	private JiraPluginContext pluginContext;

	public ProjectLinkValidator(JiraPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	public ValidationResult validate(ProjectLink link) {
		final ValidationResult validationResult = new ValidationResult();
		
		validateConnection(link, validationResult);
		validateAssets(link, validationResult);
		validateUser(link, validationResult);
		validateIssueType(link, validationResult);
		validateOpenStatus(link, validationResult);
		validateCloseStatus(link, validationResult);
		
		return validationResult;
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
		if (link.getAssets().isEmpty()) {
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