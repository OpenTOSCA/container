/*******************************************************************************
 * Copyright 2017 University of Stuttgart
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.opentosca.container.api.config;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import dk.nykredit.jackson.dataformat.hal.HALMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperProvider.class);

  private ObjectMapper objectMapper;

  @Override
  public ObjectMapper getContext(final Class<?> type) {
    if (this.objectMapper == null) {
      this.objectMapper = createDefaultMapper();
    }
    LOG.trace("Retrieving Jackson Object Mapper");
    return this.objectMapper;
  }

  private static ObjectMapper createDefaultMapper() {
    final ObjectMapper om = new HALMapper();
    om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    LOG.info("Created Jackson ObjectMapper");
    return om;
  }
}
