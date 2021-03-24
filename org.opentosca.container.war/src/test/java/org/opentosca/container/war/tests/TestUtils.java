package org.opentosca.container.war.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

	public static Csar fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider provider, QName serviceTemplateId, CsarStorageService storage, Path repositoryPath, String remoteUrl) throws IOException, SystemException, UserException, InterruptedException, ExecutionException, AccountabilityException, RepositoryCorruptException, GitAPIException {       
	    LOGGER.debug("Testing with repository directory {}", repositoryPath);
	
	    if (!Files.exists(repositoryPath)) {
	        Files.createDirectory(repositoryPath);
	    }
	
	    if (!Files.exists(repositoryPath.resolve(".git"))) {
	    	LOGGER.info("No git repository found, cloning repository from " + remoteUrl);
	        FileUtils.cleanDirectory(repositoryPath.toFile());
	
	        Git.cloneRepository()
	            .setURI(remoteUrl)
	            .setBare(false)
	            .setCloneAllBranches(true)
	            .setDirectory(repositoryPath.toFile())
	            .call();
	    } else {
	    	LOGGER.info("Found git repository under " + repositoryPath);
	    }
	
	    // inject the current path to the repository factory
	    FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = new FileBasedRepositoryConfiguration(repositoryPath, provider);
	    // force xml repository provider
	    fileBasedRepositoryConfiguration.setRepositoryProvider(provider);
	
	    IRepository repository = RepositoryFactory.getRepository(repositoryPath);
	
	    LOGGER.debug("Initialized test repository");
	
	    CsarExporter exporter = new CsarExporter(repository);
	    Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");
	
	    Map<String, Object> exportConfiguration = new HashMap<>();
	    exporter.writeCsar(new ServiceTemplateId(serviceTemplateId), Files.newOutputStream(csarFilePath), exportConfiguration);
	
	    CsarId csarId = new CsarId(serviceTemplateId.getLocalPart() + ".csar");
	    Set<Csar> csars = storage.findAll();
	    Collection<CsarId> csarIds = csars.stream().filter(x -> x.id().equals(csarId)).map(x -> x.id()).collect(Collectors.toList());
	
	    if (!csarIds.contains(csarId)) {
	        storage.storeCSAR(csarFilePath);
	    }
	    return storage.findById(csarId);
	}

}
