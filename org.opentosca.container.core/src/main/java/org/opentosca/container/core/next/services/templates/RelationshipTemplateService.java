package org.opentosca.container.core.next.services.templates;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.springframework.stereotype.Service;

// TODO it is assumed that the name of the node template is the same as its id.

/**
 * Provides data access functionality to retrieve relationship templates based on a service template. Throughout the
 * class, it is assumed that the passed service template id belongs to the passed CSAR, i.e., it is assumed that a check
 * that this is true is performed earlier.
 *
 * @author Ghareeb Falazi
 */
@Service
public class RelationshipTemplateService {

    @Inject
    private CsarStorageService storage;

    /**
     * Gets a collection of relationship templates associated to a given service template.
     *
     * @param csarId               The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @return A collection of relationship templates stored within the given service template.
     */
    public List<TRelationshipTemplate> getRelationshipTemplatesOfServiceTemplate(final String csarId,
                                                                                 final String serviceTemplateQName) {
        final Csar csar = storage.findById(new CsarId(csarId));
        return csar.serviceTemplates().stream()
            .filter(st -> st.getName().equals(serviceTemplateQName))
            .findFirst()
            .get()
            .getTopologyTemplate()
            .getRelationshipTemplates();
    }

    /**
     * Gets the relationship template specified by its id
     *
     * @param csarId                 The id of the CSAR
     * @param serviceTemplateName    The local name of the service template within the given CSAR
     * @param relationshipTemplateId The id of the relationship template we want to get and that belongs to the
     *                               specified service template
     * @return The relationship template specified by the given id
     * @throws NotFoundException If the service template does not contain the specified relationship template
     */
    public TRelationshipTemplate getRelationshipTemplateById(final String csarId, final String serviceTemplateName,
                                                             final String relationshipTemplateId) throws NotFoundException {
        final Csar csar = storage.findById(new CsarId(csarId));

        return csar.serviceTemplates().stream()
            .filter(st -> st.getName().equals(serviceTemplateName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found"))
            .getTopologyTemplate()
            .getRelationshipTemplate(relationshipTemplateId);
    }

    /**
     * Checks whether the specified service template contains a given relationship template.
     *
     * @param csarId                 The id of the CSAR
     * @param serviceTemplateQName   the QName of the service template
     * @param relationshipTemplateId the id of the relationship template to check for
     * @return <code>true</code> when the CSAR contains the service template and the service
     * template contains the relationship template, otherwise <code>false</code>
     */
    public boolean hasRelationshipTemplate(final String csarId, final QName serviceTemplateQName,
                                           final String relationshipTemplateId) {
        return getRelationshipTemplateIdsOfServiceTemplate(csarId,
            serviceTemplateQName.toString()).contains(relationshipTemplateId);
    }

    /**
     * Gets a collection of relationship template ids associated to a given service template.
     *
     * @param csarId               The id of the CSAR
     * @param serviceTemplateQName the QName of the service template within the given CSAR
     * @return A collection of relationship template ids stored within the given service template.
     */
    private List<String> getRelationshipTemplateIdsOfServiceTemplate(final String csarId,
                                                                     final String serviceTemplateQName) {
        final Csar csar = storage.findById(new CsarId(csarId));
        return csar.serviceTemplates().stream()
            .filter(st -> st.getId().equals(serviceTemplateQName))
            .findFirst()
            .orElseThrow(NotFoundException::new)
            .getTopologyTemplate()
            .getRelationshipTemplates().stream()
            .map(tosca -> tosca.getId())
            .collect(Collectors.toList());
    }
}
