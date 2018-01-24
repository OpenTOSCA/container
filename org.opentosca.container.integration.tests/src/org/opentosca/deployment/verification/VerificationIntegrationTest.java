package org.opentosca.deployment.verification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.VerificationRepository;
import org.opentosca.container.integration.tests.CsarActions;
import org.opentosca.container.integration.tests.ServiceTrackerUtil;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class VerificationIntegrationTest {

  private static final String CSAR_NAME = "MyTinyToDo_Bare_Docker.csar";
  // private static final String PLAN_NAME = "MyTinyToDo_Bare_Docker_buildPlan";

  private PlanInstanceRepository planRepository = new PlanInstanceRepository();

  private VerificationRepository verificationRepository = new VerificationRepository();

  private VerificationExecutor executor;

  private final CSARID csar = new CSARID(CSAR_NAME);


  @Before
  public void init() {
    executor = ServiceTrackerUtil.getService(VerificationExecutor.class);
  }

  @Test
  public void execute() {

    if (CsarActions.hasCsar(CSAR_NAME)) {

      // TODO: Create plan, service, node, and relation instances for testing

      final Importer importer = new Importer();
      final AbstractDefinitions defs = importer.getMainDefinitions(csar);

      for (AbstractServiceTemplate serviceTemplate : defs.getServiceTemplates()) {

        final VerificationContext context = new VerificationContext();
        context.setServiceTemplate(serviceTemplate);
        context.setPlanInstance(planRepository.findByCorrelationId("1516628690034-0"));

        executor.verify(context);
        verificationRepository.add(context.getVerification());
      }
    }
  }

  @After
  public void destroy() {

  }

  // FIXME: Remove dead code
  // final String namespace;
  // if (serviceTemplate.getTargetNamespace() != null) {
  // namespace = serviceTemplate.getTargetNamespace();
  // } else {
  // namespace = defs.getTargetNamespace();
  // }
  // final QName serviceTemplateId = new QName(namespace, serviceTemplate.getId());
  // final PlanController ctrl = new PlanController(csar, serviceTemplateId, null,
  // planService,
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
