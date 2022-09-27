package org.opentosca.container.war.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.PlanInvokerService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.control.plan.PlanGenerationService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanLanguage;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoPlanGeneratorTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = new QName("http://opentosca.org/test/applications/servicetemplates", "MyTinyToDo-DockerEngine-Test_w1-wip1");
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

        testUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        // assertEquals(4, testUtils.getDeployedPlans(this.endpointService).size());

        assertNotNull(serviceTemplate);

        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TPlan buildPlans = testUtils.getBuildPlan(plans);
        TPlan terminationPlan = testUtils.getTerminationPlan(plans);
        //assertEquals(2, buildPlans.size());
        assertNotNull("BPMN BuildPlan not found", buildPlans);
        assertNotNull("TerminationPlan not found", terminationPlan);

        ServiceTemplateInstance serviceTemplateInstance = testUtils.runBuildPlanExecution(this.planInstanceService, this.planInvokerService, this.serviceTemplateInstanceService, csar, serviceTemplate, buildPlans, this.getBuildPlanInputParameters());
        assertNotNull(serviceTemplateInstance);
        assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());
        this.checkStateAfterBuild(serviceTemplateInstance);

        testUtils.runTerminationPlanExecution(this.planInstanceService, this.planInvokerService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan);

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
        Collection<NodeTemplateInstance> nodeTemplateInstances = this.serviceTemplateInstanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false).getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = this.serviceTemplateInstanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false).getRelationshipTemplateInstances();

        assertEquals(2, nodeTemplateInstances.size());
        assertEquals(1, relationshipTemplateInstances.size());

        boolean foundDockerEngine = false;
        boolean foundTinyToDo = false;
        for (NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
            if (nodeTemplateInstance.getTemplateId().contains("DockerEngine")) {
                foundDockerEngine = true;
                checkPropertiesOfNodeTemplate(nodeTemplateInstance);
            }
            if (nodeTemplateInstance.getTemplateId().contains("MyTinyToDo")) {
                foundTinyToDo = true;
                checkPropertiesOfNodeTemplate(nodeTemplateInstance);
            }
        }

        assertTrue(foundDockerEngine);
        assertTrue(foundTinyToDo);

        testUtils.checkViaHTTPGET("http://localhost:9990", 200, "My Tiny Todolist");
    }

    private void checkPropertiesOfNodeTemplate(NodeTemplateInstance nodeTemplateInstance) {
        List<org.opentosca.container.core.extension.TParameter> inputParameters = getBuildPlanInputParameters();
        Map<String, String> properties = nodeTemplateInstanceService.getNodeTemplateInstanceProperties(nodeTemplateInstance.getId());

        if (nodeTemplateInstance.getTemplateId().contains("DockerEngine")) {
            for (org.opentosca.container.core.extension.TParameter inputParameter : inputParameters) {
                for (String property : properties.keySet()) {
                    if (inputParameter.getName().equals(property)) {
                        String propertyValue = properties.get(property);
                        assertEquals(inputParameter.getValue(), propertyValue);
                    }
                }
            }
        }
        if (nodeTemplateInstance.getTemplateId().contains("MyTinyToDo")) {
            for (String property : properties.keySet()) {
                for (org.opentosca.container.core.extension.TParameter inputParameter : inputParameters) {
                    if (property.equals("Port") && inputParameter.getName().equals("ApplicationPort")) {
                        String propertyValue = properties.get(property);
                        assertEquals(inputParameter.getValue(), propertyValue);
                    }
                }
                if (property.equals("ContainerIP")) {
                    String propertyValue = properties.get(property);
                    assertEquals(testUtils.getDockerHost(), propertyValue);
                }
                if (property.equals("ContainerPort")) {
                    String propertyValue = properties.get(property);
                    assertNotNull(propertyValue);
                }
            }
        }
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
        applicationPort.setValue("9990");
        applicationPort.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);

        inputParams.addAll(testUtils.getBaseInputParams());

        return inputParams;
    }
}
