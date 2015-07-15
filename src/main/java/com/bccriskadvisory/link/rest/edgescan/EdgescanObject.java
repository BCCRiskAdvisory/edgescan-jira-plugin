package com.bccriskadvisory.link.rest.edgescan;

import java.time.ZonedDateTime;

import com.bccriskadvisory.link.rest.gson.GsonObject;

public abstract class EdgescanObject extends GsonObject {
	
	private int id;
	private ZonedDateTime created_at;
	private ZonedDateTime updated_at;

	public int getId() {
		return id;
	}

	public ZonedDateTime getCreatedAt() {
		return created_at;
	}

	public ZonedDateTime getUpdatedAt() {
		return updated_at;
	}
}