package org.opentosca.toscaengine.service.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TBoundaryDefinitions;
import org.opentosca.model.tosca.TCapability;
import org.opentosca.model.tosca.TDeploymentArtifact;
import org.opentosca.model.tosca.TEntityTemplate;
import org.opentosca.model.tosca.TExportedInterface;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TNodeTemplate;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.TPlans;
import org.opentosca.model.tosca.TPolicy;
import org.opentosca.model.tosca.TRelationshipTemplate;
import org.opentosca.model.tosca.TRequirement;
import org.opentosca.model.tosca.TServiceTemplate;
import org.opentosca.model.tosca.TTags;
import org.opentosca.model.tosca.TTopologyTemplate;
import org.opentosca.toscaengine.service.impl.ToscaEngineServiceImpl;
import org.opentosca.toscaengine.service.impl.resolver.data.ElementNamesEnum;
import org.opentosca.toscaengine.service.impl.servicehandler.ServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * The ServiceTemplateResolver resolves references inside of TOSCA
 * ServiceTemplates according to the TOSCA specification wd14. Each found
 * element and the document in which the element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * 
 * Preconditions for resolving a ServiceTemplate: Definitions has to be valid in
 * all kind of meanings.
 * 
 * Copyright 2012 Christian Endres
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ServiceTemplateResolver extends GenericResolver {
	
	private Logger LOG = LoggerFactory.getLogger(ServiceTemplateResolver.class);
	
	
	/**
	 * Instantiate an object of the Resolver to resolve references inside of
	 * ServiceTemplates. This constructor sets the ReferenceMapper which
	 * searches for references.
	 * 
	 * @param referenceMapper
	 */
	public ServiceTemplateResolver(ReferenceMapper referenceMapper) {
		super(referenceMapper);
	}
	
	/**
	 * Resolves all ServiceTemplates inside of a Definitions and stores the
	 * mapping into the ToscaReferenceMapper.
	 * 
	 * @param definitions The Definitions object.
	 * @return true if an error occurred, false if not
	 */
	public boolean resolve(Definitions definitions, CSARID csarID) {
		
		boolean errorOccurred = false;
		QName definitionsID = new QName(definitions.getTargetNamespace(), definitions.getId());
		
		// store the Definitions for further search
		Document definitionsDocument = ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToDocument(definitions);
		this.referenceMapper.storeDocumentIntoReferenceMapper(definitionsID, definitionsDocument);
		
		// resolve all the ServiceTemplates
		for (TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			
			if (element instanceof TServiceTemplate) {
				
				TServiceTemplate serviceTemplate = (TServiceTemplate) element;
				String targetNamespace = serviceTemplate.getTargetNamespace();
				if ((targetNamespace == null) || targetNamespace.equals("")) {
					targetNamespace = definitions.getTargetNamespace();
				}
				QName serviceTemplateID = new QName(targetNamespace, serviceTemplate.getId());
				
				this.LOG.debug("Resolve the ServiceTemplate \"" + serviceTemplateID + "\".");
				
				// store the ServiceTemplate
				ToscaEngineServiceImpl.toscaReferenceMapper.storeServiceTemplateIDForCSARID(serviceTemplateID, csarID);
				this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(serviceTemplateID, serviceTemplate);
				
				// resolve the SubstitutableNodeType
				if (serviceTemplate.getSubstitutableNodeType() != null) {
					errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(serviceTemplate.getSubstitutableNodeType(), ElementNamesEnum.NODETYPE);
				}
				
				// resolve the other data objects
				errorOccurred = errorOccurred || this.resolveTags(serviceTemplate.getTags(), targetNamespace);
				errorOccurred = errorOccurred || this.resolveBoundaryDefinitions(serviceTemplate, definitions.getTargetNamespace(), csarID);
				errorOccurred = errorOccurred || this.resolveTopologyTemplate(serviceTemplate.getTopologyTemplate(), targetNamespace);
				errorOccurred = errorOccurred || this.resolvePlans(serviceTemplate.getPlans(), definitionsID, serviceTemplateID, csarID);
			}
		}
		return errorOccurred;
	}
	
	/**
	 * Resolves Tags inside of a ServiceTemplate and stores the mapping into the
	 * ToscaReferenceMapper.
	 * 
	 * @param tags The TTags object.
	 * @return true if an error occurred, false if not
	 */
	private boolean resolveTags(TTags tags, String targetNamespace) {
		// nothing to do here
		return false;
	}
	
	/**
	 * TODO Implement this!
	 * 
	 * Resolves BoundaryDefinitions inside of a ServiceTemplate and stores the
	 * mapping into the ToscaReferenceMapper.
	 * 
	 * @param definitionsTargetNamespace
	 * @param boundaryDefinitions the TBoundaryDefinitions object.
	 * @return true if an error occurred, false if not
	 */
	private boolean resolveBoundaryDefinitions(TServiceTemplate serviceTemplate, String definitionsTargetNamespace, CSARID csarID) {
		
		if (serviceTemplate.getBoundaryDefinitions() == null) {
			return false;
		} else {
			
			String targetNamespace;
			if ((null == serviceTemplate.getTargetNamespace()) || serviceTemplate.getTargetNamespace().trim().equals("")) {
				targetNamespace = definitionsTargetNamespace;
			} else {
				targetNamespace = serviceTemplate.getTargetNamespace();
			}
			
			QName serviceTemplateID = new QName(targetNamespace, serviceTemplate.getId());
			TBoundaryDefinitions boundaryDefinitions = serviceTemplate.getBoundaryDefinitions();
			
			if (boundaryDefinitions.getProperties() != null) {
				
				if (boundaryDefinitions.getProperties().getPropertyMappings() != null) {
					
					// for (TPropertyMapping propertyMapping :
					// boundaryDefinitions.getProperties().getPropertyMappings().getPropertyMapping())
					// {
					// TODO implement
					// }
					// TODO implement
				}
				// TODO implement
			}
			
			if (boundaryDefinitions.getInterfaces() != null) {
				// resolve Interfaces
				for (TExportedInterface iface : boundaryDefinitions.getInterfaces().getInterface()) {
					
					if (iface.getOperation().size() > 0) {
						this.referenceMapper.storeExportedInterface(csarID, serviceTemplateID, iface);
					}
					
					// TODO implement
					
				}
				
			}
			
			// TODO implement
		}
		// TODO implement
		return false;
	}
	
	/**
	 * Resolves the TopologyTemplate inside of a ServiceTemplate and stores the
	 * mapping into the ToscaReferenceMapper.
	 * 
	 * @param topologyTemplate the TTopologyTemplate object.
	 * @param definitions the Definitions object.
	 * @return true if an error occurred, false if not
	 */
	private boolean resolveTopologyTemplate(TTopologyTemplate topologyTemplate, String targetNamespace) {
		
		boolean errorOccurred = false;
		
		// resolve all NodeTemplates and RelationshipTemplates
		if (topologyTemplate.getNodeTemplateOrRelationshipTemplate().size() > 0) {
			for (TEntityTemplate template : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
				
				// NodeTemplates
				if (template instanceof TNodeTemplate) {
					TNodeTemplate nodeTemplate = (TNodeTemplate) template;
					
					QName nodeTemplateID = new QName(targetNamespace, nodeTemplate.getId());
					this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(nodeTemplateID, nodeTemplate);
					
					// resolve the NodeType
					if ((nodeTemplate.getType() != null) && !nodeTemplate.getType().toString().equals("")) {
						errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(nodeTemplate.getType(), ElementNamesEnum.NODETYPE);
					}
					
					// Properties
					// nothing to do here
					
					// PropertyConstraints
					// nothing to do here
					
					// Requirements
					if (nodeTemplate.getRequirements() != null) {
						for (TRequirement requirement : nodeTemplate.getRequirements().getRequirement()) {
							this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, requirement.getId()), requirement);
							errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(requirement.getType(), ElementNamesEnum.REQUIREMENTTYPE);
						}
					}
					
					// Capabilities
					if (nodeTemplate.getCapabilities() != null) {
						for (TCapability capability : nodeTemplate.getCapabilities().getCapability()) {
							this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, capability.getId()), capability);
							errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(capability.getType(), ElementNamesEnum.CAPABILITYTYPE);
						}
					}
					
					// Policies
					if (nodeTemplate.getPolicies() != null) {
						for (TPolicy policy : nodeTemplate.getPolicies().getPolicy()) {
							this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, policy.getName()), policy);
							errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(policy.getPolicyType(), ElementNamesEnum.POLICYTYPE);
							errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithID(policy.getPolicyRef());
						}
					}
					
					// DeploymentArtifacts
					if ((nodeTemplate.getDeploymentArtifacts() != null) && (nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact().size() > 0)) {
						for (TDeploymentArtifact deploymentArtifact : nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact()) {
							errorOccurred = errorOccurred || new DeploymentArtifactResolver(this.referenceMapper).resolve(deploymentArtifact, targetNamespace);
						}
					}
				} else
				
				// RelationshipTemplates
				if (template instanceof TRelationshipTemplate) {
					
					TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;
					this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(targetNamespace, relationshipTemplate.getId()), relationshipTemplate);
					
					// resolve the RelationshipType
					if ((relationshipTemplate.getType() != null) && !relationshipTemplate.getType().toString().equals("")) {
						errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(relationshipTemplate.getType(), ElementNamesEnum.RELATIONSHIPTYPE);
					}
					
					// Properties
					// nothing to do here
					
					// PropertyConstraints
					// nothing to do here
					
					// SourceElement
					if ((relationshipTemplate.getSourceElement() != null) && (relationshipTemplate.getSourceElement().getRef() != null)) {
						errorOccurred = errorOccurred || !this.referenceMapper.searchElementViaIDREF(relationshipTemplate.getSourceElement().getRef(), targetNamespace);
					}
					
					// TargetElement
					if ((relationshipTemplate.getTargetElement() != null) && (relationshipTemplate.getTargetElement().getRef() != null)) {
						errorOccurred = errorOccurred || !this.referenceMapper.searchElementViaIDREF(relationshipTemplate.getTargetElement().getRef(), targetNamespace);
					}
					
					// RelationshipConstraints
				}
			}
		}
		
		return errorOccurred;
	}
	
	/**
	 * Resolves Plans inside of a ServiceTemplate and stores the mapping into
	 * the ToscaReferenceMapper.
	 * 
	 * @param plans The TPlans object.
	 * @param csarID
	 * @return true if an error occurred, false if not
	 */
	private boolean resolvePlans(TPlans plans, QName definitionsID, QName serviceTemplateID, CSARID csarID) {
		
		if (null == plans) {
			return false;
		}
		
		for (TPlan plan : plans.getPlan()) {
			QName id = new QName(serviceTemplateID.getNamespaceURI(), plan.getId());
			this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(id, plan);
			ToscaEngineServiceImpl.toscaReferenceMapper.storePlanIDForCSARAndServiceTemplate(csarID, serviceTemplateID, id);
			ToscaEngineServiceImpl.toscaReferenceMapper.storeContainingDefinitionsID(csarID, id, definitionsID);
		}
		
		return false;
	}
}
