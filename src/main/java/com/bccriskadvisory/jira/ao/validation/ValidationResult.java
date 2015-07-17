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
package com.bccriskadvisory.jira.ao.validation;

import java.util.List;

import com.bccriskadvisory.link.rest.PluginError;
import com.google.common.collect.Lists;

public class ValidationResult {
	
	private static final String VALIDATION_ERR = "Validation Failed";
	private List<PluginError> messages = Lists.newArrayList();

	public List<PluginError> getErrors() {
		return messages;
	}

	public void addMessage(String message) {
		this.messages.add(new PluginError(VALIDATION_ERR, message));
	}
	
	public boolean isValid() {
		return messages.isEmpty();
	}
}