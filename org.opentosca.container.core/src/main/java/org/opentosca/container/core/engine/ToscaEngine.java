package org.opentosca.container.core.engine;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.*;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.model.tosca.visitor.Visitor;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.xml.XMLHelper;
import org.opentosca.container.core.model.csar.Csar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 *     This class exposes a multitude of operations that one might want to perform with elements of a Csar.
 *     All arguments passed to it are assumed to be set to a valid reference.
 * </p><p>
 *     As a convention, methods beginning with <code>get</code> are not guaranteed to return a result.
 *     They default to returning <code>null</code> or an empty {@link Optional}, if the request is not fulfillable.
 *     If a Collection type is expected as result, an empty Collection is returned.
 * </p><p>
 *     Likewise, methods beginning with <code>resolve</code> are guaranteed to return a result and throw a {@link NotFoundException} in case the request is not fulfillable.
 * </p><p>
 *     Methods that return some kind of {@link Collection} will return an empty collection as the default.
 *     They may throw {@link NotFoundException} if a component prerequisite is not met.
 * </p>
 */
@NonNullByDefault
public final class ToscaEngine {

  private static final Logger LOG = LoggerFactory.getLogger(ToscaEngine.class);

  /**
   * Gets a serviceTemplate from a csar by it's QName. This delegates to {@link #resolveServiceTemplate(Csar, QName)},
   * but will return null instead of throwing an exception
   *
   * @return null, if the service template could not be found, the {@link TServiceTemplate} otherwise.
   */
  @Nullable
  public static TServiceTemplate getServiceTemplate(Csar csar, QName serviceTemplate) {
    try {
      return resolveServiceTemplate(csar, serviceTemplate);
    } catch (NotFoundException e) {
      return null;
    }
  }

