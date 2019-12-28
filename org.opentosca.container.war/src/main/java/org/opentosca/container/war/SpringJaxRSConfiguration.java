package org.opentosca.container.war;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.opentosca.container.core.common.Settings;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;

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
    register(ApiListingResource.class);
    register(SwaggerSerializers.class);
  }

  private void logReadyMessage() {
    final String readyMessage = "===================================================\n" +
      "OPENTOSCA CONTAINER IS READY TO USE!\n" +
      "===================================================";
    LoggerFactory.getLogger("org.opentosca.container.api.ContainerApplication").info(readyMessage);
  }
}
