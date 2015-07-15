package com.bccriskadvisory.link.rest.form.components;

public class Checkbox extends Input {
	public Checkbox() { super("checkbox"); }
	
	public Checkbox(String name, String label) {
		this();
		this.name = name;
		this.label = label;
	}
}