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