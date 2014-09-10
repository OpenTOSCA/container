package org.opentosca.containerapi.portability;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resource handles the request to query for consolidatedPolicies
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public class PoliciesListResource {

	private static final Logger LOG = LoggerFactory.getLogger(PoliciesListResource.class);

	/**
	 * Returns the policies for the specified parameters
	 * 
	 * @param csarID
	 *            csarID of the service/nodeTemplate
	 * @param serviceTemplateID
	 *            serviceTemplateID
	 * @param nodeTemplateID
	 *            nodeTemplateID
	 * @return
	 */
	@GET
	@Produces(ResourceConstants.TOSCA_XML)
	// arID=TestLifeCycle.csar&namespace=org.opentosca.demo&serviceTemplateID=TestLifeCycleDemo_ServiceTemplate&nodeTemplateName=TestLifeCycleDemoNodeTemplate
	public ConsolidatedPolicies getPolicies(@QueryParam("csarID") String csarID,@QueryParam("namespace") String namespaceServiceTemplate,
			@QueryParam("serviceTemplateID") String serviceTemplateID,
			@QueryParam("nodeTemplateID") String nodeTemplateID) {

		if (Utilities.areEmpty(csarID, serviceTemplateID)) {
			throw new GenericRestException(Status.BAD_REQUEST,
					"one of the required parameters: csarID, serviceTemplateID was not set");
		}

		if (null == ToscaServiceHandler.getToscaEngineService()) {
			PoliciesListResource.LOG.error("ToscaEngineService is null!");
			return null;
		}

		CSARID csarID_csarID;
		QName templateID = null;

		try {
			csarID_csarID = new CSARID(csarID);
			//determine which ID to use
			if(nodeTemplateID == null){
				templateID = new QName(namespaceServiceTemplate, serviceTemplateID);
			}else{
				templateID = new QName(namespaceServiceTemplate, nodeTemplateID);
			}
		} catch (Exception e) {
			throw new GenericRestException(Status.BAD_REQUEST, "error converting one of the parameters: "
					+ e.getMessage());
		}

		// FIXME: use TOSCA Engine method providing this instead of ToscaReferenceMapper this is just to return
		// something "correct" at the moment
		ConsolidatedPolicies consolidatedPolicies = ToscaServiceHandler.getToscaEngineService()
				.getToscaReferenceMapper().getConsolidatedPolicies(csarID_csarID, templateID);

		if (consolidatedPolicies == null) {
			consolidatedPolicies = new ConsolidatedPolicies();
		}
		
		return consolidatedPolicies;
	}
}
