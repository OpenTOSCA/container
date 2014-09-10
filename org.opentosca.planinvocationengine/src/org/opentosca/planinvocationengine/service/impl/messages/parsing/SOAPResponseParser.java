package org.opentosca.planinvocationengine.service.impl.messages.parsing;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.Parameter;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.planinvocationengine.service.impl.ServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class parses the SOAP responses of PublicPlans.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class SOAPResponseParser {
	
	private final Logger LOG = LoggerFactory.getLogger(SOAPResponseParser.class);
	
	
	/**
	 * Parses the response and returns the CorrelationID of the Response.
	 * 
	 * This method parses the response, stores the PublicPlan to the History,
	 * remove the CorrelationID from the active plans list, and checks for
	 * faults.
	 * 
	 * @param body of the response
	 * @return CorrelationID
	 */
	public String parseSOAPBody(Document body, String correlationID) {
		
		this.LOG.debug("Parse a new response.");
		
		// // search for correlation
		// NodeList nodes =
		// body.getElementsByTagNameNS("http://www.opentosca.org/Correlation",
		// "CorrelationID");
		//
		// if ((null == nodes) || (0 == nodes.getLength())) {
		// this.LOG.error("No correlation found.");
		// }
		//
		// // if correlation is available
		// if (nodes.getLength() == 1) {
		//
		// String correlationID = nodes.item(0).getTextContent();
		PublicPlan publicPlan = ServiceHandler.correlationHandler.getPublicPlanForCorrelation(correlationID);
		publicPlan.setIsActive(false);
		
		// store the PublicPlan to the history
		ServiceHandler.csarInstanceManagement.storePublicPlanToHistory(correlationID, publicPlan);
		// delete correlation as running plan
		ServiceHandler.correlationHandler.removeCorrelation(new CSARID(publicPlan.getCSARID()), correlationID);
		
		this.LOG.info("Found correlation \"" + correlationID + "\" for PublicPlan \"." + publicPlan.getPlanID() + "\" with type \"" + publicPlan.getPlanType() + "\".");
		
		for (Parameter para : publicPlan.getOutputParameter()) {
			NodeList outputList = body.getElementsByTagNameNS("*", para.getName());
			if ((null != outputList) && (outputList.getLength() == 1)) {
				this.LOG.debug("output for " + para.getName() + ": " + outputList.item(0).getTextContent());
				para.setValue(outputList.item(0).getTextContent());
			} else {
				this.LOG.debug("Found number of elements: " + outputList.getLength());
			}
		}
		
		// TODO implement check for Faults
		// if (body.hasFault()) {
		// this.LOG.info("The PublicPlan instance of PublicPlan " +
		// publicPlan.getPlanID() + " with correlation " + correlationID +
		// " contains a Fault.");
		// SOAPFault fault = body.getFault();
		// Parameter faultParam = new Parameter();
		// faultParam.setName(fault.getFaultCode());
		// faultParam.setType(fault.getFaultActor());
		// faultParam.setValue(fault.getFaultString() + " " +
		// fault.getDetail());
		// publicPlan.setHasFailed(true);
		// publicPlan.getOutputParameter().add(faultParam);
		// }
		
		return correlationID;
		
		// } else {
		// this.LOG.error("Found {} elements looking like correlation.",
		// nodes.getLength());
		// // TODO implement!
		// // if (body.hasFault()) {
		// //
		// // SOAPFault fault = body.getFault();
		// //
		// // StringBuilder builder = new StringBuilder();
		// //
		// builder.append("The recieved SOAP Message contains a fault with fault code: "
		// + fault.getFaultCode() + "." + System.getProperty("line.separator"));
		// // builder.append("Further details: " + fault.getFaultActor() + " - "
		// + fault.getFaultString() + " " + fault.getDetail());
		// // this.LOG.error(builder.toString());
		// //
		// // }
		// }
		// return null;
	}
}
