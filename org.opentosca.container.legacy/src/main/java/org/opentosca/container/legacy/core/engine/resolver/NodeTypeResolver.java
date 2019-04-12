package org.opentosca.container.legacy.core.engine.resolver.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.legacy.core.engine.resolver.resolver.data.ElementNamesEnum;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The NodeTypeResolver resolves references inside of TOSCA NodeTypes according to the TOSCA
 * specification wd14. Each found element and the document in which the element is nested is stored
 * by the org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * <p>
 * Preconditions for resolving a NodeType: Definitions has to be valid in all kind of meanings.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class NodeTypeResolver extends GenericResolver {

  private final Logger LOG = LoggerFactory.getLogger(NodeTypeResolver.class);


  /**
   * Instantiate an object of the Resolver to resolve references inside of NodeTypes. This constructor
   * sets the ReferenceMapper which searches for references.
   *
   * @param referenceMapper
   */
  public NodeTypeResolver(final ReferenceMapper referenceMapper) {
    super(referenceMapper);
  }

  /**
   * Resolves all NodeTypes inside of a Definitions and stores the mapping into the
   * ToscaReferenceMapper.
   *
   * @param definitions The Definitions object.
   */
  public boolean resolve(final Definitions definitions) {

    boolean errorOccurred = false;

    for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
      if (element instanceof TNodeType) {

        final TNodeType nodeType = (TNodeType) element;

        // store the NodeType
        String targetNamespace;
        if (nodeType.getTargetNamespace() != null && !nodeType.getTargetNamespace().equals("")) {
          targetNamespace = nodeType.getTargetNamespace();
        } else {
          targetNamespace = definitions.getTargetNamespace();
        }
        this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
          nodeType.getName()), nodeType);

        this.LOG.debug("Resolve the NodeType \"" + targetNamespace + ":" + nodeType.getName() + "\".");

        // Tags
        // nothing to do here

        // DerivedFrom
        if (nodeType.getDerivedFrom() != null && nodeType.getDerivedFrom().getTypeRef() != null) {
          errorOccurred = errorOccurred
            || !this.referenceMapper.searchToscaElementByQNameWithName(nodeType.getDerivedFrom()
              .getTypeRef(),
            ElementNamesEnum.NODE_TYPE);
        }

        // PropertiesDefinition
        if (nodeType.getPropertiesDefinition() != null) {
          if (new PropertiesDefinitionResolver(
            this.referenceMapper).resolve(nodeType.getPropertiesDefinition())) {
            this.LOG.error("The NodeType \"" + targetNamespace + ":" + nodeType.getName()
              + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
            errorOccurred = true;
          }
        }

        // RequirementDefinition
        if (nodeType.getRequirementDefinitions() != null) {
          for (final TRequirementDefinition requirementDefinition : nodeType.getRequirementDefinitions()
            .getRequirementDefinition()) {
            this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
              requirementDefinition.getName()), requirementDefinition);
            errorOccurred = errorOccurred
              || !this.referenceMapper.searchToscaElementByQNameWithName(requirementDefinition.getRequirementType(),
              ElementNamesEnum.REQUIREMENT_TYPE);
          }
        }

        // CapabilityDefinitions
        if (nodeType.getCapabilityDefinitions() != null) {
          for (final TCapabilityDefinition capabilityDefinition : nodeType.getCapabilityDefinitions()
            .getCapabilityDefinition()) {
            this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
              capabilityDefinition.getName()), capabilityDefinition);
            errorOccurred = errorOccurred
              || !this.referenceMapper.searchToscaElementByQNameWithName(capabilityDefinition.getCapabilityType(),
              ElementNamesEnum.CAPABILITY_TYPE);
          }
        }

        // InstanceStates
        // nothing to do here

        // Interfaces
        if (nodeType.getInterfaces() != null) {
          for (final TInterface iface : nodeType.getInterfaces().getInterface()) {
            this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
              iface.getName()), iface);

            for (final TOperation operation : iface.getOperation()) {
              this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
                operation.getName()), operation);
            }
          }
        }
      }
    }
    return errorOccurred;
  }
}
