package org.opentosca.bus.management.api.soaphttp;

import javax.xml.namespace.QName;

import org.apache.camel.component.bean.BeanComponent;
import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.directvm.DirectVmComponent;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.api.soaphttp.route.Route;
import org.opentosca.bus.management.extensions.SimpleFunctionConverter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CollaborationContext of the SOAP/HTTP-Management Bus-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The activator is needed to add and start the camel routes.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class Activator implements BundleActivator {

  public static String apiID;

  public static BundleContext bundleContext;

  final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

  @Override
  public void start(final BundleContext bundleContext) throws Exception {

    Activator.apiID = bundleContext.getBundle().getSymbolicName();

    // Set relayHeaders to false to drop all SOAP headers
    final CxfHeaderFilterStrategy headerStrategy = new CxfHeaderFilterStrategy();
    headerStrategy.setRelayHeaders(false);

    bundleContext.registerService(CxfHeaderFilterStrategy.class, headerStrategy, null);

    final OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);

    final DefaultCamelContext camelContext = new OsgiDefaultCamelContext(bundleContext, reg);

    // This explicitly binds the required components, fixing the OSGI startup
    camelContext.addComponent("direct", new DirectComponent());
    camelContext.addComponent("direct-vm", new DirectVmComponent());
    camelContext.addComponent("cxf", new CxfComponent());
    camelContext.addComponent("bean", new BeanComponent());

    camelContext.getTypeConverterRegistry().addTypeConverter(QName.class, String.class,
      new SimpleFunctionConverter<QName, String>(QName::valueOf, String.class, QName.class, false));

    camelContext.addRoutes(new Route());
    camelContext.start();

    Activator.bundleContext = bundleContext;
    Activator.LOG.info("SI-SOAP/HTTP-Management Bus-API started!");
  }

  @Override
  public void stop(final BundleContext arg0) throws Exception {
    Activator.LOG.info("SI-SOAP/HTTP-Management Bus-API stopped!");
  }

}
