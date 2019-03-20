package org.opentosca.planbuilder.type.plugin.connectsto.bpel;

import javax.xml.namespace.QName;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public final class Constants {

  // the relationshipType this plugin can handle
  public static final QName MOSQUITTOC_CONNECTS_TO_RELATIONSHIP_TYPE =
    new QName("http://opentosca.org/relationshiptypes", "MosquittoConnectsTo");

  // the target nodes of the relationshiptTypes must be a stack of topic and
  // mosquitto
  public static final QName TOPIC_NODE_TYPE = new QName("http://opentosca.org/nodetypes", "Topic");
  public static final QName MOSQUITTO_NODE_TYPE = new QName("http://opentosca.org/nodetypes", "Mosquitto_3.1");

}
