package org.opentosca.container.core.engine.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.ServiceHandler;
import org.opentosca.container.core.engine.impl.ToscaReferenceMapper;
import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * The ServiceTemplateResolver resolves references inside of TOSCA ServiceTemplates according to the
 * TOSCA specification wd14. Each found element and the document in which the element is nested is
 * stored by the org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * <p>
 * Preconditions for resolving a ServiceTemplate: Definitions has to be valid in all kind of
 * meanings.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class ServiceTemplateResolver extends GenericResolver {

  private final Logger LOG = LoggerFactory.getLogger(ServiceTemplateResolver.class);
  private final ToscaReferenceMapper toscaReferenceMapper;


  /**
   * Instantiate an object of the Resolver to resolve references inside of ServiceTemplates. This
   * constructor sets the ReferenceMapper which searches for references.
   *
   * @param referenceMapper
   * @param toscaReferenceMapper the toscaReferenceMapper used to store ServiceTemplateIds
   */
  public ServiceTemplateResolver(final ReferenceMapper referenceMapper, ToscaReferenceMapper toscaReferenceMapper) {
    super(referenceMapper);
    this.toscaReferenceMapper = toscaReferenceMapper;
  }

  /**
   * Resolves all ServiceTemplates inside of a Definitions and stores the mapping into the
   * ToscaReferenceMapper.
   *
   * @param definitions The Definitions object.
   * @return true if an error occurred, false if not
   */
  public boolean resolve(final Definitions definitions, final CSARID csarID) {

    boolean errorOccurred = false;
    final QName definitionsID = new QName(definitions.getTargetNamespace(), definitions.getId());

    // store the Definitions for further search
    final Document definitionsDocument =
      ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToDocument(definitions);
    this.referenceMapper.storeDocumentIntoReferenceMapper(definitionsID, definitionsDocument);

    // resolve all the ServiceTemplates
    for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {

      if (element instanceof TServiceTemplate) {

        final TServiceTemplate serviceTemplate = (TServiceTemplate) element;
        String targetNamespace = serviceTemplate.getTargetNamespace();
        if (targetNamespace == null || targetNamespace.equals("")) {
          targetNamespace = definitions.getTargetNamespace();
        }
        final QName serviceTemplateID = new QName(targetNamespace, serviceTemplate.getId());

        this.LOG.debug("Resolve the ServiceTemplate \"" + serviceTemplateID + "\".");

        // store the ServiceTemplate
        toscaReferenceMapper.storeServiceTemplateIDForCSARID(serviceTemplateID, csarID);
        this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(serviceTemplateID, serviceTemplate);

        // resolve the SubstitutableNodeType
        if (serviceTemplate.getSubstitutableNodeType() != null) {
          errorOccurred = errorOccurred
            || !this.referenceMapper.searchToscaElementByQNameWithName(serviceTemplate.getSubstitutableNodeType(),
            ElementNamesEnum.NODETYPE);
        }

        // resolve the other data objects
        errorOccurred = errorOccurred || this.resolveTags(serviceTemplate.getTags(), targetNamespace);
        errorOccurred = errorOccurred
          || this.resolveBoundaryDefinitions(serviceTemplate, definitions.getTargetNamespace(), csarID);
        errorOccurred = errorOccurred
          || this.resolveTopologyTemplate(serviceTemplate.getTopologyTemplate(), serviceTemplateID, csarID);
        errorOccurred = errorOccurred
          || this.resolvePlans(serviceTemplate.getPlans(), definitionsID, serviceTemplateID, csarID);
      }
    }
    return errorOccurred;
  }

  /**
   * Resolves Tags inside of a ServiceTemplate and stores the mapping into the ToscaReferenceMapper.
   *
   * @param tags The TTags object.
   * @return true if an error occurred, false if not
   */
  private boolean resolveTags(final TTags tags, final String targetNamespace) {
    // nothing to do here
    return false;
  }

  /**
   * TODO Implement this!
   * <p>
   * Resolves BoundaryDefinitions inside of a ServiceTemplate and stores the mapping into the
   * ToscaReferenceMapper.
   *
   * @param definitionsTargetNamespace
   * @param boundaryDefinitions        the TBoundaryDefinitions object.
   * @return true if an error occurred, false if not
   */
  private boolean resolveBoundaryDefinitions(final TServiceTemplate serviceTemplate,
                                             final String definitionsTargetNamespace, final CSARID csarID) {

    if (serviceTemplate.getBoundaryDefinitions() == null) {
      return false;
    } else {

      String targetNamespace;
      if (null == serviceTemplate.getTargetNamespace()
        || serviceTemplate.getTargetNamespace().trim().equals("")) {
        targetNamespace = definitionsTargetNamespace;
      } else {
        targetNamespace = serviceTemplate.getTargetNamespace();
      }

      final QName serviceTemplateID = new QName(targetNamespace, serviceTemplate.getId());
      final TBoundaryDefinitions boundaryDefinitions = serviceTemplate.getBoundaryDefinitions();

      if (boundaryDefinitions.getProperties() != null) {

        final String propertiesContent =
          ServiceHandler.xmlSerializerService.getXmlSerializer()
            .marshalToString(boundaryDefinitions.getProperties());
        final PropertyMappings propertyMappings = boundaryDefinitions.getProperties().getPropertyMappings();

        // for (TPropertyMapping mapping :
        // propertyMappings.getPropertyMapping()){
        // LOG.debug("mapping: " +
        // mapping.getTargetObjectRef().toString());
        // }

        this.referenceMapper.storeServiceTemplateBoundsProperties(csarID, serviceTemplateID, propertiesContent,
          propertyMappings);

        if (boundaryDefinitions.getProperties().getPropertyMappings() != null) {

          // for (TPropertyMapping propertyMapping :
          // boundaryDefinitions.getProperties().getPropertyMappings().getPropertyMapping())
          // {
          // TODO implement
          // }
          // TODO implement
        }
        // TODO implement
      }

      if (boundaryDefinitions.getInterfaces() != null) {
        // resolve Interfaces
        for (final TExportedInterface iface : boundaryDefinitions.getInterfaces().getInterface()) {

          if (iface.getOperation().size() > 0) {
            this.referenceMapper.storeExportedInterface(csarID, serviceTemplateID, iface);
          }

          // TODO implement

        }

      }

      // TODO implement
    }
    // TODO implement
    return false;
  }

  /**
   * Resolves the TopologyTemplate inside of a ServiceTemplate and stores the mapping into the
   * ToscaReferenceMapper.
   *
   * @param topologyTemplate the TTopologyTemplate object.
   * @param definitions      the Definitions object.
   * @return true if an error occurred, false if not
   */
  private boolean resolveTopologyTemplate(final TTopologyTemplate topologyTemplate, final QName serviceTemplateID,
                                          final CSARID csarID) {

    final String targetNamespace = serviceTemplateID.getNamespaceURI();

    boolean errorOccurred = false;

    // resolve all NodeTemplates and RelationshipTemplates
    if (topologyTemplate.getNodeTemplateOrRelationshipTemplate().size() > 0) {
      for (final TEntityTemplate template : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {

        // NodeTemplates
        if (template instanceof TNodeTemplate) {
          final TNodeTemplate nodeTemplate = (TNodeTemplate) template;

          this.referenceMapper.storeNodeTemplateIDForServiceTemplateAndCSAR(csarID, serviceTemplateID,
            nodeTemplate.getId());

          final QName nodeTemplateID = new QName(targetNamespace, nodeTemplate.getId());
          this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(nodeTemplateID, nodeTemplate);

          // resolve the NodeType
          if (nodeTemplate.getType() != null && !nodeTemplate.getType().toString().equals("")) {
            errorOccurred = errorOccurred
              || !this.referenceMapper.searchToscaElementByQNameWithName(nodeTemplate.getType(),
              ElementNamesEnum.NODETYPE);
          }

          // Properties
          // nothing to do here

          // PropertyConstraints
          // nothing to do here

          // Requirements
          if (nodeTemplate.getRequirements() != null) {
            for (final TRequirement requirement : nodeTemplate.getRequirements().getRequirement()) {
              this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
                requirement.getId()), requirement);
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchToscaElementByQNameWithName(requirement.getType(),
                ElementNamesEnum.REQUIREMENTTYPE);
            }
          }

          // Capabilities
          if (nodeTemplate.getCapabilities() != null) {
            for (final TCapability capability : nodeTemplate.getCapabilities().getCapability()) {
              this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
                capability.getId()), capability);
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchToscaElementByQNameWithName(capability.getType(),
                ElementNamesEnum.CAPABILITYTYPE);
            }
          }

          // Policies
          if (nodeTemplate.getPolicies() != null) {
            for (final TPolicy policy : nodeTemplate.getPolicies().getPolicy()) {
              this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
                policy.getName()), policy);
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchToscaElementByQNameWithName(policy.getPolicyType(),
                ElementNamesEnum.POLICYTYPE);
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchToscaElementByQNameWithID(policy.getPolicyRef());
            }
          }

          // DeploymentArtifacts
          if (nodeTemplate.getDeploymentArtifacts() != null
            && nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact().size() > 0) {
            for (final TDeploymentArtifact deploymentArtifact : nodeTemplate.getDeploymentArtifacts()
              .getDeploymentArtifact()) {
              errorOccurred = errorOccurred
                || new DeploymentArtifactResolver(this.referenceMapper).resolve(deploymentArtifact,
                targetNamespace);
            }
          }
        } else

          // RelationshipTemplates
          if (template instanceof TRelationshipTemplate) {

            final TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;
            this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
              relationshipTemplate.getId()), relationshipTemplate);

            this.referenceMapper.storeRelationshipTemplateIDForServiceTemplateAndCSAR(csarID, serviceTemplateID,
              relationshipTemplate.getId());

            // resolve the RelationshipType
            if (relationshipTemplate.getType() != null
              && !relationshipTemplate.getType().toString().equals("")) {
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchToscaElementByQNameWithName(relationshipTemplate.getType(),
                ElementNamesEnum.RELATIONSHIPTYPE);
            }

            // Properties
            // nothing to do here

            // PropertyConstraints
            // nothing to do here

            // SourceElement
            if (relationshipTemplate.getSourceElement() != null
              && relationshipTemplate.getSourceElement().getRef() != null) {
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchElementViaIDREF(relationshipTemplate.getSourceElement()
                  .getRef(),
                targetNamespace);
            }

            // TargetElement
            if (relationshipTemplate.getTargetElement() != null
              && relationshipTemplate.getTargetElement().getRef() != null) {
              errorOccurred = errorOccurred
                || !this.referenceMapper.searchElementViaIDREF(relationshipTemplate.getTargetElement()
                  .getRef(),
                targetNamespace);
            }

            // RelationshipConstraints
          }
      }
    }

    return errorOccurred;
  }

  /**
   * Resolves Plans inside of a ServiceTemplate and stores the mapping into the ToscaReferenceMapper.
   *
   * @param plans  The TPlans object.
   * @param csarID
   * @return true if an error occurred, false if not
   */
  private boolean resolvePlans(final TPlans plans, final QName definitionsID, final QName serviceTemplateID,
                               final CSARID csarID) {

    if (null == plans) {
      return false;
    }

    for (final TPlan plan : plans.getPlan()) {
      final QName id = new QName(serviceTemplateID.getNamespaceURI(), plan.getId());
      this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(id, plan);
      toscaReferenceMapper.storePlanIDForCSARAndServiceTemplate(csarID, serviceTemplateID, id);
      toscaReferenceMapper.storeContainingDefinitionsID(csarID, id, definitionsID);
      toscaReferenceMapper.storeNamespaceOfPlan(csarID, plan.getId(), serviceTemplateID.getNamespaceURI());
    }

    return false;
  }
}
