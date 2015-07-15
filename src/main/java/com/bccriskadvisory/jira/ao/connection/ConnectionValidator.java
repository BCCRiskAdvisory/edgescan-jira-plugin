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
		connection.setEnabled(true);
		
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
			if(!url.startsWith("https://")) {
				result.addMessage("Invalid URL format: please specify protocol");
			}
			
			if(!url.endsWith("/")) {
				result.addMessage("Invalid URL format: url must incldue a trailing '/'");
			}
		}
	}

	private void validateName(Connection connection, ValidationResult result) {
		final String name = connection.getName();
		
		if (isNotNullOrEmpty(name)) {
			final List<Connection> sameName = connectionService.findBy("name = ?", name);
			
			if (sameName.size() > 0 && sameName.get(0).getID() != connection.getID()) {
				result.addMessage("Invalid name given: connection names must be unique.");
			}
		} else {
			result.addMessage("No name given: please specify a name for this connection");
		}
	}
}