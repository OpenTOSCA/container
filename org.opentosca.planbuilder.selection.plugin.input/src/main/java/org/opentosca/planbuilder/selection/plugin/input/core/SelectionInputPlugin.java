package org.opentosca.planbuilder.selection.plugin.input.core;

import java.util.List;

import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.IScalingPlanBuilderSelectionPlugin;
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
 */
public abstract class SelectionInputPlugin<T extends PlanContext> implements IScalingPlanBuilderSelectionPlugin<T> {

  private static final String PLUGIN_ID = "OpenTOSCA Input Selection Plugin";
  private static final String INPUT_SELECTION_STRATEGY = "UserProvided";

  @Override
  public boolean canHandle(final AbstractNodeTemplate nodeTemplate, final List<String> selectionStrategies) {
    return selectionStrategies.contains(SelectionInputPlugin.INPUT_SELECTION_STRATEGY);
  }

  @Override
  public String getID() {
    return SelectionInputPlugin.PLUGIN_ID;
  }

}
