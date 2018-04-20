package org.opentosca.planbuilder.type.plugin.serverless.bpel;

import javax.xml.namespace.QName;

/**
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public final class Constants {

    // the relationshipType this plugin can handle
    public static final QName REVERSE_HOSTED_ON_RELATIONSHIP_TYPE = new QName("http://opentosca.org/relationshiptypes",
	    "HostedOn");
    public static final QName TRIGGERS_RELATIONSHIP_TYPE = new QName("http://opentosca.org/relationshiptypes",
	    "Triggers");
    public static final QName REVERSE_HOSTED_ON_BASE_RELATIONSHIP_TYPE = new QName(
	    "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "HostedOn");

    public static final QName SERVERLESSFUNCTION_NODE_TYPE = new QName("http://opentosca.org/nodetypes",
	    "ServerlessFunction");
    public static final QName HTTPEVENT_NODE_TYPE = new QName("http://opentosca.org/nodetypes", "HTTPEvent");
    public static final QName TIMEREVENT_NODE_TYPE = new QName("http://opentosca.org/nodetypes", "TimerEvent");
    public static final QName DATABASEEVENT_NODE_TYPE = new QName("http://opentosca.org/nodetypes", "DatabaseEvent");
    public static final QName BLOBSTORAGEEVENT_NODE_TYPE = new QName("http://opentosca.org/nodetypes",
	    "BlobstorageEvent");
    public static final QName PUBSUBEVENT_NODE_TYPE = new QName("http://opentosca.org/nodetypes", "PubSubEvent");
}
