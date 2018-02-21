package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kepeskn@iaas.uni-stuttgart.de
 *
 */
public class NodeInstanceSelector {

    private BPELProcessFragments bpelFragments;
    private BPELPlanHandler bpelProcessHandler;
    private ServiceInstanceInitializer serviceInstanceInitializer;

    public NodeInstanceSelector() {
        try {
            this.bpelFragments = new BPELProcessFragments();
            this.bpelProcessHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new ServiceInstanceInitializer();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectNodeInstances(final Map<AbstractRelationshipTemplate, List<AbstractNodeTemplate>> crossingRelations2NodesMap,
                                    final BPELPlan plan) {
        final Set<AbstractNodeTemplate> processedNodes = new HashSet<>();

        // create node and relation instances response variables
        final String nodeInstancesListResponseVarName = this.createRESTResponseVar(plan);
        final String relationInstancesListResponseVarName = this.createRESTResponseVar(plan);

        // create count variables for node and relation instances
        final String nodeInstancesCountVarName = "nodeInstancesCountVar" + System.currentTimeMillis();
        final String relationInstancesCountVarName = "nodeInstancesCountVar" + System.currentTimeMillis();

        this.bpelProcessHandler.addIntegerVariable(nodeInstancesCountVarName, plan);
        this.bpelProcessHandler.addIntegerVariable(relationInstancesCountVarName, plan);

        // fetch serviceInstance VarName
        final String serviceInstanceVarName = this.serviceInstanceInitializer.getServiceInstanceVariableName(plan);

        if (serviceInstanceVarName == null) {
            return;
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : crossingRelations2NodesMap.keySet()) {
            // fetch relation instances
            try {
                Node requestRelationInstancesGET =
                    this.bpelFragments.createBPEL4RESTLightRelationInstancesGETAsNode(relationshipTemplate.getId(),
                                                                                      serviceInstanceVarName,
                                                                                      relationInstancesListResponseVarName);
                requestRelationInstancesGET = plan.getBpelDocument().importNode(requestRelationInstancesGET, true);
                plan.getBpelMainSequenceElement().appendChild(requestRelationInstancesGET);
            }
            catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (final SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // calculate count of relation instances
            try {
                Node countRelationInstancesAssign =
                    this.bpelFragments.createAssignXpathQueryToStringVarFragmentAsNode("countRelationInstanceCount_"
                        + relationshipTemplate.getId() + "_" + System.currentTimeMillis(),
                                                                                       this.createReferenceCountingXPathQuery(relationInstancesListResponseVarName),
                                                                                       relationInstancesCountVarName);
                countRelationInstancesAssign = plan.getBpelDocument().importNode(countRelationInstancesAssign, true);
                plan.getBpelMainSequenceElement().appendChild(countRelationInstancesAssign);
            }
            catch (final IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (final SAXException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            for (final AbstractNodeTemplate nodeTemplate : crossingRelations2NodesMap.get(relationshipTemplate)) {
                // fetch node instances
                try {
                    Node requestNodeInstancesGET =
                        this.bpelFragments.createBPEL4RESTLightNodeInstancesGETAsNode(nodeTemplate.getId(),
                                                                                      serviceInstanceVarName,
                                                                                      relationInstancesListResponseVarName);
                    requestNodeInstancesGET = plan.getBpelDocument().importNode(requestNodeInstancesGET, true);
                    plan.getBpelMainSequenceElement().appendChild(requestNodeInstancesGET);
                }
                catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (final SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // calculate count of nodes
                try {
                    Node countNodeInstancesAssign =
                        this.bpelFragments.createAssignXpathQueryToStringVarFragmentAsNode("countNodeInstanceCount_"
                            + nodeTemplate.getId() + "_" + System.currentTimeMillis(),
                                                                                           this.createReferenceCountingXPathQuery(nodeInstancesListResponseVarName),
                                                                                           nodeInstancesCountVarName);
                    countNodeInstancesAssign = plan.getBpelDocument().importNode(countNodeInstancesAssign, true);
                    plan.getBpelMainSequenceElement().appendChild(countNodeInstancesAssign);
                }
                catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (final SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // TODO compare counts and execute proper selection

            }

        }
    }

    public String createReferenceCountingXPathQuery(final String referencesVarName) {
        final String query =
            "count($" + referencesVarName + "/*[local-name()='References']/*[local-name()='Reference'])";
        return query;
    }

    /**
     * Adds bpel code at the beginingn of the main logic to update instance data of the given
     * nodeTemplates their set nodeInstance
     *
     * @param nodes a set of node templates whose set nodeInstance (set at runtime) will be used for
     *        updating instancedata inside the given plan
     * @param plan the plan to add the bpel code to
     */
    public void addNodeInstanceUpdate(final Set<AbstractNodeTemplate> nodes, final BPELPlan plan,
                                      final PropertyMap propMap) {
        final String instanceDataAPIResponseVarName = this.createRESTResponseVar(plan);

        for (final AbstractNodeTemplate nodeTemplate : nodes) {

            String nodeInstanceIDVarName = null;
            try {
                nodeInstanceIDVarName =
                    new NodeInstanceInitializer(this.bpelProcessHandler).findInstanceIdVarName(plan,
                                                                                               nodeTemplate.getId());
            }
            catch (final ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (nodeInstanceIDVarName == null) {
                continue;
            }

            this.addNodeInstanceUpdate(nodeTemplate, plan, propMap, nodeInstanceIDVarName,
                                       instanceDataAPIResponseVarName);
        }
    }

    private String createRESTResponseVar(final BPELPlan plan) {
        // add XMLSchema Namespace for the logic
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);
        // create Response Variable for interaction
        final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        this.bpelProcessHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
                                            new QName(xsdNamespace, "anyType", xsdPrefix), plan);
        return instanceDataAPIResponseVarName;
    }

    public void addNodeInstanceUpdate(final AbstractNodeTemplate nodeTemplate, final BPELPlan plan,
                                      final PropertyMap propMap, final String nodeInstanceIDVarName,
                                      final String instanceDataAPIResponseVarName) {
        // check whether the nodeTemplate has properties, if not, skip the
        // update
        if (nodeTemplate.getProperties() == null) {
            return;
        }

        // fetch properties from nodeInstance
        try {
            Node nodeInstancePropertiesGETNode =
                this.bpelFragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(nodeInstanceIDVarName,
                                                                                         instanceDataAPIResponseVarName);
            nodeInstancePropertiesGETNode = plan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);

            plan.getBpelMainSequenceElement().appendChild(nodeInstancePropertiesGETNode);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }

        // assign bpel variables from the requested properties
        // create mapping from property dom nodes to bpelvariable
        final Map<Element, String> element2BpelVarNameMap = new HashMap<>();
        final NodeList propChildNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
        for (int index = 0; index < propChildNodes.getLength(); index++) {
            if (propChildNodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
                final Element childElement = (Element) propChildNodes.item(index);
                // find bpelVariable
                final String bpelVarName =
                    propMap.getPropertyMappingMap(nodeTemplate.getId()).get(childElement.getLocalName());
                if (bpelVarName != null) {
                    element2BpelVarNameMap.put(childElement, bpelVarName);
                }
            }
        }

        try {
            Node assignPropertiesToVariables =
                this.bpelFragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                    + System.currentTimeMillis(), instanceDataAPIResponseVarName, element2BpelVarNameMap);
            assignPropertiesToVariables = plan.getBpelDocument().importNode(assignPropertiesToVariables, true);
            plan.getBpelMainSequenceElement().appendChild(assignPropertiesToVariables);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }

    }

}
