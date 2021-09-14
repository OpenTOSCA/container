package org.opentosca.container.war.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
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
public class MigrateMyTinyToDo2MultiMyTinyToDoIntegrationTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MigrateMyTinyToDo2MultiMyTinyToDoIntegrationTest.class);

    public QName myTinyToDocsarId = new QName("http://opentosca.org/servicetemplates", "MyTinyToDo_Bare_Docker");
    public QName multiMyTinyToDoCsarId = new QName("http://opentosca.org/servicetemplates", "Multi_MyTinyToDo_Bare_Docker_w1-wip1");

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
        Csar myTinyToDoCsar = TestUtils.setupCsarTestRepository(this.myTinyToDocsarId, this.storage);
        Csar multiMyTinyToDoCsar = TestUtils.setupCsarTestRepository(this.multiMyTinyToDoCsarId, this.storage);
        TestUtils.generatePlans(this.csarService, myTinyToDoCsar);
        TestUtils.generatePlans(this.csarService, multiMyTinyToDoCsar);

        this.csarService.generateTransformationPlans(myTinyToDoCsar, multiMyTinyToDoCsar);

        TServiceTemplate myTinyToDoServiceTemplate = myTinyToDoCsar.entryServiceTemplate();
        TServiceTemplate multiMyTinyToDoServiceTemplate = multiMyTinyToDoCsar.entryServiceTemplate();

        TestUtils.invokePlanDeployment(this.control, myTinyToDoCsar.id(), myTinyToDoServiceTemplate);
        TestUtils.invokePlanDeployment(this.control, multiMyTinyToDoCsar.id(), multiMyTinyToDoServiceTemplate);

        TPlan myTinyToDoBuildPlan = null;
        TPlan myTinyToMultiTinyTransformationPlan = null;
        TPlan multiTinyTerminationPlan = null;

        List<TPlan> myTinyToDoPlans = myTinyToDoServiceTemplate.getPlans();
        List<TPlan> multiMyTinyToDoPlans = multiMyTinyToDoServiceTemplate.getPlans();

        for (TPlan plan : myTinyToDoPlans) {
            PlanType type = PlanType.fromString(plan.getPlanType());
            switch (type) {
                case BUILD:
                    if (!plan.getId().toLowerCase().contains("defrost")) {
                        myTinyToDoBuildPlan = plan;
                    }
                    break;
                case TRANSFORMATION:
                    myTinyToMultiTinyTransformationPlan = plan;
                    break;
                default:
                    break;
            }
        }

        for (TPlan plan : multiMyTinyToDoPlans) {
            PlanType type = PlanType.fromString(plan.getPlanType());
            switch (type) {
                case TERMINATION:
                    if (!plan.getId().toLowerCase().contains("freeze")) {
                        multiTinyTerminationPlan = plan;
                    }
                    break;
                default:
                    break;
            }
        }

        Assert.assertNotNull("BuildPlan not found", myTinyToDoBuildPlan);
        Assert.assertNotNull("TransformationPlan not found", myTinyToMultiTinyTransformationPlan);
        Assert.assertNotNull("TerminationPlan not found", multiTinyTerminationPlan);

        ServiceTemplateInstance myTinyToDoServiceTemplateInstance = TestUtils.runBuildPlanExecution(this.planService, this.instanceService, myTinyToDoCsar, myTinyToDoServiceTemplate, myTinyToDoBuildPlan, this.getMyTinyToDoBuildPlanInputParameters());
        String myTinyToDoServiceInstanceUrl = TestUtils.createServiceInstanceUrl(myTinyToDoCsar.id().csarName(), myTinyToDoServiceTemplate.getId(), myTinyToDoServiceTemplateInstance.getId().toString());
        this.checkStateAfterBuild(myTinyToDoServiceTemplateInstance);

        ServiceTemplateInstance multiInstance = this.runTransformationPlan(this.planService, this.instanceService, myTinyToDoCsar, myTinyToDoServiceTemplate, myTinyToDoServiceTemplateInstance, myTinyToMultiTinyTransformationPlan, this.getTransformationPlanInputParameters(myTinyToDoServiceInstanceUrl));
        String multMyTinyToDoServiceInstanceUrl = TestUtils.createServiceInstanceUrl(multiMyTinyToDoCsar.id().csarName(), multiMyTinyToDoServiceTemplate.getId(), multiInstance.getId().toString());
        this.checkStateAfterMigration(multiInstance);

        TestUtils.runTerminationPlanExecution(this.planService, multiMyTinyToDoCsar, multMyTinyToDoServiceInstanceUrl, multiMyTinyToDoServiceTemplate, multiInstance, multiTinyTerminationPlan);

        TestUtils.clearContainer(this.storage, this.control);
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterMigration(ServiceTemplateInstance serviceInstance) throws IOException {
        Assert.assertEquals(4, serviceInstance.getNodeTemplateInstances().size());
        Assert.assertEquals(3, serviceInstance.getRelationshipTemplateInstances().size());

        Assert.assertEquals(3, serviceInstance.getNodeTemplateInstances().stream().filter(x -> x.getTemplateType().toString().toLowerCase().contains("mytiny")).count());
        Assert.assertEquals(1, serviceInstance.getNodeTemplateInstances().stream().filter(x -> x.getTemplateType().toString().toLowerCase().contains("dockerengine")).count());
        Assert.assertEquals(3, serviceInstance.getRelationshipTemplateInstances().stream().filter(x -> x.getTemplateType().toString().toLowerCase().contains("hostedon")).count());

        TestUtils.checkViaHTTPGET("http://localhost:9991", 200, "My Tiny Todolist");
        TestUtils.checkViaHTTPGET("http://localhost:9992", 200, "My Tiny Todolist");
        TestUtils.checkViaHTTPGET("http://localhost:9994", 200, "My Tiny Todolist");
    }

    private ServiceTemplateInstance runTransformationPlan(PlanService planService, InstanceService instanceService, Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, TPlan transformationPlan, List<org.opentosca.container.core.extension.TParameter> inputParams) {
        String tranformationPlanCorrelationId = planService.invokePlan(csar, serviceTemplate, serviceTemplateInstance.getId(), transformationPlan.getId(), inputParams, PlanType.TRANSFORMATION);
        PlanInstance transformationPlanInstance = planService.getPlanInstanceByCorrelationId(tranformationPlanCorrelationId);
        while (transformationPlanInstance == null) {
            transformationPlanInstance = planService.getPlanInstanceByCorrelationId(tranformationPlanCorrelationId);
        }

        PlanInstanceState tranformationPlanInstanceState = transformationPlanInstance.getState();
        while (!tranformationPlanInstanceState.equals(PlanInstanceState.FINISHED)) {
            transformationPlanInstance = planService.getPlanInstance(transformationPlanInstance.getId());
            tranformationPlanInstanceState = transformationPlanInstance.getState();
        }

        while (transformationPlanInstance.getOutputs().isEmpty()) {
            transformationPlanInstance = planService.getPlanInstance(transformationPlanInstance.getId());
        }

        Iterator<PlanInstanceOutput> iter = transformationPlanInstance.getOutputs().iterator();
        while (iter.hasNext()) {
            PlanInstanceOutput output = iter.next();
            if (output.getName().equals("instanceId")) {
                String serviceInstanceId = output.getValue();
                return instanceService.getServiceTemplateInstance(Long.valueOf(serviceInstanceId), false);
            }
        }
        return null;
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        Assert.assertTrue(nodeTemplateInstances.size() == 2);
        Assert.assertTrue(relationshipTemplateInstances.size() == 1);

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
        Assert.assertTrue(foundTinyToDo == 1);

        TestUtils.checkViaHTTPGET("http://localhost:9990", 200, "My Tiny Todolist");
    }

    private List<org.opentosca.container.core.extension.TParameter> getTransformationPlanInputParameters(String serviceInstanceUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9994");
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

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://172.17.0.1:2375");

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);
        inputParams.add(serviceInstanceUrlParam);
        inputParams.add(applicationPort2);
        inputParams.add(applicationPort3);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }

    private List<org.opentosca.container.core.extension.TParameter> getMyTinyToDoBuildPlanInputParameters() {
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
