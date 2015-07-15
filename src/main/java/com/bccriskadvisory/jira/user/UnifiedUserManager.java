package com.bccriskadvisory.jira.user;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;

public interface UnifiedUserManager {

	public abstract Optional<ApplicationUser> getUser(HttpServletRequest req);

	public abstract com.atlassian.jira.user.util.UserManager getJiraUserManager();

	public abstract UserManager getSalUserManager();

	public abstract ApplicationUser getUser(String userKey);

}
