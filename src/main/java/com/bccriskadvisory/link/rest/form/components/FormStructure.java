package com.bccriskadvisory.link.rest.form.components;

import java.util.ArrayList;
import java.util.List;

import com.bccriskadvisory.link.rest.gson.GsonObject;

@SuppressWarnings("unused")
public class FormStructure extends GsonObject {

	private String name;
	private String title;
	private String submitText;
	private String cancelText;
	private List<Input> inputs;
	private List<FormStructure> sections;
	private boolean visible;
	
	public FormStructure() { } 
	
	public FormStructure(String formName) {
		this(formName, null);
	}
	
	public FormStructure(String formName, String title) {
		this.name = formName;
		this.title = title;
		this.inputs = new ArrayList<>();
		this.sections = new ArrayList<>();
	}
	
	public FormStructure withSubmit(String submitText){
		this.submitText = submitText;
		return this;
	}
	
	public FormStructure withCancel(String cancelText){
		this.cancelText = cancelText;
		return this;
	}
	
	public FormStructure withInput(Input input){
		inputs.add(input);
		return this;
	}
	
	public FormStructure withSection(FormStructure section) {
		sections.add(section);
		return this;
	}
	
	public FormStructure visible() {
		this.visible = true;
		return this;
	}
}