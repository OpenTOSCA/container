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
import org.opentosca.model.consolidatedtosca.Parameter;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.consolidatedtosca.PublicPlanTypes;
import org.opentosca.model.tosca.TExportedInterface;
import org.opentosca.model.tosca.TExportedOperation;
import org.opentosca.model.tosca.TExportedOperation.Plan;
import org.opentosca.model.tosca.TParameter;
import org.opentosca.model.tosca.TPlan;
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
		this.xpath.setNamespaceContext(this.nscontext);
	}
	
	/**
	 * Consolidates the exported interfaces of a CSAR.
	 * 
	 * @param csarID the ID of the CSAR.
	 * @return true for success, false if an error occured
	 */
	public boolean consolidate(CSARID csarID) {
		
		this.LOG.info("Consolidate the Interfaces of the BoundaryDefinitions of CSAR \"" + csarID + "\".");
		
		// return value is negated, thus inside this method a true means at
		// least one error
		boolean errorOccured = false;
		
		Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> mapTypeToPlan = this.toscaReferenceMapper.getCSARIDToPublicPlans(csarID);
		
		for (QName serviceTemplateID : this.toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID).keySet()) {
			
			this.LOG.debug("Consolidate the Interfaces of the ServiceTemplate \"" + serviceTemplateID + "\".");
			
			for (TExportedInterface iface : this.toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID).get(serviceTemplateID)) {
				
				for (TExportedOperation operation : iface.getOperation()) {
					
					Plan planReference = operation.getPlan();
					if (null != planReference) {
						TPlan toscaPlan = (TPlan) planReference.getPlanRef();
						QName planID = new QName(serviceTemplateID.getNamespaceURI(), toscaPlan.getId());
						
						// store consolidated plan informations
						PublicPlan publicPlan = new PublicPlan();
						
						publicPlan.setCSARID(csarID.toString());
						publicPlan.setPlanID(planID);
						publicPlan.setInterfaceName(iface.getName());
						publicPlan.setOperationName(operation.getName());
						publicPlan.setPlanType(toscaPlan.getPlanType());
						publicPlan.setPlanLanguage(toscaPlan.getPlanLanguage());
						
						publicPlan.setInputMessageID(this.getInputMessageQName(csarID, publicPlan, this.toscaReferenceMapper.getListOfWSDLForCSAR(csarID)));
						
						// parameters
						if (null != toscaPlan.getInputParameters()) {
							for (TParameter parameter : toscaPlan.getInputParameters().getInputParameter()) {
								Parameter newParameter = new Parameter(parameter.getName(), parameter.getType(), parameter.getRequired().value().equals("yes"));
								publicPlan.getInputParameter().add(newParameter);
							}
						}
						if (null != toscaPlan.getOutputParameters()) {
							for (TParameter parameter : toscaPlan.getOutputParameters().getOutputParameter()) {
								Parameter newParameter = new Parameter(parameter.getName(), parameter.getType(), parameter.getRequired().value().equals("yes"));
								publicPlan.getOutputParameter().add(newParameter);
							}
						}
						
						// put the PublicPlan to the LinkedHashMap
						if (mapTypeToPlan.get(PublicPlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).isEmpty()) {
							mapTypeToPlan.get(PublicPlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).put(0, publicPlan);
						} else {
							Integer highest = 0;
							for (Integer itr : mapTypeToPlan.get(PublicPlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).keySet()) {
								if (itr > highest) {
									highest = itr;
								}
							}
							publicPlan.setInternalPlanID(highest + 1);
							mapTypeToPlan.get(PublicPlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).put(highest + 1, publicPlan);
						}
						
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
	 * @param publicPlan
	 * @param list
	 * @throws XPathExpressionException
	 */
	private QName getInputMessageQName(CSARID csarID, PublicPlan publicPlan, List<Document> list) {
		
		String wsdlInterfaceName = publicPlan.getInterfaceName();
		String wsdlOperationName = publicPlan.getOperationName();
		
		this.LOG.debug("Try to find the InputMessageID for CSAR " + csarID + " and plan " + publicPlan.getPlanID());
		
		// this.LOG.debug("countwsdl: " + list.size() + " interfacename:" +
		// wsdlInterfaceName + " operationname:" + wsdlOperationName);
		
		for (Document doc : list) {
			
			try {
				// select specific PortType/Interface with name and operation
				String exprString = "/wsdl:definitions/wsdl:portType[@name=\"" + wsdlInterfaceName + "\"]/wsdl:operation[@name=\"" + wsdlOperationName + "\"]/wsdl:input/@message";
				XPathExpression expr;
				
				expr = this.xpath.compile(exprString);
				NodeList messageQName = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				
				// this.LOG.debug(exprString);
				// this.LOG.debug("Count results: " + portTypes.getLength());
				
				// if there is a PortType/Interface in this document, then there
				// is the message defined as well
				if (messageQName.getLength() == 1) {
					
					this.LOG.debug("Found the message QName.");
					
					// check whether synchronous or asynchronous
					exprString = "/wsdl:definitions/wsdl:portType[@name=\"" + wsdlInterfaceName + "\"]/wsdl:operation[@name=\"" + wsdlOperationName + "\"]/wsdl:output/@message";
					expr = this.xpath.compile(exprString);
					NodeList output = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
					if (output.getLength() > 0) {
						// this is a synchronous bpel plan
						this.LOG.debug("This plan is synchronous.");
						this.toscaReferenceMapper.storePlanAsynchronousBoolean(csarID, publicPlan.getPlanID(), false);
					} else if (output.getLength() == 0) {
						// this is an asynchronous bpel plan
						this.LOG.debug("This plan is asynchronous.");
						this.toscaReferenceMapper.storePlanAsynchronousBoolean(csarID, publicPlan.getPlanID(), true);
					}
					
					// wsdl porttype/interface to wsdl message
					String value = messageQName.item(0).getNodeValue();
					// String prefix = value.substring(0, value.indexOf(":"));
					String name = value.substring(value.indexOf(":") + 1);
					// String namespace = doc.lookupNamespaceURI(prefix);
					// QName wsdlMessageID = new QName(namespace, name);
					
					this.LOG.debug("Found the PortType. Searching for the message \"" + name + "\".");
					
					// wsdl message to schema message
					// TODO multiple parts?
					exprString = "/wsdl:definitions/wsdl:message[@name=\"" + name + "\"]/wsdl:part/@element";
					// exprString = "/wsdl:definitions/wsdl:message[@name=\"" +
					// name
					// + "\"]/wsdl:part[@name=\"payload\"]/@element";
					expr = this.xpath.compile(exprString);
					NodeList messages = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
					
					this.LOG.debug(exprString);
					this.LOG.debug("Count results: " + messages.getLength());
					
					if (messages.getLength() == 1) {
						
						value = messages.item(0).getNodeValue();
						
						this.LOG.debug("Value of " + messages.item(0).getLocalName() + " is " + value);
						
						QName id = new QName(doc.lookupNamespaceURI(value.substring(0, value.indexOf(":"))), value.substring(value.indexOf(":") + 1));
						this.LOG.debug("Found message QName: " + id.toString());
						return id;
						
					}
				}
				
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		
		this.LOG.error("Did not found the message!");
		
		return null;
	}
}
