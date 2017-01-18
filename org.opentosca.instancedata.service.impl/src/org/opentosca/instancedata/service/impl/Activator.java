package org.opentosca.instancedata.service.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the (demo) InstanceDatService.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Here the web service is started.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Activator implements BundleActivator {
	
	
	private final static Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	static BundleContext context;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		
		LOG.trace("Starting Activator start");
		
		Activator.context = bundleContext;
		
		LOG.trace("Bundle Context set.");
		
		//		Endpoint.publish("http://localhost:8082/InstanceDataService", new InstanceDataServiceImpl());
		
		LOG.trace("Endpoint published.");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
}
