package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.patternbased.bpel.PatternBasedHandler.ConcreteOperationMatching;

public abstract class PatternBasedHandler {

	class OperationMatching {

		String interfaceName;
		String operationName;
		Map<AbstractParameter, String> inputMatching = new HashMap<>();
		Map<AbstractParameter, String> outputMatching = new HashMap<>();

	}

	class ConcreteOperationMatching {

		String interfaceName;
		String operationName;
		Map<AbstractParameter, Variable> inputMatching = new HashMap<>();
		Map<AbstractParameter, Variable> outputMatching = new HashMap<>();
	}

	protected static final BPELInvokerPlugin invoker = new BPELInvokerPlugin();

	protected boolean invokeOperation(final BPELPlanContext context, final ConcreteOperationMatching matching,
			final AbstractNodeTemplate hostingContainer) {

		return invoker.handle(context, hostingContainer.getId(), true, matching.operationName, matching.interfaceName,
				"planCallbackAddress_invoker", transformForInvoker(matching.inputMatching),
				transformForInvoker(matching.outputMatching), BPELScopePhaseType.PROVISIONING);
	}

	private Map<String, Variable> transformForInvoker(final Map<AbstractParameter, Variable> map) {
		final Map<String, Variable> newMap = new HashMap<>();
		map.forEach((x, y) -> newMap.put(x.getName(), y));
		return newMap;
	}
	
	protected boolean invokeWithMatching(final BPELPlanContext context, AbstractNodeTemplate nodeTemplate,
			AbstractInterface iface, AbstractOperation op, Set<AbstractNodeTemplate> nodesForMatching) {
		final ConcreteOperationMatching matching = createConcreteOperationMatching(context,
				createPropertyToParameterMatching(nodesForMatching, iface, op), nodesForMatching);
		return invokeOperation(context, matching, nodeTemplate);
	}

	protected ConcreteOperationMatching createConcreteOperationMatching(final BPELPlanContext context, final OperationMatching abstractMatching, Collection<AbstractNodeTemplate> nodesForMatching) {
	
	    final ConcreteOperationMatching matching = new ConcreteOperationMatching();
	
	    matching.interfaceName = abstractMatching.interfaceName;
	    matching.operationName = abstractMatching.operationName;
	
	    for (final AbstractParameter param : abstractMatching.inputMatching.keySet()) {
	        boolean added = false;
	        
	        for(AbstractNodeTemplate nodeForMatch : nodesForMatching) {
	        	for (final String nodePropName : ModelUtils.getPropertyNames(nodeForMatch)) {
	        		if (abstractMatching.inputMatching.get(param).equals(nodePropName)) {
	        			matching.inputMatching.put(param, context.getPropertyVariable(nodeForMatch, nodePropName));
	        			added = true;
	        			break;
	        		}
	        	}
	        	if (added) {
	        		break;
	        	}
	        }	        
	    }
	
	    
	    
	    for (final AbstractParameter param : abstractMatching.outputMatching.keySet()) {	    		    
	        boolean added = false;      
	        for(AbstractNodeTemplate nodeForMatch : nodesForMatching) {   	
	        	for (final String nodePropName : ModelUtils.getPropertyNames(nodeForMatch)) {
	        		if (abstractMatching.outputMatching.get(param).equals(nodePropName)) {
	        			matching.outputMatching.put(param, context.getPropertyVariable(nodeForMatch, nodePropName));
	        			added = true;
	        			break;
	        		}
	        	}
	        	if (added) {
	        		break;
	        	}
	        }	        
	    }
	
	    return matching;
	}

	protected boolean hasCompleteMatching(Collection<AbstractNodeTemplate> nodesForMatching, final AbstractInterface ifaceToMatch, final AbstractOperation operationToMatch) {
	
	    final OperationMatching matching =
	        createPropertyToParameterMatching(nodesForMatching, ifaceToMatch, operationToMatch);
	
	    if (matching.inputMatching.size() == operationToMatch.getInputParameters().size()) {
	        return true;
	    }
	
	    return false;
	}

	protected OperationMatching createPropertyToParameterMatching(Collection<AbstractNodeTemplate> nodesForMatching, final AbstractInterface ifaceToMatch, final AbstractOperation operationToMatch) {
	    final OperationMatching matching = new OperationMatching();
	
	    matching.interfaceName = ifaceToMatch.getName();
	    matching.operationName = operationToMatch.getName();
	    
	    for (final AbstractParameter param : operationToMatch.getInputParameters()) {
	        boolean matched = false;
	        
	        for(AbstractNodeTemplate nodeForMatching : nodesForMatching) {
	        	for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
	                if (param.getName().equals(propName)) {
	                    matching.inputMatching.put(param, propName);
	                    matched = true;
	                    break;
	                }
	            }
	        	 if (matched) {
	                 break;
	             }
	        }
	        
	        
	       
	        
	    }
	
	    for (final AbstractParameter param : operationToMatch.getOutputParameters()) {
	        boolean matched = false;
	       
	        for(AbstractNodeTemplate nodeForMatching : nodesForMatching) {
	        	for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
	                if (param.getName().equals(propName)) {
	                    matching.outputMatching.put(param, propName);
	                    matched = true;
	                    break;
	                }
	            }
	        	 if (matched) {
	                 break;
	             }
	        }                       
	    }
	
	    return matching;
	}
}
