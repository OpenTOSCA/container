package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core;

import javax.xml.namespace.QName;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public final class ConnectsToTypePluginConstants {

    // name of the plugin
    // the relationshipType this plugin can handle
    public static final QName MOSQUITTO_CONNECTSTO_RELATIONSHIPTYPE =
        new QName("http://opentosca.org/relationshiptypes", "MosquittoConnectsTo");

    // the target nodes of the relationshiptTypes must be a stack of topic and
    // mosquitto
    public static final QName TOPIC_NODETYPE = new QName("http://opentosca.org/nodetypes", "Topic");
    public static final QName MOSQUITTO_NODETYPE = new QName("http://opentosca.org/nodetypes", "Mosquitto_3.1");
}
