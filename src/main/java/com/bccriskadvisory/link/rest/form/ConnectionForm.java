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
package com.bccriskadvisory.link.rest.form;

import com.bccriskadvisory.link.rest.form.components.FormStructure;
import com.bccriskadvisory.link.rest.form.components.HiddenInput;
import com.bccriskadvisory.link.rest.form.components.TextInput;

public class ConnectionForm {
	
	public static FormStructure newForm() {
		return new FormStructure("connection-form")
			.withInput(new HiddenInput("ID"))
			.withInput(new TextInput("name", "Name"))
			.withInput(new TextInput("description", "Description"))
			.withInput(new TextInput("url", "URL"))
			.withInput(new TextInput("apiKey", "API Key"))
			.withInput(new TextInput("pollingInterval", "Polling Interval (minutes)"))
			.visible();
	}
}