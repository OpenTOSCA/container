package org.opentosca.deployment.verification;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.model.Verification;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.VerificationRepository;
import org.opentosca.container.integration.tests.CsarActions;
import org.opentosca.container.integration.tests.ServiceTrackerUtil;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * Currently, this integration test assumes that the "MyTinyToDo_Bare_Docker" CSAR has been manually
 * uploaded and at lease one instance of it is available. Then, the test tries to execute the
 * verification for each instance available.
 */
public class VerificationIntegrationTest {

  // private

  private static final String CSAR_NAME = "MyTinyToDo_Bare_Docker.csar";

  private final CSARID csar = new CSARID(CSAR_NAME);
  private final VerificationRepository repository = new VerificationRepository();

  private VerificationExecutor executor;


  @Before
  public void init() {
    executor = ServiceTrackerUtil.getService(VerificationExecutor.class);
  }

  @Test
  public void execute() {

    if (CsarActions.hasCsar(CSAR_NAME)) {

      final Importer importer = new Importer();
      final AbstractDefinitions defs = importer.getMainDefinitions(csar);

      for (AbstractServiceTemplate template : defs.getServiceTemplates()) {

        final Collection<ServiceTemplateInstance> instances =
            new ServiceTemplateInstanceRepository().findByTemplateId(template.getQName());
        if (instances.isEmpty()) {
          fail("No instance of service template \"" + template.getId() + "\" is available");
        }
        for (ServiceTemplateInstance instance : instances.stream()
            // Only select active instances
            .filter(i -> i.getState().equals(ServiceTemplateInstanceState.CREATED))
            .collect(Collectors.toList())) {

          // Prepare the verification
          final Verification result = new Verification();
          result.setServiceTemplateInstance(instance);
          repository.add(result);

          // Prepare the context
          final VerificationContext context = new VerificationContext();
          context.setServiceTemplate(template);
          context.setServiceTemplateInstance(instance);
          context.setVerification(result);

          // Execute the verification
          executor.verify(context).join();
          executor.shutdown();
          repository.update(result);
        }
      }
    }
  }
}
