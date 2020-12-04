package org.opentosca.bus.management.header;

/**
 * Enum needed for the MB-components.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This enum defines the headers of the camel exchange message that is used from all MB-components.<br>
 * <br>
 * <p>
 * All header fields must end with their type after the last underscore of the name, otherwise they can not be used by
 * the collaboration classes. This is because the header fields have to be transformed to String to be transmitted over
 * MQTT and the information is needed to recreate the corresponding type afterwards.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 */
public enum MBHeader {

    // ***** GENERAL MB HEADER FIELDS ***** //

    /**
     * <b>TRIGGERINGCONTAINER_STRING</b> This header field contains the host name of the OpenTOSCA
     * Container where the invoke request in this exchange was initiated. It makes all other identifying header fields
     * unique if multiple OpenTOSCA Containers interact.
     */
    TRIGGERINGCONTAINER_STRING,

    /**
     * <b>CSARID</b> This header field contains an identifier for a CSAR file in OpenTOSCA (see
     * {@link org.opentosca.container.core.model.csar.CsarId}).
     */
    CSARID,

    SERVICEINSTANCEID_URI,

    NODEINSTANCEID_STRING,

    SERVICETEMPLATEID_QNAME,

    NODETEMPLATEID_STRING,

    /**
     * <b>TYPEIMPLEMENTATIONID_QNAME</b> This header field contains an identifier for a
     * NodeTypeImplementation or a RelationshipTypeImplementation.
     */
    TYPEIMPLEMENTATIONID_QNAME,

    RELATIONSHIPTEMPLATEID_STRING,

    /**
     * <b>OPERATIONNAME_STRING</b> This header field specifies the interface which contains the
     * operation that shall be executed by passing the camel exchange message to an invocation plug-in.
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
     * <b>PLANCORRELATIONID_STRING</b> This header field contains the correlation ID which uniquely
     * identifies a PlanInstance.
     */
    PLANCORRELATIONID_STRING,

    /**
     * <b>PLANCHORCORRELATIONID_STRING</b> This header field contains the correlation ID which uniquely
     * identifies a choreographed planinstance.
     */
    PLANCHORCORRELATIONID_STRING,

    /**
     * <b>ENDPOINT_URI</b> This header field contains the endpoint of an Implementation Artifact or
     * a Plan.
     */
    ENDPOINT_URI,

    SPECIFICCONTENT_DOCUMENT,

    HASOUTPUTPARAMS_BOOLEAN,

    SYNCINVOCATION_BOOLEAN,

    CALLBACK_BOOLEAN,

    APIID_STRING,

    /**
     * <b>ARTIFACTTEMPLATEID_QNAME</b> This header field contains a QName that identifies an
     * ArtifactTemplate. The ArtifactTemplate is part of the NodeTypeImplementation or RelationshipTypeImplementation
     * that is specified by {@link MBHeader#TYPEIMPLEMENTATIONID_QNAME}.
     */
    ARTIFACTTEMPLATEID_QNAME,

    /**
     * <b>ARTIFACTTYPEID_STRING</b> This header field contains the ArtifactType of the
     * ArtifactTemplate represented by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME}
     */
    ARTIFACTTYPEID_STRING,

    /**
     * <b>PORTTYPE_QNAME</b> This header field contains the PortType of the ArtifactTemplate
     * represented by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME} if one is specified.
     */
    PORT_TYPE_QNAME,

    /**
     * <b>IMPLEMENTATION_ARTIFACT_NAME_STRING</b> This header field contains the name of the
     * Implementation Artifact which shall be invoked by the camel exchange.
     */
    IMPLEMENTATION_ARTIFACT_NAME_STRING,

    /**
     * <b>ARTIFACTREFERENCES_LISTURL</b> This header field contains a list of Strings. Each String
     * represents an ArifactReference that is defined in the TOSCA file of the ArtifactTemplate represented by {@link
     * MBHeader#ARTIFACTTEMPLATEID_QNAME}.
     */
    ARTIFACTREFERENCES_LISTSTRING,

    /**
     * <b>ARTIFACTSERVICEENDPOINT_STRING</b> This header field contains the ServiceEndpoint property
     * of the ArtifactTemplate represented by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME} if it is defined and null
     * otherwise.
     */
    ARTIFACTSERVICEENDPOINT_STRING,

    /**
     * <b>DEPLOYMENT_ARTIFACTS_STRING</b>
     * TODO: What is this header used for? Only referenced once where it is added but never extracted from the headers.
     * Used by some IAs which access the SOAP headers?
     */
    DEPLOYMENT_ARTIFACTS_STRING,

    /**
     * <b>OPERATIONSTATE_BOOLEAN</b> This header field contains the state of an operation or method
     * that is called by passing the camel exchange message to a service or plug-in. It is true if the operation was
     * called successful and false otherwise.
     */
    OPERATIONSTATE_BOOLEAN,

    /**
     * <b>DEPLOYMENTLOCATION_STRING</b> This header field contains the host name of the OpenTOSCA
     * Container where the ArtifactTemplate identified by {@link MBHeader#ARTIFACTTEMPLATEID_QNAME} has to be deployed.
     */
    DEPLOYMENTLOCATION_STRING,

    /**
     * <b>INVOCATIONTYPE_STRING</b> This header field contains the invocation type of the
     * Implementation Artifact identified by {@link MBHeader#IMPLEMENTATION_ARTIFACT_NAME_STRING}.
     */
    INVOCATIONTYPE_STRING,

    // ***** COLLABORATION ORIENTED HEADER FIELDS ***** //

    /**
     * <b>CORRELATIONID_STRING</b> This header field contains a unique ID to identify to which
     * request a response belongs. It is set by the requester and copied to the answer by the responding component.
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
    MQTTTOPIC_STRING,

    /**
     * <b>REPLYTOTOPIC_STRING</b> This header field is only used for request messages. It contains a
     * MQTT topic name which has to be used by receivers of the request as the destination for their replies.
     */
    REPLYTOTOPIC_STRING,

    /**
     * <b>REMOTEOPERATION_STRING</b> This header field contains the name of the operation that shall
     * be executed on a remote OpenTOSCA Container.
     */
    REMOTEOPERATION_STRING,

    /**
     * <b>CHOREOGRAPHY_PARTNERS</b> List of partners taking part in a choreography
     */
    CHOREOGRAPHY_PARTNERS,

    /**
     * <b>APP_CHOREO_ID</b> Correlation ID used to correlate models within a choreography
     */
    APP_CHOREO_ID
}
