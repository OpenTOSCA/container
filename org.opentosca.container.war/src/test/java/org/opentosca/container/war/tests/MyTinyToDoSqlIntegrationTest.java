package org.opentosca.container.war.tests;

import java.io.IOException;
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

        Csar csar = TestUtils.setupCsarTestRepository(this.csarId, this.storage, TESTAPPLICATIONSREPOSITORY);
        TestUtils.generatePlans(this.csarService, csar);

        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

        TestUtils.invokePlanDeployment(this.control, csar.id(), serviceTemplate);

        assertNotNull(serviceTemplate);
        List<TPlan> plans = serviceTemplate.getPlans();
        assertNotNull(plans);

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

        TestUtils.runTerminationPlanExecution(this.planService, csar, serviceInstanceUrl, serviceTemplate, serviceTemplateInstance, terminationPlan);
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

        TestUtils.checkViaHTTPGET("http://localhost:9990", 200, "mytinytodo");
        connectToMySql("jdbc:mysql://localhost:3306/todo?useSSL=false","dbUser", "dbPassword");
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
}
