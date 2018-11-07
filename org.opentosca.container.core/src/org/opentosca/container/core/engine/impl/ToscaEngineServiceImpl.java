package org.opentosca.container.core.engine.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.engine.NodeTemplateInstanceCounts;
import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedImplementationArtifact;
import org.opentosca.container.core.engine.impl.consolidation.DefinitionsConsolidation;
import org.opentosca.container.core.engine.impl.resolver.DefinitionsResolver;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactReference.Exclude;
import org.eclipse.winery.model.tosca.TArtifactReference.Include;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactTemplate.ArtifactReferences;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequiredContainerFeature;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

/**
 * This is the implementation of the interface
 * org.opentosca.toscaengine.service.IToscaEngineService.
 *
 * @see org.opentosca.container.core.engine.IToscaEngineService
 */
public class ToscaEngineServiceImpl implements IToscaEngineService {

    // FIXME check the necessity of this being static?
    private static final ToscaReferenceMapper toscaReferenceMapper = new ToscaReferenceMapper();

    private final DefinitionsResolver definitionsResolver = new DefinitionsResolver(toscaReferenceMapper);
    private final DefinitionsConsolidation definitionsConsolidation =
        new DefinitionsConsolidation(toscaReferenceMapper);

    private static final Logger LOG = LoggerFactory.getLogger(ToscaEngineServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public IToscaReferenceMapper getToscaReferenceMapper() {
        return toscaReferenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resolveDefinitions(final CSARID csarID) {

        LOG.debug("Resolve a Definitions.");
        boolean ret = this.definitionsResolver.resolveDefinitions(csarID);
        if (ret) {
            ret = this.definitionsConsolidation.consolidateCSAR(csarID);
        }
        toscaReferenceMapper.printStoredData();

        return ret;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getReferencedNodeTypesOfAServiceTemplate(final CSARID csarID, final QName serviceTemplateID) {

        final List<QName> nodeTypeQNames = new ArrayList<>();

        // get the referenced ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

        if (serviceTemplate == null || serviceTemplate.getTopologyTemplate() == null) { return nodeTypeQNames; }
        
        // for NodeTemplates and RelationshipTemplates
        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {

            TNodeTemplate nodeTemplate = new TNodeTemplate();

            // search inside of a NodeTemplate
            if (entity instanceof TNodeTemplate) {
                nodeTemplate = (TNodeTemplate) entity;
                if (nodeTemplate.getType() != null) {
                    if (!nodeTypeQNames.contains(nodeTemplate.getType())) {
                        nodeTypeQNames.add(nodeTemplate.getType());
                    }
                } else {
                    LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":" + nodeTemplate.getId()
                        + "does not specify a NodeType.");
                }
            } else

            // search inside of a RelationshipTemplate
            if (entity instanceof TRelationshipTemplate) {
                final TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) entity;

                // SourceElement
                if (relationshipTemplate.getSourceElement() != null
                    && relationshipTemplate.getSourceElement().getRef() != null) {
                    if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate) {
                        nodeTemplate = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
                        if (nodeTemplate.getType() != null) {
                            if (!nodeTypeQNames.contains(nodeTemplate.getType())) {
                                nodeTypeQNames.add(nodeTemplate.getType());
                            }
                        } else {
                            LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":"
                                + nodeTemplate.getId() + "does not specify a NodeType.");

                        }
                    } else {

                        LOG.debug("The QName \"" + relationshipTemplate.getTargetElement().getRef()
                            + "\" points to a Requirement.");
                    }
                } else {
                    LOG.error("The RelationshipTemplate \"" + serviceTemplate.getTargetNamespace() + ":"
                        + relationshipTemplate.getId() + "does not specify a SourceElement.");
                }

                // TargetElement
                if (relationshipTemplate.getTargetElement() != null
                    && relationshipTemplate.getTargetElement().getRef() != null) {
                    if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate) {
                        nodeTemplate = new TNodeTemplate();
                        nodeTemplate = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
                        if (nodeTemplate.getType() != null) {
                            if (!nodeTypeQNames.contains(nodeTemplate.getType())) {
                                nodeTypeQNames.add(nodeTemplate.getType());
                            }
                        } else {
                            LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":"
                                + nodeTemplate.getId() + "does not specify a NodeType.");
                        }
                    }
                } else {
                    LOG.error("The RelationshipTemplate \"" + serviceTemplate.getTargetNamespace() + ":"
                        + relationshipTemplate.getId() + "does not specify a TargetElement.");
                }
            }
        }

        return nodeTypeQNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOperationOfANodeTypeSpecifiedInputParams(final CSARID csarID, final QName nodeTypeID,
                                                               final String interfaceName, final String operationName) {
        return this.getNodeTypeHierarchy(csarID, nodeTypeID).stream()
                   .map(qname -> (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, qname))
                   .filter(nt -> nt.getInterfaces() != null).flatMap(nt -> nt.getInterfaces().getInterface().stream())
                   .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
                   .flatMap(iface -> iface.getOperation().stream())
                   .filter(op -> op.getName().equals(operationName) && op.getInputParameters() != null
                       && op.getInputParameters().getInputParameter() != null)
                   .findFirst().map(found -> !found.getInputParameters().getInputParameter().isEmpty())
                   .orElseGet(() -> {
                       LOG.debug("The requested operation was not found.");
                       return false;
                   });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOperationOfARelationshipTypeSpecifiedInputParams(final CSARID csarID,
                                                                       final QName relationshipTypeID,
                                                                       final String interfaceName,
                                                                       final String operationName) {

        final TRelationshipType relationshipType =
            (TRelationshipType) toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);

        return Stream.of(Optional.ofNullable(relationshipType.getSourceInterfaces()).map(i -> i.getInterface()),
                         Optional.ofNullable(relationshipType.getTargetInterfaces()).map(i -> i.getInterface()))
                 .flatMap(ol -> ol.orElse(Collections.emptyList()).stream())
                 .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
                 .flatMap(iface -> iface.getOperation().stream())
                 .filter(op -> op.getName().equals(operationName) && op.getInputParameters() != null
                     && op.getInputParameters().getInputParameter() != null)
                 .findFirst().map(found -> !found.getInputParameters().getInputParameter().isEmpty())
                 .orElseGet(() -> {
                     LOG.debug("The requested operation was not found.");
                     return false;
                 });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOperationOfANodeTypeSpecifiedOutputParams(final CSARID csarID, final QName nodeTypeID,
                                                                final String interfaceName,
                                                                final String operationName) {

        return this.getNodeTypeHierarchy(csarID, nodeTypeID).stream()
                   .map(qname -> (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, qname))
                   .filter(n -> n.getInterfaces() != null).flatMap(n -> n.getInterfaces().getInterface().stream())
                   .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
                   .flatMap(iface -> iface.getOperation().stream())
                   .filter(op -> op.getName().equals(operationName) && op.getOutputParameters() != null
                       && op.getOutputParameters().getOutputParameter() != null)
                   .findFirst().map(found -> !found.getOutputParameters().getOutputParameter().isEmpty())
                   .orElseGet(() -> {
                       LOG.debug("The requested operation was not found.");
                       return false;
                   });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOperationOfARelationshipTypeSpecifiedOutputParams(final CSARID csarID,
                                                                        final QName relationshipTypeID,
                                                                        final String interfaceName,
                                                                        final String operationName) {

        final TRelationshipType relationshipType =
            (TRelationshipType) toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);

        return Stream.of(Optional.ofNullable(relationshipType.getSourceInterfaces()).map(i -> i.getInterface()),
                         Optional.ofNullable(relationshipType.getTargetInterfaces()).map(i -> i.getInterface()))
                 .flatMap(ol -> ol.orElse(Collections.emptyList()).stream())
                 .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
                 .flatMap(iface -> iface.getOperation().stream())
                 .filter(operation -> operation.getName().equals(operationName)
                     && operation.getOutputParameters() != null
                     && operation.getOutputParameters().getOutputParameter() != null)
                 .findFirst().map(found -> !found.getOutputParameters().getOutputParameter().isEmpty())
                 .orElseGet(() -> {
                     LOG.debug("The requested operation was not found.");
                     return false;
                 });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesInterfaceOfNodeTypeContainOperation(final CSARID csarID, final QName nodeTypeID,
                                                           final String interfaceName, final String operationName) {
        return this.getNodeTypeHierarchy(csarID, nodeTypeID).stream()
                   .map(qname -> (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, qname))
                   .flatMap(n -> n.getInterfaces() == null ? Stream.empty() : n.getInterfaces().getInterface().stream())
                   .filter(iface -> iface.getName().equals(interfaceName))
                   .flatMap(iface -> iface.getOperation().stream()).anyMatch(op -> op.getName().equals(operationName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesInterfaceOfRelationshipTypeContainOperation(final CSARID csarID, final QName relationshipTypeID,
                                                                   final String interfaceName,
                                                                   final String operationName) {

        
        final TRelationshipType relationshipType =
            (TRelationshipType) toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);

        return Stream.of(Optional.ofNullable(relationshipType.getSourceInterfaces()).map(i -> i.getInterface()),
                         Optional.ofNullable(relationshipType.getTargetInterfaces()).map(i -> i.getInterface()))
                 .flatMap(ol -> ol.orElse(Collections.emptyList()).stream())
                 .filter(iface -> iface.getName().equals(interfaceName))
                 .flatMap(iface -> iface.getOperation().stream())
                 .anyMatch(op -> op.getName().equals(operationName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOperationOfRelationshipBoundToSourceNode(final CSARID csarID, final QName relationshipTypeID,
                                                              final String interfaceName, final String operationName) {

        final TRelationshipType relationshipType =
            (TRelationshipType) toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);

        return Optional.ofNullable(relationshipType.getSourceInterfaces()).map(i -> i.getInterface())
                       .orElse(Collections.emptyList()).stream()
                       .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
                       .flatMap(iface -> iface.getOperation().stream())
                       .anyMatch(op -> op.getName().equals(operationName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getNodeTypeImplementationsOfNodeType(final CSARID csarID, final QName nodeTypeID) {

        final List<QName> listOfNodeTypeImplementationQNames = new ArrayList<>();

        // search in all Definitions inside a certain CSAR
        for (final TDefinitions definitions : toscaReferenceMapper.getDefinitionsOfCSAR(csarID)) {

            for (final QName nodeTypeHierachyMember : getNodeTypeHierarchy(csarID, nodeTypeID)) {

                // search for NodeTypeImplementations
                for (final TExtensibleElements entity : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
                    if (entity instanceof TNodeTypeImplementation) {

                        // if the Implementation is for the given NodeType,
                        // remember
                        // it
                        final TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) entity;

                        if (nodeTypeImplementation.getNodeType().equals(nodeTypeHierachyMember)) {

                            // remember it
                            String targetNamespace;
                            if (nodeTypeImplementation.getTargetNamespace() != null
                                && !nodeTypeImplementation.getTargetNamespace().equals("")) {
                                targetNamespace = nodeTypeImplementation.getTargetNamespace();
                            } else {
                                targetNamespace = definitions.getTargetNamespace();
                            }
                            listOfNodeTypeImplementationQNames.add(new QName(targetNamespace,
                                nodeTypeImplementation.getName()));

                        }
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
    public List<QName> getRelationshipTypeImplementationsOfRelationshipType(final CSARID csarID,
                                                                            final QName relationshipTypeID) {

        final List<QName> listOfNodeTypeImplementationQNames = new ArrayList<>();

        // search in all Definitions inside a certain CSAR
        for (final TDefinitions definitions : toscaReferenceMapper.getDefinitionsOfCSAR(csarID)) {

            // search for NodeTypeImplementations
            for (final TExtensibleElements entity : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
                if (entity instanceof TRelationshipTypeImplementation) {

                    // if the Implementation is for the given NodeType, remember
                    // it
                    final TRelationshipTypeImplementation relationshipTypeImplementation =
                        (TRelationshipTypeImplementation) entity;
                    if (relationshipTypeImplementation.getRelationshipType().equals(relationshipTypeID)) {

                        // remember it
                        String targetNamespace;
                        if (relationshipTypeImplementation.getTargetNamespace() != null
                            && !relationshipTypeImplementation.getTargetNamespace().equals("")) {
                            targetNamespace = relationshipTypeImplementation.getTargetNamespace();
                        } else {
                            targetNamespace = definitions.getTargetNamespace();
                        }
                        listOfNodeTypeImplementationQNames.add(new QName(targetNamespace,
                            relationshipTypeImplementation.getName()));

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
    public List<String> getImplementationArtifactNamesOfNodeTypeImplementation(final CSARID csarID,
                                                                               final QName nodeTypeImplementationID) {
        return this.getNodeTypeImplementationTypeHierarchy(csarID, nodeTypeImplementationID).stream()
            .map(qname -> (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, qname))
            .filter(nti -> nti.getImplementationArtifacts() != null)
            .flatMap(nti -> nti.getImplementationArtifacts().getImplementationArtifact().stream())
            .map(ImplementationArtifact::getName)
            .collect(Collectors.toList());
    }

    @Override
    public String getRelatedNodeTemplateID(final CSARID csarID, final QName serviceTemplateID,
                                           final String nodeTemplateID, final QName relationshipType) {

        // get the ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

        return serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
            .filter(template -> template instanceof TRelationshipTemplate)
            .map(relTemplate -> (TRelationshipTemplate) relTemplate)
            .filter(relTemplate -> {
                final Object sourceElement = relTemplate.getSourceElement().getRef();
                return sourceElement instanceof TNodeTemplate
                    && ((TNodeTemplate)sourceElement).getId().equals(nodeTemplateID)
                    && relTemplate.getType().equals(relationshipType);
            })
            .map(relTemplate -> relTemplate.getTargetElement().getRef())
            .filter(target -> target instanceof TNodeTemplate)
            .map(target -> ((TNodeTemplate)target).getId())
            .findFirst()
            .orElseGet(() -> {
                LOG.error("The NodeTemplate \"" + nodeTemplateID + "\" has no related NodeTemplate with RelationshipType \""
                    + relationshipType + "\" or it isn't a NodeTemplate.");
                return null;
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNodeTemplateIDOfRelationshipTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                                final String relationshipTemplateID) {

        // get the ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

        final List<TEntityTemplate> templateList =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

        for (final TEntityTemplate template : templateList) {

            if (template instanceof TRelationshipTemplate) {

                final TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;

                if (relationshipTemplate.getId().equals(relationshipTemplateID)) {

                    // if there is a target element
                    if (relationshipTemplate.getTargetElement() != null) {

                        final Object targetElement = relationshipTemplate.getTargetElement().getRef();

                        if (targetElement instanceof TNodeTemplate) {

                            return ((TNodeTemplate) targetElement).getId();

                        }
                    }
                }
            }
        }

        LOG.error("The Relationship Template \"" + relationshipTemplateID
            + "\" has no target element or it isn't a NodeTemplate.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public String getSourceNodeTemplateIDOfRelationshipTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                                final String relationshipTemplateID) {

        // get the ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

        final List<TEntityTemplate> templateList =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

        for (final TEntityTemplate template : templateList) {

            if (template instanceof TRelationshipTemplate) {

                final TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;

                if (relationshipTemplate.getId().equals(relationshipTemplateID)) {

                    // if there is a target element
                    if (relationshipTemplate.getSourceElement() != null) {

                        final Object sourceElement = relationshipTemplate.getSourceElement().getRef();

                        if (sourceElement instanceof TNodeTemplate) {

                            return ((TNodeTemplate) sourceElement).getId();

                        }
                    }
                }
            }
        }

        LOG.error("The Relationship Template \"" + relationshipTemplateID
            + "\" has no source element or it isn't a NodeTemplate.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getImplementationArtifactNamesOfRelationshipTypeImplementation(final CSARID csarID,
                                                                                       final QName relationshipTypeImplementationID) {

        // return list
        final List<String> listOfNames = new ArrayList<>();

        // get the RelationshipTypeImplementation
        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // if there are ImplementationArtifacts, get the names
        if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts()
                                                                                       .getImplementationArtifact()) {
                listOfNames.add(implArt.getName());
            }
        }

        return listOfNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public Document getArtifactSpecificContentOfADeploymentArtifact(final CSARID csarID, final QName reference,
                                                                    final String deploymentArtifactName) {

        TDeploymentArtifacts artifacts = null;

        final Object referenceObj = toscaReferenceMapper.getJAXBReference(csarID, reference);
        if (referenceObj instanceof TNodeTypeImplementation) {
            artifacts = ((TNodeTypeImplementation) referenceObj).getDeploymentArtifacts();
        } else if (referenceObj instanceof TNodeTemplate) {
            artifacts = ((TNodeTemplate) referenceObj).getDeploymentArtifacts();
        }

        // if there are ImplementationArtifacts
        if (null != artifacts) {
            for (final TDeploymentArtifact deployArt : artifacts.getDeploymentArtifact()) {
                if (deployArt.getName().equals(deploymentArtifactName)) {

                    final List<Element> listOfAnyElements = new ArrayList<>();
                    for (final Object obj : deployArt.getAny()) {
                        if (obj instanceof Element) {
                            listOfAnyElements.add((Element) obj);
                        } else {
                            LOG.error("There is content inside of the DeploymentArtifact \"" + deploymentArtifactName
                                + "\" of the NodeTypeImplementation \"" + reference
                                + "\" which is not a processable DOM Element.");
                            return null;
                        }
                    }

                    return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                              .elementsIntoDocument(listOfAnyElements,
                                                                                    "DeploymentArtifactSpecificContent");
                }
            }
        }

        LOG.error("The requested DeploymentArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRequiredContainerFeaturesOfANodeTypeImplementation(final CSARID csarID,
                                                                              final QName nodeTypeImplementationID) {

        // return list
        final List<String> listOfStrings = new ArrayList<>();

        // get the NodeTypeImplementation
        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // if there are RequiredContainerFeatures, get the content
        if (nodeTypeImplementation.getRequiredContainerFeatures() != null) {
            for (final TRequiredContainerFeature requiredContainerFeature : nodeTypeImplementation.getRequiredContainerFeatures()
                                                                                                  .getRequiredContainerFeature()) {
                listOfStrings.add(requiredContainerFeature.getFeature());
            }
        }

        return listOfStrings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(final CSARID csarID,
                                                                                   final QName nodeTypeImplementationID,
                                                                                   final String implementationArtifactName) {

        try {
            // get the NodeTypeImplementation
            final TNodeTypeImplementation nodeTypeImplementation =
                (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                       nodeTypeImplementationID);
            // if there are ImplementationArtifacts
            if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {

                    if (implArt.getName().equals(implementationArtifactName)) {
                        return implArt.getArtifactType();
                    }
                }
            }
        }
        catch (final Exception e) {
            // get the TRelationshipTypeImplementation
            final TRelationshipTypeImplementation nodeTypeImplementation =
                (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                               nodeTypeImplementationID);
            // if there are ImplementationArtifacts
            if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {

                    if (implArt.getName().equals(implementationArtifactName)) {
                        return implArt.getArtifactType();
                    }
                }
            }
        }

        LOG.error("The requested ArtifactType was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public QName getArtifactTypeOfAImplementationArtifactOfARelationshipTypeImplementation(final CSARID csarID,
                                                                                           final QName relationshipTypeImplementationID,
                                                                                           final String implementationArtifactName) {

        // get the RelationshipTypeImplementation
        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // if there are ImplementationArtifacts
        if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts()
                                                                                       .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {
                    return implArt.getArtifactType();
                }
            }
        }

        LOG.error("The requested ArtifactType was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactTemplateOfAImplementationArtifactOfANodeTypeImplementation(final CSARID csarID,
                                                                                       final QName nodeTypeImplementationID,
                                                                                       final String implementationArtifactName) {

        try {
            // get the NodeTypeImplementation
            final TNodeTypeImplementation nodeTypeImplementation =
                (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

            // if there are ImplementationArtifacts
            if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {
                    if (implArt.getName().equals(implementationArtifactName)) {

                        ToscaEngineServiceImpl.LOG.trace("The ArtifactTemplate is found and has the QName \""
                            + implArt.getArtifactRef() + "\".");
                        return implArt.getArtifactRef();
                    }
                }
            }
        }
        catch (final Exception e) {
            // get the TRelationshipTypeImplementation
            final TRelationshipTypeImplementation nodeTypeImplementation =
                (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                               nodeTypeImplementationID);
            // if there are ImplementationArtifacts
            if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {

                    if (implArt.getName().equals(implementationArtifactName)) {

                        ToscaEngineServiceImpl.LOG.trace("The ArtifactTemplate is found and has the QName \""
                            + implArt.getArtifactRef() + "\".");
                        return implArt.getArtifactRef();
                    }
                }
            }
        }

        LOG.error("The requested ArtifactTemplate was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactTemplateOfAImplementationArtifactOfARelationshipTypeImplementation(final CSARID csarID,
                                                                                               final QName relationshipTypeImplementationID,
                                                                                               final String implementationArtifactName) {

        // get the RelationshipTypeImplementation
        final TRelationshipTypeImplementation relationTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // if there are ImplementationArtifacts
        if (relationTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : relationTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {
                    LOG.trace("The ArtifactTemplate is found and has the QName \"" + implArt.getArtifactRef() + "\".");
                    return implArt.getArtifactRef();
                }
            }
        }

        LOG.error("The requested ArtifactTemplate was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getReferenceAsNode(final CSARID csarID, final QName reference) {

        // get the ArtifactTemplate
        final Node artifactTemplateDoc = (Node) toscaReferenceMapper.getReferenceAsNode(csarID, reference);

        if (artifactTemplateDoc != null) {

            return artifactTemplateDoc;

        } else {

            LOG.error("The requested ArtifactTemplate was not found.");
            return null;
        }
    }

    @Override
    public Node getInputParametersOfANodeTypeOperation(final CSARID csarID, final QName nodeTypeID,
                                                       final String interfaceName, final String operationName) {

        for (final QName nodeTypeHierarchyMember : getNodeTypeHierarchy(csarID, nodeTypeID)) {

            final TNodeType nodeType =
                (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeHierarchyMember);

            if (nodeType.getInterfaces() != null) {

                for (final TInterface iface : nodeType.getInterfaces().getInterface()) {

                    for (final TOperation operation : iface.getOperation()) {

                        if (operation.getName().equals(operationName)
                            && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                            if (operation.getInputParameters() != null
                                && operation.getInputParameters().getInputParameter() != null) {

                                return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                          .marshalToNode(operation.getInputParameters());

                            }
                        }
                    }
                }
            }
        }
        LOG.debug("The requested operation was not found.");
        return null;

    }

    @Override
    public Node getOutputParametersOfANodeTypeOperation(final CSARID csarID, final QName nodeTypeID,
                                                        final String interfaceName, final String operationName) {

        final TNodeType nodeType = (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);

        if (nodeType.getInterfaces() != null) {

            for (final TInterface iface : nodeType.getInterfaces().getInterface()) {

                for (final TOperation operation : iface.getOperation()) {

                    if (operation.getName().equals(operationName)
                        && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                        if (operation.getOutputParameters() != null
                            && operation.getOutputParameters().getOutputParameter() != null) {

                            return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                      .marshalToNode(operation.getOutputParameters());

                        }
                    }
                }
            }
        }
        LOG.debug("The requested operation was not found.");
        return null;

    }

    @Override
    public Node getInputParametersOfARelationshipTypeOperation(final CSARID csarID, final QName relationshipTypeID,
                                                               final String interfaceName, final String operationName) {

        final TRelationshipType relationshipType =
            (TRelationshipType) toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);

        if (relationshipType.getSourceInterfaces() != null) {

            for (final TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {

                for (final TOperation operation : iface.getOperation()) {

                    if (operation.getName().equals(operationName)
                        && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                        if (operation.getInputParameters() != null
                            && operation.getInputParameters().getInputParameter() != null) {

                            return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                      .marshalToNode(operation.getInputParameters());

                        }
                    }
                }
            }
        }

        if (relationshipType.getTargetInterfaces() != null) {

            for (final TInterface iface : relationshipType.getTargetInterfaces().getInterface()) {

                for (final TOperation operation : iface.getOperation()) {

                    if (operation.getName().equals(operationName)
                        && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                        if (operation.getInputParameters() != null
                            && operation.getInputParameters().getInputParameter() != null) {

                            return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                      .marshalToNode(operation.getInputParameters());

                        }
                    }
                }
            }
        }
        LOG.debug("The requested operation was not found.");
        return null;

    }

    @Override
    public Node getOutputParametersOfARelationshipTypeOperation(final CSARID csarID, final QName relationshipTypeID,
                                                                final String interfaceName,
                                                                final String operationName) {

        final TRelationshipType relationshipType =
            (TRelationshipType) toscaReferenceMapper.getJAXBReference(csarID, relationshipTypeID);

        if (relationshipType.getSourceInterfaces() != null) {

            for (final TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {

                for (final TOperation operation : iface.getOperation()) {

                    if (operation.getName().equals(operationName)
                        && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                        if (operation.getOutputParameters() != null
                            && operation.getOutputParameters().getOutputParameter() != null) {

                            return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                      .marshalToNode(operation.getOutputParameters());

                        }
                    }
                }
            }
        }

        if (relationshipType.getTargetInterfaces() != null) {

            for (final TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {

                for (final TOperation operation : iface.getOperation()) {

                    if (operation.getName().equals(operationName)
                        && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                        if (operation.getOutputParameters() != null
                            && operation.getOutputParameters().getOutputParameter() != null) {

                            return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                      .marshalToNode(operation.getOutputParameters());

                        }
                    }
                }
            }
        }
        LOG.debug("The requested operation was not found.");
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(final CSARID csarID,
                                                                                                 final QName nodeTypeImplementationID,
                                                                                                 final String implementationArtifactName) {

        try {
            // get the NodeTypeImplementation
            final TNodeTypeImplementation nodeTypeImplementation =
                (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

            // if there are ImplementationArtifacts
            if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {

                    if (implArt.getName().equals(implementationArtifactName)) {

                        final List<Element> listOfAnyElements = new ArrayList<>();
                        for (final Object obj : implArt.getAny()) {
                            if (obj instanceof Element) {
                                listOfAnyElements.add((Element) obj);
                            } else {
                                LOG.error("There is content inside of the ImplementationArtifact \""
                                    + implementationArtifactName + "\" of the NodeTypeImplementation \""
                                    + nodeTypeImplementationID + "\" which is not a processable DOM Element.");
                                return null;
                            }
                        }

                        return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                  .elementsIntoDocument(listOfAnyElements,
                                                                                        "ImplementationArtifactSpecificContent");
                    }
                }
            }
        }
        catch (final Exception e) {
            // get the TRelationshipTypeImplementation
            final TRelationshipTypeImplementation nodeTypeImplementation =
                (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                               nodeTypeImplementationID);
            // if there are ImplementationArtifacts
            if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {

                    if (implArt.getName().equals(implementationArtifactName)) {

                        final List<Element> listOfAnyElements = new ArrayList<>();
                        for (final Object obj : implArt.getAny()) {
                            if (obj instanceof Element) {
                                listOfAnyElements.add((Element) obj);
                            } else {
                                ToscaEngineServiceImpl.LOG.error("There is content inside of the ImplementationArtifact \""
                                    + implementationArtifactName + "\" of the NodeTypeImplementation \""
                                    + nodeTypeImplementationID + "\" which is not a processable DOM Element.");
                                return null;
                            }
                        }
                        return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                  .elementsIntoDocument(listOfAnyElements,
                                                                                        "ImplementationArtifactSpecificContent");
                    }
                }
            }
        }

        LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getArtifactSpecificContentOfAImplementationArtifactOfARelationshipTypeImplementation(final CSARID csarID,
                                                                                                         final QName relationshipTypeImplementationID,
                                                                                                         final String implementationArtifactName) {

        // get the RelationshipTypeImplementation
        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // if there are ImplementationArtifacts
        if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts()
                                                                                       .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {

                    final List<Element> listOfAnyElements = new ArrayList<>();
                    for (final Object obj : implArt.getAny()) {
                        if (obj instanceof Element) {
                            listOfAnyElements.add((Element) obj);
                        } else {
                            LOG.error("There is content inside of the ImplementationArtifact \""
                                + implementationArtifactName + "\" of the RelationshipTypeImplementation \""
                                + relationshipTypeImplementationID + "\" which is not a processable DOM Element.");
                            return null;
                        }
                    }

                    return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                              .elementsIntoDocument(listOfAnyElements,
                                                                                    "ImplementationArtifactSpecificContent");

                }
            }
        }

        LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(final CSARID csarID,
                                                                                 final QName nodeTypeImplementationID,
                                                                                 final String implementationArtifactName) {

        // get the NodeTypeImplementation
        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // if there are ImplementationArtifacts
        if (nodeTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                               .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {

                    return implArt.getInterfaceName();

                }
            }
        }
        LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterfaceOfAImplementationArtifactOfARelationshipTypeImplementation(final CSARID csarID,
                                                                                         final QName relationshipTypeImplementationID,
                                                                                         final String implementationArtifactName) {

        // get the NodeTypeImplementation
        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // if there are ImplementationArtifacts
        if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts()
                                                                                       .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {

                    return implArt.getInterfaceName();

                }
            }
        }
        LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperationOfAImplementationArtifactOfANodeTypeImplementation(final CSARID csarID,
                                                                                 final QName nodeTypeImplementationID,
                                                                                 final String implementationArtifactName) {

        // get the NodeTypeImplementation
        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // if there are ImplementationArtifacts
        if (nodeTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                               .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {

                    return implArt.getOperationName();

                }
            }
        }
        LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperationOfAImplementationArtifactOfARelationshipTypeImplementation(final CSARID csarID,
                                                                                         final QName relationshipTypeImplementationID,
                                                                                         final String implementationArtifactName) {

        // get the NodeTypeImplementation
        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // if there are ImplementationArtifacts
        if (relationshipTypeImplementation.getImplementationArtifacts() != null) {
            for (final TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts()
                                                                                       .getImplementationArtifact()) {

                if (implArt.getName().equals(implementationArtifactName)) {

                    return implArt.getOperationName();

                }
            }
        }
        LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getPropertiesOfAArtifactTemplate(final CSARID csarID, final QName artifactTemplateID) {

        final Object requestedObject = toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);

        if (requestedObject instanceof TArtifactTemplate) {
            // get the ArtifactTemplate
            final TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;

            if (artifactTemplate.getProperties() != null) {

                if (artifactTemplate.getProperties().getAny() instanceof Element) {
                    final Document returnDoc =
                        ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                           .elementIntoDocument((Element) artifactTemplate.getProperties()
                                                                                                          .getAny());

                    if (returnDoc != null) {
                        LOG.debug("Return the Properties of the ArtifactTemplate \"" + artifactTemplateID + "\".");
                        return returnDoc;
                    } else {
                        LOG.error("The content of the Properties of the ArtifactTemplate \"" + artifactTemplateID
                            + "\" could not be written into a DOM Document.");
                    }
                } else {
                    LOG.error("The content of the Properties of the ArtifactTemplate \"" + artifactTemplateID
                        + "\" is not of the type DOM Element.");
                }
            }
        } else {
            LOG.error("The requested \"" + artifactTemplateID
                + "\" is not of the type ArtifactTemplate. It is of the type "
                + requestedObject.getClass().getSimpleName() + ".");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TPropertyConstraint> getPropertyConstraintsOfAArtifactTemplate(final CSARID csarID,
                                                                               final QName artifactTemplateID) {

        final Object requestedObject = toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);

        if (requestedObject instanceof TArtifactTemplate) {

            // get the ArtifactTemplate
            final TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;

            if (artifactTemplate.getPropertyConstraints() != null) {
                return artifactTemplate.getPropertyConstraints().getPropertyConstraint();
            } else {
                LOG.debug("There are no PropertyConstraints inside of the ArtifactTemplate \"" + artifactTemplateID
                    + "\".");
            }

        } else {
            LOG.error("The requested \"" + artifactTemplateID
                + "\" is not of the type ArtifactTemplate. It is of the type "
                + requestedObject.getClass().getSimpleName() + ".");
        }

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractArtifact> getArtifactsOfAArtifactTemplate(final CSARID csarID, final QName artifactTemplateID) {

        final List<AbstractArtifact> artifacts = new ArrayList<>();
        // List<File> returnFiles = new ArrayList<File>();
        final Object requestedObject = toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);

        if (requestedObject instanceof TArtifactTemplate) {

            // get the ArtifactTemplate
            final TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;

            if (artifactTemplate.getArtifactReferences() != null) {

                CSARContent csar;

                try {
                    csar = ServiceHandler.coreFileService.getCSAR(csarID);
                }
                catch (final UserException e) {
                    LOG.warn("An User Exception occured.", e);
                    return artifacts;
                }

                // iterate the references
                for (final TArtifactReference artifactReference : artifactTemplate.getArtifactReferences()
                                                                                  .getArtifactReference()) {

                    final Set<String> includePatterns = new HashSet<>();
                    final Set<String> excludePatterns = new HashSet<>();

                    for (final Object patternObj : artifactReference.getIncludeOrExclude()) {
                        if (patternObj instanceof Include) {
                            final Include include = (Include) patternObj;
                            includePatterns.add(include.getPattern());
                        } else {
                            final Exclude exclude = (Exclude) patternObj;
                            excludePatterns.add(exclude.getPattern());
                        }
                    }

                    try {
                        final AbstractArtifact artifact =
                            csar.resolveArtifactReference(artifactReference.getReference(), includePatterns,
                                                          excludePatterns);
                        artifacts.add(artifact);
                    }
                    catch (final UserException exc) {
                        LOG.warn("An User Exception occured.", exc);
                    }
                    catch (final SystemException exc) {
                        LOG.warn("A System Exception occured.", exc);
                    }
                }
            } else {
                LOG.debug("There are no ArtifactReferences in ArtifactTemplate \"" + artifactTemplateID + "\".");
            }
        } else {
            LOG.error("The requested \"" + artifactTemplateID
                + "\" is not of the type ArtifactTemplate. It is of the type "
                + requestedObject.getClass().getSimpleName() + ".");
        }

        return artifacts;
    }

    @Override
    public QName getNodeTypeOfNodeTemplate(final CSARID csarID, final QName serviceTemplateID,
                                           final String nodeTemplateID) {

        final QName NodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

        // get the NodeTemplate
        final Object obj = toscaReferenceMapper.getJAXBReference(csarID, NodeTemplateReference);

        if (obj == null) {
            LOG.error("The requested NodeTemplate was not found.");
            return null;
        }

        if (obj instanceof TNodeTemplate) {
            return ((TNodeTemplate) obj).getType();
        } else if (obj instanceof TNodeType) {
            // funny case with Moodle, since {ns}ApacheWebServer denotes a
            // NodeTemplate AND a NodeType, here we return the given QName
            return NodeTemplateReference;
        }

        LOG.error("The requested NodeTemplate was not found.");
        return null;
    }

    @Override
    public QName getRelationshipTypeOfRelationshipTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                           final String relationshipTemplateID) {

        final QName RelationshipTemplateReference =
            new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);

        // get the RelationshipTemplate
        final TRelationshipTemplate relationshipTemplate =
            (TRelationshipTemplate) toscaReferenceMapper.getJAXBReference(csarID, RelationshipTemplateReference);

        // if there are ImplementationArtifacts
        if (relationshipTemplate != null) {
            return relationshipTemplate.getType();
        }

        LOG.error("The requested RelationshipTemplate was not found.");
        return null;

    }

    @Override
    public boolean doesNodeTemplateExist(final CSARID csarID, final QName serviceTemplateID,
                                         final String nodeTemplateID) {

        final QName nodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

        // get the NodeTemplate
        final Object obj = toscaReferenceMapper.getJAXBReference(csarID, nodeTemplateReference);
        if (null == obj) {
            LOG.warn("The requested reference \"" + nodeTemplateReference + "\" was not found.");
        } else if (obj instanceof TNodeTemplate) {
            LOG.trace(nodeTemplateReference + " is a NodeTemplate and exists.");
            return true;
        } else {
            LOG.error("The requested reference is not an instance of TNodeTemplate. It seems to be a valid reference but the reference is not a NodeTemplate.");
        }

        return false;

    }

    @Override
    public boolean doesRelationshipTemplateExist(final CSARID csarID, final QName serviceTemplateID,
                                                 final String relationshipTemplateID) {
        final QName relationshipTemplateReference =
            new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);

        // get the NodeTemplate
        final Object obj = toscaReferenceMapper.getJAXBReference(csarID, relationshipTemplateReference);
        if (null == obj) {
            LOG.warn("The requested reference \"" + relationshipTemplateReference + "\" was not found.");
        } else if (obj instanceof TRelationshipTemplate) {
            LOG.trace(relationshipTemplateReference + " is a RelationshipTemplate and exists.");
            return true;
        } else {
            LOG.error("The requested reference is not an instance of TNodeTemplate. It seems to be a valid reference but the reference is not a NodeTemplate.");
        }

        return false;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean clearCSARContent(final CSARID csarID) {
        return toscaReferenceMapper.clearCSARContent(csarID);
    }

    @Override
    public Document getPropertiesOfNodeTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                final String nodeTemplateID) {
        // get the Namespace from the serviceTemplate
        final QName NodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

        // get the NodeTypeImplementation
        final TNodeTemplate nodeTemplate =
            (TNodeTemplate) toscaReferenceMapper.getJAXBReference(csarID, NodeTemplateReference);

        // check if all referenced objects exist and if returned any element is
        // really an element
        if (nodeTemplate != null) {
            final Properties properties = nodeTemplate.getProperties();
            if (properties != null) {
                final Object any = properties.getAny();
                if (any instanceof Element) {
                    final Element element = (Element) any;
                    return element.getOwnerDocument();
                } else {
                    LOG.debug("Properties is not of class Element.");
                }
            } else {
                LOG.debug("Properties are not set.");
            }
        } else {
            LOG.error("The requested NodeTemplate was not found.");
        }

        return null;
    }

    @Override
    public Document getPropertiesDefinitionOfNodeType(final CSARID csarID, final QName nodeTypeID) {

        // get the NodeType
        final TNodeType nodeType = (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);

        if (nodeType != null) {

            // TODO: fix this hack to get PropertiesDefinition. Needed till
            // you can get it "directly" via the model

            final Node nodeTypeNode = ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(nodeType);

            try {

                final NodeList list = nodeTypeNode.getChildNodes();

                for (int i = 0; i < list.getLength(); i++) {

                    final Node node = list.item(i);

                    if (node.getLocalName().equals("PropertiesDefinition")) {

                        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                        doc.appendChild(doc.importNode(node, true));

                        return doc;

                    }
                }

                LOG.debug("No PropertiesDefinition defined.");
                return null;

            }
            catch (final ParserConfigurationException e) {
                e.printStackTrace();
            }

        }

        LOG.debug("NodeType {} not found.", nodeTypeID);
        return null;
    }

    @Override
    public Document getPropertiesOfRelationshipTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                        final String relationshipTemplateID) {
        // get the Namespace from the serviceTemplate
        final QName relationshipTemplateReference =
            new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);

        // get the RelationshipTemplate
        final TRelationshipTemplate relationshipTemplate =
            (TRelationshipTemplate) toscaReferenceMapper.getJAXBReference(csarID, relationshipTemplateReference);

        // check if all referenced objects exist and if returned any element is
        // really an element
        if (relationshipTemplate != null) {
            final Properties properties = relationshipTemplate.getProperties();
            if (properties != null) {
                final Object any = properties.getAny();
                if (any instanceof Element) {
                    final Element element = (Element) any;
                    return element.getOwnerDocument();
                } else {
                    LOG.debug("Properties is not of class Element.");
                }
            } else {
                LOG.debug("Properties are not set.");
            }
        } else {
            LOG.error("The requested RelationshipTemplate was not found.");
        }

        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ResolvedArtifacts getResolvedArtifactsOfNodeTemplate(final CSARID csarID, final QName nodeTemplateID) {
        final List<ResolvedDeploymentArtifact> resolvedDAs = getNodeTemplateResolvedDAs(csarID, nodeTemplateID);

        final ResolvedArtifacts result = new ResolvedArtifacts();
        result.setDeploymentArtifacts(resolvedDAs);

        return result;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ResolvedArtifacts getResolvedArtifactsOfNodeTypeImplementation(final CSARID csarID,
                                                                          final QName nodeTypeImplementationID) {
        // TODO: add debug logger

        final List<ResolvedImplementationArtifact> resolvedIAs =
            getNodeTypeImplResolvedIAs(csarID, nodeTypeImplementationID);
        final List<ResolvedDeploymentArtifact> resolvedDAs =
            getNodeTypeImplResolvedDAs(csarID, nodeTypeImplementationID);

        final ResolvedArtifacts result = new ResolvedArtifacts();
        result.setDeploymentArtifacts(resolvedDAs);
        result.setImplementationArtifacts(resolvedIAs);

        return result;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ResolvedArtifacts getResolvedArtifactsOfRelationshipTypeImplementation(final CSARID csarID,
                                                                                  final QName nodeTypeImplementationID) {
        // TODO: add debug logger
        final List<ResolvedImplementationArtifact> resolvedIAs =
            getRelationshipTypeImplResolvedIAs(csarID, nodeTypeImplementationID);
        final List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<>();

        final ResolvedArtifacts result = new ResolvedArtifacts();
        result.setDeploymentArtifacts(resolvedDAs);
        result.setImplementationArtifacts(resolvedIAs);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNameOfReference(final CSARID csarID, final QName reference) {
        final Object jaxbReferenceObject = toscaReferenceMapper.getJAXBReference(csarID, reference);
        // check if object was found
        if (jaxbReferenceObject == null) {
            LOG.error("Failed to extract name attribute - could not retrieve correlating JAXB-Object. Reference "
                + reference + " seems to be non-existent");
            return null;
        }

        // check if class could be retrieved
        final Class<? extends Object> jaxbClass = jaxbReferenceObject.getClass();

        if (jaxbClass == null) {
            LOG.error("Failed to extract name attribute - could not retrieve correlating JAXB-Class. Reference "
                + reference + " existents but is not a valid jaxb-class");
            return null;
        }

        try {
            // try to call .getName on the referencing jaxb class
            final Method getNameMethod = jaxbClass.getMethod("getName");
            if (getNameMethod == null) {
                LOG.error("Failed to extract name attribute - could not retrieve getName-Method of JAXB-Class. Reference "
                    + reference + " existents but is not a jaxb-class containing a getName Method");
                return null;
            }

            // invoke of parameterless getName()
            final String result = (String) getNameMethod.invoke(jaxbReferenceObject, (Object[]) null);
            // return result or emptyString if result == null
            if (result == null) {
                LOG.debug("Name attribute of " + reference + " was null - returning \"\"");
                return "";
            } else {
                return result;
            }

        }
        catch (final NoSuchMethodException e) {
            final String logMsg =
                String.format("Failed to extract name attribute: The retrieved class %s didn't contain a getName() method. Check if the call with csarid: %s and QName %s was valid! (maybe a bug in code!!!)",
                              jaxbClass, csarID.toString(), reference.toString());

            LOG.error(logMsg);
        }
        catch (final InvocationTargetException e) {
            LOG.error("Failed to extract name attribute - an Invocation-exception occured while invoking getName()",
                      e.getCause());
        }
        catch (final Exception e) {
            LOG.error("Failed to extract name attribute - an exception occured while invoking getName()", e);
        }

        return null;

    }

    /**
     * Resolves the Deployment-Artifacts of a NodeTemplate
     *
     * @param csarID of the CSAR
     * @param nodeTemplateID
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no Artifact
     *         was found the returned list will be empty.
     */
    private List<ResolvedDeploymentArtifact> getNodeTemplateResolvedDAs(final CSARID csarID,
                                                                        final QName nodeTemplateID) {

        final List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<>();

        LOG.debug("Trying to fetch DA of NodeTemplate " + nodeTemplateID);

        final TNodeTemplate nodeTemplate =
            (TNodeTemplate) toscaReferenceMapper.getJAXBReference(csarID, nodeTemplateID);

        // check if there are implementationArtifact Entries
        if (nodeTemplate.getDeploymentArtifacts() == null
            || nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact() == null) {
            // return empty list
            LOG.warn("NodeTemplate " + nodeTemplate + " has no DeploymentArtifacts");
            return new ArrayList<>();
        }

        for (final TDeploymentArtifact deployArt : nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact()) {
            final ResolvedDeploymentArtifact ra = new ResolvedDeploymentArtifact();
            ra.setName(deployArt.getName());
            ra.setType(deployArt.getArtifactType());

            // we assume there is artifactSpecificContent OR a reference to
            // an artifactTemplate
            Document artifactSpecificContent = null;
            if (deployArt.getArtifactRef() != null) {
                // try to dereference artifactReference - build references
                final TArtifactTemplate artTemplate =
                    (TArtifactTemplate) toscaReferenceMapper.getJAXBReference(csarID, deployArt.getArtifactRef());

                // list to store results
                final List<String> references = new ArrayList<>();

                final ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
                if (artifactReferences != null && artifactReferences.getArtifactReference() != null) {
                    for (final TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
                        // checking if artifactReference has include
                        // patterns
                        if (artifactReference.getIncludeOrExclude() != null
                            && !artifactReference.getIncludeOrExclude().isEmpty()) {
                            for (final Object patternObj : artifactReference.getIncludeOrExclude()) {
                                if (patternObj instanceof TArtifactReference.Include) {
                                    final TArtifactReference.Include includePattern =
                                        (TArtifactReference.Include) patternObj;
                                    references.add(artifactReference.getReference() + "/"
                                        + includePattern.getPattern());
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
                artifactSpecificContent =
                    getArtifactSpecificContentOfADeploymentArtifact(csarID, nodeTemplateID, deployArt.getName());
                ra.setArtifactSpecificContent(artifactSpecificContent);
            }

            // add to collection
            resolvedDAs.add(ra);
        }

        return resolvedDAs;
    }

    /**
     * resolves the Deployment-Artifacts of the given nodeTypeImplementationID (get
     * ArtifactSpecificContent OR the reference from the ArtifactTemplate)
     *
     * @param csarID of the CSAR
     * @param nodeTypeImplementationID of the nodeTypeImplementation
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no Artifact
     *         was found the returned list will be empty.
     */
    private List<ResolvedDeploymentArtifact> getNodeTypeImplResolvedDAs(final CSARID csarID,
                                                                        final QName nodeTypeImplementationID) {
        final List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<>();

        LOG.debug("Trying to fetch DA of NodeTypeImplementation" + nodeTypeImplementationID.toString());

        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // check if there are implementationArtifact Entries
        if (nodeTypeImplementation.getDeploymentArtifacts() == null
            || nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact() == null) {
            // return empty list
            LOG.debug("NodeTypeImplementation " + nodeTypeImplementationID.toString() + " has no DeploymentArtifacts");
            return new ArrayList<>();
        }

        if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
            for (final TDeploymentArtifact deployArt : nodeTypeImplementation.getDeploymentArtifacts()
                                                                             .getDeploymentArtifact()) {
                final ResolvedDeploymentArtifact ra = new ResolvedDeploymentArtifact();
                ra.setName(deployArt.getName());
                ra.setType(deployArt.getArtifactType());

                // we assume there is artifactSpecificContent OR a reference to
                // an artifactTemplate
                Document artifactSpecificContent = null;
                if (deployArt.getArtifactRef() != null) {
                    // try to dereference artifactReference - build references
                    final TArtifactTemplate artTemplate =
                        (TArtifactTemplate) toscaReferenceMapper.getJAXBReference(csarID, deployArt.getArtifactRef());

                    // list to store results
                    final List<String> references = new ArrayList<>();

                    final ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
                    if (artifactReferences != null && artifactReferences.getArtifactReference() != null) {
                        for (final TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
                            // checking if artifactReference has include
                            // patterns
                            if (artifactReference.getIncludeOrExclude() != null
                                && !artifactReference.getIncludeOrExclude().isEmpty()) {
                                for (final Object patternObj : artifactReference.getIncludeOrExclude()) {
                                    if (patternObj instanceof TArtifactReference.Include) {
                                        final TArtifactReference.Include includePattern =
                                            (TArtifactReference.Include) patternObj;
                                        references.add(artifactReference.getReference() + "/"
                                            + includePattern.getPattern());
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
                    artifactSpecificContent =
                        getArtifactSpecificContentOfADeploymentArtifact(csarID, nodeTypeImplementationID,
                                                                        deployArt.getName());
                    ra.setArtifactSpecificContent(artifactSpecificContent);
                }

                // add to collection
                resolvedDAs.add(ra);
            }
        }

        return resolvedDAs;
    }

    /**
     * resolves the Deployment-Artifacts of the given nodeTypeImplementationID (get
     * ArtifactSpecificContent OR the reference from the ArtifactTemplate)
     *
     * @param csarID of the CSAR
     * @param nodeTypeImplementationID of the nodeTypeImplementation
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no Artifact
     *         was found the returned list will be empty.
     */
    private List<ResolvedImplementationArtifact> getNodeTypeImplResolvedIAs(final CSARID csarID,
                                                                            final QName nodeTypeImplementationID) {

        final List<ResolvedImplementationArtifact> resolvedIAs = new ArrayList<>();

        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // check if there are implementationArtifact Entries
        if (nodeTypeImplementation.getImplementationArtifacts() == null
            || nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact() == null) {
            new ArrayList<ResolvedImplementationArtifact>();
        }

        for (final TImplementationArtifact implArt : nodeTypeImplementation.getImplementationArtifacts()
                                                                           .getImplementationArtifact()) {
            final ResolvedImplementationArtifact ra = new ResolvedImplementationArtifact();

            // fill operation and interface name
            ra.setOperationName(implArt.getOperationName());
            ra.setInterfaceName(implArt.getInterfaceName());
            ra.setType(implArt.getArtifactType());
            // we assume there is artifactSpecificContent OR a reference to an
            // artifactTemplate
            Document artifactSpecificContent = null;
            if (implArt.getArtifactRef() != null) {
                // try to dereference artifactReference - build references
                final TArtifactTemplate artTemplate =
                    (TArtifactTemplate) toscaReferenceMapper.getJAXBReference(csarID, implArt.getArtifactRef());

                // list to store results
                final List<String> references = new ArrayList<>();

                final ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
                if (artifactReferences != null && artifactReferences.getArtifactReference() != null) {

                    for (final TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
                        // checking if artifactReference has include patterns
                        if (artifactReference.getIncludeOrExclude() != null
                            && !artifactReference.getIncludeOrExclude().isEmpty()) {
                            for (final Object patternObj : artifactReference.getIncludeOrExclude()) {
                                if (patternObj instanceof TArtifactReference.Include) {
                                    final TArtifactReference.Include includePattern =
                                        (TArtifactReference.Include) patternObj;
                                    references.add(artifactReference.getReference() + "/"
                                        + includePattern.getPattern());
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
                artifactSpecificContent =
                    getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                 nodeTypeImplementationID,
                                                                                                 implArt.getName());
                ra.setArtifactSpecificContent(artifactSpecificContent);
            }

            // add to collection
            resolvedIAs.add(ra);
        }
        return resolvedIAs;
    }

    /**
     * resolves the Deployment-Artifacts of the given nodeTypeImplementationID (get
     * ArtifactSpecificContent OR the reference from the ArtifactTemplate)
     *
     * @param csarID of the CSAR
     * @param nodeTypeImplementationID of the nodeTypeImplementation
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no Artifact
     *         was found the returned list will be empty.
     */
    private List<ResolvedImplementationArtifact> getRelationshipTypeImplResolvedIAs(final CSARID csarID,
                                                                                    final QName relationshipTypeImplementationID) {
        final List<ResolvedImplementationArtifact> resolvedIAs = new ArrayList<>();

        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID,
                                                                                    relationshipTypeImplementationID);

        // check if there are implementationArtifact Entries
        if (relationshipTypeImplementation.getImplementationArtifacts() == null
            || relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact() == null) {
            new ArrayList<ResolvedImplementationArtifact>();
        }

        for (final TImplementationArtifact implArt : relationshipTypeImplementation.getImplementationArtifacts()
                                                                                   .getImplementationArtifact()) {
            final ResolvedImplementationArtifact ra = new ResolvedImplementationArtifact();

            ra.setOperationName(implArt.getOperationName());
            ra.setInterfaceName(implArt.getInterfaceName());
            ra.setType(implArt.getArtifactType());
            // we assume there is artifactSpecificContent OR a reference to an
            // artifactTemplate
            Document artifactSpecificContent = null;
            if (implArt.getArtifactRef() != null) {
                // try to dereference artifactReference - build references
                final TArtifactTemplate artTemplate =
                    (TArtifactTemplate) toscaReferenceMapper.getJAXBReference(csarID, implArt.getArtifactRef());

                // list to store results
                final List<String> references = new ArrayList<>();

                final ArtifactReferences artifactReferences = artTemplate.getArtifactReferences();
                if (artifactReferences != null && artifactReferences.getArtifactReference() != null) {

                    for (final TArtifactReference artifactReference : artifactReferences.getArtifactReference()) {
                        // checking if artifactReference has include patterns
                        if (artifactReference.getIncludeOrExclude() != null
                            && !artifactReference.getIncludeOrExclude().isEmpty()) {
                            for (final Object patternObj : artifactReference.getIncludeOrExclude()) {
                                if (patternObj instanceof TArtifactReference.Include) {
                                    final TArtifactReference.Include includePattern =
                                        (TArtifactReference.Include) patternObj;
                                    references.add(artifactReference.getReference() + "/"
                                        + includePattern.getPattern());
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
                artifactSpecificContent =
                    getArtifactSpecificContentOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
                                                                                                         relationshipTypeImplementationID,
                                                                                                         implArt.getName());
                ra.setArtifactSpecificContent(artifactSpecificContent);
            }

            // add to collection
            resolvedIAs.add(ra);
        }
        return resolvedIAs;
    }

    @Override
    public NodeTemplateInstanceCounts getInstanceCountsOfNodeTemplatesByServiceTemplateID(final CSARID csarID,
                                                                                          final QName serviceTemplateID) {

        // get the referenced ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
        final List<TEntityTemplate> nodeTemplateOrRelationshipTemplate =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

        // store nodeTemplates in own list so we dont alter the jaxb object
        final List<TNodeTemplate> nodeTemplates = new ArrayList<>();
        for (final TEntityTemplate tEntityTemplate : nodeTemplateOrRelationshipTemplate) {
            // only add it if its a nodeTemplate
            if (tEntityTemplate instanceof TNodeTemplate) {
                nodeTemplates.add((TNodeTemplate) tEntityTemplate);
            }
        }

        // construct result object (getMin and MaxInstance from JAXB and store
        // them in result object)
        final NodeTemplateInstanceCounts counts = new NodeTemplateInstanceCounts();
        for (final TNodeTemplate tNodeTemplate : nodeTemplates) {
            final QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), tNodeTemplate.getId());
            final int minInstances = tNodeTemplate.getMinInstances();
            // in xml the maxInstances attribute is a String because it also can
            // contain "unbounded"
            final String maxInstances = tNodeTemplate.getMaxInstances();

            counts.addInstanceCount(nodeTemplateQName, minInstances, maxInstances);
        }

        return counts;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String getPlanName(final CSARID csar, final QName planId) {

        LOG.trace("Resolve the absolute path of the PlanModelReference of plan \"" + planId + "\" inside of CSAR \""
            + csar + "\".");

        final QName containingDefinitions = toscaReferenceMapper.getContainingDefinitionsID(csar, planId);

        if (null != containingDefinitions) {

            LOG.trace("Desired path to the PlanModel is inside the Definitions \"" + containingDefinitions + "\".");

            final String definitionsLocation = toscaReferenceMapper.getDefinitionsLocation(csar, containingDefinitions);

            if (null != definitionsLocation) {

                LOG.trace("Definitions path is \"" + definitionsLocation + "\".");

                final TPlan plan = (TPlan) toscaReferenceMapper.getJAXBReference(csar, planId);
                return plan.getName();

            }
        }

        LOG.error("Not able to retrieve to plan name of " + planId.toString() + " inside of CSAR " + csar.toString());
        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public AbstractArtifact getPlanModelReferenceAbstractArtifact(final CSARContent csar, final QName planId) {

        LOG.trace("Resolve the absolute path of the PlanModelReference of plan \"" + planId + "\" inside of CSAR \""
            + csar.getCSARID() + "\".");

        final QName containingDefinitions = toscaReferenceMapper.getContainingDefinitionsID(csar.getCSARID(), planId);

        if (null != containingDefinitions) {

            LOG.trace("Desired path to the PlanModel is inside the Definitions \"" + containingDefinitions + "\".");

            final String definitionsLocation =
                toscaReferenceMapper.getDefinitionsLocation(csar.getCSARID(), containingDefinitions);

            if (null != definitionsLocation) {

                LOG.trace("Definitions path is \"" + definitionsLocation + "\".");

                final TPlan plan = (TPlan) toscaReferenceMapper.getJAXBReference(csar.getCSARID(), planId);
                final String planModelReferenceLocation = plan.getPlanModelReference().getReference();
                LOG.trace("planModelReferenceLocation: " + planModelReferenceLocation);
                final String absoluteLocation =
                    PathResolver.resolveRelativePath(definitionsLocation, planModelReferenceLocation, csar);

                LOG.trace("Absolute path to the PlanModel is \"" + absoluteLocation + "\".");

                try {

                    final AbstractArtifact artifact = csar.resolveArtifactReference(absoluteLocation);
                    if (null != artifact) {
                        return artifact;
                    }

                }
                catch (final UserException e) {
                    LOG.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
                catch (final SystemException e) {
                    LOG.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }

        LOG.error("There was an error while resolving the absolute path of the PlanModelReference of plan \"" + planId
            + "\" inside of CSAR \"" + csar.getCSARID() + "\".");
        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getArtifactReferenceWithinArtifactTemplate(final CSARID csarID, final QName artifactTemplate) {

        final List<String> references = new ArrayList<>();

        final Object obj = toscaReferenceMapper.getJAXBReference(csarID, artifactTemplate);

        if (obj != null) {
            final TArtifactTemplate artifactTemplateObject = (TArtifactTemplate) obj;

            final List<TArtifactReference> tArtifactReferences =
                artifactTemplateObject.getArtifactReferences().getArtifactReference();

            for (final TArtifactReference tArtifactReference : tArtifactReferences) {
                references.add(tArtifactReference.getReference());
            }
        }
        return references;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public QName getArtifactTypeOfArtifactTemplate(final CSARID csarID, final QName artifactTemplate) {

        QName artifactType = null;

        final Object obj = toscaReferenceMapper.getJAXBReference(csarID, artifactTemplate);

        if (obj != null) {
            final TArtifactTemplate artifactTemplateObject = (TArtifactTemplate) obj;

            artifactType = artifactTemplateObject.getType();

        }
        return artifactType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDeploymentArtifactNamesOfNodeTypeImplementation(final CSARID csarID,
                                                                           final QName nodeTypeImplementationID) {

        // return list
        final List<String> listOfNames = new ArrayList<>();

        // get the NodeTypeImplementation
        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // if there are ImplementationArtifacts, get the names
        if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
            for (final TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts()
                                                                      .getDeploymentArtifact()) {
                listOfNames.add(da.getName());
            }
        }

        return listOfNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactTemplateOfADeploymentArtifactOfANodeTypeImplementation(final CSARID csarID,
                                                                                   final QName nodeTypeImplementationID,
                                                                                   final String deploymentArtifactName) {

        // get the NodeTypeImplementation
        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationID);

        // if there are DeploymentArtifacts
        if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
            for (final TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts()
                                                                      .getDeploymentArtifact()) {

                if (da.getName().equals(deploymentArtifactName)) {
                    LOG.trace("The ArtifactTemplate is found and has the QName \"" + da.getArtifactRef() + "\".");
                    return da.getArtifactRef();
                }
            }
        }
        LOG.error("The requested ArtifactTemplate was not found.");
        return null;
    }

    @Override
    public List<QName> getServiceTemplatesInCSAR(final CSARID csarID) {
        return toscaReferenceMapper.getServiceTemplateIDsContainedInCSAR(csarID);
    }

    @Override
    public List<String> getNodeTemplatesOfServiceTemplate(final CSARID csarID, final QName serviceTemplate) {
        final Map<QName, List<String>> map = toscaReferenceMapper.getServiceTemplatesAndNodeTemplatesInCSAR(csarID);
        if (null != map) {
            return map.get(serviceTemplate);
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getRelationshipTemplatesOfServiceTemplate(final CSARID csarID, final QName serviceTemplate) {
        final Map<QName, List<String>> map = toscaReferenceMapper.getServiceTemplate2RelationshipTemplateMap(csarID);

        if (map != null) {
            return map.get(serviceTemplate);
        }

        return new ArrayList<>();
    }

    @Override
    public TBoundaryDefinitions getBoundaryDefinitionsOfServiceTemplate(final CSARID csarId,
                                                                        final QName serviceTemplateId) {
        // get the referenced ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        return serviceTemplate.getBoundaryDefinitions();
    }

    @Override
    public List<QName> getNodeTypeHierarchy(final CSARID csarID, final QName nodeType) {
        final List<QName> qnames = new ArrayList<>();
        final TNodeType nodeTypeElement = (TNodeType) toscaReferenceMapper.getJAXBReference(csarID, nodeType);

        qnames.add(nodeType);

        if (nodeTypeElement.getDerivedFrom() != null && nodeTypeElement.getDerivedFrom().getTypeRef() != null) {
            qnames.addAll(getNodeTypeHierarchy(csarID, nodeTypeElement.getDerivedFrom().getTypeRef()));
        }

        return qnames;
    }

    @Override
    public List<QName> getNodeTypeImplementationTypeHierarchy(final CSARID csarID,
                                                              final QName nodeTypeImplementationId) {
        final List<QName> qnames = new ArrayList<>();
        final TNodeTypeImplementation nodeTypeImplElement =
            (TNodeTypeImplementation) toscaReferenceMapper.getJAXBReference(csarID, nodeTypeImplementationId);

        qnames.add(nodeTypeImplementationId);

        if (nodeTypeImplElement.getDerivedFrom() != null
            && nodeTypeImplElement.getDerivedFrom().getNodeTypeImplementationRef() != null) {
            qnames.addAll(getNodeTypeImplementationTypeHierarchy(csarID,
                                                                 nodeTypeImplElement.getDerivedFrom()
                                                                                    .getNodeTypeImplementationRef()));
        }

        return qnames;
    }

    @Override
    public List<QName> getNodeTemplateCapabilities(final CSARID csarId, final QName serviceTemplateId,
                                                   final String nodeTemplateId) {
        final List<QName> caps = new ArrayList<>();
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {
            if (entity instanceof TNodeTemplate && entity.getId().equals(nodeTemplateId)) {
                if (((TNodeTemplate) entity).getCapabilities() != null) {
                    for (final TCapability cap : ((TNodeTemplate) entity).getCapabilities().getCapability()) {
                        caps.add(new QName(serviceTemplate.getTargetNamespace(), cap.getId()));
                    }
                }
            }
        }

        return caps;
    }

    @Override
    public List<QName> getNodeTemplateRequirements(final CSARID csarId, final QName serviceTemplateId,
                                                   final String nodeTemplateId) {
        final List<QName> reqs = new ArrayList<>();
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {
            if (entity instanceof TNodeTemplate && entity.getId().equals(nodeTemplateId)) {
                if (((TNodeTemplate) entity).getRequirements() != null) {
                    for (final TRequirement req : ((TNodeTemplate) entity).getRequirements().getRequirement()) {
                        reqs.add(new QName(serviceTemplate.getTargetNamespace(), req.getId()));
                    }
                }
            }
        }

        return reqs;
    }

    @Override
    public QName getRelationshipTemplateTarget(final CSARID csarId, final QName serviceTemplateId,
                                               final String relationshipTemplateId) {
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {
            if (entity instanceof TRelationshipTemplate && entity.getId().equals(relationshipTemplateId)) {
                if (((TRelationshipTemplate) entity).getTargetElement().getRef() instanceof TNodeTemplate) {
                    return new QName(serviceTemplate.getTargetNamespace(),
                        ((TNodeTemplate) ((TRelationshipTemplate) entity).getTargetElement().getRef()).getId());
                }
                if (((TRelationshipTemplate) entity).getTargetElement().getRef() instanceof TCapability) {
                    final TCapability cap = (TCapability) ((TRelationshipTemplate) entity).getTargetElement().getRef();

                    if (resolveNodeTemplateFromCapability(csarId, serviceTemplateId, cap.getId()) != null) {
                        return new QName(serviceTemplate.getTargetNamespace(),
                            resolveNodeTemplateFromCapability(csarId, serviceTemplateId, cap.getId()).getId());
                    }

                }
            }
        }

        return null;
    }

    @Override
    public QName getRelationshipTemplateSource(final CSARID csarId, final QName serviceTemplateId,
                                               final String relationshipTemplateId) {
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);
        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {
            if (entity instanceof TRelationshipTemplate && entity.getId().equals(relationshipTemplateId)) {
                if (((TRelationshipTemplate) entity).getSourceElement().getRef() instanceof TNodeTemplate) {
                    return new QName(serviceTemplate.getTargetNamespace(),
                        ((TNodeTemplate) ((TRelationshipTemplate) entity).getSourceElement().getRef()).getId());
                }
                if (((TRelationshipTemplate) entity).getSourceElement().getRef() instanceof TRequirement) {
                    final TRequirement req =
                        (TRequirement) ((TRelationshipTemplate) entity).getSourceElement().getRef();
                    // resolve requirement to nodeTemplate

                    if (resolveNodeTemplateFromRequirement(csarId, serviceTemplateId, req.getId()) != null) {
                        return new QName(serviceTemplate.getTargetNamespace(),
                            resolveNodeTemplateFromRequirement(csarId, serviceTemplateId, req.getId()).getId());
                    }
                }
            }
        }
        return null;
    }

    private TNodeTemplate resolveNodeTemplateFromCapability(final CSARID csarId, final QName serviceTemplateId,
                                                            final String capabilityId) {
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {
            if (entity instanceof TNodeTemplate) {
                final TNodeTemplate nodeTemplate = (TNodeTemplate) entity;

                if (nodeTemplate.getCapabilities() != null) {
                    for (final TCapability req : nodeTemplate.getCapabilities().getCapability()) {
                        if (req.getId().equals(capabilityId)) {
                            return nodeTemplate;
                        }
                    }
                }
            }
        }

        return null;
    }

    private TNodeTemplate resolveNodeTemplateFromRequirement(final CSARID csarId, final QName serviceTemplateId,
                                                             final String requirementId) {
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        for (final TEntityTemplate entity : serviceTemplate.getTopologyTemplate()
                                                           .getNodeTemplateOrRelationshipTemplate()) {
            if (entity instanceof TNodeTemplate) {
                final TNodeTemplate nodeTemplate = (TNodeTemplate) entity;

                if (nodeTemplate.getRequirements() != null) {
                    for (final TRequirement req : nodeTemplate.getRequirements().getRequirement()) {
                        if (req.getId().equals(requirementId)) {
                            return nodeTemplate;
                        }
                    }
                }
            }
        }


        return null;
    }

    @Override
    public List<String> getInterfaceNamesOfNodeType(final CSARID csarID, final QName nodeTypeID) {

        final Set<String> interfaceNames = new HashSet<>();

        for (final QName nodeTypeHierarchyMember : getNodeTypeHierarchy(csarID, nodeTypeID)) {

            final TNodeType nodeType =
                (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                         nodeTypeHierarchyMember);

            if (nodeType.getInterfaces() != null) {

                for (final TInterface iface : nodeType.getInterfaces().getInterface()) {
                    interfaceNames.add(iface.getName());
                }
            }
        }

        return Lists.newArrayList(interfaceNames);
    }

    @Override
    public List<String> getOperationNamesOfNodeTypeInterface(final CSARID csarId, final QName nodeTypeId,
                                                             final String interfaceName) {
        final Set<String> operationNames = new HashSet<>();

        for (final QName nodeTypeHierarchyMember : getNodeTypeHierarchy(csarId, nodeTypeId)) {

            final TNodeType nodeType =
                (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId,
                                                                                         nodeTypeHierarchyMember);

            if (nodeType.getInterfaces() != null) {

                for (final TInterface iface : nodeType.getInterfaces().getInterface()) {

                    if (iface.getName().equals(interfaceName)) {
                        for (final TOperation op : iface.getOperation()) {
                            operationNames.add(op.getName());
                        }
                    }

                }
            }
        }


        return Lists.newArrayList(operationNames);
    }

    @Override
    public List<String> getInputParametersOfNodeTypeOperation(final CSARID csarID, final QName nodeTypeId,
                                                              final String interfaceName, final String operationName) {
        return parseParameters(getInputParametersOfANodeTypeOperation(csarID, nodeTypeId, interfaceName,
                                                                      operationName));
    }

    @Override
    public List<String> getOutputParametersOfNodeTypeOperation(final CSARID csarID, final QName nodeTypeId,
                                                               final String interfaceName, final String operationName) {
        return parseParameters(getOutputParametersOfANodeTypeOperation(csarID, nodeTypeId, interfaceName,
                                                                       operationName));
    }

    private List<String> parseParameters(final Node node) {

        final List<String> params = new ArrayList<>();
        if (node != null) {

            final NodeList definedInputParameterList = node.getChildNodes();

            for (int i = 0; i < definedInputParameterList.getLength(); i++) {

                final Node currentNode = definedInputParameterList.item(i);

                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                    final String name = ((Element) currentNode).getAttribute("name");

                    params.add(name);

                }
            }
        }

        return params;
    }

}
