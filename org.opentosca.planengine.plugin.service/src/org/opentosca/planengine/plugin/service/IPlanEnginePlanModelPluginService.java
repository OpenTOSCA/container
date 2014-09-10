package org.opentosca.planengine.plugin.service;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan.PlanModel;

/**
 * <p>
 * This is a subinterface of
 * {@link org.opentosca.planengine.plugin.service.IPlanEnginePluginService} and
 * specifies handling of PlanModel elements inside a Plan element specified in
 * <a href=
 * "http://docs.oasis-open.org/tosca/TOSCA/v1.0/csd04/TOSCA-v1.0-csd04.html#_Toc335251941"
 * >Topology and Orchestration Specification for Cloud Applications Version 1.0
 * Chapter 11: Plans</a>
 * </p>
 * 
 * <p>
 * A PlanModel element declares a Plan which is directly written inside
 * ServiceTemplate, for example a bash script. This means the plugin must deploy
 * the script on a appropiate system capable of executing it.
 * </p>
 * 
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * 
 * @see org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService
 * @see org.opentosca.model.tosca.TPlan.PlanModel
 * @see <a href=
 *      "http://docs.oasis-open.org/tosca/TOSCA/v1.0/csd04/TOSCA-v1.0-csd04.html#_Toc335251941"
 *      >Topology and Orchestration Specification for Cloud Applications Version
 *      1.0 Chapter 11: Plans</a>
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public interface IPlanEnginePlanModelPluginService extends IPlanEnginePluginService {
	
	/**
	 * <p>
	 * Method allows deployment of PlanModels
	 * </p>
	 * <p>
	 * In addition a service implementing
	 * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must
	 * provide a suitable endpoint.
	 * </p>
	 * 
	 * @param planModel the PlanModel element inside a Plan element of a
	 *            ServiceTemplate Definition
	 * @param csarId the identifier of the CSAR this PlanModel element belongs
	 *            to
	 * @return true if deployment was successful, else false
	 */
	public boolean deployPlan(PlanModel planModel, CSARID csarId);
	
	/**
	 * <p>
	 * Method allows undeployment of PlanModels
	 * </p>
	 * <p>
	 * In addition a service implementing
	 * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must
	 * provide a suitable endpoint.
	 * </p>
	 * 
	 * @param planModel the PlanModel element inside a Plan element of a
	 *            ServiceTemplate Definition
	 * @param csarId the identifier of the CSAR this PlanModel element belongs
	 *            to
	 * @return true if undeployment was successful, else false
	 */
	public boolean undeployPlan(PlanModel planModel, CSARID csarId);
	
}
