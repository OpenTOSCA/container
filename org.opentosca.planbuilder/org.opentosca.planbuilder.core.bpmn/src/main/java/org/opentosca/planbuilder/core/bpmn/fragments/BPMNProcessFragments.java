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

    public String createBPMNStartEvent(String EventID, String outgoingFlowName) throws IOException{
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", EventID);
        template = template.replaceAll("FlowToReplace", outgoingFlowName);
        return template;
    }

    public String createBPMNEndEvent(String EventID, String incomingFlowName) throws IOException{
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", EventID);
        template = template.replaceAll("FlowToReplace", incomingFlowName);
        return template;
    }

    public String createServiceTemplateInstance(String ServiceTemplateInstanceID, String incomingFlowName,
                                                String outgoingFlowName, String state) throws IOException{
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        template = template.replaceAll("ServiceTemplateInstance_IdToReplace", ServiceTemplateInstanceID);
        template = template.replaceAll("incomingFlowToReplace", incomingFlowName);
        template = template.replaceAll("outgoingFlowToReplace", outgoingFlowName);
        template = template.replaceAll("StateToSet", state);
        return template;
    }

    // noch zu testen!
    public Node createBPMNStartEventAsNode(String EventID, String outgoingFlowName) throws IOException, SAXException {
        final String templateString = createBPMNStartEvent(EventID, outgoingFlowName);
        return this.transformStringToNode(templateString);
    }

}
