package org.opentosca.container.war.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import com.google.gson.Gson;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(properties = "server.port=1337")
public class PlanQKServiceIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/UST-QuAntiL/tosca-definitions-qc-applications";
    protected static final Logger LOGGER = LoggerFactory.getLogger(PlanQKServiceIntegrationTest.class);

    public QName csarId = new QName("https://ust-quantil.github.io/quantum/applications/servicetemplates", "PlanQK-Service-Example_w1");
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
        String planqkApiKey = System.getenv("PlanqkApiKey");
        assertNotNull("The PlanQK API key needs to be specified in the environment variable PlanqkApiKey", planqkApiKey);

        Csar csar = testUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        testUtils.generatePlans(this.planGenerationService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        testUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertEquals(2, testUtils.getDeployedPlans(this.endpointService).size());

        assertNotNull(serviceTemplate);

        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TPlan buildPlan = testUtils.getBuildPlan(plans);
        TPlan terminationPlan = testUtils.getTerminationPlan(plans);

        assertNotNull("BuildPlan not found", buildPlan);
        assertNotNull("TerminationPlan not found", terminationPlan);

        try {
            ServiceTemplateInstance serviceTemplateInstance = testUtils.runBuildPlanExecution(this.planInstanceService, this.planInvokerService, this.serviceTemplateInstanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters(planqkApiKey));
            assertNotNull(serviceTemplateInstance);
            assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());
            String serviceID = this.checkStateAfterBuild(serviceTemplateInstance, planqkApiKey);

            testUtils.runTerminationPlanExecution(this.planInstanceService, this.planInvokerService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan);

            this.checkServiceWasDeleted(serviceID, planqkApiKey);

            testUtils.invokePlanUndeployment(this.control, csar.id(), serviceTemplate);

            assertEquals(0, testUtils.getDeployedPlans(this.endpointService).size());
        } catch (Exception e) {
            try {
                deleteServiceIfStillPresent("TestService", planqkApiKey);
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            throw e;
        }
    }

    @After
    public void cleanUpContainer() {
        testUtils.clearContainer(this.storage, this.control, this.planInstanceService,
            this.relationshipTemplateInstanceService, this.nodeTemplateInstanceService,
            this.serviceTemplateInstanceService);
    }

    private String checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance, String planqkApiKey) throws InterruptedException, IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        assertEquals(2, nodeTemplateInstances.size());
        assertEquals(1, relationshipTemplateInstances.size());

        boolean foundPlatform = false;
        boolean foundService = false;
        String serviceID = "";

        for (NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
            if (nodeTemplateInstance.getTemplateId().contains("PlanQK-Platform")) {
                foundPlatform = true;
            }
            if (nodeTemplateInstance.getTemplateId().contains("PlanQK-Service")) {
                foundService = true;

                var properties = nodeTemplateInstanceService.getNodeTemplateInstanceProperties(nodeTemplateInstance.getId());
                assertTrue(properties.containsKey("ServiceID"));
                serviceID = properties.get("ServiceID");
                assertNotEquals("Service ID of the PlanQK-Service node template is empty", "", serviceID);
            }
        }

        assertTrue(foundPlatform);
        assertTrue(foundService);

        assertTrue(checkServiceCreatedSuccessfully(serviceID, planqkApiKey));

        return serviceID;
    }

    private void checkServiceWasDeleted(String serviceID, String planqkApiKey) throws IOException {
        assertEquals(404, sendServiceInfoRequest(serviceID, planqkApiKey).getResponseCode());
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters(String planqkApiKey) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter planqkApiKeyParam = new org.opentosca.container.core.extension.TParameter();
        planqkApiKeyParam.setName("PlanqkApiKey");
        planqkApiKeyParam.setRequired(true);
        planqkApiKeyParam.setType("String");

        planqkApiKeyParam.setValue(planqkApiKey);

        inputParams.add(planqkApiKeyParam);

        inputParams.addAll(testUtils.getBaseInputParams());

        return inputParams;
    }

    private static boolean checkServiceCreatedSuccessfully(String serviceID, String planqkApiKey) throws InterruptedException, IOException {
        while (true) {
            ServiceDto service = getServiceInfo(serviceID, planqkApiKey);
            assertNotNull(service);
            assertEquals(1, service.serviceDefinitions.length);
            String lifecycle = service.serviceDefinitions[0].lifecycle;

            assertTrue(lifecycle.equals("CREATED") || lifecycle.equals("CREATING"));

            if (lifecycle.equals("CREATED")) {
                return true;
            } else {
                LOGGER.info("Service is still being created.");
                Thread.sleep(10000);
            }
        }
    }

    private static void deleteServiceIfStillPresent(String serviceName, String planqkApiKey) throws IOException {
        HttpURLConnection con;
        URL location = new URL("https://platform.planqk.de/qc-catalog/services");

        con = (HttpURLConnection) location.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("accept", "application/json");
        con.setRequestProperty("X-Auth-Token", planqkApiKey);

        con.connect();

        int status = con.getResponseCode();
        assertEquals(200, status);

        String responseContent = streamToString(con.getInputStream());
        Gson gson = new Gson();

        ServiceDto[] services = gson.fromJson(responseContent, ServiceDto[].class);

        for (var service : services) {
            if (service.name.equals(serviceName)) {
                deleteService(service.id, planqkApiKey);
                LOGGER.info("service {} was deleted", serviceName);

                return;
            }
        }

        LOGGER.info("no service with name {} found", serviceName);
    }

    private static void deleteService(String serviceID, String planqkApiKey) throws IOException {
        HttpURLConnection con;
        URL location = new URL("https://platform.planqk.de/qc-catalog/services/" + serviceID);

        con = (HttpURLConnection) location.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("X-Auth-Token", planqkApiKey);
        con.connect();

        assertEquals(204, con.getResponseCode());
    }

    private static ServiceDto getServiceInfo(String serviceID, String planqkApiKey) throws IOException {
        HttpURLConnection con = sendServiceInfoRequest(serviceID, planqkApiKey);
        int status = con.getResponseCode();
        assertEquals(200, status);

        String responseContent = streamToString(con.getInputStream());
        Gson gson = new Gson();

        return gson.fromJson(responseContent, ServiceDto.class);
    }

    private static HttpURLConnection sendServiceInfoRequest(String serviceID, String planqkApiKey) throws IOException {
        HttpURLConnection con;
        URL location = new URL("https://platform.planqk.de/qc-catalog/services/" + serviceID);

        con = (HttpURLConnection) location.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("accept", "application/json");
        con.setRequestProperty("X-Auth-Token", planqkApiKey);

        con.connect();

        return con;
    }

    private static String streamToString(InputStream inputStream) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        for (String line; (line = reader.readLine()) != null; ) {
            builder.append(line).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private static class ServiceDto {
        String id;
        String name;
        ServiceDefinitionDto[] serviceDefinitions;
        String accessPermissionOfLoggedInUser;

        ServiceDto() {

        }
    }

    private static class ServiceDefinitionDto {
        String id;
        String version;
        String name;
        String context;
        String description;
        String productionEndpoint;
        String gatewayEndpoint;
        String quantumBackend;
        String type;
        String lifecycle;
        String createdAt;
        String modifiedAt;
        String createdBy;
        String modifiedBy;

        ServiceDefinitionDto() {

        }
    }
}
