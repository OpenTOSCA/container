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
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.war.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class})
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoBPMNIntegrationTest {

    public QName csarId = new QName("http://opentosca.org/servicetemplates", "MyTinyToDo_Bare_Docker_BPMN");

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
        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage);
        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        TPlan buildPlan = null;
        List<TPlan> plans = serviceTemplate.getPlans();

        for (TPlan plan : plans) {
            PlanType type = PlanType.fromString(plan.getPlanType());
            switch (type) {
                case BUILD:
                    if (!plan.getId().toLowerCase().contains("defrost")) {
                        buildPlan = plan;
                    }
                    break;
                default:
                    break;
            }
        }

        Assert.assertNotNull("BuildPlan not found", buildPlan);
        ServiceTemplateInstance serviceTemplateInstance = TestUtils.runBuildPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters());
        this.checkStateAfterBuild(serviceTemplateInstance);
        TestUtils.clearContainer(this.storage, this.control);
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
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

        TestUtils.checkViaHTTPGET("http://localhost:9990", 200, "My Tiny Todolist");
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://172.17.0.1:2375");

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("80->9990;");
        applicationPort.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }
}
