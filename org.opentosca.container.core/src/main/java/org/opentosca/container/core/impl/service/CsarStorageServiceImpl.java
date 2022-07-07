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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.importing.CsarImportOptions;
import org.eclipse.winery.repository.importing.CsarImporter;
import org.eclipse.winery.repository.importing.ImportMetaInformation;
import org.eclipse.winery.repository.importing.YamlCsarImporter;
import org.eclipse.winery.repository.yaml.YamlRepository;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@NonNullByDefault
public class CsarStorageServiceImpl implements CsarStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarStorageServiceImpl.class);

    private static final Object repositoryFactoryConfigurationMutex = new Object();

    private final Path basePath;

    private final Map<CsarId, CsarImpl> csarImpls = new HashMap<>();

    public CsarStorageServiceImpl() {
        try {
            Files.createDirectories(Settings.CONTAINER_STORAGE_BASEPATH);
        } catch (IOException e) {
            LOGGER.error("Could not set up storage for Csars", e);
            throw new ExceptionInInitializerError(e);
        }
        basePath = Settings.CONTAINER_STORAGE_BASEPATH;
    }

    public CsarStorageServiceImpl(Path basePath) {
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            LOGGER.error("Could not set up storage for Csars", e);
            throw new ExceptionInInitializerError(e);
        }
        this.basePath = basePath;
    }

    @Override
    public Set<Csar> findAll() {
        LOGGER.debug("Requesting all CSARs");
        final Set<Csar> csars = new HashSet<>();
        try {
            for (@NonNull Path csarPath : Files.newDirectoryStream(basePath, Files::isDirectory)) {
                CsarId csarId = new CsarId(csarPath.getFileName().toString());
                try {
                    csars.add(findById(csarId));
                } catch (NoSuchElementException e) {
                    LOGGER.warn("Unable to find CSAR with Id: {}", csarId);
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
        return csarImpls.get(id);
    }

    private void addCsarImpl(CsarId id, CsarImpl.CsarImplType implType) {
        Path predictedSaveLocation = basePath.resolve(id.csarName());
        if (Files.exists(predictedSaveLocation)) {
            csarImpls.put(id, new CsarImpl(id, predictedSaveLocation, implType));
        }
    }

    @Override
    @Nullable
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
    public CsarId storeCSAR(Path csarLocation) throws UserException {
        LOGGER.debug("Given file to store: {}", csarLocation);
        if (!Files.isRegularFile(csarLocation)) {
            throw new UserException(
                "\"" + csarLocation + "\" to store is not an absolute path to an existing file.");
        }

        CsarId candidateId = new CsarId(csarLocation.getFileName().toString());
        Path permanentLocation = basePath.resolve(csarLocation.getFileName());
        if (Files.exists(permanentLocation)) {
            throw new UserException(
                "CSAR \"" + candidateId.csarName() + "\" is already stored. Overwriting a CSAR is not allowed.");
        }
        ImportMetaInformation importInfo = null;
        CsarImpl.CsarImplType implType = null;
        try {
            Files.createDirectory(permanentLocation);
            synchronized (repositoryFactoryConfigurationMutex) {
                // CsarImporter doesn't allow overriding the repository it imports to
                // therefore we need to reconfigure the RepositoryFactory to overwrite the target location
                // That configuration must not be changed in a different thread during the import process
                RepositoryFactory.reconfigure(new FileBasedRepositoryConfiguration(permanentLocation));


                IRepository repository = RepositoryFactory.getRepository();

                CsarImporter importer = new CsarImporter(repository);
                final CsarImportOptions importOptions = new CsarImportOptions();
                importOptions.setValidate(false); // avoid triggering accountability meddling with this
                importOptions.setAsyncWPDParsing(true);
                importOptions.setOverwrite(false);
                try {
                    importInfo = importer.readCSAR(Files.newInputStream(csarLocation), importOptions);
                    implType = CsarImpl.CsarImplType.XML;
                } catch (NullPointerException e) {
                    FileBasedRepositoryConfiguration configuration = new FileBasedRepositoryConfiguration();
                    configuration.setRepositoryPath(permanentLocation);
                    configuration.setRepositoryProvider(RepositoryConfigurationObject.RepositoryProvider.YAML);
                    repository = RepositoryFactory.getRepository(configuration);

                    // brutal hack
                    YamlCsarImporter yamlCsarImporter = new YamlCsarImporter((YamlRepository) repository);
                    importInfo = yamlCsarImporter.readCSAR(Files.newInputStream(csarLocation), importOptions);
                    implType = CsarImpl.CsarImplType.YAML;
                }
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
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.warn("CSAR Import failed with an unspecified exception", e);
            FileUtils.forceDelete(permanentLocation);
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
            LOGGER.warn("Could not find EntryServiceTemplate for Csar [{}]", candidateId.csarName());
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.");
        }
        // FIXME don't store this in the winery repo location. Use some database for this!
        try (OutputStream os = Files.newOutputStream(permanentLocation.resolve(CsarImpl.ENTRY_SERVICE_TEMPLATE_LOCATION), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
            os.write(entryServiceTemplate.getQName().toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // well... we failed to keep track of the entryServiceTemplate
            LOGGER.warn("Could not save EntryServiceTemplate for Csar [{}] due to {}", candidateId.csarName(), e);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.");
        }
        this.addCsarImpl(candidateId, implType);
        LOGGER.info("Successfully stored Csar as {}", candidateId.csarName());
        return candidateId;
    }

    @Override
    public void deleteCSAR(CsarId csarId) {
        LOGGER.debug("Deleting CSAR \"{}\"...", csarId.csarName());
        FileUtils.forceDelete(basePath.resolve(csarId.csarName()));
        LOGGER.info("Deleted CSAR \"{}\"...", csarId.csarName());
        this.csarImpls.remove(csarId);
    }

    @Override
    public void purgeCsars() throws SystemException {
        LOGGER.debug("Deleting all CSARs...");
        try {
            for (Path csarRepoContent : Files.newDirectoryStream(basePath)) {
                LOGGER.debug("Deleting CSAR at [{}]", csarRepoContent);
                if (Files.isDirectory(csarRepoContent)) {
                    // delete csar here
                    FileUtils.forceDelete(csarRepoContent);
                }
            }
            LOGGER.debug("Deleting all CSARs completed");
        } catch (IOException e) {
            throw new SystemException("Could not delete all CSARs.", e);
        }
        this.csarImpls.clear();
    }

    @Override
    public Path exportCSAR(final CsarId csarId) throws SystemException {
        LOGGER.debug("Exporting CSAR \"{}\"...", csarId.csarName());
        Csar csar = findById(csarId);

        final Path csarDownloadDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "content");
        try {
            // only create temp directory if it doesn't exist
            if (!Files.exists(csarDownloadDirectory)) {
                Files.createDirectory(csarDownloadDirectory);
            }
            final Path csarTarget = csarDownloadDirectory.resolve(csarId.csarName());
            if (Files.exists(csarTarget)) {
                // remove previous export result
                FileUtils.forceDelete(csarTarget);
            }
            csar.exportTo(csarTarget);
            LOGGER.debug("Successfully exported CSAR to {}", csarTarget);
            return csarTarget;
        } catch (final IOException e) {
            throw new SystemException("An IO Exception occured.", e);
        }
    }
}
