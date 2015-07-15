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
package com.bccriskadvisory.link.rest.edgescan;

public enum Risk {
	
	MINIMAL(1, "Minimal"),
	LOW(2, "Low"),
	MEDIUM(3, "Medium"),
	HIGH(4, "High"),
	CRITICAL(5, "Critical");
	
	private int value;
	private String displayString;

	private Risk(int value, String displayString) {
		this.value = value;
		this.displayString = displayString;
	}

	public int getValue() {
		return value;
	}
	
	public String getKeyString() {
		return displayString.toLowerCase();
	}

	public String getDisplayString() {
		return displayString;
	}
	
	public static Risk fromInt(int i){
		for (Risk risk : Risk.values()) {
			if (risk.value == i) {
				return risk;
			}
		}
		throw new IllegalArgumentException("No risk with integer value " + i);
	}
	
	public static Risk fromString(String intString){
		return fromInt(Integer.valueOf(intString));
	}
}