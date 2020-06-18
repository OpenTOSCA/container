package org.opentosca.deployment.checks;

import java.util.Collection;
import java.util.List;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;

public class TestContext {

    private final Csar csar;
    private final TServiceTemplate serviceTemplate;
    private final ServiceTemplateInstance serviceTemplateInstance;
    private final DeploymentTest deploymentTest;

    public TestContext(Csar csar, TServiceTemplate serviceTemplate, ServiceTemplateInstance serviceTemplateInstance, DeploymentTest deploymentTest) {
        this.csar = csar;
        this.serviceTemplate = serviceTemplate;
        this.serviceTemplateInstance = serviceTemplateInstance;
        this.deploymentTest = deploymentTest;
    }

    public TServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

    public ServiceTemplateInstance getServiceTemplateInstance() {
        if (this.serviceTemplate != null) {
            return this.serviceTemplateInstance;
        } else {
            throw new IllegalStateException();
        }
    }

    public DeploymentTest getDeploymentTest() {
        return this.deploymentTest;
    }

    public synchronized void setDeploymentTestResults(final List<DeploymentTestResult> deploymentTestResults) {
        if (this.deploymentTest == null) {
            throw new IllegalStateException();
        }
        deploymentTestResults.stream().forEach(this.deploymentTest::addDeploymentTestResult);
        if (this.serviceTemplateInstance != null) {
            this.deploymentTest.setServiceTemplateInstance(this.serviceTemplateInstance);
        }
    }

    public synchronized Collection<TNodeTemplate> getNodeTemplates() {
        if (this.serviceTemplate == null) {
            throw new IllegalStateException();
        }
        return this.serviceTemplate.getTopologyTemplate().getNodeTemplates();
    }

    public synchronized Collection<NodeTemplateInstance> getNodeTemplateInstances() {
        if (this.serviceTemplateInstance == null) {
            throw new IllegalStateException();
        }
        return this.serviceTemplateInstance.getNodeTemplateInstances();
    }

    public synchronized TNodeTemplate getNodeTemplate(final NodeTemplateInstance nodeTemplateInstance) {
        return getNodeTemplates().stream()
            .filter(o -> o.getType().equals(nodeTemplateInstance.getTemplateType()))
            .findFirst().orElseThrow(IllegalStateException::new);
    }

    public synchronized NodeTemplateInstance getNodeTemplateInstance(final TNodeTemplate nodeTemplate) {
        return getNodeTemplateInstances().stream()
            .filter(o -> o.getTemplateId().equals(nodeTemplate.getId()))
            .findFirst().orElseThrow(IllegalStateException::new);
    }

    public Csar getCsar() {
        return csar;
    }
}
