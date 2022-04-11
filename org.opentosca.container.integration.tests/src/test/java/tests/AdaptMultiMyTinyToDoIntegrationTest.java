package tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.PlanInvokerService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.control.plan.PlanGenerationService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.services.instances.NodeTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.PlanInstanceService;
import org.opentosca.container.core.next.services.instances.RelationshipTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.ServiceTemplateInstanceService;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.war.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(properties = "server.port=1337")
public class AdaptMultiMyTinyToDoIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = new QName("http://opentosca.org/test/applications/servicetemplates", "MultiMyTinyToDo-DockerEngine-Test_w1-wip1");
    @Inject
    public OpenToscaControlService control;
    @Inject
    public CsarStorageService storage;
    @Inject
    public PlanGenerationService planGenerationService;
    @Inject
    public PlanInstanceService planInstanceService;
    @Inject
    public PlanInvokerService planInvokerService;
    @Inject
    public ServiceTemplateInstanceService serviceTemplateInstanceService;
    @Inject
    public RelationshipTemplateInstanceService relationshipTemplateInstanceService;
    @Inject
    public NodeTemplateInstanceService nodeTemplateInstanceService;
    @Inject
    public ICoreEndpointService endpointService;
    private TestUtils testUtils = new TestUtils();

    @Test
    public void test() throws Exception {
        Csar csar = testUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        testUtils.generatePlans(this.planGenerationService, csar);
        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
        assertNotNull(serviceTemplate);

        Collection<String> targetNodeTemplateIds = new ArrayList<>();
        targetNodeTemplateIds.add("MyTinyToDoDockerContainer_w1_0");
        targetNodeTemplateIds.add("MyTinyToDoDockerContainer_w1_1");
        targetNodeTemplateIds.add("MyTinyToDoDockerContainer_w1_2");
        targetNodeTemplateIds.add("DockerEngine_w1_0");
        Collection<String> targetRelationshipTemplateIds = new ArrayList<>();
        targetRelationshipTemplateIds.add("con_HostedOn_0");
        targetRelationshipTemplateIds.add("con_HostedOn_1");
        targetRelationshipTemplateIds.add("con_HostedOn_2");
        String buildPlanId = this.planGenerationService.generateAdaptationPlan(csar,
            new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()),
            new ArrayList<>(), new ArrayList<>(), targetNodeTemplateIds, targetRelationshipTemplateIds).planId;
        String terminationPlanId = this.planGenerationService.generateAdaptationPlan(csar,
            new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()), targetNodeTemplateIds,
            targetRelationshipTemplateIds, new ArrayList<>(), new ArrayList<>()).planId;

        serviceTemplate = this.storage.findById(csar.id()).entryServiceTemplate();
        testUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(5, testUtils.getDeployedPlans(this.endpointService).size());

        TPlan buildPlan = null;
        TPlan terminationPlan = null;
        assertNotNull(serviceTemplate);
        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        for (TPlan plan : plans) {
            if (plan.getId().equals(buildPlanId)) {
                buildPlan = plan;
            }
            if (plan.getId().equals(terminationPlanId)) {
                terminationPlan = plan;
            }
        }
        assertNotNull("BuildPlan not found", buildPlan);
        assertNotNull("TerminationPlan not found", terminationPlan);

        ServiceTemplateInstance serviceTemplateInstance = this.serviceTemplateInstanceService.createServiceTemplateInstance(csar.id().csarName(), serviceTemplate.getId());
        assertNotNull(serviceTemplateInstance);

        serviceTemplateInstance = testUtils.runAdaptationPlanExecution(this.planInstanceService, this.planInvokerService, this.serviceTemplateInstanceService, csar, serviceTemplate, serviceTemplateInstance, buildPlan, this.getBuildPlanInputParameters(testUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString())));
        assertNotNull(serviceTemplateInstance);
        this.checkStateAfterBuild(serviceTemplateInstance);

        serviceTemplateInstance = testUtils.runAdaptationPlanExecution(this.planInstanceService, this.planInvokerService, this.serviceTemplateInstanceService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan, testUtils.getTerminationPlanInputParameters(testUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString())));
        assertNotNull(serviceTemplateInstance);

        testUtils.invokePlanUndeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(0, testUtils.getDeployedPlans(this.endpointService).size());
    }

    @After
    public void cleanUpContainer() {
        testUtils.clearContainer(this.storage, this.control, this.planInstanceService,
            this.relationshipTemplateInstanceService, this.nodeTemplateInstanceService,
            this.serviceTemplateInstanceService);
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());
        assertEquals(4, nodeTemplateInstances.size());
        assertEquals(3, relationshipTemplateInstances.size());

        int foundDockerEngine = 0;
        int foundTinyToDo = 0;
        for (NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
            if (nodeTemplateInstance.getTemplateId().contains("DockerEngine")) {
                foundDockerEngine++;
            }
            if (nodeTemplateInstance.getTemplateId().contains("MyTinyToDo")) {
                foundTinyToDo++;
            }
        }

        assertEquals(1, foundDockerEngine);
        assertEquals(3, foundTinyToDo);

        testUtils.checkViaHTTPGET("http://localhost:9990", 200, "My Tiny Todolist");
        testUtils.checkViaHTTPGET("http://localhost:9991", 200, "My Tiny Todolist");
        testUtils.checkViaHTTPGET("http://localhost:9992", 200, "My Tiny Todolist");
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://" + testUtils.getDockerHost() + ":2375");

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9990");
        applicationPort.setRequired(true);

        org.opentosca.container.core.extension.TParameter applicationPort2 = new org.opentosca.container.core.extension.TParameter();
        applicationPort2.setName("ApplicationPort2");
        applicationPort2.setType("String");
        applicationPort2.setValue("9991");
        applicationPort2.setRequired(true);

        org.opentosca.container.core.extension.TParameter applicationPort3 = new org.opentosca.container.core.extension.TParameter();
        applicationPort3.setName("ApplicationPort3");
        applicationPort3.setType("String");
        applicationPort3.setValue("9992");
        applicationPort3.setRequired(true);

        org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);
        inputParams.add(applicationPort2);
        inputParams.add(applicationPort3);
        inputParams.add(serviceInstanceUrlParam);

        inputParams.addAll(testUtils.getBaseInputParams());

        return inputParams;
    }
}
