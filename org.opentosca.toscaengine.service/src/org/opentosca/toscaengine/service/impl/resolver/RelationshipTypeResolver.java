package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TRelationshipType;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RelationshipTypeResolver resolves references inside of TOSCA
 * RelationshipTypes according to the TOSCA specification wd14. Each found
 * element and the document in which the element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * 
 * Preconditions for resolving a RelationshipType: Definitions has to be valid
 * in all kind of meanings.
 * 
 * Copyright 2012 Christian Endres
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class RelationshipTypeResolver extends GenericResolver {
	
	private Logger LOG = LoggerFactory.getLogger(RelationshipTypeResolver.class);
	
	
	/**
	 * Instantiate an object of the Resolver to resolve references inside of
	 * RelationshipTypes. This constructor sets the ReferenceMapper which
	 * searches for references.
	 * 
	 * @param referenceMapper
	 */
	public RelationshipTypeResolver(ReferenceMapper referenceMapper) {
		super(referenceMapper);
	}
	
	/**
	 * Resolves all RelationshipTypes inside of a Definitions and stores the
	 * mapping into the ToscaReferenceMapper.
	 * 
	 * @param definitions The Definitions object.
	 * @return true if an error occurred, false if not
	 */
	public boolean resolve(Definitions definitions) {
		
		boolean errorOccurred = false;
		
		for (TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			if (element instanceof TRelationshipType) {
				
				TRelationshipType relationshipType = (TRelationshipType) element;
				
				// store the RelationshipType
				String targetNamespace;
				if ((relationshipType.getTargetNamespace() != null) && !relationshipType.getTargetNamespace().equals("")) {
					targetNamespace = relationshipType.getTargetNamespace();
				} else {
					targetNamespace = definitions.getTargetNamespace();
				}
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, relationshipType.getName()), relationshipType);
				
				this.LOG.debug("Resolve the RelationshipType \"" + targetNamespace + ":" + relationshipType.getName() + "\".");
				
				// Tags
				// nothing to do here
				
				// DerivedFrom
				if (relationshipType.getDerivedFrom() != null) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(relationshipType.getDerivedFrom().getTypeRef(), ElementNamesEnum.RELATIONSHIPTYPE);
				}
				
				// PropertiesDefinition
				if (relationshipType.getPropertiesDefinition() != null) {
					if (new PropertiesDefinitionResolver(this.referenceMapper).resolve(relationshipType.getPropertiesDefinition())) {
						this.LOG.error("The RelationshipType \"" + targetNamespace + ":" + relationshipType.getName() + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
						errorOccurred = true;
					}
				}
				
				// InstanceStates
				// nothing to do here
				
				// SourceInterfaces
				// TODO not clear what to implement and what we aim for
				
				// TargetInterfaces
				// TODO not clear what to implement and what we aim for
				
				// TODO implement the rules of the spec mentioned in
				// 8.2-ValidSource/Target
				// ValidSource
				if (relationshipType.getValidSource() != null) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(relationshipType.getValidSource().getTypeRef(), ElementNamesEnum.ALLELEMENTS);
				}
				
				// TODO implement the rules of the spec mentioned in
				// 8.2-ValidSource/Target
				// ValidTarget
				if (relationshipType.getValidTarget() != null) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(relationshipType.getValidTarget().getTypeRef(), ElementNamesEnum.ALLELEMENTS);
				}
			}
		}
		return errorOccurred;
	}
}
