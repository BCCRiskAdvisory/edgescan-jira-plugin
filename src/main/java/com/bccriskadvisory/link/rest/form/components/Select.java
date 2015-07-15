package com.bccriskadvisory.link.rest.form.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unused")
public class Select extends Input {

	private boolean multiple;
	private int size;
	private Option defaultOption;
	private List<Option> options;
	
	public Select() { super("select"); }
	
	public Select(String label, String name) {
		this();
		this.label = label;
		this.name = name;
		options = new ArrayList<>();
	}
	
	public Select withOption(String value, String label) {
		options.add(new Option(value, label));
		return this;
	}
	
	public Select withDefault(String value, String label) {
		defaultOption = new Option(value, label);
		return this;
	}
	
	public Select withOptions(Map<String, String> optionsMap) {
		for (Entry<String, String> entry : optionsMap.entrySet()) {
			withOption(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public Select multiple() {
		multiple = true;
		return this;
	}
	
	public Select size(int size) {
		this.size = size;
		return this;
	}
}