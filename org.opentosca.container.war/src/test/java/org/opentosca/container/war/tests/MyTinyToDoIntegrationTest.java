package org.opentosca.container.war.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.war.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class})
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoIntegrationTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MyTinyToDoIntegrationTest.class);

    public QName csarId = new QName("http://opentosca.org/servicetemplates", "MyTinyToDo_Bare_Docker");

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
    public void test() throws InterruptedException, ExecutionException, RepositoryCorruptException, IOException, SystemException, AccountabilityException, UserException, GitAPIException {
        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage);
        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        TPlan buildPlan = null;
        TPlan scaleOutPlan = null;
        TPlan terminationPlan = null;
        List<TPlan> plans = serviceTemplate.getPlans();

        for (TPlan plan : plans) {
            PlanType type = PlanType.fromString(plan.getPlanType());
            switch (type) {
                case BUILD:
                    if (!plan.getId().toLowerCase().contains("defrost")) {
                        buildPlan = plan;
                    }
                    break;
                case MANAGEMENT:
                    if (plan.getId().toLowerCase().contains("scale")) {
                        scaleOutPlan = plan;
                    }
                    break;
                case TERMINATION:
                    if (!plan.getId().toLowerCase().contains("freeze")) {
                        terminationPlan = plan;
                    }
                    break;
                default:
                    break;
            }
        }

        Assert.assertNotNull("BuildPlan not found", buildPlan);
        Assert.assertNotNull("ScaleOutPlan not found", scaleOutPlan);
        Assert.assertNotNull("TerminationPlan not found", terminationPlan);

        ServiceTemplateInstance serviceTemplateInstance = TestUtils.runBuildPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters());

        this.checkStateAfterBuild(serviceTemplateInstance);

        String serviceInstanceUrl = TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString());

        TestUtils.runManagementPlanExecution(this.planService, csar, serviceInstanceUrl, serviceTemplate, serviceTemplateInstance, scaleOutPlan, this.getScaleOurPlanInputParameters(serviceInstanceUrl));

        this.checkStateAfterScaleOut(serviceTemplateInstance);

        TestUtils.runTerminationPlanExecution(this.planService, csar, serviceInstanceUrl, serviceTemplate, serviceTemplateInstance, terminationPlan);

        TestUtils.clearContainer(this.storage, this.control);
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterScaleOut(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        ServiceTemplateInstance serviceTemplateInstanceUpdated = this.instanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false);
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstanceUpdated.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstanceUpdated.getRelationshipTemplateInstances();

        Assert.assertTrue(nodeTemplateInstances.size() == 3);
        Assert.assertTrue(relationshipTemplateInstances.size() == 2);

        TestUtils.checkViaHTTPGET("http://localhost:9991", 200, "My Tiny Todolist");
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

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
        dockerEngineUrl.setValue("tcp://172.17.0.1:2375");

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
