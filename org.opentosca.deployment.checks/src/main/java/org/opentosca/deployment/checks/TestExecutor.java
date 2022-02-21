package org.opentosca.deployment.checks;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.checks.test.HttpTest;
import org.opentosca.deployment.checks.test.ManagementOperationTest;
import org.opentosca.deployment.checks.test.TcpPingTest;
import org.opentosca.deployment.checks.test.TestExecutionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TestExecutor.class);

    private final List<TestExecutionPlugin> plugins;

    private final ExecutorService jobExecutor;
    private final ExecutorService testExecutor;

    @Inject
    @Deprecated
    public TestExecutor() {
        this(Lists.newArrayList(new HttpTest(), new ManagementOperationTest(new DefaultCamelContext()), new TcpPingTest()
            // new PortBindingTest(),
            // new SqlConnectionTest()
        ));
    }

    //  @Inject
    public TestExecutor(List<TestExecutionPlugin> plugins) {
        this.plugins = plugins;
        ThreadFactory threadFactory;
        threadFactory = new ThreadFactoryBuilder().setNameFormat("job-pool-%d").setDaemon(true).build();
        this.jobExecutor = Executors.newFixedThreadPool(20, threadFactory);
        threadFactory = new ThreadFactoryBuilder().setNameFormat("test-pool-%d").setDaemon(true).build();
        this.testExecutor = Executors.newFixedThreadPool(5, threadFactory);
    }

    public CompletableFuture<Void> verify(final TestContext context) {

        Preconditions.checkNotNull(context.getServiceTemplate());
        Preconditions.checkNotNull(context.getServiceTemplateInstance());

        return CompletableFuture.supplyAsync(() -> {

            final List<CompletableFuture<DeploymentTestResult>> futures = Lists.newArrayList();

            // Submit a job if an annotations is attached to a node template that can be
            // handled by a registered plugin
            for (final NodeTemplateInstance nodeTemplateInstance : context.getNodeTemplateInstances()) {
                for (final TestExecutionPlugin plugin : this.plugins) {
                    final TNodeTemplate nodeTemplate = context.getNodeTemplate(nodeTemplateInstance);
                    final Csar csar = context.getCsar();
                    final List<TPolicyTemplate> policyTemplates = Optional.ofNullable(nodeTemplate.getPolicies())
                        .orElse(Collections.emptyList()).stream()
                        .filter(Objects::nonNull)
                        .map(p -> (TPolicyTemplate) csar.queryRepository(new PolicyTemplateId(p.getPolicyRef())))
                        .collect(Collectors.toList());
                    for (final TPolicyTemplate policyTemplate : policyTemplates) {
                        if (plugin.canExecute(nodeTemplate, policyTemplate)) {
                            logger.info("Schedule job \"{}\" for node template \"{}\" (instance={}) because annotation \"{}\" is attached...",
                                plugin.getClass().getSimpleName(), nodeTemplate.getId(),
                                nodeTemplateInstance.getId(), policyTemplate.getType());
                            futures.add(submit(plugin, context, nodeTemplate, nodeTemplateInstance, policyTemplate));
                        }
                    }
                }
            }

            // Wait until all jobs have been completed
            final List<DeploymentTestResult> results =
                futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

            context.setDeploymentTestResults(results);
            logger.info("Job statistics: {}", context.getDeploymentTest().getStatistics());

            return null;
        }, this.testExecutor);
    }

    public void shutdown() {
        try {
            logger.info("Attempt to shutdown executors...");
            this.jobExecutor.shutdown();
            this.jobExecutor.awaitTermination(5, TimeUnit.SECONDS);
            this.testExecutor.shutdown();
            this.testExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            logger.warn("Jobs interrupted");
        } finally {
            if (!this.jobExecutor.isTerminated() || !this.testExecutor.isTerminated()) {
                logger.warn("Cancel non-finished jobs...");
            }
            this.jobExecutor.shutdownNow();
            this.testExecutor.shutdownNow();
            logger.info("Shutdown finished");
        }
    }

    private CompletableFuture<DeploymentTestResult> submit(final TestExecutionPlugin plugin, final TestContext context,
                                                           final TNodeTemplate nodeTemplate,
                                                           final NodeTemplateInstance nodeTemplateInstance,
                                                           final TPolicyTemplate policyTemplate) {
        final long start = System.currentTimeMillis();
        return CompletableFuture.supplyAsync(() -> {
            final long d = System.currentTimeMillis() - start;
            logger.info("Job \"{}\" for node template \"{}\" (instance={}) spent {}ms in queue",
                plugin.getClass().getSimpleName(), nodeTemplate.getId(), nodeTemplateInstance.getId(), d);
            return plugin.execute(context, nodeTemplate, nodeTemplateInstance, policyTemplate);
        }, this.jobExecutor);
    }
}
