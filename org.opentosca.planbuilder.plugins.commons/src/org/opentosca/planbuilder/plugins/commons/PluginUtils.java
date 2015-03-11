/**
 * 
 */
package org.opentosca.planbuilder.plugins.commons;

import javax.xml.namespace.QName;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class PluginUtils {
	
	/**
	 * Checks whether the given NodeType is a cloud provider nodeType that can
	 * be handled by the set of plugins used by the PlanBuilder.
	 * 
	 * @param nodeType a QName denoting some nodeType
	 * @return a boolean. True if the given nodeType is a cloud provider
	 *         nodeType
	 */
	public static boolean isSupportedCloudProviderNodeType(QName nodeType) {
		// FIXME TODO although equals() should do a proper check, im pretty sure
		// that OpenJDK 7 (which i must use right now) has a bug
		if (nodeType.toString().equals(Types.ec2NodeType.toString()) | nodeType.toString().equals(Types.openStackNodeType.toString())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether the given Node is an ubuntu nodeType that can be handled
	 * by the set of plugins used by the PlanBuilder.
	 * 
	 * @param nodeType a QName denoting some nodeType
	 * @return a boolean. True if the given nodeType is an ubuntu nodeType
	 */
	public static boolean isSupportedUbuntuVMNodeType(QName nodeType) {
		// FIXME TODO although equals() should do a proper check, im pretty sure
		// that OpenJDK 7 (which i must use right now) has a bug
		if (nodeType.toString().equals(Types.ubuntu1310ServerNodeType.toString()) | nodeType.toString().equals(Types.ubuntuNodeType.toString()) | nodeType.toString().equals(Types.ubuntu1310ServerVmNodeType.toString())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether the given Node is a virtual machine nodeType that can by
	 * handled by the set of plugins used by the PlanBuilder
	 * 
	 * @param nodeType a QName denoting some nodeType
	 * @return a boolean. True if given nodeType is a virtual machine nodeType
	 */
	public static boolean isSupportedVMNodeType(QName nodeType) {
		if (nodeType.toString().equals(Types.vmNodeType.toString())) {
			return true;
		}
		return false;
	}
	
}
