package org.opentosca.deployment.checks;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.DeploymentTestRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.integration.tests.CsarActions;
import org.opentosca.container.integration.tests.ServiceTrackerUtil;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;

/**
 * Currently, this integration test assumes that the "MyTinyToDo_Bare_Docker" CSAR has been manually
 * uploaded and at lease one instance of it is available. Then, the test tries to execute the
 * verification for each instance available.
 */
public class TestExecutorTest {

  private static final String CSAR_NAME = "MyTinyToDo_Bare_Docker.csar";

  private final CsarId csarId = new CsarId(CSAR_NAME);
  private final DeploymentTestRepository repository = new DeploymentTestRepository();

  private TestExecutor executor;

  @Before
  public void init() {
    this.executor = ServiceTrackerUtil.getService(TestExecutor.class);
  }

  @Test
  public void execute() {

    if (CsarActions.hasCsar(CSAR_NAME)) {
      // FIXME setup
      Csar csar = null;//storage.findById(csarId);

      for (final TServiceTemplate template : csar.serviceTemplates()) {

        Collection<ServiceTemplateInstance> instances =
          // FIXME check QName replacement of id to substitutable nodetype
          new ServiceTemplateInstanceRepository().findByTemplateId(template.getSubstitutableNodeType());
        // Only select active instances
        instances = instances.stream().filter(i -> i.getState().equals(ServiceTemplateInstanceState.CREATED))
          .collect(Collectors.toList());
        if (instances.isEmpty()) {
          fail("No instance of service template \"" + template.getId() + "\" is available");
        }

        for (final ServiceTemplateInstance instance : instances) {

          // Prepare the verification
          final DeploymentTest result = new DeploymentTest();
          result.setServiceTemplateInstance(instance);
          this.repository.add(result);

          // Prepare the context
          final TestContext context = new TestContext(csar, template, instance, result);

          // Execute the verification
          this.executor.verify(context).join();
          this.executor.shutdown();
          this.repository.update(result);
        }
      }
    }
  }

  private PluginRegistry getPluginRegistry() {
    return new PluginRegistry(Collections.emptyList());
  }
}
