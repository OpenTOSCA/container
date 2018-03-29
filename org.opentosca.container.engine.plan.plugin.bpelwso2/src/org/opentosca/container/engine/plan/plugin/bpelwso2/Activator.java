/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.engine.plan.plugin.bpelwso2;

import org.opentosca.container.core.common.Settings;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        context = bundleContext;

        final String hostname = context.getProperty("org.opentosca.container.engine.plan.hostname");
        final String port = context.getProperty("org.opentosca.container.engine.plan.port");

        if (hostname != null && port != null) {
            final String url = "https://" + hostname + ":" + port;
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpelwso2.url", url);
        }

        final String servicesPort = context.getProperty("org.opentosca.container.engine.plan.services.port");

        if (hostname != null && servicesPort != null) {
            final String servicesUrl = "http://" + hostname + ":" + servicesPort + "/services";
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpelwso2.services.url", servicesUrl);
        }

        final String userName = context.getProperty("org.opentosca.container.engine.plan.plugin.bpelwso2.username");

        if (userName != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpelwso2.username", userName);
        }

        final String password = context.getProperty("org.opentosca.container.engine.plan.plugin.bpelwso2.password");

        if (password != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpelwso2.password", password);
        }

    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
                    bundleContext.getBundle().getVersion());
        Activator.context = null;
    }
}
