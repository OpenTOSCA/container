package org.opentosca.toscaengine.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.model.tosca.TArtifactReference;
import org.opentosca.model.tosca.TArtifactReference.Exclude;
import org.opentosca.model.tosca.TArtifactReference.Include;
import org.opentosca.model.tosca.TArtifactTemplate;
import org.opentosca.model.tosca.TArtifactTemplate.ArtifactReferences;
import org.opentosca.model.tosca.TBoundaryDefinitions;
import org.opentosca.model.tosca.TDefinitions;
import org.opentosca.model.tosca.TDeploymentArtifact;
import org.opentosca.model.tosca.TDeploymentArtifacts;
import org.opentosca.model.tosca.TEntityTemplate;
import org.opentosca.model.tosca.TEntityTemplate.Properties;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TImplementationArtifact;
import org.opentosca.model.tosca.TInterface;
import org.opentosca.model.tosca.TNodeTemplate;
import org.opentosca.model.tosca.TNodeType;
import org.opentosca.model.tosca.TNodeTypeImplementation;
import org.opentosca.model.tosca.TOperation;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.TPropertyConstraint;
import org.opentosca.model.tosca.TRelationshipTemplate;
import org.opentosca.model.tosca.TRelationshipType;
import org.opentosca.model.tosca.TRelationshipTypeImplementation;
import org.opentosca.model.tosca.TRequiredContainerFeature;
import org.opentosca.model.tosca.TServiceTemplate;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.opentosca.toscaengine.service.NodeTemplateInstanceCounts;
import org.opentosca.toscaengine.service.ResolvedArtifacts;
import org.opentosca.toscaengine.service.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.toscaengine.service.ResolvedArtifacts.ResolvedImplementationArtifact;
import org.opentosca.toscaengine.service.impl.consolidation.DefinitionsConsolidation;
import org.opentosca.toscaengine.service.impl.resolver.DefinitionsResolver;
import org.opentosca.toscaengine.service.impl.servicehandler.ServiceHandler;
import org.opentosca.toscaengine.service.impl.toscareferencemapping.ToscaReferenceMapper;
import org.opentosca.toscaengine.service.impl.utils.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is the implementation of the interface
 * org.opentosca.toscaengine.service.IToscaEngineService.
 *
 * @see org.opentosca.toscaengine.service.IToscaEngineService
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 *
 */
public class ToscaEngineServiceImpl implements IToscaEngineService {
	
	public static ToscaReferenceMapper toscaReferenceMapper = null;
	
	private DefinitionsResolver definitionsResolver = null;
	
