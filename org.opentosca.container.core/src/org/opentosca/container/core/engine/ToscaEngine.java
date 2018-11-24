package org.opentosca.container.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.model.csar.Csar;

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
        while (target.getDerivedFrom() != null && target.getDerivedFrom().getTypeRef() != null) {
            // update stub to take the ID of the supertype
            stub.setName(target.getDerivedFrom().getTypeRef().toString());
            // find the target in our nodeTypes
            index = Collections.binarySearch(nodeTypes, stub, compareById);
            if (index < 0) {
                // target type not found
                return typeRefs;
            }
            target = nodeTypes.get(index);
            typeRefs.add(target);
        }
        return typeRefs;
    }
    
    public static List<String> referencedNodeTypeNames(Csar csar, TServiceTemplate serviceTemplate) {
        // FIXME make sure that this is semantically appropriate!
        return serviceTemplate.getTopologyTemplate()
            .getNodeTemplates().stream()
            .map(nodeTemplate -> nodeTemplate.getType())
            .map(QName::getLocalPart)
            .collect(Collectors.toList());
    }
    
    public static boolean operationHasInputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
        TOperation.InputParameters inputParams = Stream.of(Optional.ofNullable(nodeType.getInterfaces()))
            .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
            .filter(iface -> iface.getName().equals(interfaceName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given NodeType"))
            .getOperation().stream()
            .filter(op -> op.getName().equals(operationName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Operation [" + operationName + "] was not found on the given Interface"))
            .getInputParameters();
        
        return inputParams != null && !inputParams.getInputParameter().isEmpty();
    }
    
    public static boolean operationHasInputParams(TRelationshipType relationshipType, String interfaceName, String operationName) {
        // FIXME implement
        throw new UnsupportedOperationException();
    }
    
    public static boolean operationHasOutputParams(TNodeType nodeType, String interfaceName, String operationName) throws NotFoundException {
        TOperation.OutputParameters outputParams = Stream.of(Optional.ofNullable(nodeType.getInterfaces()))
            .flatMap(opt -> opt.map(TInterfaces::getInterface).orElse(Collections.emptyList()).stream())
            .filter(iface -> iface.getName().equals(interfaceName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Interface [" + interfaceName + "] was not found in the given NodeType"))
            .getOperation().stream()
            .filter(op -> op.getName().equals(operationName))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Operation [" + operationName + "] was not found on the given Interface"))
            .getOutputParameters();
        
        return outputParams != null && !outputParams.getOutputParameter().isEmpty();
    }
    
    public static boolean operationHasOutputParams(TRelationshipType relationshipType, String interfaceName, String operationName){
        // FIXME implement
        throw new UnsupportedOperationException();
    }
    
    
}
