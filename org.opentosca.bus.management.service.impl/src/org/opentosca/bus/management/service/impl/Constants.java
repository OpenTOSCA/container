package org.opentosca.bus.management.service.impl;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;

/**
 * This class contains constants which are used by the Management Bus classes.<br>
 * <br>
 *
 * Copyright 2019 IAAS University of Stuttgart
 */
public class Constants {

    // region Collaboration

    /**
     * URL to access the local Moquette MQTT broker
     */
    public final static String LOCAL_MQTT_BROKER =
        "tcp://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_BROKER_MQTT_PORT;

    /**
     * Topic name where the OpenTOSCA Container sends his request to and where it expects requests at
     * the MQTT broker of the 'master' OpenTOSCA Container in case it acts as a 'slave'. This topic name
     * has to be consistent between interacting OpenTOSCA Containers.
     */
    public final static String REQUEST_TOPIC = "opentosca/container/collaboration/request";

    /**
     * Topic name where the OpenTOSCA Container expects responses to his requests. This topic name is
     * set as "reply-to" header field in requests. So, it could also be created dynamically and does not
     * necessarily have to be consistent between different Containers.
     */
    public final static String RESPONSE_TOPIC = "opentosca/container/collaboration/response";

    /**
     * The invocation and deployment type of the invocation/deployment plug-ins that move requests from
     * the local OpenTOSCA Container to a remote one. This type has to be different from all types that
     * are supported by all other invocation and deployment plug-ins.
     */
    public final static String REMOTE_TYPE = "remote";

    /**
     * QName of the location attribute utilized to assign NodeTemplates to partners for choreographies.
     */
    public final static QName LOCATION_ATTRIBUTE =
        QName.valueOf("{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}location");

    /**
     * Operation names of the management bus SOAP API to receive notifications from other management
     * busses.
     */
    public final static String RECEIVE_NOTIFY_PARTNER_OPERATION = "receiveNotifyPartner";
    public final static String RECEIVE_NOTIFY_PARTNERS_OPERATION = "receiveNotifyPartners";

    /**
     * Parameters for the Notify operation of the SOAP API
     */
    public final static String SERVICE_TEMPLATE_NAMESPACE_PARAM = "ServiceTemplateIDNamespaceURI";
    public final static String SERVICE_TEMPLATE_LOCAL_PARAM = "ServiceTemplateIDLocalPart";
    public final static String PLAN_CORRELATION_PARAM = "PlanCorrelationID";
    public final static String CSARID_PARAM = "CsarID";
    public final static String MESSAGE_ID_PARAM = "MessageID";
    public final static String PARAMS_PARAM = "Params";

    /**
     * Parameter containing the template ID of the RelationshipTemplate to which a notify message
     * belongs in a choreography.
     */
    public final static String RELATIONSHIP_TEMPLATE_PARAM = "ConnectingRelationshipTemplate";

    /**
     * Parameter containing the partner ID of the receiving partner for a notification.
     */
    public final static String RECEIVING_PARTNER_PARAM = "ReceivingPartner";

    /**
     * Namespace of the WSDL and XSD of the Management Bus SOAP API.
     */
    public final static String BUS_WSDL_NAMESPACE = "http://siserver.org/schema";

    /**
     * PortType for callbacks on plans.
     */
    public final static QName CALLBACK_PORT_TYPE = QName.valueOf("{http://schemas.xmlsoap.org/wsdl/}CallbackPortType");

    // endregion

    // region General

    /**
     * Start and end tags for properties that have to be replaced by instance data
     */
    public final static String PLACEHOLDER_START = "/PLACEHOLDER_";
    public final static String PLACEHOLDER_END = "_PLACEHOLDER/";

    /**
     * Path to access the process instances in the Camunda BPMN engine
     */
    public final static String PROCESS_INSTANCE_PATH = "/process-instance?processInstanceIds=";

    /**
     * Path to access the output parameters in the Camunda BPMN engine
     */
    public final static String HISTORY_PATH = "/history/variable-instance";

    // endregion
}
