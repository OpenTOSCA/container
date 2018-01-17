package org.opentosca.deployment.verification;

import java.util.Collection;
import java.util.List;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Verification;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class VerificationContext {

  private AbstractServiceTemplate serviceTemplate;

  private PlanInstance planInstance;

  private Verification verification = new Verification();


  public VerificationContext() {}

  public VerificationContext(final AbstractServiceTemplate serviceTemplate,
      final PlanInstance planInstance) {
    this.serviceTemplate = serviceTemplate;
    this.planInstance = planInstance;
  }


  public AbstractServiceTemplate getServiceTemplate() {
    return serviceTemplate;
  }

  public void setServiceTemplate(final AbstractServiceTemplate serviceTemplate) {
    this.serviceTemplate = serviceTemplate;
  }

  public PlanInstance getPlanInstance() {
    return planInstance;
  }

  public void setPlanInstance(final PlanInstance planInstance) {
    this.planInstance = planInstance;
  }

  public Verification getVerification() {
    return verification;
  }

  public Collection<AbstractNodeTemplate> getNodeTemplates() {
    if (this.serviceTemplate != null) {
      return this.serviceTemplate.getTopologyTemplate().getNodeTemplates();
    } else {
      throw new IllegalStateException();
    }
  }

  public Collection<PlanInstanceOutput> getPlanOutput() {
    if (this.planInstance != null) {
      return this.planInstance.getOutputs();
    } else {
      throw new IllegalStateException();
    }
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    if (this.planInstance != null) {
      return this.planInstance.getServiceTemplateInstance();
    } else {
      throw new IllegalStateException();
    }
  }

  public Collection<NodeTemplateInstance> getNodeTemplateInstances() {
    if (this.planInstance != null) {
      return this.planInstance.getServiceTemplateInstance().getNodeTemplateInstances();
    } else {
      throw new IllegalStateException();
    }
  }

  public AbstractNodeTemplate getNodeTemplate(final NodeTemplateInstance nodeTemplateInstance) {
    return this.getNodeTemplates().stream()
        .filter(o -> o.getType().getId().equals(nodeTemplateInstance.getTemplateType())).findFirst()
        .orElseThrow(IllegalStateException::new);
  }

  public void setVerificationResults(final List<VerificationResult> verificationResults) {
    this.verification.setVerificationResults(verificationResults);
    this.verification.setPlanInstance(this.planInstance);
  }
}
