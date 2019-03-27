package org.opentosca.container.legacy.core.engine.resolver.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.legacy.core.engine.resolver.resolver.data.ElementNamesEnum;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TAppliesTo.NodeTypeReference;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyTypeResolver extends GenericResolver {

  private final Logger LOG = LoggerFactory.getLogger(PolicyTypeResolver.class);


  public PolicyTypeResolver(final ReferenceMapper referenceMapper) {
    super(referenceMapper);
  }

  /**
   * @param definitions
   * @return true if an error occurred, false if not
   */
  public boolean resolve(final Definitions definitions) {

    boolean errorOccurred = false;

    for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
      if (element instanceof TPolicyType) {

        final TPolicyType policyType = (TPolicyType) element;

        // store the PolicyType
        String targetNamespace;
        if (policyType.getTargetNamespace() != null && !policyType.getTargetNamespace().equals("")) {
          targetNamespace = policyType.getTargetNamespace();
        } else {
          targetNamespace = definitions.getTargetNamespace();
        }
        this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace,
          policyType.getName()), policyType);

        this.LOG.debug("Resolve the PolicyType \"" + targetNamespace + ":" + policyType.getName() + "\".");

        // Tags
        // nothing to do here

        // DerivedFrom
        if (policyType.getDerivedFrom() != null && policyType.getDerivedFrom().getTypeRef() != null) {
          errorOccurred = errorOccurred
            || !this.referenceMapper.searchToscaElementByQNameWithName(policyType.getDerivedFrom()
              .getTypeRef(),
            ElementNamesEnum.POLICY_TYPE);
        }

        // PropertiesDefinition
        if (policyType.getPropertiesDefinition() != null) {
          if (new PropertiesDefinitionResolver(
            this.referenceMapper).resolve(policyType.getPropertiesDefinition())) {
            this.LOG.error("The PolicyType \"" + targetNamespace + ":" + policyType.getName()
              + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
            errorOccurred = true;
          }
        }

        // AppliesTo
        if (policyType.getAppliesTo() != null) {
          for (final NodeTypeReference nodeTypeReference : policyType.getAppliesTo().getNodeTypeReference()) {
            errorOccurred = errorOccurred
              || !this.referenceMapper.searchToscaElementByQNameWithName(nodeTypeReference.getTypeRef(),
              ElementNamesEnum.NODE_TYPE);
          }
        }
      }
    }
    return errorOccurred;
  }

}
