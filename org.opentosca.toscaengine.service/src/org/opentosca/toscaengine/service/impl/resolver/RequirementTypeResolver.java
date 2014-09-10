package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TRequirementType;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequirementTypeResolver extends GenericResolver {
	
	private Logger LOG = LoggerFactory.getLogger(RequirementTypeResolver.class);
	
	
	public RequirementTypeResolver(ReferenceMapper referenceMapper) {
		super(referenceMapper);
	}
	
	/**
	 * 
	 * @param definitions
	 * @return true if an error occurred, false if not
	 */
	public boolean resolve(Definitions definitions) {
		
		boolean errorOccurred = false;
		
		for (TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			if (element instanceof TRequirementType) {
				
				TRequirementType requirementType = (TRequirementType) element;
				
				// store the RequirementType
				String targetNamespace;
				if ((requirementType.getTargetNamespace() != null) && !requirementType.getTargetNamespace().equals("")) {
					targetNamespace = requirementType.getTargetNamespace();
				} else {
					targetNamespace = definitions.getTargetNamespace();
				}
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, requirementType.getName()), requirementType);
				
				this.LOG.debug("Resolve the RequirementType \"" + targetNamespace + ":" + requirementType.getName() + "\".");
				
				if (requirementType.getRequiredCapabilityType() != null) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(requirementType.getRequiredCapabilityType(), ElementNamesEnum.CAPABILITYTYPE);
				}
				
				// Tags
				// nothing to do here
				
				// DerivedFrom
				if (requirementType.getDerivedFrom() != null) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(requirementType.getDerivedFrom().getTypeRef(), ElementNamesEnum.REQUIREMENTTYPE);
				}
				
				// PropertiesDefinition
				if (requirementType.getPropertiesDefinition() != null) {
					if (new PropertiesDefinitionResolver(this.referenceMapper).resolve(requirementType.getPropertiesDefinition())) {
						this.LOG.error("The NodeType \"" + targetNamespace + ":" + requirementType.getName() + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
						errorOccurred = true;
					}
				}
			}
		}
		return errorOccurred;
	}
	
}
