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