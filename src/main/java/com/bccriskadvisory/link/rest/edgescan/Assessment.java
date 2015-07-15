package com.bccriskadvisory.link.rest.edgescan;

import java.time.ZonedDateTime;


public class Assessment extends EdgescanObject {

	private int asset_id;
	private ZonedDateTime date;
	private String status;
	
	public int getAsset_id() {
		return asset_id;
	}
	
	public ZonedDateTime getDate() {
		return date;
	}
	
	public String getStatus() {
		return status;
	}
}