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
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
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
public class ApacheWebAppIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = new QName("http://opentosca.org/test/applications/servicetemplates", "ApacheWebApp-Ubuntu-Docker-Test_w1-wip1");

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
    @Inject
    public ICoreEndpointService endpointService;


    @Test
    public void test() throws Exception {

        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(2, TestUtils.getDeployedPlans(this.endpointService).size());

        assertNotNull(serviceTemplate);

        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TPlan buildPlan = TestUtils.getBuildPlan(plans);
        TPlan terminationPlan = TestUtils.getTerminationPlan(plans);

        assertNotNull("BuildPlan not found", buildPlan);
        assertNotNull("TerminationPlan not found", terminationPlan);

        ServiceTemplateInstance serviceTemplateInstance = TestUtils.runBuildPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters());
        assertNotNull(serviceTemplateInstance);
        assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());
        this.checkStateAfterBuild(serviceTemplateInstance);

        TestUtils.runTerminationPlanExecution(this.planService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan);

        TestUtils.invokePlanUndeployment(this.control,csar.id(), serviceTemplate);

        assertEquals(0, TestUtils.getDeployedPlans(this.endpointService).size());
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        assertEquals(4, nodeTemplateInstances.size());
        assertEquals(3, relationshipTemplateInstances.size());

        NodeTemplateInstance dockerEngine = null;
        NodeTemplateInstance dockerContainer = null;
        NodeTemplateInstance apacheApp = null;
        NodeTemplateInstance apacheWebServer = null;
        for (NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
            if (nodeTemplateInstance.getTemplateId().contains("DockerEngine")) {
                dockerEngine = nodeTemplateInstance;
            }
            if (nodeTemplateInstance.getTemplateId().contains("DockerContainer")) {
                dockerContainer = nodeTemplateInstance;
            }
            if (nodeTemplateInstance.getTemplateId().contains("ApacheApp")) {
                apacheApp = nodeTemplateInstance;
            }
            if (nodeTemplateInstance.getTemplateId().contains("ApacheWebServer")) {
                apacheWebServer = nodeTemplateInstance;
            }
        }

        assertNotNull(dockerContainer);
        assertNotNull(dockerEngine);
        assertNotNull(apacheApp);
        assertNotNull(apacheWebServer);

        assertTrue(apacheWebServer.getPropertiesAsMap().containsKey("Port"));
        assertTrue(dockerContainer.getPropertiesAsMap().containsKey("ContainerIP"));
        assertTrue(apacheApp.getPropertiesAsMap().containsKey("URL"));
        assertTrue(apacheApp.getPropertiesAsMap().get("URL").contains(dockerContainer.getPropertiesAsMap().get("ContainerIP") + ":" + apacheWebServer.getPropertiesAsMap().get("Port")));

        TestUtils.checkViaHTTPGET("http://localhost", 200, "Uwe");
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://" + TestUtils.getDockerHost() + ":2375");

        inputParams.add(dockerEngineUrl);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }
}
