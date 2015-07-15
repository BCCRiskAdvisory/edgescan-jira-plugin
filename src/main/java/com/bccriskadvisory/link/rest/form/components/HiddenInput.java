package com.bccriskadvisory.link.rest.form.components;

public class HiddenInput extends Input {
	public HiddenInput() { super("hidden"); }
	
	public HiddenInput(String name) {
		this();
		this.name = name;
	}
}