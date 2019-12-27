package org.opentosca.container.core.service;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.tosca.extension.TPlanDTO;

/**
 * Interface of the PlanInvocationEngine. This service provides a the functionality of invoking
 * PublicPlans, getting a list of CorrelationIDs of active PublicPlans and one specific PublicPlan.
 */
public interface IPlanInvocationEngine {

  String createCorrelationId();

  /**
   * Invoke a PublicPlan for a CSAR. If this PublicPlan is of Type OTHERMANAGEMENT or TERMINATION, the
   * information about the CSARInstance is stored inside the PublicPlan.
   */
  void invokePlan(CsarId csarID, QName serviceTemplateId, long serviceTemplateInstanceID, TPlanDTO plan, String correlationID);

  void invokePlan(CsarId csarId, TServiceTemplate serviceTemplate, long serviceTemplateInstanceId, TPlanDTO plan, String correlationId);

}
