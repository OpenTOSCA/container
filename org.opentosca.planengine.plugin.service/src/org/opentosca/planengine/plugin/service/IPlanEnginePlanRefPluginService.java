package org.opentosca.planengine.plugin.service;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan.PlanModelReference;

/**
 * <p>
 * This is a subinterface of
 * {@link org.opentosca.planengine.plugin.service.IPlanEnginePluginService} and
 * specifies handling of PlanReference elements inside a Plan element specified
 * in <a href=
 * "http://docs.oasis-open.org/tosca/TOSCA/v1.0/csd04/TOSCA-v1.0-csd04.html#_Toc335251941"
 * >Topology and Orchestration Specification for Cloud Applications Version 1.0
 * Chapter 11: Plans</a>
 * </p>
 * <p>
 * The plugin musn't resolve the
 * {@link org.opentosca.model.tosca.TPlan.PlanModelReference}, a service
 * implementing {@link org.opentosca.core.file.service.ICoreFileService} should
 * be called for the raw data.
 * </p>
 * 
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * 
 * @see <a href=
 *      "http://docs.oasis-open.org/tosca/TOSCA/v1.0/csd04/TOSCA-v1.0-csd04.html#_Toc335251941"
 *      >Topology and Orchestration Specification for Cloud Applications Version
 *      1.0 Chapter 11: Plans</a>
 * @see org.opentosca.planengine.plugin.service.IPlanEnginePlanModelPluginService
 * @see org.opentosca.model.tosca.TPlan.PlanModelReference
 * @author Kalman Kepes - kepeskn@stud.informatik.uni-stuttgart.de
 * 
 */
public interface IPlanEnginePlanRefPluginService extends IPlanEnginePluginService {
	
	/**
	 * <p>
	 * Method allows deployment of PlanModelReferences.
	 * </p>
	 * <p>
	 * The reference must be resolved in a service implementing
	 * {@link org.opentosca.core.file.service.ICoreFileService}. In addition a
	 * service of
	 * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must
	 * provide a suitable endpoint.
	 * </p>
	 * 
	 * 
	 * @param planRef the PlanReference element under a Plan element of a
	 *            ServiceTemplate Definition
	 * @param csarId the identifier of the CSAR the PlanReference element
	 *            belongs to
	 * @return true if deployment was successful, else false
	 */
	public boolean deployPlanReference(QName planId, PlanModelReference planRef, CSARID csarId);
	
	/**
	 * <p>
	 * Method allows undeployment of PlanModelReferences.
	 * </p>
	 * <p>
	 * The reference must be resolved in a service implementing
	 * {@link org.opentosca.core.file.service.ICoreFileService}. In addition a
	 * service of
	 * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must
	 * provide a suitable endpoint.
	 * </p>
	 * 
	 * 
	 * @param planRef the PlanReference element under a Plan element of a
	 *            ServiceTemplate Definition
	 * @param csarId the identifier of the CSAR the PlanReference element
	 *            belongs to
	 * @return true if undeployment was successful, else false
	 */
	public boolean undeployPlanReference(QName planId, PlanModelReference planRef, CSARID csarId);
	
}
