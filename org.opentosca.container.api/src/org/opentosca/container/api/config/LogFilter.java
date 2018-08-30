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

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.opentosca.container.api.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LogFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class.getName());

    @Override
    public void filter(final ContainerRequestContext request) throws IOException {
        logger.debug("LogFilter.filter()");
        logger.debug("Method: {}", request.getMethod());
        logger.debug("URL: {}", UriUtil.encode(request.getUriInfo().getAbsolutePath()));
        for (final String key : request.getHeaders().keySet()) {
            logger.debug(key + " : " + request.getHeaders().get(key));
        }
        // final List<MediaType> mediaTypes =
        // Lists.newArrayList(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE,
        // MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_XML_TYPE, MediaType.TEXT_HTML_TYPE);
        // if (request.getMediaType() != null && mediaTypes.contains(request.getMediaType())) {
        // if (request.hasEntity()) {
        // final String body = IOUtils.toString(request.getEntityStream());
        // request.setEntityStream(IOUtils.toInputStream(body));
        // logger.debug("Body: {}", body);
        // }
        // }
    }
}
