package org.opentosca.deployment.verification;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.job.NodeTemplateJob;
import org.opentosca.deployment.verification.job.ServiceTemplateJob;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class VerificationExecutor {

  private static Logger logger = LoggerFactory.getLogger(VerificationExecutor.class);

  private final Set<NodeTemplateJob> nodeTemplateJobs = Sets.newHashSet();
  private final Set<ServiceTemplateJob> serviceTemplateJobs = Sets.newHashSet();

  private final ExecutorService executor;


  public VerificationExecutor() {
    this(50);
  }

  public VerificationExecutor(final int threadPoolSize) {
    final ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("job-pool-%d").setDaemon(true).build();
    this.executor = Executors.newFixedThreadPool(threadPoolSize, threadFactory);
  }


  public void verify(final VerificationContext context) {

    // Check context...
    Preconditions.checkNotNull(context.getServiceTemplate());
    Preconditions.checkNotNull(context.getPlanInstance());

    // Prepare result...
    final List<CompletableFuture<VerificationResult>> futures = Lists.newArrayList();
    // ... and submit jobs based on node templates
    for (NodeTemplateInstance nodeTemplateInstance : context.getNodeTemplateInstances()) {
      for (NodeTemplateJob job : nodeTemplateJobs) {
        final AbstractNodeTemplate nodeTemplate = context.getNodeTemplate(nodeTemplateInstance);
        if (job.canExecute(nodeTemplate)) {
          logger.info("Schedule job \"{}\" for node template instance \"{}\" ({})...",
              job.getClass().getSimpleName(), nodeTemplateInstance.getId(), nodeTemplate.getId());
          futures.add(this.submit(job, context, nodeTemplate, nodeTemplateInstance));
        }
      }
    }
    // ... and based on service templates
    for (ServiceTemplateJob job : serviceTemplateJobs) {
      final AbstractServiceTemplate serviceTemplate = context.getServiceTemplate();
      final ServiceTemplateInstance serviceTemplateInstance = context.getServiceTemplateInstance();
      if (job.canExecute(serviceTemplate)) {
        logger.info("Schedule job \"{}\" for service template instance \"{}\" ({})...",
            job.getClass().getSimpleName(), serviceTemplateInstance.getId(),
            serviceTemplate.getId());
        futures.add(this.submit(job, context, serviceTemplate, serviceTemplateInstance));
      }
    }

    // Wait until all jobs have been completed
    final List<VerificationResult> results =
        futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

    context.setVerificationResults(results);
    logger.info("Job statistics: {}", context.getVerification().getStatistics());
  }

  public void shutdown() {
    try {
      logger.info("Attempt to shutdown verification executor...");
      this.executor.shutdown();
      this.executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.warn("Jobs interrupted");
    } finally {
      if (!this.executor.isTerminated()) {
        logger.warn("Cancel non-finished jobs...");
      }
      this.executor.shutdownNow();
      logger.info("Shutdown finished");
    }
  }

  private CompletableFuture<VerificationResult> submit(final NodeTemplateJob job,
      final VerificationContext context, final AbstractNodeTemplate nodeTemplate,
      final NodeTemplateInstance nodeTemplateInstance) {
    final long start = System.currentTimeMillis();
    return CompletableFuture.supplyAsync(() -> {
      final long d = System.currentTimeMillis() - start;
      logger.info("Job \"{}\" for node template instance \"{}\" ({}) spent {}ms in queue",
          job.getClass().getSimpleName(), nodeTemplateInstance.getId(), nodeTemplate.getId(), d);
      return job.execute(context, nodeTemplate, nodeTemplateInstance);
    }, this.executor);
  }

  private CompletableFuture<VerificationResult> submit(final ServiceTemplateJob job,
      final VerificationContext context, final AbstractServiceTemplate serviceTemplate,
      final ServiceTemplateInstance serviceTemplateInstance) {
    final long start = System.currentTimeMillis();
    return CompletableFuture.supplyAsync(() -> {
      final long d = System.currentTimeMillis() - start;
      logger.info("Job \"{}\" for service template instance \"{}\" ({}) spent {}ms in queue",
          job.getClass().getSimpleName(), serviceTemplateInstance.getId(), serviceTemplate.getId(),
          d);
      return job.execute(context, serviceTemplate, serviceTemplateInstance);
    }, this.executor);
  }

  public void bindNodeTemplateJob(final NodeTemplateJob job) {
    nodeTemplateJobs.add(job);
  }

  public void unbindNodeTemplateJob(final NodeTemplateJob job) {
    nodeTemplateJobs.remove(job);
  }

  public void bindServiceTemplateJob(final ServiceTemplateJob job) {
    serviceTemplateJobs.add(job);
  }

  public void unbindServiceTemplateJob(final ServiceTemplateJob job) {
    serviceTemplateJobs.remove(job);
  }
}
