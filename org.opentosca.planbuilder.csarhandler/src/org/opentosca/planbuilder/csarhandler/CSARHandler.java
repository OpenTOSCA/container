package org.opentosca.planbuilder.csarhandler;

import java.io.File;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
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
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class CSARHandler {

    final private static Logger LOG = LoggerFactory.getLogger(CSARHandler.class);


    /**
     * Stores a CSAR given as file object
     *
     * @param file File referencing a CSAR
     * @return an Object representing an ID of the stored CSAR, if something went wrong null is returned
     *         instead
     * @throws SystemException
     * @throws UserException
     */
    public Object storeCSAR(final File file) throws UserException, SystemException {
        CSARHandler.LOG.debug("Trying to store csar");
        final ICoreFileService fileService = this.fetchCoreFileService();

        final CSARID csarId = fileService.storeCSAR(file.toPath());
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
        CSARHandler.LOG.info("Deleting all CSAR files");
        final ICoreFileService fileService = this.fetchCoreFileService();

        try {
            fileService.deleteCSARs();
        } catch (final SystemException e) {

        }
    }

    /**
     * Returns a CSARContent Object for the given CSARID
     *
     * @param id a CSARID
     * @return the CSARContent for the given CSARID
     * @throws UserException is thrown when something inside the OpenTOSCA Core fails
     */
    public CSARContent getCSARContentForID(final CSARID id) throws UserException {
        LOG.debug("Fetching CSARContent for given ID");
        return this.fetchCoreFileService().getCSAR(id);
    }

    private ICoreFileService fetchCoreFileService() {
        CSARHandler.LOG.debug("Retrieving bundle context");
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

        if (bundleContext == null) {
            CSARHandler.LOG.debug("BundleContext from FrameworkUtil is null. Fallback to Activator.");
            bundleContext = Activator.bundleContext;
        }

        if (bundleContext != null) {
            CSARHandler.LOG.debug("Retrieving ServiceReference for ICoreFileService");
            final ServiceReference<?> fileServiceRef = bundleContext.getServiceReference(
                ICoreFileService.class.getName());
            CSARHandler.LOG.debug("Retrieving Service for ICoreFileService");
            final ICoreFileService fileService = (ICoreFileService) bundleContext.getService(fileServiceRef);
            return fileService;
        } else {
            LOG.debug("BundleContext still null. Fallback to ServiceRegistry");
            return ServiceRegistry.getCoreFileService();
        }
    }

}
