package com.bccriskadvisory.link.rest.projectlink;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.atlassian.jira.avatar.Avatar.Size;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.user.ApplicationUser;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.connector.EdgescanV1Connector;
import com.bccriskadvisory.link.rest.edgescan.Asset;
import com.bccriskadvisory.link.rest.edgescan.EdgescanResponse;
import com.google.common.base.Joiner;

public class ProjectLinkTranslator {
	
	private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	private JiraPluginContext pluginContext;

	public ProjectLinkTranslator(JiraPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	public ProjectLinkDetails detail(ProjectLink link, ApplicationUser remoteUser){
		final ProjectLinkDetails detailedLink = new ProjectLinkDetails();
		Connection connection = pluginContext.getConnectionService().find(link.getConnectionId());
		
		detailedLink.setConnectionName(connection.getName());
		populateAssets(link, connection, detailedLink);
		populatePriorities(link, detailedLink);
		populateUserInfo(link, remoteUser, detailedLink);
		populateRemaining(link, detailedLink);

		final Optional<ZonedDateTime> dateTimeFromDate = link.getLastUpdated();
		if (dateTimeFromDate.isPresent()) {
			detailedLink.setLastUpdated(DISPLAY_FORMAT.format(dateTimeFromDate.get()));
		}
		
		return detailedLink;
	}

	private void populateRemaining(ProjectLink link, final ProjectLinkDetails detailedLink) {
		final ConstantsManager constantsManager = pluginContext.getConstantsManager();
		IssueType issueType = constantsManager.getIssueTypeObject(link.getIssueTypeId());
		detailedLink.setIssueTypeDetails(issueType.getName(), issueType.getCompleteIconUrl());
		
		Status openStatus = constantsManager.getStatusObject(link.getOpenStatusId());
		detailedLink.setOpenStatusDetails(openStatus.getName(), openStatus.getStatusCategory().getColorName());
		
		Status closeStatus = constantsManager.getStatusObject(link.getCloseStatusId());
		detailedLink.setCloseStatusDetails(closeStatus.getName(), closeStatus.getStatusCategory().getColorName());
	}

	private void populateUserInfo(ProjectLink link, ApplicationUser remoteUser, final ProjectLinkDetails detailedLink) {
		ApplicationUser user = pluginContext.getUserManager().getUser(link.getUserKey());
		URI avatarURL = pluginContext.getAvatarService().getAvatarAbsoluteURL(remoteUser, user, Size.SMALL);
		detailedLink.setUserDetails(user.getDisplayName(), avatarURL.toString());
	}

	private void populateAssets(ProjectLink link, Connection connection, final ProjectLinkDetails detailedLink) {
		Optional<List<Asset>> assets = getAssets(link, connection);
		
		if (assets.isPresent()) {
			for (Asset asset : assets.get()) {
				detailedLink.addAsset(asset.getId(), asset.getName());
			}
		}
	}

	private Optional<List<Asset>> getAssets(ProjectLink link, Connection connection) {
		EdgescanV1Connector requestFactory = new EdgescanV1Connector(pluginContext.getRequestFactory(), connection);
		
		EdgescanResponse edgescanResponse = requestFactory.assets().stringQuery("asset_id_in", Joiner.on(",").join(link.getAssets())).execute();
		
		return edgescanResponse.getAssets();
	}

	private void populatePriorities(ProjectLink link, final ProjectLinkDetails detailedLink) {
		populatePriority("Minimal", link.getMinimalPriorityId(), detailedLink);
		populatePriority("Low", link.getLowPriorityId(), detailedLink);
		populatePriority("Medium", link.getMediumPriorityId(), detailedLink);
		populatePriority("High", link.getHighPriorityId(), detailedLink);
		populatePriority("Critical", link.getCriticalPriorityId(), detailedLink);
	}

	private void populatePriority(final String riskName, final String priorityID, final ProjectLinkDetails detailedLink) {
		Priority priority = pluginContext.getConstantsManager().getPriorityObject(priorityID);
		if (priority != null) {
			detailedLink.addPriority(riskName, priority.getName(), priority.getCompleteIconUrl());
		} else {
			detailedLink.addPriority(riskName, "Don't import", null);
		}
	}
}