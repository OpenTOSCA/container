package org.opentosca.planbuilder.selection.plugin.mosquitto.workload.bpel;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.selection.plugin.mosquitto.workload.core.MosquittoSelectionPlugin;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of NodeTemplate Instances to the OpenTOSCA
 * Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELMosquittoSelectionPlugin extends MosquittoSelectionPlugin<BPELPlanContext> {

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
    public boolean handle(final BPELPlanContext context, final TNodeTemplate nodeTemplate,
                          final List<String> selectionStrategies) {

        // TODO fetch instance variables
        final String nodeTemplateInstanceVar = this.findInstanceVar(context, nodeTemplate.getId(), true);

        final List<TRelationshipTemplate> relations = ModelUtils.getOutgoingInfrastructureEdges(nodeTemplate, context.getCsar());

        if (relations.isEmpty()) {
            return false;
        }

        final TRelationshipTemplate relation = relations.get(0);
        final String relationTemplateInstnaceVar = this.findInstanceVar(context, relation.getId(), false);

        final String responseVarName = "selectFirstInstance_" + nodeTemplate.getId() + "_FetchRelationInstance_"
            + relation.getId() + "_" + System.currentTimeMillis();
        final QName anyTypeDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "any", "xsd"));
        context.addVariable(responseVarName, BPELPlan.VariableType.MESSAGE, anyTypeDeclId);

        try {
            Node getRelationInstance =
                new BPELProcessFragments().generateBPEL4RESTLightGETonURLAsNode(relationTemplateInstnaceVar,
                    responseVarName, null);
            getRelationInstance = context.importNode(getRelationInstance);
            context.getPrePhaseElement().appendChild(getRelationInstance);

            final String xpath2Query =
                "//*[local-name()='Reference' and @*[local-name()='title' and string()='SourceInstanceId']]/@*[local-name()='href']/string()";
            Node fetchSourceInstance =
                new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode("selectFirstInstance_"
                        + nodeTemplate.getId() + "_FetchSourceNodeInstance_" + System.currentTimeMillis(), xpath2Query,
                    nodeTemplateInstanceVar);
            fetchSourceInstance = context.importNode(fetchSourceInstance);
            context.getPrePhaseElement().appendChild(fetchSourceInstance);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
