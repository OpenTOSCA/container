/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.core;

import java.io.File;

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


        logger.info("org.opentosca.container.hostname={}", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
        logger.info("org.opentosca.container.port={}", Settings.OPENTOSCA_CONTAINER_PORT);

        // /////////////////// PATHS ///////////////////

        // contains data of OpenTOSCA that should be stored permanently
        String openTOSCAPath = "";
        if (System.getProperty("openTOSCAPath") == null) {
            openTOSCAPath = System.getProperty("java.io.tmpdir") + File.separator + "opentosca";
        } else {
            openTOSCAPath = System.getProperty("openTOSCAPath") + File.separator + "opentosca";
        }

        // contains data of OpenTOSCA that should be stored temporarily
        Settings.setSetting("temp", openTOSCAPath + File.separator + "Temp");

        // Derby database location
        Settings.setSetting("databaseLocation", openTOSCAPath + File.separator + "DB");

        // relative path where CSARs will be stored locally; used by the
        // Filesystem storage provider
        Settings.setSetting("csarStorePath", openTOSCAPath + File.separator + "CSARs");

        // /////////////////// URLS ///////////////////

        // URI of the ContainerAPI
        Settings.setSetting("containerUri", Settings.CONTAINER_API);

        // URI of the DataInstanceAPI
        Settings.setSetting("datainstanceUri", "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":"
            + Settings.OPENTOSCA_CONTAINER_PORT + "/datainstance");

        // /////////////////// CSAR ///////////////////

        // extension of a CSAR file
        Settings.setSetting("csarExtension", "csar");

        // relative path of IMPORTS directory in a CSAR file
        Settings.setSetting("csarImportsRelPath", "IMPORTS");

        // relative path of Definitions directory in a CSAR file
        Settings.setSetting("csarDefinitionsRelPath", "Definitions");

        // relative path where the TOSCA meta file is located in a CSAR file
        Settings.setSetting("toscaMetaFileRelPath", "TOSCA-Metadata" + File.separator + "TOSCA.meta");

        // possible file extensions of a TOSCA file, separated by character ";"
        Settings.setSetting("toscaFileExtensions", "xml;tosca;ste");

        // /////////////////// OTHERS ///////////////////

        // Container Capabilities
        Settings.setSetting("containerCapabilities", Settings.containerCapabilities);
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
                    bundleContext.getBundle().getVersion());
        Activator.context = null;
    }
}