  /**
   * Resolves a serviceTemplate from a csar by it's QName.
   * If no matching serviceTemplate can be found, an Exception is thrown.
   *
   * @return A {@link TServiceTemplate} instance matching the passed QName's localPart with its id.
   * Guaranteed to not be <tt>null</tt>.
   * @throws NotFoundException
   */
  public static TServiceTemplate resolveServiceTemplate(Csar csar, QName serviceTemplate) throws NotFoundException {
    return csar.serviceTemplates().stream()
      .filter(st -> st.getId().equals(serviceTemplate.getLocalPart()))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Service template \"" + serviceTemplate + "\" could not be found"));
  }

  public static TNodeTemplate resolveNodeTemplate(Csar csar, QName serviceTemplateId, String nodeTemplate) throws NotFoundException {
    return resolveNodeTemplate(resolveServiceTemplate(csar, serviceTemplateId), nodeTemplate);
  }

  public static TNodeTemplate resolveNodeTemplate(TServiceTemplate serviceTemplate, String nodeTemplate) throws NotFoundException {
    TNodeTemplate nullable = serviceTemplate.getTopologyTemplate().getNodeTemplate(nodeTemplate);
    if (nullable == null) {
      throw new NotFoundException("Node template \"" + nodeTemplate + "\" could not be found");
    }
    return nullable;
  }

  public static Optional<TNodeTemplate> getNodeTemplate(TServiceTemplate serviceTemplate, String nodeTemplate) {
    try {
      return Optional.of(resolveNodeTemplate(serviceTemplate, nodeTemplate));
    } catch (NotFoundException e) {
      return Optional.empty();
    }
  }

  @Nullable
  public static TNodeTemplate getRelatedNodeTemplateWithin(TServiceTemplate serviceTemplate, TNodeTemplate template, QName relationshipType) {
    return serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
      .filter(candidate -> candidate instanceof TRelationshipTemplate)
      .map(relation -> (TRelationshipTemplate) relation)
      .filter((relation) -> relation.getType().equals(relationshipType)).filter(relation -> {
        final Object source = relation.getSourceElement().getRef();
        return source instanceof TNodeTemplate && source.equals(template);
      }).map(relation -> relation.getTargetElement().getRef())
      .filter((target) -> target instanceof TNodeTemplate)
      .map(TNodeTemplate.class::cast)
      .findFirst()
      .orElse(null);
  }

  public static List<TInterface> getInterfaces(TNodeTemplate nodeTemplate, Csar csar) {
    @Nullable
    TNodeType nodeType = csar.nodeTypes().stream()
      .filter(type -> type.getName().equals(nodeTemplate.getType().getLocalPart()))
      .findFirst().orElse(null);
    if (nodeType == null) {
      return Collections.emptyList();
    }
    TInterfaces nullable = nodeType.getInterfaces();
    return nullable == null ? Collections.emptyList() : nullable.getInterface();
  }

  public static TNodeType resolveNodeTypeReference(Csar csar, String nodeTypeId) throws NotFoundException {
    final Comparator<TNodeType> compareById = Comparator.comparing(TNodeType::getName);
    List<TNodeType> nodeTypes = csar.nodeTypes();
    nodeTypes.sort(compareById);

    // this stub acts as base element to compare against for binary search
    final TNodeType stub = new TNodeType();
    stub.setName(nodeTypeId);
    int index = Collections.binarySearch(nodeTypes, stub, compareById);
    if (index < 0) {
      // element not found
      throw new NotFoundException("The requested node type was not present in the given csar");
    }
    return nodeTypes.get(index);
  }

  public static List<TNodeType> resolveNodeTypeHierarchy(Csar csar, String nodeTypeId) throws NotFoundException {
    List<TNodeType> typeRefs = new ArrayList<>();
    TNodeType target = resolveNodeTypeReference(csar, nodeTypeId);
    typeRefs.add(target);
    while (target.getDerivedFrom() != null) {
      target = (TNodeType)csar.queryRepository(new NodeTypeId(target.getDerivedFrom().getTypeRef()));
      typeRefs.add(target);
    }
    return typeRefs;
  }

  public static List<TNodeType> referencedNodeTypes(Csar csar, TServiceTemplate serviceTemplate) {
    List<TNodeType> nodeTypes = csar.nodeTypes();
    final Comparator<TNodeType> byTargetNamespaceAndName = Comparator.comparing(TNodeType::getTargetNamespace)
      .thenComparing(TNodeType::getName);
    Collections.sort(nodeTypes, byTargetNamespaceAndName);
    return referencedNodeTypeNames(csar, serviceTemplate).stream()
      .map(name -> {
        TNodeType stub = new TNodeType();
        stub.setTargetNamespace(serviceTemplate.getTargetNamespace());
        stub.setName(name);
        int index = Collections.binarySearch(nodeTypes, stub, byTargetNamespaceAndName);
        if (index < 0) {
          return null;
        }
        return nodeTypes.get(index);
      })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public static List<String> referencedNodeTypeNames(Csar csar, TServiceTemplate serviceTemplate) {
    // FIXME make sure that this is semantically appropriate!
    return serviceTemplate.getTopologyTemplate()
      .getNodeTemplates().stream()
      .map(nodeTemplate -> nodeTemplate.getType())
      .map(QName::getLocalPart)
      .collect(Collectors.toList());
  }

  public static TInterface resolveInterface(TNodeType nodeType, String interfaceName) throws NotFoundException {
    @SuppressWarnings("null")
    TInterface result = Stream.of(Optional.ofNullable(nodeType.getInterfaces()))
      .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
      .filter(iface -> iface.getName().equals(interfaceName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given NodeType"));
    return result;
  }

  public static TInterface resolveInterface(TRelationshipType relationshipType, String interfaceName) throws NotFoundException {
    @SuppressWarnings("null")
    TInterface result = Stream.of(Optional.ofNullable(relationshipType.getInterfaces()))
      .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
      .filter(iface -> iface.getName().equals(interfaceName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given RelationshipType"));
    return result;
  }

  public static TOperation resolveOperation(TInterface iface, String operationName) throws NotFoundException {
    return iface.getOperation().stream()
      .filter(op -> op.getName().equals(operationName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Operation [" + operationName + "] was not found on the given Interface"));
  }

  public static boolean operationHasInputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = resolveInterface(nodeType, interfaceName);
    TOperation operation = resolveOperation(iface, operationName);
    TOperation.InputParameters inputParams = operation.getInputParameters();

    return inputParams != null && !inputParams.getInputParameter().isEmpty();
  }

  public static boolean operationHasOutputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = resolveInterface(nodeType, interfaceName);
    TOperation operation = resolveOperation(iface, operationName);
    TOperation.OutputParameters outputParams = operation.getOutputParameters();

    return outputParams != null && !outputParams.getOutputParameter().isEmpty();
  }

  public static boolean operationHasInputParams(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = resolveInterface(relationshipType, interfaceName);
    TOperation operation = resolveOperation(iface, operationName);
    TOperation.InputParameters inputParams = operation.getInputParameters();

    return inputParams != null && !inputParams.getInputParameter().isEmpty();
  }

  public static boolean operationHasOutputParams(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = resolveInterface(relationshipType, interfaceName);
    TOperation operation = resolveOperation(iface, operationName);
    TOperation.OutputParameters outputParams = operation.getOutputParameters();

    return outputParams != null && !outputParams.getOutputParameter().isEmpty();
  }

  public static List<TRelationshipTemplate> relationshipTemplates(TServiceTemplate serviceTemplate) {
    return serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
  }

  public static List<TRelationshipTypeImplementation> relationshipTypeImplementations(Csar csar, TRelationshipTemplate relationshipTemplate) {
    QName type = relationshipTemplate.getType();
    return csar.relationshipTypeImplementations().stream()
      .filter(rtImpl -> rtImpl.getRelationshipType().equals(type))
      .collect(Collectors.toList());
  }

  public static List<TRelationshipTypeImplementation> relationshipTypeImplementations(Csar csar, TNodeType nodeType) {
    QName type = QName.valueOf(nodeType.getIdFromIdOrNameField());
    return csar.relationshipTypeImplementations().stream()
      .filter(rtImpl -> rtImpl.getRelationshipType().equals(type))
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

  public static List<TEntityTypeImplementation> resolveImplementationTypeHierarchy(TEntityTypeImplementation impl, Csar csar) throws NotFoundException {
    List<TEntityTypeImplementation> allImplementations = new ArrayList<>();
    allImplementations.addAll(csar.nodeTypeImplementations());
    allImplementations.addAll(csar.relationshipTypeImplementations());

    Comparator<TEntityTypeImplementation> byTypeRef = Comparator.comparing(TEntityTypeImplementation::getTypeAsQName,
      Comparator.comparing(QName::getNamespaceURI).thenComparing(QName::getLocalPart));
    Collections.sort(allImplementations, byTypeRef);

    List<TEntityTypeImplementation> typeRefs = new ArrayList<>();

    // this stub acts as base element to compare against for binary search
    @SuppressWarnings( {"serial", "null"}) final TEntityTypeImplementation stub = new TEntityTypeImplementation() {
      @Override
      public HasType getDerivedFrom() {
        return null; /* noop */
      }

      @Override
      public void setDerivedFrom(HasType value) { /* noop */ }

      @Override
      public void accept(Visitor visitor) { /* noop */ }
    };
    stub.setType(impl.getTypeAsQName());
    int index = Collections.binarySearch(allImplementations, stub, byTypeRef);
    if (index < 0) {
      // element not found
      throw new NotFoundException("The requested node type was not present in the given csar");
    }
    TEntityTypeImplementation target = allImplementations.get(index);

    typeRefs.add(target);
    while (target.getDerivedFrom() != null && target.getDerivedFrom().getTypeAsQName() != null) {
      // update stub to take the ID of the supertype
      stub.setType(target.getDerivedFrom().getTypeAsQName());
      // find the target in our nodeTypes
      index = Collections.binarySearch(allImplementations, stub, byTypeRef);
      if (index < 0) {
        // target type not found
        return typeRefs;
      }
      target = allImplementations.get(index);
      typeRefs.add(target);
    }
    return typeRefs;
  }

  @NonNull
  public static TPlan resolvePlanReference(Csar csar, QName planId) throws NotFoundException {
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
    TArtifactTemplate artifactTemplate = csar.artifactTemplates().stream()
      .filter(candidate -> candidate.getId().equals(artifactTemplateId.toString()))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No artifactTemplate matching " + artifactTemplateId + " was found in csar " + csar.id().csarName()));
    return artifactTemplate;
  }

  @Nullable
  public static TServiceTemplate getContainingServiceTemplate(Csar csar, TPlan toscaPlan) {
    return csar.serviceTemplates().stream()
      .filter(st -> {
        TPlans plans = st.getPlans();
        return plans != null && plans.getPlan().stream().anyMatch(toscaPlan::equals);
      })
      .findFirst()
      .orElse(null);
  }

  public static TNodeTypeImplementation resolveNodeTypeImplementation(Csar csar, QName nodeTypeImplQname) throws NotFoundException {
    return csar.nodeTypeImplementations().stream()
      .filter(nti -> QName.valueOf(nti.getIdFromIdOrNameField()).equals(nodeTypeImplQname))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No node type implementation was found for the QName " + nodeTypeImplQname));
  }

  public static ResolvedArtifacts resolvedDeploymentArtifactsOfNodeTemplate(Csar context, TNodeTemplate nodeTemplate) {
    final ResolvedArtifacts result = new ResolvedArtifacts();
    result.setDeploymentArtifacts(resolvedDeploymentArtifactsForNodeTemplate(context, nodeTemplate));
    return result;
  }

  public static List<ResolvedArtifacts.ResolvedDeploymentArtifact> resolvedDeploymentArtifactsForNodeTemplate(Csar context, TNodeTemplate nodeTemplate) {
    LOG.debug("Trying to fetch DAs of NodeTemplate {}", nodeTemplate.getName());
    if (nodeTemplate.getDeploymentArtifacts() == null
        || nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact() == null) {
      LOG.info("NodeTemplate {} has no deployment artifacts", nodeTemplate.getName());
      return Collections.emptyList();
    }

    return nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact().stream()
      .map(da -> ToscaEngine.resolveDA(context, nodeTemplate, da))
      .collect(Collectors.toList());
  }

  private static ResolvedArtifacts.ResolvedDeploymentArtifact resolveDA(Csar context, TNodeTemplate nodeTemplate, TDeploymentArtifact da) {
    final ResolvedArtifacts.ResolvedDeploymentArtifact result = new ResolvedArtifacts.ResolvedDeploymentArtifact();
    result.setName(da.getName());
    result.setType(da.getArtifactType());

    // assumption: there is artifactSpecificContent OR an artifactTemplateRef
    if (da.getArtifactRef() != null) {
      result.setArtifactSpecificContent(/* resolve artifact specific content */null);
      return result;
    }

    TArtifactTemplate template = (TArtifactTemplate) context.queryRepository(new ArtifactTemplateId(da.getArtifactRef()));
    final List<String> references = new ArrayList<>();
    for (final TArtifactReference artifactReference : Optional.ofNullable(template.getArtifactReferences()).map(ars -> ars.getArtifactReference()).orElse(Collections.emptyList())) {
      // if there is no include patterns, just add the reference
      if (artifactReference.getIncludeOrExclude() == null
        || artifactReference.getIncludeOrExclude().isEmpty()) {
          references.add(artifactReference.getReference());
          continue;
      }
      artifactReference.getIncludeOrExclude().stream()
        .filter(o -> o instanceof TArtifactReference.Include)
        .map(TArtifactReference.Include.class::cast)
        .forEach(includePattern -> references.add(artifactReference.getReference() + "/" + includePattern.getPattern()));
    }
    result.setReferences(references);

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
  public static Document getNodeTemplateProperties(Csar csar, QName serviceTemplateId, String nodeTemplateId) {
    try {
      return getNodeTemplateProperties(resolveServiceTemplate(csar, serviceTemplateId), nodeTemplateId);
    } catch (NotFoundException e) {
      return null;
    }
  }

  @Nullable
  public static Document getNodeTemplateProperties(TServiceTemplate serviceTemplate, String nodeTemplateId) {
    try {
      return getEntityTemplateProperties(resolveNodeTemplate(serviceTemplate, nodeTemplateId));
    } catch (NotFoundException e) {
      return null;
    }
  }

  @Nullable
  public static Document getNodeTemplateProperties(TNodeTemplate nodeTemplate) {
    return getEntityTemplateProperties(nodeTemplate);
  }

  @Nullable
  private static Document getEntityTemplateProperties(TEntityTemplate template) {
    return Optional.of(template)
      .map(TEntityTemplate::getProperties)
      .map(TEntityTemplate.Properties::getAny)
      .filter(p -> p instanceof Element)
      .map(Element.class::cast)
      .map(XMLHelper::fromRootNode)
      .orElse(null);
  }

  @Nullable
  public static TExportedOperation getReferencingOperationWithin(TServiceTemplate serviceTemplate, TPlan plan) {
    return Optional.of(serviceTemplate)
      .map(TServiceTemplate::getBoundaryDefinitions)
      .map(TBoundaryDefinitions::getInterfaces)
      .map(TBoundaryDefinitions.Interfaces::getInterface)
      .orElse(Collections.emptyList())
      .stream()
      .map(TExportedInterface::getOperation)
      .flatMap(Collection::stream)
      .filter(operation -> operation.getPlan().getPlanRef().equals(plan))
      .findFirst()
      .orElse(null);
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
}
