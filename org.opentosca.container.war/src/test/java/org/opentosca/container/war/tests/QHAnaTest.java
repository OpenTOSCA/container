package org.opentosca.container.war.tests;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(properties = "server.port=1337")
public class QHAnaTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = QName.valueOf("{https://ust-quantil.github.io/quantum/applications/servicetemplates}QHAna_w3");
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
        HttpResponse<String> uiResponse = httpClient.sendAsync(
                HttpRequest.newBuilder(URI.create("http://localhost:9999")).build(),
                HttpResponse.BodyHandlers.ofString())
            .join();
        assertEquals(200, uiResponse.statusCode());

        HttpResponse<String> backendResponse = httpClient.sendAsync(
                HttpRequest.newBuilder(URI.create("http://localhost:9998")).build(),
                HttpResponse.BodyHandlers.ofString())
            .join();
        assertEquals(200, backendResponse.statusCode());

        HttpResponse<String> pluginRunnerResponse = httpClient.sendAsync(
                HttpRequest.newBuilder(URI.create("http://localhost:9997")).build(),
                HttpResponse.BodyHandlers.ofString())
            .join();
        assertEquals(200, pluginRunnerResponse.statusCode());

        // Create experiment
        String inputJsonExperiment = "{ \"name\":\"test-name\", \"description\":\"test\"}";

        final HttpRequest requestExperiment = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9998/experiments"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(inputJsonExperiment))
            .build();

        final HttpResponse<String> experimentResponse = httpClient.sendAsync(
                requestExperiment,
                HttpResponse.BodyHandlers.ofString())
            .join();

        JSONObject experimentJson = (JSONObject) new JSONParser().parse(experimentResponse.body());
        final String experimentPath = experimentJson.get("@self").toString();

        assertEquals(200, experimentResponse.statusCode());

        // Send request to hello-world plugin
        final String inputData = "-----------------------------1294583022956651503273599773\n" +
            "Content-Disposition: form-data; name=\"inputStr\"\n" +
            "\n" +
            "test input\n" +
            "-----------------------------1294583022956651503273599773--";

        final HttpRequest pluginRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9997/plugins/hello-world%40v0-1-0/process/"))
            .header("Content-Type", "multipart/form-data; boundary=---------------------------1294583022956651503273599773")
            .POST(HttpRequest.BodyPublishers.ofString(inputData))
            .build();

        final HttpResponse<String> pluginResponse = httpClient.sendAsync(
                pluginRequest,
                HttpResponse.BodyHandlers.ofString())
            .join();

        final Optional<String> location = pluginResponse.headers().firstValue("Location");

        assertTrue(location.isPresent());

        // register plugin execution as task in the created experiment
        String inputJsonData = "{\n" +
            "\t\"inputData\": [],\n" +
            "\t\"parameters\": \"inputStr=test+input\",\n" +
            "\t\"parametersContentType\": \"application/x-www-form-urlencoded\",\n" +
            "\t\"processorLocation\": \"http://localhost:9997/plugins/hello-world%40v0-1-0/\",\n" +
            "\t\"processorName\": \"hello-world\",\n" +
            "\t\"processorVersion\": \"v0.1.0\",\n" +
            "\t\"resultLocation\": \"" + location.get() + "\"\n" +
            "}";

        final HttpRequest requestData = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9998" + experimentPath + "/timeline"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(inputJsonData))
            .build();

        final HttpResponse<String> dataResponse = httpClient.sendAsync(
                requestData,
                HttpResponse.BodyHandlers.ofString())
            .join();

        assertEquals(200, dataResponse.statusCode());

        JSONObject dataJson = (JSONObject) new JSONParser().parse(dataResponse.body());
        final String pollPath = dataJson.get("@self").toString();

        // poll result status from backend
        String status = "PENDING";
        for (int i = 0; i < 40; i++) {
            Thread.sleep(1500);
            HttpResponse<String> pollResponse = httpClient.sendAsync(
                    HttpRequest.newBuilder(URI.create("http://localhost:9998" + pollPath)).build(),
                    HttpResponse.BodyHandlers.ofString())
                .join();
            String body = pollResponse.body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(body);
            status = jsonObject.get("status").toString();
            if (!status.equals("PENDING")) {
                break;
            }
        }

        assertEquals("SUCCESS", status);

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

        TParameter ibmQToken = new TParameter();
        ibmQToken.setName("IBMQ_TOKEN");
        ibmQToken.setRequired(true);
        ibmQToken.setType("String");
        ibmQToken.setValue("token");
        baseInputParams.add(ibmQToken);

        TParameter ibmQBackend = new TParameter();
        ibmQBackend.setName("IBMQ_BACKEND_NAME");
        ibmQBackend.setRequired(true);
        ibmQBackend.setType("String");
        ibmQBackend.setValue("backend");
        baseInputParams.add(ibmQBackend);

        return baseInputParams;
    }
}
