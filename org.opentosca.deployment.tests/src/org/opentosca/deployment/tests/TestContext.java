package org.opentosca.deployment.tests;

import java.util.Collection;
import java.util.List;

import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class TestContext {

    private AbstractServiceTemplate serviceTemplate;
    private ServiceTemplateInstance serviceTemplateInstance;
    private DeploymentTest deploymentTest;


    public AbstractServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

    public void setServiceTemplate(final AbstractServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    public ServiceTemplateInstance getServiceTemplateInstance() {
        if (this.serviceTemplate != null) {
            return this.serviceTemplateInstance;
        } else {
            throw new IllegalStateException();
        }
    }

    public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
        this.serviceTemplateInstance = serviceTemplateInstance;
    }

    public synchronized void setVerification(final DeploymentTest deploymentTest) {
        this.deploymentTest = deploymentTest;
        if (this.serviceTemplateInstance != null) {
            this.deploymentTest.setServiceTemplateInstance(this.serviceTemplateInstance);
        }
    }

    public DeploymentTest getVerification() {
        return this.deploymentTest;
    }

    public synchronized void setVerificationResults(final List<DeploymentTestResult> verificationResults) {
        if (this.deploymentTest == null) {
            throw new IllegalStateException();
        }
        verificationResults.stream().forEach(this.deploymentTest::addDeploymentTestResult);
        if (this.serviceTemplateInstance != null) {
            this.deploymentTest.setServiceTemplateInstance(this.serviceTemplateInstance);
        }
    }

    public synchronized Collection<AbstractNodeTemplate> getNodeTemplates() {
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

    public synchronized AbstractNodeTemplate getNodeTemplate(final NodeTemplateInstance nodeTemplateInstance) {
        return getNodeTemplates().stream()
                                 .filter(o -> o.getType().getId().equals(nodeTemplateInstance.getTemplateType()))
                                 .findFirst().orElseThrow(IllegalStateException::new);
    }

    public synchronized NodeTemplateInstance getNodeTemplateInstance(final AbstractNodeTemplate nodeTemplate) {
        return getNodeTemplateInstances().stream()
                                         .filter(o -> o.getTemplateId().getLocalPart().equals(nodeTemplate.getId()))
                                         .findFirst().orElseThrow(IllegalStateException::new);
    }
}