	private DefinitionsConsolidation definitionsConsolidation = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(ToscaEngineServiceImpl.class);
	
	
	public ToscaEngineServiceImpl() {
		ToscaEngineServiceImpl.toscaReferenceMapper = new ToscaReferenceMapper();
		this.definitionsResolver = new DefinitionsResolver();
		this.definitionsConsolidation = new DefinitionsConsolidation();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IToscaReferenceMapper getToscaReferenceMapper() {
		return ToscaEngineServiceImpl.toscaReferenceMapper;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean resolveDefinitions(CSARID csarID) {
		
		ToscaEngineServiceImpl.LOG.debug("Resolve a Definitions.");
		boolean ret = this.definitionsResolver.resolveDefinitions(csarID);
		if (ret) {
			ret = this.definitionsConsolidation.consolidateCSAR(csarID);
		}
		ToscaEngineServiceImpl.toscaReferenceMapper.printStoredData();
		
		return ret;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getReferencedNodeTypesOfAServiceTemplate(CSARID csarID, QName serviceTemplateID) {
		
		List<QName> nodeTypeQNames = new ArrayList<QName>();
		
		// get the referenced ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
		
		// for NodeTemplates and RelationshipTemplates
		for (TEntityTemplate entity : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
			
			TNodeTemplate nodeTemplate = new TNodeTemplate();
			
			// search inside of a NodeTemplate
			if (entity instanceof TNodeTemplate) {
				nodeTemplate = (TNodeTemplate) entity;
				if (nodeTemplate.getType() != null) {
					if (!nodeTypeQNames.contains(nodeTemplate.getType())) {
						nodeTypeQNames.add(nodeTemplate.getType());
					}
				} else {
					ToscaEngineServiceImpl.LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":" + nodeTemplate.getId() + "does not specify a NodeType.");
				}
			} else
			
			// search inside of a RelationshipTemplate
			if (entity instanceof TRelationshipTemplate) {
				TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) entity;
				
				// SourceElement
				if ((relationshipTemplate.getSourceElement() != null) && (relationshipTemplate.getSourceElement().getRef() != null)) {
					if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate) {
						nodeTemplate = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
						if (nodeTemplate.getType() != null) {
							if (!nodeTypeQNames.contains(nodeTemplate.getType())) {
								nodeTypeQNames.add(nodeTemplate.getType());
							}
						} else {
							ToscaEngineServiceImpl.LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":" + nodeTemplate.getId() + "does not specify a NodeType.");
							
						}
					} else {
						
						ToscaEngineServiceImpl.LOG.debug("The QName \"" + relationshipTemplate.getTargetElement().getRef() + "\" points to a Requirement.");
					}
				} else {
					ToscaEngineServiceImpl.LOG.error("The RelationshipTemplate \"" + serviceTemplate.getTargetNamespace() + ":" + relationshipTemplate.getId() + "does not specify a SourceElement.");
				}
				
				// TargetElement
				if ((relationshipTemplate.getTargetElement() != null) && (relationshipTemplate.getTargetElement().getRef() != null)) {
					if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate) {
						nodeTemplate = new TNodeTemplate();
						nodeTemplate = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
						if (nodeTemplate.getType() != null) {
							if (!nodeTypeQNames.contains(nodeTemplate.getType())) {
								nodeTypeQNames.add(nodeTemplate.getType());
							}
						} else {
							ToscaEngineServiceImpl.LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":" + nodeTemplate.getId() + "does not specify a NodeType.");
						}
					}
				} else {
					ToscaEngineServiceImpl.LOG.error("The RelationshipTemplate \"" + serviceTemplate.getTargetNamespace() + ":" + relationshipTemplate.getId() + "does not specify a TargetElement.");
				}
			}
		}
		
		return nodeTypeQNames;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasOperationOfANodeTypeSpecifiedInputParams(CSARID csarID, QName nodeTypeID, String interfaceName, String operationName) {
		
		TNodeType nodeType = (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);
		
		if (nodeType.getInterfaces() != null) {
			
			for (TInterface iface : nodeType.getInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getInputParameters() != null) && (operation.getInputParameters().getInputParameter() != null)) {
							
							return !operation.getInputParameters().getInputParameter().isEmpty();
							
						}
						
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasOperationOfARelationshipTypeSpecifiedInputParams(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName) {
		
		TRelationshipType relationshipType = (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);
		
		if (relationshipType.getSourceInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getInputParameters() != null) && (operation.getInputParameters().getInputParameter() != null)) {
							
							return !operation.getInputParameters().getInputParameter().isEmpty();
							
						}
						
					}
				}
			}
		}
		
		if (relationshipType.getTargetInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getTargetInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getInputParameters() != null) && (operation.getInputParameters().getInputParameter() != null)) {
							
							return !operation.getInputParameters().getInputParameter().isEmpty();
							
						}
						
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasOperationOfANodeTypeSpecifiedOutputParams(CSARID csarID, QName nodeTypeID, String interfaceName, String operationName) {
		
		TNodeType nodeType = (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);
		
		if (nodeType.getInterfaces() != null) {
			
			for (TInterface iface : nodeType.getInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getOutputParameters() != null) && (operation.getOutputParameters().getOutputParameter() != null)) {
							
							return !operation.getOutputParameters().getOutputParameter().isEmpty();
							
						}
						
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasOperationOfARelationshipTypeSpecifiedOutputParams(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName) {
		
		TRelationshipType relationshipType = (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);
		
		if (relationshipType.getSourceInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getOutputParameters() != null) && (operation.getOutputParameters().getOutputParameter() != null)) {
							
							return !operation.getOutputParameters().getOutputParameter().isEmpty();
							
						}
						
					}
				}
			}
		}
		
		if (relationshipType.getTargetInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getTargetInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getOutputParameters() != null) && (operation.getOutputParameters().getOutputParameter() != null)) {
							
							return !operation.getOutputParameters().getOutputParameter().isEmpty();
							
						}
						
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesInterfaceOfNodeTypeContainOperation(CSARID csarID, QName nodeTypeID, String interfaceName, String operationName) {
		
		TNodeType nodeType = (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);
		
		if (nodeType.getInterfaces() != null) {
			
			for (TInterface iface : nodeType.getInterfaces().getInterface()) {
				
				if (iface.getName().equals(interfaceName)) {
					
					for (TOperation operation : iface.getOperation()) {
						
						if (operation.getName().equals(operationName)) {
							
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesInterfaceOfRelationshipTypeContainOperation(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName) {
		
		TRelationshipType relationshipType = (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);
		
		if (relationshipType.getSourceInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				if (iface.getName().equals(interfaceName)) {
					
					for (TOperation operation : iface.getOperation()) {
						
						if (operation.getName().equals(operationName)) {
							
							return true;
						}
					}
				}
				
			}
		}
		if (relationshipType.getTargetInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getTargetInterfaces().getInterface()) {
				
				if (iface.getName().equals(interfaceName)) {
					
					for (TOperation operation : iface.getOperation()) {
						
						if (operation.getName().equals(operationName)) {
							
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOperationOfRelationshipBoundToSourceNode(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName) {
		
		TRelationshipType relationshipType = (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);
		
		if (relationshipType.getSourceInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				if (iface.getName().equals(interfaceName) || (interfaceName == null)) {
					
					for (TOperation operation : iface.getOperation()) {
						
						if (operation.getName().equals(operationName)) {
							
							return true;
						}
					}
				}
				
			}
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getNodeTypeImplementationsOfNodeType(CSARID csarID, QName nodeTypeID) {
		
		List<QName> listOfNodeTypeImplementationQNames = new ArrayList<QName>();
		
		// search in all Definitions inside a certain CSAR
		for (TDefinitions definitions : ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsOfCSAR(csarID)) {
			
			// search for NodeTypeImplementations
			for (TExtensibleElements entity : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
				if (entity instanceof TNodeTypeImplementation) {
					
					// if the Implementation is for the given NodeType, remember
					// it
					TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) entity;
					
					if (nodeTypeImplementation.getNodeType().equals(nodeTypeID)) {
						
						// remember it
						String targetNamespace;
						if ((nodeTypeImplementation.getTargetNamespace() != null) && !nodeTypeImplementation.getTargetNamespace().equals("")) {
							targetNamespace = nodeTypeImplementation.getTargetNamespace();
						} else {
							targetNamespace = definitions.getTargetNamespace();
						}
						listOfNodeTypeImplementationQNames.add(new QName(targetNamespace, nodeTypeImplementation.getName()));
						
					}
				}
			}
			
		}
		
		return listOfNodeTypeImplementationQNames;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getRelationshipTypeImplementationsOfRelationshipType(CSARID csarID, QName relationshipTypeID) {
		
		List<QName> listOfNodeTypeImplementationQNames = new ArrayList<QName>();
		
		// search in all Definitions inside a certain CSAR
		for (TDefinitions definitions : ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsOfCSAR(csarID)) {
			
			// search for NodeTypeImplementations
			for (TExtensibleElements entity : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
				if (entity instanceof TRelationshipTypeImplementation) {
					
					// if the Implementation is for the given NodeType, remember
					// it
					TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) entity;
					if (relationshipTypeImplementation.getRelationshipType().equals(relationshipTypeID)) {
						
						// remember it
						String targetNamespace;
						if ((relationshipTypeImplementation.getTargetNamespace() != null) && !relationshipTypeImplementation.getTargetNamespace().equals("")) {
							targetNamespace = relationshipTypeImplementation.getTargetNamespace();
						} else {
							targetNamespace = definitions.getTargetNamespace();
						}
						listOfNodeTypeImplementationQNames.add(new QName(targetNamespace, relationshipTypeImplementation.getName()));
						
					}
				}
			}
			
		}
		
		return listOfNodeTypeImplementationQNames;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getImplementationArtifactNamesOfNodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID) {
		
		// return list
		List<String> listOfNames = new ArrayList<String>();
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts, get the names
		if (nodeTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				listOfNames.add(implArt.getName());
			}
		}
		
		return listOfNames;
	}
	
	@Override
	public String getRelatedNodeTemplateID(CSARID csarID, QName serviceTemplateID, String nodeTemplateID, QName relationshipType) {
		
		// get the ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
		
		List<TEntityTemplate> templateList = serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		
		for (TEntityTemplate template : templateList) {
			
			if (template instanceof TRelationshipTemplate) {
				
				TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;
				
				Object sourceElement = relationshipTemplate.getSourceElement().getRef();
				
				if (sourceElement instanceof TNodeTemplate) {
					
					TNodeTemplate sourceNodeTemplate = (TNodeTemplate) sourceElement;
					
					if (sourceNodeTemplate.getId().equals(nodeTemplateID)) {
						
						if (relationshipTemplate.getType().equals(relationshipType)) {
							
							Object targetElement = relationshipTemplate.getTargetElement().getRef();
							
							if (targetElement instanceof TNodeTemplate) {
								
								return ((TNodeTemplate) targetElement).getId();
								
							}
							
						}
					}
				}
				
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The NodeTemplate \"" + nodeTemplateID + "\" has no related NodeTemplate with RelationshipType \"" + relationshipType + "\" or it isn't a NodeTemplate.");
		return null;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetNodeTemplateIDOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID, String relationshipTemplateID) {
		
		// get the ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
		
		List<TEntityTemplate> templateList = serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		
		for (TEntityTemplate template : templateList) {
			
			if (template instanceof TRelationshipTemplate) {
				
				TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;
				
				if (relationshipTemplate.getId().equals(relationshipTemplateID)) {
					
					// if there is a target element
					if (relationshipTemplate.getTargetElement() != null) {
						
						Object targetElement = relationshipTemplate.getTargetElement().getRef();
						
						if (targetElement instanceof TNodeTemplate) {
							
							return ((TNodeTemplate) targetElement).getId();
							
						}
					}
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The Relationship Template \"" + relationshipTemplateID + "\" has no target element or it isn't a NodeTemplate.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceNodeTemplateIDOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID, String relationshipTemplateID) {
		
		// get the ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
		
		List<TEntityTemplate> templateList = serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		
		for (TEntityTemplate template : templateList) {
			
			if (template instanceof TRelationshipTemplate) {
				
				TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;
				
				if (relationshipTemplate.getId().equals(relationshipTemplateID)) {
					
					// if there is a target element
					if (relationshipTemplate.getSourceElement() != null) {
						
						Object sourceElement = relationshipTemplate.getSourceElement().getRef();
						
						if (sourceElement instanceof TNodeTemplate) {
							
							return ((TNodeTemplate) sourceElement).getId();
							
						}
					}
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The Relationship Template \"" + relationshipTemplateID + "\" has no source element or it isn't a NodeTemplate.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getImplementationArtifactNamesOfRelationshipTypeImplementation(CSARID csarID, QName relationshipTypeImplementationID) {
		
		// return list
		List<String> listOfNames = new ArrayList<String>();
		
		// get the RelationshipTypeImplementation
		TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// if there are ImplementationArtifacts, get the names
		if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				listOfNames.add(implArt.getName());
			}
		}
		
		return listOfNames;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document getArtifactSpecificContentOfADeploymentArtifact(CSARID csarID, QName reference, String deploymentArtifactName) {
		
		TDeploymentArtifacts artifacts = null;
		
		Object referenceObj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, reference);
		if (referenceObj instanceof TNodeTypeImplementation) {
			artifacts = ((TNodeTypeImplementation) referenceObj).getDeploymentArtifacts();
		} else if (referenceObj instanceof TNodeTemplate) {
			artifacts = ((TNodeTemplate) referenceObj).getDeploymentArtifacts();
		}
		
		// if there are ImplementationArtifacts
		if (null != artifacts) {
			for (TDeploymentArtifact deployArt : artifacts.getDeploymentArtifact()) {
				if (deployArt.getName().equals(deploymentArtifactName)) {
					
					List<Element> listOfAnyElements = new ArrayList<Element>();
					for (Object obj : deployArt.getAny()) {
						if (obj instanceof Element) {
							listOfAnyElements.add((Element) obj);
						} else {
							ToscaEngineServiceImpl.LOG.error("There is content inside of the DeploymentArtifact \"" + deploymentArtifactName + "\" of the NodeTypeImplementation \"" + reference + "\" which is not a processable DOM Element.");
							return null;
						}
					}
					
					return ServiceHandler.xmlSerializerService.getXmlSerializer().elementsIntoDocument(listOfAnyElements, "DeploymentArtifactSpecificContent");
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested DeploymentArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getRequiredContainerFeaturesOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID) {
		
		// return list
		List<String> listOfStrings = new ArrayList<String>();
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are RequiredContainerFeatures, get the content
		if (nodeTypeImplementation.getRequiredContainerFeatures() != null) {
			for (TRequiredContainerFeature requiredContainerFeature : nodeTypeImplementation.getRequiredContainerFeatures().getRequiredContainerFeature()) {
				listOfStrings.add(requiredContainerFeature.getFeature());
			}
		}
		
		return listOfStrings;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (nodeTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					return implArt.getArtifactType();
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ArtifactType was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getArtifactTypeOfAImplementationArtifactOfARelationshipTypeImplementation(CSARID csarID, QName relationshipTypeImplementationID, String implementationArtifactName) {
		
		// get the RelationshipTypeImplementation
		TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					return implArt.getArtifactType();
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ArtifactType was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getArtifactTemplateOfAImplementationArtifactOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (nodeTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					ToscaEngineServiceImpl.LOG.trace("The ArtifactTemplate is found and has the QName \"" + implArt.getArtifactRef() + "\".");
					return implArt.getArtifactRef();
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getArtifactTemplateOfAImplementationArtifactOfARelationshipTypeImplementation(CSARID csarID, QName relationshipTypeImplementationID, String implementationArtifactName) {
		
		// get the RelationshipTypeImplementation
		TRelationshipTypeImplementation relationTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (relationTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : relationTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					ToscaEngineServiceImpl.LOG.trace("The ArtifactTemplate is found and has the QName \"" + implArt.getArtifactRef() + "\".");
					return implArt.getArtifactRef();
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getReferenceAsNode(CSARID csarID, QName reference) {
		
		// get the ArtifactTemplate
		Node artifactTemplateDoc = (Node) ToscaEngineServiceImpl.toscaReferenceMapper.getReferenceAsNode(csarID, reference);
		
		if (artifactTemplateDoc != null) {
			
			return artifactTemplateDoc;
			
		} else {
			
			ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate was not found.");
			return null;
		}
	}
	
	@Override
	public Node getInputParametersOfANodeTypeOperation(CSARID csarID, QName nodeTypeID, String interfaceName, String operationName) {
		
		TNodeType nodeType = (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);
		
		if (nodeType.getInterfaces() != null) {
			
			for (TInterface iface : nodeType.getInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getInputParameters() != null) && (operation.getInputParameters().getInputParameter() != null)) {
							
							return ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(operation.getInputParameters());
							
						}
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return null;
		
	}
	
	@Override
	public Node getOutputParametersOfANodeTypeOperation(CSARID csarID, QName nodeTypeID, String interfaceName, String operationName) {
		
		TNodeType nodeType = (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);
		
		if (nodeType.getInterfaces() != null) {
			
			for (TInterface iface : nodeType.getInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getOutputParameters() != null) && (operation.getOutputParameters().getOutputParameter() != null)) {
							
							return ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(operation.getOutputParameters());
							
						}
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return null;
		
	}
	
	@Override
	public Node getInputParametersOfARelationshipTypeOperation(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName) {
		
		TRelationshipType relationshipType = (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);
		
		if (relationshipType.getSourceInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getInputParameters() != null) && (operation.getInputParameters().getInputParameter() != null)) {
							
							return ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(operation.getInputParameters());
							
						}
					}
				}
			}
		}
		
		if (relationshipType.getTargetInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getTargetInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getInputParameters() != null) && (operation.getInputParameters().getInputParameter() != null)) {
							
							return ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(operation.getInputParameters());
							
						}
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return null;
		
	}
	
	@Override
	public Node getOutputParametersOfARelationshipTypeOperation(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName) {
		
		TRelationshipType relationshipType = (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);
		
		if (relationshipType.getSourceInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getOutputParameters() != null) && (operation.getOutputParameters().getOutputParameter() != null)) {
							
							return ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(operation.getOutputParameters());
							
						}
					}
				}
			}
		}
		
		if (relationshipType.getTargetInterfaces() != null) {
			
			for (TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {
				
				for (TOperation operation : iface.getOperation()) {
					
					if (operation.getName().equals(operationName) && (iface.getName().equals(interfaceName) || (interfaceName == null))) {
						
						if ((operation.getOutputParameters() != null) && (operation.getOutputParameters().getOutputParameter() != null)) {
							
							return ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(operation.getOutputParameters());
							
						}
					}
				}
			}
		}
		ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
		return null;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (nodeTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					
					List<Element> listOfAnyElements = new ArrayList<Element>();
					for (Object obj : implArt.getAny()) {
						if (obj instanceof Element) {
							listOfAnyElements.add((Element) obj);
						} else {
							ToscaEngineServiceImpl.LOG.error("There is content inside of the ImplementationArtifact \"" + implementationArtifactName + "\" of the NodeTypeImplementation \"" + nodeTypeImplementationID + "\" which is not a processable DOM Element.");
							return null;
						}
					}
					
					return ServiceHandler.xmlSerializerService.getXmlSerializer().elementsIntoDocument(listOfAnyElements, "ImplementationArtifactSpecificContent");
					
					// Node implArtNode =
					// ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(implArt);
					// NodeList childNodes = implArtNode.getChildNodes();
					//
					// try {
					// Document returnDocument =
					// DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
					// Element root =
					// returnDocument.createElement("SpecificContent");
					// returnDocument.appendChild(root);
					//
					// for (int i = 0; i < childNodes.getLength(); i++) {
					// Node node = childNodes.item(i);
					// Node copyNode = returnDocument.importNode(node, true);
					// root.appendChild(copyNode);
					// }
					//
					// return returnDocument;
					//
					// } catch (ParserConfigurationException e) {
					// this.LOG.error(e.getLocalizedMessage());
					// e.printStackTrace();
					// return null;
					// }
					
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document getArtifactSpecificContentOfAImplementationArtifactOfARelationshipTypeImplementation(CSARID csarID, QName relationshipTypeImplementationID, String implementationArtifactName) {
		
		// get the RelationshipTypeImplementation
		TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					
					List<Element> listOfAnyElements = new ArrayList<Element>();
					for (Object obj : implArt.getAny()) {
						if (obj instanceof Element) {
							listOfAnyElements.add((Element) obj);
						} else {
							ToscaEngineServiceImpl.LOG.error("There is content inside of the ImplementationArtifact \"" + implementationArtifactName + "\" of the RelationshipTypeImplementation \"" + relationshipTypeImplementationID + "\" which is not a processable DOM Element.");
							return null;
						}
					}
					
					return ServiceHandler.xmlSerializerService.getXmlSerializer().elementsIntoDocument(listOfAnyElements, "ImplementationArtifactSpecificContent");
					
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (nodeTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					
					return implArt.getInterfaceName();
					
				}
			}
		}
		ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInterfaceOfAImplementationArtifactOfARelationshipTypeImplementation(CSARID csarID, QName relationshipTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					
					return implArt.getInterfaceName();
					
				}
			}
		}
		ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOperationOfAImplementationArtifactOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (nodeTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					
					return implArt.getOperationName();
					
				}
			}
		}
		ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOperationOfAImplementationArtifactOfARelationshipTypeImplementation(CSARID csarID, QName relationshipTypeImplementationID, String implementationArtifactName) {
		
		// get the NodeTypeImplementation
		TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// if there are ImplementationArtifacts
		if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
			for (TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
				
				if (implArt.getName().equals(implementationArtifactName)) {
					
					return implArt.getOperationName();
					
				}
			}
		}
		ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document getPropertiesOfAArtifactTemplate(CSARID csarID, QName artifactTemplateID) {
		
		Object requestedObject = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);
		
		if (requestedObject instanceof TArtifactTemplate) {
			// get the ArtifactTemplate
			TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;
			
			if (artifactTemplate.getProperties() != null) {
				
				if (artifactTemplate.getProperties().getAny() instanceof Element) {
					Document returnDoc = ServiceHandler.xmlSerializerService.getXmlSerializer().elementIntoDocument((Element) artifactTemplate.getProperties().getAny());
					
					if (returnDoc != null) {
						ToscaEngineServiceImpl.LOG.debug("Return the Properties of the ArtifactTemplate \"" + artifactTemplateID + "\".");
						return returnDoc;
					} else {
						ToscaEngineServiceImpl.LOG.error("The content of the Properties of the ArtifactTemplate \"" + artifactTemplateID + "\" could not be written into a DOM Document.");
					}
				} else {
					ToscaEngineServiceImpl.LOG.error("The content of the Properties of the ArtifactTemplate \"" + artifactTemplateID + "\" is not of the type DOM Element.");
				}
			}
		} else {
			ToscaEngineServiceImpl.LOG.error("The requested \"" + artifactTemplateID + "\" is not of the type ArtifactTemplate. It is of the type " + requestedObject.getClass().getSimpleName() + ".");
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TPropertyConstraint> getPropertyConstraintsOfAArtifactTemplate(CSARID csarID, QName artifactTemplateID) {
		
		Object requestedObject = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);
		
		if (requestedObject instanceof TArtifactTemplate) {
			
			// get the ArtifactTemplate
			TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;
			
			if (artifactTemplate.getPropertyConstraints() != null) {
				return artifactTemplate.getPropertyConstraints().getPropertyConstraint();
			} else {
				ToscaEngineServiceImpl.LOG.debug("There are no PropertyConstraints inside of the ArtifactTemplate \"" + artifactTemplateID + "\".");
			}
			
		} else {
			ToscaEngineServiceImpl.LOG.error("The requested \"" + artifactTemplateID + "\" is not of the type ArtifactTemplate. It is of the type " + requestedObject.getClass().getSimpleName() + ".");
		}
		
		return new ArrayList<TPropertyConstraint>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractArtifact> getArtifactsOfAArtifactTemplate(CSARID csarID, QName artifactTemplateID) {
		
		List<AbstractArtifact> artifacts = new ArrayList<AbstractArtifact>();
		// List<File> returnFiles = new ArrayList<File>();
		Object requestedObject = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);
		
		if (requestedObject instanceof TArtifactTemplate) {
			
			// get the ArtifactTemplate
			TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;
			
			if (artifactTemplate.getArtifactReferences() != null) {
				
				CSARContent csar;
				
				try {
					csar = ServiceHandler.coreFileService.getCSAR(csarID);
				} catch (UserException e) {
					ToscaEngineServiceImpl.LOG.warn("An User Exception occured.", e);
					return artifacts;
				}
				
				// iterate the references
				for (TArtifactReference artifactReference : artifactTemplate.getArtifactReferences().getArtifactReference()) {
					
					Set<String> includePatterns = new HashSet<String>();
					Set<String> excludePatterns = new HashSet<String>();
					
					for (Object patternObj : artifactReference.getIncludeOrExclude()) {
						if (patternObj instanceof Include) {
							Include include = (Include) patternObj;
							includePatterns.add(include.getPattern());
						} else {
							Exclude exclude = (Exclude) patternObj;
							excludePatterns.add(exclude.getPattern());
						}
					}
					
					try {
						AbstractArtifact artifact = csar.resolveArtifactReference(artifactReference.getReference(), includePatterns, excludePatterns);
						artifacts.add(artifact);
					} catch (UserException exc) {
						ToscaEngineServiceImpl.LOG.warn("An User Exception occured.", exc);
					} catch (SystemException exc) {
						ToscaEngineServiceImpl.LOG.warn("A System Exception occured.", exc);
						
					}
					
					// all files pointed to by the reference
					// List<AbstractFile> abstractFiles =
					// csarContent.resolveFileRef(artifactReference.getReference());
					
					// adapt the patterns
					// for (Object patternObj :
					// artifactReference.getIncludeOrExclude()) {
					//
					// List<AbstractFile> subset =
					// this.getSubsetMatchingWithPattern(abstractFiles,
					// patternObj);
					//
					// // take new subset or remove all inside the subset
					// if (patternObj instanceof Include) {
					// this.LOG.debug("Use subset as new list of files
					// (Include).");
					// abstractFiles = subset;
					// } else {
					// this.LOG.debug("Remove subset from used list of files
					// (Exclude).");
					// abstractFiles.removeAll(subset);
					// }
					//
					// }
					//
					// // remember the remaining files
					// for (AbstractFile file : abstractFiles) {
					// returnFiles.add(file.getFile());
					// }
					
				}
				
			} else {
				ToscaEngineServiceImpl.LOG.debug("There are no ArtifactReferences in ArtifactTemplate \"" + artifactTemplateID + "\".");
			}
		} else {
			ToscaEngineServiceImpl.LOG.error("The requested \"" + artifactTemplateID + "\" is not of the type ArtifactTemplate. It is of the type " + requestedObject.getClass().getSimpleName() + ".");
		}
		
		return artifacts;
	}
	
	// /**
	// *
	// * @param abstractFiles
	// * @param patternObj
	// * @return
	// */
	// private List<AbstractFile>
	// getSubsetMatchingWithPattern(List<AbstractFile> abstractFiles, Object
	// patternObj) {
	//
	// List<AbstractFile> returnFiles = new ArrayList<AbstractFile>();
	//
	// // get the pattern String
	// String patternString = null;
	// if (patternObj instanceof Include) {
	// patternString = ((Include) patternObj).getPattern();
	// } else {
	// patternString = ((Exclude) patternObj).getPattern();
	// }
	//
	// // regex
	// Pattern pattern = Pattern.compile(patternString);
	// Matcher matcher = null;
	//
	// // match regex with files
	// for (AbstractFile file : abstractFiles) {
	// this.LOG.debug("Try to match the pattern \"" + patternString +
	// "\" to a file with a relative path \"" + file.getRelPath() + "\".");
	// matcher = pattern.matcher(file.getRelPath());
	//
	// // shall the file be included?
	// if (matcher.matches()) {
	// returnFiles.add(file);
	// this.LOG.debug("Include this file to subset to return!");
	// } else {
	// this.LOG.debug("Exclude this file to subset to return!");
	// }
	// }
	//
	// return returnFiles;
	// }
	
	@Override
	public QName getNodeTypeOfNodeTemplate(CSARID csarID, QName serviceTemplateID, String nodeTemplateID) {
		
		QName NodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);
		
		// get the NodeTemplate
		Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, NodeTemplateReference);
		
		if (obj == null) {
			ToscaEngineServiceImpl.LOG.error("The requested NodeTemplate was not found.");
			return null;
		}
		
		if (obj instanceof TNodeTemplate) {
			return ((TNodeTemplate) obj).getType();
		} else if (obj instanceof TNodeType) {
			// funny case with Moodle, since {ns}ApacheWebServer denotes a
			// NodeTemplate AND a NodeType, here we return the given QName
			return NodeTemplateReference;
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested NodeTemplate was not found.");
		return null;
		
	}
	
	// @Override
	// public QName getNodeTypeOfNodeTemplate(CSARID csarID, QName
	// nodeTemplateID) {
	// // get the NodeTypeImplementation
	// Object obj = ToscaEngineServiceImpl.toscaReferenceMapper
	// .getJAXBReference(csarID, nodeTemplateID);
	// if (obj == null) {
	// this.LOG.error("The requested NodeTemplate was not found.");
	// return null;
	// }
	//
	// if (obj instanceof TNodeTemplate) {
	// return ((TNodeTemplate) obj).getType();
	// } else if (obj instanceof TNodeType) {
	// // funny case with Moodle, since {ns}ApacheWebServer denotes a
	// // NodeTemplate AND a NodeType, here we return the given QName
	// return nodeTemplateID;
	// }
	//
	// this.LOG.error("The requested NodeTemplate was not found.");
	// return null;
	// }
	
	@Override
	public QName getRelationshipTypeOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID, String relationshipTemplateID) {
		
		QName RelationshipTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);
		
		// get the RelationshipTemplate
		TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, RelationshipTemplateReference);
		
		// if there are ImplementationArtifacts
		if (relationshipTemplate != null) {
			return relationshipTemplate.getType();
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested RelationshipTemplate was not found.");
		return null;
		
	}
	
	@Override
	public boolean doesNodeTemplateExist(CSARID csarID, QName serviceTemplateID, String nodeTemplateID) {
		
		QName nodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);
		
		// get the NodeTemplate
		Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTemplateReference);
		if (null == obj) {
			ToscaEngineServiceImpl.LOG.warn("The requested reference \"" + nodeTemplateReference + "\" was not found.");
		} else if (obj instanceof TNodeTemplate) {
			ToscaEngineServiceImpl.LOG.trace(nodeTemplateReference + " is a NodeTemplate and exists.");
			return true;
		} else {
			ToscaEngineServiceImpl.LOG.error("The requested reference is not an instance of TNodeTemplate. It seems to be a valid reference but the reference is not a NodeTemplate.");
		}
		
		return false;
		
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean clearCSARContent(CSARID csarID) {
		return ToscaEngineServiceImpl.toscaReferenceMapper.clearCSARContent(csarID);
	}
	
	@Override
	public Document getPropertiesOfNodeTemplate(CSARID csarID, QName serviceTemplateID, String nodeTemplateID) {
		// get the Namespace from the serviceTemplate
		QName NodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);
		
		// get the NodeTypeImplementation
		TNodeTemplate nodeTemplate = (TNodeTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, NodeTemplateReference);
		
		// check if all referenced objects exist and if returned any element is
		// really an element
		if (nodeTemplate != null) {
			Properties properties = nodeTemplate.getProperties();
			if (properties != null) {
				Object any = properties.getAny();
				if (any instanceof Element) {
					Element element = (Element) any;
					return element.getOwnerDocument();
				} else {
					ToscaEngineServiceImpl.LOG.debug("Properties is not of class Element.");
				}
			} else {
				ToscaEngineServiceImpl.LOG.debug("Properties are not set.");
			}
		} else {
			ToscaEngineServiceImpl.LOG.error("The requested NodeTemplate was not found.");
		}
		
		return null;
	}
	
	@Override
	public Document getPropertiesDefinitionOfNodeType(CSARID csarID, QName nodeTypeID) {
		
		// get the NodeType
		TNodeType nodeType = (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);
		
		if (nodeType != null) {
			
			// TODO: fix this hack to get PropertiesDefinition. Needed till
			// you can get it "directly" via the model
			
			Node nodeTypeNode = ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(nodeType);
			
			try {
				
				NodeList list = nodeTypeNode.getChildNodes();
				
				for (int i = 0; i < list.getLength(); i++) {
					
					Node node = list.item(i);
					
					if (node.getLocalName().equals("PropertiesDefinition")) {
						
						Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
						doc.appendChild(doc.importNode(node, true));
						
						return doc;
						
					}
				}
				
				ToscaEngineServiceImpl.LOG.debug("No PropertiesDefinition defined.");
				return null;
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
		}
		
		ToscaEngineServiceImpl.LOG.debug("NodeType {} not found.", nodeTypeID);
		return null;
	}
	
	@Override
	public Document getPropertiesOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID, String relationshipTemplateID) {
		// get the Namespace from the serviceTemplate
		QName relationshipTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);
		
		// get the RelationshipTemplate
		TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTemplateReference);
		
		// check if all referenced objects exist and if returned any element is
		// really an element
		if (relationshipTemplate != null) {
			Properties properties = relationshipTemplate.getProperties();
			if (properties != null) {
				Object any = properties.getAny();
				if (any instanceof Element) {
					Element element = (Element) any;
					return element.getOwnerDocument();
				} else {
					ToscaEngineServiceImpl.LOG.debug("Properties is not of class Element.");
				}
			} else {
				ToscaEngineServiceImpl.LOG.debug("Properties are not set.");
			}
		} else {
			ToscaEngineServiceImpl.LOG.error("The requested RelationshipTemplate was not found.");
		}
		
		return null;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public ResolvedArtifacts getResolvedArtifactsOfNodeTemplate(CSARID csarID, QName nodeTemplateID) {
		
		List<ResolvedDeploymentArtifact> resolvedDAs = this.getNodeTemplateResolvedDAs(csarID, nodeTemplateID);
		
		ResolvedArtifacts result = new ResolvedArtifacts();
		result.setDeploymentArtifacts(resolvedDAs);
		
		return result;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public ResolvedArtifacts getResolvedArtifactsOfNodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID) {
		// TODO: add debug logger
		
		List<ResolvedImplementationArtifact> resolvedIAs = this.getNodeTypeImplResolvedIAs(csarID, nodeTypeImplementationID);
		List<ResolvedDeploymentArtifact> resolvedDAs = this.getNodeTypeImplResolvedDAs(csarID, nodeTypeImplementationID);
		
		ResolvedArtifacts result = new ResolvedArtifacts();
		result.setDeploymentArtifacts(resolvedDAs);
		result.setImplementationArtifacts(resolvedIAs);
		
		return result;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public ResolvedArtifacts getResolvedArtifactsOfRelationshipTypeImplementation(CSARID csarID, QName nodeTypeImplementationID) {
		// TODO: add debug logger
		
		List<ResolvedImplementationArtifact> resolvedIAs = this.getRelationshipTypeImplResolvedIAs(csarID, nodeTypeImplementationID);
		List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<ResolvedDeploymentArtifact>();
		
		ResolvedArtifacts result = new ResolvedArtifacts();
		result.setDeploymentArtifacts(resolvedDAs);
		result.setImplementationArtifacts(resolvedIAs);
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNameOfReference(CSARID csarID, QName reference) {
		Object jaxbReferenceObject = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, reference);
		// check if object was found
		if (jaxbReferenceObject == null) {
			ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - could not retrieve correlating JAXB-Object. Reference " + reference + " seems to be non-existent");
			return null;
		}
		
		// check if class could be retrieved
		Class<? extends Object> jaxbClass = jaxbReferenceObject.getClass();
		
		if (jaxbClass == null) {
			ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - could not retrieve correlating JAXB-Class. Reference " + reference + " existents but is not a valid jaxb-class");
			return null;
		}
		
		try {
			// try to call .getName on the referencing jaxb class
			Method getNameMethod = jaxbClass.getMethod("getName");
			if (getNameMethod == null) {
				ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - could not retrieve getName-Method of JAXB-Class. Reference " + reference + " existents but is not a jaxb-class containing a getName Method");
				return null;
			}
			
			// invoke of parameterless getName()
			String result = (String) getNameMethod.invoke(jaxbReferenceObject, (Object[]) null);
			// return result or emptyString if result == null
			if (result == null) {
				ToscaEngineServiceImpl.LOG.debug("Name attribute of " + reference + " was null - returning \"\"");
				return "";
			} else {
				return result;
			}
			
		} catch (NoSuchMethodException e) {
			String logMsg = String.format("Failed to extract name attribute: The retrieved class %s didn't contain a getName() method. Check if the call with csarid: %s and QName %s was valid! (maybe a bug in code!!!)", jaxbClass, csarID.toString(), reference.toString());
			
			ToscaEngineServiceImpl.LOG.error(logMsg);
		} catch (InvocationTargetException e) {
			ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - an Invocation-exception occured while invoking getName()", e.getCause());
		} catch (Exception e) {
			ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - an exception occured while invoking getName()", e);
		}
		
		return null;
		
	}
	
	/**
	 * Resolves the Deployment-Artifacts of a NodeTemplate
	 * 
	 * @param csarID of the CSAR
	 * @param nodeTemplateID
	 * @return List of ResolvedArtifact containing artifactSpecificContent or
	 *         references. If no Artifact was found the returned list will be
	 *         empty.
	 */
	private List<ResolvedDeploymentArtifact> getNodeTemplateResolvedDAs(CSARID csarID, QName nodeTemplateID) {
		
		List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<ResolvedDeploymentArtifact>();
		
		ToscaEngineServiceImpl.LOG.debug("Trying to fetch DA of NodeTemplate " + nodeTemplateID);
		
		TNodeTemplate nodeTemplate = (TNodeTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTemplateID);
		
		// check if there are implementationArtifact Entries
		if ((nodeTemplate.getDeploymentArtifacts() == null) || (nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact() == null)) {
			// return empty list
			ToscaEngineServiceImpl.LOG.warn("NodeTemplate " + nodeTemplate + " has no DeploymentArtifacts");
			return new ArrayList<ResolvedDeploymentArtifact>();
		}
		
		for (TDeploymentArtifact deployArt : nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact()) {
			ResolvedDeploymentArtifact ra = new ResolvedDeploymentArtifact();
			ra.setName(deployArt.getName());
			ra.setType(deployArt.getArtifactType());
			
			// we assume there is artifactSpecificContent OR a reference to
			// an artifactTemplate
			Document artifactSpecificContent = null;
			if (deployArt.getArtifactRef() != null) {
				// try to dereference artifactReference - build references
				TArtifactTemplate artTemplate = (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, deployArt.getArtifactRef());
				
				// list to store results
				List<String> references = new ArrayList<String>();
				
				ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
				if ((artifactReferences != null) && (artifactReferences.getArtifactReference() != null)) {
					for (TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
						// checking if artifactReference has include
						// patterns
						if ((artifactReference.getIncludeOrExclude() != null) && !artifactReference.getIncludeOrExclude().isEmpty()) {
							for (Object patternObj : artifactReference.getIncludeOrExclude()) {
								if (patternObj instanceof TArtifactReference.Include) {
									TArtifactReference.Include includePattern = (TArtifactReference.Include) patternObj;
									references.add(artifactReference.getReference() + "/" + includePattern.getPattern());
								}
							}
						} else {
							references.add(artifactReference.getReference());
						}
					}
				}
				
				// set resulting list in return object
				ra.setReferences(references);
			} else {
				artifactSpecificContent = this.getArtifactSpecificContentOfADeploymentArtifact(csarID, nodeTemplateID, deployArt.getName());
				ra.setArtifactSpecificContent(artifactSpecificContent);
			}
			
			// add to collection
			resolvedDAs.add(ra);
		}
		
		return resolvedDAs;
	}
	
	/**
	 * resolves the Deployment-Artifacts of the given nodeTypeImplementationID
	 * (get ArtifactSpecificContent OR the reference from the ArtifactTemplate)
	 * 
	 * @param csarID of the CSAR
	 * @param nodeTypeImplementationID of the nodeTypeImplementation
	 * @return List of ResolvedArtifact containing artifactSpecificContent or
	 *         references. If no Artifact was found the returned list will be
	 *         empty.
	 */
	private List<ResolvedDeploymentArtifact> getNodeTypeImplResolvedDAs(CSARID csarID, QName nodeTypeImplementationID) {
		List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<ResolvedDeploymentArtifact>();
		
		ToscaEngineServiceImpl.LOG.debug("Trying to fetch DA of NodeTypeImplementation" + nodeTypeImplementationID.toString());
		
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// check if there are implementationArtifact Entries
		if ((nodeTypeImplementation.getDeploymentArtifacts() == null) || (nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact() == null)) {
			// return empty list
			ToscaEngineServiceImpl.LOG.debug("NodeTypeImplementation " + nodeTypeImplementationID.toString() + " has no DeploymentArtifacts");
			return new ArrayList<ResolvedDeploymentArtifact>();
		}
		
		if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
			for (TDeploymentArtifact deployArt : nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact()) {
				ResolvedDeploymentArtifact ra = new ResolvedDeploymentArtifact();
				ra.setName(deployArt.getName());
				ra.setType(deployArt.getArtifactType());
				
				// we assume there is artifactSpecificContent OR a reference to
				// an artifactTemplate
				Document artifactSpecificContent = null;
				if (deployArt.getArtifactRef() != null) {
					// try to dereference artifactReference - build references
					TArtifactTemplate artTemplate = (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, deployArt.getArtifactRef());
					
					// list to store results
					List<String> references = new ArrayList<String>();
					
					ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
					if ((artifactReferences != null) && (artifactReferences.getArtifactReference() != null)) {
						for (TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
							// checking if artifactReference has include
							// patterns
							if ((artifactReference.getIncludeOrExclude() != null) && !artifactReference.getIncludeOrExclude().isEmpty()) {
								for (Object patternObj : artifactReference.getIncludeOrExclude()) {
									if (patternObj instanceof TArtifactReference.Include) {
										TArtifactReference.Include includePattern = (TArtifactReference.Include) patternObj;
										references.add(artifactReference.getReference() + "/" + includePattern.getPattern());
									}
								}
							} else {
								references.add(artifactReference.getReference());
							}
						}
					}
					
					// set resulting list in return object
					ra.setReferences(references);
				} else {
					artifactSpecificContent = this.getArtifactSpecificContentOfADeploymentArtifact(csarID, nodeTypeImplementationID, deployArt.getName());
					ra.setArtifactSpecificContent(artifactSpecificContent);
				}
				
				// add to collection
				resolvedDAs.add(ra);
			}
		}
		
		return resolvedDAs;
	}
	
	/**
	 * resolves the Deployment-Artifacts of the given nodeTypeImplementationID
	 * (get ArtifactSpecificContent OR the reference from the ArtifactTemplate)
	 * 
	 * @param csarID of the CSAR
	 * @param nodeTypeImplementationID of the nodeTypeImplementation
	 * @return List of ResolvedArtifact containing artifactSpecificContent or
	 *         references. If no Artifact was found the returned list will be
	 *         empty.
	 */
	private List<ResolvedImplementationArtifact> getNodeTypeImplResolvedIAs(CSARID csarID, QName nodeTypeImplementationID) {
		
		List<ResolvedImplementationArtifact> resolvedIAs = new ArrayList<ResolvedImplementationArtifact>();
		
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// check if there are implementationArtifact Entries
		if ((nodeTypeImplementation.getImplementationArtifacts() == null) || (nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact() == null)) {
			new ArrayList<ResolvedImplementationArtifact>();
		}
		
		for (TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
			ResolvedImplementationArtifact ra = new ResolvedImplementationArtifact();
			
			// fill operation and interface name
			ra.setOperationName(implArt.getOperationName());
			ra.setInterfaceName(implArt.getInterfaceName());
			ra.setType(implArt.getArtifactType());
			// we assume there is artifactSpecificContent OR a reference to an
			// artifactTemplate
			Document artifactSpecificContent = null;
			if (implArt.getArtifactRef() != null) {
				// try to dereference artifactReference - build references
				TArtifactTemplate artTemplate = (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, implArt.getArtifactRef());
				
				// list to store results
				List<String> references = new ArrayList<String>();
				
				ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
				if ((artifactReferences != null) && (artifactReferences.getArtifactReference() != null)) {
					
					for (TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
						// checking if artifactReference has include patterns
						if ((artifactReference.getIncludeOrExclude() != null) && !artifactReference.getIncludeOrExclude().isEmpty()) {
							for (Object patternObj : artifactReference.getIncludeOrExclude()) {
								if (patternObj instanceof TArtifactReference.Include) {
									TArtifactReference.Include includePattern = (TArtifactReference.Include) patternObj;
									references.add(artifactReference.getReference() + "/" + includePattern.getPattern());
								}
							}
						} else {
							references.add(artifactReference.getReference());
						}
					}
				}
				
				// set resulting list in return object
				ra.setReferences(references);
			} else {
				artifactSpecificContent = this.getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(csarID, nodeTypeImplementationID, implArt.getName());
				ra.setArtifactSpecificContent(artifactSpecificContent);
			}
			
			// add to collection
			resolvedIAs.add(ra);
		}
		return resolvedIAs;
	}
	
	/**
	 * resolves the Deployment-Artifacts of the given nodeTypeImplementationID
	 * (get ArtifactSpecificContent OR the reference from the ArtifactTemplate)
	 * 
	 * @param csarID of the CSAR
	 * @param nodeTypeImplementationID of the nodeTypeImplementation
	 * @return List of ResolvedArtifact containing artifactSpecificContent or
	 *         references. If no Artifact was found the returned list will be
	 *         empty.
	 */
	private List<ResolvedImplementationArtifact> getRelationshipTypeImplResolvedIAs(CSARID csarID, QName relationshipTypeImplementationID) {
		List<ResolvedImplementationArtifact> resolvedIAs = new ArrayList<ResolvedImplementationArtifact>();
		
		TRelationshipTypeImplementation relationshipTypeImplementation = (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeImplementationID);
		
		// check if there are implementationArtifact Entries
		if ((relationshipTypeImplementation.getImplementationArtifacts() == null) || (relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact() == null)) {
			new ArrayList<ResolvedImplementationArtifact>();
		}
		
		for (TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact()) {
			ResolvedImplementationArtifact ra = new ResolvedImplementationArtifact();
			
			ra.setOperationName(implArt.getOperationName());
			ra.setInterfaceName(implArt.getInterfaceName());
			ra.setType(implArt.getArtifactType());
			// we assume there is artifactSpecificContent OR a reference to an
			// artifactTemplate
			Document artifactSpecificContent = null;
			if (implArt.getArtifactRef() != null) {
				// try to dereference artifactReference - build references
				TArtifactTemplate artTemplate = (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, implArt.getArtifactRef());
				
				// list to store results
				List<String> references = new ArrayList<String>();
				
				ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
				if ((artifactReferences != null) && (artifactReferences.getArtifactReference() != null)) {
					
					for (TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
						// checking if artifactReference has include patterns
						if ((artifactReference.getIncludeOrExclude() != null) && !artifactReference.getIncludeOrExclude().isEmpty()) {
							for (Object patternObj : artifactReference.getIncludeOrExclude()) {
								if (patternObj instanceof TArtifactReference.Include) {
									TArtifactReference.Include includePattern = (TArtifactReference.Include) patternObj;
									references.add(artifactReference.getReference() + "/" + includePattern.getPattern());
								}
							}
						} else {
							references.add(artifactReference.getReference());
						}
					}
				}
				
				// set resulting list in return object
				ra.setReferences(references);
			} else {
				artifactSpecificContent = this.getArtifactSpecificContentOfAImplementationArtifactOfARelationshipTypeImplementation(csarID, relationshipTypeImplementationID, implArt.getName());
				ra.setArtifactSpecificContent(artifactSpecificContent);
			}
			
			// add to collection
			resolvedIAs.add(ra);
		}
		return resolvedIAs;
	}
	
	@Override
	public NodeTemplateInstanceCounts getInstanceCountsOfNodeTemplatesByServiceTemplateID(CSARID csarID, QName serviceTemplateID) {
		
		// get the referenced ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
		List<TEntityTemplate> nodeTemplateOrRelationshipTemplate = serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		
		// store nodeTemplates in own list so we dont alter the jaxb object
		List<TNodeTemplate> nodeTemplates = new ArrayList<TNodeTemplate>();
		for (TEntityTemplate tEntityTemplate : nodeTemplateOrRelationshipTemplate) {
			// only add it if its a nodeTemplate
			if (tEntityTemplate instanceof TNodeTemplate) {
				nodeTemplates.add((TNodeTemplate) tEntityTemplate);
			}
		}
		
		// construct result object (getMin and MaxInstance from JAXB and store
		// them in result object)
		NodeTemplateInstanceCounts counts = new NodeTemplateInstanceCounts();
		for (TNodeTemplate tNodeTemplate : nodeTemplates) {
			QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), tNodeTemplate.getId());
			int minInstances = tNodeTemplate.getMinInstances();
			// in xml the maxInstances attribute is a String because it also can
			// contain "unbounded"
			String maxInstances = tNodeTemplate.getMaxInstances();
			
			counts.addInstanceCount(nodeTemplateQName, minInstances, maxInstances);
		}
		
		return counts;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public String getPlanName(CSARID csar, QName planId) {
		
		ToscaEngineServiceImpl.LOG.trace("Resolve the absolute path of the PlanModelReference of plan \"" + planId + "\" inside of CSAR \"" + csar + "\".");
		
		QName containingDefinitions = ToscaEngineServiceImpl.toscaReferenceMapper.getContainingDefinitionsID(csar, planId);
		
		if (null != containingDefinitions) {
			
			ToscaEngineServiceImpl.LOG.trace("Desired path to the PlanModel is inside the Definitions \"" + containingDefinitions + "\".");
			
			String definitionsLocation = ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsLocation(csar, containingDefinitions);
			
			if (null != definitionsLocation) {
				
				ToscaEngineServiceImpl.LOG.trace("Definitions path is \"" + definitionsLocation + "\".");
				
				TPlan plan = (TPlan) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csar, planId);
				return plan.getName();
				
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("Not able to retrieve to plan name of " + planId.toString() + " inside of CSAR " + csar.toString());
		return null;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public AbstractArtifact getPlanModelReferenceAbstractArtifact(CSARContent csar, QName planId) {
		
		ToscaEngineServiceImpl.LOG.trace("Resolve the absolute path of the PlanModelReference of plan \"" + planId + "\" inside of CSAR \"" + csar.getCSARID() + "\".");
		
		QName containingDefinitions = ToscaEngineServiceImpl.toscaReferenceMapper.getContainingDefinitionsID(csar.getCSARID(), planId);
		
		if (null != containingDefinitions) {
			
			ToscaEngineServiceImpl.LOG.trace("Desired path to the PlanModel is inside the Definitions \"" + containingDefinitions + "\".");
			
			String definitionsLocation = ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsLocation(csar.getCSARID(), containingDefinitions);
			
			if (null != definitionsLocation) {
				
				ToscaEngineServiceImpl.LOG.trace("Definitions path is \"" + definitionsLocation + "\".");
				
				TPlan plan = (TPlan) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csar.getCSARID(), planId);
				String planModelReferenceLocation = plan.getPlanModelReference().getReference();
				ToscaEngineServiceImpl.LOG.trace("planModelReferenceLocation: " + planModelReferenceLocation);
				String absoluteLocation = PathResolver.resolveRelativePath(definitionsLocation, planModelReferenceLocation, csar);
				
				ToscaEngineServiceImpl.LOG.trace("Absolute path to the PlanModel is \"" + absoluteLocation + "\".");
				
				try {
					
					AbstractArtifact artifact = csar.resolveArtifactReference(absoluteLocation);
					if (null != artifact) {
						return artifact;
					}
					
				} catch (UserException e) {
					ToscaEngineServiceImpl.LOG.error(e.getLocalizedMessage());
					e.printStackTrace();
				} catch (SystemException e) {
					ToscaEngineServiceImpl.LOG.error(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("There was an error while resolving the absolute path of the PlanModelReference of plan \"" + planId + "\" inside of CSAR \"" + csar.getCSARID() + "\".");
		return null;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getArtifactReferenceWithinArtifactTemplate(CSARID csarID, QName artifactTemplate) {
		
		List<String> references = new ArrayList<String>();
		
		Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplate);
		
		if (obj != null) {
			TArtifactTemplate artifactTemplateObject = (TArtifactTemplate) obj;
			
			List<TArtifactReference> tArtifactReferences = artifactTemplateObject.getArtifactReferences().getArtifactReference();
			
			for (TArtifactReference tArtifactReference : tArtifactReferences) {
				references.add(tArtifactReference.getReference());
			}
		}
		return references;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public QName getArtifactTypeOfArtifactTemplate(CSARID csarID, QName artifactTemplate) {
		
		QName artifactType = null;
		
		Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplate);
		
		if (obj != null) {
			TArtifactTemplate artifactTemplateObject = (TArtifactTemplate) obj;
			
			artifactType = artifactTemplateObject.getType();
			
		}
		return artifactType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getDeploymentArtifactNamesOfNodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID) {
		
		// return list
		List<String> listOfNames = new ArrayList<String>();
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are ImplementationArtifacts, get the names
		if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
			for (TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact()) {
				listOfNames.add(da.getName());
			}
		}
		
		return listOfNames;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getArtifactTemplateOfADeploymentArtifactOfANodeTypeImplementation(CSARID csarID, QName nodeTypeImplementationID, String deploymentArtifactName) {
		
		// get the NodeTypeImplementation
		TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);
		
		// if there are DeploymentArtifacts
		if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
			for (TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact()) {
				
				if (da.getName().equals(deploymentArtifactName)) {
					ToscaEngineServiceImpl.LOG.trace("The ArtifactTemplate is found and has the QName \"" + da.getArtifactRef() + "\".");
					return da.getArtifactRef();
				}
			}
		}
		
		ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate was not found.");
		return null;
	}
	
	@Override
	public TBoundaryDefinitions getBoundaryDefinitionsOfServiceTemplate(CSARID csarId, QName serviceTemplateId) {
		// get the referenced ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

		return serviceTemplate.getBoundaryDefinitions();
	}
	
}
