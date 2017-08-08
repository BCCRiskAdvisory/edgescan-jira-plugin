package com.bccriskadvisory.link.rest.form.components;

@SuppressWarnings("unused")
public class UserSelect extends Input {

	private String path;
	
	public UserSelect() { super("user-select"); }
	
	public UserSelect(String label, String name) {
		this();
		this.label = label;
		this.name = name;
	}
	
	public UserSelect withPath(String path) {
		this.path = path;
		return this;
	}
}

