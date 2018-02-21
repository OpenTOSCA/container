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
package org.opentosca.container.application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Activator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    private static BundleContext context;


    static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        logger.info("Starting bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
            bundleContext.getBundle().getVersion());
        Activator.context = bundleContext;

        // Install logging bridge handler for SLF4J
        SLF4JBridgeHandler.install();
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
            bundleContext.getBundle().getVersion());
        Activator.context = null;
    }
}
