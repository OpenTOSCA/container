package org.opentosca.model.tosca.conventions;

import javax.xml.namespace.QName;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Types {

	// TODO Remove the old stuff
	// cloud provider nodeTypes (old)
	public final static QName ec2NodeType = new QName("http://opentosca.org/types/declarative", "EC2");
	public final static QName openStackNodeType = new QName("http://opentosca.org/types/declarative", "OpenStack");

	// virtual machine nodeTypes (old)
	public final static QName vmNodeType = new QName("http://opentosca.org/types/declarative", "VM");
	public final static QName ubuntuNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu");
	public final static QName ubuntu1310ServerNodeType = new QName("http://opentosca.org/types/declarative",
			"Ubuntu-13.10-Server");
	public final static QName ubuntu1310ServerVmNodeType = new QName("http://opentosca.org/types/declarative",
			"Ubuntu-13.10-Server-VM");

	// cloud provider nodeTypes
	public final static QName openStackLiberty12NodeType = new QName("http://opentosca.org/nodetypes",
			"OpenStack-Liberty-12");
	public final static QName vmWareVsphere55NodeType = new QName("http://opentosca.org/nodetypes","VSphere_5.5");

	// virtual machine nodeTypes
	public final static QName ubuntu1404ServerVmNodeType = new QName("http://opentosca.org/nodetypes",
			"Ubuntu-14.04-VM");

	// raspbian nodeTypes
	public final static QName raspbianJessieOSNodeType = new QName("http://opentosca.org/nodetypes", "RaspbianJessie");
	
	// relationship types
	public final static QName connectsToRelationType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ConnectsTo");
	public final static QName dependsOnRelationType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DependsOn");
	public final static QName deployedOnRelationType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DeployedOn");
	public final static QName hostedOnRelationType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "HostedOn");
}
