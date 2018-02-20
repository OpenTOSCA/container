package org.opentosca.container.api.util;

import org.opentosca.container.api.config.ObjectMapperProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class JsonUtil {
	
	private static ObjectMapper objectMapper = ObjectMapperProvider.createDefaultMapper();

	static {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	
	public static <T> String writeValueAsString(final T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
