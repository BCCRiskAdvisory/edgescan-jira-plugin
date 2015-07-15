package com.bccriskadvisory.link.rest.form.components;

import com.bccriskadvisory.link.rest.gson.GsonObject;

@SuppressWarnings("unused")
public class Option extends GsonObject {
	
	private String value;
	private String label;
	
	public Option() { }
	
	public Option(String value, String label) {
		this.value = value;
		this.label = label;
	}
}