package org.opentosca.planbuilder.type.plugin.serverless.core.handler;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

/**
 * This class is the interface for the BPELServerlessPluginHandler.
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public interface ServerlessPluginHandler<T extends PlanContext> {

    public boolean handle(final T context, final AbstractNodeTemplate nodeTemplate);

    boolean handleWithServerlessInterface(BPELPlanContext context, AbstractNodeTemplate nodeTemplate);

}
