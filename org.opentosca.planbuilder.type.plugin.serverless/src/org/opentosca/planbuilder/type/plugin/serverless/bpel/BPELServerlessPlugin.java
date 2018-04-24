
package org.opentosca.planbuilder.type.plugin.serverless.bpel;

import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.type.plugin.serverless.bpel.handler.BPELServerlessPluginHandler;
import org.opentosca.planbuilder.type.plugin.serverless.core.ServerlessPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class forwards incoming serverless node templates depending on the node
 * type to the respective handler method. If a ServerlessFunction NodeTemplate
 * which is hosted on a supported ServerlessPlatform NodeType is found, the
 * respective method to handle this NodeTemplate is called.
 *
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public class BPELServerlessPlugin extends ServerlessPlugin<BPELPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPELServerlessPlugin.class);
    private final BPELServerlessPluginHandler handler = new BPELServerlessPluginHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final BPELPlanContext templateContext) {
	final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
	if (nodeTemplate == null) {
	    return false;
	}

	BPELServerlessPlugin.LOG.debug("Handle of ServerlessPlugin is invoked");

	BPELServerlessPlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

	if (Utils.isSupportedServerlessFunctionNodeType(nodeTemplate.getType().getId())) {
	    LOG.debug("Serverless Function found!");
	    for (final AbstractRelationshipTemplate hostedRelation : nodeTemplate.getOutgoingRelations()) {
		if (Utils.isSupportedServerlessPlatformNodeType(hostedRelation.getTarget().getType().getId())) {
		    LOG.debug("Serverless Function is hosted on: " + hostedRelation.getTarget().getName());
		    return this.handler.handleWithServerlessInterface(templateContext, nodeTemplate);
		} else {
		    LOG.debug("Serverless Function is hosted on unknown Serverless Platform: "
			    + hostedRelation.getTarget() + ". Please add it to the Properties Class to get support.");
		    return this.handler.handle(templateContext, nodeTemplate);
		}
	    }

	    return true;
	}
	return false;
    }

}
