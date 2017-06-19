package org.opentosca.container.core.engine.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.tosca.model.Definitions;
import org.opentosca.container.core.tosca.model.TCapabilityType;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityTypeResolver extends GenericResolver {

	private final Logger LOG = LoggerFactory.getLogger(NodeTypeResolver.class);


	public CapabilityTypeResolver(final ReferenceMapper referenceMapper) {
		super(referenceMapper);
	}

	/**
	 * 
	 * @param definitions
	 * @return true if an error occurred, false if not
	 */
	public boolean resolve(final Definitions definitions) {

		boolean errorOccurred = false;

		for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			if (element instanceof TCapabilityType) {

				final TCapabilityType capabilityType = (TCapabilityType) element;

				// store the CapabilityType
				String targetNamespace;
				if ((capabilityType.getTargetNamespace() != null) && !capabilityType.getTargetNamespace().equals("")) {
					targetNamespace = capabilityType.getTargetNamespace();
				} else {
					targetNamespace = definitions.getTargetNamespace();
				}
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, capabilityType.getName()), capabilityType);

				this.LOG.debug("Resolve the CapabilityType \"" + targetNamespace + ":" + capabilityType.getName() + "\".");

				// Tags
				// nothing to do here

				// DerivedFrom
				if ((capabilityType.getDerivedFrom() != null) && (capabilityType.getDerivedFrom().getTypeRef() != null)) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(capabilityType.getDerivedFrom().getTypeRef(), ElementNamesEnum.CAPABILITYTYPE);
				}

				// PropertiesDefinition
				if (capabilityType.getPropertiesDefinition() != null) {
					if ((new PropertiesDefinitionResolver(this.referenceMapper)).resolve(capabilityType.getPropertiesDefinition())) {
						this.LOG.error("The CapabilityType \"" + targetNamespace + ":" + capabilityType.getName() + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
						errorOccurred = true;
					}
				}
			}
		}
		return errorOccurred;
	}
}
