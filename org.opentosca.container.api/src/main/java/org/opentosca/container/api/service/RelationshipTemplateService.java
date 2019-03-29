package org.opentosca.container.api.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
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
   * @param serviceTemplateQName   The QName of the service template within the given CSAR
   * @param relationshipTemplateId The id of the relationship template we want to get and that
   *                               belongs to the specified service template
   * @return The relationship template specified by the given id
   * @throws NotFoundException If the service template does not contain the specified relationship
   *                           template
   */
  public RelationshipTemplateDTO getRelationshipTemplateById(final String csarId, final QName serviceTemplateQName,
                                                             final String relationshipTemplateId) throws NotFoundException {
    final Csar csar = storage.findById(new CsarId(csarId));

    TRelationshipTemplate template = csar.serviceTemplates().stream()
      // FIXME check semantic correctness here!
      .filter(st -> st.getId().equals(serviceTemplateQName.toString()))
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
   *
   * @param csarId
   * @param serviceTemplateQName
   * @param relationshipTemplateId
   * @return
   */
  public Document getPropertiesOfRelationshipTemplate(final String csarId, final QName serviceTemplateQName,
                                                      final String relationshipTemplateId) {
    final Csar csar = storage.findById(new CsarId(csarId));

    TRelationshipTemplate.Properties props = csar.serviceTemplates().stream()
      // FIXME check qname equality to id?
      .filter(st -> st.getId().equals(serviceTemplateQName.toString()))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Relationship template \"" + relationshipTemplateId + "\" could not be found"))
      .getTopologyTemplate()
      .getRelationshipTemplate(relationshipTemplateId)
      .getProperties();
    // FIXME do null- and typechecking. possibly do not even return a Document here!
    return ((Element) props.getAny()).getOwnerDocument();
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

  /* Service Injection */

  /*********************/
  public void setCsarStorageService(final CsarStorageService storage) {
    this.storage = storage;
  }
}
