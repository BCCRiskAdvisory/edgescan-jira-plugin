package com.bccriskadvisory.jira.ao.connection;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface ConnectionEntity extends Entity {
	
	void setName(String name);
	String getName();

	void setDescription(String description);
	String getDescription();
	
	void setUrl(String url);
	String getUrl();
	
	void setApiKey(String apiKey);
	String getApiKey();
	
	void setPollingInterval(String pollingInterval);
	String getPollingInterval();
	
	void setEnabled(boolean isEnabled);
	boolean isEnabled();
}
