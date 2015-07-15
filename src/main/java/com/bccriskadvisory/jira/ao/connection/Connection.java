package com.bccriskadvisory.jira.ao.connection;

import com.bccriskadvisory.jira.ao.PluginObject;
import com.bccriskadvisory.link.rest.gson.GsonObject;

public class Connection extends GsonObject implements PluginObject<ConnectionEntity> {

	private int ID;
	
	private String name;
	private String description;
	private String url;
	private String pollingInterval;
	private String apiKey;
	private boolean isEnabled;
	
	public Connection() { }
	
	public Connection(ConnectionEntity entity) {
		ID = entity.getID();
		name = entity.getName();
		description = entity.getDescription();
		url = entity.getUrl();
		pollingInterval = entity.getPollingInterval();
		apiKey = entity.getApiKey();
		isEnabled = entity.isEnabled();
	}
	
	@Override
	public void copyTo(ConnectionEntity other) {
		other.setName(getName());
		other.setDescription(getDescription());
		other.setUrl(getUrl());
		other.setApiKey(getApiKey());
		other.setPollingInterval(getPollingInterval());
		other.setEnabled(isEnabled());
	}

	@Override
	public int getID() {
		return ID;
	}
	
	public void setID(int iD) {
		ID = iD;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPollingInterval() {
		return pollingInterval;
	}
	
	public void setPollingInterval(String pollingInterval) {
		this.pollingInterval = pollingInterval;
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}