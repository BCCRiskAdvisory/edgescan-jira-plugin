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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.bccriskadvisory.jira.ao.PluginObject;
import com.bccriskadvisory.link.rest.edgescan.Risk;
import com.bccriskadvisory.link.rest.gson.GsonObject;
import com.bccriskadvisory.link.utility.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProjectLink extends GsonObject implements PluginObject<ProjectLinkEntity> {

	private static final TypeToken<List<Integer>> INTEGER_LIST_TYPE = new TypeToken<List<Integer>>(){};

	private int ID;
	private String projectKey;
	private Optional<Boolean> isEnabled = Optional.empty();
	private Optional<ZonedDateTime> lastUpdated = Optional.empty();

	private Integer connectionId;
	private List<Integer> assets;
	
	private String minimalPriorityId;
	private String lowPriorityId;
	private String mediumPriorityId;
	private String highPriorityId;
	private String criticalPriorityId;

	private String userKey;
	private String issueTypeId;
	private String openStatusId;
	private String closeStatusId;

	public ProjectLink() { }
	
	public ProjectLink(String projectKey) {
		this.projectKey = projectKey; 
	}

	public ProjectLink(ProjectLinkEntity entity) {
		ID = entity.getID();
		connectionId = entity.getConnectionId();
		projectKey = entity.getProjectKey();
		isEnabled = Optional.of(entity.isEnabled());
		lastUpdated = Utilities.fromDate(entity.getLastUpdated());

		assets = new Gson().fromJson(entity.getAssets(), INTEGER_LIST_TYPE.getType());

		minimalPriorityId = entity.getMinimalPriorityId();
		lowPriorityId = entity.getLowPriorityId();
		mediumPriorityId = entity.getMediumPriorityId();
		highPriorityId = entity.getHighPriorityId();
		criticalPriorityId = entity.getCriticalPriorityId();

		userKey = entity.getUserKey();
		issueTypeId = entity.getIssueTypeId();
		openStatusId = entity.getOpenStatusId();
		closeStatusId = entity.getCloseStatusId();
	}

	@Override
	public void copyTo(ProjectLinkEntity entity) {
		entity.setConnectionId(connectionId);
		entity.setProjectKey(projectKey);
		if (isEnabled.isPresent())   entity.setEnabled(isEnabled.get());
		if (lastUpdated.isPresent()) entity.setLastUpdated(Utilities.toDate(lastUpdated));

		entity.setAssets(new Gson().toJson(assets));

		entity.setMinimalPriorityId(minimalPriorityId);
		entity.setLowPriorityId(lowPriorityId);
		entity.setMediumPriorityId(mediumPriorityId);
		entity.setHighPriorityId(highPriorityId);
		entity.setCriticalPriorityId(criticalPriorityId);

		entity.setUserKey(userKey);
		entity.setIssueTypeId(issueTypeId);
		entity.setOpenStatusId(openStatusId);
		entity.setCloseStatusId(closeStatusId);
	}

	@Override
	public int getID() {
		return ID;
	}

	public Integer getConnectionId() {
		return connectionId;
	}

	public List<Integer> getAssets() {
		return assets;
	}

	public String getUserKey() {
		return userKey;
	}

	public String getIssueTypeId() {
		return issueTypeId;
	}

	public String getOpenStatusId() {
		return openStatusId;
	}

	public String getCloseStatusId() {
		return closeStatusId;
	}

	public Optional<ZonedDateTime> getLastUpdated() {
		return lastUpdated;
	}

	public boolean isEnabled() {
		return isEnabled.get();
	}
	
	public void toggleEnabled() {
		this.isEnabled = Optional.of(!isEnabled.get());
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getMappedPriorityFromRisk(int risk) {
		return getMappedPriorityFromRisk(Risk.fromInt(risk));
	}
	
	public String getMappedPriorityFromRisk(Risk risk) {
		switch (risk) {
			case MINIMAL:
				return getMinimalPriorityId();
			case LOW:
				return getLowPriorityId();
			case MEDIUM:
				return getMediumPriorityId();
			case HIGH:
				return getHighPriorityId();
			case CRITICAL:
				return getCriticalPriorityId();
			default:
				return "-1";
		}
	}

	public boolean riskIsMapped(Risk risk) {
		return ! "-1".equals(getMappedPriorityFromRisk(risk));
	}

	public String getMinimalPriorityId() {
		return minimalPriorityId;
	}

	public String getLowPriorityId() {
		return lowPriorityId;
	}

	public String getMediumPriorityId() {
		return mediumPriorityId;
	}

	public String getHighPriorityId() {
		return highPriorityId;
	}

	public String getCriticalPriorityId() {
		return criticalPriorityId;
	}

	public void setLastUpdated(ZonedDateTime lastUpdated) {
		this.lastUpdated = Optional.ofNullable(lastUpdated);
	}
}