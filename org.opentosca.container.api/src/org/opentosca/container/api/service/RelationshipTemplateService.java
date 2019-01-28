package org.opentosca.container.api.service;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
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
 * @author Ghareeb Falazi
 *
 */
public class RelationshipTemplateService {
    private static Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private CsarService csarService;
    private IToscaEngineService toscaEngineService;

    /**
     * Gets a collection of relationship templates associated to a given service template.
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @return A collection of relationship templates stored within the given service template.
     */
    public List<RelationshipTemplateDTO> getRelationshipTemplatesOfServiceTemplate(final String csarId,
                                                                                   final String serviceTemplateQName) {
        final CSARContent csarContent = this.csarService.findById(csarId);
        final List<String> relationshipTemplateIds =
            this.toscaEngineService.getRelationshipTemplatesOfServiceTemplate(csarContent.getCSARID(),
                                                                              QName.valueOf(serviceTemplateQName));
        final List<RelationshipTemplateDTO> relationshipTemplates = Lists.newArrayList();
        RelationshipTemplateDTO currentRelationshipTemplate;

        for (final String id : relationshipTemplateIds) {
            currentRelationshipTemplate =
                createRelationshipTemplate(csarContent.getCSARID(), QName.valueOf(serviceTemplateQName), id);
            relationshipTemplates.add(currentRelationshipTemplate);
        }

        return relationshipTemplates;
    }

    /**
     * Gets the relationship template specified by its id
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @param relationshipTemplateId The id of the relationship template we want to get and that
     *        belongs to the specified service template
     * @return The relationship template specified by the given id
     * @throws NotFoundException If the service template does not contain the specified relationship
     *         template
     */
    public RelationshipTemplateDTO getRelationshipTemplateById(final String csarId, final QName serviceTemplateQName,
                                                               final String relationshipTemplateId) throws NotFoundException {
        final CSARContent csarContent = this.csarService.findById(csarId);
        final CSARID idOfCsar = csarContent.getCSARID();

        if (!this.toscaEngineService.getRelationshipTemplatesOfServiceTemplate(idOfCsar, serviceTemplateQName)
                                    .contains(relationshipTemplateId)) {
            logger.info("Relationship template \"" + relationshipTemplateId + "\" could not be found");
            throw new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found");
        }

        return createRelationshipTemplate(idOfCsar, serviceTemplateQName, relationshipTemplateId);
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
    public boolean hasRelationshipTemplate(final String csarId, final QName serviceTemplateQName,
                                           final String relationshipTemplateId) {
        return getRelationshipTemplateIdsOfServiceTemplate(csarId,
                                                           serviceTemplateQName.toString()).contains(relationshipTemplateId);
    }

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

    /**
     * Creates a new instance of the RelationshipTemplateDTO class.
     *
     * @param csarId
     * @param serviceTemplateQName
     * @param relationshipTemplateId
     * @return
     */
    private RelationshipTemplateDTO createRelationshipTemplate(final CSARID csarId, final QName serviceTemplateQName,
                                                               final String relationshipTemplateId) {
        final RelationshipTemplateDTO currentRelationshipTemplate = new RelationshipTemplateDTO();
        currentRelationshipTemplate.setId(relationshipTemplateId);
        currentRelationshipTemplate.setName(relationshipTemplateId);
        currentRelationshipTemplate.setRelationshipType(this.toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarId,
                                                                                                                          serviceTemplateQName,
                                                                                                                          relationshipTemplateId)
                                                                               .toString());

        return currentRelationshipTemplate;
    }

    /**
     * Gets a collection of relationship template ids associated to a given service template.
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName the QName of the service template within the given CSAR
     * @return A collection of relationship template ids stored within the given service template.
     */
    private List<String> getRelationshipTemplateIdsOfServiceTemplate(final String csarId,
                                                                     final String serviceTemplateQName) {
        final CSARContent csarContent = this.csarService.findById(csarId);

        return this.toscaEngineService.getRelationshipTemplatesOfServiceTemplate(csarContent.getCSARID(),
                                                                                 QName.valueOf(serviceTemplateQName));
    }

    /* Service Injection */
    /*********************/
    public void setCsarService(final CsarService csarService) {
        this.csarService = csarService;
    }

    public void setToscaEngineService(final IToscaEngineService toscaEngineService) {
        this.toscaEngineService = toscaEngineService;
    }
}
