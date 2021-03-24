package org.opentosca.container.war.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;
import org.junit.Assert;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
		
	
	public static Csar setupCsarTestRepository(QName csarId, CsarStorageService storage) throws RepositoryCorruptException, IOException, SystemException, UserException, InterruptedException, ExecutionException, AccountabilityException, GitAPIException {
		String testLocalRepositoryPath = Settings.OPENTOSCA_TEST_LOCAL_REPOSITORY_PATH;
    	String testRemoteRepositoryUrl = Settings.OPENTOSCA_TEST_REMOTE_REPOSITORY_URL;
    	
    	Path repositoryPath;
    	if(testLocalRepositoryPath != null && !testLocalRepositoryPath.isEmpty()) {
    		repositoryPath = Paths.get(testLocalRepositoryPath);
    	} else {
    		repositoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("opentosca-test-repository");;
    	}
    	
    	String remoteUrl;
    	if(testRemoteRepositoryUrl != null && !testRemoteRepositoryUrl.isEmpty()) {
    		remoteUrl = testRemoteRepositoryUrl;
    	} else {
    		remoteUrl = null;
    	}
        
    	if(repositoryPath == null & remoteUrl == null) {
    		Assert.fail("Neither local repository path or remote url is defined");    		
    	}
    	return TestUtils.fetchCSARFromRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, csarId, storage, repositoryPath, remoteUrl);
	}

	public static Csar fetchCSARFromRepository(RepositoryConfigurationObject.RepositoryProvider provider, QName serviceTemplateId, CsarStorageService storage, Path repositoryPath, String remoteUrl) throws IOException, SystemException, UserException, InterruptedException, ExecutionException, AccountabilityException, RepositoryCorruptException, GitAPIException {       
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

	public static void generatePlans(CsarService csarService, Csar csar) {
	    try {
	        Assert.assertTrue(csarService.generatePlans(csar));
	    } catch (SystemException e) {
	        e.printStackTrace();
	        Assert.fail(e.getMessage());
	    } catch (UserException e) {
	        e.printStackTrace();
	        Assert.fail(e.getMessage());
	    }
	}

	public static void invokePlanDeployment(OpenToscaControlService control, CsarId csarId, TServiceTemplate serviceTemplate) {
		control.invokePlanDeployment(csarId, serviceTemplate);
	}

	public static void runTerminationPlanExecution(PlanService planService, Csar csar, String serviceInstanceUrl, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan terminationPlan) {
	    List<org.opentosca.container.core.extension.TParameter> terminationOutInputParams = TestUtils.getTerminationPlanInputParameters(serviceInstanceUrl);
	    String terminationPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), terminationPlan.getId(), terminationOutInputParams, PlanType.TERMINATION);
	    PlanInstance terminationPlanInstance = planService.getPlanInstanceByCorrelationId(terminationPlanCorrelationId);
	    while (terminationPlanInstance == null) {
	        terminationPlanInstance = planService.getPlanInstanceByCorrelationId(terminationPlanCorrelationId);
	    }
	
	    PlanInstanceState terminationPlanInstanceState = terminationPlanInstance.getState();
	    while (!terminationPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
	        terminationPlanInstance = planService.getPlanInstance(terminationPlanInstance.getId());
	        terminationPlanInstanceState = terminationPlanInstance.getState();
	    }
	}

	public static List<org.opentosca.container.core.extension.TParameter> getTerminationPlanInputParameters(String serviceInstanceUrl) {
	    List<org.opentosca.container.core.extension.TParameter> inputParams = TestUtils.getBaseInputParams();
	
	    org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
	    serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
	    serviceInstanceUrlParam.setType("String");
	    serviceInstanceUrlParam.setValue(serviceInstanceUrl);
	    serviceInstanceUrlParam.setRequired(true);
	
	    inputParams.add(serviceInstanceUrlParam);
	    return inputParams;
	}

	public static List<org.opentosca.container.core.extension.TParameter> getBaseInputParams() {
	    List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();
	
	    org.opentosca.container.core.extension.TParameter instanceDataAPIUrl = new org.opentosca.container.core.extension.TParameter();
	    instanceDataAPIUrl.setName("instanceDataAPIUrl");
	    instanceDataAPIUrl.setType("String");
	    instanceDataAPIUrl.setValue(null);
	    instanceDataAPIUrl.setRequired(true);
	
	    org.opentosca.container.core.extension.TParameter csarEntrypoint = new org.opentosca.container.core.extension.TParameter();
	    csarEntrypoint.setName("csarEntrypoint");
	    csarEntrypoint.setType("String");
	    csarEntrypoint.setValue(null);
	    csarEntrypoint.setRequired(true);
	
	    org.opentosca.container.core.extension.TParameter correlationId = new org.opentosca.container.core.extension.TParameter();
	    correlationId.setName("CorrelationID");
	    correlationId.setType("String");
	    correlationId.setValue(null);
	    correlationId.setRequired(true);
	
	    inputParams.add(instanceDataAPIUrl);
	    inputParams.add(csarEntrypoint);
	    inputParams.add(correlationId);
	
	    return inputParams;
	}

	public static ServiceTemplateInstance runBuildPlanExecution(PlanService planService, InstanceService instanceService, Csar csar, TServiceTemplate serviceTemplate, TPlan buildPlan, List<org.opentosca.container.core.extension.TParameter> buildPlanInputParams) {        
	    String buildPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, -1L, buildPlan.getId(), buildPlanInputParams, PlanType.BUILD);
	    PlanInstance buildPlanInstance = planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
	    while (buildPlanInstance == null) {
	        buildPlanInstance = planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
	    }
	
	    PlanInstanceState buildPlanInstanceState = buildPlanInstance.getState();
	    while (!buildPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
	        buildPlanInstance = planService.getPlanInstance(buildPlanInstance.getId());
	        buildPlanInstanceState = buildPlanInstance.getState();
	    }
	
	    ServiceTemplateInstance serviceTemplateInstance = instanceService.getServiceTemplateInstance(buildPlanInstance.getServiceTemplateInstance().getId(), false);
	    return serviceTemplateInstance;
	}

	public static void runManagementPlanExecution(PlanService planService, Csar csar, String serviceInstanceUrl, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan scaleOutPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {        
	    String scaleOurPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), scaleOutPlan.getId(), inputParams, PlanType.MANAGEMENT);
	    PlanInstance scaleOutPlanInstance = planService.getPlanInstanceByCorrelationId(scaleOurPlanCorrelationId);
	    while (scaleOutPlanInstance == null) {
	        scaleOutPlanInstance = planService.getPlanInstanceByCorrelationId(scaleOurPlanCorrelationId);
	    }
	
	    PlanInstanceState scaleOutPlanInstanceState = scaleOutPlanInstance.getState();
	    while (!scaleOutPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
	        scaleOutPlanInstance = planService.getPlanInstance(scaleOutPlanInstance.getId());
	        scaleOutPlanInstanceState = scaleOutPlanInstance.getState();
	    }
	}

	public static String createServiceInstanceUrl(String csarId, String serviceTemplateId, String serviceInstanceId) {
	    return Settings.CONTAINER_INSTANCEDATA_API.replace("{csarid}", csarId).replace("{servicetemplateid}", serviceTemplateId) + "/" + serviceInstanceId;
	}

	public static void clearContainer(CsarStorageService storage, OpenToscaControlService control) {
	    storage.findAll().forEach(x -> control.deleteCsar(x.id()));
	}

}
