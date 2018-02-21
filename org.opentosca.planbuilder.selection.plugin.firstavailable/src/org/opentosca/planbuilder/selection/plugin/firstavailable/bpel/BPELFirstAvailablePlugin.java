package org.opentosca.planbuilder.selection.plugin.firstavailable.bpel;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.NodeInstanceInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceInitializer;
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
 *
 */
public class BPELFirstAvailablePlugin extends FirstAvailablePlugin<BPELPlanContext> {

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
        // fetch instance variables
        final String nodeTemplateInstanceVar = this.findInstanceVar(context, nodeTemplate.getId(), true);
        String serviceInstanceIDVar = null;
        try {
            serviceInstanceIDVar = new ServiceInstanceInitializer().getServiceInstanceVariableName(
                context.getMainVariableNames());
        } catch (final ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (nodeTemplateInstanceVar == null | serviceInstanceIDVar == null) {
            return false;
        }

        final String responseVarName = "selectFirstInstance_" + nodeTemplate.getId() + "_" + System.currentTimeMillis();
        final QName anyTypeDeclId = context.importQName(
            new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
        context.addVariable(responseVarName, BPELPlan.VariableType.TYPE, anyTypeDeclId);

        try {

            // TODO SELECT THE FIRST STARTED INSTANCE (use get with query, is already in
            // fragments)
            Node getNodeInstances = new BPELProcessFragments().createBPEL4RESTLightNodeInstancesGETAsNode(
                nodeTemplate.getId(), serviceInstanceIDVar, responseVarName);
            getNodeInstances = context.importNode(getNodeInstances);
            context.getPrePhaseElement().appendChild(getNodeInstances);

            final String xpath2Query = "$" + responseVarName
                + "/*[local-name()='Reference' and @*[local-name()='title' and string()!='Self']][1]/@*[local-name()='href']/string()";
            Node fetchNodeInstance = new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode(
                "selectFirstInstance_" + nodeTemplate.getId() + "_FetchSourceNodeInstance_"
                    + System.currentTimeMillis(),
                xpath2Query, nodeTemplateInstanceVar);
            fetchNodeInstance = context.importNode(fetchNodeInstance);
            context.getPrePhaseElement().appendChild(fetchNodeInstance);

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
            final NodeInstanceInitializer nodeInit = new NodeInstanceInitializer(new BPELPlanHandler());

            nodeInit.addPropertyVariableUpdateBasedOnNodeInstanceID(context, nodeTemplate);
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

}
