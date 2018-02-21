package org.opentosca.container.api.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class JAXBContextProvider implements ContextResolver<JAXBContext> {

    private static Logger logger = LoggerFactory.getLogger(JAXBContextProvider.class);

    private final Map<Class<?>, JAXBContext> contextMap = Collections.synchronizedMap(new HashMap<>());


    @Override
    public JAXBContext getContext(final Class<?> type) {

        JAXBContext context = this.contextMap.get(type);

        if (context == null) {
            try {
                logger.debug("Creating JAXBContext for type \"{}\"", type.getName());
                context = JAXBContext.newInstance(type);
                this.contextMap.put(type, context);
            } catch (final JAXBException e) {
                logger.error("Error creating JAXBContext: {}", e.getMessage(), e);
            }
        }

        return context;
    }
}
