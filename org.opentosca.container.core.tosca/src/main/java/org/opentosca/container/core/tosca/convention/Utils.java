package org.opentosca.container.core.tosca.convention;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

public class Utils {

    private static final List<String> ipPropertyNames;
    private static final List<String> instanceIdPropertyNames;
    private static final List<String> loginNamePropertyNames;
    private static final List<String> loginPasswordPropertyNames;

    static {
        ipPropertyNames = new ArrayList<>();
        Utils.ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
        Utils.ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP);
        Utils.ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP);
        Utils.ipPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CONTAINERIP);
        instanceIdPropertyNames = new ArrayList<>();
        Utils.instanceIdPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID);
        Utils.instanceIdPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID);
        loginNamePropertyNames = new ArrayList<>();
        Utils.loginNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SSHUSER);
        Utils.loginNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
        Utils.loginNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANUSER);
        loginPasswordPropertyNames = new ArrayList<>();
        Utils.loginPasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SSHPRIVATEKEY);
        Utils.loginPasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
        Utils.loginPasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANPASSWD);
    }

    public static List<String> getSupportedVirtualMachineIPPropertyNames() {
        return Utils.ipPropertyNames;
    }

    public static List<String> getSupportedVirtualMachineInstanceIdPropertyNames() {
        return Utils.instanceIdPropertyNames;
    }

    public static List<String> getSupportedVirtualMachineLoginUserNamePropertyNames() {
        return Utils.loginNamePropertyNames;
    }

    public static List<String> getSupportedVirtualMachineLoginPasswordPropertyNames() {
        return Utils.loginPasswordPropertyNames;
    }

    public static boolean isSupportedSSHUserPropery(final String name) {
        return getSupportedVirtualMachineLoginUserNamePropertyNames().contains(name);
    }

    public static boolean isSupportedSSHKeyProperty(final String name) {
        return getSupportedVirtualMachineLoginPasswordPropertyNames().contains(name);
    }

    /**
     * Checks whether the given property name represents a special case property: the ip property of an virtual machine
     *
     * @param name a String containing some property name
     * @return true iff the given name equals to the predefined IP property names of the plugins
     */
    public static boolean isSupportedVirtualMachineIPProperty(final String name) {
        return name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP)
            || name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)
            || name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CONTAINERIP)
            || name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP);
    }

    /**
     * Checks whether the given NodeType is a cloud provider nodeType that can be handled by the set of plugins used by
     * the PlanBuilder.
     *
     * @param nodeType a QName denoting some nodeType
     * @return a boolean. True if the given nodeType is a cloud provider nodeType
     */
    public static boolean isSupportedCloudProviderNodeType(final QName nodeType) {
        return nodeType.equals(Types.amazonEc2NodeType)
            || nodeType.equals(Types.openStackNodeType)
            || nodeType.equals(Types.openStackLiberty12NodeType)
            || nodeType.equals(Types.vmWareVsphere55NodeType)
            || nodeType.equals(Types.localHypervisor)
            || nodeType.equals(Types.KVM_QEMU_HYPERVISOR_TYPE)
            || nodeType.equals(Types.openStackTrainNodeType)
            || nodeType.getNamespaceURI().equals(Types.openStackLiberty12NodeTypeGenerated.getNamespaceURI())
            && (nodeType.getLocalPart().startsWith(Types.openStackLiberty12NodeTypeGenerated.getLocalPart())
            || nodeType.getLocalPart().startsWith(Types.openStackTrainNodeType.getLocalPart()));
    }

    /**
     * Checks whether the given Node is an infrastructure nodeType that can be handled by the set of plugins used by the
     * PlanBuilder.
     *
     * @param nodeType a QName denoting some nodeType
     * @return a boolean. True if the given nodeType is an infrastructure nodeType
     */
    public static boolean isSupportedInfrastructureNodeType(final QName nodeType) {

        if (nodeType.equals(Types.abstractOperatingSystemNodeType)) {
            return true;
        }

        if (nodeType.equals(Types.ubuntuNodeType)) {
            return true;
        }

        final String nodeTypeNS = nodeType.getNamespaceURI();
        final String nodeTypeLN = nodeType.getLocalPart();

        if (nodeTypeNS.equals("http://opentosca.org/types/declarative") && Utils.isProperUbuntuLocalName(nodeTypeLN)) {
            return true;
        }

        // code for new namespace http://opentosca.org/NodeTypes
        if (isSupportedVMNodeType(nodeType)) {
            return true;
        }

        // code for docker
        if (nodeType.equals(Types.dockerContainerNodeType)) {
            return true;
        }

        if (nodeType.equals(Types.dockerEngineNodeType)) {
            return true;
        }

        if (nodeType.equals(Types.KVM_QEMU_VM_TYPE)) {
            return true;
        }

        if (nodeType.equals(Types.raspbianJessieOSNodeType)) {
            return true;
        }

        return false;
    }

    private static boolean isProperUbuntuLocalName(final String localName) {
        // new QName("http://opentosca.org/types/declarative",
        // "Ubuntu-13.10-Server");

        final String[] dotSplit = localName.split("\\.");

        if (dotSplit.length != 2) {
            return false;
        }

        final String[] leftDashSplit = dotSplit[0].split("\\-");
        final String[] rightDashSplit = dotSplit[1].split("\\-");

        if (leftDashSplit.length != 2 && rightDashSplit.length != 2) {
            return false;
        }

        if (!leftDashSplit[0].equals("Ubuntu")) {
            return false;
        }

        try {
            Integer.parseInt(leftDashSplit[1]);
        } catch (final NumberFormatException e) {
            return false;
        }

        if (!rightDashSplit[1].equals("Server")) {
            return false;
        }

        try {
            Integer.parseInt(rightDashSplit[0]);
        } catch (final NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the given Node is a virtual machine nodeType that can by handled by the set of plugins used by the
     * PlanBuilder
     *
     * @param nodeType a QName denoting some nodeType
     * @return a boolean. True if given nodeType is a virtual machine nodeType
     */
    public static boolean isSupportedVMNodeType(final QName nodeType) {
        return nodeType.equals(Types.ubuntu1404ServerVmNodeType) || nodeType.equals(Types.ubuntu1404ServerVmNodeType2)
            || nodeType.equals(Types.ubuntu1404ServerVmNodeType3) || nodeType.equals(Types.ubuntu1604ServerVmNodeType)
            || nodeType.equals(Types.ubuntu1804ServerVmNodeType)
            || nodeType.getNamespaceURI().equals(Types.ubuntu1804ServerVmNodeTypeGenerated.getNamespaceURI())
            && nodeType.getLocalPart().startsWith(Types.ubuntu1804ServerVmNodeTypeGenerated.getLocalPart());
    }

    public static boolean isSupportedOSNodeType(final QName nodeType) {
        return nodeType.equals(Types.raspbianJessieOSNodeType);
    }

    public static boolean isSupportedDeviceNodeType(final QName nodeType) {
        return nodeType.equals(Types.raspberryPi3) | nodeType.equals(Types.mCore) | nodeType.equals(Types.mBot);
    }

    public static boolean isSupportedHardwareNodeType(final QName nodeType) {
        return nodeType.equals(Types.fs20Adapater) | nodeType.equals(Types.lineFollowerSensor) | nodeType.equals(Types.motor) | nodeType.equals(Types.ultrasonicSensor);
    }

    /**
     * Checks whether the given NodeType is a docker engine nodeType that can be handled by the set of plugins used by
     * the PlanBuilder.
     *
     * @param nodeType a QName denoting some nodeType
     * @return a boolean. True if the given nodeType is a docker engine nodeType
     */
    public static boolean isSupportedDockerEngineNodeType(final QName nodeType) {
        return nodeType.equals(Types.dockerEngineNodeType) || nodeType.equals(Types.dockerEngineNodeTypeAlt);
    }
}
