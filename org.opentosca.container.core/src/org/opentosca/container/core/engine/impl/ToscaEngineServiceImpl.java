package org.opentosca.container.core.engine.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
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
import org.opentosca.container.core.tosca.model.TArtifactReference;
import org.opentosca.container.core.tosca.model.TArtifactReference.Exclude;
import org.opentosca.container.core.tosca.model.TArtifactReference.Include;
import org.opentosca.container.core.tosca.model.TArtifactTemplate;
import org.opentosca.container.core.tosca.model.TArtifactTemplate.ArtifactReferences;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions;
import org.opentosca.container.core.tosca.model.TCapability;
import org.opentosca.container.core.tosca.model.TDefinitions;
import org.opentosca.container.core.tosca.model.TDeploymentArtifact;
import org.opentosca.container.core.tosca.model.TDeploymentArtifacts;
import org.opentosca.container.core.tosca.model.TEntityTemplate;
import org.opentosca.container.core.tosca.model.TEntityTemplate.Properties;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.opentosca.container.core.tosca.model.TImplementationArtifact;
import org.opentosca.container.core.tosca.model.TInterface;
import org.opentosca.container.core.tosca.model.TNodeTemplate;
import org.opentosca.container.core.tosca.model.TNodeType;
import org.opentosca.container.core.tosca.model.TNodeTypeImplementation;
import org.opentosca.container.core.tosca.model.TOperation;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TPropertyConstraint;
import org.opentosca.container.core.tosca.model.TRelationshipTemplate;
import org.opentosca.container.core.tosca.model.TRelationshipType;
import org.opentosca.container.core.tosca.model.TRelationshipTypeImplementation;
import org.opentosca.container.core.tosca.model.TRequiredContainerFeature;
import org.opentosca.container.core.tosca.model.TRequirement;
import org.opentosca.container.core.tosca.model.TServiceTemplate;
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
    public boolean resolveDefinitions(final CSARID csarID) {

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
    public List<QName> getReferencedNodeTypesOfAServiceTemplate(final CSARID csarID, final QName serviceTemplateID) {

        final List<QName> nodeTypeQNames = new ArrayList<>();

        // get the referenced ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

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
                    ToscaEngineServiceImpl.LOG.error("The NodeTemplate \"" + serviceTemplate.getTargetNamespace() + ":"
                        + nodeTemplate.getId() + "does not specify a NodeType.");
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
                            ToscaEngineServiceImpl.LOG.error("The NodeTemplate \""
                                + serviceTemplate.getTargetNamespace() + ":" + nodeTemplate.getId()
                                + "does not specify a NodeType.");

                        }
                    } else {

                        ToscaEngineServiceImpl.LOG.debug("The QName \""
                            + relationshipTemplate.getTargetElement().getRef() + "\" points to a Requirement.");
                    }
                } else {
                    ToscaEngineServiceImpl.LOG.error("The RelationshipTemplate \""
                        + serviceTemplate.getTargetNamespace() + ":" + relationshipTemplate.getId()
                        + "does not specify a SourceElement.");
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
                            ToscaEngineServiceImpl.LOG.error("The NodeTemplate \""
                                + serviceTemplate.getTargetNamespace() + ":" + nodeTemplate.getId()
                                + "does not specify a NodeType.");
                        }
                    }
                } else {
                    ToscaEngineServiceImpl.LOG.error("The RelationshipTemplate \""
                        + serviceTemplate.getTargetNamespace() + ":" + relationshipTemplate.getId()
                        + "does not specify a TargetElement.");
                }
            }
        }

        return nodeTypeQNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOperationOfATypeSpecifiedInputParams(final CSARID csarID, final QName typeID,
                                                           final String interfaceName, final String operationName) {

        final Object type = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeID);

        final Predicate<TOperation> inputParamListDefined = (op) -> op != null && op.getInputParameters() != null
            && op.getInputParameters().getInputParameter() != null;

        if (type instanceof TNodeType) {

            return getNodeTypeHierarchy(csarID, typeID).stream()
                                                       .map(qname -> toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                           qname))
                                                       .filter((jaxb) -> jaxb != null && jaxb instanceof TNodeType)
                                                       .map((jaxb) -> (TNodeType) jaxb)
                                                       .filter(nt -> nt.getInterfaces() != null)
                                                       .map((nt) -> getOperationFromInterfaces(nt.getInterfaces()
                                                                                                 .getInterface(),
                                                                                               interfaceName,
                                                                                               operationName))
                                                       .filter(inputParamListDefined)
                                                       .findFirst().map(op -> !op.getInputParameters()
                                                                                 .getInputParameter().isEmpty())
                                                       .orElse(false);

        } else if (type instanceof TRelationshipType) {

            final TRelationshipType relationshipType = (TRelationshipType) type;

            return Stream.of(Optional.ofNullable(relationshipType.getSourceInterfaces()).map(i -> i.getInterface()),
                             Optional.ofNullable(relationshipType.getTargetInterfaces()).map(i -> i.getInterface()))
                         .filter(ol -> ol.isPresent()).map((ol) -> ol.get())
                         .map(interfaces -> getOperationFromInterfaces(interfaces, interfaceName, operationName))
                         .filter(inputParamListDefined).findFirst()
                         .map(op -> !op.getInputParameters().getInputParameter().isEmpty()).orElse(false);

        } else {
            LOG.warn("Given typeID does not identifiy a NodeType or RelationshipType: {}", typeID);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOperationOfATypeSpecifiedOutputParams(final CSARID csarID, final QName typeID,
                                                            final String interfaceName, final String operationName) {

        final Object type = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeID);

        final Predicate<TOperation> outputParamListDefined = (op) -> op != null && op.getOutputParameters() != null
            && op.getOutputParameters().getOutputParameter() != null;

        if (type instanceof TNodeType) {

            return getNodeTypeHierarchy(csarID, typeID).stream()
                                                       .map(qname -> toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                           qname))
                                                       .filter((jaxb) -> jaxb != null && jaxb instanceof TNodeType)
                                                       .map((jaxb) -> (TNodeType) jaxb)
                                                       .filter(nt -> nt.getInterfaces() != null)
                                                       .map((nt) -> getOperationFromInterfaces(nt.getInterfaces()
                                                                                                 .getInterface(),
                                                                                               interfaceName,
                                                                                               operationName))
                                                       .filter(outputParamListDefined)
                                                       .findFirst().map(op -> !op.getOutputParameters()
                                                                                 .getOutputParameter().isEmpty())
                                                       .orElse(false);

        } else if (type instanceof TRelationshipType) {

            final TRelationshipType relationshipType = (TRelationshipType) type;

            return Stream.of(Optional.ofNullable(relationshipType.getSourceInterfaces()).map(i -> i.getInterface()),
                             Optional.ofNullable(relationshipType.getTargetInterfaces()).map(i -> i.getInterface()))
                         .filter(ol -> ol.isPresent()).map((ol) -> ol.get())
                         .map(interfaces -> getOperationFromInterfaces(interfaces, interfaceName, operationName))
                         .filter(outputParamListDefined).findFirst()
                         .map(op -> !op.getOutputParameters().getOutputParameter().isEmpty()).orElse(false);

        } else {
            LOG.warn("Given typeID does not identifiy a NodeType or RelationshipType: {}", typeID);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesInterfaceOfTypeContainOperation(final CSARID csarID, final QName typeID,
                                                       final String interfaceName, final String operationName) {

        final Object type = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeID);

        if (type instanceof TNodeType) {

            // handle NodeType operations
            for (final QName nodeTypeHierarchyMember : getNodeTypeHierarchy(csarID, typeID)) {

                final TNodeType nodeType =
                    (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                             nodeTypeHierarchyMember);

                if (nodeType.getInterfaces() != null) {

                    final TOperation operation = getOperationFromInterfaces(nodeType.getInterfaces().getInterface(),
                                                                            interfaceName, operationName);

                    if (operation != null) {
                        return true;
                    }
                }
            }
        } else if (type instanceof TRelationshipType) {

            // handle RelationshipType operations
            final TRelationshipType relationshipType = (TRelationshipType) type;

            if (relationshipType.getSourceInterfaces() != null) {
                final TOperation operation =
                    getOperationFromInterfaces(relationshipType.getSourceInterfaces().getInterface(), interfaceName,
                                               operationName);

                if (operation != null) {
                    return true;
                }
            }

            if (relationshipType.getTargetInterfaces() != null) {
                final TOperation operation =
                    getOperationFromInterfaces(relationshipType.getTargetInterfaces().getInterface(), interfaceName,
                                               operationName);

                if (operation != null) {
                    return true;
                }
            }

        } else {
            ToscaEngineServiceImpl.LOG.warn("Given typeID does not identifiy a NodeType or RelationshipType: {}",
                                            typeID);
        }

        return false;
    }

    /**
     * Get the TOperation object for a given interface and operation name from a list of interfaces.
     *
     * @param ifaces the List of interfaces
     * @param interfaceName the name of the interface of the operation
     * @param operationName the name of the operation
     * @return The TOperation object if one was found with the given properties, else
     *         <code>null</code>.
     */
    private TOperation getOperationFromInterfaces(final List<TInterface> ifaces, final String interfaceName,
                                                  final String operationName) {

        for (final TInterface iface : ifaces) {
            for (final TOperation operation : iface.getOperation()) {

                if (operation.getName().equals(operationName)
                    && (iface.getName().equals(interfaceName) || interfaceName == null)) {

                    return operation;
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOperationOfRelationshipBoundToSourceNode(final CSARID csarID, final QName relationshipTypeID,
                                                              final String interfaceName, final String operationName) {

        final TRelationshipType relationshipType =
            (TRelationshipType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                             relationshipTypeID);

        if (relationshipType.getSourceInterfaces() != null) {

            for (final TInterface iface : relationshipType.getSourceInterfaces().getInterface()) {

                if (iface.getName().equals(interfaceName) || interfaceName == null) {

                    for (final TOperation operation : iface.getOperation()) {

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
    public List<QName> getTypeImplementationsOfType(final CSARID csarID, final QName typeID) {

        final List<QName> listOfTypeImplementationQNames = new ArrayList<>();

        final Object type = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeID);

        if (type instanceof TNodeType) {

            // search in all Definitions inside a certain CSAR
            for (final TDefinitions definitions : ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsOfCSAR(csarID)) {

                for (final QName nodeTypeHierachyMember : getNodeTypeHierarchy(csarID, typeID)) {

                    // search for NodeTypeImplementations
                    for (final TExtensibleElements entity : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
                        if (entity instanceof TNodeTypeImplementation) {

                            final TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) entity;
                            if (nodeTypeImplementation.getNodeType().equals(nodeTypeHierachyMember)) {

                                String targetNamespace;
                                if (nodeTypeImplementation.getTargetNamespace() != null
                                    && !nodeTypeImplementation.getTargetNamespace().equals("")) {
                                    targetNamespace = nodeTypeImplementation.getTargetNamespace();
                                } else {
                                    targetNamespace = definitions.getTargetNamespace();
                                }
                                listOfTypeImplementationQNames.add(new QName(targetNamespace,
                                    nodeTypeImplementation.getName()));
                            }
                        }
                    }
                }
            }

        } else if (type instanceof TRelationshipType) {

            // search in all Definitions inside a certain CSAR
            for (final TDefinitions definitions : ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsOfCSAR(csarID)) {

                // search for RelationshipTypeImplementations
                for (final TExtensibleElements entity : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
                    if (entity instanceof TRelationshipTypeImplementation) {

                        final TRelationshipTypeImplementation relationshipTypeImplementation =
                            (TRelationshipTypeImplementation) entity;
                        if (relationshipTypeImplementation.getRelationshipType().equals(typeID)) {

                            String targetNamespace;
                            if (relationshipTypeImplementation.getTargetNamespace() != null
                                && !relationshipTypeImplementation.getTargetNamespace().equals("")) {
                                targetNamespace = relationshipTypeImplementation.getTargetNamespace();
                            } else {
                                targetNamespace = definitions.getTargetNamespace();
                            }
                            listOfTypeImplementationQNames.add(new QName(targetNamespace,
                                relationshipTypeImplementation.getName()));
                        }
                    }
                }
            }

        } else {
            ToscaEngineServiceImpl.LOG.warn("Given typeID does not identifiy a NodeType or RelationshipType: {}",
                                            typeID);
        }

        return listOfTypeImplementationQNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getImplementationArtifactNamesOfTypeImplementation(final CSARID csarID,
                                                                           final QName typeImplementationID) {

        final List<String> listOfNames = new ArrayList<>();

        for (final TImplementationArtifact ia : getImplementationArtifactsOfType(csarID, typeImplementationID)) {
            listOfNames.add(ia.getName());
        }

        return listOfNames;
    }

    /**
     * Return all ImplementationArtifacts for a given NodeTypeImplementation or
     * RelationshipTypeImplementation
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifacts.
     * @return List with all ImplementationArtifacts for the given type.
     */
    private List<TImplementationArtifact> getImplementationArtifactsOfType(final CSARID csarID,
                                                                           final QName typeImplementationID) {
        final List<TImplementationArtifact> listOfIAs = new ArrayList<>();

        final Object typeImplementation =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeImplementationID);

        if (typeImplementation instanceof TNodeTypeImplementation) {

            // handle NodeTypeImplementations
            for (final QName nodeTypeImplHierarchyMember : getNodeTypeImplementationTypeHierarchy(csarID,
                                                                                                  typeImplementationID)) {

                final TNodeTypeImplementation nodeTypeImplementation =
                    (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                           nodeTypeImplHierarchyMember);

                // add all IAs to the list
                if (nodeTypeImplementation.getImplementationArtifacts() != null
                    && nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact() != null) {
                    listOfIAs.addAll(nodeTypeImplementation.getImplementationArtifacts().getImplementationArtifact());
                }
            }

        } else if (typeImplementation instanceof TRelationshipTypeImplementation) {

            // handle RelationshipTypeImplementations
            final TRelationshipTypeImplementation relationshipTypeImplementation =
                (TRelationshipTypeImplementation) typeImplementation;

            // add all IAs to the list
            if (relationshipTypeImplementation.getImplementationArtifacts() != null
                && relationshipTypeImplementation.getImplementationArtifacts().getImplementationArtifact() != null) {
                listOfIAs.addAll(relationshipTypeImplementation.getImplementationArtifacts()
                                                               .getImplementationArtifact());
            }

        } else {
            ToscaEngineServiceImpl.LOG.warn("Given typeImplementationID does not identifiy a NodeTypeImplementation or RelationshipTypeImplementation: {}",
                                            typeImplementationID);
        }

        return listOfIAs;
    }

    @Override
    public String getRelatedNodeTemplateID(final CSARID csarID, final QName serviceTemplateID,
                                           final String nodeTemplateID, final QName relationshipType) {

        // get the ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

        final List<TEntityTemplate> templateList =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

        for (final TEntityTemplate template : templateList) {

            if (template instanceof TRelationshipTemplate) {

                final TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;

                final Object sourceElement = relationshipTemplate.getSourceElement().getRef();

                if (sourceElement instanceof TNodeTemplate) {

                    final TNodeTemplate sourceNodeTemplate = (TNodeTemplate) sourceElement;

                    if (sourceNodeTemplate.getId().equals(nodeTemplateID)) {

                        if (relationshipTemplate.getType().equals(relationshipType)) {

                            final Object targetElement = relationshipTemplate.getTargetElement().getRef();

                            if (targetElement instanceof TNodeTemplate) {

                                return ((TNodeTemplate) targetElement).getId();

                            }

                        }
                    }
                }

            }
        }

        ToscaEngineServiceImpl.LOG.error("The NodeTemplate \"" + nodeTemplateID
            + "\" has no related NodeTemplate with RelationshipType \"" + relationshipType
            + "\" or it isn't a NodeTemplate.");
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNodeTemplateIDOfRelationshipTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                                final String relationshipTemplateID) {

        // get the ServiceTemplate
        final TServiceTemplate serviceTemplate =
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

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

        ToscaEngineServiceImpl.LOG.error("The Relationship Template \"" + relationshipTemplateID
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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

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

        ToscaEngineServiceImpl.LOG.error("The Relationship Template \"" + relationshipTemplateID
            + "\" has no source element or it isn't a NodeTemplate.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public Document getArtifactSpecificContentOfADeploymentArtifact(final CSARID csarID, final QName reference,
                                                                    final String deploymentArtifactName) {

        TDeploymentArtifacts artifacts = null;

        final Object referenceObj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, reference);
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
                            ToscaEngineServiceImpl.LOG.error("There is content inside of the DeploymentArtifact \""
                                + deploymentArtifactName + "\" of the NodeTypeImplementation \"" + reference
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

        ToscaEngineServiceImpl.LOG.error("The requested DeploymentArtifact was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRequiredContainerFeaturesOfATypeImplementation(final CSARID csarID,
                                                                          final QName typeImplementationID) {

        final List<String> requiredFeatures = new ArrayList<>();

        final Object typeImplementation =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeImplementationID);

        if (typeImplementation instanceof TNodeTypeImplementation) {
            final TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) typeImplementation;

            if (nodeTypeImplementation.getRequiredContainerFeatures() != null) {
                for (final TRequiredContainerFeature requiredContainerFeature : nodeTypeImplementation.getRequiredContainerFeatures()
                                                                                                      .getRequiredContainerFeature()) {
                    requiredFeatures.add(requiredContainerFeature.getFeature());
                }
            }

        } else if (typeImplementation instanceof TRelationshipTypeImplementation) {
            final TRelationshipTypeImplementation relationshipTypeImplementation =
                (TRelationshipTypeImplementation) typeImplementation;

            if (relationshipTypeImplementation.getRequiredContainerFeatures() != null) {
                for (final TRequiredContainerFeature requiredContainerFeature : relationshipTypeImplementation.getRequiredContainerFeatures()
                                                                                                              .getRequiredContainerFeature()) {
                    requiredFeatures.add(requiredContainerFeature.getFeature());
                }
            }
        } else {
            ToscaEngineServiceImpl.LOG.warn("Given typeImplementationID does not identifiy a NodeTypeImplementation or RelationshipTypeImplementation: {}",
                                            typeImplementationID);
        }

        return requiredFeatures;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactTypeOfAImplementationArtifactOfATypeImplementation(final CSARID csarID,
                                                                               final QName typeImplementationID,
                                                                               final String implementationArtifactName) {

        for (final TImplementationArtifact ia : getImplementationArtifactsOfType(csarID, typeImplementationID)) {
            if (ia.getName().equals(implementationArtifactName)) {
                return ia.getArtifactType();
            }
        }

        ToscaEngineServiceImpl.LOG.error("The requested ArtifactType for CSARID: {}; TypeImplementation: {}; IA name: {}; was not found.",
                                         csarID, typeImplementationID, implementationArtifactName);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getArtifactTemplateOfAImplementationArtifactOfATypeImplementation(final CSARID csarID,
                                                                                   final QName typeImplementationID,
                                                                                   final String implementationArtifactName) {

        for (final TImplementationArtifact ia : getImplementationArtifactsOfType(csarID, typeImplementationID)) {
            if (ia.getName().equals(implementationArtifactName)) {
                return ia.getArtifactRef();
            }
        }

        ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate for CSARID: {}; TypeImplementation: {}; IA name: {}; was not found.",
                                         csarID, typeImplementationID, implementationArtifactName);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getReferenceAsNode(final CSARID csarID, final QName reference) {

        // get the ArtifactTemplate
        final Node artifactTemplateDoc =
            (Node) ToscaEngineServiceImpl.toscaReferenceMapper.getReferenceAsNode(csarID, reference);

        if (artifactTemplateDoc != null) {

            return artifactTemplateDoc;

        } else {

            ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate was not found.");
            return null;
        }
    }

    @Override
    public Node getInputParametersOfATypeOperation(final CSARID csarID, final QName typeID, final String interfaceName,
                                                   final String operationName) {

        final Object type = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeID);

        if (type instanceof TNodeType) {

            // handle NodeType operations
            for (final QName nodeTypeHierarchyMember : getNodeTypeHierarchy(csarID, typeID)) {

                final TNodeType nodeType =
                    (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                             nodeTypeHierarchyMember);

                if (nodeType.getInterfaces() != null) {

                    final TOperation operation = getOperationFromInterfaces(nodeType.getInterfaces().getInterface(),
                                                                            interfaceName, operationName);

                    if (operation != null && operation.getInputParameters() != null
                        && operation.getInputParameters().getInputParameter() != null) {

                        return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                  .marshalToNode(operation.getInputParameters());
                    }
                }
            }
        } else if (type instanceof TRelationshipType) {

            // handle RelationshipType operations
            final TRelationshipType relationshipType = (TRelationshipType) type;

            if (relationshipType.getSourceInterfaces() != null) {

                final TOperation operation =
                    getOperationFromInterfaces(relationshipType.getSourceInterfaces().getInterface(), interfaceName,
                                               operationName);

                if (operation != null && operation.getInputParameters() != null
                    && operation.getInputParameters().getInputParameter() != null) {

                    return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                              .marshalToNode(operation.getInputParameters());
                }
            }

            if (relationshipType.getTargetInterfaces() != null) {

                final TOperation operation =
                    getOperationFromInterfaces(relationshipType.getTargetInterfaces().getInterface(), interfaceName,
                                               operationName);

                if (operation != null && operation.getInputParameters() != null
                    && operation.getInputParameters().getInputParameter() != null) {

                    return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                              .marshalToNode(operation.getInputParameters());
                }
            }

        } else {
            ToscaEngineServiceImpl.LOG.warn("Given typeID does not identifiy a NodeType or RelationshipType: {}",
                                            typeID);
        }

        ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
        return null;
    }

    @Override
    public Node getOutputParametersOfATypeOperation(final CSARID csarID, final QName typeID, final String interfaceName,
                                                    final String operationName) {

        final Object type = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, typeID);

        if (type instanceof TNodeType) {

            // handle NodeType operations
            for (final QName nodeTypeHierarchyMember : getNodeTypeHierarchy(csarID, typeID)) {

                final TNodeType nodeType =
                    (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                             nodeTypeHierarchyMember);

                if (nodeType.getInterfaces() != null) {

                    final TOperation operation = getOperationFromInterfaces(nodeType.getInterfaces().getInterface(),
                                                                            interfaceName, operationName);

                    if (operation != null && operation.getOutputParameters() != null
                        && operation.getOutputParameters().getOutputParameter() != null) {

                        return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                                  .marshalToNode(operation.getOutputParameters());
                    }
                }
            }
        } else if (type instanceof TRelationshipType) {

            // handle RelationshipType operations
            final TRelationshipType relationshipType = (TRelationshipType) type;

            if (relationshipType.getSourceInterfaces() != null) {

                final TOperation operation =
                    getOperationFromInterfaces(relationshipType.getSourceInterfaces().getInterface(), interfaceName,
                                               operationName);

                if (operation != null && operation.getOutputParameters() != null
                    && operation.getOutputParameters().getOutputParameter() != null) {

                    return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                              .marshalToNode(operation.getOutputParameters());
                }
            }

            if (relationshipType.getTargetInterfaces() != null) {

                final TOperation operation =
                    getOperationFromInterfaces(relationshipType.getTargetInterfaces().getInterface(), interfaceName,
                                               operationName);

                if (operation != null && operation.getOutputParameters() != null
                    && operation.getOutputParameters().getOutputParameter() != null) {

                    return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                              .marshalToNode(operation.getOutputParameters());
                }
            }

        } else {
            ToscaEngineServiceImpl.LOG.warn("Given typeID does not identifiy a NodeType or RelationshipType: {}",
                                            typeID);
        }

        ToscaEngineServiceImpl.LOG.debug("The requested operation was not found.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getArtifactSpecificContentOfAImplementationArtifact(final CSARID csarID,
                                                                        final QName typeImplementationID,
                                                                        final String implementationArtifactName) {

        final TImplementationArtifact ia =
            getImplementationArtifactForName(csarID, typeImplementationID, implementationArtifactName);

        if (ia != null) {
            final List<Element> listOfAnyElements = new ArrayList<>();
            for (final Object obj : ia.getAny()) {
                if (obj instanceof Element) {
                    listOfAnyElements.add((Element) obj);
                } else {
                    ToscaEngineServiceImpl.LOG.error("There is content inside of the ImplementationArtifact \""
                        + implementationArtifactName + "\" of the TypeImplementation \"" + typeImplementationID
                        + "\" which is not a processable DOM Element.");
                    return null;
                }
            }

            return ServiceHandler.xmlSerializerService.getXmlSerializer()
                                                      .elementsIntoDocument(listOfAnyElements,
                                                                            "ImplementationArtifactSpecificContent");
        } else {
            ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterfaceOfAImplementationArtifactOfATypeImplementation(final CSARID csarID,
                                                                             final QName typeImplementationID,
                                                                             final String implementationArtifactName) {

        final TImplementationArtifact ia =
            getImplementationArtifactForName(csarID, typeImplementationID, implementationArtifactName);

        if (ia != null) {
            return ia.getInterfaceName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperationOfAImplementationArtifactOfATypeImplementation(final CSARID csarID,
                                                                             final QName typeImplementationID,
                                                                             final String implementationArtifactName) {

        final TImplementationArtifact ia =
            getImplementationArtifactForName(csarID, typeImplementationID, implementationArtifactName);

        if (ia != null) {
            return ia.getOperationName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getPropertiesOfAArtifactTemplate(final CSARID csarID, final QName artifactTemplateID) {

        final Object requestedObject =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);

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
                        ToscaEngineServiceImpl.LOG.debug("Return the Properties of the ArtifactTemplate \""
                            + artifactTemplateID + "\".");
                        return returnDoc;
                    } else {
                        ToscaEngineServiceImpl.LOG.error("The content of the Properties of the ArtifactTemplate \""
                            + artifactTemplateID + "\" could not be written into a DOM Document.");
                    }
                } else {
                    ToscaEngineServiceImpl.LOG.error("The content of the Properties of the ArtifactTemplate \""
                        + artifactTemplateID + "\" is not of the type DOM Element.");
                }
            }
        } else {
            ToscaEngineServiceImpl.LOG.error("The requested \"" + artifactTemplateID
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

        final Object requestedObject =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);

        if (requestedObject instanceof TArtifactTemplate) {

            // get the ArtifactTemplate
            final TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;

            if (artifactTemplate.getPropertyConstraints() != null) {
                return artifactTemplate.getPropertyConstraints().getPropertyConstraint();
            } else {
                ToscaEngineServiceImpl.LOG.debug("There are no PropertyConstraints inside of the ArtifactTemplate \""
                    + artifactTemplateID + "\".");
            }

        } else {
            ToscaEngineServiceImpl.LOG.error("The requested \"" + artifactTemplateID
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
        final Object requestedObject =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplateID);

        if (requestedObject instanceof TArtifactTemplate) {

            // get the ArtifactTemplate
            final TArtifactTemplate artifactTemplate = (TArtifactTemplate) requestedObject;

            if (artifactTemplate.getArtifactReferences() != null) {

                CSARContent csar;

                try {
                    csar = ServiceHandler.coreFileService.getCSAR(csarID);
                }
                catch (final UserException e) {
                    ToscaEngineServiceImpl.LOG.warn("An User Exception occured.", e);
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
                        ToscaEngineServiceImpl.LOG.warn("An User Exception occured.", exc);
                    }
                    catch (final SystemException exc) {
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
                ToscaEngineServiceImpl.LOG.debug("There are no ArtifactReferences in ArtifactTemplate \""
                    + artifactTemplateID + "\".");
            }
        } else {
            ToscaEngineServiceImpl.LOG.error("The requested \"" + artifactTemplateID
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
        final Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, NodeTemplateReference);

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

    @Override
    public QName getRelationshipTypeOfRelationshipTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                           final String relationshipTemplateID) {

        final QName RelationshipTemplateReference =
            new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);

        // get the RelationshipTemplate
        final TRelationshipTemplate relationshipTemplate =
            (TRelationshipTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                 RelationshipTemplateReference);

        // if there are ImplementationArtifacts
        if (relationshipTemplate != null) {
            return relationshipTemplate.getType();
        }

        ToscaEngineServiceImpl.LOG.error("The requested RelationshipTemplate was not found.");
        return null;

    }

    @Override
    public boolean doesNodeTemplateExist(final CSARID csarID, final QName serviceTemplateID,
                                         final String nodeTemplateID) {

        final QName nodeTemplateReference = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

        // get the NodeTemplate
        final Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTemplateReference);
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
    public boolean doesRelationshipTemplateExist(final CSARID csarID, final QName serviceTemplateID,
                                                 final String relationshipTemplateID) {
        final QName relationshipTemplateReference =
            new QName(serviceTemplateID.getNamespaceURI(), relationshipTemplateID);

        // get the NodeTemplate
        final Object obj =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, relationshipTemplateReference);
        if (null == obj) {
            ToscaEngineServiceImpl.LOG.warn("The requested reference \"" + relationshipTemplateReference
                + "\" was not found.");
        } else if (obj instanceof TRelationshipTemplate) {
            ToscaEngineServiceImpl.LOG.trace(relationshipTemplateReference + " is a RelationshipTemplate and exists.");
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
    public boolean clearCSARContent(final CSARID csarID) {
        return ToscaEngineServiceImpl.toscaReferenceMapper.clearCSARContent(csarID);
    }

    @Override
    public Document getPropertiesOfTemplate(final CSARID csarID, final QName serviceTemplateID,
                                            final String templateID) {

        // get the Namespace from the serviceTemplate
        final QName templateReference = new QName(serviceTemplateID.getNamespaceURI(), templateID);

        final Object template = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, templateReference);

        // retrieve the properties from the Template
        Properties properties = null;
        if (template instanceof TNodeTemplate) {
            final TNodeTemplate nodeTemplate = (TNodeTemplate) template;
            properties = nodeTemplate.getProperties();
        } else if (template instanceof TRelationshipTemplate) {
            final TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) template;
            properties = relationshipTemplate.getProperties();
        } else {
            ToscaEngineServiceImpl.LOG.error("Unable to retrieve a NodeTemplate or RelationshipTemplate for templateID: {}",
                                             templateID);
        }

        // return the document containing the properties if found
        if (properties != null) {
            final Object any = properties.getAny();
            if (any instanceof Element) {
                final Element element = (Element) any;
                return element.getOwnerDocument();
            } else {
                ToscaEngineServiceImpl.LOG.warn("Properties is not of class Element.");
            }
        } else {
            ToscaEngineServiceImpl.LOG.warn("Properties are not set.");
        }

        return null;
    }

    @Override
    public Document getPropertiesDefinitionOfNodeType(final CSARID csarID, final QName nodeTypeID) {

        // get the NodeType
        final TNodeType nodeType =
            (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTypeID);

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

                ToscaEngineServiceImpl.LOG.debug("No PropertiesDefinition defined.");
                return null;

            }
            catch (final ParserConfigurationException e) {
                e.printStackTrace();
            }

        }

        ToscaEngineServiceImpl.LOG.debug("NodeType {} not found.", nodeTypeID);
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
        final Object jaxbReferenceObject =
            ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, reference);
        // check if object was found
        if (jaxbReferenceObject == null) {
            ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - could not retrieve correlating JAXB-Object. Reference "
                + reference + " seems to be non-existent");
            return null;
        }

        // check if class could be retrieved
        final Class<? extends Object> jaxbClass = jaxbReferenceObject.getClass();

        if (jaxbClass == null) {
            ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - could not retrieve correlating JAXB-Class. Reference "
                + reference + " existents but is not a valid jaxb-class");
            return null;
        }

        try {
            // try to call .getName on the referencing jaxb class
            final Method getNameMethod = jaxbClass.getMethod("getName");
            if (getNameMethod == null) {
                ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - could not retrieve getName-Method of JAXB-Class. Reference "
                    + reference + " existents but is not a jaxb-class containing a getName Method");
                return null;
            }

            // invoke of parameterless getName()
            final String result = (String) getNameMethod.invoke(jaxbReferenceObject, (Object[]) null);
            // return result or emptyString if result == null
            if (result == null) {
                ToscaEngineServiceImpl.LOG.debug("Name attribute of " + reference + " was null - returning \"\"");
                return "";
            } else {
                return result;
            }

        }
        catch (final NoSuchMethodException e) {
            final String logMsg =
                String.format("Failed to extract name attribute: The retrieved class %s didn't contain a getName() method. Check if the call with csarid: %s and QName %s was valid! (maybe a bug in code!!!)",
                              jaxbClass, csarID.toString(), reference.toString());

            ToscaEngineServiceImpl.LOG.error(logMsg);
        }
        catch (final InvocationTargetException e) {
            ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - an Invocation-exception occured while invoking getName()",
                                             e.getCause());
        }
        catch (final Exception e) {
            ToscaEngineServiceImpl.LOG.error("Failed to extract name attribute - an exception occured while invoking getName()",
                                             e);
        }

        return null;

    }

    /**
     * Resolves the Deployment-Artifacts of a NodeTemplate
     *
     * @param csarID of the CSAR
     * @param nodeTemplateID
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no
     *         Artifact was found the returned list will be empty.
     */
    private List<ResolvedDeploymentArtifact> getNodeTemplateResolvedDAs(final CSARID csarID,
                                                                        final QName nodeTemplateID) {

        final List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<>();

        ToscaEngineServiceImpl.LOG.debug("Trying to fetch DA of NodeTemplate " + nodeTemplateID);

        final TNodeTemplate nodeTemplate =
            (TNodeTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeTemplateID);

        // check if there are implementationArtifact Entries
        if (nodeTemplate.getDeploymentArtifacts() == null
            || nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact() == null) {
            // return empty list
            ToscaEngineServiceImpl.LOG.warn("NodeTemplate " + nodeTemplate + " has no DeploymentArtifacts");
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
                    (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                     deployArt.getArtifactRef());

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
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no
     *         Artifact was found the returned list will be empty.
     */
    private List<ResolvedDeploymentArtifact> getNodeTypeImplResolvedDAs(final CSARID csarID,
                                                                        final QName nodeTypeImplementationID) {
        final List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<>();

        ToscaEngineServiceImpl.LOG.debug("Trying to fetch DA of NodeTypeImplementation"
            + nodeTypeImplementationID.toString());

        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                   nodeTypeImplementationID);

        // check if there are implementationArtifact Entries
        if (nodeTypeImplementation.getDeploymentArtifacts() == null
            || nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact() == null) {
            // return empty list
            ToscaEngineServiceImpl.LOG.debug("NodeTypeImplementation " + nodeTypeImplementationID.toString()
                + " has no DeploymentArtifacts");
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
                        (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                         deployArt.getArtifactRef());

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
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no
     *         Artifact was found the returned list will be empty.
     */
    private List<ResolvedImplementationArtifact> getNodeTypeImplResolvedIAs(final CSARID csarID,
                                                                            final QName nodeTypeImplementationID) {

        final List<ResolvedImplementationArtifact> resolvedIAs = new ArrayList<>();

        final TNodeTypeImplementation nodeTypeImplementation =
            (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                   nodeTypeImplementationID);

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
                    (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                     implArt.getArtifactRef());

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
                    getArtifactSpecificContentOfAImplementationArtifact(csarID, nodeTypeImplementationID,
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
     * @return List of ResolvedArtifact containing artifactSpecificContent or references. If no
     *         Artifact was found the returned list will be empty.
     */
    private List<ResolvedImplementationArtifact> getRelationshipTypeImplResolvedIAs(final CSARID csarID,
                                                                                    final QName relationshipTypeImplementationID) {
        final List<ResolvedImplementationArtifact> resolvedIAs = new ArrayList<>();

        final TRelationshipTypeImplementation relationshipTypeImplementation =
            (TRelationshipTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
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
                    (TArtifactTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                     implArt.getArtifactRef());

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
                    getArtifactSpecificContentOfAImplementationArtifact(csarID, relationshipTypeImplementationID,
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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);
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

        ToscaEngineServiceImpl.LOG.trace("Resolve the absolute path of the PlanModelReference of plan \"" + planId
            + "\" inside of CSAR \"" + csar + "\".");

        final QName containingDefinitions =
            ToscaEngineServiceImpl.toscaReferenceMapper.getContainingDefinitionsID(csar, planId);

        if (null != containingDefinitions) {

            ToscaEngineServiceImpl.LOG.trace("Desired path to the PlanModel is inside the Definitions \""
                + containingDefinitions + "\".");

            final String definitionsLocation =
                ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsLocation(csar, containingDefinitions);

            if (null != definitionsLocation) {

                ToscaEngineServiceImpl.LOG.trace("Definitions path is \"" + definitionsLocation + "\".");

                final TPlan plan = (TPlan) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csar, planId);
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

        ToscaEngineServiceImpl.LOG.trace("Resolve the absolute path of the PlanModelReference of plan \"" + planId
            + "\" inside of CSAR \"" + csar.getCSARID() + "\".");

        final QName containingDefinitions =
            ToscaEngineServiceImpl.toscaReferenceMapper.getContainingDefinitionsID(csar.getCSARID(), planId);

        if (null != containingDefinitions) {

            ToscaEngineServiceImpl.LOG.trace("Desired path to the PlanModel is inside the Definitions \""
                + containingDefinitions + "\".");

            final String definitionsLocation =
                ToscaEngineServiceImpl.toscaReferenceMapper.getDefinitionsLocation(csar.getCSARID(),
                                                                                   containingDefinitions);

            if (null != definitionsLocation) {

                ToscaEngineServiceImpl.LOG.trace("Definitions path is \"" + definitionsLocation + "\".");

                final TPlan plan =
                    (TPlan) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csar.getCSARID(), planId);
                final String planModelReferenceLocation = plan.getPlanModelReference().getReference();
                ToscaEngineServiceImpl.LOG.trace("planModelReferenceLocation: " + planModelReferenceLocation);
                final String absoluteLocation =
                    PathResolver.resolveRelativePath(definitionsLocation, planModelReferenceLocation, csar);

                ToscaEngineServiceImpl.LOG.trace("Absolute path to the PlanModel is \"" + absoluteLocation + "\".");

                try {

                    final AbstractArtifact artifact = csar.resolveArtifactReference(absoluteLocation);
                    if (null != artifact) {
                        return artifact;
                    }

                }
                catch (final UserException e) {
                    ToscaEngineServiceImpl.LOG.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
                catch (final SystemException e) {
                    ToscaEngineServiceImpl.LOG.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }

        ToscaEngineServiceImpl.LOG.error("There was an error while resolving the absolute path of the PlanModelReference of plan \""
            + planId + "\" inside of CSAR \"" + csar.getCSARID() + "\".");
        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getArtifactReferenceWithinArtifactTemplate(final CSARID csarID, final QName artifactTemplate) {

        final List<String> references = new ArrayList<>();

        final Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplate);

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

        final Object obj = ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, artifactTemplate);

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
            (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                   nodeTypeImplementationID);

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
            (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                   nodeTypeImplementationID);

        // if there are DeploymentArtifacts
        if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
            for (final TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts()
                                                                      .getDeploymentArtifact()) {

                if (da.getName().equals(deploymentArtifactName)) {
                    ToscaEngineServiceImpl.LOG.trace("The ArtifactTemplate is found and has the QName \""
                        + da.getArtifactRef() + "\".");
                    return da.getArtifactRef();
                }
            }
        }
        ToscaEngineServiceImpl.LOG.error("The requested ArtifactTemplate was not found.");
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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

        return serviceTemplate.getBoundaryDefinitions();
    }

    @Override
    public List<QName> getNodeTypeHierarchy(final CSARID csarID, final QName nodeType) {
        final List<QName> qnames = new ArrayList<>();
        final TNodeType nodeTypeElement =
            (TNodeType) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID, nodeType);

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
            (TNodeTypeImplementation) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarID,
                                                                                                   nodeTypeImplementationId);

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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);
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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

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
            (TServiceTemplate) ToscaEngineServiceImpl.toscaReferenceMapper.getJAXBReference(csarId, serviceTemplateId);

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
    public List<String> getInputParametersOfTypeOperation(final CSARID csarID, final QName typeId,
                                                          final String interfaceName, final String operationName) {
        return parseParameters(getInputParametersOfATypeOperation(csarID, typeId, interfaceName, operationName));
    }

    @Override
    public List<String> getOutputParametersOfTypeOperation(final CSARID csarID, final QName typeId,
                                                           final String interfaceName, final String operationName) {
        return parseParameters(getOutputParametersOfATypeOperation(csarID, typeId, interfaceName, operationName));
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

    /**
     * Retrieve all ImplementationArtifacts for a given NodeTypeImplementation or
     * RelationshipTypeImplementation and return the one that matches the given name.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact.
     * @param implementationArtifactName of the ImplementationArtifact
     * @return the ImplementationArtifact if found, <code>null</code> otherwise.
     */
    private TImplementationArtifact getImplementationArtifactForName(final CSARID csarID,
                                                                     final QName typeImplementationID,
                                                                     final String implementationArtifactName) {

        for (final TImplementationArtifact ia : getImplementationArtifactsOfType(csarID, typeImplementationID)) {
            if (ia.getName().equals(implementationArtifactName)) {
                return ia;
            }
        }

        ToscaEngineServiceImpl.LOG.error("The requested ImplementationArtifact was not found.");
        return null;
    }
}
