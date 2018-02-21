package org.opentosca.deployment.verification;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.test.HttpTest;
import org.opentosca.deployment.verification.test.ManagementOperationTest;
import org.opentosca.deployment.verification.test.TestExecutionPlugin;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class VerificationExecutor {

    private static Logger logger = LoggerFactory.getLogger(VerificationExecutor.class);

    private final List<TestExecutionPlugin> plugins = Lists.newArrayList();

    private final ExecutorService jobExecutor;
    private final ExecutorService verificationExecutor;


    public VerificationExecutor() {
        ThreadFactory threadFactory;

        // Load available plugins
        this.plugins.add(new HttpTest());
        this.plugins.add(new ManagementOperationTest());

        // Prepare job executor
        threadFactory = new ThreadFactoryBuilder().setNameFormat("job-pool-%d").setDaemon(true).build();
        this.jobExecutor = Executors.newFixedThreadPool(20, threadFactory);

        // Prepare verification executor
        threadFactory = new ThreadFactoryBuilder().setNameFormat("verification-pool-%d").setDaemon(true).build();
        this.verificationExecutor = Executors.newFixedThreadPool(5, threadFactory);
    }

    public CompletableFuture<Void> verify(final VerificationContext context) {

        Preconditions.checkNotNull(context.getServiceTemplate());
        Preconditions.checkNotNull(context.getServiceTemplateInstance());

        return CompletableFuture.supplyAsync(() -> {

            final List<CompletableFuture<VerificationResult>> futures = Lists.newArrayList();

            // Submit a test job if an annotations is attached to a node template that can be
            // handled by a registered plugin
            for (final NodeTemplateInstance nodeTemplateInstance : context.getNodeTemplateInstances()) {
                for (final TestExecutionPlugin plugin : this.plugins) {
                    final AbstractNodeTemplate nodeTemplate = context.getNodeTemplate(nodeTemplateInstance);
                    final List<AbstractPolicyTemplate> policyTemplates = nodeTemplate.getPolicies().stream()
                                                                                     .filter(Objects::nonNull)
                                                                                     .map(p -> p.getTemplate())
                                                                                     .collect(Collectors.toList());
                    for (final AbstractPolicyTemplate policyTemplate : policyTemplates) {
                        if (plugin.canExecute(nodeTemplate, policyTemplate)) {
                            logger.info(
                                "Schedule job \"{}\" for node template \"{}\" (instance={}) because annotation \"{}\" is attached...",
                                plugin.getClass().getSimpleName(), nodeTemplate.getId(), nodeTemplateInstance.getId(),
                                policyTemplate.getType().getId());
                            futures.add(
                                this.submit(plugin, context, nodeTemplate, nodeTemplateInstance, policyTemplate));
                        }
                    }
                }
            }

            // Wait until all jobs have been completed
            final List<VerificationResult> results = futures.stream().map(CompletableFuture::join)
                                                            .collect(Collectors.toList());

            context.setVerificationResults(results);
            logger.info("Job statistics: {}", context.getVerification().getStatistics());

            return null;
        }, this.verificationExecutor);
    }

    public void shutdown() {
        try {
            logger.info("Attempt to shutdown executors...");
            this.jobExecutor.shutdown();
            this.jobExecutor.awaitTermination(5, TimeUnit.SECONDS);
            this.verificationExecutor.shutdown();
            this.verificationExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            logger.warn("Jobs interrupted");
        } finally {
            if (!this.jobExecutor.isTerminated() || !this.verificationExecutor.isTerminated()) {
                logger.warn("Cancel non-finished jobs...");
            }
            this.jobExecutor.shutdownNow();
            this.verificationExecutor.shutdownNow();
            logger.info("Shutdown finished");
        }
    }

    private CompletableFuture<VerificationResult> submit(final TestExecutionPlugin plugin,
                    final VerificationContext context, final AbstractNodeTemplate nodeTemplate,
                    final NodeTemplateInstance nodeTemplateInstance, final AbstractPolicyTemplate policyTemplate) {
        final long start = System.currentTimeMillis();
        return CompletableFuture.supplyAsync(() -> {
            final long d = System.currentTimeMillis() - start;
            logger.info("Job \"{}\" for node template \"{}\" (instance={}) spent {}ms in queue",
                plugin.getClass().getSimpleName(), nodeTemplate.getId(), nodeTemplateInstance.getId(), d);
            return plugin.execute(context, nodeTemplate, nodeTemplateInstance, policyTemplate);
        }, this.jobExecutor);
    }
}
