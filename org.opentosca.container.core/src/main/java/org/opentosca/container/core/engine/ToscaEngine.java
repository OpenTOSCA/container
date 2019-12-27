package org.opentosca.container.core.engine;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.common.ids.definitions.*;
import org.eclipse.winery.model.tosca.*;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.xml.XMLHelper;
import org.opentosca.container.core.model.csar.Csar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class exposes a multitude of operations that one might want to perform with elements of a Csar.
 * All arguments passed to it are assumed to be set to a valid reference.
 * </p><p>
 * As a convention, methods beginning with <code>get</code> are not guaranteed to return a result.
 * They default to returning <code>null</code> or an empty {@link Optional}, if the request is not fulfillable.
 * If a Collection type is expected as result, an empty Collection is returned.
 * </p><p>
 * Likewise, methods beginning with <code>resolve</code> are guaranteed to return a result and throw a {@link NotFoundException} in case the request is not fulfillable.
 * </p><p>
 * Methods that return some kind of {@link Collection} will return an empty collection as the default.
 * They may throw {@link NotFoundException} if a component prerequisite is not met.
 * </p>
 */
@NonNullByDefault
public final class ToscaEngine {

  private static final Logger LOG = LoggerFactory.getLogger(ToscaEngine.class);

  /**
   * Gets a serviceTemplate from a csar by it's QName.
   *
   * @return null, if the service template could not be found, the {@link TServiceTemplate} otherwise.
   */
  @Nullable
  public static TServiceTemplate getServiceTemplate(Csar csar, QName serviceTemplateId) {
    return (TServiceTemplate) csar.queryRepository(new ServiceTemplateId(serviceTemplateId));
  }

  /**
   * Resolves a serviceTemplate from a csar by it's fully qualified QName.
   * If no matching serviceTemplate can be found, an Exception is thrown.
   *
   * @return A {@link TServiceTemplate} instance matching the passed QName as it's id.
   * Guaranteed to not be <tt>null</tt>.
   * @throws NotFoundException
   */
  public static TServiceTemplate resolveServiceTemplate(Csar csar, QName serviceTemplateId) throws NotFoundException {
    TServiceTemplate serviceTemplate = getServiceTemplate(csar, serviceTemplateId);
    if (serviceTemplate == null) {
      throw new NotFoundException("Service template \"" + serviceTemplate + "\" could not be found");
    }
    return serviceTemplate;
  }

  /**
   * Resolves a ServiceTemplate from a csar by it's <b>local</b> name.
   * If no matching serviceTemplate can be found, an Exception is thrown.
   *
   * @param csar                The csar to query for the service template with the given name
   * @param serviceTemplateName The local name of the service template
   * @return A {@link TServiceTemplate} instance from within the given CSAR where the local name matches
   *         the search parameter given to this method.
   * @throws NotFoundException
   */
  public static TServiceTemplate resolveServiceTemplate(Csar csar, String serviceTemplateName) throws NotFoundException {
    // Iterate service templates here to allow resolving service templates by name without knowing their fully qualified Id
    return csar.serviceTemplates()
      .stream()
      .filter(st -> serviceTemplateName.equals(st.getName()))
      .findFirst()
      .orElseThrow(() -> new NotFoundException(String.format("Csar %s does not contain a service template with the name %s", csar.id(), serviceTemplateName)));
  }

  public static TNodeTemplate resolveNodeTemplate(Csar csar, QName serviceTemplateId, String nodeTemplate) throws NotFoundException {
    return resolveNodeTemplate(resolveServiceTemplate(csar, serviceTemplateId), nodeTemplate);
  }

