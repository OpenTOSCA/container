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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LogFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class.getName());

    @Override
    public void filter(final ContainerRequestContext request) throws IOException {
        logger.debug("LogFilter.filter()");

        for (final String key : request.getHeaders().keySet()) {
            logger.debug(key + " : " + request.getHeaders().get(key));
        }
        if (request.getMethod().equalsIgnoreCase("POST")) {
            logger.debug("POST method");
        }

        if (request.getMediaType() != null) {
            logger.debug("MediaType: " + request.getMediaType());
            if (request.getMediaType().toString().contains("xml")) {
                if (request.hasEntity()) {
                    logger.debug(IOUtils.toString(request.getEntityStream()));
                }
            }
        }
    }
}
