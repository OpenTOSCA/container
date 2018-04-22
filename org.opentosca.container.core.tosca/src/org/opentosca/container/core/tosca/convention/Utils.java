package org.opentosca.container.core.tosca.convention;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

public class Utils {

    private static final List<String> ipPropertyNames;
    private static final List<String> instanceIdPropertyNames;
    private static final List<String> loginNamePropertyNames;
    private static final List<String> loginPasswordPropertyNames;
    private static final List<String> serverlessFunctionNamePropertyNames;
    private static final List<String> serverlessFunctionRuntimePropertyNames;
    private static final List<String> serverlessFunctionUrlPropertyNames;
    private static final List<String> EventNamePropertyNames;
    private static final List<String> httpEventCreateHTTPEventPropertyNames;
    private static final List<String> httpEventHTTPMethodPropertyNames;
    private static final List<String> httpEventAPIIDPropertyNames;
    private static final List<String> httpEventResourceIDPropertyNames;
    private static final List<String> httpEventFunctionURIPropertyNames;
    private static final List<String> httpEventAUTHTYPEPropertyNames;
    private static final List<String> timerEventCRONPropertyNames;
    private static final List<String> databaseEventdatabaseNamePropertyNames;
    private static final List<String> databaseEventdatabaseUsernamePropertyNames;
    private static final List<String> databaseEventdatabasePasswordPropertyNames;
    private static final List<String> databaseEventdatabaseHostUrlPropertyNames;
    private static final List<String> databaseEventTypeOfChangePropertyNames;
    private static final List<String> databaseEventStartPosPropertyNames;
    private static final List<String> blobstorageEventBucketNamePropertyNames;
    private static final List<String> blobstorageEventEventTypePropertyNames;
    private static final List<String> pubsubEventTopicNamePropertyNames;
    private static final List<String> pubsubEventMessageHubInstanceNamePropertyNames;

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
	serverlessFunctionNamePropertyNames = new ArrayList<>();
	Utils.serverlessFunctionNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONNAME);
	serverlessFunctionRuntimePropertyNames = new ArrayList<>();
	Utils.serverlessFunctionRuntimePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RUNTIME);
	serverlessFunctionUrlPropertyNames = new ArrayList<>();
	Utils.serverlessFunctionUrlPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURL);
	httpEventCreateHTTPEventPropertyNames = new ArrayList<>();
	Utils.httpEventCreateHTTPEventPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CREATEHTTPEVENT);
	EventNamePropertyNames = new ArrayList<>();
	Utils.EventNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME);
	httpEventHTTPMethodPropertyNames = new ArrayList<>();
	Utils.httpEventHTTPMethodPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_HTTPMETHOD);
	httpEventAPIIDPropertyNames = new ArrayList<>();
	Utils.httpEventAPIIDPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_APIID);
	httpEventResourceIDPropertyNames = new ArrayList<>();
	Utils.httpEventResourceIDPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RESOURCEID);
	httpEventFunctionURIPropertyNames = new ArrayList<>();
	Utils.httpEventFunctionURIPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURI);
	httpEventAUTHTYPEPropertyNames = new ArrayList<>();
	Utils.httpEventAUTHTYPEPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_AUTHTYPE);
	timerEventCRONPropertyNames = new ArrayList<>();
	Utils.timerEventCRONPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CRON);
	databaseEventdatabaseNamePropertyNames = new ArrayList<>();
	Utils.databaseEventdatabaseNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASENAME);
	databaseEventTypeOfChangePropertyNames = new ArrayList<>();
	Utils.databaseEventTypeOfChangePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TYPEOFCHANGE);
	databaseEventdatabaseHostUrlPropertyNames = new ArrayList<>();
	Utils.databaseEventdatabaseHostUrlPropertyNames
		.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEHOSTURL);
	databaseEventdatabaseUsernamePropertyNames = new ArrayList<>();
	Utils.databaseEventdatabaseUsernamePropertyNames
		.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEUSER);
	databaseEventdatabasePasswordPropertyNames = new ArrayList<>();
	Utils.databaseEventdatabasePasswordPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEPW);
	databaseEventStartPosPropertyNames = new ArrayList<>();
	Utils.databaseEventStartPosPropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_STARTPOS);
	blobstorageEventBucketNamePropertyNames = new ArrayList<>();
	Utils.blobstorageEventBucketNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_BUCKETNAME);
	blobstorageEventEventTypePropertyNames = new ArrayList<>();
	Utils.blobstorageEventEventTypePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTTYPE);
	pubsubEventTopicNamePropertyNames = new ArrayList<>();
	Utils.pubsubEventTopicNamePropertyNames.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TOPIC);
	pubsubEventMessageHubInstanceNamePropertyNames = new ArrayList<>();
	Utils.pubsubEventMessageHubInstanceNamePropertyNames
		.add(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_MESSAGEHUBINSTANCE);

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

    public static List<String> getSupportedServerlessFunctionNamePropertyNames() {
	return Utils.serverlessFunctionNamePropertyNames;
    }

    public static List<String> getSupportedServerlessFunctionRuntimePropertyNames() {
	return Utils.serverlessFunctionRuntimePropertyNames;
    }

    public static List<String> getSupportedServerlessFunctionUrlPropertyNames() {
	return Utils.serverlessFunctionUrlPropertyNames;
    }

    public static List<String> getSupportedEventNamePropertyNames() {
	return Utils.EventNamePropertyNames;
    }

    public static List<String> getSupportedCRONPropertyNames() {
	return Utils.timerEventCRONPropertyNames;
    }

    public static List<String> getSupportedhttpEventAPIIDPropertyNames() {
	return Utils.httpEventAPIIDPropertyNames;
    }

    public static List<String> getSupportedhttpEventAuthTypePropertyNames() {
	return Utils.httpEventAUTHTYPEPropertyNames;
    }

    public static List<String> getSupportedhttpEventCreateHTTPEventPropertyNames() {
	return Utils.httpEventCreateHTTPEventPropertyNames;
    }

    public static List<String> getSupportedhttpEventFunctionURIPropertyNames() {
	return Utils.httpEventFunctionURIPropertyNames;
    }

    public static List<String> getSupportedhttpEventHttpMethodPropertyNames() {
	return Utils.httpEventHTTPMethodPropertyNames;
    }

    public static List<String> getSupportedhttpEventResourceIDPropertyNames() {
	return Utils.httpEventResourceIDPropertyNames;
    }

    public static List<String> getSupporteddatabaseEventdatabaseNamePropertyNames() {
	return Utils.databaseEventdatabaseNamePropertyNames;
    }

    public static List<String> getSupporteddatabaseEventdatabaseHostUrlPropertyNames() {
	return Utils.databaseEventdatabaseHostUrlPropertyNames;
    }

    public static List<String> getSupporteddatabaseEventdatabaseUsernamePropertyNames() {
	return Utils.databaseEventdatabaseUsernamePropertyNames;
    }

    public static List<String> getSupporteddatabaseEventdatabasePasswordPropertyNames() {
	return Utils.databaseEventdatabasePasswordPropertyNames;
    }

    public static List<String> getSupporteddatabaseEventStartPosPropertyNames() {
	return Utils.databaseEventStartPosPropertyNames;
    }

    public static List<String> getSupporteddatabaeEventTypeOfChangePropertyNames() {
	return Utils.databaseEventTypeOfChangePropertyNames;
    }

    public static List<String> getSupportedblobstorageEventBucketNamePropertyNames() {
	return Utils.blobstorageEventBucketNamePropertyNames;
    }

    public static List<String> getSupportedblobstorageEventEventTypePropertyNames() {
	return Utils.blobstorageEventEventTypePropertyNames;
    }

    public static List<String> getSupportedpubsubEventTopicNamePropertyNames() {
	return Utils.pubsubEventTopicNamePropertyNames;
    }

    public static List<String> getSupportedpubsubEventMessageHubInstanceNamePropertyNames() {
	return Utils.pubsubEventMessageHubInstanceNamePropertyNames;
    }

    /**
     * Checks whether the given property name represents a special case property:
     * the ip property of an virtual machine
     *
     * @param name
     *            a String containing some property name
     * @return true iff the given name equals to the predefined IP property names of
     *         the plugins
     */
    public static boolean isSupportedVirtualMachineIPProperty(final String name) {
	if (name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)) {
	    return true;
	}
	return false;
    }

    /**
     * Checks whether the given property name represents a special case property:
     * the property of a serverless function or event
     *
     * @param name
     *            a String containing some property name
     * @return true iff the given name equals to the predefined serverless function
     *         or event property names of the plugins
     */
    public static boolean isSupportedServerlessProperty(final String name) {
	if (name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONNAME)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RUNTIME)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CRON)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASENAME)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEHOSTURL)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TYPEOFCHANGE)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEUSER)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEPW)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_STARTPOS)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_BUCKETNAME)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTTYPE)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TOPIC)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_MESSAGEHUBINSTANCE)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CREATEHTTPEVENT)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_HTTPMETHOD)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_APIID)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RESOURCEID)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURI)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_AUTHTYPE)) {
	    return true;
	}
	return false;
    }

    /**
     * Checks whether the given property name represents a speciial case property:
     * the instanceId property of an virtual machine
     *
     * @param name
     *            a String containing some property name
     * @return true if the given name equals to the predefined InstanceId property
     *         names of the plugins
     */
    public static boolean isSupportedVirtualMachineInstanceIdProperty(final String name) {
	if (name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID)
		| name.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID)) {
	    return true;
	}
	return false;
    }

    /**
     * Checks whether the given NodeType is a cloud provider nodeType that can be
     * handled by the set of plugins used by the PlanBuilder.
     *
     * @param nodeType
     *            a QName denoting some nodeType
     * @return a boolean. True if the given nodeType is a cloud provider nodeType
     */
    public static boolean isSupportedCloudProviderNodeType(final QName nodeType) {

	if (nodeType.equals(Types.amazonEc2NodeType) | nodeType.equals(Types.openStackNodeType)
		| nodeType.equals(Types.openStackLiberty12NodeType) | nodeType.equals(Types.vmWareVsphere55NodeType)
		| nodeType.equals(Types.localHypervisor)) {
	    return true;
	}

	return false;
    }

    /**
     * Checks whether the given QName denotes a nodeType which can't/won't be
     * managed by OpenTOSCA, such as remote entites
     *
     * @param nodeType
     *            a QName denoting a NodeType
     * @return true if the nodeType is supported
     */
    public static boolean isSupportedUnmanagedNodeType(final QName nodeType) {

	if (nodeType.equals(Types.remoteHostNodeType)) {
	    return true;
	}

	return false;
    }

    /**
     * Checks whether the given Node is an infrastructure nodeType that can be
     * handled by the set of plugins used by the PlanBuilder.
     *
     * @param nodeType
     *            a QName denoting some nodeType
     * @return a boolean. True if the given nodeType is an infrastructure nodeType
     */
    public static boolean isSupportedInfrastructureNodeType(final QName nodeType) {

	if (nodeType.equals(Types.ubuntuNodeType)) {
	    return true;
	}

	final String nodeTypeNS = nodeType.getNamespaceURI();
	final String nodeTypeLN = nodeType.getLocalPart();

	if (nodeTypeNS.equals("http://opentosca.org/types/declarative") && Utils.isProperUbuntuLocalName(nodeTypeLN)) {
	    return true;
	}

	// code for new namespace http://opentosca.org/NodeTypes
	if (nodeType.equals(Types.ubuntu1404ServerVmNodeType) || nodeType.equals(Types.ubuntu1404ServerVmNodeType2)) {
	    return true;
	}

	// code for serverless node types
	if (nodeType.equals(Types.managementInfrastructureNodeType)) {
	    return true;
	}

	// code for raspbian and stuff
	if (nodeType.equals(Types.raspbianJessieOSNodeType)) {
	    return true;
	}

	// code for docker
	if (nodeType.equals(Types.dockerContainerNodeType)) {
	    return true;
	}

	if (nodeType.equals(Types.dockerEngineNodeType)) {
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
     * Checks whether the given Node is a virtual machine nodeType that can by
     * handled by the set of plugins used by the PlanBuilder
     *
     * @param nodeType
     *            a QName denoting some nodeType
     * @return a boolean. True if given nodeType is a virtual machine nodeType
     */
    public static boolean isSupportedVMNodeType(final QName nodeType) {
	boolean check = Utils.isSupportedInfrastructureNodeType(nodeType);

	// code for raspbian and stuff
	if (nodeType.equals(Types.raspbianJessieOSNodeType)) {
	    check = false;
	}
	return check;
    }

    public static boolean isSupportedServerlessFunctionNodeType(final QName nodeType) {
	if (nodeType.equals(Types.serverlessFunctionNodeType)) {
	    return true;
	}
	return false;
    }

    public static boolean isSupportedServerlessEventNodeType(final QName nodeType) {
	if (nodeType.equals(Types.blobstorageEventNodeType) | nodeType.equals(Types.databaseEventNodeType)
		| nodeType.equals(Types.httpEventNodeType) | nodeType.equals(Types.pubsubEventNodeType)
		| nodeType.equals(Types.timerEventNodeType)) {
	    return true;
	}
	return false;
    }

    public static boolean isSupportedServerlessPlatformNodeType(final QName nodeType) {
	if (nodeType.equals(Types.openWhiskNodeType) | nodeType.equals(Types.managementInfrastructureNodeType)) {
	    return true;
	}
	return false;
    }

    /**
     * Checks whether the given NodeType is a docker engine nodeType that can be
     * handled by the set of plugins used by the PlanBuilder.
     *
     * @param nodeType
     *            a QName denoting some nodeType
     * @return a boolean. True if the given nodeType is a docker engine nodeType
     */
    public static boolean isSupportedDockerEngineNodeType(final QName nodeType) {
	if (nodeType.equals(Types.dockerEngineNodeType) | nodeType.equals(Types.dockerEngineNodeTypeAlt)) {
	    return true;
	}

	return false;
    }

}
