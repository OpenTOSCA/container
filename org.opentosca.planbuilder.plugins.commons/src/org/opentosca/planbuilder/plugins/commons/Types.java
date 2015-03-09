package org.opentosca.planbuilder.plugins.commons;

import javax.xml.namespace.QName;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Types {
	
	// cloud provider nodeTypes
	public final static QName ec2NodeType = new QName("http://opentosca.org/types/declarative", "EC2");
	public final static QName openStackNodeType = new QName("http://opentosca.org/types/declarative", "OpenStack");
	
	// virtual machine nodeTypes
	public final static QName vmNodeType = new QName("http://opentosca.org/types/declarative", "VM");
	public final static QName ubuntuNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu");
	public final static QName ubuntu1310ServerNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu-13.10-Server");
	public final static QName ubuntu1310ServerVmNodeType = new QName("http://opentosca.org/types/declarative", "Ubuntu-13.10-Server-VM");
}
