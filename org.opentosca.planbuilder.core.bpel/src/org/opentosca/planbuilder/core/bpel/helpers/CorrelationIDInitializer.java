package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CorrelationIDInitializer {

    private BPELPlanHandler buildPlanHandler;
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

    public CorrelationIDInitializer() {
        try {
            this.buildPlanHandler = new BPELPlanHandler();
            this.docFactory = DocumentBuilderFactory.newInstance();
            this.docFactory.setNamespaceAware(true);
            this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void addCorrellationID(final BPELPlan buildPlan) {
        // set a correlation id which will can be set in the input and will be
        // send back with the response
        this.buildPlanHandler.addStringElementToPlanRequest("CorrelationID", buildPlan);
        this.buildPlanHandler.addStringElementToPlanResponse("CorrelationID", buildPlan);

        // add an assign
        try {
            Node assignNode = this.createAssignFromInputToOutputAsNode(buildPlan.getWsdl().getTargetNamespace());
            assignNode = buildPlan.getBpelDocument().importNode(assignNode, true);
            final Element flowElement = buildPlan.getBpelMainFlowElement();

            final Node mainSequenceNode = flowElement.getParentNode();

            mainSequenceNode.insertBefore(assignNode, flowElement);
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String createAssignFromInputToOutput(final String targetNamespace) {
        final String bpelAssign = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignCorrelationID\"><bpel:copy><bpel:from variable=\"input\" part=\"payload\"><bpel:query xmlns:tns=\""
            + targetNamespace
            + "\" queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[tns:CorrelationID]]></bpel:query></bpel:from><bpel:to variable=\"output\" part=\"payload\"><bpel:query xmlns:tns=\""
            + targetNamespace
            + "\" queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[tns:CorrelationID]]></bpel:query></bpel:to></bpel:copy></bpel:assign>";
        return bpelAssign;
    }

    public Node createAssignFromInputToOutputAsNode(final String targetNamespace) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(this.createAssignFromInputToOutput(targetNamespace)));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }
}
