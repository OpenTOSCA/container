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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.TEXT_PLAIN)
public class PlainTextMessageBodyWriter implements MessageBodyWriter<Serializable> {
	
	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		return type instanceof Serializable;
	}
	
	@Override
	public long getSize(final Serializable t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		return 0;
	}
	
	@Override
	public void writeTo(final Serializable t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
		new DataOutputStream(entityStream).writeUTF(t.toString());
	}
}
