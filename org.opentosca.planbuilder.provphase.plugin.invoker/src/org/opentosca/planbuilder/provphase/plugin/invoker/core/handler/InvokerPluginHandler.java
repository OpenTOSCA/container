package org.opentosca.planbuilder.provphase.plugin.invoker.core.handler;

import java.util.Map;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;

public interface InvokerPluginHandler<T extends PlanContext> {

	public boolean handle(final T context, final AbstractOperation operation, final AbstractImplementationArtifact ia)
			throws Exception;

	public boolean handle(final T context, final String templateId, final boolean isNodeTemplate,
			final String operationName, final String interfaceName, final String callbackAddressVarName,
			final Map<String, Variable> internalExternalPropsInput,
			final Map<String, Variable> internalExternalPropsOutput, final boolean appendToPrePhase) throws Exception;

}
