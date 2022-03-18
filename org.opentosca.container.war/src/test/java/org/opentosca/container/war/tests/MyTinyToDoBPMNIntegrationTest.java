package org.opentosca.container.war.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.control.plan.PlanGenerationService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanInvokerService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.services.instances.PlanInstanceService;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.war.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoBPMNIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = new QName("http://opentosca.org/test/applications/servicetemplates", "MyTinyToDo-DockerEngine-BPMN-Test_w1-wip1");

    private TestUtils testUtils = new TestUtils();

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
    public InstanceService instanceService;
    @Inject
    public ICoreEndpointService endpointService;

    @Test
    public void test() throws Exception {
        Csar csar = testUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        testUtils.generatePlans(this.planGenerationService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        testUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(2, testUtils.getDeployedPlans(this.endpointService).size());

        List<TPlan> plans = serviceTemplate.getPlans();
        Assert.assertNotNull(plans);

        TPlan buildPlan = testUtils.getBuildPlan(plans);
        TPlan terminationPlan = testUtils.getTerminationPlan(plans);

        Assert.assertNotNull("BuildPlan not found", buildPlan);
        Assert.assertNotNull("TerminationPlan not found", terminationPlan);
        ServiceTemplateInstance serviceTemplateInstance = testUtils.runBuildPlanExecution(this.planInstanceService, this.planInvokerService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters());
        this.checkStateAfterBuild(serviceTemplateInstance);

        testUtils.runTerminationPlanExecution(this.planInstanceService, this.planInvokerService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan);

        testUtils.invokePlanUndeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(0, testUtils.getDeployedPlans(this.endpointService).size());
    }

    @After
    public void cleanUpContainer() {
        testUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = this.instanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false).getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = this.instanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false).getRelationshipTemplateInstances();
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

        testUtils.checkViaHTTPGET("http://localhost:9993", 200, "My Tiny Todolist");
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://" + testUtils.getDockerHost() + ":2375");

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("80->9993;");
        applicationPort.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);

        inputParams.addAll(testUtils.getBaseInputParams());

        return inputParams;
    }
}
