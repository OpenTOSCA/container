package org.opentosca.container.core.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class exposes a multitude of operations that one might want to perform with elements of a Csar. All arguments
 * passed to it are assumed to be set to a valid reference.
 * </p><p>
 * As a convention, methods beginning with <code>get</code> are not guaranteed to return a result. They default to
 * returning <code>null</code> or an empty {@link Optional}, if the request is unfulfillable. If a Collection type is
 * expected as result, an empty Collection is returned.
 * </p><p>
 * Likewise, methods beginning with <code>resolve</code> are guaranteed to return a result and throw a {@link
 * NotFoundException} in case the request is unfulfillable.
 * </p><p>
 * Methods that return some kind of {@link Collection} will return an empty collection as the default. They may throw
 * {@link NotFoundException} if a component prerequisite is not met.
 * </p>
 */
//@NonNullByDefault
public abstract class ToscaEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ToscaEngine.class);

    /**
     * Gets a serviceTemplate from a csar by its QName.
     *
     * @return null, if the service template could not be found, the {@link TServiceTemplate} otherwise.
     */
    @Nullable
    public static TServiceTemplate getServiceTemplate(Csar csar, QName serviceTemplateId) {
        return (TServiceTemplate) csar.queryRepository(new ServiceTemplateId(serviceTemplateId));
    }

    /**
     * Resolves a serviceTemplate from a csar by its fully qualified QName. If no matching serviceTemplate can be found,
     * an Exception is thrown.
     *
     * @return A {@link TServiceTemplate} instance matching the passed QName as its id. Guaranteed to not be
     * <tt>null</tt>.
     */
    public static TServiceTemplate resolveServiceTemplate(Csar csar, QName serviceTemplateId) throws NotFoundException {
        TServiceTemplate serviceTemplate = getServiceTemplate(csar, serviceTemplateId);
        if (serviceTemplate == null) {
            throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
        }
        return serviceTemplate;
    }

    /**
     * Resolves a ServiceTemplate from a csar by it's <b>local</b> id. If no matching serviceTemplate can be found, an
     * Exception is thrown.
     *
     * @param csar              The csar to query for the service template with the given id
     * @param serviceTemplateId The local id of the service template
     * @return A {@link TServiceTemplate} instance from within the given CSAR where the local id matches the search
     * parameter given to this method.
     */
    public static TServiceTemplate resolveServiceTemplate(Csar csar, String serviceTemplateId) throws NotFoundException {
        // Iterate service templates here to allow resolving service templates by name without knowing their fully qualified ID
        return csar.serviceTemplates()
            .stream()
            .filter(st -> serviceTemplateId.equals(st.getIdFromIdOrNameField()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(String.format("Csar %s does not contain a service template with the name %s", csar.id(), serviceTemplateId)));
    }

    public static TNodeTemplate resolveNodeTemplate(Csar csar, QName serviceTemplateId, String nodeTemplate) throws NotFoundException {
        return resolveNodeTemplate(resolveServiceTemplate(csar, serviceTemplateId), nodeTemplate);
    }

    public static TNodeTemplate resolveNodeTemplate(TServiceTemplate serviceTemplate, String nodeTemplate) throws NotFoundException {
        return getNodeTemplate(serviceTemplate, nodeTemplate)
            .orElseThrow(() -> new NotFoundException("Node template \"" + nodeTemplate + "\" could not be found"));
    }

    public static TRelationshipTemplate resolveRelationshipTemplate(TServiceTemplate serviceTemplate, String relationshipTemplate) throws NotFoundException {
        return getRelationshipTemplate(serviceTemplate, relationshipTemplate)
            .orElseThrow(() -> new NotFoundException("Relationship template \"" + relationshipTemplate + "\" could not be found"));
    }

    public static Optional<TNodeTemplate> getNodeTemplate(Csar csar, QName serviceTemplateId, String nodeTemplate) {
        TServiceTemplate serviceTemplate = getServiceTemplate(csar, serviceTemplateId);
        if (serviceTemplate == null) {
            return Optional.empty();
        }
        return getNodeTemplate(serviceTemplate, nodeTemplate);
    }

    public static Optional<TNodeTemplate> getNodeTemplate(TServiceTemplate serviceTemplate, String nodeTemplate) {
        return Optional.ofNullable(serviceTemplate.getTopologyTemplate())
            .map(tt -> tt.getNodeTemplate(nodeTemplate));
    }

    public static boolean isOperationBoundToSourceNode(final TRelationshipType relationshipType, final String interfaceName, final String operationName) {
        return Optional.ofNullable(relationshipType.getSourceInterfaces())
            .orElse(Collections.emptyList()).stream()
            .filter(anInterface -> interfaceName == null || anInterface.getName().equals(interfaceName))
            .flatMap(anInterface -> anInterface.getOperations().stream())
            .anyMatch(op -> op.getName().equals(operationName));
    }

    @Nullable
    public static TNodeTemplate getRelatedNodeTemplate(TServiceTemplate serviceTemplate, TNodeTemplate template, QName... relationshipType) {
        return getRelatedNodeTemplates(serviceTemplate, template, relationshipType)
            .findFirst()
            .orElse(null);
    }

    public static Stream<TNodeTemplate> getRelatedNodeTemplates(TServiceTemplate serviceTemplate, TNodeTemplate template, QName... relationshipType) {
        return serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
            .filter(candidate -> candidate instanceof TRelationshipTemplate)
            .map(relation -> (TRelationshipTemplate) relation)
            .filter(relation -> Arrays.stream(relationshipType).anyMatch(Predicate.isEqual(relation.getType())))
            .filter(relation -> {
                final Object source = relation.getSourceElement().getRef();
                return source instanceof TNodeTemplate && source.equals(template);
            })
            .map(relation -> relation.getTargetElement().getRef())
            .filter((target) -> target instanceof TNodeTemplate)
            .map(TNodeTemplate.class::cast);
    }

    public static List<TNodeTemplate> getRelatedSourceNodeTemplate(TServiceTemplate serviceTemplate, TNodeTemplate template, QName... relationshipType) {
        return serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
            .filter(candidate -> candidate instanceof TRelationshipTemplate)
            .map(relation -> (TRelationshipTemplate) relation)
            .filter(relation -> Arrays.stream(relationshipType).anyMatch(Predicate.isEqual(relation.getType())))
            .map(relation -> relation.getSourceElement().getRef())
            .filter((target) -> target instanceof TNodeTemplate)
            .map(TNodeTemplate.class::cast)
            .collect(Collectors.toList());
    }

    public static List<TInterface> getInterfaces(TNodeTemplate nodeTemplate, Csar csar) {
        TNodeType nodeType = resolveNodeType(csar, nodeTemplate);
        return Optional.ofNullable(nodeType.getInterfaces())
            .orElse(Collections.emptyList());
    }

    public static TEntityType resolveEntityTypeReference(Csar csar, QName typeId) throws NotFoundException {
        // prefer nodetypes for no particular reason
        try {
            return resolveNodeTypeReference(csar, typeId);
        } catch (NotFoundException e) {
            try {
                return resolveRelationshipTypeReference(csar, typeId);
            } catch (NotFoundException inner) {
                inner.addSuppressed(e);
                throw inner;
            }
        }
    }

    public static TNodeType resolveNodeType(Csar csar, TNodeTemplate nodeTemplate) {
        try {
            return resolveNodeTypeReference(csar, nodeTemplate.getType());
        } catch (NotFoundException e) {
            throw new RuntimeException("Could not resolve NodeType of an existing NodeTemplate, something went badly wrong", e);
        }
    }

    public static TNodeType resolveNodeTypeReference(Csar csar, QName nodeTypeId) throws NotFoundException {
        TNodeType nodeType = (TNodeType) csar.queryRepository(new NodeTypeId(nodeTypeId));
        if (nodeType == null) {
            throw new NotFoundException(String.format("Could not find NodeType [%s] in Csar [%s]", nodeTypeId, csar.id()));
        }
        return nodeType;
    }

    public static TRelationshipType resolveRelationshipTypeReference(Csar csar, QName relationshipTypeId) throws NotFoundException {
        TRelationshipType relationshipType = (TRelationshipType) csar.queryRepository(new RelationshipTypeId(relationshipTypeId));
        if (relationshipType == null) {
            throw new NotFoundException(String.format("Could not find RelationshipType [%s] in Csar [%s]", relationshipTypeId, csar.id()));
        }
        return relationshipType;
    }

    public static List<TNodeType> resolveNodeTypeHierarchy(Csar csar, TNodeTemplate nodeTypeId) throws NotFoundException {
        TNodeType target = resolveNodeType(csar, nodeTypeId);
        return resolveNodeTypeHierarchy(csar, target);
    }

    public static List<TNodeType> resolveNodeTypeHierarchy(Csar csar, TNodeType target) throws NotFoundException {
        List<TNodeType> typeRefs = new ArrayList<>();
        typeRefs.add(target);
        while (target.getDerivedFrom() != null) {
            target = resolveNodeTypeReference(csar, target.getDerivedFrom().getTypeRef());
            typeRefs.add(target);
        }
        return typeRefs;
    }

    public static TInterface resolveInterface(Csar csar, TEntityType type, String interfaceName) {
        if (type instanceof TRelationshipType) {
            // This is only required in YAML mode which is not yet supported in the container...
            return resolveInterface((TRelationshipType) type, interfaceName);
        } else if (type instanceof TNodeType) {
            return ModelUtils.getInterfaceOfNodeType(csar, (TNodeType) type, interfaceName);
        }
        LOG.error("The given EntityType was not a RelationshipType or NodeType!");
        return null;
    }

    public static TInterface resolveInterface(TRelationshipType relationshipType, String interfaceName) {
        TInterface iface = ModelUtils.getInterface(relationshipType.getInterfaces(), interfaceName);
        if (iface != null) {
            return iface;
        }
        TInterface sourceIface = ModelUtils.getInterface(relationshipType.getSourceInterfaces(), interfaceName);
        if (sourceIface != null) {
            return sourceIface;
        }
        TInterface targetIface = ModelUtils.getInterface(relationshipType.getTargetInterfaces(), interfaceName);
        if (targetIface != null) {
            return targetIface;
        }
        return null;
    }

    public static TOperation resolveOperation(TInterface tInterface, String operationName) throws NotFoundException {
        return tInterface.getOperations().stream()
            .filter(op -> op.getName().equals(operationName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Operation [" + operationName + "] was not found on the given Interface"));
    }

    public static TOperation resolveOperation(Csar csar, TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
        return resolveOperation(ModelUtils.getInterfaceOfNodeType(csar, nodeType, interfaceName), operationName);
    }

    public static Optional<TRelationshipTemplate> getRelationshipTemplate(TServiceTemplate serviceTemplate, String localTemplateId) {
        return Objects.isNull(localTemplateId) || Objects.isNull(serviceTemplate) || Objects.isNull(serviceTemplate.getTopologyTemplate())
            ? Optional.empty()
            : Optional.ofNullable(serviceTemplate.getTopologyTemplate().getRelationshipTemplate(localTemplateId));
    }

    public static List<? extends TEntityTypeImplementation> getTypeImplementations(Csar csar, TEntityType type) {
        if (type instanceof TNodeType) {
            return getNodeTypeImplementations(csar, (TNodeType) type);
        } else if (type instanceof TRelationshipType) {
            return getRelationshipTypeImplementations(csar, (TRelationshipType) type);
        } else {
            LOG.warn("Attempted to get TypeImplementations for EntityType other than NodeType or RelationshipType");
            return Collections.emptyList();
        }
    }

    public static List<TRelationshipTypeImplementation> getRelationshipTypeImplementations(Csar csar, TRelationshipType type) {
        return csar.relationshipTypeImplementations().stream()
            .filter(impl -> impl.getRelationshipType().equals(type.getQName()))
            .collect(Collectors.toList());
    }

    public static List<TNodeTypeImplementation> getNodeTypeImplementations(Csar csar, TNodeType type) {
        final List<TNodeType> hierarchy;
        try {
            hierarchy = resolveNodeTypeHierarchy(csar, type);
        } catch (NotFoundException e) {
            LOG.warn("Could not resolve type hierarchy for known NodeType");
            return Collections.emptyList();
        }

        // keep NodeTypeImplementations in hierarchy order avoiding using an overwritten implementation
        List<TNodeTypeImplementation> result = new ArrayList<>();
        for (TNodeType hierarchyNodeType : hierarchy) {
            result.addAll(csar.nodeTypeImplementations().stream().filter(impl -> {
                try {
                    return resolveNodeTypeReference(csar, impl.getNodeType()).getQName().equals(hierarchyNodeType.getQName());
                } catch (NotFoundException e) {
                    return false;
                }
            }).collect(Collectors.toList()));
        }

        return result;
    }

    public static List<TImplementationArtifact> implementationArtifacts(TEntityTypeImplementation impl) {
        return Optional.ofNullable(impl.getImplementationArtifacts())
            .orElse(Collections.emptyList());
    }

    public static TImplementationArtifact resolveImplementationArtifact(TEntityTypeImplementation impl, String iaName) throws NotFoundException {
        return implementationArtifacts(impl).stream()
            .filter(ia -> ia.getName().equals(iaName))
            .findFirst()
            .orElseThrow(() ->
                new NotFoundException("No implementation Artifact matching " + iaName + "found in EntityTypeImplementation " + impl.getIdFromIdOrNameField())
            );
    }

    public static List<TNodeTypeImplementation> nodeTypeImplementations(Csar csar, TNodeType superType) {
        QName typeRef = QName.valueOf(superType.getIdFromIdOrNameField());
        return csar.nodeTypeImplementations().stream()
            .filter(nti -> nti.getDerivedFrom().getType().equals(typeRef))
            .collect(Collectors.toList());
    }

    @NonNull
    public static TPlan resolvePlanReference(Csar csar, QName planId) throws NotFoundException {
        // can't reformulate using queryRepository because PlanId requires a PlansId as parent for resolution
        return csar.serviceTemplates().stream()
            .flatMap(st ->
                st.getPlans() == null ? Stream.empty() : st.getPlans().stream()
            )
            .filter(tPlan -> tPlan.getId().equals(planId.getLocalPart()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("No plan matching " + planId + " was found in csar" + csar.id().csarName()));
    }

    @NonNull
    public static TArtifactTemplate resolveArtifactTemplate(Csar csar, QName artifactTemplateId) throws NotFoundException {
        TArtifactTemplate artifactTemplate = (TArtifactTemplate) csar.queryRepository(new ArtifactTemplateId(artifactTemplateId));
        if (artifactTemplate == null) {
            throw new NotFoundException("No artifactTemplate matching " + artifactTemplateId + " was found in csar " + csar.id().csarName());
        }
        return artifactTemplate;
    }

    public static TArtifactType resolveArtifactType(Csar csar, QName artifactTypeId) throws NotFoundException {
        TArtifactType result = (TArtifactType) csar.queryRepository(new ArtifactTypeId(artifactTypeId));
        if (result == null) {
            throw new NotFoundException(String.format("Csar [%s] does not contain the ArtifactType [%s]", csar.id().csarName(), artifactTypeId));
        }
        return result;
    }

    public static TEntityTypeImplementation resolveTypeImplementation(Csar csar, QName typeImplementationQName) throws NotFoundException {
        TEntityTypeImplementation result = (TEntityTypeImplementation) csar.queryRepository(new NodeTypeImplementationId(typeImplementationQName));
        if (result == null) {
            result = (TEntityTypeImplementation) csar.queryRepository(new RelationshipTypeImplementationId(typeImplementationQName));
        }
        if (result == null) {
            throw new NotFoundException(String.format("No NodeTypeImplementation or RelationshipTypeImplementation found for id [%s] in Csar %s", typeImplementationQName, csar.id()));
        }
        return result;
    }

    @Nullable
    public static Document getArtifactTemplateProperties(Csar csar, QName artifactTemplateId) {
        try {
            return getEntityTemplateProperties(resolveArtifactTemplate(csar, artifactTemplateId));
        } catch (NotFoundException missing) {
            return null;
        }
    }

    @Nullable
    public static Document getNodeTemplateProperties(TNodeTemplate nodeTemplate) {
        return getEntityTemplateProperties(nodeTemplate);
    }

    @Nullable
    public static Document getEntityTemplateProperties(TEntityTemplate template) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();

            if (template.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                TEntityTemplate.WineryKVProperties props = (TEntityTemplate.WineryKVProperties) template.getProperties();
                Map<String, String> propMap = props.getKVProperties();

                // So just that people understand:
                /*
                <Properties>
                    <Properties xmlns="http://opentosca.org/nodetypes/propertiesdefinition/winery">
                        <ContainerPort>80</ContainerPort>
                        <Port>get_input: ApplicationPort</Port>
                        <ContainerID/>
                        <ContainerIP/>
                    </Properties>
                </Properties>

                <Properties>
                    <DockerEngine_Properties xmlns="http://opentosca.org/nodetypes/properties">
                        <DockerEngineURL>get_input: DockerEngineURL</DockerEngineURL>
                        <DockerEngineCertificate/>
                        <State>Running</State>
                    </DockerEngine_Properties>
                </Properties>

                In case of the  DockerEngine Properties props.getElementName() returns DockerEngine_Properties

                But

                in case of the MyTinyToDoDocker Container Properties props.getElementName() returns NULL!!

                TODO: FIXME in winery!
                 */

                Element rootElement = doc.createElementNS(props.getNamespace(), props.getElementName() != null ? props.getElementName() : "Properties");
                doc.appendChild(rootElement);

                for (String propName : propMap.keySet()) {
                    Element propElement = doc.createElementNS(props.getNamespace(), propName);
                    propElement.setTextContent(propMap.get(propName));
                    rootElement.appendChild(propElement);
                }
            }

            return doc;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<TExportedOperation> listOperations(TServiceTemplate serviceTemplate) {
        return Optional.of(serviceTemplate)
            .map(TServiceTemplate::getBoundaryDefinitions)
            .map(TBoundaryDefinitions::getInterfaces)
            .orElse(Collections.emptyList())
            .stream()
            .map(TExportedInterface::getOperation)
            .flatMap(Collection::stream);
    }

    @Nullable
    public static TExportedOperation getReferencingOperationWithin(TServiceTemplate serviceTemplate, String planReference) {
        return listOperations(serviceTemplate)
            .filter(operation -> ((TPlan) operation.getPlan().getPlanRef()).getId().equals(planReference))
            .findFirst()
            .orElse(null);
    }

    public static TExportedOperation resolveBoundaryDefinitionOperation(TServiceTemplate serviceTemplate, String interfaceName, String operationName) throws NotFoundException {
        return Optional.of(serviceTemplate)
            .map(TServiceTemplate::getBoundaryDefinitions)
            .map(TBoundaryDefinitions::getInterfaces)
            .orElse(Collections.emptyList())
            .stream()
            .filter(anInterface -> anInterface.getName().equals(interfaceName))
            .findFirst()
            .map(TExportedInterface::getOperation)
            .orElse(Collections.emptyList())
            .stream()
            .filter(op -> op.getName().equals(operationName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(String.format("Could not resolve operation [%s] in interface [%s]", operationName, interfaceName)));
    }

    public static boolean isOperationUniqueInType(Csar csar, TEntityType type, String providedInterface, String neededOperation) {
        if (type instanceof TNodeType) {
            return isOperationUniqueInType(csar, (TNodeType) type, providedInterface, neededOperation);
        } else if (type instanceof TRelationshipType) {
            return isOperationUniqueInType(csar, (TRelationshipType) type, providedInterface, neededOperation);
        } else {
            return false;
        }
    }

    public static boolean isOperationUniqueInType(Csar csar, TNodeType type, String providedInterface, String neededOperation) {
        final List<TNodeType> hierarchy;
        try {
            hierarchy = resolveNodeTypeHierarchy(csar, type);
        } catch (NotFoundException e) {
            LOG.warn("Could not resolve NodeTypeHierarchy for known node type");
            return false;
        }
        return hierarchy.stream()
            .filter(t -> t.getInterfaces() != null)
            .flatMap(t -> t.getInterfaces().stream()
                // iterate over the interfaces that match the provided interface
                .filter(i -> i.getName().equals(providedInterface))
            )
            // filter to only those that provide the needed operation
            .filter(i -> {
                try {
                    return resolveOperation(i, neededOperation) != null;
                } catch (NotFoundException e) {
                    return false;
                }
            })
            // and check whether it is only a single one
            .count() == 1;
    }

    public static boolean isOperationUniqueInType(Csar csar, TRelationshipType type, String providedInterface, String neededOperation) {
        List<TInterface> interfaces = type.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        return interfaces.stream()
            .filter(i -> i.getName().equals(providedInterface))
            // filter to only those that provide the needed operation
            .filter(i -> {
                try {
                    return resolveOperation(i, neededOperation) != null;
                } catch (NotFoundException e) {
                    return false;
                }
            })
            // and check whether it is only a single one
            .count() == 1;
    }
}
