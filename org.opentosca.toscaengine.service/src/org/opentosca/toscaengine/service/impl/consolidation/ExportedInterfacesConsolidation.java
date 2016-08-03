package org.opentosca.toscaengine.service.impl.consolidation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TExportedInterface;
import org.opentosca.model.tosca.TExportedOperation;
import org.opentosca.model.tosca.TExportedOperation.Plan;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.toscaengine.service.impl.ToscaEngineServiceImpl;
import org.opentosca.toscaengine.service.impl.toscareferencemapping.ToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class consolidates the BoundaryDefinitions and Plans to PublicPlans.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class ExportedInterfacesConsolidation {

    ToscaReferenceMapper toscaReferenceMapper = ToscaEngineServiceImpl.toscaReferenceMapper;

    /**
     * NamespaceContext
     */
    XPath xpath = XPathFactory.newInstance().newXPath();
    NamespaceContext nscontext = new NamespaceContext() {

	@Override
	public String getNamespaceURI(String prefix) {
	    String uri;
	    if (prefix.equals("wsdl")) {
		uri = "http://schemas.xmlsoap.org/wsdl/";
	    } else if (prefix.equals("xs")) {
		uri = "http://www.w3.org/2001/XMLSchema";
	    } else if (prefix.equals("tosca")) {
		uri = "http://docs.oasis-open.org/tosca/ns/2011/12";
	    } else {
		uri = null;
	    }
	    return uri;
	}

	// Dummy implementation
	// Suppress warnings because of this method is auto generated
	// and not
	// used.
	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String val) {
	    return null;
	}

	// Dummy implemenation - not used!
	@Override
	public String getPrefix(String uri) {
	    return null;
	}
    };

    private final Logger LOG = LoggerFactory.getLogger(ExportedInterfacesConsolidation.class);

    public ExportedInterfacesConsolidation() {
	xpath.setNamespaceContext(nscontext);
    }

    /**
     * Consolidates the exported interfaces of a CSAR.
     * 
     * @param csarID
     *            the ID of the CSAR.
     * @return true for success, false if an error occured
     */
    public boolean consolidate(CSARID csarID) {

	LOG.info("Consolidate the Interfaces of the BoundaryDefinitions of CSAR \"" + csarID + "\".");

	// return value is negated, thus inside this method a true means at
	// least one error
	boolean errorOccured = false;

	Map<PlanTypes, LinkedHashMap<QName, TPlan>> mapTypeToPlan = toscaReferenceMapper.getCSARIDToPlans(csarID);

	for (QName serviceTemplateID : toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID).keySet()) {

	    LOG.debug("Consolidate the Interfaces of the ServiceTemplate \"" + serviceTemplateID + "\".");

	    for (TExportedInterface iface : toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID)
		.get(serviceTemplateID)) {

		for (TExportedOperation operation : iface.getOperation()) {

		    Plan planReference = operation.getPlan();
		    if (null != planReference) {
			TPlan toscaPlan = (TPlan) planReference.getPlanRef();

			QName id = new QName(toscaReferenceMapper.getNamespaceOfPlan(csarID, toscaPlan.getId()),
			    toscaPlan.getId());

			toscaReferenceMapper.setBoundaryInterfaceForCSARIDPlan(csarID, id, iface.getName());
			toscaReferenceMapper.setBoundaryOperationForCSARIDPlan(csarID, id, operation.getName());

			mapTypeToPlan.get(PlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).put(id, toscaPlan);

			toscaReferenceMapper.storePlanInputMessageID(csarID, id,
			    getInputMessageQName(csarID, iface.getName(), operation.getName(), toscaPlan.getId(),
				toscaPlan, toscaReferenceMapper.getListOfWSDLForCSAR(csarID)));

		    } else {
			// just need the plans
		    }
		}

	    }
	}

	return !errorOccured;
    }

    /**
     * TODO implement for wsdl 2.0 TODO transitive reloading of imported stuff?
     * TODO all informations have to be in one wsdl (change this?)
     * 
     * @param planID
     * 
     * @param boundaryPlan
     * @param list
     * @throws XPathExpressionException
     */
    private QName getInputMessageQName(CSARID csarID, String wsdlInterfaceName, String wsdlOperationName, String planID,
	TPlan boundaryPlan, List<Document> list) {

	LOG.debug("Try to find the InputMessageID for CSAR " + csarID + " and plan " + boundaryPlan.getId());

	LOG.debug("countwsdl: " + list.size() + " interfacename:" + wsdlInterfaceName + " operationname:"
	    + wsdlOperationName);

	// IXMLSerializer serializer =
	// ServiceHandler.xmlSerializerService.getXmlSerializer();

	for (Document doc : list) {

	    try {
		// select specific PortType/Interface with name and operation
		String exprString = "/wsdl:definitions/wsdl:portType[@name=\"" + wsdlInterfaceName
		    + "\"]/wsdl:operation[@name=\"" + wsdlOperationName + "\"]/wsdl:input/@message";
		XPathExpression expr;

		expr = xpath.compile(exprString);
		NodeList messageQName = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		LOG.debug(exprString);
		LOG.trace("Found results: " + messageQName.getLength());
		// LOG.trace(serializer.docToString(doc, true));

		// if there is a PortType/Interface in this document, then there
		// is the message defined as well
		if (messageQName.getLength() == 1) {

		    QName id = new QName(toscaReferenceMapper.getNamespaceOfPlan(csarID, planID), planID);

		    LOG.debug("Found the message QName {} for plan {}.", messageQName, id);

		    // check whether synchronous or asynchronous
		    exprString = "/wsdl:definitions/wsdl:portType[@name=\"" + wsdlInterfaceName
			+ "\"]/wsdl:operation[@name=\"" + wsdlOperationName + "\"]/wsdl:output/@message";
		    expr = xpath.compile(exprString);
		    NodeList output = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    if (output.getLength() > 0) {
			// this is a synchronous bpel plan
			LOG.debug("This plan is synchronous.");
			toscaReferenceMapper.storePlanAsynchronousBoolean(csarID, id, false);
		    } else if (output.getLength() == 0) {
			// this is an asynchronous bpel plan
			LOG.debug("This plan is asynchronous.");
			toscaReferenceMapper.storePlanAsynchronousBoolean(csarID, id, true);
		    }

		    // wsdl porttype/interface to wsdl message
		    String value = messageQName.item(0).getNodeValue();
		    // String prefix = value.substring(0, value.indexOf(":"));
		    String name = value.substring(value.indexOf(":") + 1);
		    // String namespace = doc.lookupNamespaceURI(prefix);
		    // QName wsdlMessageID = new QName(namespace, name);

		    LOG.debug("Found the PortType. Searching for the message \"" + name + "\".");

		    // wsdl message to schema message
		    // TODO multiple parts?
		    exprString = "/wsdl:definitions/wsdl:message[@name=\"" + name + "\"]/wsdl:part/@element";
		    // exprString = "/wsdl:definitions/wsdl:message[@name=\"" +
		    // name
		    // + "\"]/wsdl:part[@name=\"payload\"]/@element";
		    expr = xpath.compile(exprString);
		    NodeList messages = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		    LOG.debug(exprString);
		    LOG.debug("Count results: " + messages.getLength());

		    if (messages.getLength() == 1) {

			value = messages.item(0).getNodeValue();

			LOG.debug("Value of " + messages.item(0).getLocalName() + " is " + value);

			QName messageID = new QName(doc.lookupNamespaceURI(value.substring(0, value.indexOf(":"))),
			    value.substring(value.indexOf(":") + 1));
			LOG.debug("Found message QName: " + messageID.toString());
			return messageID;

		    }
		}

	    } catch (XPathExpressionException e) {
		e.printStackTrace();
	    }
	}

	LOG.error("Did not find the message!");

	return null;
    }
}
