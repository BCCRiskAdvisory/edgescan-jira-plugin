package com.bccriskadvisory.link.rest;

import java.util.List;

import com.bccriskadvisory.link.rest.gson.GsonObject;
import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class PluginError extends GsonObject {
	
	private String type;
	private String message;

	public PluginError() { }
	
	public PluginError(String type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public PluginError(Exception e) {
		this.type = e.getClass().getSimpleName() + " thrown";
		this.message = e.getMessage();
	}
}