package org.opentosca.deployment.verification.job;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public interface NodeTemplateJob {

  VerificationResult execute(final VerificationContext context, final AbstractNodeTemplate template,
      final NodeTemplateInstance instance);

  boolean canExecute(final AbstractNodeTemplate template);
}
