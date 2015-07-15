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