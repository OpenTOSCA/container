package org.opentosca.deployment.verification;

import java.util.Collection;
import java.util.List;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanType;
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

  public void setVerification(final Verification verification) {
    this.verification = verification;
    if (this.serviceTemplateInstance != null) {
      this.verification.setServiceTemplateInstance(this.serviceTemplateInstance);
    }
  }

  public Verification getVerification() {
    return verification;
  }

  public void setVerificationResults(final List<VerificationResult> verificationResults) {
    if (verification == null) {
      throw new IllegalStateException();
    }
    verificationResults.stream().forEach(this.verification::addVerificationResult);
    if (this.serviceTemplateInstance != null) {
      this.verification.setServiceTemplateInstance(this.serviceTemplateInstance);
    }
  }

  public Collection<AbstractNodeTemplate> getNodeTemplates() {
    if (this.serviceTemplate == null) {
      throw new IllegalStateException();
    }
    return this.serviceTemplate.getTopologyTemplate().getNodeTemplates();
  }

  public Collection<NodeTemplateInstance> getNodeTemplateInstances() {
    if (this.serviceTemplateInstance == null) {
      throw new IllegalStateException();
    }
    return this.serviceTemplateInstance.getNodeTemplateInstances();
  }

  public Collection<PlanInstanceOutput> getBuildPlanOutput() {
    if (this.serviceTemplateInstance == null) {
      throw new IllegalStateException();
    }
    final PlanInstance plan = this.serviceTemplateInstance.getPlanInstances().stream()
        .filter(p -> p.getType().equals(PlanType.BUILD)).findFirst().orElse(null);
    if (plan == null) {
      throw new IllegalStateException();
    }
    return plan.getOutputs();
  }

  public AbstractNodeTemplate getNodeTemplate(final NodeTemplateInstance nodeTemplateInstance) {
    return this.getNodeTemplates().stream()
        .filter(o -> o.getType().getId().equals(nodeTemplateInstance.getTemplateType())).findFirst()
        .orElseThrow(IllegalStateException::new);
  }
}
