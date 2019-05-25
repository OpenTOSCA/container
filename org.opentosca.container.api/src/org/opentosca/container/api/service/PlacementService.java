package org.opentosca.container.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO it is assumed that the name of the node template is the same as its id.
/**
 * Provides data access functionality to retrieve relationship templates based on a service
 * template. Throughout the class, it is assumed that the passed service template id belongs to the
 * passed CSAR, i.e., it is assumed that a check that this is true is performed earlier.
 *
 */
public class PlacementService {
	
    private static Logger logger = LoggerFactory.getLogger(PlacementService.class);
    
    // public PlacementCandidates findPlacementCandidates(final String csarId, final String serviceTemplateId)
	public String findPlacementCandidates(final String csarId, final String serviceTemplateId) {
		
		logger.info("Inside PlacementService::findPlacementCandidates(csarId, serviceTempladeId)");
		logger.info("csarId: " + csarId);
		logger.info("serviceTemplateId: " + serviceTemplateId);
		
		return "PlacmentCandidates";


        // TODO: Check if instance belongs to CSAR and Service Template
		/*
		 * final ServiceTemplateInstance sti = new
		 * ServiceTemplateInstanceRepository().find(Long.valueOf(id)).orElse(null); if
		 * (sti == null) { logger.info("Service template instance \"" + id +
		 * "\" of template \"" + this.serviceTemplateId + "\" could not be found");
		 * throw new NotFoundException("Service template instance \"" + id +
		 * "\" of template \"" + this.serviceTemplateId + "\" could not be found"); }
		 */

        // final URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(result.getId())).build();
        // return Response.created(UriUtil.encode(location)).build();


		// final PlacementCandidates result = this.createServiceTemplateInstance(csar, stqn, pi);
		
	}
}
