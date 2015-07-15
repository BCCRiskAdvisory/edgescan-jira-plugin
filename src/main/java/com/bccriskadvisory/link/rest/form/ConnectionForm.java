package com.bccriskadvisory.link.rest.form;

import com.bccriskadvisory.link.rest.form.components.Checkbox;
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
			.withInput(new Checkbox("isEnabled", "Enabled"))
			.visible();
	}
}