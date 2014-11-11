package org.opentosca.planbuilder.helpers;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CorrelationIDInitializer {
	
	private BuildPlanHandler buildPlanHandler;
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	
	public CorrelationIDInitializer() {
		try {
			this.buildPlanHandler = new BuildPlanHandler();
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docFactory.setNamespaceAware(true);
			this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void addCorrellationID(BuildPlan buildPlan) {
		// set a correlation id which will can be set in the input and will be
		// send back with the response
		this.buildPlanHandler.addStringElementToPlanRequest("CorrelationID", buildPlan);
		this.buildPlanHandler.addStringElementToPlanResponse("CorrelationID", buildPlan);
		
		// add an assign
		try {
			Node assignNode = this.createAssignFromInputToOutputAsNode();
			assignNode = buildPlan.getBpelDocument().importNode(assignNode, true);
			Element flowElement = buildPlan.getBpelMainFlowElement();
			
			Node mainSequenceNode = flowElement.getParentNode();
			
			mainSequenceNode.insertBefore(assignNode, flowElement);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String createAssignFromInputToOutput() {
		String bpelAssign = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignCorrelationID\"><bpel:from variable=\"input\" part=\"payload\"><![CDATA[tns:CorrelationID]]></bpel:from><bpel:to variable=\"output\" part=\"payload\"><![CDATA[tns:CorrelationID]]></bpel:to></bpel:assign>";
		return bpelAssign;
	}
	
	public Node createAssignFromInputToOutputAsNode() throws SAXException, IOException {
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(this.createAssignFromInputToOutput()));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
}
