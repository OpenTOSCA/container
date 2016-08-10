package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TPolicyTemplate;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;

public class PolicyTemplateResolver extends GenericResolver {
	
	public PolicyTemplateResolver(ReferenceMapper referenceMapper) {
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
			
			// PolicyTemplate
			if (element instanceof TPolicyTemplate) {
				
				TPolicyTemplate policyTemplate = (TPolicyTemplate) element;
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(definitions.getTargetNamespace(), policyTemplate.getId()), policyTemplate);
				
				// resolve the PolicyType
				if ((policyTemplate.getType() != null)) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(policyTemplate.getType(), ElementNamesEnum.POLICYTYPE);
				}
				
				// Properties
				// nothing to do here
				
				// PropertyConstraints
				// nothing to do here
				
			}
		}
		return errorOccurred;
	}
}
