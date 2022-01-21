package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
@Component
public class BPMNProcessFragments {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNProcessFragments.class);

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public BPMNProcessFragments() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public Node createBPMNScopeAsNode(BPMNScope bpmnScope) {
        LOG.debug("Creating BPMNScope as Node: {} with type: {}", bpmnScope.getId(), bpmnScope.getBpmnScopeType().name());
        try {
            // TODO: consider change to switch
            if (bpmnScope.getBpmnScopeType() == BPMNScopeType.SEQUENCE_FLOW) {
                return this.createBPMNSequenceFlowAsNode(bpmnScope);
            } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.SUBPROCESS) {
                return this.createBPMNSubprocess(bpmnScope);
            } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.START_EVENT) {
                return this.createBPMNStartEventAsNode(bpmnScope);
            } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.END_EVENT) {
                return this.createBPMNEndEventAsNode(bpmnScope);
            } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.CREATE_ST_INSTANCE) {
                return this.createServiceTemplateInstanceAsNode(bpmnScope);
            } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.SET_ST_STATE) {
                return this.createSetServiceTemplateStateAsNode(bpmnScope);
            } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.CREATE_RT_INSTANCE) {
                return this.createRelationshipTemplateInstanceAsNode(bpmnScope);
            }
        } catch (Exception e) {
            LOG.debug("Fail to create BPMN Element due to {}", e);
        }
        LOG.debug("Doesn't find matching BPMNScope Type for {}", bpmnScope.getId());
        return null;
    }

    private Node createRelationshipTemplateInstanceAsNode(BPMNScope bpmnScope) throws IOException, SAXException {
        String template = createRelationshipTemplateInstance(bpmnScope.getId());
        Node node =  this.transformStringToNode(template);
        bpmnScope.setBpmnScopeElement((Element) node);
        addIncomings(bpmnScope);
        addOutgoings(bpmnScope);
        return node;
    }

    private Node createBPMNSequenceFlowAsNode(BPMNScope bpmnScope) throws IOException, SAXException {
        String template = createBPMNSequenceFlow(bpmnScope.getId(),
            bpmnScope.getIncomingLinks().iterator().next().getId(),
            bpmnScope.getOutgoingLinks().iterator().next().getId()
        );
        return this.transformStringToNode(template);
    }

    public Node transformStringToNode(String xmlString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createScript(String scriptName) throws IOException {
        String script = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("scripts/" + scriptName + ".groovy"));
        return script;
    }

    public String createBPMNStartEvent(String EventID, String outgoingFlowName) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", EventID);
        template = template.replaceAll("FlowToReplace", outgoingFlowName);
        return template;
    }

    public String createBPMNStartEvent(String EventID) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", EventID);
        return template;
    }

    public String createBPMNEndEvent(String EventID) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", EventID);
        return template;
    }

    public String createBPMNEndEvent(String EventID, String incomingFlowName) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", EventID);
        template = template.replaceAll("FlowToReplace", incomingFlowName);
        return template;
    }

    public String createBPMNSequenceFlow(String FlowID, String incomingFlowName, String outgoingFlowName) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSequenceFlow.xml"));
        template = template.replaceAll("Flow_IdToReplace", FlowID);
        template = template.replaceAll("SourceToReplace", incomingFlowName);
        template = template.replaceAll("TargetToReplace", outgoingFlowName);
        return template;
    }

    public String createBPMNNodeSubprocess(String ActivityID, String incomingFlowName, String outgoingFlowName) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeSubprocess.xml"));
        template = template.replaceAll("NodeSubprocess_IdToReplace", ActivityID);
        template = template.replaceAll("SourceToReplace", incomingFlowName);
        template = template.replaceAll("TargetToReplace", outgoingFlowName);
        return template;
    }

    public String createServiceTemplateInstance(String ServiceTemplateInstanceID, String name, String incomingFlowName,
                                                String outgoingFlowName, String state, String resultVariable) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateServiceTemplateInstanceScriptTask.xml"));
        template = template.replaceAll("ServiceTemplateInstance_IdToReplace", ServiceTemplateInstanceID);
        template = template.replaceAll("IncomingFlowToReplace", incomingFlowName);
        template = template.replaceAll("OutgoingFlowToReplace", outgoingFlowName);
        template = template.replaceAll("StateToSet", state);
        template = template.replaceAll("NameToSet", name);
        template = template.replaceAll("ResultVariableToSet", resultVariable);
        return template;
    }

    public String createNodeTemplateInstance(String NodeTemplateInstanceID, String name, String NodeTemplate, String incomingFlowName,
                                             String outgoingFlowName, String state, String resultVariable) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeTemplateInstanceScriptTask.xml"));
        template = template.replaceAll("NodeTemplateInstance_IdToReplace", NodeTemplateInstanceID);
        template = template.replaceAll("IncomingFlowToReplace", incomingFlowName);
        template = template.replaceAll("OutgoingFlowToReplace", outgoingFlowName);
        template = template.replaceAll("StateToSet", state);
        template = template.replaceAll("NameToSet", name);
        template = template.replaceAll("NodeTemplateToSet", NodeTemplate);
        template = template.replaceAll("ResultVariableToSet", resultVariable);
        return template;
    }

    public String createRelationshipTemplateInstance(String RelationshipTemplateInstanceID) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateRelationshipTemplateInstanceScriptTask.xml"));
        template = template.replaceAll("RelationshipTemplate_IdToReplace", RelationshipTemplateInstanceID);
        return template;
    }

    public String createRelationshipTemplateInstance(String RelationshipTemplateInstanceID, String name, String RelationshipTemplate,
                                                     String source, String target, String incomingFlowName,
                                                     String outgoingFlowName, String state, String resultVariable) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateRelationshipTemplateInstanceScriptTask.xml"));
        template = template.replaceAll("RelationshipTemplate_IdToReplace", RelationshipTemplateInstanceID);
        template = template.replaceAll("IncomingFlowToReplace", incomingFlowName);
        template = template.replaceAll("OutgoingFlowToReplace", outgoingFlowName);
        template = template.replaceAll("StateToSet", state);
        template = template.replaceAll("NameToSet", name);
        template = template.replaceAll("RelationshipTemplateToSet", RelationshipTemplate);
        template = template.replaceAll("SourceURLToSet", source);
        template = template.replaceAll("TargetURLToSet", target);
        template = template.replaceAll("ResultVariableToSet", resultVariable);
        return template;
    }

    public String createNodeOperation(String name, String ServiceInstanceURL, String incomingFlowName, String outgoingFlowName,
                                      String csar, String ServiceTemplateId, String NodeTemplate, String Interface, String Operation,
                                      String InputParamNames, String InputParamValues, String OutputParamNames) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeOperationScriptTask.xml"));
        template = template.replaceAll("ServiceInstanceURLToSet", ServiceInstanceURL);
        template = template.replaceAll("IncomingFlowToReplace", incomingFlowName);
        template = template.replaceAll("OutgoingFlowToReplace", outgoingFlowName);
        template = template.replaceAll("CsarToSet", csar);
        template = template.replaceAll("NameToSet", name);
        template = template.replaceAll("Wasauchimmerhierreinkommt", ServiceTemplateId); // !!!!!!!!!!!!!!!!!!!!!!
        template = template.replaceAll("NodeTemplateToSet", NodeTemplate);
        template = template.replaceAll("InerfaceToSet", Interface);
        template = template.replaceAll("OperationToSet", Operation);
        template = template.replaceAll("InputParamNamesToSet", InputParamNames);
        template = template.replaceAll("InputParamValuesToSet", InputParamValues);
        template = template.replaceAll("OutputParamNamesToSet", OutputParamNames);
        return template;
    }

    // noch zu testen!
    public Node createBPMNStartEventAsNode(String EventID, String outgoingFlowName) throws IOException, SAXException {
        final String templateString = createBPMNStartEvent(EventID, outgoingFlowName);
        return this.transformStringToNode(templateString);
    }

    public Node createServiceTemplateInstanceAsNode(String ServiceTemplateInstanceID, String name, String incomingFlowName,
                                                    String outgoingFlowName, String state, String resultVariable) throws IOException, SAXException {
        final String templateString = createServiceTemplateInstance(ServiceTemplateInstanceID, name, incomingFlowName, outgoingFlowName, state, resultVariable);
        return this.transformStringToNode(templateString);
    }

    public Node createBPMNEndEventAsNode(String EventID, String incomingFlowName) throws IOException, SAXException {
        final String templateString = createBPMNEndEvent(EventID, incomingFlowName);
        return this.transformStringToNode(templateString);
    }

    public Node createBPMNSequenceFlowAsNode(String FlowID, String incomingName, String outgoingName) throws IOException, SAXException {
        final String templateString = createBPMNSequenceFlow(FlowID, incomingName, outgoingName);
        return this.transformStringToNode(templateString);
    }

    public Node createBPMNSubprocess(BPMNScope bpmnScope) throws IOException, SAXException {
        final String templateString = createBPMNSubprocess(bpmnScope.getId());
        Node node = this.transformStringToNode(templateString);
        bpmnScope.setBpmnScopeElement((Element) node);
        this.addIncomings(bpmnScope);
        this.addOutgoings(bpmnScope);
        return node;
    }

    private String createBPMNSubprocess(String id) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeSubprocess.xml"));
        template = template.replace("NodeSubprocess_IdToReplace", id);
        return template;
    }

    private String createBPMNSubprocess(String id, String id1, String id2) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeSubprocess.xml"));
        template.replace("NodeSubprocess_IdToReplace", id);
        template.replace("IncomingFlowToReplace", id1);
        template.replace("OutgoingFlowToReplace", id2);
        return template;
    }

    public Node createSetServiceTemplateStateAsNode(BPMNScope bpmnScope) throws IOException, SAXException {
        final String templateString = createSetServiceTemplateStateAsNode(bpmnScope.getId());
        Node node = this.transformStringToNode(templateString);
        bpmnScope.setBpmnScopeElement((Element) node);
        this.addIncomings(bpmnScope);
        this.addOutgoings(bpmnScope);
        return node;
    }

    private String createSetServiceTemplateStateAsNode(String id) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetServiceTemplateStateScriptTask.xml"));
        template = template.replace("SetProperties_IdToReplace", id);
        return template;
    }

    private String createSetServiceTemplateStateAsNode(String id, String id1, String id2) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeSubprocess.xml"));
        template.replace("SetProperties_IdToReplace", id);
        template.replace("IncomingFlowToReplace", id1);
        template.replace("OutgoingFlowToReplace", id2);
        return template;
    }

    public Node createServiceTemplateInstanceAsNode(BPMNScope bpmnScope) throws IOException, SAXException {
        final String templateString = createServiceTemplateInstanceAsNode(bpmnScope.getId());
        Node node = this.transformStringToNode(templateString);
        bpmnScope.setBpmnScopeElement((Element) node);
        this.addIncomings(bpmnScope);
        this.addOutgoings(bpmnScope);
        return node;
    }

    private String createServiceTemplateInstanceAsNode(String id) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateServiceTemplateInstanceScriptTask.xml"));
        template = template.replace("ServiceTemplateInstance_IdToReplace", id);
        template = template.replace("StateToSet", "CREATING");
        return template;
    }

    private String createServiceTemplateInstanceAsNode(String id, String id1, String id2) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateServiceTemplateInstanceScriptTask.xml"));
        template.replace("ServiceTemplateInstance_IdToReplace", id);
        template.replace("StateToSet", "CREATING");
        template.replace("IncomingFlowToReplace", id1);
        template.replace("OutgoingFlowToReplace", id2);
        return template;
    }

    private void addIncomings(BPMNScope bpmnScope) throws IOException, SAXException {
        Node node = (Node) bpmnScope.getBpmnScopeElement();
        Document doc = node.getOwnerDocument();
        for (BPMNScope incoming : bpmnScope.getIncomingLinks()) {
            Node in = createIncomingFlowAsNode(incoming.getId());
            node.appendChild(doc.importNode(in, true));
        }
    }

    private void addOutgoings(BPMNScope bpmnScope) throws IOException, SAXException {
        Node node = (Node) bpmnScope.getBpmnScopeElement();
        Document doc = node.getOwnerDocument();
        for (BPMNScope outgoing : bpmnScope.getOutgoingLinks()) {
            Node out = createOutgoingFlowAsNode(outgoing.getId());
            node.appendChild(doc.importNode(out, true));
        }
    }

    private Node createIncomingFlowAsNode(String id) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNIncoming.xml"));
        template = template.replace("IncomingFlowToReplace", id);
        return this.transformStringToNode(template);
    }

    private Node createOutgoingFlowAsNode(String id) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNOutgoing.xml"));
        template = template.replace("OutgoingFlowToReplace", id);
        return this.transformStringToNode(template);
    }

    public Node createBPMNStartEventAsNode(BPMNScope bpmnScope) throws IOException, SAXException {
        final String templateString = createBPMNStartEvent(bpmnScope.getId());
        Node node =  this.transformStringToNode(templateString);
        bpmnScope.setBpmnScopeElement((Element) node);
        addOutgoings(bpmnScope);
        return node;
    }

    public Node createBPMNEndEventAsNode(BPMNScope bpmnScope) throws IOException, SAXException {
        final String templateString = createBPMNEndEvent(bpmnScope.getId());
        Node node =  this.transformStringToNode(templateString);
        bpmnScope.setBpmnScopeElement((Element) node);
        addIncomings(bpmnScope);
        return node;
    }
}
