package org.opentosca.container.legacy.core.engine.resolver.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.legacy.core.engine.resolver.resolver.data.ElementNamesEnum;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationshipTypeImplementationResolver extends GenericResolver {

  private final Logger LOG = LoggerFactory.getLogger(RelationshipTypeImplementationResolver.class);


  public RelationshipTypeImplementationResolver(final ReferenceMapper referenceMapper) {
    super(referenceMapper);
  }

  /**
   * @param definitions
   * @return true if an error occurred, false if not
   */
  public boolean resolve(final Definitions definitions) {

    boolean errorOccurred = false;

    for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
      if (element instanceof TRelationshipTypeImplementation) {

        final TRelationshipTypeImplementation relationshipTypeImplementation =
          (TRelationshipTypeImplementation) element;

        // store the RelationshipTypeImplementation
        String targetNamespace;
        if (relationshipTypeImplementation.getTargetNamespace() != null
          && !relationshipTypeImplementation.getTargetNamespace().equals("")) {
          targetNamespace = relationshipTypeImplementation.getTargetNamespace();
        } else {
          targetNamespace = definitions.getTargetNamespace();
        }

        this.LOG.debug("Resolve the RelationshipTypeImplementation \"" + targetNamespace + ":"
          + relationshipTypeImplementation.getName() + "\".");

        // Tags
        // nothing to do here

        // DerivedFrom
        if (relationshipTypeImplementation.getDerivedFrom() != null
          && relationshipTypeImplementation.getDerivedFrom().getRelationshipTypeImplementationRef() != null) {
          errorOccurred = errorOccurred
            || !this.referenceMapper.searchToscaElementByQNameWithName(relationshipTypeImplementation.getDerivedFrom()
              .getRelationshipTypeImplementationRef(),
            ElementNamesEnum.RELATIONSHIP_TYPE_IMPLEMENTATION);
        }

        // RequieredContainerFeatures
        // nothing to do here

        // ImplementationArtifacts
        if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
          for (final TImplementationArtifact implementationArtifact : relationshipTypeImplementation.getImplementationArtifacts()
            .getImplementationArtifact()) {
            final int iANumber =
              relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()
                .indexOf(implementationArtifact);
            errorOccurred = errorOccurred || new ImplementationArtifactResolver(
              this.referenceMapper).resolve(implementationArtifact, targetNamespace,
              relationshipTypeImplementation.getName(), iANumber);
          }
        }

        this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
          relationshipTypeImplementation.getName()), relationshipTypeImplementation);
      }
    }
    return errorOccurred;
  }

}
