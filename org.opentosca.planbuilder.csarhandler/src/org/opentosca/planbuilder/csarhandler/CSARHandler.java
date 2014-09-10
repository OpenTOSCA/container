package org.opentosca.planbuilder.csarhandler;

import java.io.File;

import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a small layer over the ICoreFileService of the OpenTOSCA Core
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class CSARHandler {
	
	final private static Logger LOG = LoggerFactory.getLogger(CSARHandler.class);
	
	
	/**
	 * Stores a CSAR given as file object
	 * 
	 * @param file File referencing a CSAR
	 * @return an Object representing an ID of the stored CSAR, if something
	 *         went wrong null is returned instead
	 * @throws SystemException
	 * @throws UserException
	 */
	public Object storeCSAR(File file) throws UserException, SystemException {
		CSARHandler.LOG.debug("Retrieving bundle context");
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		CSARHandler.LOG.debug("Retrieving ServiceReference for ICoreFileService");
		ServiceReference<?> fileServiceRef = bundleContext.getServiceReference(ICoreFileService.class.getName());
		CSARHandler.LOG.debug("Retrieving Service for ICoreFileService");
		ICoreFileService fileService = (ICoreFileService) bundleContext.getService(fileServiceRef);
		
		CSARHandler.LOG.debug("Trying to store csar");
		CSARID csarId = fileService.storeCSAR(file.toPath());
		if (csarId == null) {
			CSARHandler.LOG.warn("Storing CSAR file failed");
			return null;
		}
		CSARHandler.LOG.info("Storing CSAR file was successful");
		return csarId;
	}
	
	/**
	 * Deletes all CSARs in the OpenTOSCA Core
	 */
	public void deleteAllCsars() {
		CSARHandler.LOG.debug("Retrieving bundle context");
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		CSARHandler.LOG.debug("Retrieving ServiceReference for ICoreFileService");
		ServiceReference<?> fileServiceRef = bundleContext.getServiceReference(ICoreFileService.class.getName());
		CSARHandler.LOG.debug("Retrieving Service for ICoreFileService");
		ICoreFileService fileService = (ICoreFileService) bundleContext.getService(fileServiceRef);
		
		CSARHandler.LOG.info("Deleted all CSAR files");
		try {
			fileService.deleteCSARs();
		} catch (SystemException e) {
			
		}
	}
	
	/**
	 * Returns a CSARContent Object for the given CSARID
	 * 
	 * @param id a CSARID
	 * @return the CSARContent for the given CSARID
	 * @throws UserException is thrown when something inside the OpenTOSCA Core
	 *             fails
	 */
	public CSARContent getCSARContentForID(CSARID id) throws UserException {
		CSARHandler.LOG.debug("Retrieving bundle context");
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		CSARHandler.LOG.debug("Retrieving ServiceReference for ICoreFileService");
		ServiceReference<?> fileServiceRef = bundleContext.getServiceReference(ICoreFileService.class.getName());
		CSARHandler.LOG.debug("Retrieving Service for ICoreFileService");
		ICoreFileService fileService = (ICoreFileService) bundleContext.getService(fileServiceRef);
		
		return fileService.getCSAR(id);
		
	}
	
}
