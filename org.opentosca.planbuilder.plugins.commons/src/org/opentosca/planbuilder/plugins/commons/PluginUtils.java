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
	 * @param nodeType
	 *            a QName denoting some nodeType
	 * @return a boolean. True if the given nodeType is a cloud provider
	 *         nodeType
	 */
	public static boolean isSupportedCloudProviderNodeType(QName nodeType) {
		// FIXME TODO although equals() should do a proper check, im pretty sure
		// that OpenJDK 7 (which i must use right now) has a bug
		if (nodeType.toString().equals(Types.ec2NodeType.toString())
				| nodeType.toString()
						.equals(Types.openStackNodeType.toString())) {
			return true;
		}

		return false;
	}

	/**
	 * Checks whether the given Node is an ubuntu nodeType that can be handled
	 * by the set of plugins used by the PlanBuilder.
	 * 
	 * @param nodeType
	 *            a QName denoting some nodeType
	 * @return a boolean. True if the given nodeType is an ubuntu nodeType
	 */
	public static boolean isSupportedUbuntuVMNodeType(QName nodeType) {

		if (nodeType.toString().equals(Types.ubuntuNodeType.toString())) {
			return true;
		}

		// new QName("http://opentosca.org/types/declarative",
		// "Ubuntu-13.10-Server");
		String nodeTypeNS = nodeType.getNamespaceURI();
		String nodeTypeLN = nodeType.getLocalPart();
		
		if(nodeTypeNS.equals("http://opentosca.org/types/declarative") && PluginUtils.isProperUbuntuLocalName(nodeTypeLN)){
			return true;
		}

		// FIXME TODO although equals() should do a proper check, im pretty sure
		// that OpenJDK 7 (which i must use right now) has a bug
//		if (nodeType.toString().equals(
//				Types.ubuntu1310ServerNodeType.toString())
//				| nodeType.toString().equals(Types.ubuntuNodeType.toString())
//				| nodeType.toString().equals(
//						Types.ubuntu1310ServerVmNodeType.toString())) {
//			return true;
//		}

		return false;
	}

	private static boolean isProperUbuntuLocalName(String localName) {
		// new QName("http://opentosca.org/types/declarative",
		// "Ubuntu-13.10-Server");

		String[] dotSplit = localName.split("\\.");

		if (dotSplit.length != 2) {
			return false;
		}

		String[] leftDashSplit = dotSplit[0].split("\\-");
		String[] rightDashSplit = dotSplit[1].split("\\-");

		if (leftDashSplit.length != 2 && rightDashSplit.length != 2) {
			return false;
		}

		if (!leftDashSplit[0].equals("Ubuntu")) {
			return false;
		}

		try {
			int majorVers = Integer.parseInt(leftDashSplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}

		if (!rightDashSplit[1].equals("Server")) {
			return false;
		}

		try {
			int minorVers = Integer.parseInt(rightDashSplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * Checks whether the given Node is a virtual machine nodeType that can by
	 * handled by the set of plugins used by the PlanBuilder
	 * 
	 * @param nodeType
	 *            a QName denoting some nodeType
	 * @return a boolean. True if given nodeType is a virtual machine nodeType
	 */
	public static boolean isSupportedVMNodeType(QName nodeType) {
		if (nodeType.toString().equals(Types.vmNodeType.toString())) {
			return true;
		}
		return false;
	}

}
