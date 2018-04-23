
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
 * <p>
 * This class implements a PlanBuilder Type Plugin for the ServerlessFunction
 * NodeType. To get an abstracted ServerlessFunction NodeType, the logic to
 * deploy a Serverless Function is implemented in the IA of the respective,
 * underlying ServerlessPlatform NodeType. The ServerlessFunction NodeType is
 * connected with a HostedOn RelationshipType with a ServerlessPlatform
 * NodeType. But the IA of the ServerlessPlatform NodeType needs to access the
 * properties of the ServerlessFunction NodeType, which is not possible in the
 * current PlanBuilder with a HostedOn RelationshipType, as it is only possible
 * to search downwards the graph of the TopologyTemplate.
 *
 * Thus, this plugin helps to get the properties of the source element of a
 * HostedOn RelationshipType and maps it onto the input parameters of the
 * respective management operation of the IA.
 * </p>
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
