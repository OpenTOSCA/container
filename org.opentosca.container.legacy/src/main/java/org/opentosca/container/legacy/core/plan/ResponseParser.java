package org.opentosca.container.legacy.core.plan;

import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.legacy.core.service.ServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class parses the SOAP responses of PublicPlans.
 */
@Deprecated
public class ResponseParser {

  private static final Logger LOG = LoggerFactory.getLogger(ResponseParser.class);


  /**
   * Parses the response and returns the CorrelationID of the Response.
   * <p>
   * This method parses the response, stores the PublicPlan to the History, remove the CorrelationID
   * from the active plans list, and checks for faults.
   *
   * @param body of the response
   * @return CorrelationID
   */
  public String parseSOAPBody(final CSARID csarID, final QName planID, final String correlationID,
                              final Document body) {

    LOG.debug("Parse a new response.");

    final TPlanDTO plan = ServiceProxy.correlationHandler.getPublicPlanForCorrelation(correlationID);
    final ServiceTemplateInstanceID instanceID = ServiceProxy.csarInstanceManagement.getInstanceForCorrelation(correlationID);

    // store the PublicPlan to the history
    ServiceProxy.csarInstanceManagement.storePublicPlanToHistory(correlationID,
      new PlanInvocationEvent(csarID.toString(), plan, correlationID, instanceID.getInstanceID(),
      ServiceProxy.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, planID),
      ServiceProxy.toscaReferenceMapper.getOperationNameOfPlan(csarID, planID),
      ServiceProxy.toscaReferenceMapper.getPlanInputMessageID(csarID, planID),
        null, // TODO
      // !!!
      false, // not active anymore
      false));
    // delete correlation as running plan
    ServiceProxy.correlationHandler.removeCorrelation(csarID, correlationID);

    LOG.info("Found correlation \"" + correlationID + "\" for Plan \"." + planID + "\" with type \"" + plan.getPlanType() + "\".");

    if (null == plan.getOutputParameters()) {
      LOG.error("There are no output parameters set in Plan {} for CSAR {}.", planID, csarID);
      return null;
    }
    for (final TParameterDTO para : plan.getOutputParameters().getOutputParameter()) {
      final NodeList outputList = body.getElementsByTagNameNS("*", para.getName());
      if (null != outputList && outputList.getLength() == 1) {
        LOG.debug("output for " + para.getName() + ": " + outputList.item(0).getTextContent());
        para.setValue(outputList.item(0).getTextContent());
      } else {
        LOG.debug("Found number of elements: " + outputList.getLength());
      }
    }
    return correlationID;
  }

  /**
   * Parses the response and returns the CorrelationID of the Response.
   * <p>
   * This method parses the response, stores the PublicPlan to the History, remove the CorrelationID
   * from the active plans list, and checks for faults.
   *
   * @param body of the response
   * @return CorrelationID
   */
  public String parseSOAPBody(final CSARID csarID, final QName planID, final String correlationID, final Map<String, String> outputParas) {

    LOG.debug("Parse a new response.");

    final TPlanDTO plan = ServiceProxy.correlationHandler.getPublicPlanForCorrelation(correlationID);
    final ServiceTemplateInstanceID instanceID = ServiceProxy.csarInstanceManagement.getInstanceForCorrelation(correlationID);

    // store the PublicPlan to the history
    ServiceProxy.csarInstanceManagement.storePublicPlanToHistory(correlationID,
      new PlanInvocationEvent(csarID.toString(), plan, correlationID, instanceID.getInstanceID(),
      ServiceProxy.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, planID),
      ServiceProxy.toscaReferenceMapper.getOperationNameOfPlan(csarID, planID),
      ServiceProxy.toscaReferenceMapper.getPlanInputMessageID(csarID, planID),
      null, // TODO
      // !!!
      false, // not active anymore
      false));
    // delete correlation as running plan
    ServiceProxy.correlationHandler.removeCorrelation(csarID, correlationID);

    LOG.info("Found correlation \"" + correlationID + "\" for Plan \"." + planID + "\" with type \"" + plan.getPlanType() + "\".");
    if (null == plan.getOutputParameters()) {
      LOG.error("There are no output parameters set in Plan {} for CSAR {}.", planID, csarID);
      return null;
    }

    for (final TParameterDTO para : plan.getOutputParameters().getOutputParameter()) {
      para.setValue(outputParas.get(plan.getId().getLocalPart()));
    }
    return correlationID;
  }

  public String parseRESTResponse(final CSARID csarID, final QName planID, final String correlationID, final Object responseBody) {
    final String resp = (String) responseBody;
    String instanceID = resp.substring(resp.indexOf("href\":\"") + 7, resp.length());
    instanceID = instanceID.substring(instanceID.lastIndexOf("/") + 1, instanceID.indexOf("\""));

    LOG.debug("Parsing REST response, found instance ID {} for Correlation {}", instanceID, correlationID);
    return instanceID;
  }
}
