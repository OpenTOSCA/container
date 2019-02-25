package org.opentosca.deployment.checks;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.DeploymentTestRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
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
public class TestExecutorTest {

    private static final String CSAR_NAME = "MyTinyToDo_Bare_Docker.csar";

    private final CsarId csar = new CsarId(CSAR_NAME);
    private final DeploymentTestRepository repository = new DeploymentTestRepository();

    private TestExecutor executor;


    @Before
    public void init() {
        this.executor = ServiceTrackerUtil.getService(TestExecutor.class);
    }

    @Test
    public void execute() {

        if (CsarActions.hasCsar(CSAR_NAME)) {

            final Importer importer = new Importer();
            final AbstractDefinitions defs = importer.getMainDefinitions(this.csar.toOldCsarId());

            for (final AbstractServiceTemplate template : defs.getServiceTemplates()) {

                Collection<ServiceTemplateInstance> instances =
                    new ServiceTemplateInstanceRepository().findByTemplateId(template.getQName());
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
                    final TestContext context = new TestContext();
                    context.setServiceTemplate(template);
                    context.setServiceTemplateInstance(instance);
                    context.setDeploymentTest(result);

                    // Execute the verification
                    this.executor.verify(context).join();
                    this.executor.shutdown();
                    this.repository.update(result);
                }
            }
        }
    }
}
