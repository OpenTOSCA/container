package org.opentosca.deployment.verification;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.InternalServerErrorException;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Verification;
import org.opentosca.container.core.next.model.VerificationState;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
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

  /**
   * Runs a deployment verification if a plan with the given correlation ID is in state FINISHED.
   * 
   * @param csarId The corresponding CSAR
   * @param correlationId The correlation ID of a plan
   */
  public void runAfterPlan(final CSARID csarId, final String correlationId) {

    logger.info("Trigger verification after plan has been finished; correlation_id={}, csar={}",
        correlationId, csarId);

    pool.submit(() -> {
      long sleep = 1000;
      long timeout = TimeUnit.MINUTES.toMillis(45);
      long waited = 0;
      while (true) {
        PlanInstance pi = null;
        boolean finished = false;
        try {
          pi = new PlanInstanceRepository().findByCorrelationId(correlationId);
          finished = pi.getState().equals(PlanInstanceState.FINISHED);
        } catch (Exception e) {
          finished = false;
        }
        if (finished) {
          run(csarId, pi.getServiceTemplateInstance());
          break;
        }
        if (waited >= timeout) {
          logger.warn("Timeout reached, verification has not been executed");
          break;
        }
        try {
          Thread.sleep(sleep);
        } catch (InterruptedException e) {
        }
        waited += sleep;
      }
    });
  }

  /**
   * Runs a deployment verification for a certain service template instance.
   * 
   * @param csarId The corresponding CSAR
   * @param serviceTemplateInstance The service template instance
   * @return The created Verification object
   */
  public Verification run(final CSARID csarId,
      final ServiceTemplateInstance serviceTemplateInstance) {

    logger.info("Trigger verification for service template instance \"{}\" of CSAR \"{}\"",
        serviceTemplateInstance.getId(), csarId);

    // Prepare the verification
    final Verification result = new Verification();
    result.setServiceTemplateInstance(serviceTemplateInstance);
    result.setState(VerificationState.STARTED);
    repository.add(result);

    // Execute the verification
    pool.submit(() -> {
      logger.info("Executing verification...");
      // Prepare the context
      final AbstractDefinitions defs = importer.getMainDefinitions(csarId);
      final AbstractServiceTemplate serviceTemplate = defs.getServiceTemplates().stream()
          .findFirst().orElseThrow(InternalServerErrorException::new);
      final VerificationContext context = new VerificationContext();
      context.setServiceTemplate(serviceTemplate);
      context.setServiceTemplateInstance(serviceTemplateInstance);
      context.setVerification(result);
      final CompletableFuture<Void> future = executor.verify(context);
      logger.info("Wait until verification jobs has been finished...");
      try {
        future.join();
        logger.info("Verification jobs has been finished");
        result.setState(VerificationState.FINISHED);
      } catch (Exception e) {
        logger.error("Verification jobs completed with exception: {}", e.getMessage(), e);
        result.setState(VerificationState.FAILED);
      }
      repository.update(result);
    });
    logger.info("Verification is running in background...");

    return result;
  }

  public void setVerificationExecutor(final VerificationExecutor executor) {
    this.executor = executor;
  }
}
