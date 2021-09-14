package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all logic to append BPEL code which updates the InstanceData of a NodeTemplate
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class Handler {

    private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
    private final XPathFactory xPathfactory = XPathFactory.newInstance();
    private Fragments fragments;
    private BPELProcessFragments bpelFrags;
    private BPELInvokerPlugin invoker;

    public Handler() {

        try {
            this.fragments = new Fragments();
            this.bpelFrags = new BPELProcessFragments();
            this.invoker = new BPELInvokerPlugin();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String createRESTResponseVar(final BPELPlanContext context) {
        final String restCallResponseVarName = "bpel4restlightVarResponse" + context.getIdForNames();
        final QName restCallResponseDeclId =
            context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
        if (!context.addGlobalVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE, restCallResponseDeclId)) {
            return null;
        }
        return restCallResponseVarName;
    }

    private String createStateVar(final BPELPlanContext context, final String templateId) {
        // create state variable inside scope
        final String stateVarName = ModelUtils.makeValidNCName(templateId) + "_state_" + context.getIdForNames();
        final QName stringTypeDeclId =
            context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        if (!context.addGlobalVariable(stateVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
            return null;
        }

        return stateVarName;
    }

    public String createInstanceURLVar(final BPELPlanContext context, final String templateId) {
        final String instanceURLVarName = (context.getRelationshipTemplate() == null ? "node" : "relationship")
            + "InstanceURL_" + ModelUtils.makeValidNCName(templateId) + "_" + context.getIdForNames();
        final QName stringTypeDeclId =
            context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        if (!context.addGlobalVariable(instanceURLVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
            return null;
        }

        return instanceURLVarName;
    }

    public String createInstanceIDVar(final BPELPlanContext context, final String templateId) {
        final String instanceURLVarName = (context.getRelationshipTemplate() == null ? "node" : "relationship")
            + "InstanceID_" + ModelUtils.makeValidNCName(templateId) + "_" + context.getIdForNames();
        final QName stringTypeDeclId =
            context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        if (!context.addGlobalVariable(instanceURLVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
            return null;
        }

        return instanceURLVarName;
    }

    public boolean handleTerminate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        final boolean hasProps = checkProperties(nodeTemplate.getProperties());

        final String serviceInstanceVarName = context.getServiceInstanceURLVarName();
        if (serviceInstanceVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = createStateVar(context, context.getNodeTemplate().getId());

        if (stateVarName == null) {
            return false;
        }

        String nodeInstanceURLVarName = "";

        if (context.findInstanceURLVar(context.getNodeTemplate().getId(), true) == null) {
            // generate String var for nodeInstance URL
            nodeInstanceURLVarName = createInstanceURLVar(context, context.getNodeTemplate().getId());
        } else {
            nodeInstanceURLVarName = context.findInstanceURLVar(context.getNodeTemplate().getId(), true);
        }

        if (nodeInstanceURLVarName == null) {
            return false;
        }

        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        String lastSetState = "DELETED";

        /*
         * Prov Phase code
         */

        // fetch all assigns that assign an invoke async operation request

        final Element provisioningPhaseElement = context.getProvisioningPhaseElement();
        final List<Element> assignContentElements = fetchInvokerCallAssigns(provisioningPhaseElement);

        // for each assign element we fetch the operation name, determine the
        // pre and post states, and append the pre state before the found assign
        // and the post state after the receive of the invoker iteraction
        for (final Element assignContentElement : assignContentElements) {

            // fetch operationName from literal contents
            final String operationName = fetchOperationName(assignContentElement);
            // determine pre and post state for operation
            final String preState = InstanceStates.getOperationPreState(operationName);
            final String postState = InstanceStates.getOperationPostState(operationName);

            if (preState != null) {

                try {

                    // assign prestate to state variable
                    final BPELProcessFragments frag = new BPELProcessFragments();
                    Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeStateFor_"
                        + operationName + "_" + System.currentTimeMillis(), "string('" + preState + "')", stateVarName);
                    assignNode = context.importNode(assignNode);
                    lastSetState = preState;

                    // assign the state before the assign of the invoker request
                    // is made
                    final Node bpelAssignNode =
                        assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
                    bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);

                    // create REST Put activity
                    final String bpelString =
                        this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
                    Node extActiv = ModelUtils.string2dom(bpelString);
                    extActiv = context.importNode(extActiv);

                    // send the state before the assign of the invoker request
                    // is made
                    bpelAssignNode.getParentNode().insertBefore(extActiv, bpelAssignNode);
                } catch (final IOException e2) {
                    e2.printStackTrace();
                } catch (final SAXException e) {
                    e.printStackTrace();
                } catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }

            if (postState != null) {
                try {
                    // create state assign activity
                    final BPELProcessFragments frag = new BPELProcessFragments();
                    Node assignNode =
                        frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeState_" + operationName + "_"
                            + System.currentTimeMillis(), "string('" + postState + "')", stateVarName);
                    assignNode = context.importNode(assignNode);

                    lastSetState = postState;

                    /*
                     * assign the state after the receiving the response of the
                     */

                    // fetch assign node
                    final Node bpelAssignNode =
                        assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();

                    // fetch the variable name which is used as request body
                    final String reqVarName = fetchRequestVarNameFromInvokerAssign(assignContentElement);

                    // from the assign element search for the receive element
                    // that is witing for the response
                    final Element invokerReceiveElement = fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

                    // insert assign after the receive
                    assignNode = invokerReceiveElement.getParentNode()
                        .insertBefore(assignNode, invokerReceiveElement.getNextSibling());

                    // create PUT activity
                    final String bpelString =
                        this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
                    Node extActiv = ModelUtils.string2dom(bpelString);
                    extActiv = context.importNode(extActiv);

                    // insert REST call after the assign
                    invokerReceiveElement.getParentNode().insertBefore(extActiv, assignNode.getNextSibling());
                } catch (final IOException e2) {
                    e2.printStackTrace();
                } catch (final SAXException e) {
                    e.printStackTrace();
                } catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        // needs property update only if the node has properties
        if (hasProps) {
            // make a GET on the nodeInstance properties

            try {
                // fetch properties
                Node nodeInstancePropsGETNode =
                    this.fragments.generateInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
                nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
                context.getPostPhaseElement().appendChild(nodeInstancePropsGETNode);
            } catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            } catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, QName> propertyVarNameToDOMMapping =
                buildMappingsFromVarNameToDomElement(context, nodeTemplate);
            try {
                // then generate an assign to have code that writes the runtime
                // values into the instance data db.
                // we use the restCallResponseVarName from the GET before, as it
                // has
                // proper format
                Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
                    propertyVarNameToDOMMapping);
                assignNode = context.importNode(assignNode);
                context.getPostPhaseElement().appendChild(assignNode);
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }

            // generate BPEL4RESTLight PUT request to update the instance data
            try {
                Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                    nodeInstanceURLVarName);
                bpel4restPUTNode = context.importNode(bpel4restPUTNode);
                context.getPostPhaseElement().appendChild(bpel4restPUTNode);
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        this.appendStateUpdateToPostPhase(context, nodeInstanceURLVarName, stateVarName, lastSetState);
        this.appendFailedStateToFaultHandler(context, nodeInstanceURLVarName);
        return true;
    }

    public boolean handleTerminate(final BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        final String restCallResponseVarName = createRESTResponseVar(context);
        final String stateVarName = createStateVar(context, relationshipTemplate.getId());

        String relationInstanceURLVarName = context.findInstanceURLVar(relationshipTemplate.getId(), false);

        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        String lastSetState = "DELETED";

        final Element provisioningPhaseElement = context.getProvisioningPhaseElement();
        final List<Element> assignContentElements = fetchInvokerCallAssigns(provisioningPhaseElement);

        // for each assign element we fetch the operation name, determine the
        // pre and post states, and append the pre state before the found assign
        // and the post state after the receive of the invoker iteraction
        for (final Element assignContentElement : assignContentElements) {

            // fetch operationName from literal contents
            final String operationName = fetchOperationName(assignContentElement);
            // determine pre and post state for operation
            final String preState = InstanceStates.getOperationPreState(operationName);
            final String postState = InstanceStates.getOperationPostState(operationName);

            if (preState != null) {

                try {

                    // assign prestate to state variable
                    final BPELProcessFragments frag = new BPELProcessFragments();
                    Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignRelationStateFor_"
                        + operationName + "_" + System.currentTimeMillis(), "string('" + preState + "')", stateVarName);
                    assignNode = context.importNode(assignNode);
                    lastSetState = preState;

                    // assign the state before the assign of the invoker request
                    // is made
                    final Node bpelAssignNode =
                        assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
                    bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);

                    // create REST Put activity
                    final String bpelString =
                        this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
                    Node extActiv = ModelUtils.string2dom(bpelString);
                    extActiv = context.importNode(extActiv);

                    // send the state before the assign of the invoker request
                    // is made
                    bpelAssignNode.getParentNode().insertBefore(extActiv, bpelAssignNode);
                } catch (final IOException e2) {
                    e2.printStackTrace();
                } catch (final SAXException e) {
                    e.printStackTrace();
                } catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }

            if (postState != null) {
                try {
                    // create state assign activity
                    final BPELProcessFragments frag = new BPELProcessFragments();
                    Node assignNode =
                        frag.createAssignXpathQueryToStringVarFragmentAsNode("assignRelationState_" + operationName + "_"
                            + System.currentTimeMillis(), "string('" + postState + "')", stateVarName);
                    assignNode = context.importNode(assignNode);

                    lastSetState = postState;

                    /*
                     * assign the state after the receiving the response of the
                     */

                    // fetch assign node
                    final Node bpelAssignNode =
                        assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();

                    // fetch the variable name which is used as request body
                    final String reqVarName = fetchRequestVarNameFromInvokerAssign(assignContentElement);

                    // from the assign element search for the receive element
                    // that is witing for the response
                    final Element invokerReceiveElement = fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

                    // insert assign after the receive
                    assignNode = invokerReceiveElement.getParentNode()
                        .insertBefore(assignNode, invokerReceiveElement.getNextSibling());

                    // create PUT activity
                    final String bpelString =
                        this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
                    Node extActiv = ModelUtils.string2dom(bpelString);
                    extActiv = context.importNode(extActiv);

                    // insert REST call after the assign
                    invokerReceiveElement.getParentNode().insertBefore(extActiv, assignNode.getNextSibling());
                } catch (final IOException e2) {
                    e2.printStackTrace();
                } catch (final SAXException e) {
                    e.printStackTrace();
                } catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        // needs property update only if the node has properties
        if (this.checkProperties(relationshipTemplate.getProperties())) {
            // make a GET on the nodeInstance properties

            try {
                // fetch properties
                Node nodeInstancePropsGETNode =
                    this.fragments.generateInstancePropertiesGETAsNode(relationInstanceURLVarName, restCallResponseVarName);
                nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
                context.getPostPhaseElement().appendChild(nodeInstancePropsGETNode);
            } catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            } catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, QName> propertyVarNameToDOMMapping =
                buildMappingsFromVarNameToDomElement(context, relationshipTemplate);
            try {
                // then generate an assign to have code that writes the runtime
                // values into the instance data db.
                // we use the restCallResponseVarName from the GET before, as it
                // has
                // proper format
                Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
                    propertyVarNameToDOMMapping);
                assignNode = context.importNode(assignNode);
                context.getPostPhaseElement().appendChild(assignNode);
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }

            // generate BPEL4RESTLight PUT request to update the instance data
            try {
                Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                    relationInstanceURLVarName);
                bpel4restPUTNode = context.importNode(bpel4restPUTNode);
                context.getPostPhaseElement().appendChild(bpel4restPUTNode);
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        this.appendStateUpdateToPostPhase(context, relationInstanceURLVarName, stateVarName, lastSetState);
        this.appendFailedStateToFaultHandler(context, relationInstanceURLVarName);
        return true;
    }

    public boolean handleUpdate(final BPELPlanContext sourceContext, final BPELPlanContext targetContext,
                                TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        final boolean hasProps = checkProperties(sourceNodeTemplate.getProperties());

        /* create new node instance */
        final String targetServiceInstanceUrlVar = targetContext.getServiceInstanceURLVarName();
        final String targetServiceInstanceIdVar = targetContext.getServiceInstanceIDVarName();
        final String targetServiceTemplateUrlVar = targetContext.getServiceTemplateURLVar();

        final String restCallResponseVar = this.createRESTResponseVar(targetContext);
        final String stateVar = this.createStateVar(targetContext, targetNodeTemplate.getId());

        // create instance at API
        try {
            final String bpelString =
                this.fragments.generateBPEL4RESTLightNodeInstancePOST(targetServiceTemplateUrlVar,
                    targetServiceInstanceIdVar,
                    targetNodeTemplate.getId(), restCallResponseVar);
            Node createNodeInstanceExActiv = ModelUtils.string2dom(bpelString);
            createNodeInstanceExActiv = targetContext.importNode(createNodeInstanceExActiv);
            targetContext.getPrePhaseElement().appendChild(createNodeInstanceExActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final String targetNodeInstanceUrlVar = targetContext.findInstanceURLVar(targetNodeTemplate.getId(), true);
        final String targetNodeInstanceIdVar = targetContext.findInstanceIDVar(targetNodeTemplate.getId(), true);
        final String sourceNodeInstanceURLVarName = sourceContext.findInstanceURLVar(sourceNodeTemplate.getId(), true);
        // save data from response in node instance vars
        try {
            // save nodeInstance url from response
            final String bpelString =
                this.fragments.generateAssignFromNodeInstancePOSTResponseToStringVar(targetNodeInstanceUrlVar,
                    targetNodeInstanceIdVar,
                    restCallResponseVar);
            Node assignNodeInstanceUrl = ModelUtils.string2dom(bpelString);
            assignNodeInstanceUrl = targetContext.importNode(assignNodeInstanceUrl);
            targetContext.getPrePhaseElement().appendChild(assignNodeInstanceUrl);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* load properties and state from old instance to new instance */
        if (hasProps) {
            this.appendUpdatePropertiesFromSourceToTarget(sourceContext, targetContext, sourceNodeTemplate,
                sourceNodeInstanceURLVarName, targetNodeInstanceUrlVar,
                restCallResponseVar, targetContext.getPostPhaseElement());
        }

        this.appendGetStateToPostPhase(targetContext, sourceNodeInstanceURLVarName, stateVar);
        this.appendStateUpdateToPostPhase(targetContext, targetNodeInstanceUrlVar, stateVar);
        /* set state of old instance to migrated */
        this.appendStateUpdateToPostPhase(sourceContext, sourceNodeInstanceURLVarName, stateVar, "MIGRATED");

        this.appendFailedStateToFaultHandler(targetContext, targetNodeInstanceUrlVar);
        this.appendFailedStateToFaultHandler(sourceContext, sourceNodeInstanceURLVarName);
        return true;
    }

    /**
     * Appends BPEL Code that updates InstanceData for the given NodeTemplate. Needs initialization code on the global
     * level in the plan. This will be checked and appended if needed.
     *
     * @param context      the TemplateContext of the NodeTemplate
     * @param nodeTemplate the NodeTemplate to handle
     * @return true iff appending all BPEL code was successful
     */
    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        final boolean hasProps = checkProperties(nodeTemplate.getProperties());

        final String serviceInstanceVarName = context.getServiceInstanceURLVarName();
        if (serviceInstanceVarName == null) {
            return false;
        }

        final String serviceInstanceIDVarName = context.getServiceInstanceIDVarName();
        if (serviceInstanceIDVarName == null) {
            return false;
        }

        final String serviceTemplateUrlVarName = context.getServiceTemplateURLVar();
        if (serviceTemplateUrlVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = createStateVar(context, context.getNodeTemplate().getId());

        if (stateVarName == null) {
            return false;
        }

        /*
         * (i) append bpel code to create the nodeInstance (ii) append bpel code to fetch nodeInstanceURL
         */

        try {
            // create bpel extension activity and append
            final String bpelString =
                this.fragments.generateBPEL4RESTLightNodeInstancePOST(serviceTemplateUrlVarName,
                    serviceInstanceIDVarName,
                    context.getNodeTemplate().getId(),
                    restCallResponseVarName);
            Node createNodeInstanceExActiv = ModelUtils.string2dom(bpelString);
            createNodeInstanceExActiv = context.importNode(createNodeInstanceExActiv);
            context.getPrePhaseElement().appendChild(createNodeInstanceExActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String nodeInstanceURLVarName = "";

        if (context.findInstanceURLVar(context.getNodeTemplate().getId(), true) == null) {
            // generate String var for nodeInstance URL
            nodeInstanceURLVarName = createInstanceURLVar(context, context.getNodeTemplate().getId());
        } else {
            nodeInstanceURLVarName = context.findInstanceURLVar(context.getNodeTemplate().getId(), true);
        }

        if (nodeInstanceURLVarName == null) {
            return false;
        }

        String nodeInstanceIDVarName = "";

        if (context.findInstanceIDVar(context.getNodeTemplate().getId(), true) == null) {
            nodeInstanceIDVarName = createInstanceIDVar(context, context.getNodeTemplate().getId());
        } else {
            nodeInstanceIDVarName = context.findInstanceIDVar(context.getNodeTemplate().getId(), true);
        }

        if (nodeInstanceIDVarName == null) {
            return false;
        }

        try {
            // save nodeInstance url from response
            final String bpelString =
                this.fragments.generateAssignFromNodeInstancePOSTResponseToStringVar(nodeInstanceURLVarName,
                    nodeInstanceIDVarName,
                    restCallResponseVarName);
            Node assignNodeInstanceUrl = ModelUtils.string2dom(bpelString);
            assignNodeInstanceUrl = context.importNode(assignNodeInstanceUrl);
            context.getPrePhaseElement().appendChild(assignNodeInstanceUrl);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        String lastSetState = "INITIAL";

        this.appendStateUpdateToPrePhase(context, nodeInstanceURLVarName, stateVarName, lastSetState);

        /*
         * Prov Phase code
         */

        // fetch all assigns that assign an invoke async operation request

        final Element provisioningPhaseElement = context.getProvisioningPhaseElement();
        final List<Element> assignContentElements = fetchInvokerCallAssigns(provisioningPhaseElement);

        final List<String> operationNames = new ArrayList<>();

        // for each assign element we fetch the operation name, determine the
        // pre and post states, and append the pre state before the found assign
        // and the post state after the receive of the invoker iteraction
        for (final Element assignContentElement : assignContentElements) {

            // fetch operationName from literal contents
            final String operationName = fetchOperationName(assignContentElement);
            operationNames.add(operationName);
            // determine pre and post state for operation
            final String preState = InstanceStates.getOperationPreState(operationName);
            final String postState = InstanceStates.getOperationPostState(operationName);

            if (preState != null) {

                try {

                    // assign prestate to state variable
                    final BPELProcessFragments frag = new BPELProcessFragments();
                    Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeStateFor_"
                        + operationName + "_" + System.currentTimeMillis(), "string('" + preState + "')", stateVarName);
                    assignNode = context.importNode(assignNode);
                    lastSetState = preState;

                    // assign the state before the assign of the invoker request
                    // is made
                    final Node bpelAssignNode =
                        assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
                    bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);

                    // create REST Put activity
                    final String bpelString =
                        this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
                    Node extActiv = ModelUtils.string2dom(bpelString);
                    extActiv = context.importNode(extActiv);

                    // send the state before the assign of the invoker request
                    // is made
                    bpelAssignNode.getParentNode().insertBefore(extActiv, bpelAssignNode);
                } catch (final IOException e2) {
                    e2.printStackTrace();
                } catch (final SAXException e) {
                    e.printStackTrace();
                } catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }

            if (postState != null) {
                try {
                    // create state assign activity
                    final BPELProcessFragments frag = new BPELProcessFragments();
                    Node assignNode =
                        frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeState_" + operationName + "_"
                            + System.currentTimeMillis(), "string('" + postState + "')", stateVarName);
                    assignNode = context.importNode(assignNode);

                    lastSetState = postState;

                    /*
                     * assign the state after the receiving the response of the
                     */

                    // fetch assign node
                    final Node bpelAssignNode =
                        assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();

                    // fetch the variable name which is used as request body
                    final String reqVarName = fetchRequestVarNameFromInvokerAssign(assignContentElement);

                    // from the assign element search for the receive element
                    // that is witing for the response
                    final Element invokerReceiveElement = fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

                    // insertAfterUpdateProperties(context, nodeTemplate, nodeInstanceURLVarName,
                    // restCallResponseVarName,
                    // invokerReceiveElement);

                    // insert assign after the receive
                    assignNode = invokerReceiveElement.getParentNode()
                        .insertBefore(assignNode, invokerReceiveElement.getNextSibling());

                    // create PUT activity
                    final String bpelString =
                        this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
                    Node extActiv = ModelUtils.string2dom(bpelString);
                    extActiv = context.importNode(extActiv);

                    // insert REST call after the assign
                    final Element afterElement =
                        (Element) invokerReceiveElement.getParentNode().insertBefore(extActiv,
                            assignNode.getNextSibling());

                    if (hasProps) {
                        appendUpdateProperties(context, nodeTemplate, nodeInstanceURLVarName, restCallResponseVarName,
                            afterElement.getParentNode());
                    }
                } catch (final IOException e2) {
                    e2.printStackTrace();
                } catch (final SAXException e) {
                    e.printStackTrace();
                } catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
         * Post Phase code
         */

        if (lastSetState.equals("INITIAL") || lastSetState.equals("CONFIGURED")) {
            try {
                // set state
                String nextState = InstanceStates.getNextStableOperationState(lastSetState);
                // if this node never was handled by lifecycle ops we just set
                // it to started
                if (operationNames.isEmpty()) {
                    nextState = "STARTED";
                }
                final BPELProcessFragments frag = new BPELProcessFragments();
                Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignFinalNodeState"
                    + System.currentTimeMillis(), "string('" + nextState + "')", stateVarName);
                assignNode = context.importNode(assignNode);

                // create PUT activity
                final String bpelString =
                    this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
                Node extActiv = ModelUtils.string2dom(bpelString);
                extActiv = context.importNode(extActiv);

                context.getPostPhaseElement().appendChild(assignNode);
                context.getPostPhaseElement().appendChild(extActiv);
            } catch (final IOException e2) {
                e2.printStackTrace();
            } catch (final SAXException e) {
                e.printStackTrace();
            } catch (final ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        // needs property update only if the node has properties
        if (hasProps) {
            final Element postPhaseElement = context.getPostPhaseElement();
            // make a GET on the nodeInstance properties
            appendUpdateProperties(context, nodeTemplate, nodeInstanceURLVarName, restCallResponseVarName,
                postPhaseElement);
        }

        // add progression log message
        appendProgressionUpdateLogMessage(context, nodeTemplate.getId());

        this.appendFailedStateToFaultHandler(context, nodeInstanceURLVarName);

        return true;
    }

    /**
     * Appends BPEL Code that updates InstanceData for the given NodeTemplate.
     *
     * @param context      the TemplateContext of the NodeTemplate
     * @param nodeTemplate the NodeTemplate to handle
     * @return true iff appending all BPEL code was successful
     */
    public boolean handleUpgrade(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {

        final boolean hasProps = checkProperties(nodeTemplate.getProperties());

        final String serviceInstanceVarName = context.getServiceInstanceURLVarName();
        if (serviceInstanceVarName == null) {
            return false;
        }

        final String serviceInstanceIDVarName = context.getServiceInstanceIDVarName();
        if (serviceInstanceIDVarName == null) {
            return false;
        }

        final String serviceTemplateUrlVarName = context.getServiceTemplateURLVar();
        if (serviceTemplateUrlVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = createStateVar(context, context.getNodeTemplate().getId());

        if (stateVarName == null) {
            return false;
        }

        String nodeInstanceURLVarName = "";

        if (context.findInstanceURLVar(context.getNodeTemplate().getId(), true) == null) {
            // generate String var for nodeInstance URL
            nodeInstanceURLVarName = createInstanceURLVar(context, context.getNodeTemplate().getId());
        } else {
            nodeInstanceURLVarName = context.findInstanceURLVar(context.getNodeTemplate().getId(), true);
        }

        if (nodeInstanceURLVarName == null) {
            return false;
        }

        String lastSetState = "UPDATING";

        this.appendStateUpdateToPrePhase(context, nodeInstanceURLVarName, stateVarName, lastSetState);

        // needs property update only if the node has properties
        if (hasProps) {
            final Element postPhaseElement = context.getPostPhaseElement();
            // make a GET on the nodeInstance properties
            appendUpdateProperties(context, nodeTemplate, nodeInstanceURLVarName, restCallResponseVarName,
                postPhaseElement);
        }
        lastSetState = "UPDATED";

        this.appendStateUpdateToPostPhase(context, nodeInstanceURLVarName, stateVarName, lastSetState);
        this.appendFailedStateToFaultHandler(context, nodeInstanceURLVarName);

        return true;
    }

    private void appendGetStateToPostPhase(BPELPlanContext context, String instanceUrlVar, String stateVarName) {
        this.appendGetStateToElement(context, instanceUrlVar, stateVarName, context.getPostPhaseElement());
    }

    private void appendGetStateToElement(BPELPlanContext context, String instanceURLVar, String stateVarName, Element toAppendAsChild) {
        try {

            Node getStateNode =
                this.fragments.generateBPEL4RESTLightGETInstanceStateAsNode(instanceURLVar, stateVarName);
            getStateNode = context.importNode(getStateNode);
            toAppendAsChild.appendChild(getStateNode);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }
    }

    private void appendFailedStateToFaultHandler(BPELPlanContext context, String nodeInstanceURLVarName) {
        String stateVarName = this.createStateVar(context, context.getTemplateId());
        this.appendStateUpdateAsChild(context, nodeInstanceURLVarName, stateVarName, "ERROR", context.getProvisioningFaultHandlerPhaseElement());
    }

    private void appendStateUpdateToPostPhase(BPELPlanContext context, String nodeInstanceURLVarName,
                                              String stateVarName) {
        this.appendStateUpdateFromVarToElement(context, nodeInstanceURLVarName, stateVarName, context.getPostPhaseElement());
    }

    private void appendStateUpdateFromVarToElement(BPELPlanContext context, String instanceUrlVar, String stateVarName, Element element) {
        try {

            // send state to api
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(instanceUrlVar, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);
            element.appendChild(extActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void appendStateUpdateToPostPhase(BPELPlanContext context, String nodeRelationInstanceURLVarName,
                                              String stateVarName, String stateToSet) {
        this.appendStateUpdateAsChild(context, nodeRelationInstanceURLVarName, stateVarName, stateToSet, context.getPostPhaseElement());
    }

    private void appendStateUpdateToPrePhase(BPELPlanContext context, String nodeInstanceURLVarName,
                                             String stateVarName, String stateToSet) {
        this.appendStateUpdateAsChild(context, nodeInstanceURLVarName, stateVarName, stateToSet, context.getPrePhaseElement());
    }

    private void appendStateUpdateAsChild(BPELPlanContext context, String nodeRelationInstanceURLVarName,
                                          String stateVarName, String stateToSet, Element parentElement) {
        try {
            // update state variable to uninstalled
            final BPELProcessFragments frag = new BPELProcessFragments();
            Node assignNode =
                frag.createAssignXpathQueryToStringVarFragmentAsNode("assignSetNodeState" + System.currentTimeMillis(),
                    "string('" + stateToSet + "')", stateVarName);
            assignNode = context.importNode(assignNode);
            parentElement.appendChild(assignNode);

            // send state to api
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeRelationInstanceURLVarName, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);
            parentElement.appendChild(extActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void appendProgressionUpdateLogMessage(final BPELPlanContext context, final String templateId) {

        final int topologySize = context.getNodeTemplates().size() + context.getRelationshipTemplates().size();

        final String message = "Finished with " + templateId + " of overall topology with steps of " + topologySize;

        this.invoker.addLogActivity(context, message, PlanContext.Phase.POST);
    }

    public boolean appendUpdatePropertiesFromSourceToTarget(final BPELPlanContext sourceNodeContext, BPELPlanContext targetNodeContext,
                                                            final TNodeTemplate nodeTemplate,
                                                            final String sourceNodeInstanceURLVarName,
                                                            final String targetNodeInstanceUrlVarName,
                                                            final String restCallResponseVarName,
                                                            final Node appendAsChildElement) {
        try {
            // fetch properties
            Node nodeInstancePropsGETNode =
                this.fragments.generateInstancePropertiesGETAsNode(sourceNodeInstanceURLVarName,
                    restCallResponseVarName);
            nodeInstancePropsGETNode = sourceNodeContext.importNode(nodeInstancePropsGETNode);
            appendAsChildElement.appendChild(nodeInstancePropsGETNode);
        } catch (final SAXException e1) {
            e1.printStackTrace();
            return false;
        } catch (final IOException e1) {
            e1.printStackTrace();
            return false;
        }

        // assign the values from the property variables into REST/HTTP
        // Request
        // and send
        // first build a mapping from property variable names to dom element
        final Map<String, QName> propertyVarNameToDOMMapping =
            buildMappingsFromVarNameToDomElement(sourceNodeContext, nodeTemplate);
        try {
            // then generate an assign to have code that writes the runtime
            // values into the instance data db.
            // we use the restCallResponseVarName from the GET before, as it
            // has
            // proper format
            Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
                propertyVarNameToDOMMapping);
            assignNode = targetNodeContext.importNode(assignNode);
            appendAsChildElement.appendChild(assignNode);
        } catch (final SAXException e) {
            e.printStackTrace();
            return false;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }

        final Map<String, String> propName2BpelVarNameMap = new HashMap<>();

        Map<String, String> propertiesMap = ModelUtils.asMap(targetNodeContext.getNodeTemplate().getProperties());

        for (PropertyVariable var : targetNodeContext.getPropertyVariables(targetNodeContext.getNodeTemplate())) {
            if (propertiesMap.containsKey(var.getPropertyName())) {
                propName2BpelVarNameMap.put(var.getPropertyName(), var.getVariableName());
            }
        }

        try {
            Node assignPropertiesToVariables =
                this.fragments.createAssignFromInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                    + System.currentTimeMillis(), restCallResponseVarName, propName2BpelVarNameMap, ModelUtils.getNamespace(targetNodeContext.getNodeTemplate().getProperties()));
            assignPropertiesToVariables =
                targetNodeContext.importNode(assignPropertiesToVariables);
            targetNodeContext.getPostPhaseElement().appendChild(assignPropertiesToVariables);
        } catch (final IOException | SAXException e) {
            e.printStackTrace();
            return false;
        }

        // generate BPEL4RESTLight PUT request to update the instance data
        try {
            Node bpel4restPUTNode =
                this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                    targetNodeInstanceUrlVarName);
            bpel4restPUTNode = sourceNodeContext.importNode(bpel4restPUTNode);
            appendAsChildElement.appendChild(bpel4restPUTNode);
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        } catch (final SAXException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean appendUpdateProperties(final BPELPlanContext context, final TNodeTemplate nodeTemplate,
                                          final String nodeInstanceURLVarName, final String restCallResponseVarName,
                                          final Node appendAsChildElement) {
        try {
            // fetch properties
            Node nodeInstancePropsGETNode =
                this.fragments.generateInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
            nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
            appendAsChildElement.appendChild(nodeInstancePropsGETNode);
        } catch (final SAXException e1) {
            e1.printStackTrace();
            return false;
        } catch (final IOException e1) {
            e1.printStackTrace();
            return false;
        }

        // assign the values from the property variables into REST/HTTP
        // Request
        // and send
        // first build a mapping from property variable names to dom element
        final Map<String, QName> propertyVarNameToDOMMapping =
            buildMappingsFromVarNameToDomElement(context, nodeTemplate);
        try {
            // then generate an assign to have code that writes the runtime
            // values into the instance data db.
            // we use the restCallResponseVarName from the GET before, as it
            // has
            // proper format
            Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
                propertyVarNameToDOMMapping);
            assignNode = context.importNode(assignNode);
            appendAsChildElement.appendChild(assignNode);
        } catch (final SAXException e) {
            e.printStackTrace();
            return false;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }

        // generate BPEL4RESTLight PUT request to update the instance data
        try {
            Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                nodeInstanceURLVarName);
            bpel4restPUTNode = context.importNode(bpel4restPUTNode);
            appendAsChildElement.appendChild(bpel4restPUTNode);
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        } catch (final SAXException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean handleUpdate(final BPELPlanContext sourceContext, final BPELPlanContext targetContext,
                                TRelationshipTemplate sourceRelationshipTemplate,
                                TRelationshipTemplate targetRelationshipTemplate) {

        final String targetServiceTemplateUrlVarName = targetContext.getServiceTemplateURLVar();
        final String targetServiceInstanceIdVarName = targetContext.getServiceInstanceIDVarName();

        // create variable for all responses
        final String restCallResponseVarName = createRESTResponseVar(sourceContext);

        final String stateVarName = createStateVar(sourceContext, sourceRelationshipTemplate.getId());

        // find already available instanceIds from the target nodes (source and target of the relation to
        // create
        final String targetServiceRelationSourceNodeInstanceIdVar =
            targetContext.findInstanceIDVar(ModelUtils.getSource(targetRelationshipTemplate, targetContext.getCsar()).getId(), true);
        final String targetServiceRelationTargetNodeInstanceIdVar =
            targetContext.findInstanceIDVar(ModelUtils.getTarget(targetRelationshipTemplate, targetContext.getCsar()).getId(), true);

        // if it is a connect to relation that we migrate, the node instances are already migrated,
        // therefore we can create the new instance in the connect migration scope
        // else only the the target of a hosted/depends on relation is already migrated, we have to wait
        // until it source is migrated. therefore we add the creation to the scope of the relation source
        // (e.g. Raspian-hostedOn->Raspi3 => create code is added to raspbian)
        Element injectionPreElement = null;
        Element injectionPostElement = null;
        if (ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(targetRelationshipTemplate, targetContext.getCsar()), targetContext.getCsar())
            .contains(Types.connectsToRelationType)) {
            injectionPreElement = targetContext.getPrePhaseElement();
            injectionPostElement = targetContext.getPostPhaseElement();
        } else {
            // fetch nodeTemplate
            final TNodeTemplate sourceNodeTemplate = ModelUtils.getSource(sourceRelationshipTemplate, sourceContext.getCsar());
            injectionPreElement = targetContext.createContext(sourceNodeTemplate, ActivityType.MIGRATION).getPostPhaseElement();
            injectionPostElement = targetContext.createContext(sourceNodeTemplate, ActivityType.MIGRATION).getPostPhaseElement();
        }

        // register request message
        final String createRelTInstanceReqVarName = "createRelationshipTemplateRequest" + targetContext.getIdForNames();

        try {
            final Path opentoscaApiSchemaFile = this.bpelFrags.getOpenTOSCAAPISchemaFile();
            QName createRelationshipTemplateInstanceRequestQName =
                this.bpelFrags.getOpenToscaApiCreateRelationshipTemplateInstanceRequestElementQname();
            targetContext.registerType(createRelationshipTemplateInstanceRequestQName, opentoscaApiSchemaFile);
            createRelationshipTemplateInstanceRequestQName =
                targetContext.importQName(createRelationshipTemplateInstanceRequestQName);

            targetContext.addGlobalVariable(createRelTInstanceReqVarName, BPELPlan.VariableType.ELEMENT,
                createRelationshipTemplateInstanceRequestQName);
        } catch (final IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        // assign nodeInstanceId to req message and create relationInstance
        try {
            // create bpel extension activity and append
            final String bpelString =
                this.fragments.generateBPEL4RESTLightRelationInstancePOST(targetServiceTemplateUrlVarName,
                    targetRelationshipTemplate.getId(),
                    createRelTInstanceReqVarName,
                    restCallResponseVarName,
                    targetServiceRelationSourceNodeInstanceIdVar,
                    targetServiceRelationTargetNodeInstanceIdVar, targetServiceInstanceIdVarName);
            Node createRelationInstanceExActiv = ModelUtils.string2dom(bpelString);
            createRelationInstanceExActiv = targetContext.importNode(createRelationInstanceExActiv);
            injectionPreElement.appendChild(createRelationInstanceExActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // store relationinstance url

        final String createdRelationInstanceId =
            targetContext.findInstanceIDVar(targetRelationshipTemplate.getId(), false);
        final String createRelationInstanceUrl =
            targetContext.findInstanceURLVar(targetRelationshipTemplate.getId(), false);
        final String oldRelationInstanceUrl =
            sourceContext.findInstanceURLVar(sourceRelationshipTemplate.getId(), false);

        try {
            // save relationInstance url from response
            final String bpelString =
                this.fragments.generateAssignFromRelationInstancePOSTResponseToStringVar(createRelationInstanceUrl,
                    createdRelationInstanceId,
                    restCallResponseVarName);
            Node assignRelationInstanceUrl = ModelUtils.string2dom(bpelString);
            assignRelationInstanceUrl = targetContext.importNode(assignRelationInstanceUrl);
            injectionPreElement.appendChild(assignRelationInstanceUrl);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* load properties and state from old instance to new instance */
        if (checkProperties(sourceRelationshipTemplate.getProperties())) {
            // make a GET on the nodeInstance properties

            try {
                // fetch properties
                Node nodeInstancePropsGETNode =
                    this.fragments.generateInstancePropertiesGETAsNode(oldRelationInstanceUrl, restCallResponseVarName);
                nodeInstancePropsGETNode = targetContext.importNode(nodeInstancePropsGETNode);
                injectionPostElement.appendChild(nodeInstancePropsGETNode);
            } catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            } catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, QName> propertyVarNameToDOMMapping =
                buildMappingsFromVarNameToDomElement(targetContext, sourceRelationshipTemplate);
            try {
                // then generate an assign to have code that writes the runtime
                // values into the instance data db.
                // we use the restCallResponseVarName from the GET before, as it
                // has
                // proper format
                Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
                    propertyVarNameToDOMMapping);
                assignNode = targetContext.importNode(assignNode);
                injectionPostElement.appendChild(assignNode);
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }

            // generate BPEL4RESTLight PUT request to update the instance data
            try {
                Node bpel4restPUTNode =
                    this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                        createRelationInstanceUrl);
                bpel4restPUTNode = targetContext.importNode(bpel4restPUTNode);
                injectionPostElement.appendChild(bpel4restPUTNode);
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        this.appendGetStateToElement(targetContext, oldRelationInstanceUrl, stateVarName, injectionPostElement);
        this.appendStateUpdateFromVarToElement(targetContext, createRelationInstanceUrl, stateVarName, injectionPostElement);

        /* set state of old instance to migrated */
        this.appendStateUpdateAsChild(targetContext, oldRelationInstanceUrl, stateVarName, "MIGRATED", injectionPostElement);

        return true;
    }

    public boolean handleCreate(final BPELPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {

        final String serviceInstanceVarName = context.getServiceInstanceURLVarName();
        if (serviceInstanceVarName == null) {
            return false;
        }

        final String serviceTemplateUrlVarName = context.getServiceTemplateURLVar();
        if (serviceTemplateUrlVarName == null) {
            return false;
        }

        final String serviceInstanceIdVarName = context.getServiceInstanceIDVarName();
        if (serviceInstanceIdVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = createStateVar(context, context.getRelationshipTemplate().getId());

        if (stateVarName == null) {
            return false;
        }

        // based on the relatioships baseType we add the logic into different
        // phases of relations AND nodes
        // connectsTo = own phases
        // else = source node phasesl

        Element injectionPreElement = null;
        Element injectionPostElement = null;
        final String sourceInstanceVarName =
            context.findInstanceIDVar(ModelUtils.getSource(context.getRelationshipTemplate(), context.getCsar()).getId(), true);
        final String targetInstanceVarName =
            context.findInstanceIDVar(ModelUtils.getTarget(context.getRelationshipTemplate(), context.getCsar()).getId(), true);

        if (ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(context.getRelationshipTemplate(), context.getCsar()), context.getCsar())
            .contains(Types.connectsToRelationType)) {
            injectionPreElement = context.getPrePhaseElement();
            injectionPostElement = context.getPostPhaseElement();
        } else {
            // fetch nodeTemplate
            final TNodeTemplate sourceNodeTemplate = ModelUtils.getSource(context.getRelationshipTemplate(), context.getCsar());
            LOG.debug("Trying to create provisioning plan context for sourceNodeTemplate {} of relationshipTemplate {}", sourceNodeTemplate.toString(), context.getRelationshipTemplate().toString());

            // Right now the knowledge of DEFROST and PROVISIONING activities is to hard of an assumption, if you ask me
            BPELPlanContext sourceContext = context.createContext(sourceNodeTemplate, ActivityType.PROVISIONING, ActivityType.DEFROST);
            if (sourceContext == null) {
                LOG.error("Couldn't create context for sourceNodeTemplate {}", sourceNodeTemplate.toString());
                return false;
            }
            injectionPreElement = sourceContext.getPostPhaseElement();
            injectionPostElement = sourceContext.getPostPhaseElement();
        }

        if (injectionPostElement == null | injectionPreElement == null | sourceInstanceVarName == null
            | targetInstanceVarName == null) {
            return false;
        }

        /*
         * import request message type and create variable
         */

        final String createRelTInstanceReqVarName = "createRelationshipTemplateRequest" + context.getIdForNames();

        try {
            final Path opentoscaApiSchemaFile = this.bpelFrags.getOpenTOSCAAPISchemaFile();
            QName createRelationshipTemplateInstanceRequestQName =
                this.bpelFrags.getOpenToscaApiCreateRelationshipTemplateInstanceRequestElementQname();
            context.registerType(createRelationshipTemplateInstanceRequestQName, opentoscaApiSchemaFile);
            createRelationshipTemplateInstanceRequestQName =
                context.importQName(createRelationshipTemplateInstanceRequestQName);

            context.addGlobalVariable(createRelTInstanceReqVarName, BPELPlan.VariableType.ELEMENT,
                createRelationshipTemplateInstanceRequestQName);
        } catch (final IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        /*
         * (i) append bpel code to create the relationInstance (ii) append bpel code to fetch
         * relationInstanceURL
         */

        try {
            // create bpel extension activity and append
            final String bpelString =
                this.fragments.generateBPEL4RESTLightRelationInstancePOST(serviceTemplateUrlVarName,
                    context.getRelationshipTemplate().getId(),
                    createRelTInstanceReqVarName,
                    restCallResponseVarName,
                    sourceInstanceVarName, targetInstanceVarName, serviceInstanceIdVarName);
            Node createRelationInstanceExActiv = ModelUtils.string2dom(bpelString);
            createRelationInstanceExActiv = context.importNode(createRelationInstanceExActiv);
            injectionPreElement.appendChild(createRelationInstanceExActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // generate String var for relationInstance URL
        String relationInstanceURLVarName = "";
        if (context.findInstanceURLVar(context.getRelationshipTemplate().getId(), false) == null) {
            // generate String var for relationInstance URL
            relationInstanceURLVarName = createInstanceURLVar(context, context.getRelationshipTemplate().getId());
        } else {
            relationInstanceURLVarName = context.findInstanceURLVar(context.getRelationshipTemplate().getId(), false);
        }

        if (relationInstanceURLVarName == null) {
            return false;
        }

        String relationInstanceIDVarName = "";

        if (context.findInstanceIDVar(context.getRelationshipTemplate().getId(), false) == null) {
            // generate String var for nodeInstance URL
            relationInstanceIDVarName = createInstanceIDVar(context, context.getRelationshipTemplate().getId());
        } else {
            relationInstanceIDVarName = context.findInstanceIDVar(context.getRelationshipTemplate().getId(), false);
        }

        if (relationInstanceIDVarName == null) {
            return false;
        }

        try {
            // save relationInstance data from response
            final String bpelString =
                this.fragments.generateAssignFromRelationInstancePOSTResponseToStringVar(relationInstanceURLVarName,
                    relationInstanceIDVarName,
                    restCallResponseVarName);
            Node assignRelationInstanceUrl = ModelUtils.string2dom(bpelString);
            assignRelationInstanceUrl = context.importNode(assignRelationInstanceUrl);
            injectionPreElement.appendChild(assignRelationInstanceUrl);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        final String lastSetState = "INITIAL";

        try {
            // update state variable to uninstalled
            final BPELProcessFragments frag = new BPELProcessFragments();
            Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignInitRelationState"
                + System.currentTimeMillis(), "string('" + lastSetState + "')", stateVarName);
            assignNode = context.importNode(assignNode);
            injectionPreElement.appendChild(assignNode);

            // send state to api
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);
            injectionPreElement.appendChild(extActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            // set state
            final BPELProcessFragments frag = new BPELProcessFragments();
            Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
                "assignFinalNodeState"
                    + System.currentTimeMillis(),
                "string('CREATED')", stateVarName);
            assignNode = context.importNode(assignNode);

            // create PUT activity
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);

            injectionPostElement.appendChild(assignNode);
            injectionPostElement.appendChild(extActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        // needs property update only if the relation has properties
        if (checkProperties(relationshipTemplate.getProperties())) {
            // make a GET on the nodeInstance properties

            try {
                // fetch properties
                Node nodeInstancePropsGETNode =
                    this.fragments.generateInstancePropertiesGETAsNode(relationInstanceURLVarName,
                        restCallResponseVarName);
                nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
                injectionPostElement.appendChild(nodeInstancePropsGETNode);
            } catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            } catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, QName> propertyVarNameToDOMMapping =
                buildMappingsFromVarNameToDomElement(context, relationshipTemplate);
            try {
                // then generate an assign to have code that writes the runtime
                // values into the instance data db.
                // we use the restCallResponseVarName from the GET before, as it
                // has
                // proper format
                Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
                    propertyVarNameToDOMMapping);
                assignNode = context.importNode(assignNode);
                injectionPostElement.appendChild(assignNode);
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }

            // generate BPEL4RESTLight PUT request to update the instance data
            try {
                Node bpel4restPUTNode =
                    this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                        relationInstanceURLVarName);
                bpel4restPUTNode = context.importNode(bpel4restPUTNode);
                injectionPostElement.appendChild(bpel4restPUTNode);
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        appendProgressionUpdateLogMessage(context, relationshipTemplate.getId());

        return true;
    }

    private Element fetchInvokerReceive(final Element invokerAssign, final String requestVarName) {

        Node sibling = invokerAssign.getNextSibling();

        while (sibling != null & !sibling.getNodeName().contains("invoke")) {
            sibling = sibling.getNextSibling();
        }

        if (sibling.getNodeType() == Node.ELEMENT_NODE
            & sibling.getAttributes().getNamedItem("inputVariable").getTextContent().equals(requestVarName)) {
            return (Element) sibling.getNextSibling();
        }

        return null;
    }

    private String fetchRequestVarNameFromInvokerAssign(final Element assignContentElement) {
        String reqVarName = null;

        final Node fromNode = fetchFromNode(assignContentElement);

        final Node toNode = fetchNextNamedNodeRecursively(fromNode, "to");

        reqVarName = toNode.getAttributes().getNamedItem("variable").getTextContent();

        return reqVarName;
    }

    private Node fetchNextNamedNodeRecursively(final Node node, final String name) {
        Node sibling = node.getNextSibling();

        while (sibling != null & !sibling.getNodeName().contains(name)) {
            sibling = sibling.getNextSibling();
        }

        return sibling;
    }

    private Node fetchFromNode(final Element assignContentElement) {
        Node parent = assignContentElement.getParentNode();

        while (parent != null & !parent.getNodeName().contains("from")) {
            parent = parent.getParentNode();
        }

        return parent;
    }

    private String fetchOperationName(final Element assignElement) {
        final XPath xpath = this.xPathfactory.newXPath();
        String operationName = null;

        try {

            operationName = (String) xpath.evaluate(".//*[local-name()='OperationName']/node()", assignElement,
                XPathConstants.STRING);
        } catch (final XPathExpressionException e) {
            e.printStackTrace();
        }

        return operationName;
    }

    private List<Element> fetchInvokerCallAssigns(final Element provisioningPhaseElement) {
        final XPath xpath = this.xPathfactory.newXPath();
        final List<Element> assignElements = new ArrayList<>();
        final String xpathQuery = ".//*[local-name()='invokeOperationAsync']";
        try {
            final NodeList nodeList =
                (NodeList) xpath.evaluate(xpathQuery, provisioningPhaseElement, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    assignElements.add((Element) nodeList.item(i));
                }
            }
        } catch (final XPathExpressionException e) {
            e.printStackTrace();
        }

        return assignElements;
    }

    /**
     * <p>
     * This method is initializing a Map from BpelVariableName to a DomElement of the given Properties and Context.
     * </p>
     *
     * @param context      BPELPlanContext
     * @param nodeTemplate nodeTemplate of the context
     * @return a Map<String,Node> of BpelVariableName to DOM Node. Maybe null if the mapping is not complete, e.g. some
     * bpel variable was not found or the properties weren't parsed right.
     */
    private Map<String, QName> buildMappingsFromVarNameToDomElement(final PlanContext context,
                                                                    TNodeTemplate nodeTemplate) {
        final Map<String, String> propertiesMap = ModelUtils.asMap(nodeTemplate.getProperties());
        final Map<String, QName> mapping = new HashMap<>();

        for (String propertyName : propertiesMap.keySet()) {
            final String propVarName = context.getVariableNameOfProperty(nodeTemplate, propertyName);
            mapping.put(propVarName, new QName(ModelUtils.getNamespace(nodeTemplate.getProperties()), propertyName));
        }

        return mapping;
    }

    private Map<String, QName> buildMappingsFromVarNameToDomElement(final PlanContext context,
                                                                    TRelationshipTemplate relationshipTemplate) {
        final Map<String, String> propertiesMap = ModelUtils.asMap(relationshipTemplate.getProperties());
        final Map<String, QName> mapping = new HashMap<>();

        for (String propertyName : propertiesMap.keySet()) {
            final String propVarName = context.getVariableNameOfProperty(relationshipTemplate, propertyName);
            mapping.put(propVarName, new QName(ModelUtils.getNamespace(relationshipTemplate.getProperties()), propertyName));
        }

        return mapping;
    }

    /**
     * <p>
     * Checks the given AbstractProperties against following criteria: Nullpointer-Check for properties itself and its
     * given DOM Element, followed by whether the dom element has any child elements (if not, we have no
     * properties/bpel-variables defined)
     * </p>
     *
     * @param properties AbstractProperties of an TNodeTemplate or TRelationshipTemplate
     * @return true iff properties and properties.getDomElement() != null and DomElement.hasChildNodes() == true
     */
    private boolean checkProperties(final TEntityTemplate.Properties properties) {
        if (properties == null) {
            return false;
        }

        return !ModelUtils.asMap(properties).isEmpty();
    }

    public boolean handlePasswordCheck(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {

        // find properties which store passwords
        // find their variables
        final Collection<Variable> pwVariables = new ArrayList<>();
        final Collection<PropertyVariable> nodePropertyVariables = context.getPropertyVariables(nodeTemplate);

        for (final Variable var : nodePropertyVariables) {
            if (var.getVariableName().contains("Password")) {
                pwVariables.add(var);
            }
        }

        // find runScript method

        final TNodeTemplate node = findRunScriptNode(nodeTemplate, context.getCsar());

        if (node == null) {
            return false;
        }

        final Map<TParameter, Variable> inputParams = new HashMap<>();

        final String cmdStringName = "checkPasswordScript_" + nodeTemplate.getId() + "_" + System.currentTimeMillis();
        final String cmdStringVal = createPlaceHolderPwCheckCmdString(pwVariables);
        final Variable cmdVar = context.createGlobalStringVariable(cmdStringName, cmdStringVal);

        final String xPathReplacementCmd = createPlaceholderReplaceingXPath(cmdVar.getVariableName(), pwVariables);

        try {
            Node assignPlaceholder =
                this.bpelFrags.createAssignXpathQueryToStringVarFragmentAsNode("replacePlaceholdersOfPWCheck"
                    + System.currentTimeMillis(), xPathReplacementCmd, cmdVar.getVariableName());
            assignPlaceholder = context.importNode(assignPlaceholder);
            context.getPrePhaseElement().appendChild(assignPlaceholder);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        inputParams.put(new TParameter() {

            @Override
            public boolean getRequired() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public String getType() {
                // TODO Auto-generated method stub
                return "xs:String";
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return "Script";
            }
        }, cmdVar);

        final Map<TParameter, Variable> outputParams = new HashMap<>();

        final String outputVarName = "pwCheckResult" + System.currentTimeMillis();

        final Variable outputVar = context.createGlobalStringVariable(outputVarName, "");

        outputParams.put(new TParameter() {

            @Override
            public boolean getRequired() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public String getType() {
                // TODO Auto-generated method stub
                return "xs:String";
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return "ScriptResult";
            }
        }, outputVar);

        // generate call to method
        context.executeOperation(node, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT, inputParams,
            outputParams, BPELScopePhaseType.PRE, context.getPrePhaseElement());

        // check result and eventually throw error
        Variable faultVariable = context.createGlobalStringVariable("checkPasswordFaultVariable" + context.getIdForNames(), "Could'nt hold policy strong password");
        Node ifTrueThrowError =
            this.bpelFrags.createIfTrueThrowsError("contains($" + outputVar.getVariableName() + ",'false')",
                new QName("http://opentosca.org/plans/faults", "PasswordWeak"), faultVariable.getVariableName());
        ifTrueThrowError = context.importNode(ifTrueThrowError);
        context.getPrePhaseElement().appendChild(ifTrueThrowError);

        return true;
    }

    private String createPlaceholderReplaceingXPath(final String cmdStringName,
                                                    final Collection<Variable> pwVariables) {
        String xpath = "$" + cmdStringName + ",";

        for (final Variable var : pwVariables) {
            xpath = "replace(" + xpath;
            xpath += "'" + var.getVariableName() + "'," + "$" + var.getVariableName() + ")";
        }

        return xpath;
    }

    private String createPlaceHolderPwCheckCmdString(final Collection<Variable> pwVariables) {
        /*
         * if echo "$candidate_password" | grep -Eq "$strong_pw_regex"; then echo strong else echo weak fi
         */
        String cmdString = "";

        for (final Variable var : pwVariables) {
            cmdString += "if echo \"" + var.getVariableName()
                + "\" | grep -Eq \"(?=^.{8,255}$)((?=.*\\d)(?!.*\\s)(?=.*[A-Z])(?=.*[a-z]))^.*\"; then : else echo \"false\" fi;";
        }

        return cmdString;
    }

    protected TNodeTemplate findRunScriptNode(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TNodeTemplate> infraNodes = new ArrayList<>();

        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, csar);

        for (final TNodeTemplate node : infraNodes) {
            for (final TInterface iface : ModelUtils.findNodeType(node, csar).getInterfaces()) {
                if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)
                    | iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER)) {
                    for (final TOperation op : iface.getOperations()) {
                        if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
                            | op.getName()
                            .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
    }
}
