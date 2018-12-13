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
package org.opentosca.deployment.checks;

import org.apache.camel.component.bean.BeanComponent;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.directvm.DirectVmComponent;
import org.apache.camel.component.stream.StreamComponent;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.deployment.checks.camel.RouteConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    public static final String ID = "org.opentosca.deployment.checks";

    private static DefaultCamelContext camelContext;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        camelContext = new OsgiDefaultCamelContext(bundleContext);

        // This explicitly binds the required components, fixing the OSGI startup
        camelContext.addComponent("direct", new DirectComponent());
        camelContext.addComponent("direct-vm", new DirectVmComponent());
        camelContext.addComponent("stream", new StreamComponent());
        camelContext.addComponent("bean", new BeanComponent());
        
        camelContext.addRoutes(new RouteConfiguration());
        camelContext.start();
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        final ServiceReference<TestExecutor> ref = bundleContext.getServiceReference(TestExecutor.class);
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
