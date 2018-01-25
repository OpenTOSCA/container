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

  private Verification verification = new Verification();


  public VerificationContext() {}

  public VerificationContext(final AbstractServiceTemplate serviceTemplate,
      final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplate = serviceTemplate;
    this.serviceTemplateInstance = serviceTemplateInstance;
  }


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

  public void setServiceTemplateInstance(ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
  }

  public Verification getVerification() {
    return verification;
  }

  public void setVerificationResults(final List<VerificationResult> verificationResults) {
    verificationResults.stream().forEach(this.verification::addVerificationResult);
    this.verification.setServiceTemplateInstance(this.serviceTemplateInstance);
  }

  public Collection<AbstractNodeTemplate> getNodeTemplates() {
    if (this.serviceTemplate != null) {
      return this.serviceTemplate.getTopologyTemplate().getNodeTemplates();
    } else {
      throw new IllegalStateException();
    }
  }

  public Collection<NodeTemplateInstance> getNodeTemplateInstances() {
    if (this.serviceTemplateInstance != null) {
      return this.serviceTemplateInstance.getNodeTemplateInstances();
    } else {
      throw new IllegalStateException();
    }
  }

  public Collection<PlanInstanceOutput> getBuildPlanOutput() {
    if (this.serviceTemplateInstance != null) {
      final PlanInstance plan = this.serviceTemplateInstance.getPlanInstances().stream()
          .filter(p -> p.getType().equals(PlanType.BUILD)).findFirst().orElse(null);
      if (plan == null) {
        throw new IllegalStateException();
      }
      return plan.getOutputs();
    } else {
      throw new IllegalStateException();
    }
  }

  public AbstractNodeTemplate getNodeTemplate(final NodeTemplateInstance nodeTemplateInstance) {
    return this.getNodeTemplates().stream()
        .filter(o -> o.getType().getId().equals(nodeTemplateInstance.getTemplateType())).findFirst()
        .orElseThrow(IllegalStateException::new);
  }
}
