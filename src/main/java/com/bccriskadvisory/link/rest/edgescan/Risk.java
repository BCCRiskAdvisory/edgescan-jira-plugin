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