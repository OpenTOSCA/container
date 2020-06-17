package org.opentosca.container.core.tosca.convention;

import javax.xml.namespace.QName;

public class Types {

    // TODO refactor this class into some kind of configuration file to make it much more flexible ?

    // TODO Remove the old stuff
    // cloud provider nodeTypes (old)
    public final static QName ec2NodeType = new QName("http://opentosca.org/types/declarative", "EC2");
    public final static QName openStackNodeType = new QName("http://opentosca.org/types/declarative", "OpenStack");

    // virtual machine nodeTypes (old)
    public final static QName vmNodeType = new QName("http://opentosca.org/types/declarative", "VM");
    public final static QName ubuntuNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu");
    public final static QName ubuntu1310ServerNodeType =
        new QName("http://opentosca.org/types/declarative", "Ubuntu-13.10-Server");
    public final static QName ubuntu1310ServerVmNodeType =
        new QName("http://opentosca.org/types/declarative", "Ubuntu-13.10-Server-VM");

    // cloud provider nodeTypes
    public final static QName openStackLiberty12NodeType =
        new QName("http://opentosca.org/nodetypes", "OpenStack-Liberty-12");
    public final static QName openStackTrainNodeType =
        new QName("http://opentosca.org/nodetypes", "OpenStack_Train-w1");
    public final static QName vmWareVsphere55NodeType = new QName("http://opentosca.org/nodetypes", "VSphere_5.5");

    public final static QName amazonEc2NodeType = new QName("http://opentosca.org/NodeTypes", "AmazonEC2");

    // docker nodeTypes
    public final static QName dockerEngineNodeType = new QName("http://opentosca.org/nodetypes", "DockerEngine");

    public final static QName dockerContainerNodeType = new QName("http://opentosca.org/nodetypes", "DockerContainer");

    // docker nodeTypes (old)
    public final static QName dockerEngineNodeTypeAlt = new QName("http://opentosca.org/NodeTypes", "DockerEngine");

    // abstract operating system node type
    public final static QName abstractOperatingSystemNodeType =
        new QName("http://opentosca.org/nodetypes", "OperatingSystem");

    // virtual machine nodeTypes
    public final static QName ubuntu1404ServerVmNodeType =
        new QName("http://opentosca.org/nodetypes", "Ubuntu-14.04-VM");

    public final static QName ubuntu1404ServerVmNodeType2 =
        new QName("http://opentosca.org/NodeTypes", "Ubuntu-14.04-VM");

    public final static QName ubuntu1404ServerVmNodeType3 =
        new QName("http://opentosca.org/nodetypes", "Ubuntu-VM_14.04-w1");

    public final static QName ubuntu1604ServerVmNodeType =
        new QName("http://opentosca.org/nodetypes", "Ubuntu-VM_16.04-w1");

    public final static QName ubuntu1804ServerVmNodeType =
        new QName("http://opentosca.org/nodetypes", "Ubuntu-VM_18.04-w1");

    // FIXME: find a better way to support generated NodeTypes
    public final static QName ubuntu1804ServerVmNodeTypeGenerated =
        new QName("http://opentosca.org/nodetypes/generated", "Ubuntu-VM_18.04");
    public final static QName openStackLiberty12NodeTypeGenerated =
        new QName("http://opentosca.org/nodetypes/generated", "OpenStack-Liberty-12");

    // OS
    // raspbian OS nodeType
    public final static QName raspbianJessieOSNodeType = new QName("http://opentosca.org/nodetypes", "RaspbianJessie");

    // remote host nodeType for nodes not managed by opentosca
    public final static QName remoteHostNodeType = new QName("http://opentosca.org/nodetypes", "RemoteHost");

    // local linux hypervisor nodeType
    public final static QName localHypervisor = new QName("http://opentosca.org/nodetypes", "Libvirt-Qemu-KVM");
    // relationship types
    public final static QName connectsToRelationType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ConnectsTo");
    public final static QName dependsOnRelationType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DependsOn");
    public final static QName deployedOnRelationType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DeployedOn");
    public final static QName hostedOnRelationType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "HostedOn");

    public final static QName KVM_QEMU_VM_TYPE = new QName("http://opentosca.org/nodetypes", "KVM_QEMU_VM");
    public final static QName KVM_QEMU_HYPERVISOR_TYPE =
        new QName("http://opentosca.org/nodetypes", "KVM_QEMU_Hypervisor");

    public static final QName TOSCABASETYPE_SERVER =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "Server");

    public static final QName TOSCABASETYPE_OS =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "OperatingSystem");

    // ArtifactTypes
    public static final QName scriptArtifactType = new QName(
        "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ScriptArtifact");

    // hardware, such as sensors, actuators and devices
    public static final QName raspberryPi3 = new QName("http://opentosca.org/nodetypes", "RaspberryPI3");
    public static final QName fs20Adapater = new QName("http://opentosca.org/nodetypes", "FS20_USBDongle");
    public static final QName lineFollowerSensor = new QName("http://opentosca.org/nodetypes","LineFollowerSensor_1-w1-wip1");
    public static final QName mCore = new QName("http://opentosca.org/nodetypes","mCore_1-w1-wip1");
    public static final QName mBot = new QName("http://opentosca.org/nodetypes","mBot_1-w1-wip1");
    public static final QName ultrasonicSensor = new QName("http://opentosca.org/nodetypes","UltrasonicSensor_1-w1-wip1");
    public static final QName motor = new QName("http://opentosca.org/nodetypes","Motor_1-w1-wip1");

    // Policy Types
    public static final QName situationPolicyType = new QName("http://opentosca.org/servicetemplates/policytypes", "SituationPolicy_w1-wip1");
}
