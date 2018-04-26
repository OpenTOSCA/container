
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
     * this method checks the incoming nodeTemplates and invokes the respective
     * method to handle them.
     */
    @Override
    public boolean handle(final BPELPlanContext templateContext) {

	final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
	if (nodeTemplate == null) {
	    return false;
	}

	BPELServerlessPlugin.LOG.debug("Handle of ServerlessPlugin is invoked");

	BPELServerlessPlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

	// check if the nodeTemplate is of ServerlessFunction nodeType
	if (Utils.isSupportedServerlessFunctionNodeType(nodeTemplate.getType().getId())) {
	    LOG.debug("Serverless Function found!");
	    // get all outgoing relationshipTemplates
	    for (final AbstractRelationshipTemplate hostedRelation : nodeTemplate.getOutgoingRelations()) {
		// check if the ServerlessFunction nodeType is hosted on a supported
		// serverlessPlatform nodeType
		if (Utils.isSupportedServerlessPlatformNodeType(hostedRelation.getTarget().getType().getId())) {
		    LOG.debug("Serverless Function is hosted on: " + hostedRelation.getTarget().getName());
		    // call the handler for a ServerlessFunction nodeTemplate which is hosted on a
		    // supported ServerlessPlatform nodeTemplate
		    return this.handler.handleWithServerlessInterface(templateContext, nodeTemplate);
		} else {
		    // ServerlessFunction NodeTemplate is hosted on an unknown nodeTemplate
		    LOG.debug("Serverless Function is hosted on unknown Serverless Platform: "
			    + hostedRelation.getTarget()
			    + ". Please add it to the Properties Class and the isSupportedServerlessPlatformNodeType list to get support.");
		    return this.handler.handle(templateContext, nodeTemplate);
		}
	    }

	    return true;
	}
	return false;
    }

}
