package org.opentosca.container.war;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class SpringJaxRSConfiguration extends ResourceConfig {

  public SpringJaxRSConfiguration() {
//    configureSwagger();
    registerControllers();
    registerFilters();
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
    BeanConfig swaggerConfig = new BeanConfig();
    swaggerConfig.setResourcePackage("org.opentosca.container.api.controller");
    swaggerConfig.setPrettyPrint(true);
    swaggerConfig.setScan(true);
    register(ApiListingResource.class);
  }

  private void logReadyMessage() {
    final String readyMessage = "===================================================\n" +
      "OPENTOSCA CONTAINER IS READY TO USE!\n" +
      "===================================================";
    LoggerFactory.getLogger("org.opentosca.container.api.ContainerApplication").info(readyMessage);
  }
}
