package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.postphase.plugin.instancedata.bpel.Fragments;
import org.opentosca.planbuilder.postphase.plugin.instancedata.core.InstanceStates;
import org.opentosca.planbuilder.postphase.plugin.instancedata.core.handler.InstanceDataHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all logic to append BPEL code which updates the InstanceData of a
 * NodeTemplate
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELHandler implements InstanceDataHandler<BPELPlanContext> {

    private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";

    private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";
    private Fragments fragments;
    private final XPathFactory xPathfactory = XPathFactory.newInstance();;

    public BPELHandler() {

        try {
            this.fragments = new Fragments();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * This method is initializing a Map from BpelVariableName to a DomElement of the given Properties
     * and Context.
     * </p>
     *
     * @param context BPELPlanContext
     * @param properties AbstractProperties with proper DOM Element
     * @return a Map<String,Node> of BpelVariableName to DOM Node. Maybe null if the mapping is not
     *         complete, e.g. some bpel variable was not found or the properties weren't parsed right.
     */
    private Map<String, Node> buildMappingsFromVarNameToDomElement(final BPELPlanContext context,
                                                                   final AbstractProperties properties) {
        final Element propRootElement = properties.getDOMElement();

        final Map<String, Node> mapping = new HashMap<>();

        // get list of child elements
        final NodeList childList = propRootElement.getChildNodes();

        for (int i = 0; i < childList.getLength(); i++) {
            final Node child = childList.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                final String propertyName = child.getLocalName();
                final String propVarName = context.getVarNameOfTemplateProperty(propertyName);
                mapping.put(propVarName, child);
            }

        }
        return mapping;
    }

    /**
     * <p>
     * Checks the given AbstractProperties against following criteria: Nullpointer-Check for properties
     * itself and its given DOM Element, followed by whether the dom element has any child elements (if
     * not, we have no properties/bpel-variables defined)
     * </p>
     *
     * @param properties AbstractProperties of an AbstractNodeTemplate or AbstractRelationshipTemplate
     * @return true iff properties and properties.getDomElement() != null and DomElement.hasChildNodes()
     *         == true
     */
    private boolean checkProperties(final AbstractProperties properties) {
        if (properties == null) {
            return false;
        }

        if (properties.getDOMElement() == null) {
            return false;
        }

        final Element propertiesRootElement = properties.getDOMElement();

        if (!propertiesRootElement.hasChildNodes()) {
            return false;
        }

        return true;
    }

    private String createInstanceVar(final BPELPlanContext context, final String templateId) {
        final String instanceURLVarName = (context.getRelationshipTemplate() == null ? "node" : "relationship")
            + "InstanceURL_" + templateId + "_" + context.getIdForNames();
        final QName stringTypeDeclId =
            context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        if (!context.addGlobalVariable(instanceURLVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
            return null;
        }

        return instanceURLVarName;
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
        final String stateVarName = templateId + "_state_" + context.getIdForNames();
        final QName stringTypeDeclId =
            context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        if (!context.addGlobalVariable(stateVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
            return null;
        }

        return stateVarName;
    }

    private Node fetchFromNode(final Element assignContentElement) {
        Node parent = assignContentElement.getParentNode();

        while (parent != null & !parent.getNodeName().contains("from")) {
            parent = parent.getParentNode();
        }

        return parent;
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

        }
        catch (final XPathExpressionException e) {
            e.printStackTrace();
        }

        return assignElements;
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

    private Node fetchNextNamedNodeRecursively(final Node node, final String name) {
        Node sibling = node.getNextSibling();

        while (sibling != null & !sibling.getNodeName().contains(name)) {
            sibling = sibling.getNextSibling();
        }

        return sibling;
    }

    private String fetchOperationName(final Element assignElement) {
        final XPath xpath = this.xPathfactory.newXPath();
        String operationName = null;

        try {

            operationName = (String) xpath.evaluate(".//*[local-name()='OperationName']/node()", assignElement,
                                                    XPathConstants.STRING);
        }
        catch (final XPathExpressionException e) {
            e.printStackTrace();
        }

        return operationName;
    }

    private String fetchRequestVarNameFromInvokerAssign(final Element assignContentElement) {
        String reqVarName = null;

        final Node fromNode = this.fetchFromNode(assignContentElement);

        final Node toNode = this.fetchNextNamedNodeRecursively(fromNode, "to");

        reqVarName = toNode.getAttributes().getNamedItem("variable").getTextContent();

        return reqVarName;
    }

    private String findInstanceVar(final BPELPlanContext context, final String templateId, final boolean isNode) {
        final String instanceURLVarName = (isNode ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
        for (final String varName : context.getMainVariableNames()) {
            if (varName.contains(instanceURLVarName)) {
                return varName;
            }
        }
        return null;
    }

    private String getServiceInstanceVarName(final BPELPlanContext context) {
        // check whether main sequence already contains service instance calls
        // to container API
        final List<String> mainVarNames = context.getMainVariableNames();
        String serviceInstanceVarName = null;
        String instanceDataUrlVarName = null;
        for (final String varName : mainVarNames) {
            // pretty lame but should work
            if (varName.contains(BPELHandler.ServiceInstanceVarKeyword)) {
                serviceInstanceVarName = varName;
            }
            if (varName.contains(BPELHandler.InstanceDataAPIUrlKeyword)) {
                instanceDataUrlVarName = varName;
            }
        }

        // if at least one is null we need to init the whole

        if (instanceDataUrlVarName == null) {
            return null;
        }

        if (serviceInstanceVarName == null) {
            return null;
        }
        return serviceInstanceVarName;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractRelationshipTemplate relationshipTemplate) {

        final String serviceInstanceVarName = this.getServiceInstanceVarName(context);
        if (serviceInstanceVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = this.createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = this.createStateVar(context, context.getRelationshipTemplate().getId());

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
            this.findInstanceVar(context, context.getRelationshipTemplate().getSource().getId(), true);
        final String targetInstanceVarName =
            this.findInstanceVar(context, context.getRelationshipTemplate().getTarget().getId(), true);

        if (ModelUtils.getRelationshipTypeHierarchy(context.getRelationshipTemplate().getRelationshipType())
                      .contains(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
            injectionPreElement = context.getPrePhaseElement();
            injectionPostElement = context.getPostPhaseElement();
        } else {
            // fetch nodeTemplate
            final AbstractNodeTemplate sourceNodeTemplate = context.getRelationshipTemplate().getSource();
            injectionPreElement = context.createContext(sourceNodeTemplate).getPrePhaseElement();
            injectionPostElement = context.createContext(sourceNodeTemplate).getPostPhaseElement();
        }

        if (injectionPostElement == null | injectionPreElement == null | sourceInstanceVarName == null
            | targetInstanceVarName == null) {
            return false;
        }

        /*
         * (i) append bpel code to create the nodeInstance (ii) append bpel code to fetch nodeInstanceURL
         */

        try {
            // create bpel extension activity and append
            final String bpelString =
                this.fragments.generateBPEL4RESTLightRelationInstancePOST(serviceInstanceVarName,
                                                                          context.getRelationshipTemplate().getId(),
                                                                          restCallResponseVarName,
                                                                          sourceInstanceVarName, targetInstanceVarName);
            Node createRelationInstanceExActiv = ModelUtils.string2dom(bpelString);
            createRelationInstanceExActiv = context.importNode(createRelationInstanceExActiv);
            injectionPreElement.appendChild(createRelationInstanceExActiv);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        // generate String var for relationInstance URL
        String relationInstanceURLVarName = "";

        if (this.findInstanceVar(context, context.getRelationshipTemplate().getId(), false) == null) {
            // generate String var for nodeInstance URL
            relationInstanceURLVarName = this.createInstanceVar(context, context.getRelationshipTemplate().getId());
        } else {
            relationInstanceURLVarName =
                this.findInstanceVar(context, context.getRelationshipTemplate().getId(), false);
        }

        if (relationInstanceURLVarName == null) {
            return false;
        }

        try {
            // save relationInstance url from response
            final String bpelString =
                this.fragments.generateAssignFromRelationInstancePOSTResponseToStringVar(relationInstanceURLVarName,
                                                                                         restCallResponseVarName);
            Node assignRelationInstanceUrl = ModelUtils.string2dom(bpelString);
            assignRelationInstanceUrl = context.importNode(assignRelationInstanceUrl);
            injectionPreElement.appendChild(assignRelationInstanceUrl);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        final String lastSetState = "initial";

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
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            // set state
            final BPELProcessFragments frag = new BPELProcessFragments();
            Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignFinalNodeState"
                + System.currentTimeMillis(), "string('initialized')", stateVarName);
            assignNode = context.importNode(assignNode);

            // create PUT activity
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);

            injectionPostElement.appendChild(assignNode);
            injectionPostElement.appendChild(extActiv);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        // needs property update only if the relation has properties
        if (this.checkProperties(relationshipTemplate.getProperties())) {
            // make a GET on the nodeInstance properties

            try {
                // fetch properties
                Node nodeInstancePropsGETNode =
                    this.fragments.generateInstancePropertiesGETAsNode(relationInstanceURLVarName,
                                                                       restCallResponseVarName);
                nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
                injectionPostElement.appendChild(nodeInstancePropsGETNode);
            }
            catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            }
            catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, Node> propertyVarNameToDOMMapping =
                this.buildMappingsFromVarNameToDomElement(context, relationshipTemplate.getProperties());
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
            }
            catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
            catch (final IOException e) {
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
            }
            catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
            catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Appends BPEL Code that updates InstanceData for the given NodeTemplate. Needs initialization code
     * on the global level in the plan. This will be checked and appended if needed.
     *
     * @param context the TemplateContext of the NodeTemplate
     * @param nodeTemplate the NodeTemplate to handle
     * @return true iff appending all BPEL code was successful
     */
    @Override
    public boolean handleBuild(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        final boolean hasProps = this.checkProperties(nodeTemplate.getProperties());

        final String serviceInstanceVarName = this.getServiceInstanceVarName(context);
        if (serviceInstanceVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = this.createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = this.createStateVar(context, context.getNodeTemplate().getId());

        if (stateVarName == null) {
            return false;
        }

        /*
         * (i) append bpel code to create the nodeInstance (ii) append bpel code to fetch nodeInstanceURL
         */

        try {
            // create bpel extension activity and append
            final String bpelString =
                this.fragments.generateBPEL4RESTLightNodeInstancePOST(serviceInstanceVarName,
                                                                      context.getNodeTemplate().getId(),
                                                                      restCallResponseVarName);
            Node createNodeInstanceExActiv = ModelUtils.string2dom(bpelString);
            createNodeInstanceExActiv = context.importNode(createNodeInstanceExActiv);
            context.getPrePhaseElement().appendChild(createNodeInstanceExActiv);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        String nodeInstanceURLVarName = "";

        if (this.findInstanceVar(context, context.getNodeTemplate().getId(), true) == null) {
            // generate String var for nodeInstance URL
            nodeInstanceURLVarName = this.createInstanceVar(context, context.getNodeTemplate().getId());
        } else {
            nodeInstanceURLVarName = this.findInstanceVar(context, context.getNodeTemplate().getId(), true);
        }

        if (nodeInstanceURLVarName == null) {
            return false;
        }

        try {
            // save nodeInstance url from response
            final String bpelString =
                this.fragments.generateAssignFromNodeInstancePOSTResponseToStringVar(nodeInstanceURLVarName,
                                                                                     restCallResponseVarName);
            Node assignNodeInstanceUrl = ModelUtils.string2dom(bpelString);
            assignNodeInstanceUrl = context.importNode(assignNodeInstanceUrl);
            context.getPrePhaseElement().appendChild(assignNodeInstanceUrl);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            // update state variable to uninstalled
            final BPELProcessFragments frag = new BPELProcessFragments();
            Node assignNode =
                frag.createAssignXpathQueryToStringVarFragmentAsNode("assignInitNodeState" + System.currentTimeMillis(),
                                                                     "string('initial')", stateVarName);
            assignNode = context.importNode(assignNode);
            context.getPrePhaseElement().appendChild(assignNode);

            // send state to api
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);
            context.getPrePhaseElement().appendChild(extActiv);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        String lastSetState = "initial";

        /*
         * Prov Phase code
         */

        // fetch all assigns that assign an invoke async operation request

        final Element provisioningPhaseElement = context.getProvisioningPhaseElement();
        final List<Element> assignContentElements = this.fetchInvokerCallAssigns(provisioningPhaseElement);

        final List<String> operationNames = new ArrayList<>();

        // for each assign element we fetch the operation name, determine the
        // pre and post states, and append the pre state before the found assign
        // and the post state after the receive of the invoker iteraction
        for (final Element assignContentElement : assignContentElements) {

            // fetch operationName from literal contents
            final String operationName = this.fetchOperationName(assignContentElement);
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
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
                catch (final SAXException e) {
                    e.printStackTrace();
                }
                catch (final ParserConfigurationException e) {
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
                    final String reqVarName = this.fetchRequestVarNameFromInvokerAssign(assignContentElement);

                    // from the assign element search for the receive element
                    // that is witing for the response
                    final Element invokerReceiveElement =
                        this.fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

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

                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
                catch (final SAXException e) {
                    e.printStackTrace();
                }
                catch (final ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }

        }

        /*
         * Post Phase code
         */

        if (lastSetState.equals("initial")) {
            try {
                // set state
                String nextState = InstanceStates.getNextStableOperationState(lastSetState);
                // if this node never was handled by lifecycle ops we just set it to started
                if (operationNames.isEmpty()) {
                    nextState = "started";
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
            }
            catch (final IOException e2) {
                e2.printStackTrace();
            }
            catch (final SAXException e) {
                e.printStackTrace();
            }
            catch (final ParserConfigurationException e) {
                e.printStackTrace();
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
            }
            catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            }
            catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, Node> propertyVarNameToDOMMapping =
                this.buildMappingsFromVarNameToDomElement(context, nodeTemplate.getProperties());
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
            }
            catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
            catch (final IOException e) {
                e.printStackTrace();
                return false;
            }

            // generate BPEL4RESTLight PUT request to update the instance data
            try {
                Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                                                                                                nodeInstanceURLVarName);
                bpel4restPUTNode = context.importNode(bpel4restPUTNode);
                context.getPostPhaseElement().appendChild(bpel4restPUTNode);
            }
            catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
            catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handleTerminate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        final boolean hasProps = this.checkProperties(nodeTemplate.getProperties());

        final String serviceInstanceVarName = this.getServiceInstanceVarName(context);
        if (serviceInstanceVarName == null) {
            return false;
        }

        /*
         * Pre Phase code
         */

        // create variable for all responses
        final String restCallResponseVarName = this.createRESTResponseVar(context);

        if (restCallResponseVarName == null) {
            return false;
        }

        // create state variable inside scope
        final String stateVarName = this.createStateVar(context, context.getNodeTemplate().getId());

        if (stateVarName == null) {
            return false;
        }

        String nodeInstanceURLVarName = "";

        if (this.findInstanceVar(context, context.getNodeTemplate().getId(), true) == null) {
            // generate String var for nodeInstance URL
            nodeInstanceURLVarName = this.createInstanceVar(context, context.getNodeTemplate().getId());
        } else {
            nodeInstanceURLVarName = this.findInstanceVar(context, context.getNodeTemplate().getId(), true);
        }

        if (nodeInstanceURLVarName == null) {
            return false;
        }

        // we'll use this later when we determine that the handle Node doesn't
        // have lifecycle operations. Without this check all nodes without
        // lifecycle (or cloud prov operations) will be in an uninstalled state
        String lastSetState = "deleted";

        /*
         * Prov Phase code
         */

        // fetch all assigns that assign an invoke async operation request

        final Element provisioningPhaseElement = context.getProvisioningPhaseElement();
        final List<Element> assignContentElements = this.fetchInvokerCallAssigns(provisioningPhaseElement);

        // for each assign element we fetch the operation name, determine the
        // pre and post states, and append the pre state before the found assign
        // and the post state after the receive of the invoker iteraction
        for (final Element assignContentElement : assignContentElements) {

            // fetch operationName from literal contents
            final String operationName = this.fetchOperationName(assignContentElement);
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
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
                catch (final SAXException e) {
                    e.printStackTrace();
                }
                catch (final ParserConfigurationException e) {
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
                    final String reqVarName = this.fetchRequestVarNameFromInvokerAssign(assignContentElement);

                    // from the assign element search for the receive element
                    // that is witing for the response
                    final Element invokerReceiveElement =
                        this.fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

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

                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
                catch (final SAXException e) {
                    e.printStackTrace();
                }
                catch (final ParserConfigurationException e) {
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
            }
            catch (final SAXException e1) {
                e1.printStackTrace();
                return false;
            }
            catch (final IOException e1) {
                e1.printStackTrace();
                return false;
            }

            // assign the values from the property variables into REST/HTTP
            // Request
            // and send
            // first build a mapping from property variable names to dom element
            final Map<String, Node> propertyVarNameToDOMMapping =
                this.buildMappingsFromVarNameToDomElement(context, nodeTemplate.getProperties());
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
            }
            catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
            catch (final IOException e) {
                e.printStackTrace();
                return false;
            }

            // generate BPEL4RESTLight PUT request to update the instance data
            try {
                Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
                                                                                                nodeInstanceURLVarName);
                bpel4restPUTNode = context.importNode(bpel4restPUTNode);
                context.getPostPhaseElement().appendChild(bpel4restPUTNode);
            }
            catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
            catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        // try {
        // Node deleteNode =
        // this.fragments.createRESTDeleteOnURLBPELVarAsNode(nodeInstanceURLVarName,
        // restCallResponseVarName);
        //
        // deleteNode = context.importNode(deleteNode);
        //
        // context.getPostPhaseElement().appendChild(deleteNode);
        //
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // return false;
        // } catch (SAXException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        return true;
    }
}
