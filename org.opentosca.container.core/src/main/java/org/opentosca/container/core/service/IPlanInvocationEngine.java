package org.opentosca.container.core.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.tosca.extension.TPlanDTO;

/**
 * Interface of the PlanInvocationEngine. This service provides a the functionality of invoking
 * PublicPlans, getting a list of CorrelationIDs of active PublicPlans and one specific PublicPlan.
 */
public interface IPlanInvocationEngine {

  public String createCorrelationId(final CsarId csarID, final QName serviceTemplateId, long serviceTemplateInstanceID, final TPlanDTO givenPlan);

  /**
   * Invoke a PublicPlan for a CSAR. If this PublicPlan is of Type OTHERMANAGEMENT or TERMINATION, the
   * information about the CSARInstance is stored inside the PublicPlan.
   *
   * @param csarID
   * @param instance   ID of a CSAR instance
   * @param publicPlan
   * @return boolean about success
   * @throws UnsupportedEncodingException
   */
  public void invokePlan(CsarId csarID, QName serviceTemplateId, long serviceTemplateInstanceID, TPlanDTO plan, String correlationID) throws UnsupportedEncodingException;

  public String invokePlan(CsarId csarID, TServiceTemplate serviceTemplate, long serviceTemplateInstanceID, TPlanDTO plan) throws UnsupportedEncodingException;

  public void correctCorrelationToServiceTemplateInstanceIdMapping(CsarId csarID, QName serviceTemplateId, String corrId, int correctSTInstanceId);

  /**
   * Returns a list of CorrelationIDs of activce PublicPlans of a CSARInstance.
   *
   * @param csarInstanceID
   * @return list of CorrelationIDs of active PublicPlans
   */
  public List<String> getActiveCorrelationsOfInstance(ServiceTemplateInstanceID csarInstanceID);

  /**
   * Returns a specific active PublicPlan.
   *
   * @param csarInstanceID
   * @param correlationID
   * @return PublicPlan
   */
  public TPlanDTO getActivePublicPlanOfInstance(ServiceTemplateInstanceID csarInstanceID, String correlationID);

  public IPlanLogHandler getPlanLogHandler();

}
