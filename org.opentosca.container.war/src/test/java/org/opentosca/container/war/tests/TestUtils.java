package org.opentosca.container.war.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
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

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.opentosca.container.api.service.PlanInvokerService;
import org.opentosca.container.control.winery.WineryConnector;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.control.plan.PlanGenerationService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.Endpoint;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.services.instances.NodeTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.PlanInstanceService;
import org.opentosca.container.core.next.services.instances.RelationshipTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.ServiceTemplateInstanceService;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.common.Constants.DEFAULT_LOCAL_REPO_NAME;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_DefrostPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_FreezePlanOperation;

public class TestUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    public Csar setupCsarTestRepository(QName csarId, CsarStorageService storage) throws Exception {
        return setupCsarTestRepository(csarId, storage, Settings.OPENTOSCA_TEST_REMOTE_REPOSITORY_URL);
    }

    public Collection<QName> getServiceTemplateIdsFromWineryRepository(String wineryRepositoryUrl) {
        WineryConnector connector = new WineryConnector();
        return connector.getServiceTemplates(wineryRepositoryUrl);
    }

    public void enrichCsarFile(Path file, String wineryLocation) {
        WineryConnector connector = new WineryConnector();
        connector.performManagementFeatureEnrichment(file.toFile(), true, wineryLocation);
    }

    public void clearWineryRepository(String wineryRepositoryUrl) {
        WineryConnector connector = new WineryConnector();
        connector.clearRepository(wineryRepositoryUrl);
    }

    public void uploadCsarToWineryRepository(QName serviceTemplateId, String wineryRepositoryUrl, String testRemoteRepositoryUrl) throws GitAPIException, IOException, AccountabilityException, RepositoryCorruptException, ExecutionException, InterruptedException, URISyntaxException {
        String testLocalRepositoryPath = Settings.OPENTOSCA_TEST_LOCAL_REPOSITORY_PATH;

        Path repositoryPath;
        if (testLocalRepositoryPath != null && !testLocalRepositoryPath.isEmpty()) {
            repositoryPath = Paths.get(testLocalRepositoryPath);
        } else {
            repositoryPath = getRepositoryPath(testRemoteRepositoryUrl);
        }

        String remoteUrl;
        if (testRemoteRepositoryUrl != null && !testRemoteRepositoryUrl.isEmpty()) {
            remoteUrl = testRemoteRepositoryUrl;
        } else {
            remoteUrl = null;
        }

        IRepository repository = fetchRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, repositoryPath, remoteUrl);

        CsarExporter exporter = new CsarExporter(repository);
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");

        Map<String, Object> exportConfiguration = new HashMap<>();
        exporter.writeCsar(new ServiceTemplateId(serviceTemplateId), Files.newOutputStream(csarFilePath), exportConfiguration);

        WineryConnector connector = new WineryConnector();
        connector.uploadCSAR(csarFilePath.toFile(), true, wineryRepositoryUrl);
    }

    public Csar setupCsarTestRepository(QName csarId, CsarStorageService storage, String testRemoteRepositoryUrl) throws Exception {
        String testLocalRepositoryPath = Settings.OPENTOSCA_TEST_LOCAL_REPOSITORY_PATH;

        Path repositoryPath;
        if (testLocalRepositoryPath != null && !testLocalRepositoryPath.isEmpty()) {
            repositoryPath = Paths.get(testLocalRepositoryPath);
        } else {
            repositoryPath = getRepositoryPath(testRemoteRepositoryUrl);
        }

        String remoteUrl;
        if (testRemoteRepositoryUrl != null && !testRemoteRepositoryUrl.isEmpty()) {
            remoteUrl = testRemoteRepositoryUrl;
        } else {
            remoteUrl = null;
        }

        return this.loadCSARFromRepositoryIntoStorage(RepositoryConfigurationObject.RepositoryProvider.FILE, csarId, storage, repositoryPath, remoteUrl);
    }

    private Path getRepositoryPath(String testRemoteRepositoryUrl) {
        Path repositoryPath;
        String repoSuffix = "";
        if (testRemoteRepositoryUrl != null) {
            String[] split = testRemoteRepositoryUrl.split("/");
            if (split.length > 0) {
                repoSuffix = split[split.length - 1];
            }
        }
        repositoryPath = Paths.get(System.getProperty("java.io.tmpdir"))
            .resolve("opentosca-test-repository-" + repoSuffix);
        LOGGER.info("Using repository path '{}'", repositoryPath);
        return repositoryPath;
    }

    public Csar loadCSARFromRepositoryIntoStorage(RepositoryConfigurationObject.RepositoryProvider provider, QName serviceTemplateId,
                                                  CsarStorageService storage, Path repositoryInputPath, String remoteUrl)
        throws Exception {
        IRepository repository = fetchRepository(provider, repositoryInputPath, remoteUrl);
        LOGGER.debug("Initialized test repository");

        Path csarFilePath = exportCsarFromRepository(repository, serviceTemplateId);

        return storeCsarFileIntoStorage(serviceTemplateId, storage, csarFilePath);
    }

    public Path exportCsarFromRepository(IRepository repository, QName serviceTemplateId) throws IOException, AccountabilityException, RepositoryCorruptException, ExecutionException, InterruptedException {
        CsarExporter exporter = new CsarExporter(repository);
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");

        Map<String, Object> exportConfiguration = new HashMap<>();
        exporter.writeCsar(new ServiceTemplateId(serviceTemplateId), Files.newOutputStream(csarFilePath), exportConfiguration);
        return csarFilePath;
    }

    public Csar storeCsarFileIntoStorage(QName serviceTemplateId, CsarStorageService storage, Path csarFilePath) throws SystemException, UserException {
        CsarId csarId = new CsarId(serviceTemplateId.getLocalPart() + ".csar");
        Set<Csar> csars = storage.findAll();
        Collection<CsarId> csarIds = csars.stream()
            .map(Csar::id)
            .filter(id -> id.equals(csarId))
            .collect(Collectors.toList());

        if (!csarIds.contains(csarId)) {
            storage.storeCSAR(csarFilePath);
        }
        return storage.findById(csarId);
    }

    public IRepository fetchRepository(String testRemoteRepositoryUrl) throws GitAPIException, IOException {
        String testLocalRepositoryPath = Settings.OPENTOSCA_TEST_LOCAL_REPOSITORY_PATH;

        Path repositoryPath;
        if (testLocalRepositoryPath != null && !testLocalRepositoryPath.isEmpty()) {
            repositoryPath = Paths.get(testLocalRepositoryPath);
        } else {
            repositoryPath = getRepositoryPath(testRemoteRepositoryUrl);
        }

        String remoteUrl;
        if (testRemoteRepositoryUrl != null && !testRemoteRepositoryUrl.isEmpty()) {
            remoteUrl = testRemoteRepositoryUrl;
        } else {
            remoteUrl = null;
        }
        return fetchRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, repositoryPath, remoteUrl);
    }

    private IRepository fetchRepository(RepositoryConfigurationObject.RepositoryProvider provider, Path repositoryInputPath, String remoteUrl) throws GitAPIException, IOException {
        Path repositoryPath = repositoryInputPath;
        LOGGER.info("Testing with repository directory '{}'", repositoryPath);
        boolean isInitializedRepo = false;
        if (!Files.exists(repositoryPath)) {
            Files.createDirectory(repositoryPath);
        }

        if (!Files.exists(repositoryPath.resolve(".git")) && remoteUrl != null && !Files.exists(repositoryPath.resolve(DEFAULT_LOCAL_REPO_NAME).resolve(".git"))) {
            LOGGER.info("No git repository found, cloning repository from " + remoteUrl);
            cloneRepo(repositoryPath, remoteUrl);
        } else {
            if (remoteUrl == null) {
                LOGGER.info("Remote URL is undefined");
                if (Files.exists(repositoryPath.resolve(DEFAULT_LOCAL_REPO_NAME).resolve(".git"))) {
                    LOGGER.info("Found git repo under /workspace in '{}'", repositoryPath);
                    isInitializedRepo = true;
                }
            } else {
                boolean isCorrectRepository;
                try {
                    isCorrectRepository = Git.open(repositoryPath.toFile())
                        .remoteList().call()
                        .stream().anyMatch(remote ->
                            remote.getURIs().stream().anyMatch(uri -> uri.toASCIIString().equals(remoteUrl))
                        );
                } catch (Exception e) {
                    try {
                        isCorrectRepository = Git.open(repositoryPath.resolve(DEFAULT_LOCAL_REPO_NAME).toFile())
                            .remoteList().call()
                            .stream().anyMatch(remote ->
                                remote.getURIs().stream().anyMatch(uri -> uri.toASCIIString().equals(remoteUrl))
                            );
                    } catch (Exception e1) {
                        LOGGER.error("Something went badly wrong!", e);
                        isCorrectRepository = false;
                    }
                }
                if (!isCorrectRepository && remoteUrl != null && !remoteUrl.isEmpty()) {
                    repositoryPath = getRepositoryPath(remoteUrl);
                    cloneRepo(repositoryPath, remoteUrl);
                }
            }
        }

        // inject the current path to the repository factory
        if (!isInitializedRepo) {
            RepositoryFactory.reconfigure(
                new GitBasedRepositoryConfiguration(false, new FileBasedRepositoryConfiguration(repositoryPath, provider))
            );
        } else {
            RepositoryFactory.reconfigure(new FileBasedRepositoryConfiguration(repositoryPath, provider));
        }

        return RepositoryFactory.getRepository();
    }

    private void cloneRepo(Path repositoryPath, String remoteUrl) throws IOException, GitAPIException {
        if (!Files.exists(repositoryPath)) {
            Files.createDirectory(repositoryPath);
        }
        FileUtils.cleanDirectory(repositoryPath.toFile());

        Git.cloneRepository()
            .setURI(remoteUrl)
            .setBare(false)
            .setCloneAllBranches(true)
            .setDirectory(repositoryPath.toFile())
            .call();
    }

    public void generatePlans(PlanGenerationService planGenerationService, Csar csar) {
        try {
            Assert.assertTrue(planGenerationService.generatePlans(csar));
        } catch (SystemException | UserException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public void invokePlanDeployment(OpenToscaControlService control, CsarId csarId, TServiceTemplate serviceTemplate) {
        control.invokePlanDeployment(csarId, serviceTemplate);
    }

    public void invokePlanUndeployment(OpenToscaControlService control, CsarId csarId, TServiceTemplate serviceTemplate) {
        control.undeployAllPlans(csarId, serviceTemplate);
    }

    public Collection<Endpoint> getDeployedPlans(ICoreEndpointService endpointService) {
        Collection<Endpoint> endpoints = endpointService.getEndpointsWithMetadata();
        // if it has a planId and not a portType of a callback we have a plan endpoint
        return endpoints.stream().filter(endpoint -> endpoint.getMetadata() != null
            && endpoint.getMetadata().containsKey("PlanType")
            && endpoint.getMetadata().containsKey("EndpointType")
            && endpoint.getMetadata().get("EndpointType").equals("Invoke")).collect(Collectors.toList());
    }

    public List<org.opentosca.container.core.extension.TParameter> getFreezePlanInputParameters(String serviceInstanceUrl, String wineryUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = this.getBaseInputParams();

        org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        org.opentosca.container.core.extension.TParameter containerApiAddress = new org.opentosca.container.core.extension.TParameter();
        containerApiAddress.setName("containerApiAddress");
        containerApiAddress.setType("String");
        containerApiAddress.setValue(null);
        containerApiAddress.setRequired(true);

        org.opentosca.container.core.extension.TParameter storeStateEndpoint = new org.opentosca.container.core.extension.TParameter();
        storeStateEndpoint.setName("StoreStateServiceEndpoint");
        storeStateEndpoint.setRequired(true);
        storeStateEndpoint.setType("String");
        storeStateEndpoint.setValue(wineryUrl);

        inputParams.add(serviceInstanceUrlParam);
        inputParams.add(containerApiAddress);
        inputParams.add(storeStateEndpoint);

        return inputParams;
    }

    public List<org.opentosca.container.core.extension.TParameter> getTerminationPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = this.getBaseInputParams();

        org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        org.opentosca.container.core.extension.TParameter containerApiAddress = new org.opentosca.container.core.extension.TParameter();
        containerApiAddress.setName("containerApiAddress");
        containerApiAddress.setType("String");
        containerApiAddress.setValue(null);
        containerApiAddress.setRequired(true);

        inputParams.add(serviceInstanceUrlParam);
        inputParams.add(containerApiAddress);
        return inputParams;
    }

    public List<org.opentosca.container.core.extension.TParameter> getBaseInputParams() {
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

        org.opentosca.container.core.extension.TParameter containerApiAddress = new org.opentosca.container.core.extension.TParameter();
        containerApiAddress.setName("containerApiAddress");
        containerApiAddress.setType("String");
        containerApiAddress.setValue(null);
        containerApiAddress.setRequired(true);

        org.opentosca.container.core.extension.TParameter correlationId = new org.opentosca.container.core.extension.TParameter();
        correlationId.setName("CorrelationID");
        correlationId.setType("String");
        correlationId.setValue(null);
        correlationId.setRequired(true);

        inputParams.add(instanceDataAPIUrl);
        inputParams.add(csarEntrypoint);
        inputParams.add(correlationId);
        inputParams.add(containerApiAddress);

        return inputParams;
    }

    public String getDockerHost() {
        String os = SystemUtils.OS_NAME;
        if (os.toLowerCase().contains("windows")) {
            return "host.docker.internal";
        } else {
            return "172.17.0.1";
        }
    }

    public Path downloadServiceTemplateFromWinery(QName serviceTemplateId, String wineryRepository) throws IOException {
        WineryConnector connector = new WineryConnector();
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");
        connector.downloadServiceTemplate(csarFilePath, serviceTemplateId, wineryRepository);
        return csarFilePath;
    }

    public void runTerminationPlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan terminationPlan) {
        List<org.opentosca.container.core.extension.TParameter> terminationOutInputParams = this.getTerminationPlanInputParameters(this.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString()));
        String terminationPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), terminationPlan.getId(), terminationOutInputParams, PlanType.TERMINATION);

        PlanInstance terminationPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(terminationPlanCorrelationId).joinAndGet();

        terminationPlanInstance = (PlanInstance) planInstanceService.waitForStateChange(terminationPlanInstance, PlanInstanceState.FINISHED).joinAndGet();
    }

    public void runFreezePlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan terminationPlan, String wineryRepositoryUrl) {
        List<org.opentosca.container.core.extension.TParameter> terminationOutInputParams = this.getFreezePlanInputParameters(this.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString()), wineryRepositoryUrl);
        String freezePlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), terminationPlan.getId(), terminationOutInputParams, PlanType.TERMINATION);

        PlanInstance freezePlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(freezePlanCorrelationId).joinAndGet();

        freezePlanInstance = (PlanInstance) planInstanceService.waitForStateChange(freezePlanInstance, PlanInstanceState.FINISHED).joinAndGet();
    }

    public ServiceTemplateInstance runAdaptationPlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, ServiceTemplateInstanceService serviceTemplateInstanceService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan adaptPlan, List<org.opentosca.container.core.extension.TParameter> adaptPlanInputParams) {
        String buildPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), adaptPlan.getId(), adaptPlanInputParams, PlanType.TRANSFORMATION);

        PlanInstance buildPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(buildPlanCorrelationId).joinAndGet();

        buildPlanInstance = (PlanInstance) planInstanceService.waitForStateChange(buildPlanInstance, PlanInstanceState.FINISHED).joinAndGet();

        return serviceTemplateInstanceService.getServiceTemplateInstance(buildPlanInstance.getServiceTemplateInstance().getId(), false);
    }

    public ServiceTemplateInstance runBuildPlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, ServiceTemplateInstanceService serviceTemplateInstanceService, Csar csar, TServiceTemplate serviceTemplate, TPlan buildPlan, List<org.opentosca.container.core.extension.TParameter> buildPlanInputParams) {
        String buildPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, -1L, buildPlan.getId(), buildPlanInputParams, PlanType.BUILD);

        // TODO we should remove this, it is only necessary right now because the bpmn plans don't log properly
        if (buildPlan.getPlanLanguage().contains("BPMN")) {
            return this.waitForServiceInstanceCreation(serviceTemplateInstanceService, serviceTemplate);
        }

        PlanInstance buildPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(buildPlanCorrelationId).joinAndGet();

        buildPlanInstance = (PlanInstance) planInstanceService.waitForStateChange(buildPlanInstance, PlanInstanceState.FINISHED).joinAndGet();

        return serviceTemplateInstanceService.getServiceTemplateInstance(buildPlanInstance.getServiceTemplateInstance().getId(), false);
    }

    public ServiceTemplateInstance runDefrostPlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, ServiceTemplateInstanceService serviceTemplateInstanceService, Csar csar, TServiceTemplate serviceTemplate, TPlan defrostPlan, List<org.opentosca.container.core.extension.TParameter> buildPlanInputParams) {
        String defrostPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, -1L, defrostPlan.getId(), buildPlanInputParams, PlanType.BUILD);

        // TODO we should remove this, it is only necessary right now because the bpmn plans don't log properly
        if (defrostPlan.getPlanLanguage().contains("BPMN")) {
            return this.waitForServiceInstanceCreation(serviceTemplateInstanceService, serviceTemplate);
        }

        PlanInstance defrostPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(defrostPlanCorrelationId).joinAndGet();

        defrostPlanInstance = (PlanInstance) planInstanceService.waitForStateChange(defrostPlanInstance, PlanInstanceState.FINISHED).joinAndGet();
        return serviceTemplateInstanceService.getServiceTemplateInstance(defrostPlanInstance.getServiceTemplateInstance().getId(), false);
    }

    public void runManagementPlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan scaleOutPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {
        String scaleOurPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), scaleOutPlan.getId(), inputParams, PlanType.MANAGEMENT);

        PlanInstance scaleOutPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(scaleOurPlanCorrelationId).joinAndGet();

        planInstanceService.waitForStateChange(scaleOutPlanInstance, PlanInstanceState.FINISHED).joinAndGet();
    }

    public void runBackupPlanExecution(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan backupPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {
        String backupPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), backupPlan.getId(), inputParams, PlanType.MANAGEMENT);

        PlanInstance backupPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(backupPlanCorrelationId).joinAndGet();

        backupPlanInstance = (PlanInstance) planInstanceService.waitForStateChange(backupPlanInstance, PlanInstanceState.FINISHED).joinAndGet();
    }

    public ServiceTemplateInstance runTransformationPlan(PlanInstanceService planInstanceService, PlanInvokerService planInvokerService, ServiceTemplateInstanceService serviceTemplateInstanceService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan transformationPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {
        String tranformationPlanCorrelationId = planInvokerService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), transformationPlan.getId(), inputParams, PlanType.TRANSFORMATION);

        PlanInstance transformationPlanInstance = (PlanInstance) planInstanceService.waitForInstanceAvailable(tranformationPlanCorrelationId).joinAndGet();

        transformationPlanInstance = (PlanInstance) planInstanceService.waitForStateChange(transformationPlanInstance, PlanInstanceState.FINISHED).joinAndGet();

        transformationPlanInstance = planInstanceService.getPlanInstanceWithOutputs(transformationPlanInstance.getId());

        for (PlanInstanceOutput output : transformationPlanInstance.getOutputs()) {
            if (output.getName().equals("instanceId")) {
                String serviceInstanceId = output.getValue();
                return serviceTemplateInstanceService.getServiceTemplateInstance(Long.valueOf(serviceInstanceId), false);
            }
        }
        return null;
    }

    public ServiceTemplateInstance waitForServiceInstanceCreation(ServiceTemplateInstanceService serviceTemplateInstanceService, TServiceTemplate serviceTemplate) {
        Collection<ServiceTemplateInstance> coll = serviceTemplateInstanceService.getServiceTemplateInstances(serviceTemplate.getId());
        ServiceTemplateInstance s = new ServiceTemplateInstance();
        while (coll.size() != 1) {
            coll = serviceTemplateInstanceService.getServiceTemplateInstances(serviceTemplate.getId());
        }

        for (ServiceTemplateInstance serviceTemplateInstance : coll) {
            s = serviceTemplateInstance;
        }
        ServiceTemplateInstanceState state = serviceTemplateInstanceService.getServiceTemplateInstanceState(s.getId());

        while ((state != ServiceTemplateInstanceState.CREATED)) {
            state = serviceTemplateInstanceService.getServiceTemplateInstanceState(s.getId());
        }
        s.setState(state);
        return s;
    }

    public String createServiceInstanceUrl(String csarId, String serviceTemplateId, String serviceInstanceId) {
        return Settings.CONTAINER_INSTANCEDATA_API.replace("{csarid}", csarId).replace("{servicetemplateid}", serviceTemplateId) + "/" + serviceInstanceId;
    }

    public void clearContainer(CsarStorageService storage, OpenToscaControlService control,
                               PlanInstanceService planInstanceService,
                               RelationshipTemplateInstanceService relationshipTemplateInstanceService,
                               NodeTemplateInstanceService nodeTemplateInstanceService,
                               ServiceTemplateInstanceService serviceTemplateInstanceService) {
        storage.findAll().forEach(x -> control.deleteCsar(x.id()));

        // after deleting all CSARs, also all related instances should be removed from the database
        Assert.assertEquals(0, planInstanceService.getPlanInstances().size());
        Assert.assertEquals(0, relationshipTemplateInstanceService.getRelationshipTemplateInstances().size());
        Assert.assertEquals(0, nodeTemplateInstanceService.getNodeTemplateInstances().size());
        Assert.assertEquals(0, serviceTemplateInstanceService.getServiceTemplateInstances().size());
    }

    public void checkViaHTTPGET(String url, int expectedStatus, String contains) throws IOException {
        URL location = new URL(url);
        int retries = 0;
        int maxRetries = 10;
        int status = -1;
        HttpURLConnection con;

        do {
            con = (HttpURLConnection) location.openConnection();
            con.setRequestMethod("GET");
            status = con.getResponseCode();
        } while (status != expectedStatus && retries++ < maxRetries);

        Assert.assertEquals(expectedStatus, status);

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();

        if (contains != null && !contains.isEmpty()) {
            Assert.assertTrue(content.toString().contains(contains));
        }
    }

    public TPlan getBuildPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD)
                && !plan.getId().toLowerCase().contains(OpenTOSCA_DefrostPlanOperation)
                && plan.getId().toLowerCase().contains("buildplan") && plan.getPlanLanguage().contains("BPMN")) {
                return plan;
            }
        }
        return null;
    }

    public TPlan getBPELBuildPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD)
                && !plan.getId().toLowerCase().contains(OpenTOSCA_DefrostPlanOperation)
                && plan.getId().toLowerCase().contains("buildplan") && plan.getPlanLanguage().contains("BPEL")) {
                return plan;
            }
        }
        return null;
    }

    public List<TPlan> getBuildPlans(List<TPlan> plans) {
        List<TPlan> buildPlans = new ArrayList<>();
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD)
                && !plan.getId().toLowerCase().contains(OpenTOSCA_DefrostPlanOperation)
                && plan.getId().toLowerCase().contains("buildplan")) {
                buildPlans.add(plan);
            }
        }
        return buildPlans;
    }

    public TPlan getTerminationPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.TERMINATION)
                && !plan.getId().toLowerCase().contains(OpenTOSCA_FreezePlanOperation)
                && plan.getId().toLowerCase().contains("terminationplan")) {
                return plan;
            }
        }
        return null;
    }

    public TPlan getScaleOutPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.MANAGEMENT) && plan.getId().toLowerCase().contains("scale")) {
                return plan;
            }
        }
        return null;
    }

    public TPlan getTransformationPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.TRANSFORMATION)) {
                return plan;
            }
        }
        return null;
    }

    public TPlan getFreezePlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.TERMINATION) && plan.getId().toLowerCase().contains("freezeplan")) {
                return plan;
            }
        }
        return null;
    }

    public TPlan getDefrostPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD) && plan.getId().toLowerCase().contains("defrostplan")) {
                return plan;
            }
        }
        return null;
    }

    public TPlan getBackupPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.MANAGEMENT) && plan.getId().toLowerCase().contains("backupmanagementplan")) {
                return plan;
            }
        }
        return null;
    }
}
