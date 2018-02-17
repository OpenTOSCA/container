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
package org.opentosca.deployment.verification;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.deployment.verification.camel.RouteConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

  public static final String ID = "org.opentosca.deployment.verification";

  private static DefaultCamelContext camelContext;

  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    camelContext = new OsgiDefaultCamelContext(bundleContext);
    camelContext.addRoutes(new RouteConfiguration());
    camelContext.start();
  }

  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    final ServiceReference<VerificationExecutor> ref =
        bundleContext.getServiceReference(VerificationExecutor.class);
    if (ref != null) {
      bundleContext.getService(ref).shutdown();
    }
    if (camelContext != null) {
      camelContext.stop();
    }
  }

  public static DefaultCamelContext getCamelContext() {
    if (camelContext == null) {
      throw new IllegalStateException();
    }
    return camelContext;
  }
}
