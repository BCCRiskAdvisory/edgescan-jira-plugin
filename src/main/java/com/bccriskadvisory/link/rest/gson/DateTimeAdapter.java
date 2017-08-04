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
package com.bccriskadvisory.link.rest.gson;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.bccriskadvisory.link.utility.AbstractLogSupported;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateTimeAdapter extends AbstractLogSupported implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {

	public static final DateTimeFormatter JSON_PATTERN = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	@Override
	public DateTime deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		String date = element.getAsString();
		
		try {
			return JSON_PATTERN.parseDateTime(date);
		} catch (IllegalArgumentException e) {
			getLog().error("Unable to parse date due to:", e);
			return null;
		}
	}

	@Override
	public JsonElement serialize(DateTime src, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(JSON_PATTERN.print(src));
	}
}