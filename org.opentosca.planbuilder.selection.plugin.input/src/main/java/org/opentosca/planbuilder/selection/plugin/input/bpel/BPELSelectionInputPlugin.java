package org.opentosca.planbuilder.selection.plugin.input.bpel;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.selection.plugin.input.core.SelectionInputPlugin;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
public class BPELSelectionInputPlugin extends SelectionInputPlugin<BPELPlanContext> {

  private String findInstanceVar(final BPELPlanContext context, final String templateId, final boolean isNode) {
    final String instanceURLVarName = (isNode ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
    for (final String varName : context.getMainVariableNames()) {
      if (varName.contains(instanceURLVarName)) {
        return varName;
      }
    }
    return null;
  }

  @Override
  public boolean handle(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                        final List<String> selectionStrategies) {
    // add input field
    final String inputFieldName = nodeTemplate.getId() + "_InstanceID";
    context.addStringValueToPlanRequest(inputFieldName);

    // fetch nodeInstanceVar
    final String nodeInstanceVarName = findInstanceVar(context, nodeTemplate.getId(), true);

    // add assign from input to nodeInstanceVar
    try {
      Node assignFromInputToNodeInstanceIdVar =
        new BPELProcessFragments().generateAssignFromInputMessageToStringVariableAsNode(inputFieldName,
          nodeInstanceVarName);
      assignFromInputToNodeInstanceIdVar = context.importNode(assignFromInputToNodeInstanceIdVar);
      context.getPrePhaseElement().appendChild(assignFromInputToNodeInstanceIdVar);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      e.printStackTrace();
    }

    try {
      new NodeRelationInstanceVariablesHandler(
        new BPELPlanHandler()).addPropertyVariableUpdateBasedOnNodeInstanceID(context, nodeTemplate);
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
    }

    return true;
  }

}
