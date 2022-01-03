package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
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
}
