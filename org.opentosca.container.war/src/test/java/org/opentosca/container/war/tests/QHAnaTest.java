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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.war.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class})
@TestPropertySource(properties = "server.port=1337")
public class QHAnaTest {

    public static final String QcApplicationsRepository = "https://github.com/UST-QuAntiL/tosca-definitions-qc-applications";

    public QName csarId = QName.valueOf("{https://ust-quantil.github.io/quantum/applications/servicetemplates}QHAna_w1");

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
    public void testDeployment() throws Exception {
        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage,
            QcApplicationsRepository);
        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
        assertNotNull(serviceTemplate);

        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TPlan buildPlan = plans.stream()
            .filter(tPlan -> tPlan.getPlanType().equals(PlanType.BUILD.toString()))
            .filter(tPlan -> !tPlan.getId().toLowerCase().contains("defrost"))
            .findFirst()
            .orElse(null);
        assertNotNull(buildPlan);

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        ServiceTemplateInstance serviceTemplateInstance = TestUtils.runBuildPlanExecution(
            this.planService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters()
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
    }

    private List<TParameter> getBuildPlanInputParameters() {
        List<TParameter> baseInputParams = TestUtils.getBaseInputParams();

        TParameter dockerInDocker = new TParameter();
        dockerInDocker.setName("DockerEngineURL");
        dockerInDocker.setRequired(true);
        dockerInDocker.setType("String");
        dockerInDocker.setValue("tcp://172.17.0.1:2375");
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
