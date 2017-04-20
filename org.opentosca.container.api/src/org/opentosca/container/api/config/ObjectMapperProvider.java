/*******************************************************************************
 * Copyright 2017 University of Stuttgart
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.opentosca.container.api.config;

import java.io.IOException;

import javax.ws.rs.core.Link;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
	
	private ObjectMapper objectMapper;


	@Override
	public ObjectMapper getContext(final Class<?> type) {
		if (this.objectMapper == null) {
			this.objectMapper = createDefaultMapper();
			// Custom module to serialize Link objects
			final SimpleModule m = new SimpleModule();
			m.addSerializer(Link.class, new LinkSerializer());
			this.objectMapper.registerModule(m);
		}
		return this.objectMapper;
	}
	
	private static ObjectMapper createDefaultMapper() {
		
		return new ObjectMapper();
	}
	
	
	public static class LinkSerializer extends JsonSerializer<Link> {
		
		@Override
		public void serialize(final Link link, final JsonGenerator json, final SerializerProvider provider) throws IOException, JsonProcessingException {
			if ((link.getUri() == null) || (link.getRel() == null) || link.getRel().isEmpty()) {
				return;
			}
			json.writeStartObject();
			json.writeObjectFieldStart(link.getRel());
			json.writeStringField("href", link.getUri().toString());
			if ((link.getTitle() != null) && !link.getTitle().isEmpty()) {
				json.writeStringField(Link.TITLE, link.getTitle());
			}
			if ((link.getType() != null) && !link.getType().isEmpty()) {
				json.writeStringField(Link.TYPE, link.getType());
			}
			json.writeEndObject();
			json.writeEndObject();
		}
	}
}
