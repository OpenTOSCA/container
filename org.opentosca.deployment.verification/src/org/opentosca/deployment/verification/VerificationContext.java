package org.opentosca.deployment.verification;

import java.util.Collection;
import java.util.List;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Verification;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class VerificationContext {

    private AbstractServiceTemplate serviceTemplate;
    private ServiceTemplateInstance serviceTemplateInstance;
    private Verification verification;


    public AbstractServiceTemplate getServiceTemplate() {
        return serviceTemplate;
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

    public synchronized void setVerification(final Verification verification) {
        this.verification = verification;
        if (this.serviceTemplateInstance != null) {
            this.verification.setServiceTemplateInstance(this.serviceTemplateInstance);
        }
    }

    public Verification getVerification() {
        return verification;
    }

    public synchronized void setVerificationResults(
            final List<VerificationResult> verificationResults) {
        if (verification == null) {
            throw new IllegalStateException();
        }
        verificationResults.stream().forEach(this.verification::addVerificationResult);
        if (this.serviceTemplateInstance != null) {
            this.verification.setServiceTemplateInstance(this.serviceTemplateInstance);
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

    public synchronized AbstractNodeTemplate getNodeTemplate(
            final NodeTemplateInstance nodeTemplateInstance) {
        return this.getNodeTemplates().stream()
                .filter(o -> o.getType().getId().equals(nodeTemplateInstance.getTemplateType()))
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    public synchronized NodeTemplateInstance getNodeTemplateInstance(
            final AbstractNodeTemplate nodeTemplate) {
        return this.getNodeTemplateInstances().stream()
                .filter(o -> o.getTemplateId().getLocalPart().equals(nodeTemplate.getId()))
                .findFirst().orElseThrow(IllegalStateException::new);
    }
}
