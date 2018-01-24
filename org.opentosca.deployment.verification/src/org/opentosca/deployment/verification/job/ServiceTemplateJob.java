package org.opentosca.deployment.verification.job;

import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public interface ServiceTemplateJob {

  VerificationResult execute(final VerificationContext context,
      final AbstractServiceTemplate template, final ServiceTemplateInstance instance);

  boolean canExecute(final AbstractServiceTemplate template);
}
