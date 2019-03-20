package org.opentosca.planbuilder.selection.plugin.firstavailable.bpel;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceVariablesHandler;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.selection.plugin.firstavailable.core.FirstAvailablePlugin;
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
public class BPELFirstAvailablePlugin extends FirstAvailablePlugin<BPELPlanContext> {

  private String findInstanceURLVar(final BPELPlanContext context, final String templateId, final boolean isNode) {
    final String instanceURLVarName = (isNode ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
    for (final String varName : context.getMainVariableNames()) {
      if (varName.contains(instanceURLVarName)) {
        return varName;
      }
    }
    return null;
  }

  private String findInstanceIDVar(final BPELPlanContext context, final String templateId, final boolean isNode) {
    final String instanceURLVarName = (isNode ? "node" : "relationship") + "InstanceID_" + templateId + "_";
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
    // fetch instance variables
    final String nodeTemplateInstanceURLVar = findInstanceURLVar(context, nodeTemplate.getId(), true);
    final String nodeTemplateInstanceIDVar = findInstanceIDVar(context, nodeTemplate.getId(), true);
    final String serviceTemplateUrlVar =
      ServiceInstanceVariablesHandler.getServiceTemplateURLVariableName(context.getMainVariableNames());

    if (nodeTemplateInstanceURLVar == null | serviceTemplateUrlVar == null | nodeTemplateInstanceIDVar == null) {
      return false;
    }

    final String responseVarName = "selectFirstInstance_" + nodeTemplate.getId() + "_" + System.currentTimeMillis();
    final QName anyTypeDeclId =
      context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
    context.addVariable(responseVarName, BPELPlan.VariableType.TYPE, anyTypeDeclId);

    try {
      Node getNodeInstances =
        new BPELProcessFragments().createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVar,
          responseVarName,
          nodeTemplate.getId(), null);
      getNodeInstances = context.importNode(getNodeInstances);
      context.getPrePhaseElement().appendChild(getNodeInstances);

      final String xpath2Query =
        "//*[local-name()='NodeTemplateInstanceResources']/*[local-name()='NodeTemplateInstances']/*[local-name()='NodeTemplateInstance']/*[1]/*[local-name()='Link']/@*[local-name()='href']/string()";
      Node fetchNodeInstance =
        new BPELProcessFragments().createAssignVarToVarWithXpathQueryAsNode("selectFirstInstance_"
            + nodeTemplate.getId() + "_FetchSourceNodeInstance_" + System.currentTimeMillis(), responseVarName,
          nodeTemplateInstanceURLVar,
          xpath2Query);
      ;
      fetchNodeInstance = context.importNode(fetchNodeInstance);
      context.getPrePhaseElement().appendChild(fetchNodeInstance);

      final String assignIDFromUrlVarQuery = "tokenize(//*,'/')[last()]";
      Node assignId =
        new BPELProcessFragments().createAssignVarToVarWithXpathQueryAsNode("seleftFirstInstanceassignIDFromUrlVar",
          nodeTemplateInstanceURLVar,
          nodeTemplateInstanceIDVar,
          assignIDFromUrlVarQuery);
      assignId = context.importNode(assignId);
      context.getPrePhaseElement().appendChild(assignId);
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {
      final NodeRelationInstanceVariablesHandler nodeInit =
        new NodeRelationInstanceVariablesHandler(new BPELPlanHandler());

      nodeInit.addPropertyVariableUpdateBasedOnNodeInstanceID(context, nodeTemplate);
    } catch (final ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return true;
  }

}
