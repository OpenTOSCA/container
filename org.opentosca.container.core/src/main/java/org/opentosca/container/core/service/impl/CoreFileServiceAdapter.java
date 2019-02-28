package org.opentosca.container.core.service.impl;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// This is only an intermediary class helper that proxies to the CsarStorageService
// until code actually depends on the new service
@Deprecated
@Service
public class CoreFileServiceAdapter implements ICoreFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreFileServiceAdapter.class);
    private CsarStorageService actualService;

    @Override
    public CSARID storeCSAR(Path csarFile) throws UserException, SystemException {
        LOGGER.debug("Delegating storage request to CsarStorageService");
        return actualService.storeCSAR(csarFile).toOldCsarId();
    }

    @Override
    public CSARContent getCSAR(CSARID csarID) throws UserException {
        CsarId newId = new CsarId(csarID);
        Csar csar = actualService.findById(newId);
        LOGGER.debug("Retrieved Csar by id, wrapping it into CSARContent");
        return new CSARContent(csarID
                               , new FileSystemDirectory(csar.getSaveLocation())
                               , csar.metafileReplacement());
    }

    @Override
    public Path exportCSAR(CSARID csarID) throws UserException, SystemException {
        LOGGER.debug("Delegating csar export request to actual service");
        return actualService.exportCSAR(new CsarId(csarID));
    }

    @Override
    public Set<CSARID> getCSARIDs() {
        LOGGER.debug("Retrieving CSARIDs from actual service");
        return actualService.findAll().stream()
            .map(c -> c.id().toOldCsarId())
            .collect(Collectors.toSet());
    }

    @Override
    public void deleteCSAR(CSARID csarID) throws SystemException, UserException {
        LOGGER.debug("Delegating csar deletion request to actual service");
        actualService.deleteCSAR(new CsarId(csarID));
    }

    @Override
    public void deleteCSARs() throws SystemException {
        LOGGER.debug("Delegating csar purge to actual service");
        actualService.purgeCsars();
    }
    
    public void bindStorage(CsarStorageService boundService) {
        if (boundService == null) {
            LOGGER.warn("Tried to bind null storage service");
            return;
        }
        actualService = boundService;
        LOGGER.info("Bound storage service provider");
    }
    
    public void unbindStorage(CsarStorageService removedService) {
        actualService = null;
        LOGGER.info("Unbound storage service provider");
    }

}
