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
