/**
 * 
 */
package org.opentosca.model.tosca.conventions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Utils {
	
	private static final List<String> ipPropertyNames;
	private static final List<String> instanceIdPropertyNames;
	private static final List<String> loginNamePropertyNames;
	private static final List<String> loginPasswordPropertyNames;
	
	static {
		ipPropertyNames = new ArrayList<String>();
		ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP);
		ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP);
		instanceIdPropertyNames = new ArrayList<String>();
		instanceIdPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID);
		instanceIdPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID);
		loginNamePropertyNames = new ArrayList<String>();
		loginNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SSHUSER);
		loginNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
		loginNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANUSER);
		loginPasswordPropertyNames = new ArrayList<String>();
		loginPasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SSHPRIVATEKEY);
		loginPasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
		loginPasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANPASSWD);
	}
	
	public static List<String> getSupportedVirtualMachineIPPropertyNames(){
		return ipPropertyNames;
	}
	
	public static List<String> getSupportedVirtualMachineInstanceIdPropertyNames(){
		return instanceIdPropertyNames;
	}
	
	public static List<String> getSupportedVirtualMachineLoginUserNamePropertyNames(){
		return loginNamePropertyNames;
	}
	
	public static List<String> getSupportedVirtualMachineLoginPasswordPropertyNames(){
		return loginPasswordPropertyNames;
	}

	/**
	 * Checks whether the given property name represents a special case
	 * property: the ip property of an virtual machine
	 * 
	 * @param name
	 *            a String containing some property name
	 * @return true iff the given name equals to the predefined IP property
	 *         names of the plugins
	 */
	public static boolean isSupportedVirtualMachineIPProperty(String name) {
		if (name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP)
				| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the given property name represents a speciial case
	 * property: the instanceId property of an virtual machine
	 * 
	 * @param name
	 *            a String containing some property name
	 * @return true iff the given name equals to the predefined InstanceId
	 *         property names of the plugins
	 */
	public static boolean isSupportedVirtualMachineInstanceIdProperty(String name) {
		if (name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID)
				| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID)) {
			return true;
		}
		return false;
	}

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
		if (nodeType.equals(Types.ec2NodeType) | nodeType.equals(Types.openStackNodeType)
				| nodeType.equals(Types.openStackLiberty12NodeType) | nodeType.equals(Types.vmWareVsphere55NodeType)) {
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

		if (nodeType.equals(Types.ubuntuNodeType)) {
			return true;
		}

		String nodeTypeNS = nodeType.getNamespaceURI();
		String nodeTypeLN = nodeType.getLocalPart();

		if (nodeTypeNS.equals("http://opentosca.org/types/declarative")
				&& Utils.isProperUbuntuLocalName(nodeTypeLN)) {
			return true;
		}

		// code for new namespace http://opentosca.org/NodeTypes
		if (nodeType.equals(Types.ubuntu1404ServerVmNodeType)) {
			return true;
		}
		
		// code for raspbian and stuff
		if(nodeType.equals(Types.raspbianJessieOSNodeType)){
			return true;
		}

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
			Integer.parseInt(leftDashSplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}

		if (!rightDashSplit[1].equals("Server")) {
			return false;
		}

		try {
			Integer.parseInt(rightDashSplit[0]);
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
		if (nodeType.equals(Types.vmNodeType)) {
			return true;
		}
		return false;
	}

}
