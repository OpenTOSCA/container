package org.opentosca.deployment.verification.test;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;

public interface TestExecutionPlugin {

  VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance,
      final AbstractPolicyTemplate policyTemplate);

  boolean canExecute(final AbstractNodeTemplate nodeTemplate,
      final AbstractPolicyTemplate policyTemplate);
}
