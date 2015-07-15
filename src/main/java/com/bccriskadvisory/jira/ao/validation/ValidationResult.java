package com.bccriskadvisory.jira.ao.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	private List<String> messages = new ArrayList<>();

	public List<String> getMessages() {
		return messages;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}
	
	public boolean isValid() {
		return messages.isEmpty();
	}
}