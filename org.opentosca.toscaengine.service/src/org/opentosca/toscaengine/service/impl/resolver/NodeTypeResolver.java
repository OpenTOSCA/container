package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TCapabilityDefinition;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TInterface;
import org.opentosca.model.tosca.TNodeType;
import org.opentosca.model.tosca.TOperation;
import org.opentosca.model.tosca.TRequirementDefinition;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The NodeTypeResolver resolves references inside of TOSCA NodeTypes according
 * to the TOSCA specification wd14. Each found element and the document in which
 * the element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * 
 * Preconditions for resolving a NodeType: Definitions has to be valid in all
 * kind of meanings.
 * 
 * Copyright 2012 Christian Endres
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class NodeTypeResolver extends GenericResolver {
	
	private Logger LOG = LoggerFactory.getLogger(NodeTypeResolver.class);
	
	
	/**
	 * Instantiate an object of the Resolver to resolve references inside of
	 * NodeTypes. This constructor sets the ReferenceMapper which searches for
	 * references.
	 * 
	 * @param referenceMapper
	 */
	public NodeTypeResolver(ReferenceMapper referenceMapper) {
		super(referenceMapper);
	}
	
	/**
	 * Resolves all NodeTypes inside of a Definitions and stores the mapping
	 * into the ToscaReferenceMapper.
	 * 
	 * @param definitions The Definitions object.
	 */
	public boolean resolve(Definitions definitions) {
		
		boolean errorOccurred = false;
		
		for (TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			if (element instanceof TNodeType) {
				
				TNodeType nodeType = (TNodeType) element;
				
				// store the NodeType
				String targetNamespace;
				if ((nodeType.getTargetNamespace() != null) && !nodeType.getTargetNamespace().equals("")) {
					targetNamespace = nodeType.getTargetNamespace();
				} else {
					targetNamespace = definitions.getTargetNamespace();
				}
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, nodeType.getName()), nodeType);
				
				this.LOG.debug("Resolve the NodeType \"" + targetNamespace + ":" + nodeType.getName() + "\".");
				
				// Tags
				// nothing to do here
				
				// DerivedFrom
				if ((nodeType.getDerivedFrom() != null) && (nodeType.getDerivedFrom().getTypeRef() != null)) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(nodeType.getDerivedFrom().getTypeRef(), ElementNamesEnum.NODETYPE);
				}
				
				// PropertiesDefinition
				if (nodeType.getPropertiesDefinition() != null) {
					if (new PropertiesDefinitionResolver(this.referenceMapper).resolve(nodeType.getPropertiesDefinition())) {
						this.LOG.error("The NodeType \"" + targetNamespace + ":" + nodeType.getName() + "\" specifies both attributes in its child element PropertiesDefinition which is not allowed.");
						errorOccurred = true;
					}
				}
				
				// RequirementDefinition
				if (nodeType.getRequirementDefinitions() != null) {
					for (TRequirementDefinition requirementDefinition : nodeType.getRequirementDefinitions().getRequirementDefinition()) {
						this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, requirementDefinition.getName()), requirementDefinition);
						errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(requirementDefinition.getRequirementType(), ElementNamesEnum.REQUIREMENTTYPE);
					}
				}
				
				// CapabilityDefinitions
				if (nodeType.getCapabilityDefinitions() != null) {
					for (TCapabilityDefinition capabilityDefinition : nodeType.getCapabilityDefinitions().getCapabilityDefinition()) {
						this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, capabilityDefinition.getName()), capabilityDefinition);
						errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(capabilityDefinition.getCapabilityType(), ElementNamesEnum.CAPABILITYTYPE);
					}
				}
				
				// InstanceStates
				// nothing to do here
				
				// Interfaces
				if (nodeType.getInterfaces() != null) {
					for (TInterface iface : nodeType.getInterfaces().getInterface()) {
						this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, iface.getName()), iface);
						
						for (TOperation operation : iface.getOperation()) {
							this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, operation.getName()), operation);
						}
					}
				}
			}
		}
		return errorOccurred;
	}
}
