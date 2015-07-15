/**
 * Copyright (C) 2015 BCC Risk Advisory (info@bccriskadvisory.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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