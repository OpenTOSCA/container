package org.opentosca.deployment.verification;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public interface VerificationJob {

  VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance);

  boolean canExecute(final AbstractNodeTemplate nodeTemplate);
}
