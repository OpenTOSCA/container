import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSARTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CSARTest.class);

    public IRepository repository;
    public Path repositoryPath;
    public Git git;
    public Csar csar;

    protected void fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider provider, QName serviceTemplateId, CsarStorageService storage) throws SystemException, UserException, IOException, InterruptedException, ExecutionException, AccountabilityException, RepositoryCorruptException {

        this.repositoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("tosca-definitions-public");
        String remoteUrl = "https://github.com/OpenTOSCA/tosca-definitions-public";

        try {
            LOGGER.debug("Testing with repository directory {}", repositoryPath);

            if (!Files.exists(repositoryPath)) {
                Files.createDirectory(repositoryPath);
            }

            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            if (!Files.exists(repositoryPath.resolve(".git"))) {
                FileUtils.cleanDirectory(repositoryPath.toFile());
                this.git = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setBare(false)
                    .setCloneAllBranches(true)
                    .setDirectory(repositoryPath.toFile())
                    .call();
            } else {
                Repository gitRepo = builder.setWorkTree(repositoryPath.toFile()).setMustExist(false).build();
                this.git = new Git(gitRepo);
                try {
                    this.git.fetch().call();
                } catch (TransportException e) {
                    // we ignore it to enable offline testing
                    LOGGER.debug("Working in offline mode", e);
                }
            }

            // inject the current path to the repository factory
            FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = new FileBasedRepositoryConfiguration(repositoryPath, provider);
            // force xml repository provider
            fileBasedRepositoryConfiguration.setRepositoryProvider(provider);
            GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = new GitBasedRepositoryConfiguration(false, fileBasedRepositoryConfiguration);
            //RepositoryFactory.reconfigure(gitBasedRepositoryConfiguration);

            this.repository = RepositoryFactory.getRepository(repositoryPath);

            //this.repository = new GitBasedRepository(gitBasedRepositoryConfiguration,(AbstractFileBasedRepository) RepositoryFactory.getRepository(repositoryPath));
            LOGGER.debug("Initialized test repository");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CsarExporter exporter = new CsarExporter(this.repository);
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");
        Map<String, Object> exportConfiguration = new HashMap<>();
        exporter.writeCsar(new ServiceTemplateId(serviceTemplateId), Files.newOutputStream(csarFilePath), exportConfiguration);

        CsarId csarId = new CsarId(serviceTemplateId.getLocalPart() + ".csar");
        Set<Csar> csars = storage.findAll();
        Collection<CsarId> csarIds = csars.stream().filter(x -> x.id().equals(csarId)).map(x -> x.id()).collect(Collectors.toList());

        if (!csarIds.contains(csarId)) {
            storage.storeCSAR(csarFilePath);
        }
        this.csar = storage.findById(csarId);
    }
}
