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

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.war.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class})
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoIntegrationTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MyTinyToDoIntegrationTest.class);

    public IRepository repository;
    public Path repositoryPath;
    public Git git;
    public Csar csar;

    @Inject
    public OpenToscaControlService control;
    @Inject
    public CsarStorageService storage;
    @Inject
    public CsarService csarService;
    @Inject
    public PlanService planService;
    @Inject
    public InstanceService instanceService;

    private void checkServices() {
        Assert.assertNotNull(storage);
        Assert.assertNotNull(control);
        Assert.assertNotNull(repository);
    }

    private void generatePlans() {
        try {
            Assert.assertTrue(this.csarService.generatePlans(this.csar));
        } catch (SystemException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (UserException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test() throws InterruptedException, ExecutionException, RepositoryCorruptException, IOException, SystemException, AccountabilityException, UserException, GitAPIException {
        this.fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, new QName("http://opentosca.org/servicetemplates", "MyTinyToDo_Bare_Docker"), this.storage);
        this.checkServices();
        this.generatePlans();

        TServiceTemplate serviceTemplate = this.csar.entryServiceTemplate();

        this.control.invokePlanDeployment(this.csar.id(), serviceTemplate);

        TPlan buildPlan = null;
        TPlan scaleOutPlan = null;
        TPlan terminationPlan = null;
        List<TPlan> plans = serviceTemplate.getPlans().getPlan();

        for (TPlan plan : plans) {
            PlanType type = PlanType.fromString(plan.getPlanType());
            switch (type) {
                case BUILD:
                    buildPlan = plan;
                    break;
                case MANAGEMENT:
                    if (plan.getId().toLowerCase().contains("scale")) {
                        scaleOutPlan = plan;
                    }
                    break;
                case TERMINATION:
                    terminationPlan = plan;
                    break;
                default:
                    break;
            }
        }

        ServiceTemplateInstance serviceTemplateInstance = this.runBuildPlanExecution(serviceTemplate, buildPlan);

        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        Assert.assertTrue(nodeTemplateInstances.size() == 2);
        Assert.assertTrue(relationshipTemplateInstances.size() == 1);

        boolean foundDockerEngine = false;
        boolean foundTinyToDo = false;
        for (NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
            if (nodeTemplateInstance.getTemplateId().contains("DockerEngine")) {
                foundDockerEngine = true;
            }
            if (nodeTemplateInstance.getTemplateId().contains("MyTinyToDo")) {
                foundTinyToDo = true;
            }
        }

        Assert.assertTrue(foundDockerEngine);
        Assert.assertTrue(foundTinyToDo);

        String serviceInstanceUrl = this.createServiceInstanceUrl(this.csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString());

        this.runScaleOutPlanExecution(serviceInstanceUrl, serviceTemplate, serviceTemplateInstance, scaleOutPlan);

        serviceTemplateInstance = this.instanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false);
        nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        Assert.assertTrue(nodeTemplateInstances.size() == 3);
        Assert.assertTrue(relationshipTemplateInstances.size() == 2);

        this.runTerminationPlanExecution(serviceInstanceUrl, serviceTemplate, serviceTemplateInstance, terminationPlan);

        this.control.deleteCsar(this.csar.id());
    }

    private ServiceTemplateInstance runBuildPlanExecution(TServiceTemplate serviceTemplate, TPlan buildPlan) {
        List<org.opentosca.container.core.tosca.extension.TParameter> buildPlanInputParams = this.getBuildPlanInputParameters();
        String buildPlanCorrelationId = this.planService.invokePlan(this.csar, serviceTemplate, -1L, buildPlan.getId(), buildPlanInputParams, PlanType.BUILD);
        PlanInstance buildPlanInstance = this.planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
        while (buildPlanInstance == null) {
            buildPlanInstance = this.planService.getPlanInstanceByCorrelationId(buildPlanCorrelationId);
        }

        PlanInstanceState buildPlanInstanceState = buildPlanInstance.getState();
        while (!buildPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            buildPlanInstance = this.planService.getPlanInstance(buildPlanInstance.getId());
            buildPlanInstanceState = buildPlanInstance.getState();
        }

        ServiceTemplateInstance serviceTemplateInstance = this.instanceService.getServiceTemplateInstance(buildPlanInstance.getServiceTemplateInstance().getId(), false);
        return serviceTemplateInstance;
    }

    private void runScaleOutPlanExecution(String serviceInstanceUrl, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan scaleOutPlan) {
        List<org.opentosca.container.core.tosca.extension.TParameter> scaleOutInputParams = this.getScaleOurPlanInputParameters(serviceInstanceUrl);
        String scaleOurPlanCorrelationId = this.planService.invokePlan(this.csar, serviceTemplate, serviceTemplateInstance.getId(), scaleOutPlan.getId(), scaleOutInputParams, PlanType.MANAGEMENT);
        PlanInstance scaleOutPlanInstance = this.planService.getPlanInstanceByCorrelationId(scaleOurPlanCorrelationId);
        while (scaleOutPlanInstance == null) {
            scaleOutPlanInstance = this.planService.getPlanInstanceByCorrelationId(scaleOurPlanCorrelationId);
        }

        PlanInstanceState scaleOutPlanInstanceState = scaleOutPlanInstance.getState();
        while (!scaleOutPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            scaleOutPlanInstance = this.planService.getPlanInstance(scaleOutPlanInstance.getId());
            scaleOutPlanInstanceState = scaleOutPlanInstance.getState();
        }
    }

    private void runTerminationPlanExecution(String serviceInstanceUrl, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan terminationPlan) {
        List<org.opentosca.container.core.tosca.extension.TParameter> terminationOutInputParams = this.getTerminationPlanInputParameters(serviceInstanceUrl);
        String terminationPlanCorrelationId = this.planService.invokePlan(this.csar, serviceTemplate, serviceTemplateInstance.getId(), terminationPlan.getId(), terminationOutInputParams, PlanType.TERMINATION);
        PlanInstance terminationPlanInstance = this.planService.getPlanInstanceByCorrelationId(terminationPlanCorrelationId);
        while (terminationPlanInstance == null) {
            terminationPlanInstance = this.planService.getPlanInstanceByCorrelationId(terminationPlanCorrelationId);
        }

        PlanInstanceState terminationPlanInstanceState = terminationPlanInstance.getState();
        while (!terminationPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            terminationPlanInstance = this.planService.getPlanInstance(terminationPlanInstance.getId());
            terminationPlanInstanceState = terminationPlanInstance.getState();
        }
    }

    private String createServiceInstanceUrl(String csarId, String serviceTemplateId, String serviceInstanceId) {
        return Settings.CONTAINER_INSTANCEDATA_API.replace("{csarid}", csarId).replace("{servicetemplateid}", serviceTemplateId) + "/" + serviceInstanceId;
    }

    @After
    public void clearContainer() {
        this.storage.findAll().forEach(x -> this.control.deleteCsar(x.id()));
    }

    private List<org.opentosca.container.core.tosca.extension.TParameter> getTerminationPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = this.getBaseInputParams();

        org.opentosca.container.core.tosca.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.tosca.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        inputParams.add(serviceInstanceUrlParam);
        return inputParams;
    }

    private List<org.opentosca.container.core.tosca.extension.TParameter> getScaleOurPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.tosca.extension.TParameter applicationPort = new org.opentosca.container.core.tosca.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9991");
        applicationPort.setRequired(true);

        org.opentosca.container.core.tosca.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.tosca.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        inputParams.add(applicationPort);
        inputParams.add(serviceInstanceUrlParam);

        inputParams.addAll(this.getBaseInputParams());

        return inputParams;
    }

    private List<org.opentosca.container.core.tosca.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.tosca.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.tosca.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://172.17.0.1:2375");

        org.opentosca.container.core.tosca.extension.TParameter applicationPort = new org.opentosca.container.core.tosca.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9990");
        applicationPort.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);

        inputParams.addAll(this.getBaseInputParams());

        return inputParams;
    }

    public List<org.opentosca.container.core.tosca.extension.TParameter> getBaseInputParams() {
        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.tosca.extension.TParameter instanceDataAPIUrl = new org.opentosca.container.core.tosca.extension.TParameter();
        instanceDataAPIUrl.setName("instanceDataAPIUrl");
        instanceDataAPIUrl.setType("String");
        instanceDataAPIUrl.setValue(null);
        instanceDataAPIUrl.setRequired(true);

        org.opentosca.container.core.tosca.extension.TParameter csarEntrypoint = new org.opentosca.container.core.tosca.extension.TParameter();
        csarEntrypoint.setName("csarEntrypoint");
        csarEntrypoint.setType("String");
        csarEntrypoint.setValue(null);
        csarEntrypoint.setRequired(true);

        org.opentosca.container.core.tosca.extension.TParameter correlationId = new org.opentosca.container.core.tosca.extension.TParameter();
        correlationId.setName("CorrelationID");
        correlationId.setType("String");
        correlationId.setValue(null);
        correlationId.setRequired(true);

        inputParams.add(instanceDataAPIUrl);
        inputParams.add(csarEntrypoint);
        inputParams.add(correlationId);

        return inputParams;
    }

    protected void fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider provider, QName serviceTemplateId, CsarStorageService storage) throws IOException, SystemException, UserException, InterruptedException, ExecutionException, AccountabilityException, RepositoryCorruptException, GitAPIException {
        this.repositoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("tosca-definitions-public");
        String remoteUrl = "https://github.com/OpenTOSCA/tosca-definitions-public";

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
            this.git.fetch().call();
        }

        // inject the current path to the repository factory
        FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = new FileBasedRepositoryConfiguration(repositoryPath, provider);
        // force xml repository provider
        fileBasedRepositoryConfiguration.setRepositoryProvider(provider);

        this.repository = RepositoryFactory.getRepository(repositoryPath);

        LOGGER.debug("Initialized test repository");

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
