package com.bccriskadvisory.jira.user;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;

public class UnifiedUserManagerImpl implements UnifiedUserManager {

	private UserManager salUserManager;
	private com.atlassian.jira.user.util.UserManager jiraUserManager;
	
	public UnifiedUserManagerImpl(UserManager salUserManager, com.atlassian.jira.user.util.UserManager jiraUserManager) {
		this.salUserManager = salUserManager;
		this.jiraUserManager = jiraUserManager;
	}

	@Override
	public UserManager getSalUserManager() {
		return salUserManager;
	}

	@Override
	public com.atlassian.jira.user.util.UserManager getJiraUserManager() {
		return jiraUserManager;
	}
	
	@Override
	public Optional<ApplicationUser> getUser(HttpServletRequest req) {
		final ApplicationUser userByName = jiraUserManager.getUserByName(salUserManager.getRemoteUsername(req));
		return Optional.ofNullable(userByName);
	}

	@Override
	public ApplicationUser getUser(String userKey) {
		return jiraUserManager.getUserByKey(userKey);
	}
}