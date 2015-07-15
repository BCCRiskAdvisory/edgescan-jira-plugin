package com.bccriskadvisory.link.rest.form.components;

public class TextInput extends Input {
	
	public TextInput() { super("text"); }
	
	public TextInput(String name, String label) {
		this();
		this.name = name;
		this.label = label;
	}
}