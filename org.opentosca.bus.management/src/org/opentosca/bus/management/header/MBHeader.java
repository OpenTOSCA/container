package org.opentosca.bus.management.header;

import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Enum needed for the MB-components.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * This enum defines the headers of the camel exchange message that is used from all MB-components.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public enum MBHeader {

    // ***** GENERAL MB HEADER FIELDS ***** //

    /**
     * <b>TRIGGERINGCONTAINER_STRING</b> This header field contains the host name of the OpenTOSCA
     * Container where the invoke request in this exchange was initiated. It makes all other
     * identifying header fields unique if multiple OpenTOSCA Containers interact.
     */
    TRIGGERINGCONTAINER_STRING,

    /**
     * <b>CSARID</b> This header field contains an identifier for a CSAR file in OpenTOSCA (see
     * {@link CSARID}).
     */
    CSARID,

    SERVICEINSTANCEID_URI,

    NODEINSTANCEID_STRING,

    SERVICETEMPLATEID_QNAME,

    NODETEMPLATEID_STRING,

    /**
     * <b>NODETYPEIMPLEMENTATIONID_QNAME</b> This header field contains an identifier for a
     * NodeTypeImplementation. The NodeTypeImplementation implements the NodeType specified by
     * {@link MBHeader#NODETYPEID_QNAME}.
     */
    NODETYPEIMPLEMENTATIONID_QNAME,

    RELATIONSHIPTEMPLATEID_STRING,

    /**
     * <b>NODETYPEID_QNAME</b> This header field contains the QName of the NodeType which is the
     * type of the NodeTemplate represented by {@link MBHeader#NODETEMPLATEID_STRING}.
     */
    NODETYPEID_QNAME,

    RELATIONSHIPTYPEID_QNAME,

    /**
     * <b>OPERATIONNAME_STRING</b> This header field specifies the interface which contains the
     * operation that shall be executed by passing the camel exchange message to an invocation
     * plug-in.
     */
    INTERFACENAME_STRING,

    /**
     * <b>OPERATIONNAME_STRING</b> This header field specifies the operation name of the operation
     * which shall be executed by passing the camel exchange message to an invocation plug-in.
     */
    OPERATIONNAME_STRING,

    /**
     * <b>PLANID_QNAME</b> This header field specifies the ID of a plan which shall be executed by
     * passing the camel exchange message to an invocation plug-in.
     */
    PLANID_QNAME,

    /**
     * <b>ENDPOINT_URI</b> This header field contains the endpoint of an Implementation Artifact or
     * a Plan.
     */
    ENDPOINT_URI,

    SPECIFICCONTENT_DOCUMENT,

    HASOUTPUTPARAMS_BOOLEAN,

    SYNCINVOCATION_BOOLEAN,

    APIID_STRING,

    /**
     * <b>ARTIFACTTEMPLATEID_QNAME</b> This header field contains a QName that identifies an
     * ArtifactTemplate. The ArtifactTemplate is part of the NodeTypeImplementation that is
     * specified by {@link MBHeader#NODETYPEIMPLEMENTATIONID_QNAME}.
     */
    ARTIFACTTEMPLATEID_QNAME,

    /**
     * <b>ARTIFACTREFERENCES_LIST_URL</b> This header field contains a list of Strings. Each String
     * represents an ArifactReference that is defined in the TOSCA file of the ArtifactTemplate
     * represented by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME}.
     */
    ARTIFACTREFERENCES_LIST_STRING,

    /**
     * <b>ARTIFACTSERVICEENDPOINT_STRING</b> This header field contains the ServiceEndpoint property
     * of the ArtifactTemplate represented by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME} if it is
     * defined and null otherwise.
     */
    ARTIFACTSERVICEENDPOINT_STRING,

    DEPLOYMENT_ARTIFACTS,

    /**
     * <b>OPERATIONSTATE_BOOLEAN</b> This header field contains the state of an operation or method
     * that is called by passing the camel exchange message to a service or plug-in. It is true if
     * the operation was called successful and false otherwise.
     */
    OPERATIONSTATE_BOOLEAN,

    /**
     * <b>DEPLOYMENTLOCATION_STRING</b> This header field contains the host name of the OpenTOSCA
     * Container where the ArtifactTemplate identified by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME}
     * has to be deployed.
     */
    DEPLOYMENTLOCATION_STRING,

    // ***** COLLABORATION ORIENTED HEADER FIELDS ***** //

    /**
     * <b>CORRELATIONID_STRING</b> This header field contains a unique ID to identify to which
     * request a response belongs. It is set by the requester and copied to the answer by the
     * responding component.
     */
    CORRELATIONID_STRING,

    /**
     * <b>MQTTBROKERHOSTNAME_STRING</b> This header field contains the host name of a MQTT broker
     * which is used by the collaboration camel routes to send Exchanges to the correct destination.
     */
    MQTTBROKERHOSTNAME_STRING,

    /**
     * <b>MQTTTOPIC_STRING</b> This header field contains a MQTT topic name which is used by the
     * collaboration camel routes to send Exchanges to the correct destination.
     */
    MQTTTOPIC_STRING
}
