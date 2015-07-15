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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.bccriskadvisory.link.utility.AbstractLogSupported;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This adapter makes optional types 'transparent' - the serialized form looks like the contained object.
 * <b>NOTE:</b>one shortcoming of this implementation is that fields that are null or absent in the json representation will not be 
 * deserialized to empty optionals, since the deserialize method will not be called for such fields. Therefore <b>any</b> optional 
 * fields in a gson object must be initialized to Optional.empty in the field declaration.
 */
public class OptionalAdapter<T> extends AbstractLogSupported implements JsonDeserializer<Optional<T>>, JsonSerializer<Optional<T>>{

	@Override
	public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src.orElse(null));
	}

	@Override
	public Optional<T> deserialize(JsonElement json, Type optionalType, JsonDeserializationContext context) throws JsonParseException {
		final T value = context.deserialize(json, ((ParameterizedType) optionalType).getActualTypeArguments()[0]);
		return Optional.ofNullable(value);
	}
}