package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TAppliesTo.NodeTypeReference;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TPolicyType;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyTypeResolver extends GenericResolver {
	
	private Logger LOG = LoggerFactory.getLogger(PolicyTypeResolver.class);
	
	
	public PolicyTypeResolver(ReferenceMapper referenceMapper) {
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
			if (element instanceof TPolicyType) {
				
				TPolicyType policyType = (TPolicyType) element;
				
				// store the PolicyType
				String targetNamespace;
				if ((policyType.getTargetNamespace() != null) && !policyType.getTargetNamespace().equals("")) {
					targetNamespace = policyType.getTargetNamespace();
				} else {
					targetNamespace = definitions.getTargetNamespace();
				}
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, policyType.getName()), policyType);
				
				this.LOG.debug("Resolve the PolicyType \"" + targetNamespace + ":" + policyType.getName() + "\".");
				
				// Tags
				// nothing to do here
				
				// DerivedFrom
				if ((policyType.getDerivedFrom() != null) && (policyType.getDerivedFrom().getTypeRef() != null)) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(policyType.getDerivedFrom().getTypeRef(), ElementNamesEnum.POLICYTYPE);
				}
				
				// PropertiesDefinition
				if (policyType.getPropertiesDefinition() != null) {
					if ((new PropertiesDefinitionResolver(this.referenceMapper)).resolve(policyType.getPropertiesDefinition())) {
						this.LOG.error("The PolicyType \"" + targetNamespace + ":" + policyType.getName() + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
						errorOccurred = true;
					}
				}
				
				// AppliesTo
				if (policyType.getAppliesTo() != null) {
					for (NodeTypeReference nodeTypeReference : policyType.getAppliesTo().getNodeTypeReference()) {
						errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(nodeTypeReference.getTypeRef(), ElementNamesEnum.NODETYPE);
					}
				}
			}
		}
		return errorOccurred;
	}
	
}
