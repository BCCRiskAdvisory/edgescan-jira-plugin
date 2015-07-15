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