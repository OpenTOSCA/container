package org.opentosca.container.core.tosca.convention;

/**
 * This class holds the names of the well-known interfaces and their operations.
 */
public class Interfaces {

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER = "CloudProviderInterface";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM = "createVM";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM = "terminateVM";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM = "OperatingSystemInterface";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_WAITFORAVAIL = "waitForAvailability";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT = "runScript";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE = "transferFile";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE = "installPackage";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE = "InterfaceDockerEngine";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER = "startContainer";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER = "removeContainer";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER = "ContainerManagementInterface";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Backup = "http://opentosca.org/interfaces/backup";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT = "runScript";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE = "transferFile";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Freeze = "freeze";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Defreeze = "defreeze";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_UPDATE = "UpdateManagementInterface";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_UPDATE_RUNUPDATE = "update";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_STATE = "http://opentosca.org/interfaces/state";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE = "freeze";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT = "StoreStateServiceEndpoint";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE = "defrost";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_PACKAGENAMES = "PackageNames";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SCRIPT = "Script";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_TARGETABSOLUTPATH =
        "TargetAbsolutePath";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SOURCEURLORLOCALPATH =
        "SourceURLorLocalPath";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE = "http://www.example.com/interfaces/lifecycle";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE2 = "http://opentosca.org/interfaces/lifecycle";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE3 = "http://opentosca.org/interfaces/pattern/lifecycle";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_INSTALL = "install";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_CONFIGURE = "configure";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START = "start";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP = "stop";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_UNINSTALL = "uninstall";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN = "http://opentosca.org/interfaces/pattern/container";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_CREATE = "create";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_TERMINATE = "terminate";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_REMOTEMANAGERPATTERN =
        "http://opentosca.org/interfaces/pattern/remotemanager";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_REMOTEMANAGERPATTERN_INSTALL = "install";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_REMOTEMANAGERPATTERN_UNINSTALL = "uninstall";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_NON_INTERRUPTIVE =
        "http://opentosca.org/interfaces/connections/nonInterruptive";
    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_CONNECTTO = "connectTo";

    public static final String OPENTOSCA_DECLARATIVE_INTERFACE_TEST = "http://opentosca.org/interfaces/test";
}
