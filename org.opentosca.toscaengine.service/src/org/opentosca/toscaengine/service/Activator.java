package org.opentosca.toscaengine.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the OSGiEvent-Management Bus-API.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The activator is needed to add and start the camel routes.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Activator implements BundleActivator {

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);
    public static BundleContext context;


    @Override
    public void start(BundleContext bundleContext) throws Exception {
	Activator.context = bundleContext;
	Activator.LOG.debug("TOSCAEngine started");

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
	Activator.context = null;
	Activator.LOG.debug("TOSCAEngine stopped");

    }
}
