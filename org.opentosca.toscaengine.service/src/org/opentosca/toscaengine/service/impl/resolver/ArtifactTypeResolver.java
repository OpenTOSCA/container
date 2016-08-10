package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TArtifactType;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactTypeResolver extends GenericResolver {
	
	private Logger LOG = LoggerFactory.getLogger(ArtifactTypeResolver.class);
	
	
	public ArtifactTypeResolver(ReferenceMapper referenceMapper) {
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
			if (element instanceof TArtifactType) {
				
				TArtifactType artifactType = (TArtifactType) element;
				
				// store the ArtifactType
				String targetNamespace;
				if ((artifactType.getTargetNamespace() != null) && !artifactType.getTargetNamespace().equals("")) {
					targetNamespace = artifactType.getTargetNamespace();
				} else {
					targetNamespace = definitions.getTargetNamespace();
				}
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, artifactType.getName()), artifactType);
				
				this.LOG.debug("Resolve the ArtifactType \"" + targetNamespace + ":" + artifactType.getName() + "\".");
				
				// DerivedFrom
				if ((artifactType.getDerivedFrom() != null) && (artifactType.getDerivedFrom().getTypeRef() != null)) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(artifactType.getDerivedFrom().getTypeRef(), ElementNamesEnum.ARTIFACTTYPE);
				}
				
				// PropertiesDefinition
				// if (artifactType.getPropertiesDefinition() != null) {
				// if (!(new
				// PropertiesDefinitionResolver(this.referenceMapper)).resolve(artifactType.getPropertiesDefinition()))
				// {
				// this.LOG.error("The ArtifactType \"" + targetNamespace + ":"
				// + artifactType.getName() +
				// "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
				// }
				// }
			}
		}
		return errorOccurred;
	}
	
}
