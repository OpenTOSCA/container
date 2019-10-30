package org.opentosca.container.api.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// TODO it is assumed that the name of the node template is the same as its id.

/**
 * Provides data access functionality to retrieve relationship templates based on a service
 * template. Throughout the class, it is assumed that the passed service template id belongs to the
 * passed CSAR, i.e., it is assumed that a check that this is true is performed earlier.
 *
 * @author Ghareeb Falazi
 */
@Service
public class RelationshipTemplateService {

  private static final Logger LOG = LoggerFactory.getLogger(RelationshipTemplateService.class);

  @Inject
  private CsarStorageService storage;

  /**
   * Gets a collection of relationship templates associated to a given service template.
   *
   * @param csarId               The id of the CSAR
   * @param serviceTemplateQName The QName of the service template within the given CSAR
   * @return A collection of relationship templates stored within the given service template.
   */
  public List<RelationshipTemplateDTO> getRelationshipTemplatesOfServiceTemplate(final String csarId,
                                                                                 final String serviceTemplateQName) {
    final Csar csar = storage.findById(new CsarId(csarId));
    List<TRelationshipTemplate> relationshipTemplates = csar.serviceTemplates().stream()
      .filter(st -> st.getName().equals(serviceTemplateQName))
      .findFirst()
      .get()
      .getTopologyTemplate()
      .getRelationshipTemplates();

    return relationshipTemplates.stream()
      .map(RelationshipTemplateDTO::fromToscaObject)
      .collect(Collectors.toList());
  }

  /**
   * Gets the relationship template specified by its id
   *
   * @param csarId                 The id of the CSAR
   * @param serviceTemplateName    The local name of the service template within the given CSAR
   * @param relationshipTemplateId The id of the relationship template we want to get and that
   *                               belongs to the specified service template
   * @return The relationship template specified by the given id
   * @throws NotFoundException If the service template does not contain the specified relationship
   *                           template
   */
  public RelationshipTemplateDTO getRelationshipTemplateById(final String csarId, final String serviceTemplateName,
                                                             final String relationshipTemplateId) throws NotFoundException {
    final Csar csar = storage.findById(new CsarId(csarId));

    TRelationshipTemplate template = csar.serviceTemplates().stream()
      .filter(st -> st.getName().equals(serviceTemplateName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found"))
      .getTopologyTemplate()
      .getRelationshipTemplate(relationshipTemplateId);

    return RelationshipTemplateDTO.fromToscaObject(template);
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
   * Gets the properties (as an XML document) of a given relationship template.
   */
  public Document getPropertiesOfRelationshipTemplate(final String csarId, final String serviceTemplateName,
                                                      final String relationshipTemplateId) {
    final Csar csar = storage.findById(new CsarId(csarId));
    try {
      final TServiceTemplate serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateName);
      // FIXME forced get here because I'm lazy right now
      final TRelationshipTemplate relationshipTemplate = ToscaEngine.getRelationshipTemplate(csar, serviceTemplate, relationshipTemplateId).get();
      return ((Element)relationshipTemplate.getProperties().getInternalAny()).getOwnerDocument();
    } catch (org.opentosca.container.core.common.NotFoundException e) {
      LOG.warn("Could not get properties of relationship template {} for service template {} in csar {}",
        relationshipTemplateId, serviceTemplateName, csarId);
    }
    return null;
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
