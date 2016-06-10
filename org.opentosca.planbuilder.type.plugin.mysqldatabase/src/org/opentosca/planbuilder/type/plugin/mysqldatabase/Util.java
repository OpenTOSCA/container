/**
 * 
 */
package org.opentosca.planbuilder.type.plugin.mysqldatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.planbuilder.utils.Utils;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Util {
	
	public static boolean hasSqlScriptArtifact(List<AbstractDeploymentArtifact> das) {
		for (AbstractDeploymentArtifact da : das) {
			if (Util.isSqlScriptArtifact(da)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSqlScriptArtifact(AbstractDeploymentArtifact da) {
		if (da.getArtifactType().equals(Constants.sqlScriptArtifactType)) {
			return true;
		}
		return false;
	}
	
	public static List<AbstractImplementationArtifact> getIAsForInterfaces(List<AbstractImplementationArtifact> ias) {
		List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<AbstractImplementationArtifact>();
		for (AbstractImplementationArtifact ia : ias) {
			if ((ia.getOperationName() == null) || ia.getOperationName().equals("")) {
				iasForIfaces.add(ia);
			}
		}
		return iasForIfaces;
	}
	
	public static List<AbstractImplementationArtifact> getIAsForLifecycleInterface(List<AbstractImplementationArtifact> ias) {
		List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<AbstractImplementationArtifact>();
		for (AbstractImplementationArtifact ia : ias) {
			if (ia.getInterfaceName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
				iasForIfaces.add(ia);
			}
		}
		return iasForIfaces;
	}
	
	public static List<AbstractImplementationArtifact> getIAsForOperations(List<AbstractImplementationArtifact> ias) {
		List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<AbstractImplementationArtifact>();
		for (AbstractImplementationArtifact ia : ias) {
			if ((ia.getOperationName() != null) && !ia.getOperationName().equals("")) {
				iasForIfaces.add(ia);
			}
		}
		return iasForIfaces;
	}
	
	/**
	 * Checks whether there is some VM along the path from the given
	 * nodeTemplate to the sinks of the Topology
	 *
	 * @param nodeTemplate an AbstractNodeTemplate
	 * @return true iff there exists a path from the given NodeTemplate to a VM
	 *         Template in direction to the sinks
	 */
	public static boolean isConnectedToVM(AbstractNodeTemplate nodeTemplate) {
		List<AbstractNodeTemplate> nodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);
		for (AbstractNodeTemplate node : nodes) {
			if (Utils.checkForTypeInHierarchy(node, Types.vmNodeType) | Utils.checkForTypeInHierarchy(node, Types.ubuntu1310ServerNodeType)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <p>
	 * Searches for a NodeTypeImplementation which satisfies following
	 * condition: <br>
	 * - the nodeTypeImplementation has an IA for each operation of the TOSCA
	 * lifecycle interface. <br>
	 * 
	 * Note that the given nodeTemplate must define the TOSCA Lifecycle
	 * interface so that the condition may be true
	 * </p>
	 * 
	 * @param nodeTemplate a NodeTemplate with a TOSCA Lifecycle interface
	 * @return a NodeTypeImplementation which implements all operations of the
	 *         TOSCA Lifecycle interface of the given NodeTemplate, if not found
	 *         null
	 */
	public static AbstractNodeTypeImplementation selectLifecycleInterfaceNodeTypeImplementation(AbstractNodeTemplate nodeTemplate) {
		if (nodeTemplate == null) {
			return null;
		}
		AbstractNodeType nodeType = nodeTemplate.getType();
		
		AbstractInterface usableIface = null;
		
		// check whether the nodeType contains any TOSCA interface
		for (AbstractInterface iface : nodeType.getInterfaces()) {
			if (iface.getName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
				// check if we have operations to work with, e.g. install,
				// configure and start
				int toscaOperations = 0;
				for (AbstractOperation operation : iface.getOperations()) {
					switch (operation.getName()) {
					case "install":
					case "start":
					case "configure":
						toscaOperations++;
						break;
					default:
						break;
					}
				}
				if (toscaOperations != iface.getOperations().size()) {
					// we just accept pure TOSCA interfaces
					continue;
				} else {
					usableIface = iface;
				}
			}
		}
		
		for (AbstractNodeTypeImplementation nodeImpl : nodeTemplate.getImplementations()) {
			
			// check the IA's with the found interfaces, and if we found an IA
			// we
			// can use for one of the interfaces we'll use that
			List<AbstractImplementationArtifact> iasForInterfaces = Util.getIAsForLifecycleInterface(Util.getIAsForInterfaces(nodeImpl.getImplementationArtifacts()));
			List<AbstractImplementationArtifact> iasForOperations = Util.getIAsForLifecycleInterface(Util.getIAsForOperations(nodeImpl.getImplementationArtifacts()));
			
			// first check if we have an IA for a whole interface
			if (iasForInterfaces.size() == 1) {
				// found an implementation for the lifecycle interface ->
				// nodeTypeImpl will suffice
				return nodeImpl;
			}
			
			if (usableIface != null) {
				// check if operations in the interface are implementated by
				// single
				// ia's
				if (usableIface.getOperations().size() == iasForOperations.size()) {
					// TODO pretty vague check but should suffice
					return nodeImpl;
				}
			} else {
				// if the node doesn't have an interface basically no extra
				// operations will be executed, just upload of da's into the
				// right spots
				return nodeImpl;
			}
			
		}
		return null;
	}
	
	public static List<AbstractDeploymentArtifact> getSQLScriptArtifactDAs(List<AbstractDeploymentArtifact> das) {
		List<AbstractDeploymentArtifact> sqlDAs = new ArrayList<AbstractDeploymentArtifact>();
		
		for (AbstractDeploymentArtifact da : das) {
			if (Util.isSqlScriptArtifact(da)) {
				sqlDAs.add(da);
			}
		}
		
		return sqlDAs;
	}
	
	/**
	 * Removes duplicates from the given list
	 * 
	 * @param das a List of DeploymentArtifacts
	 * @return a possibly empty List of DeploymentArtifacts
	 */
	public static List<AbstractDeploymentArtifact> removeDuplicates(List<AbstractDeploymentArtifact> das) {
		if (das.size() == 0) {
			// list of size 0 has no duplicates
			return das;
		}
		
		AbstractDeploymentArtifact da = das.get(0);
		
		while (Collections.frequency(das, da) > 1 & das.size() > 0) {
			if (das.remove(da)) {
				da = das.get(0);
			}
		}
		
		return das;
	}
	
	/**
	 * <p>
	 * Searches for a NodeTypeImplementation with atleast one DA of type
	 * {http://opentosca.org/types/declarative}SQLScriptArtifact
	 * </p>
	 * 
	 * @param nodeTemplate a NodeTemplate of type MySQLDatabase
	 * @return a NodeTypeImplementation if found, else null
	 */
	public static AbstractNodeTypeImplementation selectSQLFileNodeTypeImplementation(AbstractNodeTemplate nodeTemplate) {
		if (nodeTemplate == null) {
			return null;
		}
		
		for (AbstractNodeTypeImplementation nodeImpl : nodeTemplate.getImplementations()) {
			// check if nodeImpl has SQL DA's
			if (Util.hasSqlScriptArtifact(nodeImpl.getDeploymentArtifacts())) {
				return nodeImpl;
			}
		}
		
		return null;
	}
	
	public static boolean canDeployNodeTemplate(AbstractNodeTemplate nodeTemplate) {
		
		if (Util.selectSQLFileNodeTypeImplementation(nodeTemplate) != null) {
			// the easiest implementation
			return true;
		}
		
		if (Util.selectLifecycleInterfaceNodeTypeImplementation(nodeTemplate) != null) {
			// "standard" lifecycle interface handling (deploy da's, deploy
			// ia's, execute ia/op's,..)
			return true;
		}
		
		// check nodeTemplate itself, you can attach DA's to nodeTemplates
		if (Util.hasSqlScriptArtifact(nodeTemplate.getDeploymentArtifacts())) {
			// this means we can use an SQL file to deploy the sql schema
			return true;
		}
		
		return false;
	}
	
}
