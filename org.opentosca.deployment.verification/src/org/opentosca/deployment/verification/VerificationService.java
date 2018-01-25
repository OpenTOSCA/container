package org.opentosca.deployment.verification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.InternalServerErrorException;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Verification;
import org.opentosca.container.core.next.model.VerificationState;
import org.opentosca.container.core.next.repository.VerificationRepository;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerificationService {

  private static Logger logger = LoggerFactory.getLogger(VerificationService.class);

  private final VerificationRepository repository = new VerificationRepository();
  private final Importer importer = new Importer();

  private final ExecutorService pool = Executors.newFixedThreadPool(5);

  private VerificationExecutor executor;

  public Verification run(final CSARID csarId,
      final ServiceTemplateInstance serviceTemplateInstance) {

    logger.info("Trigger verification for service template instance \"{}\" of CSAR \"{}\"",
        serviceTemplateInstance.getId(), csarId);

    final AbstractDefinitions defs = importer.getMainDefinitions(csarId);
    final AbstractServiceTemplate serviceTemplate =
        defs.getServiceTemplates().stream().findFirst().orElse(null);
    if (serviceTemplate == null) {
      throw new InternalServerErrorException();
    }

    // Prepare the verification
    final Verification result = new Verification();
    result.setServiceTemplateInstance(serviceTemplateInstance);
    result.setState(VerificationState.STARTED);
    repository.add(result);

    // Prepare the context
    final VerificationContext context = new VerificationContext();
    context.setServiceTemplate(serviceTemplate);
    context.setServiceTemplateInstance(serviceTemplateInstance);
    context.setVerification(result);

    // Execute the verification
    pool.submit(() -> {
      executor.verify(context).join();
      result.setState(VerificationState.FINISHED);
      repository.update(result);
    });
    logger.info("Verification is running in background...");

    return result;
  }

  public void setVerificationExecutor(final VerificationExecutor executor) {
    this.executor = executor;
  }
}
