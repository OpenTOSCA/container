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
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.connector.winery.WineryConnector;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.common.Constants.DEFAULT_LOCAL_REPO_NAME;

public abstract class TestUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    public static Csar setupCsarTestRepository(QName csarId, CsarStorageService storage) throws Exception {
        return setupCsarTestRepository(csarId, storage, Settings.OPENTOSCA_TEST_REMOTE_REPOSITORY_URL);
    }

    public static Collection<QName> getServiceTemplateIdsFromWineryRepository(String wineryRepositoryUrl) {
        WineryConnector connector = new WineryConnector();
        return connector.getServiceTemplates(wineryRepositoryUrl);
    }

    public static void enrichCsarFile(Path file, String wineryLocation) {
        WineryConnector connector = new WineryConnector();
        connector.performManagementFeatureEnrichment(file.toFile(), wineryLocation);
    }

    public static void clearWineryRepository(String wineryRepositoryUrl) {
        WineryConnector connector = new WineryConnector();
        connector.clearRepository(wineryRepositoryUrl);
    }

    public static void uploadCsarToWineryRepository(QName serviceTemplateId, String wineryRepositoryUrl, String testRemoteRepositoryUrl) throws GitAPIException, IOException, AccountabilityException, RepositoryCorruptException, ExecutionException, InterruptedException, URISyntaxException {
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

        IRepository repository = fetchRepository(RepositoryConfigurationObject.RepositoryProvider.FILE,  repositoryPath, remoteUrl);

        CsarExporter exporter = new CsarExporter(repository);
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");

        Map<String, Object> exportConfiguration = new HashMap<>();
        exporter.writeCsar(new ServiceTemplateId(serviceTemplateId), Files.newOutputStream(csarFilePath), exportConfiguration);

        WineryConnector connector = new WineryConnector();
        connector.uploadCSAR(csarFilePath.toFile(),true, wineryRepositoryUrl);
    }

    public static Csar setupCsarTestRepository(QName csarId, CsarStorageService storage, String testRemoteRepositoryUrl) throws Exception {
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

        return TestUtils.loadCSARFromRepositoryIntoStorage(RepositoryConfigurationObject.RepositoryProvider.FILE, csarId, storage, repositoryPath, remoteUrl);
    }

    private static Path getRepositoryPath(String testRemoteRepositoryUrl) {
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

    public static Csar loadCSARFromRepositoryIntoStorage(RepositoryConfigurationObject.RepositoryProvider provider, QName serviceTemplateId,
                                                         CsarStorageService storage, Path repositoryInputPath, String remoteUrl)
        throws Exception {
        IRepository repository = fetchRepository(provider, repositoryInputPath, remoteUrl);
        LOGGER.debug("Initialized test repository");

        Path csarFilePath = exportCsarFromRepository(repository, serviceTemplateId);

        return storeCsarFileIntoStorage(serviceTemplateId, storage, csarFilePath);
    }

    public static Path exportCsarFromRepository(IRepository repository, QName serviceTemplateId) throws IOException, AccountabilityException, RepositoryCorruptException, ExecutionException, InterruptedException {
        CsarExporter exporter = new CsarExporter(repository);
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");

        Map<String, Object> exportConfiguration = new HashMap<>();
        exporter.writeCsar(new ServiceTemplateId(serviceTemplateId), Files.newOutputStream(csarFilePath), exportConfiguration);
        return csarFilePath;
    }

    public static Csar storeCsarFileIntoStorage(QName serviceTemplateId, CsarStorageService storage, Path csarFilePath) throws SystemException, UserException {
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

    public static IRepository fetchRepository(String testRemoteRepositoryUrl) throws GitAPIException, IOException {
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


    public static IRepository fetchRepository(Path repositoryInputPath, String remoteUrl) throws GitAPIException, IOException {
        return fetchRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, repositoryInputPath, remoteUrl);
    }

    private static IRepository fetchRepository(RepositoryConfigurationObject.RepositoryProvider provider, Path repositoryInputPath, String remoteUrl) throws GitAPIException, IOException {
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

    private static void cloneRepo(Path repositoryPath, String remoteUrl) throws IOException, GitAPIException {
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

    public static void generatePlans(CsarService csarService, Csar csar) {
        try {
            Assert.assertTrue(csarService.generatePlans(csar));
        } catch (SystemException | UserException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public static void invokePlanDeployment(OpenToscaControlService control, CsarId csarId, TServiceTemplate serviceTemplate) {
        control.invokePlanDeployment(csarId, serviceTemplate);
    }

    public static void runTerminationPlanExecution(PlanService planService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan terminationPlan) {
        List<org.opentosca.container.core.extension.TParameter> terminationOutInputParams = TestUtils.getTerminationPlanInputParameters(TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString()));
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

    public static void runFreezePlanExecution(PlanService planService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan terminationPlan, String wineryRepositoryUrl) {
        List<org.opentosca.container.core.extension.TParameter> terminationOutInputParams = TestUtils.getFreezePlanInputParameters(TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString()), wineryRepositoryUrl);
        String freezePlanCorrelationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), terminationPlan.getId(), terminationOutInputParams, PlanType.TERMINATION);
        PlanInstance freezePlanInstance = planService.getPlanInstanceByCorrelationId(freezePlanCorrelationId);
        while (freezePlanInstance == null) {
            freezePlanInstance = planService.getPlanInstanceByCorrelationId(freezePlanCorrelationId);
        }

        PlanInstanceState terminationPlanInstanceState = freezePlanInstance.getState();
        while (!terminationPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            freezePlanInstance = planService.getPlanInstance(freezePlanInstance.getId());
            terminationPlanInstanceState = freezePlanInstance.getState();
        }
    }

    public static List<org.opentosca.container.core.extension.TParameter> getFreezePlanInputParameters(String serviceInstanceUrl, String wineryUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = TestUtils.getBaseInputParams();

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

    public static List<org.opentosca.container.core.extension.TParameter> getTerminationPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = TestUtils.getBaseInputParams();

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

    public static String getDockerHost() {
        String os = SystemUtils.OS_NAME;
        if (os.toLowerCase().contains("windows")) {
            return "host.docker.internal";
        } else {
            return "172.17.0.1";
        }
    }

    public static ServiceTemplateInstance runAdaptationPlanExecution(PlanService planService, InstanceService instanceService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan adaptPlan, List<org.opentosca.container.core.extension.TParameter> adaptPlanInputParams) {
        String buildPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), adaptPlan.getId(), adaptPlanInputParams, PlanType.TRANSFORMATION);
        PlanInstance buildPlanInstance = planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
        while (buildPlanInstance == null) {
            buildPlanInstance = planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
        }

        PlanInstanceState buildPlanInstanceState = buildPlanInstance.getState();
        while (!buildPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            buildPlanInstance = planService.getPlanInstance(buildPlanInstance.getId());
            buildPlanInstanceState = buildPlanInstance.getState();
        }

        return instanceService.getServiceTemplateInstance(buildPlanInstance.getServiceTemplateInstance().getId(), false);
    }

    public static Path downloadServiceTemplateFromWinery(QName serviceTemplateId, String wineryRepository) throws IOException {
        WineryConnector connector = new WineryConnector();
        Path csarFilePath = Files.createTempDirectory(serviceTemplateId.getLocalPart() + "_Test").resolve(serviceTemplateId.getLocalPart() + ".csar");
        connector.downloadServiceTemplate(csarFilePath,serviceTemplateId, wineryRepository);
        return csarFilePath;
    }

    public static ServiceTemplateInstance runBuildPlanExecution(PlanService planService, InstanceService instanceService, Csar csar, TServiceTemplate serviceTemplate, TPlan buildPlan, List<org.opentosca.container.core.extension.TParameter> buildPlanInputParams) {
        String buildPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, -1L, buildPlan.getId(), buildPlanInputParams, PlanType.BUILD);
        if (buildPlan.getPlanLanguage().contains("BPMN")) {
            Collection<ServiceTemplateInstance> coll = instanceService.getServiceTemplateInstances(serviceTemplate.getId());
            ServiceTemplateInstance s = new ServiceTemplateInstance();
            while (coll.size() != 1) {
                coll = instanceService.getServiceTemplateInstances(serviceTemplate.getId());
            }

            for (ServiceTemplateInstance serviceTemplateInstance: coll) {
                s = serviceTemplateInstance;
            }
            ServiceTemplateInstanceState state = instanceService.getServiceTemplateInstanceState(s.getId());

            while ((state != ServiceTemplateInstanceState.CREATED)) {
                state = instanceService.getServiceTemplateInstanceState(s.getId());
            }
            return s;
        }

        PlanInstance buildPlanInstance = planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
        while (buildPlanInstance == null) {
            buildPlanInstance = planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
        }

        PlanInstanceState buildPlanInstanceState = buildPlanInstance.getState();
        while (!buildPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            buildPlanInstance = planService.getPlanInstance(buildPlanInstance.getId());
            buildPlanInstanceState = buildPlanInstance.getState();
        }

        return instanceService.getServiceTemplateInstance(buildPlanInstance.getServiceTemplateInstance().getId(), false);
    }

    public static ServiceTemplateInstance runDefrostPlanExecution(PlanService planService, InstanceService instanceService, Csar csar, TServiceTemplate serviceTemplate, TPlan defrostPlan, List<org.opentosca.container.core.extension.TParameter> buildPlanInputParams) {
        String defrostPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, -1L, defrostPlan.getId(), buildPlanInputParams, PlanType.BUILD);
        if (defrostPlan.getPlanLanguage().contains("BPMN")) {
            Collection<ServiceTemplateInstance> coll = instanceService.getServiceTemplateInstances(serviceTemplate.getId());
            ServiceTemplateInstance s = new ServiceTemplateInstance();
            while (coll.size() != 1) {
                coll = instanceService.getServiceTemplateInstances(serviceTemplate.getId());
            }

            for (ServiceTemplateInstance serviceTemplateInstance: coll) {
                s = serviceTemplateInstance;
            }
            ServiceTemplateInstanceState state = instanceService.getServiceTemplateInstanceState(s.getId());

            while ((state != ServiceTemplateInstanceState.CREATED)) {
                state = instanceService.getServiceTemplateInstanceState(s.getId());
            }
            return s;
        }

        PlanInstance defrostPlanInstance = planService.getPlanInstanceByCorrelationId(defrostPlanCorrelationId);
        while (defrostPlanInstance == null) {
            defrostPlanInstance = planService.getPlanInstanceByCorrelationId(defrostPlanCorrelationId);
        }

        PlanInstanceState buildPlanInstanceState = defrostPlanInstance.getState();
        while (!buildPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            defrostPlanInstance = planService.getPlanInstance(defrostPlanInstance.getId());
            buildPlanInstanceState = defrostPlanInstance.getState();
        }

        return instanceService.getServiceTemplateInstance(defrostPlanInstance.getServiceTemplateInstance().getId(), false);
    }

    public static void runManagementPlanExecution(PlanService planService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan scaleOutPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {
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

    public static void runBackupPlanExecution(PlanService planService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan backupPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {
        String scaleOurPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), backupPlan.getId(), inputParams, PlanType.MANAGEMENT);
        PlanInstance backupPlanInstance = planService.getPlanInstanceByCorrelationId(scaleOurPlanCorrelationId);
        while (backupPlanInstance == null) {
            backupPlanInstance = planService.getPlanInstanceByCorrelationId(scaleOurPlanCorrelationId);
        }

        PlanInstanceState backupPlanInstanceState = backupPlanInstance.getState();
        while (!backupPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            backupPlanInstance = planService.getPlanInstance(backupPlanInstance.getId());
            backupPlanInstanceState = backupPlanInstance.getState();
        }
    }

    public static String createServiceInstanceUrl(String csarId, String serviceTemplateId, String serviceInstanceId) {
        return Settings.CONTAINER_INSTANCEDATA_API.replace("{csarid}", csarId).replace("{servicetemplateid}", serviceTemplateId) + "/" + serviceInstanceId;
    }

    public static void clearContainer(CsarStorageService storage, OpenToscaControlService control) {
        storage.findAll().forEach(x -> control.deleteCsar(x.id()));
    }

    public static void checkViaHTTPGET(String url, int expectedStatus, String contains) throws IOException {
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

    public static TPlan getBuildPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD) && !plan.getId().toLowerCase().contains("defrost") && plan.getId().toLowerCase().contains("buildplan")) {
                return plan;
            }
        }
        return null;
    }

    public static TPlan getTerminationPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.TERMINATION) && !plan.getId().toLowerCase().contains("freeze") && plan.getId().toLowerCase().contains("terminationplan")) {
                return plan;
            }
        }
        return null;
    }

    public static TPlan getScaleOutPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.MANAGEMENT) && plan.getId().toLowerCase().contains("scale")) {
                return plan;
            }
        }
        return null;
    }

    public static TPlan getTransformationPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.TRANSFORMATION)) {
                return plan;
            }
        }
        return null;
    }

    public static TPlan getFreezePlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.TERMINATION) && plan.getId().toLowerCase().contains("freezeplan")) {
                return plan;
            }
        }
        return null;
    }

    public static TPlan getDefrostPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD) && plan.getId().toLowerCase().contains("defrostplan")) {
                return plan;
            }
        }
        return null;
    }

    public static TPlan getBackupPlan(List<TPlan> plans) {
        for (TPlan plan : plans) {
            if (PlanType.fromString(plan.getPlanType()).equals(PlanType.MANAGEMENT) && plan.getId().toLowerCase().contains("backupmanagementplan")) {
                return plan;
            }
        }
        return null;
    }
}
