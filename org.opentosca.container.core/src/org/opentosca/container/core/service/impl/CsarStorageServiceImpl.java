package org.opentosca.container.core.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.ServerError;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
//import org.opentosca.container.core.impl.service.internal.file.csar.CSARMetaDataJPAStore;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
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
    public Set<CSARContent> findAll() {
        LOGGER.debug("Requesting all CSARs");
        final Set<CSARContent> csars = new HashSet<>();
        try {
            for (Path csarId : Files.newDirectoryStream(CSAR_BASE_PATH, Files::isDirectory)) {
                Path csarPath = CSAR_BASE_PATH.resolve(csarId.getFileName());
                // IRepository repository = RepositoryFactory.getRepository(new
                // FileBasedRepositoryConfiguration(csarPath));
                // FIXME fucking CSARContent basically needs to die!?
                csars.add(new CSARContent());
            }
        }
        catch (IOException e) {
            LOGGER.error("Error when traversing '{}' for CSARs", CSAR_BASE_PATH);
            throw new ServerErrorException(Response.serverError().build());
        }
        return csars;
    }

    @Override
    public CSARContent findById(CSARID id) throws NotFoundException {
        Path assumedPath = CSAR_BASE_PATH.resolve(id.getFileName());
        if (Files.exists(assumedPath)) {
            return new CSARContent(); // FIXME pass path here
        }
        LOGGER.info("CSAR '{}' could not be found", id.getFileName());
        throw new NotFoundException(String.format("CSAR '%s' could not be found", id.getFileName()));
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
    public CSARID storeCSAR(Path csarLocation) throws UserException, SystemException {
         // delegate to CoreService for now:
        return CORE_FILE_SERVICE.storeCSAR(csarLocation);
    }

    @Override
    public void deleteCSAR(CSARID csarId) throws SystemException, UserException {
        CORE_FILE_SERVICE.deleteCSAR(csarId);
    }

}