  public static TNodeTemplate resolveNodeTemplate(TServiceTemplate serviceTemplate, String nodeTemplate) throws NotFoundException {
    return getNodeTemplate(serviceTemplate, nodeTemplate)
      .orElseThrow(() -> new NotFoundException("Node template \"" + nodeTemplate + "\" could not be found"));
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
    return Optional.ofNullable(relationshipType.getSourceInterfaces()).map(TInterfaces::getInterface)
      .orElse(Collections.emptyList()).stream()
      .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
      .flatMap(iface -> iface.getOperation().stream())
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

  public static List<TInterface> getInterfaces(TNodeTemplate nodeTemplate, Csar csar) {
    TNodeType nodeType = resolveNodeType(csar, nodeTemplate);
    TInterfaces nullable = nodeType.getInterfaces();
    return nullable == null ? Collections.emptyList() : nullable.getInterface();
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

  public static TInterface resolveInterfaceAbstract(TEntityType type, String interfaceName) throws NotFoundException {
    if (type instanceof TRelationshipType) {
      return resolveInterface((TRelationshipType) type, interfaceName);
    } else if (type instanceof TNodeType) {
      return resolveInterface((TNodeType) type, interfaceName);
    } else {
      throw new NotFoundException("The given EntityType was not a RelationshipType or NodeType");
    }
  }

  public static TInterface resolveInterface(TRelationshipType relationshipType, String interfaceName) throws NotFoundException {
    return resolveInterface(relationshipType.getInterfaces(), interfaceName);
  }

  public static TInterface resolveInterface(TNodeType nodeType, String interfaceName) throws NotFoundException {
    return resolveInterface(nodeType.getInterfaces(), interfaceName);
  }

  private static TInterface resolveInterface(TInterfaces interfaces, String interfaceName) throws NotFoundException {
    return Stream.of(Optional.ofNullable(interfaces))
      .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
      .filter(iface -> iface.getName().equals(interfaceName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given EntityType"));
  }

  public static TOperation resolveOperation(TInterface iface, String operationName) throws NotFoundException {
    return iface.getOperation().stream()
      .filter(op -> op.getName().equals(operationName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Operation [" + operationName + "] was not found on the given Interface"));
  }

  public static TOperation resolveOperation(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    return resolveOperation(resolveInterface(nodeType, interfaceName), operationName);
  }

  public static TOperation resolveOperation(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    return resolveOperation(resolveInterface(relationshipType, interfaceName), operationName);
  }

  public static boolean operationHasInputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    return hasInputParameters(resolveOperation(nodeType, interfaceName, operationName));
  }

  public static boolean operationHasInputParams(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    return hasInputParameters(resolveOperation(relationshipType, interfaceName, operationName));
  }

  private static boolean hasInputParameters(TOperation operation) {
    TOperation.InputParameters inputParams = operation.getInputParameters();
    return inputParams != null && !inputParams.getInputParameter().isEmpty();
  }

  public static boolean operationHasOutputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    return hasOutputParameters(resolveOperation(nodeType, interfaceName, operationName));
  }

  public static boolean operationHasOutputParams(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    return hasOutputParameters(resolveOperation(relationshipType, interfaceName, operationName));
  }

  private static boolean hasOutputParameters(TOperation operation) {
    TOperation.OutputParameters outputParams = operation.getOutputParameters();
    return outputParams != null && !outputParams.getOutputParameter().isEmpty();
  }

  public static Optional<TRelationshipTemplate> getRelationshipTemplate(TServiceTemplate serviceTemplate, String localTemplateId) {
    return Optional.ofNullable(serviceTemplate.getTopologyTemplate().getRelationshipTemplate(localTemplateId));
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
    // FIXME this is a bit weird, because it resolves the implementations of the whole type hierarchy,
    //  but that matches the previous implementation, soo ...
    return csar.nodeTypeImplementations().stream()
      .filter(impl -> {
        try {
          TNodeType implementationNodeType = resolveNodeTypeReference(csar, impl.getNodeType());
          return hierarchy.contains(implementationNodeType);
        } catch (NotFoundException e) {
          LOG.warn("Could not find NodeType of a known NodeTypeImplementation");
          return false;
        }
      })
      .collect(Collectors.toList());
  }

  public static List<TImplementationArtifacts.ImplementationArtifact> implementationArtifacts(TEntityTypeImplementation impl) {
    TImplementationArtifacts nullable = impl.getImplementationArtifacts();
    return nullable == null ? Collections.emptyList() : nullable.getImplementationArtifact();
  }

  public static TImplementationArtifacts.ImplementationArtifact resolveImplementationArtifact(TEntityTypeImplementation impl, String iaName) throws NotFoundException {
    return implementationArtifacts(impl).stream()
      .filter(ia -> ia.getName().equals(iaName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No implementation Artifact matching " + iaName + "found in EntityTypeImplementation " + impl.getIdFromIdOrNameField()));
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
    TPlan plan = csar.serviceTemplates().stream()
      .flatMap(st -> {
        TPlans plans = st.getPlans();
        return plans == null ? Stream.empty() : plans.getPlan().stream();
      })
      .filter(tplan -> tplan.getId().equals(planId.getLocalPart()))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No plan matching " + planId + " was found in csar" + csar.id().csarName()));
    return plan;
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
      throw new NotFoundException(String.format("Csar [{}] does not contain the ArtifacType [{}]", csar.id().csarName(), artifactTypeId));
    }
    return result;
  }

  @Nullable
  public static TServiceTemplate getContainingServiceTemplate(Csar csar, TPlan toscaPlan) {
    // can't obtain serviceTemplateId from the plan, therefore iterate all service templates
    return csar.serviceTemplates().stream()
      .filter(st -> {
        TPlans plans = st.getPlans();
        return plans != null && plans.getPlan().stream().anyMatch(toscaPlan::equals);
      })
      .findFirst()
      .orElse(null);
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
    return Optional.of(template)
      .map(TEntityTemplate::getProperties)
      // map via internal any to deal with HashMap property code in winery
      .map(TEntityTemplate.Properties::getInternalAny)
      .filter(p -> p instanceof Element)
      .map(Element.class::cast)
      .map(XMLHelper::fromRootNode)
      .orElse(null);
  }

  private static Stream<TExportedOperation> listOperations(TServiceTemplate serviceTemplate) {
    return Optional.of(serviceTemplate)
      .map(TServiceTemplate::getBoundaryDefinitions)
      .map(TBoundaryDefinitions::getInterfaces)
      .map(TBoundaryDefinitions.Interfaces::getInterface)
      .orElse(Collections.emptyList())
      .stream()
      .map(TExportedInterface::getOperation)
      .flatMap(Collection::stream);
  }

  @Nullable
  public static TExportedOperation getReferencingOperationWithin(TServiceTemplate serviceTemplate, TPlan plan) {
    return listOperations(serviceTemplate)
      // winery automatically fills the PlanRef with the TPlan instance
      .filter(operation -> operation.getPlan().getPlanRef().equals(plan))
      .findFirst()
      .orElse(null);
  }

  @Nullable
  public static TExportedOperation getReferencingOperationWithin(TServiceTemplate serviceTemplate, String planReference) {
    return listOperations(serviceTemplate)
      .filter(operation -> ((TPlan)operation.getPlan().getPlanRef()).getId().equals(planReference))
      .findFirst()
      .orElse(null);
  }

  public static TExportedOperation resolveBoundaryDefinitionOperation(TServiceTemplate serviceTemplate, String interfaceName, String operationName) throws NotFoundException {
    return Optional.of(serviceTemplate)
      .map(TServiceTemplate::getBoundaryDefinitions)
      .map(TBoundaryDefinitions::getInterfaces)
      .map(TBoundaryDefinitions.Interfaces::getInterface)
      .orElse(Collections.emptyList())
      .stream()
      .filter(iface -> iface.getName().equals(interfaceName))
      .findFirst()
      .map(TExportedInterface::getOperation)
      .orElse(Collections.emptyList())
      .stream()
      .filter(op -> op.getName().equals(operationName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException(String.format("Could not resolve operation [%s] in interface [%s]", operationName, interfaceName)));
  }

  @Nullable
  public static TExportedInterface getReferencingInterfaceWithin(TServiceTemplate serviceTemplate, TExportedOperation operation) {
    return Optional.of(serviceTemplate)
      .map(TServiceTemplate::getBoundaryDefinitions)
      .map(TBoundaryDefinitions::getInterfaces)
      .map(TBoundaryDefinitions.Interfaces::getInterface)
      .orElse(Collections.emptyList())
      .stream()
      .filter(iface -> iface.getOperation().contains(operation))
      .findFirst()
      .orElse(null);
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
      .flatMap(t -> t.getInterfaces().getInterface().stream()
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
    TInterfaces interfaces = type.getInterfaces();
    if (interfaces == null) {
      return false;
    }
    return interfaces.getInterface().stream()
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
