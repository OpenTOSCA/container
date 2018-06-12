/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.engine.plan.plugin.bpel;

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

        final String processEngine = context.getProperty("org.opentosca.container.engine.plan.plugin.bpel.engine");
        if (processEngine != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpel.engine", processEngine);
        }

        final String url = context.getProperty("org.opentosca.container.engine.plan.plugin.bpel.url");

        if (url != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpel.url", url);
        }

        final String servicesUrl = context.getProperty("org.opentosca.container.engine.plan.plugin.bpel.services.url");

        if (servicesUrl != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpel.services.url", servicesUrl);
        }

        final String userName = context.getProperty("org.opentosca.container.engine.plan.plugin.bpel.username");

        if (userName != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpel.username", userName);
        }

        final String password = context.getProperty("org.opentosca.container.engine.plan.plugin.bpel.password");

        if (password != null) {
            Settings.setSetting("org.opentosca.container.engine.plan.plugin.bpel.password", password);
        }

    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
                    bundleContext.getBundle().getVersion());
        Activator.context = null;
    }

}
