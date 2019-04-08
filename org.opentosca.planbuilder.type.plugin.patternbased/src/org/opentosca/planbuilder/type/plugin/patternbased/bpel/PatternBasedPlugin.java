
package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the provisioning of
 * NodeType that are modeled based on provisioning patterns.
 * </p>
 *
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class PatternBasedPlugin implements IPlanBuilderTypePlugin<BPELPlanContext> {
	
	private final static Logger LOG = LoggerFactory.getLogger(PatternBasedPlugin.class);

	private static final String id = "OpenTOSCA PlanBuilder Type Plugin Pattern-Based Provisioning";

	private static final ContainerPatternBasedHandler containerPatternHandler = new ContainerPatternBasedHandler();

	private static final LifecyclePatternBasedHandler lifecyclePatternHandler = new LifecyclePatternBasedHandler();

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean handleCreate(final BPELPlanContext templateContext) {		
		LOG.debug("Handling nodeTemplate {} by pattern", templateContext.getNodeTemplate().getId());
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
			LOG.debug("Handling by container pattern");
			return containerPatternHandler.handleCreate(templateContext, nodeTemplate);
		} else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate)) {
			LOG.debug("Handling by lifecycle pattern");
			return lifecyclePatternHandler.handleCreate(templateContext, nodeTemplate);
		} else {
			return false;
		}
	}

	@Override
	public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
		LOG.debug("Checking if nodeTemplate {} can be handled by container or lifecycle pattern", nodeTemplate.getId());
		if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
			LOG.debug("Can be handled by container pattern");
			return true;
		} else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate)) {
			LOG.debug("Can be handled by lifecycle pattern");
			return true;
		} else {
			LOG.debug("Can't be handled by pattern plugin");
			return false;
		}
	}

	@Override
	public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
		// can only handle node templates
		return false;
	}

}
