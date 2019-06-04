package org.opentosca.container.war;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.opentosca.container.api.config.*;
import org.opentosca.container.api.controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public Application() {
    LOGGER.info("Application has been instantiated");
  }

  @Override
  public Set<Class<?>> getClasses() {
    return Stream.of(
      BoundaryDefinitionController.class
      , BuildPlanController.class
      , CsarController.class
      , ManagementPlanController.class
      , NodeTemplateController.class
      , NodeTemplateInstanceController.class
      , RelationshipTemplateController.class
      , RelationshipTemplateInstanceController.class
      , RootController.class
      , ServiceTemplateController.class
      , ServiceTemplateInstanceController.class
      , SituationsController.class
    ).collect(Collectors.toSet());
  }

  @Override
  public Set<Object> getSingletons() {
    CorsFilter corsFilter = new CorsFilter();
    corsFilter.getAllowedOrigins().add("*");

    return Stream.of(
      corsFilter
      , new PlainTextMessageBodyWriter()
      , new URI2XMLMessageBodyWriter()
      , new ObjectMapperProvider()
      , new LogFilter()
      , new JAXBContextProvider()
      , new LoggingExceptionMapper()
    ).collect(Collectors.toSet());
  }
}
