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
import com.bccriskadvisory.link.connector.EdgescanConnectionException;
import com.bccriskadvisory.link.connector.EdgescanV1Connector;
import com.bccriskadvisory.link.rest.edgescan.Asset;
import com.bccriskadvisory.link.rest.edgescan.EdgescanResponse;
import com.bccriskadvisory.link.utility.AbstractLogSupported;
import com.google.common.base.Joiner;

public class ProjectLinkTranslator extends AbstractLogSupported {
	
	private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	private JiraPluginContext pluginContext;

	public ProjectLinkTranslator(JiraPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	public ProjectLinkDetails detail(ProjectLink link, ApplicationUser remoteUser) {
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
		Optional<List<Asset>> assets;
		try {
			assets = getAssets(link, connection);
		} catch (EdgescanConnectionException e) {
			getLog().error("Could not retrieve edgescan assets, check configured connection");
			detailedLink.addAsset(0, "Edgescan Connection Failed!");
			return;
		}
		
		if (assets.isPresent()) {
			for (Asset asset : assets.get()) {
				detailedLink.addAsset(asset.getId(), asset.getName());
			}
		}
	}

	private Optional<List<Asset>> getAssets(ProjectLink link, Connection connection) throws EdgescanConnectionException {
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