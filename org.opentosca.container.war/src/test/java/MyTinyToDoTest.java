import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.service.CsarStorageService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/root-context.xml", "classpath:/spring/web-context.xml"})
public class MyTinyToDoTest extends CSARTest {

    @Inject
    OpenToscaControlService control;
    @Inject
    CsarService csarService;
    @Inject
    CsarStorageService storage;
    @Inject
    PlanService planService;

    public MyTinyToDoTest() {
    }

    @Test
    public void test() throws SystemException, UserException, InterruptedException, ExecutionException, RepositoryCorruptException, AccountabilityException, IOException {

        this.fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, new QName("http://opentosca.org/servicetemplates", "MyTinyToDo_Bare_Docker"), this.storage);

        Assert.assertNotNull(storage);
        Assert.assertNotNull(control);
        Assert.assertNotNull(repository);

        Assert.assertTrue(this.csarService.generatePlans(this.csar));

        TServiceTemplate serviceTemplate = this.csar.entryServiceTemplate();

        /*
        this.control.invokePlanDeployment(this.csar.id(), serviceTemplate);

        TPlan buildPlan = null;
        TPlan scaleOutPlan = null;
        TPlan terminationPlan = null;

        List<TPlan> plans = serviceTemplate.getPlans().getPlan();

        for(TPlan plan : plans){
            PlanType type = PlanType.fromString(plan.getPlanType());
            switch(type){
                case BUILD: buildPlan = plan; break;
                case MANAGEMENT: if(plan.getId().toLowerCase().contains("scale")){ scaleOutPlan = plan;}; break;
                case TERMINATION: terminationPlan = plan; break;
            }

        }

        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = this.getBuildPlanInputParameters();
        String correlationId = this.planService.invokePlan(this.csar,serviceTemplate, -1L, buildPlan.getId(), inputParams, PlanType.BUILD);
        PlanInstance buildPlanInstance = this.planService.getPlanInstanceByCorrelationId(correlationId);
        while(buildPlanInstance == null) {
            buildPlanInstance = this.planService.getPlanInstanceByCorrelationId(correlationId);
        }

        PlanInstanceState buildPlanInstanceState = buildPlanInstance.getState();
        while(!buildPlanInstanceState.equals(PlanInstanceState.FINISHED)){
            buildPlanInstance = this.planService.getPlanInstance(buildPlanInstance.getId());
        }
        */
        this.control.deleteCsar(this.csar.id());
    }

    private List<org.opentosca.container.core.tosca.extension.TParameter> getBuildPlanInputParameters() {
        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.tosca.extension.TParameter dockerEngineUrl = new org.opentosca.container.core.tosca.extension.TParameter();
        dockerEngineUrl.setName("DockerEngineURL");
        dockerEngineUrl.setRequired(true);
        dockerEngineUrl.setType("String");
        dockerEngineUrl.setValue("tcp://dind:2375");

        org.opentosca.container.core.tosca.extension.TParameter applicationPort = new org.opentosca.container.core.tosca.extension.TParameter();
        applicationPort.setName("ApplicationPort");
        applicationPort.setType("String");
        applicationPort.setValue("9990");
        applicationPort.setRequired(true);

        inputParams.add(dockerEngineUrl);
        inputParams.add(applicationPort);

        inputParams.addAll(this.getBaseInputParams());

        return inputParams;
    }

    public List<org.opentosca.container.core.tosca.extension.TParameter> getBaseInputParams() {
        List<org.opentosca.container.core.tosca.extension.TParameter> inputParams = new ArrayList<>();

        org.opentosca.container.core.tosca.extension.TParameter instanceDataAPIUrl = new org.opentosca.container.core.tosca.extension.TParameter();
        instanceDataAPIUrl.setName("instanceDataAPIUrl");
        instanceDataAPIUrl.setType("String");
        instanceDataAPIUrl.setValue(null);
        instanceDataAPIUrl.setRequired(true);

        org.opentosca.container.core.tosca.extension.TParameter csarEntrypoint = new org.opentosca.container.core.tosca.extension.TParameter();
        csarEntrypoint.setName("csarEntrypoint");
        csarEntrypoint.setType("String");
        csarEntrypoint.setValue(null);
        csarEntrypoint.setRequired(true);

        org.opentosca.container.core.tosca.extension.TParameter correlationId = new org.opentosca.container.core.tosca.extension.TParameter();
        correlationId.setName("CorrelationID");
        correlationId.setType("String");
        correlationId.setValue(null);
        correlationId.setRequired(true);

        inputParams.add(instanceDataAPIUrl);
        inputParams.add(csarEntrypoint);
        inputParams.add(correlationId);

        return inputParams;
    }
}
