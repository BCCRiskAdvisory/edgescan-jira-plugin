package com.bccriskadvisory.link.rest.gson;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import com.bccriskadvisory.link.utility.AbstractLogSupported;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ZonedDateTimeAdapter extends AbstractLogSupported implements JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {

	public static final DateTimeFormatter JSON_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	@Override
	public ZonedDateTime deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		String date = element.getAsString();
		
		try {
			TemporalAccessor parse = JSON_PATTERN.parse(date);
			return ZonedDateTime.from(parse);
		} catch (DateTimeParseException e) {
			getLog().error("Unable to parse date due to:", e);
			return null;
		}
	}

	@Override
	public JsonElement serialize(ZonedDateTime src, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(JSON_PATTERN.format(src));
	}
}