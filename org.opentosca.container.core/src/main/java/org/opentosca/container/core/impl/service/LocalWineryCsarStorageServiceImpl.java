package org.opentosca.container.core.impl.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.importing.CsarImportOptions;
import org.eclipse.winery.repository.importing.CsarImporter;
import org.eclipse.winery.repository.importing.ImportMetaInformation;

import org.eclipse.jdt.annotation.NonNull;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalWineryCsarStorageServiceImpl implements CsarStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarStorageServiceImpl.class);

    private static final Object repositoryFactoryConfigurationMutex = new Object();

    private final Path basePath;
    private final Path serviceTemplatesPath;

    public LocalWineryCsarStorageServiceImpl() {
        if (this.isLocalWineryRepositoryAvailable()) {
            basePath = Paths.get(Settings.OPENTOSCA_CONTAINER_LOCAL_WINERY_REPOSITORY);
            serviceTemplatesPath = basePath.resolve("servicetemplates");
        } else {
            LOGGER.error("Could not set up storage for Csars");
            throw new ExceptionInInitializerError("Local winery storage not specified");
        }
    }

    public boolean isLocalWineryRepositoryAvailable(){
        return Settings.OPENTOSCA_CONTAINER_LOCAL_WINERY_REPOSITORY != null && !Settings.OPENTOSCA_CONTAINER_LOCAL_WINERY_REPOSITORY.isEmpty();
    }

    public Path csarIdToServiceTemplatePath(CsarId csarId) {
        Csar csar = this.findAll().stream().filter(x -> x.id().equals(csarId)).findFirst().orElse(null);
        if(csar != null){
            return csar.getSaveLocation();
        } else {
            return null;
        }
    }

    @Override
    public Set<Csar> findAll() {
        LOGGER.debug("Requesting all CSARs");
        final Set<Csar> csars = new HashSet<>();
        try {
            for (@NonNull Path namespace : Files.newDirectoryStream(serviceTemplatesPath, Files::isDirectory)) {
                // FIXME make CsarId a name and put the path somewhere else
                for (@NonNull Path csarId : Files.newDirectoryStream(namespace, Files::isDirectory)) {
                    csars.add(new CsarImpl(new CsarId(csarId.getFileName().toString()), csarId));
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error when traversing '{}' for CSARs", basePath);
            throw new UncheckedIOException(e);
        }
        return csars;
    }

    @Override
    public Csar findById(CsarId id) throws NoSuchElementException {
        return this.findAll().stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Path storeCSARTemporarily(String filename, InputStream is) {
        try {
            Path tempLocation = Paths.get(System.getProperty("java.io.tmpdir"), filename);
            if (Files.exists(tempLocation)) {
                // well ... umm ... let's just delete it, I guess?
                Files.delete(tempLocation);
            }
            Files.copy(is, tempLocation);
            return tempLocation;
        } catch (IOException e) {
            LOGGER.error("Exception occured when writing temporary CSAR file: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public CsarId storeCSAR(Path csarLocation) throws UserException, SystemException {
        LOGGER.debug("Given file to store: {}", csarLocation);
        if (!Files.isRegularFile(csarLocation)) {
            throw new UserException(
                "\"" + csarLocation.toString() + "\" to store is not an absolute path to an existing file.");
        }

        CsarId candidateId = new CsarId(csarLocation.getFileName().toString());
        Path permanentLocation = basePath;
        ImportMetaInformation importInfo = null;
        try {
            Files.createDirectory(permanentLocation);
            synchronized (repositoryFactoryConfigurationMutex) {
                // CsarImporter doesn't allow overriding the repository it imports to
                // therefore we need to reconfigure the RepositoryFactory to overwrite the target location
                // That configuration must not be changed in a different thread during the import process
                RepositoryFactory.reconfigure(new FileBasedRepositoryConfiguration(permanentLocation));

                CsarImporter importer = new CsarImporter(RepositoryFactory.getRepository());
                final CsarImportOptions importOptions = new CsarImportOptions();
                importOptions.setValidate(false); // avoid triggering accountability meddling with this
                importOptions.setAsyncWPDParsing(true);
                importOptions.setOverwrite(false);
                importInfo = importer.readCSAR(Files.newInputStream(csarLocation), importOptions);
            }
            if (!importInfo.errors.isEmpty()) {
                throw new UserException("Importing the csar failed with errors: " + importInfo.errors.stream().collect(Collectors.joining(System.lineSeparator())));
            }
        } catch (IOException e) {
            // roll back the import
            FileUtils.forceDelete(permanentLocation);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        } catch (AccountabilityException e) {
            LOGGER.debug("Accountability for imported CSAR could not be checked", e);
            FileUtils.forceDelete(permanentLocation);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.warn("CSAR Import was interrupted or terminated with an exception", e);
            FileUtils.forceDelete(permanentLocation);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        } catch (Throwable e) {
            LOGGER.warn("CSAR Import failed with an unspecified exception", e);
            FileUtils.forceDelete(permanentLocation);
            if (e instanceof RuntimeException || e instanceof Error) {
                throw e;
            }
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        }
        assert (importInfo != null);
        // if (importInfo == null || !importInfo.errors.isEmpty()) {
        //   LOGGER.info("Import failed with information ", importInfo);
        //   throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported: ");
        // }
        // apparently there will always be an EntryServiceTemplate??
        ServiceTemplateId entryServiceTemplate = importInfo.entryServiceTemplate;
        // we may be able to "guarantee" it's not null, since we validate CSARs on import
        if (entryServiceTemplate == null) {
            return candidateId;
        }
        // FIXME don't store this in the winery repo location. Use some database for this!
        try (OutputStream os = Files.newOutputStream(permanentLocation.resolve(CsarImpl.ENTRY_SERVICE_TEMPLATE_LOCATION), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
            os.write(entryServiceTemplate.getQName().toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // well... we failed to keep track of the entryServiceTemplate
            LOGGER.warn("Could not save EntryServiceTemplate for Csar [{}] due to {}", candidateId.csarName(), e);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.");
        }
        LOGGER.info("Successfully stored Csar as {}", candidateId.csarName());
        return candidateId;
    }

    @Override
    public void deleteCSAR(CsarId csarId) throws UserException, SystemException {

    }

    @Override
    public void purgeCsars() throws SystemException {

    }

    @Override
    public Path exportCSAR(CsarId csarId) throws UserException, SystemException {
        return null;
    }
}
