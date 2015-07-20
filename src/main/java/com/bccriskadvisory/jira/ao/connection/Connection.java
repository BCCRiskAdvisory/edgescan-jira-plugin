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
	
	public Connection() { }
	
	public Connection(ConnectionEntity entity) {
		ID = entity.getID();
		name = entity.getName();
		description = entity.getDescription();
		url = entity.getUrl();
		pollingInterval = entity.getPollingInterval();
		apiKey = entity.getApiKey();
	}
	
	@Override
	public void copyTo(ConnectionEntity other) {
		other.setName(getName());
		other.setDescription(getDescription());
		other.setUrl(getUrl());
		other.setApiKey(getApiKey());
		other.setPollingInterval(getPollingInterval());
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
}