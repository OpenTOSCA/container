package org.opentosca.placement;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.service.ServiceTemplateService;
import org.opentosca.container.api.util.UriUtil;
// import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.DeploymentTest;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

// TODO it is assumed that the name of the node template is the same as its id.
/**
 * Provides data access functionality to retrieve relationship templates based on a service
 * template. Throughout the class, it is assumed that the passed service template id belongs to the
 * passed CSAR, i.e., it is assumed that a check that this is true is performed earlier.
 *
 */
public class PlacementService {
	
    private static Logger logger = LoggerFactory.getLogger(PlacementService.class);
    
    private final ExecutorService pool = Executors.newFixedThreadPool(5);

    private CsarService csarService;
    private ServiceTemplateService serviceTemplateService;
    private InstanceService instanceService;
    private IToscaEngineService toscaEngineService;
    
    public PlacementService(final ServiceTemplateService serviceTemplateService,
    						final InstanceService instanceService,
    						final CsarService csarService) {
    	this.instanceService = instanceService;
    	this.serviceTemplateService = serviceTemplateService;
    	this.csarService = csarService;
    	
    }
    
    // public PlacementCandidates findPlacementCandidates(final String csarId, final String serviceTemplateId)
	public void findPlacementCandidates(final String csarId, final String serviceTemplateId)
			throws NotFoundException, IllegalAccessException, IllegalArgumentException {
		final CSARID csar = this.serviceTemplateService.checkServiceTemplateExistence(csarId, serviceTemplateId);
		
		logger.info("Inside PlacementService::findPlacementCandidates(csarId, serviceTempladeId)");
		logger.info("csarId: " + csarId);
		logger.info("serviceTemplateId: " + serviceTemplateId);
		
		
		////
		
		final CSARContent csarContent = this.csarService.findById(csarId);
        if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
            logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
            throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
        } else {
        	logger.info("Service template \"" + serviceTemplateId + "\" was found!");
        }

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
		
		
		////


		// final PlacementCandidates result = this.createServiceTemplateInstance(csar, stqn, pi);
		// return result;
	}

    /**
     * Checks whether the specified service template contains a given relationship template.
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName the QName of the service template
     * @param relationshipTemplateId the id of the relationship template to check for
     * @return <code>true</code> when the CSAR contains the service template and the service
     *         template contains the relationship template, otherwise <code>false</code>
     */
	/*
	 * public boolean hasRelationshipTemplate(final String csarId, final QName
	 * serviceTemplateQName, final String relationshipTemplateId) { return
	 * getRelationshipTemplateIdsOfServiceTemplate(csarId,
	 * serviceTemplateQName.toString()).contains(relationshipTemplateId); }
	 */

    /**
     * Gets the properties (as an XML document) of a given relationship template.
     *
     * @param csarId
     * @param serviceTemplateQName
     * @param relationshipTemplateId
     * @return
     */
    public Document getPropertiesOfRelationshipTemplate(final String csarId, final QName serviceTemplateQName,
                                                        final String relationshipTemplateId) {
        final CSARContent csarContent = this.csarService.findById(csarId);
        final CSARID idOfCsar = csarContent.getCSARID();

        if (!this.toscaEngineService.getRelationshipTemplatesOfServiceTemplate(idOfCsar, serviceTemplateQName)
                                    .contains(relationshipTemplateId)) {
            logger.info("Relationship template \"" + relationshipTemplateId + "\" could not be found");
            throw new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found");
        }

        final Document properties =
            this.toscaEngineService.getPropertiesOfTemplate(idOfCsar, serviceTemplateQName, relationshipTemplateId);

        return properties;
    }

    public void setCsarService(final CsarService csarService) {
        this.csarService = csarService;
    }

    public void setToscaEngineService(final IToscaEngineService toscaEngineService) {
        this.toscaEngineService = toscaEngineService;
    }
    
    public void setInstanceService(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }
}
