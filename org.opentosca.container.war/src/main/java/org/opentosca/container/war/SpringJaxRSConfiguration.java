package org.opentosca.container.war;

import javax.ws.rs.ApplicationPath;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;

@ApplicationPath("/")
public class SpringJaxRSConfiguration extends ResourceConfig {

    public SpringJaxRSConfiguration() {
        registerControllers();
        registerFilters();
        configureSwagger();
        logReadyMessage();
    }

    private void registerControllers() {
        packages("org.opentosca.container.api.controller");
    }

    private void registerFilters() {
        packages("org.opentosca.container.api.config");
        register(MultiPartFeature.class);
    }

    private void configureSwagger() {
        register(OpenApiResource.class);
    }

    private void logReadyMessage() {
        final String readyMessage = "\n" +
            "======================================================================================================\n" +
            "                              OpenTOSCA CONTAINER is ready to use!\n" +
            "======================================================================================================";
        LoggerFactory.getLogger("org.opentosca.container.api.ContainerApplication").info(readyMessage);
    }
}
