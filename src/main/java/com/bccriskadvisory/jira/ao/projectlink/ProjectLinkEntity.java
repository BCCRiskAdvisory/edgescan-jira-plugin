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

import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface ProjectLinkEntity extends Entity {
	
	void setConnectionId(int connectionId);
	int getConnectionId();
	
	void setProjectKey(String projectKey);
	String getProjectKey();

	void setEnabled(boolean isEnabled);
	boolean isEnabled();
	
	void setAssets(String assets);
	String getAssets();
	
	void setMinimalPriorityId(String minimalPriorityId);
	String getMinimalPriorityId();
	
	void setLowPriorityId(String lowPriorityId);
	String getLowPriorityId();;
	
	void setMediumPriorityId(String mediumPriorityId);
	String getMediumPriorityId();
	
	void setHighPriorityId(String highPriorityId);
	String getHighPriorityId();
	
	void setCriticalPriorityId(String criticalPriorityId);
	String getCriticalPriorityId();
	
	void setIssueTypeId(String issueTypeId);
	String getIssueTypeId();
	
	void setUserKey(String userId);
	String getUserKey();
	
	void setOpenStatusId(String openStatusId);
	String getOpenStatusId();

	void setCloseStatusId(String closeStatusId);
	String getCloseStatusId();
	
	void setLastUpdated(Date lastUpdated);
	Date getLastUpdated();
	
}