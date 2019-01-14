package org.opentosca.container.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactTemplate.ArtifactReferences;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.visitor.Visitor;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.CSARArtifact;
import org.opentosca.container.core.model.csar.Csar;

@NonNullByDefault
public final class ToscaEngine {

    public static TServiceTemplate findServiceTemplate(Csar csar, QName serviceTemplate) throws NotFoundException {
        return csar.serviceTemplates().stream()
            .filter(st -> st.getId().equals(serviceTemplate.getLocalPart()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Service template \"" + serviceTemplate + "\" could not be found"));
    }
    
    public static TNodeTemplate findNodeTemplate(TServiceTemplate serviceTemplate, String nodeTemplate) throws NotFoundException {
        TNodeTemplate nullable = serviceTemplate.getTopologyTemplate().getNodeTemplate(nodeTemplate);
        if (nullable == null) {
            throw new NotFoundException("Node template \"" + nodeTemplate + "\" could not be found");
        }
        return nullable;
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
    
    public static List<TNodeType> getNodeTypeHierarchy(Csar csar, String nodeTypeId) throws NotFoundException {
        final Comparator<TNodeType> compareById  = Comparator.comparing(TNodeType::getName);
        List<TNodeType> nodeTypes = csar.nodeTypes(); 
        Collections.sort(nodeTypes, compareById);
        
        List<TNodeType> typeRefs = new ArrayList<>();
        
        // this stub acts as base element to compare against for binary search
        final TNodeType stub = new TNodeType();
        stub.setName(nodeTypeId);
        int index = Collections.binarySearch(nodeTypes, stub, compareById);
        if (index < 0) {
            // element not found
            throw new NotFoundException("The requested node type was not present in the given csar");
        }
        TNodeType target = nodeTypes.get(index);
        
        typeRefs.add(target);
        // local introduced for correct null-analysis
        DerivedFrom derivedFrom = target.getDerivedFrom();
        while (derivedFrom != null) {
            // update stub to take the ID of the supertype
            stub.setName(derivedFrom.getTypeRef().toString());
            // find the target in our nodeTypes
            index = Collections.binarySearch(nodeTypes, stub, compareById);
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
        @SuppressWarnings({"serial", "null"})
        final TEntityTypeImplementation stub = new TEntityTypeImplementation() {
            @Override public HasType getDerivedFrom() { return null; /* noop */ }
            @Override public void setDerivedFrom(HasType value) { /* noop */ }
            @Override public void accept(Visitor visitor) { /* noop */ }
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

    @Deprecated
    //FIXME find a better representation of an Artifact inside a Csar
    public static List<AbstractArtifact> artifactsOfTemplate(TArtifactTemplate artifactTemplate, Csar csar) {
        ArtifactReferences references = artifactTemplate.getArtifactReferences();
        if (references == null) {
            return Collections.emptyList();
        } 
        return references.getArtifactReference().stream()
            .map(tar -> {
                List<Object> inclExcl = tar.getIncludeOrExclude();
                Set<String> includes = inclExcl.stream()
                    .filter(ie -> ie instanceof TArtifactReference.Include)
                    .map(TArtifactReference.Include.class::cast)
                    .map(TArtifactReference.Include::getPattern)
                    .collect(Collectors.toSet());
                Set<String> excludes = inclExcl.stream()
                    .filter(ie -> ie instanceof TArtifactReference.Exclude)
                    .map(TArtifactReference.Exclude.class::cast)
                    .map(TArtifactReference.Exclude::getPattern)
                    .collect(Collectors.toSet());
                CSARArtifact csarArtifact;
                try {
                    csarArtifact = new CSARArtifact(tar.getReference(),includes, excludes, csar.id().toOldCsarId(),
                                            Collections.emptySet(), Collections.emptyMap());
                }
                catch (UserException e) {
                    // LOGGER.warn("Could not create CSARArtifact", e);
                    return null;
                }
                return csarArtifact;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
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

    @Nullable
    public static TServiceTemplate containingServiceTemplate(Csar csar, TPlan toscaPlan) {
        return csar.serviceTemplates().stream()
            .filter(st -> {
                TPlans plans = st.getPlans();
                return plans == null ? false : plans.getPlan().stream().anyMatch(toscaPlan::equals);
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
    
    
}
