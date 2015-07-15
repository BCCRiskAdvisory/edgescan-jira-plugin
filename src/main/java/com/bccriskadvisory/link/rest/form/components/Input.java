package com.bccriskadvisory.link.rest.form.components;

import com.bccriskadvisory.link.rest.gson.GsonObject;

@SuppressWarnings("unused")
public class Input extends GsonObject {
	
	private String type;
	protected String name;
	protected String label;
	private boolean disabled;
	
	public Input(String type) {
		this.type = type;
	}
	
	public Input disable(){
		disabled = true;
		return this;
	}
}