package org.opentosca.planbuilder.type.plugin.serverless.core;

import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class checks for supported NodeTemplates of this plugin.
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public abstract class ServerlessPlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {
    public static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Serverless Plugin";
    private final static Logger LOG = LoggerFactory.getLogger(ServerlessPlugin.class);

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.AbstractNodeTemplate)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(final AbstractNodeTemplate nodeTemplate) {

	LOG.debug("CanHandle of Serverless Plugin is invoked");
	if (nodeTemplate == null) {
	    ServerlessPlugin.LOG.debug("NodeTemplate is null");
	}
	if (nodeTemplate.getType() == null) {
	    ServerlessPlugin.LOG.debug("NodeTemplate NodeType is null. NodeTemplate Id:" + nodeTemplate.getId());
	}
	if (nodeTemplate.getType().getId() == null) {
	    ServerlessPlugin.LOG.debug("NodeTemplate NodeType id is null");
	}
	// this plugin can handle all referenced nodeTypes
	if (Utils.isSupportedServerlessFunctionNodeType(nodeTemplate.getType().getId())
		| Utils.isSupportedServerlessEventNodeType(nodeTemplate.getType().getId())
		| Utils.isSupportedServerlessPlatformNodeType(nodeTemplate.getType().getId())) {
	    return true;
	} else {
	    return false;
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
     * opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate)
     */
    @Override
    public boolean canHandle(final AbstractRelationshipTemplate relationshipTemplate) {
	// this plugin cannot handle relationship templates
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.plugins.IPlanBuilderPlugin#getID()
     */
    @Override
    public String getID() {
	return ServerlessPlugin.PLUGIN_ID;
    }

}
