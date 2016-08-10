package org.opentosca.planbuilder.type.plugin.connectsto;

import javax.xml.namespace.QName;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public final class Constants {

	// name of the plugin
	public static final String pluginId = "OpenTOSCA PlanBuilder Type Plugin Client connects to Mosquitto Broker";

	// the relationshipType this plugin can handle
	public static final QName mosquittoConnectsToRelationshipType = new QName("http://opentosca.org/relationshiptypes",
			"MosquittoConnectsTo");
	
	// the target nodes of the relationshiptTypes must be a stack of topic and mosquitto
	public static final QName topicNodeType = new QName("http://opentosca.org/nodetypes", "Topic");
	public static final QName mosquittoNodeType = new QName("http://opentosca.org/nodetypes", "Mosquitto_3.1");

}
