package org.opentosca.container.war.tests;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.model.csar.Csar;
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
public class ConnectToIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = QName.valueOf("{http://opentosca.org/test/applications/servicetemplates}ConnectTo-Test-Application_w1");
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
    public void testDeployment() throws Exception {
        Csar csar = testUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        testUtils.generatePlans(this.planGenerationService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
        assertNotNull(serviceTemplate);

        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TPlan buildPlan = testUtils.getBuildPlan(plans);
        TPlan terminationPlan = testUtils.getTerminationPlan(plans);
        assertNotNull(buildPlan);
        assertNotNull(terminationPlan);

        testUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(2, testUtils.getDeployedPlans(this.endpointService).size());

        ServiceTemplateInstance serviceTemplateInstance = testUtils.runBuildPlanExecution(this.planInstanceService,
            this.planInvokerService, this.serviceTemplateInstanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters()
        );

        assertNotNull(serviceTemplateInstance);
        assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> connectAResponse = httpClient.sendAsync(
                HttpRequest.newBuilder(URI.create("http://localhost:9999/connectToA.txt")).build(),
                HttpResponse.BodyHandlers.ofString())
            .join();
        assertEquals(200, connectAResponse.statusCode());
        String stringA = connectAResponse.body();
        // Check number of lines, each connectTo adds one line
        assertEquals(1, stringA.split("[\n\r]").length);

        HttpResponse<String> connectBResponse = httpClient.sendAsync(
                HttpRequest.newBuilder(URI.create("http://localhost:9999/connectToB.txt")).build(),
                HttpResponse.BodyHandlers.ofString())
            .join();
        assertEquals(200, connectBResponse.statusCode());
        String stringB = connectBResponse.body();
        assertEquals(1, stringB.split("[\n\r]").length);

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

    private List<TParameter> getBuildPlanInputParameters() {
        List<TParameter> baseInputParams = testUtils.getBaseInputParams();

        TParameter dockerInDocker = new TParameter();
        dockerInDocker.setName("DockerEngineURL");
        dockerInDocker.setRequired(true);
        dockerInDocker.setType("String");
        dockerInDocker.setValue("tcp://" + testUtils.getDockerHost() + ":2375");
        baseInputParams.add(dockerInDocker);

        TParameter port = new TParameter();
        port.setName("Port");
        port.setRequired(true);
        port.setType("String");
        port.setValue("9999");
        baseInputParams.add(port);

        return baseInputParams;
    }
}
