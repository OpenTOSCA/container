package org.opentosca.planinvocationengine.service.impl.messages.parsing;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
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
public class ResponseParser {
	
	private final Logger LOG = LoggerFactory.getLogger(ResponseParser.class);
	
	/**
	 * Parses the response and returns the CorrelationID of the Response.
	 * 
	 * This method parses the response, stores the PublicPlan to the History,
	 * remove the CorrelationID from the active plans list, and checks for
	 * faults.
	 * 
	 * @param body
	 *            of the response
	 * @return CorrelationID
	 */
	public String parseSOAPBody(CSARID csarID, QName planID, String correlationID, Document body) {
		
		LOG.debug("Parse a new response.");
		
		TPlanDTO plan = ServiceHandler.correlationHandler.getPublicPlanForCorrelation(correlationID);
		CSARInstanceID instanceID = ServiceHandler.csarInstanceManagement.getInstanceForCorrelation(correlationID);
		
		// store the PublicPlan to the history
		ServiceHandler.csarInstanceManagement.storePublicPlanToHistory(correlationID,
				new PlanInvocationEvent(csarID.toString(), plan, correlationID, instanceID.getInstanceID(),
						ServiceHandler.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, planID),
						ServiceHandler.toscaReferenceMapper.getOperationNameOfPlan(csarID, planID),
						ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, planID), null, // TODO
						// !!!
						false, // not active anymore
						false));
		// delete correlation as running plan
		ServiceHandler.correlationHandler.removeCorrelation(csarID, correlationID);
		
		LOG.info("Found correlation \"" + correlationID + "\" for Plan \"." + planID + "\" with type \""
				+ plan.getPlanType() + "\".");
		
		if (null == plan.getOutputParameters()) {
			LOG.error("There are no output parameters set in Plan {} for CSAR {}.", planID, csarID);
			return null;
		}
		for (TParameterDTO para : plan.getOutputParameters().getOutputParameter()) {
			NodeList outputList = body.getElementsByTagNameNS("*", para.getName());
			if ((null != outputList) && (outputList.getLength() == 1)) {
				LOG.debug("output for " + para.getName() + ": " + outputList.item(0).getTextContent());
				para.setValue(outputList.item(0).getTextContent());
			} else {
				LOG.debug("Found number of elements: " + outputList.getLength());
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
		// builder.append("The recieved SOAP Message contains a fault with fault
		// code: "
		// + fault.getFaultCode() + "." + System.getProperty("line.separator"));
		// // builder.append("Further details: " + fault.getFaultActor() + " - "
		// + fault.getFaultString() + " " + fault.getDetail());
		// // this.LOG.error(builder.toString());
		// //
		// // }
		// }
		// return null;
	}
	
	public String parseRESTResponse(CSARID csarID, QName planID, String correlationID, Object responseBody) {
		
		String resp = (String) responseBody;
		String instanceID = resp.substring(resp.indexOf("href\":\"") + 7, resp.length());
		instanceID = instanceID.substring(instanceID.lastIndexOf("/") + 1, instanceID.indexOf("\""));
		
		LOG.debug("Parsing REST response, found instance ID {} for Correlation {}", instanceID, correlationID);
		
		return instanceID;
	}
}
