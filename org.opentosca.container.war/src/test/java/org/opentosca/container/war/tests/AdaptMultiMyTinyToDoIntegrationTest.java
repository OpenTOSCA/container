package org.opentosca.container.war.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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
public class AdaptMultiMyTinyToDoIntegrationTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AdaptMultiMyTinyToDoIntegrationTest.class);

    public QName csarId = new QName("http://opentosca.org/servicetemplates", "Multi_MyTinyToDo_Bare_Docker_w1-wip1");

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
    public void test() throws InterruptedException, ExecutionException, RepositoryCorruptException, IOException, SystemException, AccountabilityException, UserException, GitAPIException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException {
        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage);
        TestUtils.generatePlans(this.csarService, csar);
        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        Collection<String> targetNodeTemplateIds = new ArrayList<String>();
        targetNodeTemplateIds.add("MyTinyToDoDockerContainer");
        targetNodeTemplateIds.add("MyTinyToDoDockerContainer_0");
        targetNodeTemplateIds.add("MyTinyToDoDockerContainer_1");
        targetNodeTemplateIds.add("DockerEngine");
        Collection<String> targetRelationshipTemplateIds = new ArrayList<String>();
        targetRelationshipTemplateIds.add("con_17");
        targetRelationshipTemplateIds.add("con_HostedOn_0");
        targetRelationshipTemplateIds.add("con_HostedOn_1");
        String buildPlanId = this.csarService.generateAdaptationPlan(csar.id(), new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()), new ArrayList<String>(), new ArrayList<String>(), targetNodeTemplateIds, targetRelationshipTemplateIds).planId;
        String terminationPlanId = this.csarService.generateAdaptationPlan(csar.id(), new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()), targetNodeTemplateIds, targetRelationshipTemplateIds, new ArrayList<String>(), new ArrayList<String>()).planId;

        serviceTemplate = this.storage.findById(csar.id()).entryServiceTemplate();
        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        TPlan buildPlan = null;
        TPlan terminationPlan = null;
        List<TPlan> plans = serviceTemplate.getPlans().getPlan();

        for (TPlan plan : plans) {
            if (plan.getId().equals(buildPlanId)) {
                buildPlan = plan;
            }
            if (plan.getId().equals(terminationPlanId)) {
                terminationPlan = plan;
            }
        }
        Assert.assertNotNull("BuildPlan not found", buildPlan);
        Assert.assertNotNull("TerminationPlan not found", terminationPlan);

        ServiceTemplateInstance serviceTemplateInstance = this.instanceService.createServiceTemplateInstance(csar.id().csarName(), serviceTemplate.getId());
        Assert.assertNotNull(serviceTemplateInstance);

        serviceTemplateInstance = TestUtils.runAdaptationPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, serviceTemplateInstance, buildPlan, this.getBuildPlanInputParameters(TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString())));
        Assert.assertNotNull(serviceTemplateInstance);
        this.checkStateAfterBuild(serviceTemplateInstance);

        serviceTemplateInstance = TestUtils.runAdaptationPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, serviceTemplateInstance, terminationPlan, TestUtils.getTerminationPlanInputParameters(TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString())));
        Assert.assertNotNull(serviceTemplateInstance);

        TestUtils.clearContainer(this.storage, this.control);
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        Assert.assertTrue(nodeTemplateInstances.size() == 4);
        Assert.assertTrue(relationshipTemplateInstances.size() == 3);

        int foundDockerEngine = 0;
        int foundTinyToDo = 0;
        for (NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
            if (nodeTemplateInstance.getTemplateId().contains("DockerEngine")) {
                foundDockerEngine++;
            }
            if (nodeTemplateInstance.getTemplateId().contains("MyTinyToDo")) {
                foundTinyToDo++;
            }
        }

        Assert.assertTrue(foundDockerEngine == 1);
        Assert.assertTrue(foundTinyToDo == 3);

        TestUtils.checkViaHTTPGET("http://localhost:9990", 200, "My Tiny Todolist");
        TestUtils.checkViaHTTPGET("http://localhost:9991", 200, "My Tiny Todolist");
        TestUtils.checkViaHTTPGET("http://localhost:9992", 200, "My Tiny Todolist");
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters(String serviceInstanceUrl) {
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

        org.opentosca.container.core.extension.TParameter applicationPort2 = new org.opentosca.container.core.extension.TParameter();
        applicationPort2.setName("ApplicationPort2");
        applicationPort2.setType("String");
        applicationPort2.setValue("9991");
        applicationPort2.setRequired(true);

        org.opentosca.container.core.extension.TParameter applicationPort3 = new org.opentosca.container.core.extension.TParameter();
        applicationPort3.setName("ApplicationPort3");
        applicationPort3.setType("String");
        applicationPort3.setValue("9992");
        applicationPort3.setRequired(true);

        org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);
        inputParams.add(applicationPort2);
        inputParams.add(applicationPort3);
        inputParams.add(serviceInstanceUrlParam);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }
}
