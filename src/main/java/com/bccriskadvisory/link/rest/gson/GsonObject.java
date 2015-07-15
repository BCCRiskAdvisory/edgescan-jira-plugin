package com.bccriskadvisory.link.rest.gson;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.google.gson.GsonBuilder;

public class GsonObject {
	
	private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
		.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
		.registerTypeAdapter(Optional.class, new OptionalAdapter<>());

	public String toString() {
		return GSON_BUILDER.create().toJson(this);
	}
	
	public static <T extends GsonObject> T fromJson(String json, Class<T> clazz) {
		return GSON_BUILDER.create().fromJson(json, clazz);
	}
}