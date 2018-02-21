package org.opentosca.planbuilder.selection.plugin.firstavailable.core;

import java.util.List;

import org.opentosca.planbuilder.core.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of NodeTemplate Instances to
 * the OpenTOSCA Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class FirstAvailablePlugin<T extends PlanContext> implements IScalingPlanBuilderSelectionPlugin<T> {

    private static final String PLUGIN_ID = "OpenTOSCA First Available Selection Plugin";
    private static final String FIRST_AVAIABLE_SELECTION_STRATEGY = "FirstInstance";

    @Override
    public boolean canHandle(final AbstractNodeTemplate nodeTemplate, final List<String> selectionStrategies) {
        // we can basically handle every type with this strategy
        return selectionStrategies.contains(FirstAvailablePlugin.FIRST_AVAIABLE_SELECTION_STRATEGY);
    }

    @Override
    public String getID() {
        return FirstAvailablePlugin.PLUGIN_ID;
    }
}
