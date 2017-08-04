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

import static com.bccriskadvisory.link.utility.Utilities.isNotNullOrEmpty;
import static com.bccriskadvisory.link.utility.Utilities.isNullOrEmpty;

import java.util.List;

import com.bccriskadvisory.jira.ao.validation.ValidationResult;

public class ConnectionValidator {
	
	private ConnectionService connectionService;

	public ConnectionValidator(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}
	
	public void normalize(Connection connection) {
		if (isNullOrEmpty(connection.getPollingInterval())) {
			connection.setPollingInterval("60");
		}
	}
	
	public ValidationResult validate(Connection connection) {
		ValidationResult result = new ValidationResult();
		
		validateName(connection, result);
		validateUrl(connection, result);
		validateApiKey(connection, result);
		validatePollingInterval(connection, result);
		
		return result;
	}

	private void validatePollingInterval(Connection connection, ValidationResult result) {
		final String pollingInterval = connection.getPollingInterval();
		
		if (isNotNullOrEmpty(pollingInterval)) {
			try {
				Long.parseLong(pollingInterval);
			} catch (NumberFormatException e) {
				result.addMessage("Invalid polling interval given: " + pollingInterval + " is not a number.");
			}
		}
	}

	private void validateApiKey(Connection connection, ValidationResult result) {
		if (isNullOrEmpty(connection.getApiKey())) {
			result.addMessage("No API key given: please specify an API Key for this connection.");
		}
	}

	private void validateUrl(Connection connection, ValidationResult result) {
		final String url = connection.getUrl();
		
		if (isNullOrEmpty(url)) {
			result.addMessage("No URL given: please provide an edgescan url .");
		} else {
			if(!url.startsWith("https://") && !url.startsWith("http://")) {
				result.addMessage("Invalid URL format: please specify protocol");
			}
			
			if(!url.endsWith("/")) {
				result.addMessage("Invalid URL format: url must include a trailing '/'");
			}
		}
	}

	private void validateName(Connection connection, ValidationResult result) {
		final String name = connection.getName();
		
		if (isNotNullOrEmpty(name)) {
			final List<Connection> sameName = connectionService.findBy("NAME = ?", name);
			
			if (sameName.size() > 0 && sameName.get(0).getID() != connection.getID()) {
				result.addMessage("Invalid name given: connection names must be unique.");
			}
		} else {
			result.addMessage("No name given: please specify a name for this connection");
		}
	}
}