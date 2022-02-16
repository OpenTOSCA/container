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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.war.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class})
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";


    public QName csarId = new QName("http://opentosca.org/test/applications/servicetemplates", "MyTinyToDo-DockerEngine-Test_w1-wip1");

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

    @Test
    public void test() throws Exception {

        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertNotNull(serviceTemplate);
        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TPlan buildPlan = TestUtils.getBuildPlan(plans);
        TPlan scaleOutPlan = TestUtils.getScaleOutPlan(plans);
        TPlan terminationPlan = TestUtils.getTerminationPlan(plans);

        assertNotNull("BuildPlan not found", buildPlan);
        assertNotNull("ScaleOutPlan not found", scaleOutPlan);
        assertNotNull("TerminationPlan not found", terminationPlan);

        ServiceTemplateInstance serviceTemplateInstance = TestUtils.runBuildPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters());
        assertNotNull(serviceTemplateInstance);
        assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());
        this.checkStateAfterBuild(serviceTemplateInstance);

        String serviceInstanceUrl = TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString());

        TestUtils.runManagementPlanExecution(this.planService, csar, serviceTemplate, serviceTemplateInstance, scaleOutPlan, this.getScaleOurPlanInputParameters(serviceInstanceUrl));

        this.checkStateAfterScaleOut(serviceTemplateInstance);

        TestUtils.runTerminationPlanExecution(this.planService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan);
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterScaleOut(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        ServiceTemplateInstance serviceTemplateInstanceUpdated = this.instanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false);
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstanceUpdated.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstanceUpdated.getRelationshipTemplateInstances();

        assertEquals(3, nodeTemplateInstances.size());
        assertEquals(2, relationshipTemplateInstances.size());

        TestUtils.checkViaHTTPGET("http://localhost:9991", 200, "My Tiny Todolist");
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        assertEquals(2, nodeTemplateInstances.size());
        assertEquals(1, relationshipTemplateInstances.size());

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

        assertTrue(foundDockerEngine);
        assertTrue(foundTinyToDo);

        TestUtils.checkViaHTTPGET("http://localhost:9990", 200, "My Tiny Todolist");
    }

    private List<org.opentosca.container.core.extension.TParameter> getScaleOurPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9991");
        applicationPort.setRequired(true);

        org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        inputParams.add(applicationPort);
        inputParams.add(serviceInstanceUrlParam);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://" + TestUtils.getDockerHost() + ":2375");

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9990");
        applicationPort.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }
}
