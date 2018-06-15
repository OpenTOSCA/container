package org.opentosca.container.core.impl.service;

import java.nio.file.Path;
import java.util.Set;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.internal.ICoreInternalFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation currently acts as a Proxy to the Core Internal File Service. It can in future
 * be used to modify the incoming parameters to fit another back end interface / implementation.
 */
public class CoreFileServiceImpl implements ICoreFileService {

    private final static Logger LOG = LoggerFactory.getLogger(CoreFileServiceImpl.class);

    private ICoreInternalFileService internalFileService;

    /**
     * {@inheritDoc}
     *
     * <br />
     * <br />
     * This currently acts as a proxy.
     */
    @Override
    public CSARID storeCSAR(final Path csarFile) throws UserException, SystemException {
        return this.internalFileService.storeCSAR(csarFile);
    }

    /**
     * {@inheritDoc}
     *
     * <br />
     * <br />
     * This currently acts as a proxy.
     */
    @Override
    public CSARContent getCSAR(final CSARID csarID) throws UserException {
        return this.internalFileService.getCSAR(csarID);
    }

    /**
     * {@inheritDoc}
     *
     * <br />
     * <br />
     * This currently acts as a proxy.
     */
    @Override
    public Path exportCSAR(final CSARID csarID) throws UserException, SystemException {
        return this.internalFileService.exportCSAR(csarID);
    }

    /**
     * {@inheritDoc}
     *
     * <br />
     * <br />
     * This currently acts as a proxy.
     */
    @Override
    public Set<CSARID> getCSARIDs() {
        return this.internalFileService.getCSARIDs();
    }

    /**
     * {@inheritDoc}
     *
     * <br />
     * <br />
     * This currently acts as a proxy.
     */
    @Override
    public void deleteCSAR(final CSARID csarID) throws SystemException, UserException {
        this.internalFileService.deleteCSAR(csarID);
    }

    /**
     * {@inheritDoc}
     *
     * <br />
     * <br />
     * This currently acts as a proxy.
     */
    @Override
    public void deleteCSARs() throws SystemException {
        this.internalFileService.deleteCSARs();
    }

    /**
     * Binds the Core Internal File Service.
     *
     * @param fileService to bind
     */
    public void bindCoreInternalFileService(final ICoreInternalFileService internalFileService) {
        if (internalFileService == null) {
            CoreFileServiceImpl.LOG.error("Can't bind Core Internal File Service.");
        } else {
            this.internalFileService = internalFileService;
            CoreFileServiceImpl.LOG.debug("Core Internal File Service bound.");
        }
    }

    /**
     * Unbinds the Core Internal File Service.
     *
     * @param fileService to unbind
     */
    public void unbindCoreInternalFileService(final ICoreInternalFileService internalFileService) {
        this.internalFileService = null;
        CoreFileServiceImpl.LOG.debug("Core Internal File Service unbound.");
    }

}
