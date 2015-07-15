package com.bccriskadvisory.link.monitor;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.connection.ConnectionService;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLinkServiceImpl;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.processor.ProjectLinkImportProcessor;
import com.bccriskadvisory.link.utility.Utilities;

public class EdgescanLinkTask implements PluginJob {

	private JiraPluginContext pluginContext;

	@Override
	public void execute(final Map<String, Object> jobDataMap) {
		final EdgescanMonitorImpl monitor = (EdgescanMonitorImpl) jobDataMap.get(EdgescanMonitorImpl.KEY);
		
		pluginContext = monitor.getPluginContext();
		
		final ProjectLinkServiceImpl projectLinkService = pluginContext.getProjectLinkService();
		final ConnectionService connectionService = pluginContext.getConnectionService();
		
		for (final ProjectLink link : projectLinkService.index()) {
			if (link.isEnabled()) {
				logInActingUser(link);
				final Connection connection = connectionService.find(link.getConnectionId());
				
				if (updateDue(link, connection)) {
					new ProjectLinkImportProcessor(pluginContext, ProjectLinkImportProcessor.UPDATE_MODE, false).initWithLink(link).processImport();
				}
			}
		}
	}

	private void logInActingUser(final ProjectLink link) {
		final UserManager jiraUserManager = pluginContext.getUserManager().getJiraUserManager();
		final ApplicationUser actingUser = jiraUserManager.getUserByKey(link.getUserKey());
		
		final JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
		authenticationContext.setLoggedInUser(actingUser);
	}

	private boolean updateDue(final ProjectLink link, final Connection connection) {
		final Optional<ZonedDateTime> linkLastUpdated = link.getLastUpdated();
		final ZonedDateTime now = Utilities.now();

		if (linkLastUpdated.isPresent()) {
			final ZonedDateTime nextUpdateTime = linkLastUpdated.get().plus(Long.parseLong(connection.getPollingInterval()), ChronoUnit.MINUTES);
			return now.isAfter(nextUpdateTime);
		} else {
			return true; 
		}
	}
}