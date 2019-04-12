package org.opentosca.container.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.*;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.model.tosca.visitor.Visitor;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.xml.XMLHelper;
import org.opentosca.container.core.model.csar.Csar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NonNullByDefault
public final class ToscaEngine {

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

  public static List<TNodeType> getNodeTypeHierarchy(Csar csar, String nodeTypeId) throws NotFoundException {
    final Comparator<TNodeType> compareById = Comparator.comparing(TNodeType::getName);
    List<TNodeType> nodeTypes = csar.nodeTypes();
    nodeTypes.sort(compareById);

    List<TNodeType> typeRefs = new ArrayList<>();
    TNodeType target = resolveNodeTypeReference(csar, nodeTypeId);
    typeRefs.add(target);
    // local introduced for correct null-analysis
    final TNodeType stub = new TNodeType();
    DerivedFrom derivedFrom = target.getDerivedFrom();
    while (derivedFrom != null) {
      // update stub to take the ID of the supertype
      stub.setName(derivedFrom.getTypeRef().toString());
      // find the target in our nodeTypes
      int index = Collections.binarySearch(nodeTypes, stub, compareById);
      if (index < 0) {
        // target type not found
        return typeRefs;
      }
      target = nodeTypes.get(index);
      derivedFrom = target.getDerivedFrom();
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

  public static TInterface interfaceByName(TNodeType nodeType, String interfaceName) throws NotFoundException {
    @SuppressWarnings("null")
    TInterface result = Stream.of(Optional.ofNullable(nodeType.getInterfaces()))
      .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
      .filter(iface -> iface.getName().equals(interfaceName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given NodeType"));
    return result;
  }

  public static TInterface interfaceByName(TRelationshipType relationshipType, String interfaceName) throws NotFoundException {
    @SuppressWarnings("null")
    TInterface result = Stream.of(Optional.ofNullable(relationshipType.getInterfaces()))
      .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
      .filter(iface -> iface.getName().equals(interfaceName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given RelationshipType"));
    return result;
  }

  public static TOperation operationByName(TInterface iface, String operationName) throws NotFoundException {
    return iface.getOperation().stream()
      .filter(op -> op.getName().equals(operationName))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Operation [" + operationName + "] was not found on the given Interface"));
  }

  public static boolean operationHasInputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = interfaceByName(nodeType, interfaceName);
    TOperation operation = operationByName(iface, operationName);
    TOperation.InputParameters inputParams = operation.getInputParameters();

    return inputParams != null && !inputParams.getInputParameter().isEmpty();
  }

  public static boolean operationHasOutputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = interfaceByName(nodeType, interfaceName);
    TOperation operation = operationByName(iface, operationName);
    TOperation.OutputParameters outputParams = operation.getOutputParameters();

    return outputParams != null && !outputParams.getOutputParameter().isEmpty();
  }

  public static boolean operationHasInputParams(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = interfaceByName(relationshipType, interfaceName);
    TOperation operation = operationByName(iface, operationName);
    TOperation.InputParameters inputParams = operation.getInputParameters();

    return inputParams != null && !inputParams.getInputParameter().isEmpty();
  }

  public static boolean operationHasOutputParams(TRelationshipType relationshipType, String interfaceName, String operationName) throws NotFoundException {
    TInterface iface = interfaceByName(relationshipType, interfaceName);
    TOperation operation = operationByName(iface, operationName);
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

  public static TImplementationArtifacts.ImplementationArtifact implementationArtifact(TEntityTypeImplementation impl, String iaName) throws NotFoundException {
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

  public static List<TEntityTypeImplementation> implementationTypeHierarchy(TEntityTypeImplementation impl, Csar csar) throws NotFoundException {
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
  public static TServiceTemplate containingServiceTemplate(Csar csar, TPlan toscaPlan) {
    return csar.serviceTemplates().stream()
      .filter(st -> {
        TPlans plans = st.getPlans();
        return plans != null && plans.getPlan().stream().anyMatch(toscaPlan::equals);
      })
      .findFirst()
      .orElse(null);
  }

  public static TNodeTypeImplementation findNodeTypeImplementation(Csar csar, QName nodeTypeImplQname) throws NotFoundException {
    return csar.nodeTypeImplementations().stream()
      .filter(nti -> QName.valueOf(nti.getIdFromIdOrNameField()).equals(nodeTypeImplQname))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No node type implementation was found for the QName " + nodeTypeImplQname));
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
}
