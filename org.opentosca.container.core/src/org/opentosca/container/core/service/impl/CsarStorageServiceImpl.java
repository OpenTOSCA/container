package org.opentosca.container.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.next.utils.Consts;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarStorageServiceImpl implements CsarStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarStorageServiceImpl.class);
    private static ICoreFileService CORE_FILE_SERVICE;
    // private final CSARMetaDataJPAStore jpaStorage = new CSARMetaDataJPAStore();

    // FIXME obtain from settings or otherwise

    private static final Path CSAR_BASE_PATH = Paths.get(Settings.getSetting("org.opentosca.csar.basepath"));

    @Override
    public Set<Csar> findAll() {
        LOGGER.debug("Requesting all CSARs");
        final Set<Csar> csars = new HashSet<>();
        try {
            for (Path csarId : Files.newDirectoryStream(CSAR_BASE_PATH, Files::isDirectory)) {
                csars.add(new CsarImpl(new CsarId(csarId)));
            }
        }
        catch (IOException e) {
            LOGGER.error("Error when traversing '{}' for CSARs", CSAR_BASE_PATH);
            throw new ServerErrorException(Response.serverError().build());
        }
        return csars;
    }

    @Override
    public Csar findById(CsarId id) throws NoSuchElementException {
        if (Files.exists(id.getSaveLocation())) {
            return new CsarImpl(id); // FIXME pass path here
        }
        LOGGER.info("CSAR '{}' could not be found", id.toString());
        throw new NoSuchElementException(String.format("CSAR '%s' could not be found", id.toString()));
    }

    @Override
    public Path storeCSARTemporarily(String filename, InputStream is) {
        try {
            Path tempLocation = Paths.get(Consts.TMPDIR, filename);
            Files.copy(is, tempLocation);
            return tempLocation;
        }
        catch (IOException e) {
            LOGGER.error("Exception occured when writing temporary CSAR file: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public CsarId storeCSAR(Path csarLocation) throws UserException, SystemException {
         // delegate to CoreService for now:
        return new CsarId(CORE_FILE_SERVICE.storeCSAR(csarLocation));
    }

    @Override
    public void deleteCSAR(CsarId csarId) throws SystemException, UserException {
        CORE_FILE_SERVICE.deleteCSAR(csarId.toOldCsarId());
    }

}
