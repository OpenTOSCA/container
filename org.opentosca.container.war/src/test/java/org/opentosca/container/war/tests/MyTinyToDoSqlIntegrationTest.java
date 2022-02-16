package org.opentosca.container.war.tests;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
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
import org.opentosca.container.war.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {Application.class})
@TestPropertySource(properties = "server.port=1337")
public class MyTinyToDoSqlIntegrationTest {

    public static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-test-applications";

    public QName csarId = new QName("http://opentosca.org/example/applications/servicetemplates", "MyTinyToDo-MySql_Docker-w1");

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
        String wineryRepositoryUrlForDockerContainer = "http://" + TestUtils.getDockerHost() + ":8091/winery";
        String wineryRepositoryUrl = "http://localhost:8091/winery";

        // download csar from winery
        Path csarPath = TestUtils.exportCsarFromRepository(TestUtils.fetchRepository(TESTAPPLICATIONSREPOSITORY), csarId);
        // enrich csar
        TestUtils.enrichCsarFile(csarPath, wineryRepositoryUrl);
        Csar csar = this.storage.findById(this.storage.storeCSAR(csarPath));

        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
        assertNotNull(serviceTemplate);
        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);
        TestUtils.uploadCsarToWineryRepository(new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()), wineryRepositoryUrlForDockerContainer, TESTAPPLICATIONSREPOSITORY);

        Collection<QName> serviceTemplateIdsAtWineryRepository = TestUtils.getServiceTemplateIdsFromWineryRepository(wineryRepositoryUrl);
        //assertEquals(1, serviceTemplateIdsAtWineryRepository.size());
        QName serviceTemplateId = new QName(csarId.getNamespaceURI(), csarId.getLocalPart());

        TPlan buildPlan = TestUtils.getBuildPlan(plans);
        TPlan terminationPlan = TestUtils.getTerminationPlan(plans);
        TPlan freezePlan = TestUtils.getFreezePlan(plans);
        TPlan defrostPlan = TestUtils.getDefrostPlan(plans);
        TPlan backupPlan = TestUtils.getBackupPlan(plans);

        assertNotNull("BuildPlan not found", buildPlan);
        assertNotNull("TerminationPlan not found", terminationPlan);
        assertNotNull("FreezePlan not found", freezePlan);
        assertNotNull("DefrostPlan not found", defrostPlan);
        assertNotNull("BackupPlan not found", backupPlan);

        ServiceTemplateInstance serviceTemplateInstance = TestUtils.runBuildPlanExecution(this.planService, this.instanceService, csar, serviceTemplate, buildPlan, this.getBuildPlanInputParameters());
        assertNotNull(serviceTemplateInstance);
        assertEquals(ServiceTemplateInstanceState.CREATED, serviceTemplateInstance.getState());
        this.checkStateAfterBuild(serviceTemplateInstance);
        String serviceInstanceUrl = TestUtils.createServiceInstanceUrl(csar.id().csarName(), serviceTemplate.getId(), serviceTemplateInstance.getId().toString());

        // lets test the backup plan

        TestUtils.runBackupPlanExecution(this.planService,csar,serviceTemplate,serviceTemplateInstance,backupPlan, this.getBackupPlanInputParameters(wineryRepositoryUrlForDockerContainer, serviceInstanceUrl));

        serviceTemplateIdsAtWineryRepository = TestUtils.getServiceTemplateIdsFromWineryRepository(wineryRepositoryUrl);
        serviceTemplateIdsAtWineryRepository = serviceTemplateIdsAtWineryRepository.stream().filter(x -> x.getLocalPart().toLowerCase().contains("stateful")).collect(Collectors.toList());
        assertEquals(1, serviceTemplateIdsAtWineryRepository.size());
        QName backupServiceTemplateId = serviceTemplateIdsAtWineryRepository.iterator().next();
        assertNotNull(backupServiceTemplateId);

        // testfreeze
        TestUtils.runFreezePlanExecution(this.planService, csar, serviceTemplate, serviceTemplateInstance, freezePlan, wineryRepositoryUrlForDockerContainer);
        serviceTemplateIdsAtWineryRepository = TestUtils.getServiceTemplateIdsFromWineryRepository(wineryRepositoryUrl);
        //assertEquals(3, serviceTemplateIdsAtWineryRepository.size());
        QName freezeServiceTemplateId = serviceTemplateIdsAtWineryRepository.stream().filter(x -> !x.equals(serviceTemplateId) && !x.equals(backupServiceTemplateId)).findFirst().orElse(null);
        assertNotNull(freezeServiceTemplateId);

        serviceTemplateInstance = this.instanceService.getServiceTemplateInstance(serviceTemplateInstance.getId(), false);
        assertEquals(ServiceTemplateInstanceState.DELETED, serviceTemplateInstance.getState());

        /*
          Checking of defrosting
         */
        Path statefulCsarPath = TestUtils.downloadServiceTemplateFromWinery(freezeServiceTemplateId, wineryRepositoryUrl + "/");
        Csar statefulCsar = this.storage.findById(this.storage.storeCSAR(statefulCsarPath));

        TestUtils.generatePlans(this.csarService, statefulCsar);

        TServiceTemplate statefulCsarServiceTemplate = statefulCsar.entryServiceTemplate();
        assertNotNull(statefulCsarServiceTemplate);
        List<TPlan> statefulCsarServiceTemplatePlans = statefulCsarServiceTemplate.getPlans();
        assertNotNull(statefulCsarServiceTemplatePlans);

        TestUtils.invokePlanDeployment(this.control, statefulCsar.id(), statefulCsarServiceTemplate);

        TPlan statefulCsarDefrostPlan = TestUtils.getDefrostPlan(statefulCsarServiceTemplatePlans);
        TPlan statefulCsarTerminationPlan = TestUtils.getTerminationPlan(statefulCsarServiceTemplatePlans);

        ServiceTemplateInstance statefulCsarServiceTemplateInstance = TestUtils.runDefrostPlanExecution(this.planService, this.instanceService, statefulCsar, statefulCsarServiceTemplate, statefulCsarDefrostPlan, this.getBuildPlanInputParameters());
        assertNotNull(statefulCsarServiceTemplateInstance);
        assertEquals(ServiceTemplateInstanceState.CREATED, statefulCsarServiceTemplateInstance.getState());

        // TODO FIXME The freeze and defrost operations are NOT working right now as the management features are not working to add the implementations for freeze or the CSAR itself is broken
        //this.checkStateAfterBuild(statefulCsarServiceTemplateInstance);

        TestUtils.runTerminationPlanExecution(this.planService, statefulCsar, statefulCsarServiceTemplate, statefulCsarServiceTemplateInstance, statefulCsarTerminationPlan);
        //TestUtils.clearWineryRepository(wineryRepositoryUrl);
    }

    @After
    public void cleanUpContainer() {
        TestUtils.clearContainer(this.storage, this.control);
    }

    private void checkStateAfterBuild(ServiceTemplateInstance serviceTemplateInstance) throws IOException, SQLException, ClassNotFoundException {
        Collection<NodeTemplateInstance> nodeTemplateInstances = serviceTemplateInstance.getNodeTemplateInstances();
        Collection<RelationshipTemplateInstance> relationshipTemplateInstances = serviceTemplateInstance.getRelationshipTemplateInstances();

        assertEquals(5, nodeTemplateInstances.size());
        assertEquals(5, relationshipTemplateInstances.size());

        connectToMySql("jdbc:mysql://localhost:3306/todo?useSSL=false","dbUser", "dbPassword");
        TestUtils.checkViaHTTPGET("http://localhost:9990", 200, "mytinytodo");
    }

    private void connectToMySql(String MySQLURL, String databseUserName, String databasePassword) throws ClassNotFoundException, SQLException {
        Connection con = null;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
        assertNotNull(con);
        con.close();
    }

    private List<org.opentosca.container.core.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.extension.TParameter();
        dockerEngineUrl.setName("dockerUrl");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://" + TestUtils.getDockerHost() + ":2375");

        org.opentosca.container.core.extension.TParameter applicationPort = new org.opentosca.container.core.extension.TParameter();
        applicationPort.setName("UI_Port");
        applicationPort.setType("String");
        applicationPort.setValue("9990");
        applicationPort.setRequired(true);

        org.opentosca.container.core.extension.TParameter dbmsPassword = new org.opentosca.container.core.extension.TParameter();
        dbmsPassword.setName("DBMSPassword");
        dbmsPassword.setType("String");
        dbmsPassword.setValue("dbmsPassword");
        dbmsPassword.setRequired(true);

        org.opentosca.container.core.extension.TParameter dbPassword = new org.opentosca.container.core.extension.TParameter();
        dbPassword.setName("DBPassword");
        dbPassword.setType("String");
        dbPassword.setValue("dbPassword");
        dbPassword.setRequired(true);

        org.opentosca.container.core.extension.TParameter dbUser = new org.opentosca.container.core.extension.TParameter();
        dbUser.setName("DBUser");
        dbUser.setType("String");
        dbUser.setValue("dbUser");
        dbUser.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);
        inputParams.add(dbmsPassword);
        inputParams.add(dbPassword);
        inputParams.add(dbUser);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }

    private List<org.opentosca.container.core.extension.TParameter> getBackupPlanInputParameters(String wineryEndpoint, String serviceInstanceUrl) {
        List<org.opentosca.container.core.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.extension.TParameter storeStateEndpoint = new org.opentosca.container.core.extension.TParameter();
        storeStateEndpoint.setName("StoreStateServiceEndpoint");
        storeStateEndpoint.setRequired(true);
        storeStateEndpoint.setType("String");
        storeStateEndpoint.setValue(wineryEndpoint);

        org.opentosca.container.core.extension.TParameter serviceInstanceUrlParam = new org.opentosca.container.core.extension.TParameter();
        serviceInstanceUrlParam.setName("OpenTOSCAContainerAPIServiceInstanceURL");
        serviceInstanceUrlParam.setType("String");
        serviceInstanceUrlParam.setValue(serviceInstanceUrl);
        serviceInstanceUrlParam.setRequired(true);

        inputParams.add(storeStateEndpoint);
        inputParams.add(serviceInstanceUrlParam);

        inputParams.addAll(TestUtils.getBaseInputParams());

        return inputParams;
    }
}
