package org.opentosca.planbuilder.selection.plugin.mosquitto.workload.core;

import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;

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
public abstract class MosquittoSelectionPlugin<T extends PlanContext> implements IScalingPlanBuilderSelectionPlugin<T> {

    private static final String PLUGIN_ID = "OpenTOSCA Mosquitto Workload Selection Plugin";
    private static final String WORKLOAD_SELECTION_STRATEGY = "WorkloadBased";

    @Override
    public boolean canHandle(final AbstractNodeTemplate nodeTemplate, final List<String> selectionStrategies) {
        // we can basically handle every type with this strategy
        return selectionStrategies.contains(MosquittoSelectionPlugin.WORKLOAD_SELECTION_STRATEGY);

    }

    @Override
    public String getID() {
        return MosquittoSelectionPlugin.PLUGIN_ID;
    }

}
