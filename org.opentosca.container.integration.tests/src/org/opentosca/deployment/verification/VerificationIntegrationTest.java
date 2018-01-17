package org.opentosca.deployment.verification;

import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.PlanService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.integration.tests.CsarActions;
import org.opentosca.container.integration.tests.ServiceTrackerUtil;
import org.opentosca.container.integration.tests.TestingUtil;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerificationIntegrationTest {

  private static Logger logger = LoggerFactory.getLogger(VerificationIntegrationTest.class);

  private static final String CSAR_NAME = "MyTinyToDo_Bare_Docker.csar";
  private static final String PLAN_NAME = "MyTinyToDo_Bare_Docker_buildPlan";

  private final PlanService planService = ServiceTrackerUtil.getService(PlanService.class);
  private final InstanceService instanceService =
      ServiceTrackerUtil.getService(InstanceService.class);

  private PlanInstanceRepository planRepository = new PlanInstanceRepository();

  private final CSARID csar = new CSARID(CSAR_NAME);

  private final UriInfo uriInfo = Mockito.mock(UriInfo.class);

  @Before
  public void init() {
    // Upload the test CSAR
    if (!CsarActions.hasCsar(CSAR_NAME)) {
      CsarActions.uploadCsar(TestingUtil.pathToURL(CSAR_NAME));
    }
  }

  @Test
  public void execute() {

    final Importer importer = new Importer();
    final AbstractDefinitions defs = importer.getMainDefinitions(csar);

    for (AbstractServiceTemplate serviceTemplate : defs.getServiceTemplates()) {

      final String namespace;
      if (serviceTemplate.getTargetNamespace() != null) {
        namespace = serviceTemplate.getTargetNamespace();
      } else {
        namespace = defs.getTargetNamespace();
      }
      final QName serviceTemplateId = new QName(namespace, serviceTemplate.getId());

      // Build context
      final VerificationContext context = new VerificationContext();
      context.setServiceTemplate(serviceTemplate);
      context.setPlanInstance(planRepository.findByCorrelationId("1516628690034-0"));



      // final PlanController ctrl = new PlanController(csar, serviceTemplateId, null, planService,
      // instanceService, PlanTypes.BUILD);
      // // Mock expectations
      // Mockito.when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri(
      // "http://localhost:1337/csars/Plantage.csar/servicetemplates/{servicetemplate}/buildplans/{plan}/instances")
      // .resolveTemplate("servicetemplate", serviceTemplateId)
      // .resolveTemplate("plan", PLAN_NAME));
      // // Execute plan
      // Response r = ctrl.invokePlan(PLAN_NAME, uriInfo, Lists.newArrayList());
      // logger.info(r.getLocation().toString());
      // Uninterruptibles.sleepUninterruptibly(60, TimeUnit.SECONDS);



      // final List<AbstractNodeTemplate> nodeTemplates =
      // serviceTemplate.getTopologyTemplate().getNodeTemplates();
    }

    VerificationExecutor e = ServiceTrackerUtil.getService(VerificationExecutor.class);
    assertNotNull(e);
  }

  @After
  public void destroy() {
    // Clean up test CSAR
    // if (!CsarActions.hasCsar(CSAR_NAME)) {
    // CsarActions.removeCsar(CSAR_NAME);
    // }
  }
}
