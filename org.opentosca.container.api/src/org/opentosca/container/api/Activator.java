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
package org.opentosca.container.api;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.opentosca.container.api.config.CorsFilter;
import org.opentosca.container.api.config.JAXBContextProvider;
import org.opentosca.container.api.config.ObjectMapperProvider;
import org.opentosca.container.api.config.PlainTextMessageBodyWriter;
import org.opentosca.container.api.controller.RootController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.jaxrs.publisher.ApplicationConfiguration;

public class Activator implements BundleActivator, ApplicationConfiguration {

  private static Logger logger = LoggerFactory.getLogger(Activator.class);

  private static BundleContext context;

  private final List<ServiceRegistration<?>> services = new ArrayList<>();


  static BundleContext getContext() {
    return context;
  }

  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    logger.info("Starting bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
        bundleContext.getBundle().getVersion());

    context = bundleContext;

    // Non-OSGi Endpoint Resources
    this.services
        .add(bundleContext.registerService(RootController.class, new RootController(), null));

    // Jersey Configuration
    this.configurator(bundleContext);
    this.configureSwagger(bundleContext);
    this.services.add(bundleContext.registerService(ApplicationConfiguration.class, this, null));
    this.services.add(bundleContext.registerService(CorsFilter.class, new CorsFilter(), null));
    this.services.add(bundleContext.registerService(PlainTextMessageBodyWriter.class,
        new PlainTextMessageBodyWriter(), null));
    this.services.add(bundleContext.registerService(ObjectMapperProvider.class,
        new ObjectMapperProvider(), null));
    this.services
        .add(bundleContext.registerService(JacksonFeature.class, new JacksonFeature(), null));
    this.services
        .add(bundleContext.registerService(MultiPartFeature.class, new MultiPartFeature(), null));

    // Custom JAXBContext provider to have proper error logging. Can be
    // removed once the API is in a stable state.
    this.services.add(
        bundleContext.registerService(JAXBContextProvider.class, new JAXBContextProvider(), null));
  }

  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
        bundleContext.getBundle().getVersion());
    this.services.forEach(service -> service.unregister());
    context = null;
  }

  private void configurator(final BundleContext bundleContext) throws Exception {
    final ServiceReference<?> configAdminRef =
        bundleContext.getServiceReference(ConfigurationAdmin.class.getName());

    if (configAdminRef == null) {
      logger.warn(
          "Reference to <ConfigurationAdmin> service could not be found, did you activate the bundle?");
      return;
    }

    final ConfigurationAdmin configAdmin =
        (ConfigurationAdmin) bundleContext.getService(configAdminRef);
    final Configuration config =
        configAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null);

    Dictionary<String, Object> properties = config.getProperties();
    if (properties == null) {
      properties = new Hashtable<>();
    }

    properties.put("root", "/");

    config.update(properties);
  }

  private void configureSwagger(final BundleContext context) throws Exception {
    ServiceReference<?> reference = context.getServiceReference(ConfigurationAdmin.class.getName());
    ConfigurationAdmin configAdmin = (ConfigurationAdmin) context.getService(reference);
    Configuration configuration =
        configAdmin.getConfiguration("com.eclipsesource.jaxrs.swagger.config", null);

    final Dictionary<String, Object> properties = new Hashtable<>();

    properties.put("swagger.basePath", "/");
    properties.put("swagger.host", "localhost:1337");
    properties.put("swagger.info.title", "A Swagger test API");
    properties.put("swagger.info.description", "This API only exist to test swagger support");
    properties.put("swagger.info.version", "1.0");
    properties.put("swagger.info.contact.name", "OpenTOSCA");
    properties.put("swagger.info.contact.url", "http://opentosca.org");
    properties.put("swagger.info.license.name", "Apache License, Version 2.0");
    properties.put("swagger.info.license.url", "https://www.apache.org/licenses/LICENSE-2.0");
    configuration.update(properties);
    context.ungetService(reference);
  }

  @Override
  public Map<String, Object> getProperties() {
    final Map<String, Object> properties = new HashMap<>();
    properties.put(ServerProperties.WADL_FEATURE_DISABLE, true);
    return properties;
  }
}
