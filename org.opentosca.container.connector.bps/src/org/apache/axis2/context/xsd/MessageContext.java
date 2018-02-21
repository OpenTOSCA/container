
/**
 * MessageContext.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.context.xsd;

import org.apache.axis2.addressing.xsd.RelatesTo;

/**
 * MessageContext bean class
 */

public class MessageContext implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = MessageContext Namespace URI =
     * http://context.axis2.apache.org/xsd Namespace Prefix = ns10
     */


    /**
     *
     */
    private static final long serialVersionUID = 8509954566845595027L;

    /**
     * field for FLOW
     */


    protected int localFLOW;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFLOWTracker = false;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getFLOW() {
        return this.localFLOW;
    }



    /**
     * Auto generated setter method
     *
     * @param param FLOW
     */
    public void setFLOW(final int param) {

        // setting primitive attribute tracker to true
        this.localFLOWTracker = param != java.lang.Integer.MIN_VALUE;

        this.localFLOW = param;


    }


    /**
     * field for SOAP11
     */


    protected boolean localSOAP11;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSOAP11Tracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getSOAP11() {
        return this.localSOAP11;
    }



    /**
     * Auto generated setter method
     *
     * @param param SOAP11
     */
    public void setSOAP11(final boolean param) {

        // setting primitive attribute tracker to true
        this.localSOAP11Tracker = true;

        this.localSOAP11 = param;


    }


    /**
     * field for WSAAction
     */


    protected java.lang.String localWSAAction;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localWSAActionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getWSAAction() {
        return this.localWSAAction;
    }



    /**
     * Auto generated setter method
     *
     * @param param WSAAction
     */
    public void setWSAAction(final java.lang.String param) {
        this.localWSAActionTracker = true;

        this.localWSAAction = param;


    }


    /**
     * field for WSAMessageId
     */


    protected java.lang.String localWSAMessageId;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localWSAMessageIdTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getWSAMessageId() {
        return this.localWSAMessageId;
    }



    /**
     * Auto generated setter method
     *
     * @param param WSAMessageId
     */
    public void setWSAMessageId(final java.lang.String param) {
        this.localWSAMessageIdTracker = true;

        this.localWSAMessageId = param;


    }


    /**
     * field for AttachmentMap
     */


    protected org.apache.axiom.attachments.xsd.Attachments localAttachmentMap;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAttachmentMapTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.attachments.xsd.Attachments
     */
    public org.apache.axiom.attachments.xsd.Attachments getAttachmentMap() {
        return this.localAttachmentMap;
    }



    /**
     * Auto generated setter method
     *
     * @param param AttachmentMap
     */
    public void setAttachmentMap(final org.apache.axiom.attachments.xsd.Attachments param) {
        this.localAttachmentMapTracker = true;

        this.localAttachmentMap = param;


    }


    /**
     * field for AxisMessage
     */


    protected org.apache.axis2.description.xsd.AxisMessage localAxisMessage;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisMessageTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.AxisMessage
     */
    public org.apache.axis2.description.xsd.AxisMessage getAxisMessage() {
        return this.localAxisMessage;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisMessage
     */
    public void setAxisMessage(final org.apache.axis2.description.xsd.AxisMessage param) {
        this.localAxisMessageTracker = true;

        this.localAxisMessage = param;


    }


    /**
     * field for AxisOperation
     */


    protected org.apache.axis2.description.xsd.AxisOperation localAxisOperation;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisOperationTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.AxisOperation
     */
    public org.apache.axis2.description.xsd.AxisOperation getAxisOperation() {
        return this.localAxisOperation;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisOperation
     */
    public void setAxisOperation(final org.apache.axis2.description.xsd.AxisOperation param) {
        this.localAxisOperationTracker = true;

        this.localAxisOperation = param;


    }


    /**
     * field for AxisService
     */


    protected org.apache.axis2.description.xsd.AxisService localAxisService;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisServiceTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.AxisService
     */
    public org.apache.axis2.description.xsd.AxisService getAxisService() {
        return this.localAxisService;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisService
     */
    public void setAxisService(final org.apache.axis2.description.xsd.AxisService param) {
        this.localAxisServiceTracker = true;

        this.localAxisService = param;


    }


    /**
     * field for AxisServiceGroup
     */


    protected org.apache.axis2.description.xsd.AxisServiceGroup localAxisServiceGroup;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisServiceGroupTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.AxisServiceGroup
     */
    public org.apache.axis2.description.xsd.AxisServiceGroup getAxisServiceGroup() {
        return this.localAxisServiceGroup;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisServiceGroup
     */
    public void setAxisServiceGroup(final org.apache.axis2.description.xsd.AxisServiceGroup param) {
        this.localAxisServiceGroupTracker = true;

        this.localAxisServiceGroup = param;


    }


    /**
     * field for ConfigurationContext
     */


    protected org.apache.axis2.context.xsd.ConfigurationContext localConfigurationContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localConfigurationContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ConfigurationContext
     */
    public org.apache.axis2.context.xsd.ConfigurationContext getConfigurationContext() {
        return this.localConfigurationContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param ConfigurationContext
     */
    public void setConfigurationContext(final org.apache.axis2.context.xsd.ConfigurationContext param) {
        this.localConfigurationContextTracker = true;

        this.localConfigurationContext = param;


    }


    /**
     * field for CurrentHandlerIndex
     */


    protected int localCurrentHandlerIndex;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localCurrentHandlerIndexTracker = false;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getCurrentHandlerIndex() {
        return this.localCurrentHandlerIndex;
    }



    /**
     * Auto generated setter method
     *
     * @param param CurrentHandlerIndex
     */
    public void setCurrentHandlerIndex(final int param) {

        // setting primitive attribute tracker to true
        this.localCurrentHandlerIndexTracker = param != java.lang.Integer.MIN_VALUE;

        this.localCurrentHandlerIndex = param;


    }


    /**
     * field for CurrentPhaseIndex
     */


    protected int localCurrentPhaseIndex;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localCurrentPhaseIndexTracker = false;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getCurrentPhaseIndex() {
        return this.localCurrentPhaseIndex;
    }



    /**
     * Auto generated setter method
     *
     * @param param CurrentPhaseIndex
     */
    public void setCurrentPhaseIndex(final int param) {

        // setting primitive attribute tracker to true
        this.localCurrentPhaseIndexTracker = param != java.lang.Integer.MIN_VALUE;

        this.localCurrentPhaseIndex = param;


    }


    /**
     * field for DoingMTOM
     */


    protected boolean localDoingMTOM;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDoingMTOMTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getDoingMTOM() {
        return this.localDoingMTOM;
    }



    /**
     * Auto generated setter method
     *
     * @param param DoingMTOM
     */
    public void setDoingMTOM(final boolean param) {

        // setting primitive attribute tracker to true
        this.localDoingMTOMTracker = true;

        this.localDoingMTOM = param;


    }


    /**
     * field for DoingREST
     */


    protected boolean localDoingREST;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDoingRESTTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getDoingREST() {
        return this.localDoingREST;
    }



    /**
     * Auto generated setter method
     *
     * @param param DoingREST
     */
    public void setDoingREST(final boolean param) {

        // setting primitive attribute tracker to true
        this.localDoingRESTTracker = true;

        this.localDoingREST = param;


    }


    /**
     * field for DoingSwA
     */


    protected boolean localDoingSwA;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDoingSwATracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getDoingSwA() {
        return this.localDoingSwA;
    }



    /**
     * Auto generated setter method
     *
     * @param param DoingSwA
     */
    public void setDoingSwA(final boolean param) {

        // setting primitive attribute tracker to true
        this.localDoingSwATracker = true;

        this.localDoingSwA = param;


    }


    /**
     * field for EffectivePolicy
     */


    protected org.apache.neethi.xsd.Policy localEffectivePolicy;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localEffectivePolicyTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.neethi.xsd.Policy
     */
    public org.apache.neethi.xsd.Policy getEffectivePolicy() {
        return this.localEffectivePolicy;
    }



    /**
     * Auto generated setter method
     *
     * @param param EffectivePolicy
     */
    public void setEffectivePolicy(final org.apache.neethi.xsd.Policy param) {
        this.localEffectivePolicyTracker = true;

        this.localEffectivePolicy = param;


    }


    /**
     * field for Envelope
     */


    protected org.apache.axiom.soap.xsd.SOAPEnvelope localEnvelope;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localEnvelopeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.soap.xsd.SOAPEnvelope
     */
    public org.apache.axiom.soap.xsd.SOAPEnvelope getEnvelope() {
        return this.localEnvelope;
    }



    /**
     * Auto generated setter method
     *
     * @param param Envelope
     */
    public void setEnvelope(final org.apache.axiom.soap.xsd.SOAPEnvelope param) {
        this.localEnvelopeTracker = true;

        this.localEnvelope = param;


    }


    /**
     * field for ExecutedPhases
     */


    protected authclient.java.util.xsd.Iterator localExecutedPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExecutedPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Iterator
     */
    public authclient.java.util.xsd.Iterator getExecutedPhases() {
        return this.localExecutedPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param ExecutedPhases
     */
    public void setExecutedPhases(final authclient.java.util.xsd.Iterator param) {
        this.localExecutedPhasesTracker = true;

        this.localExecutedPhases = param;


    }


    /**
     * field for ExecutedPhasesExplicit
     */


    protected authclient.java.util.xsd.LinkedList localExecutedPhasesExplicit;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExecutedPhasesExplicitTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.LinkedList
     */
    public authclient.java.util.xsd.LinkedList getExecutedPhasesExplicit() {
        return this.localExecutedPhasesExplicit;
    }



    /**
     * Auto generated setter method
     *
     * @param param ExecutedPhasesExplicit
     */
    public void setExecutedPhasesExplicit(final authclient.java.util.xsd.LinkedList param) {
        this.localExecutedPhasesExplicitTracker = true;

        this.localExecutedPhasesExplicit = param;


    }


    /**
     * field for ExecutionChain
     */


    protected java.lang.Object localExecutionChain;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExecutionChainTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getExecutionChain() {
        return this.localExecutionChain;
    }



    /**
     * Auto generated setter method
     *
     * @param param ExecutionChain
     */
    public void setExecutionChain(final java.lang.Object param) {
        this.localExecutionChainTracker = true;

        this.localExecutionChain = param;


    }


    /**
     * field for FailureReason
     */


    protected org.apache.axiom.om.OMElement localFailureReason;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFailureReasonTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getFailureReason() {
        return this.localFailureReason;
    }



    /**
     * Auto generated setter method
     *
     * @param param FailureReason
     */
    public void setFailureReason(final org.apache.axiom.om.OMElement param) {
        this.localFailureReasonTracker = true;

        this.localFailureReason = param;


    }


    /**
     * field for Fault
     */


    protected boolean localFault;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getFault() {
        return this.localFault;
    }



    /**
     * Auto generated setter method
     *
     * @param param Fault
     */
    public void setFault(final boolean param) {

        // setting primitive attribute tracker to true
        this.localFaultTracker = true;

        this.localFault = param;


    }


    /**
     * field for FaultTo
     */


    protected org.apache.axis2.addressing.xsd.EndpointReference localFaultTo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultToTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.EndpointReference
     */
    public org.apache.axis2.addressing.xsd.EndpointReference getFaultTo() {
        return this.localFaultTo;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultTo
     */
    public void setFaultTo(final org.apache.axis2.addressing.xsd.EndpointReference param) {
        this.localFaultToTracker = true;

        this.localFaultTo = param;


    }


    /**
     * field for From
     */


    protected org.apache.axis2.addressing.xsd.EndpointReference localFrom;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFromTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.EndpointReference
     */
    public org.apache.axis2.addressing.xsd.EndpointReference getFrom() {
        return this.localFrom;
    }



    /**
     * Auto generated setter method
     *
     * @param param From
     */
    public void setFrom(final org.apache.axis2.addressing.xsd.EndpointReference param) {
        this.localFromTracker = true;

        this.localFrom = param;


    }


    /**
     * field for HeaderPresent
     */


    protected boolean localHeaderPresent;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localHeaderPresentTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getHeaderPresent() {
        return this.localHeaderPresent;
    }



    /**
     * Auto generated setter method
     *
     * @param param HeaderPresent
     */
    public void setHeaderPresent(final boolean param) {

        // setting primitive attribute tracker to true
        this.localHeaderPresentTracker = true;

        this.localHeaderPresent = param;


    }


    /**
     * field for InboundContentLength
     */


    protected long localInboundContentLength;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInboundContentLengthTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getInboundContentLength() {
        return this.localInboundContentLength;
    }



    /**
     * Auto generated setter method
     *
     * @param param InboundContentLength
     */
    public void setInboundContentLength(final long param) {

        // setting primitive attribute tracker to true
        this.localInboundContentLengthTracker = param != java.lang.Long.MIN_VALUE;

        this.localInboundContentLength = param;


    }


    /**
     * field for IncomingTransportName
     */


    protected java.lang.String localIncomingTransportName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localIncomingTransportNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getIncomingTransportName() {
        return this.localIncomingTransportName;
    }



    /**
     * Auto generated setter method
     *
     * @param param IncomingTransportName
     */
    public void setIncomingTransportName(final java.lang.String param) {
        this.localIncomingTransportNameTracker = true;

        this.localIncomingTransportName = param;


    }


    /**
     * field for IsSOAP11Explicit
     */


    protected boolean localIsSOAP11Explicit;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localIsSOAP11ExplicitTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getIsSOAP11Explicit() {
        return this.localIsSOAP11Explicit;
    }



    /**
     * Auto generated setter method
     *
     * @param param IsSOAP11Explicit
     */
    public void setIsSOAP11Explicit(final boolean param) {

        // setting primitive attribute tracker to true
        this.localIsSOAP11ExplicitTracker = true;

        this.localIsSOAP11Explicit = param;


    }


    /**
     * field for LogCorrelationID
     */


    protected java.lang.String localLogCorrelationID;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localLogCorrelationIDTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getLogCorrelationID() {
        return this.localLogCorrelationID;
    }



    /**
     * Auto generated setter method
     *
     * @param param LogCorrelationID
     */
    public void setLogCorrelationID(final java.lang.String param) {
        this.localLogCorrelationIDTracker = true;

        this.localLogCorrelationID = param;


    }


    /**
     * field for LogIDString
     */


    protected java.lang.String localLogIDString;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localLogIDStringTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getLogIDString() {
        return this.localLogIDString;
    }



    /**
     * Auto generated setter method
     *
     * @param param LogIDString
     */
    public void setLogIDString(final java.lang.String param) {
        this.localLogIDStringTracker = true;

        this.localLogIDString = param;


    }


    /**
     * field for MessageID
     */


    protected java.lang.String localMessageID;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessageIDTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getMessageID() {
        return this.localMessageID;
    }



    /**
     * Auto generated setter method
     *
     * @param param MessageID
     */
    public void setMessageID(final java.lang.String param) {
        this.localMessageIDTracker = true;

        this.localMessageID = param;


    }


    /**
     * field for NewThreadRequired
     */


    protected boolean localNewThreadRequired;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localNewThreadRequiredTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getNewThreadRequired() {
        return this.localNewThreadRequired;
    }



    /**
     * Auto generated setter method
     *
     * @param param NewThreadRequired
     */
    public void setNewThreadRequired(final boolean param) {

        // setting primitive attribute tracker to true
        this.localNewThreadRequiredTracker = true;

        this.localNewThreadRequired = param;


    }


    /**
     * field for OperationContext
     */


    protected org.apache.axis2.context.xsd.OperationContext localOperationContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOperationContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.OperationContext
     */
    public org.apache.axis2.context.xsd.OperationContext getOperationContext() {
        return this.localOperationContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param OperationContext
     */
    public void setOperationContext(final org.apache.axis2.context.xsd.OperationContext param) {
        this.localOperationContextTracker = true;

        this.localOperationContext = param;


    }


    /**
     * field for Options
     */


    protected org.apache.axis2.client.xsd.Options localOptions;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOptionsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.client.xsd.Options
     */
    public org.apache.axis2.client.xsd.Options getOptions() {
        return this.localOptions;
    }



    /**
     * Auto generated setter method
     *
     * @param param Options
     */
    public void setOptions(final org.apache.axis2.client.xsd.Options param) {
        this.localOptionsTracker = true;

        this.localOptions = param;


    }


    /**
     * field for OptionsExplicit
     */


    protected org.apache.axis2.client.xsd.Options localOptionsExplicit;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOptionsExplicitTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.client.xsd.Options
     */
    public org.apache.axis2.client.xsd.Options getOptionsExplicit() {
        return this.localOptionsExplicit;
    }



    /**
     * Auto generated setter method
     *
     * @param param OptionsExplicit
     */
    public void setOptionsExplicit(final org.apache.axis2.client.xsd.Options param) {
        this.localOptionsExplicitTracker = true;

        this.localOptionsExplicit = param;


    }


    /**
     * field for OutputWritten
     */


    protected boolean localOutputWritten;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOutputWrittenTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getOutputWritten() {
        return this.localOutputWritten;
    }



    /**
     * Auto generated setter method
     *
     * @param param OutputWritten
     */
    public void setOutputWritten(final boolean param) {

        // setting primitive attribute tracker to true
        this.localOutputWrittenTracker = true;

        this.localOutputWritten = param;


    }


    /**
     * field for Paused
     */


    protected boolean localPaused;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPausedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getPaused() {
        return this.localPaused;
    }



    /**
     * Auto generated setter method
     *
     * @param param Paused
     */
    public void setPaused(final boolean param) {

        // setting primitive attribute tracker to true
        this.localPausedTracker = true;

        this.localPaused = param;


    }


    /**
     * field for ProcessingFault
     */


    protected boolean localProcessingFault;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localProcessingFaultTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getProcessingFault() {
        return this.localProcessingFault;
    }



    /**
     * Auto generated setter method
     *
     * @param param ProcessingFault
     */
    public void setProcessingFault(final boolean param) {

        // setting primitive attribute tracker to true
        this.localProcessingFaultTracker = true;

        this.localProcessingFault = param;


    }


    /**
     * field for Properties
     */


    protected authclient.java.util.xsd.Map localProperties;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPropertiesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Map
     */
    public authclient.java.util.xsd.Map getProperties() {
        return this.localProperties;
    }



    /**
     * Auto generated setter method
     *
     * @param param Properties
     */
    public void setProperties(final authclient.java.util.xsd.Map param) {
        this.localPropertiesTracker = true;

        this.localProperties = param;


    }


    /**
     * field for RelatesTo
     */


    protected org.apache.axis2.addressing.xsd.RelatesTo localRelatesTo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRelatesToTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.RelatesTo
     */
    public org.apache.axis2.addressing.xsd.RelatesTo getRelatesTo() {
        return this.localRelatesTo;
    }



    /**
     * Auto generated setter method
     *
     * @param param RelatesTo
     */
    public void setRelatesTo(final org.apache.axis2.addressing.xsd.RelatesTo param) {
        this.localRelatesToTracker = true;

        this.localRelatesTo = param;


    }


    /**
     * field for Relationships This was an Array!
     */


    protected org.apache.axis2.addressing.xsd.RelatesTo[] localRelationships;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRelationshipsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.RelatesTo[]
     */
    public org.apache.axis2.addressing.xsd.RelatesTo[] getRelationships() {
        return this.localRelationships;
    }



    /**
     * validate the array for Relationships
     */
    protected void validateRelationships(final org.apache.axis2.addressing.xsd.RelatesTo[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param Relationships
     */
    public void setRelationships(final org.apache.axis2.addressing.xsd.RelatesTo[] param) {

        validateRelationships(param);

        this.localRelationshipsTracker = true;

        this.localRelationships = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axis2.addressing.xsd.RelatesTo
     */
    public void addRelationships(final org.apache.axis2.addressing.xsd.RelatesTo param) {
        if (this.localRelationships == null) {
            this.localRelationships = new org.apache.axis2.addressing.xsd.RelatesTo[] {};
        }


        // update the setting tracker
        this.localRelationshipsTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localRelationships);
        list.add(param);
        this.localRelationships = (org.apache.axis2.addressing.xsd.RelatesTo[]) list.toArray(
            new org.apache.axis2.addressing.xsd.RelatesTo[list.size()]);

    }


    /**
     * field for ReplyTo
     */


    protected org.apache.axis2.addressing.xsd.EndpointReference localReplyTo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localReplyToTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.EndpointReference
     */
    public org.apache.axis2.addressing.xsd.EndpointReference getReplyTo() {
        return this.localReplyTo;
    }



    /**
     * Auto generated setter method
     *
     * @param param ReplyTo
     */
    public void setReplyTo(final org.apache.axis2.addressing.xsd.EndpointReference param) {
        this.localReplyToTracker = true;

        this.localReplyTo = param;


    }


    /**
     * field for ResponseWritten
     */


    protected boolean localResponseWritten;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localResponseWrittenTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getResponseWritten() {
        return this.localResponseWritten;
    }



    /**
     * Auto generated setter method
     *
     * @param param ResponseWritten
     */
    public void setResponseWritten(final boolean param) {

        // setting primitive attribute tracker to true
        this.localResponseWrittenTracker = true;

        this.localResponseWritten = param;


    }


    /**
     * field for RootContext
     */


    protected org.apache.axis2.context.xsd.ConfigurationContext localRootContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRootContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ConfigurationContext
     */
    public org.apache.axis2.context.xsd.ConfigurationContext getRootContext() {
        return this.localRootContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param RootContext
     */
    public void setRootContext(final org.apache.axis2.context.xsd.ConfigurationContext param) {
        this.localRootContextTracker = true;

        this.localRootContext = param;


    }


    /**
     * field for SelfManagedDataMapExplicit
     */


    protected authclient.java.util.xsd.LinkedHashMap localSelfManagedDataMapExplicit;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSelfManagedDataMapExplicitTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.LinkedHashMap
     */
    public authclient.java.util.xsd.LinkedHashMap getSelfManagedDataMapExplicit() {
        return this.localSelfManagedDataMapExplicit;
    }



    /**
     * Auto generated setter method
     *
     * @param param SelfManagedDataMapExplicit
     */
    public void setSelfManagedDataMapExplicit(final authclient.java.util.xsd.LinkedHashMap param) {
        this.localSelfManagedDataMapExplicitTracker = true;

        this.localSelfManagedDataMapExplicit = param;


    }


    /**
     * field for ServerSide
     */


    protected boolean localServerSide;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServerSideTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getServerSide() {
        return this.localServerSide;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServerSide
     */
    public void setServerSide(final boolean param) {

        // setting primitive attribute tracker to true
        this.localServerSideTracker = true;

        this.localServerSide = param;


    }


    /**
     * field for ServiceContext
     */


    protected org.apache.axis2.context.xsd.ServiceContext localServiceContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ServiceContext
     */
    public org.apache.axis2.context.xsd.ServiceContext getServiceContext() {
        return this.localServiceContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceContext
     */
    public void setServiceContext(final org.apache.axis2.context.xsd.ServiceContext param) {
        this.localServiceContextTracker = true;

        this.localServiceContext = param;


    }


    /**
     * field for ServiceContextID
     */


    protected java.lang.String localServiceContextID;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceContextIDTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getServiceContextID() {
        return this.localServiceContextID;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceContextID
     */
    public void setServiceContextID(final java.lang.String param) {
        this.localServiceContextIDTracker = true;

        this.localServiceContextID = param;


    }


    /**
     * field for ServiceGroupContext
     */


    protected org.apache.axis2.context.xsd.ServiceGroupContext localServiceGroupContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ServiceGroupContext
     */
    public org.apache.axis2.context.xsd.ServiceGroupContext getServiceGroupContext() {
        return this.localServiceGroupContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContext
     */
    public void setServiceGroupContext(final org.apache.axis2.context.xsd.ServiceGroupContext param) {
        this.localServiceGroupContextTracker = true;

        this.localServiceGroupContext = param;


    }


    /**
     * field for ServiceGroupContextId
     */


    protected java.lang.String localServiceGroupContextId;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextIdTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getServiceGroupContextId() {
        return this.localServiceGroupContextId;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContextId
     */
    public void setServiceGroupContextId(final java.lang.String param) {
        this.localServiceGroupContextIdTracker = true;

        this.localServiceGroupContextId = param;


    }


    /**
     * field for SessionContext
     */


    protected org.apache.axis2.context.xsd.SessionContext localSessionContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSessionContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.SessionContext
     */
    public org.apache.axis2.context.xsd.SessionContext getSessionContext() {
        return this.localSessionContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param SessionContext
     */
    public void setSessionContext(final org.apache.axis2.context.xsd.SessionContext param) {
        this.localSessionContextTracker = true;

        this.localSessionContext = param;


    }


    /**
     * field for SoapAction
     */


    protected java.lang.String localSoapAction;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSoapActionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getSoapAction() {
        return this.localSoapAction;
    }



    /**
     * Auto generated setter method
     *
     * @param param SoapAction
     */
    public void setSoapAction(final java.lang.String param) {
        this.localSoapActionTracker = true;

        this.localSoapAction = param;


    }


    /**
     * field for To
     */


    protected org.apache.axis2.addressing.xsd.EndpointReference localTo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localToTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.EndpointReference
     */
    public org.apache.axis2.addressing.xsd.EndpointReference getTo() {
        return this.localTo;
    }



    /**
     * Auto generated setter method
     *
     * @param param To
     */
    public void setTo(final org.apache.axis2.addressing.xsd.EndpointReference param) {
        this.localToTracker = true;

        this.localTo = param;


    }


    /**
     * field for TransportIn
     */


    protected org.apache.axis2.description.xsd.TransportInDescription localTransportIn;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransportInTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.TransportInDescription
     */
    public org.apache.axis2.description.xsd.TransportInDescription getTransportIn() {
        return this.localTransportIn;
    }



    /**
     * Auto generated setter method
     *
     * @param param TransportIn
     */
    public void setTransportIn(final org.apache.axis2.description.xsd.TransportInDescription param) {
        this.localTransportInTracker = true;

        this.localTransportIn = param;


    }


    /**
     * field for TransportOut
     */


    protected org.apache.axis2.description.xsd.TransportOutDescription localTransportOut;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransportOutTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.TransportOutDescription
     */
    public org.apache.axis2.description.xsd.TransportOutDescription getTransportOut() {
        return this.localTransportOut;
    }



    /**
     * Auto generated setter method
     *
     * @param param TransportOut
     */
    public void setTransportOut(final org.apache.axis2.description.xsd.TransportOutDescription param) {
        this.localTransportOutTracker = true;

        this.localTransportOut = param;


    }



    /**
     *
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    @Override
    public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                    final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {



        final org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
            parentQName);
        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

    }

    @Override
    public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
        serialize(parentQName, xmlWriter, false);
    }

    @Override
    public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter,
                    final boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {



        java.lang.String prefix = null;
        java.lang.String namespace = null;


        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();
        writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

        if (serializeType) {


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://context.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":MessageContext", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "MessageContext", xmlWriter);
            }


        }
        if (this.localFLOWTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "FLOW", xmlWriter);

            if (this.localFLOW == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("FLOW cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFLOW));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localSOAP11Tracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "SOAP11", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("SOAP11 cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSOAP11));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localWSAActionTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "WSAAction", xmlWriter);


            if (this.localWSAAction == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localWSAAction);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localWSAMessageIdTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "WSAMessageId", xmlWriter);


            if (this.localWSAMessageId == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localWSAMessageId);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localAttachmentMapTracker) {
            if (this.localAttachmentMap == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "attachmentMap", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAttachmentMap.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "attachmentMap"), xmlWriter);
            }
        }
        if (this.localAxisMessageTracker) {
            if (this.localAxisMessage == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisMessage", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisMessage.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisMessage"), xmlWriter);
            }
        }
        if (this.localAxisOperationTracker) {
            if (this.localAxisOperation == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisOperation", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisOperation.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisOperation"), xmlWriter);
            }
        }
        if (this.localAxisServiceTracker) {
            if (this.localAxisService == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisService", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisService.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisService"), xmlWriter);
            }
        }
        if (this.localAxisServiceGroupTracker) {
            if (this.localAxisServiceGroup == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisServiceGroup", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisServiceGroup.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisServiceGroup"),
                    xmlWriter);
            }
        }
        if (this.localConfigurationContextTracker) {
            if (this.localConfigurationContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "configurationContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localConfigurationContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "configurationContext"),
                    xmlWriter);
            }
        }
        if (this.localCurrentHandlerIndexTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "currentHandlerIndex", xmlWriter);

            if (this.localCurrentHandlerIndex == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("currentHandlerIndex cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCurrentHandlerIndex));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localCurrentPhaseIndexTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "currentPhaseIndex", xmlWriter);

            if (this.localCurrentPhaseIndex == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("currentPhaseIndex cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCurrentPhaseIndex));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localDoingMTOMTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "doingMTOM", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("doingMTOM cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDoingMTOM));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localDoingRESTTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "doingREST", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("doingREST cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDoingREST));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localDoingSwATracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "doingSwA", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("doingSwA cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDoingSwA));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localEffectivePolicyTracker) {
            if (this.localEffectivePolicy == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "effectivePolicy", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localEffectivePolicy.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "effectivePolicy"), xmlWriter);
            }
        }
        if (this.localEnvelopeTracker) {
            if (this.localEnvelope == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "envelope", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localEnvelope.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "envelope"), xmlWriter);
            }
        }
        if (this.localExecutedPhasesTracker) {
            if (this.localExecutedPhases == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "executedPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localExecutedPhases.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executedPhases"), xmlWriter);
            }
        }
        if (this.localExecutedPhasesExplicitTracker) {
            if (this.localExecutedPhasesExplicit == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "executedPhasesExplicit", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localExecutedPhasesExplicit.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executedPhasesExplicit"),
                    xmlWriter);
            }
        }
        if (this.localExecutionChainTracker) {

            if (this.localExecutionChain != null) {
                if (this.localExecutionChain instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localExecutionChain).serialize(
                        new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executionChain"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://context.axis2.apache.org/xsd", "executionChain", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localExecutionChain,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://context.axis2.apache.org/xsd", "executionChain", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localFailureReasonTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "failureReason", xmlWriter);


            if (this.localFailureReason == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {

                this.localFailureReason.serialize(xmlWriter);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "fault", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("fault cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFault));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultToTracker) {
            if (this.localFaultTo == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "faultTo", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localFaultTo.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "faultTo"), xmlWriter);
            }
        }
        if (this.localFromTracker) {
            if (this.localFrom == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "from", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localFrom.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "from"),
                    xmlWriter);
            }
        }
        if (this.localHeaderPresentTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "headerPresent", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("headerPresent cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localHeaderPresent));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localInboundContentLengthTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "inboundContentLength", xmlWriter);

            if (this.localInboundContentLength == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("inboundContentLength cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localInboundContentLength));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localIncomingTransportNameTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "incomingTransportName", xmlWriter);


            if (this.localIncomingTransportName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localIncomingTransportName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localIsSOAP11ExplicitTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "isSOAP11Explicit", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("isSOAP11Explicit cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localIsSOAP11Explicit));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localLogCorrelationIDTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "logCorrelationID", xmlWriter);


            if (this.localLogCorrelationID == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localLogCorrelationID);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localLogIDStringTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "logIDString", xmlWriter);


            if (this.localLogIDString == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localLogIDString);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localMessageIDTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "messageID", xmlWriter);


            if (this.localMessageID == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localMessageID);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localNewThreadRequiredTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "newThreadRequired", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("newThreadRequired cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localNewThreadRequired));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localOperationContextTracker) {
            if (this.localOperationContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "operationContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localOperationContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "operationContext"),
                    xmlWriter);
            }
        }
        if (this.localOptionsTracker) {
            if (this.localOptions == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "options", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localOptions.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "options"), xmlWriter);
            }
        }
        if (this.localOptionsExplicitTracker) {
            if (this.localOptionsExplicit == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "optionsExplicit", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localOptionsExplicit.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "optionsExplicit"), xmlWriter);
            }
        }
        if (this.localOutputWrittenTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "outputWritten", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("outputWritten cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localOutputWritten));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localPausedTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "paused", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("paused cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPaused));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localProcessingFaultTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "processingFault", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("processingFault cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localProcessingFault));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localPropertiesTracker) {
            if (this.localProperties == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "properties", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localProperties.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "properties"), xmlWriter);
            }
        }
        if (this.localRelatesToTracker) {
            if (this.localRelatesTo == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "relatesTo", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localRelatesTo.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relatesTo"), xmlWriter);
            }
        }
        if (this.localRelationshipsTracker) {
            if (this.localRelationships != null) {
                for (final RelatesTo localRelationship : this.localRelationships) {
                    if (localRelationship != null) {
                        localRelationship.serialize(
                            new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relationships"),
                            xmlWriter);
                    } else {

                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "relationships", xmlWriter);

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "relationships", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }
        }
        if (this.localReplyToTracker) {
            if (this.localReplyTo == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "replyTo", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localReplyTo.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "replyTo"), xmlWriter);
            }
        }
        if (this.localResponseWrittenTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "responseWritten", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("responseWritten cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localResponseWritten));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localRootContextTracker) {
            if (this.localRootContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "rootContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localRootContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext"), xmlWriter);
            }
        }
        if (this.localSelfManagedDataMapExplicitTracker) {
            if (this.localSelfManagedDataMapExplicit == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "selfManagedDataMapExplicit", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSelfManagedDataMapExplicit.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "selfManagedDataMapExplicit"),
                    xmlWriter);
            }
        }
        if (this.localServerSideTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "serverSide", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("serverSide cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localServerSide));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localServiceContextTracker) {
            if (this.localServiceContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localServiceContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceContext"), xmlWriter);
            }
        }
        if (this.localServiceContextIDTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "serviceContextID", xmlWriter);


            if (this.localServiceContextID == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localServiceContextID);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localServiceGroupContextTracker) {
            if (this.localServiceGroupContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localServiceGroupContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContext"),
                    xmlWriter);
            }
        }
        if (this.localServiceGroupContextIdTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "serviceGroupContextId", xmlWriter);


            if (this.localServiceGroupContextId == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localServiceGroupContextId);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localSessionContextTracker) {
            if (this.localSessionContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "sessionContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSessionContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "sessionContext"), xmlWriter);
            }
        }
        if (this.localSoapActionTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "soapAction", xmlWriter);


            if (this.localSoapAction == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localSoapAction);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localToTracker) {
            if (this.localTo == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "to", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTo.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "to"),
                    xmlWriter);
            }
        }
        if (this.localTransportInTracker) {
            if (this.localTransportIn == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "transportIn", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTransportIn.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportIn"), xmlWriter);
            }
        }
        if (this.localTransportOutTracker) {
            if (this.localTransportOut == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "transportOut", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTransportOut.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportOut"), xmlWriter);
            }
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://context.axis2.apache.org/xsd")) {
            return "ns10";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, final java.lang.String namespace,
                    final java.lang.String localPart, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        final java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }

    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(final java.lang.String prefix, final java.lang.String namespace,
                    final java.lang.String attName, final java.lang.String attValue,
                    final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace, attName, attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(final java.lang.String namespace, final java.lang.String attName,
                    final java.lang.String attValue, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }
    }


    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeQNameAttribute(final java.lang.String namespace, final java.lang.String attName,
                    final javax.xml.namespace.QName qname, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

        final java.lang.String attributeNamespace = qname.getNamespaceURI();
        java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
        if (attributePrefix == null) {
            attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
        }
        java.lang.String attributeValue;
        if (attributePrefix.trim().length() > 0) {
            attributeValue = attributePrefix + ":" + qname.getLocalPart();
        } else {
            attributeValue = qname.getLocalPart();
        }

        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attributeValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attributeValue);
        }
    }

    /**
     * method to handle Qnames
     */

    private void writeQName(final javax.xml.namespace.QName qname, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        final java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
                xmlWriter.writeCharacters(
                    prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(final javax.xml.namespace.QName[] qnames, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            final java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if (prefix == null || prefix.length() == 0) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix, namespaceURI);
                    }

                    if (prefix.trim().length() > 0) {
                        stringToWrite.append(prefix).append(":").append(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(final javax.xml.stream.XMLStreamWriter xmlWriter,
                    final java.lang.String namespace)
        throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
     * databinding method to get an XML representation of this object
     *
     */
    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(final javax.xml.namespace.QName qName)
        throws org.apache.axis2.databinding.ADBException {



        final java.util.ArrayList elementList = new java.util.ArrayList();
        final java.util.ArrayList attribList = new java.util.ArrayList();

        if (this.localFLOWTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "FLOW"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFLOW));
        }
        if (this.localSOAP11Tracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "SOAP11"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSOAP11));
        }
        if (this.localWSAActionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "WSAAction"));

            elementList.add(
                this.localWSAAction == null ? null
                                            : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                this.localWSAAction));
        }
        if (this.localWSAMessageIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "WSAMessageId"));

            elementList.add(
                this.localWSAMessageId == null ? null
                                               : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                   this.localWSAMessageId));
        }
        if (this.localAttachmentMapTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "attachmentMap"));


            elementList.add(this.localAttachmentMap == null ? null : this.localAttachmentMap);
        }
        if (this.localAxisMessageTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisMessage"));


            elementList.add(this.localAxisMessage == null ? null : this.localAxisMessage);
        }
        if (this.localAxisOperationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisOperation"));


            elementList.add(this.localAxisOperation == null ? null : this.localAxisOperation);
        }
        if (this.localAxisServiceTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisService"));


            elementList.add(this.localAxisService == null ? null : this.localAxisService);
        }
        if (this.localAxisServiceGroupTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisServiceGroup"));


            elementList.add(this.localAxisServiceGroup == null ? null : this.localAxisServiceGroup);
        }
        if (this.localConfigurationContextTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "configurationContext"));


            elementList.add(this.localConfigurationContext == null ? null : this.localConfigurationContext);
        }
        if (this.localCurrentHandlerIndexTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "currentHandlerIndex"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCurrentHandlerIndex));
        }
        if (this.localCurrentPhaseIndexTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "currentPhaseIndex"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCurrentPhaseIndex));
        }
        if (this.localDoingMTOMTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "doingMTOM"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDoingMTOM));
        }
        if (this.localDoingRESTTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "doingREST"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDoingREST));
        }
        if (this.localDoingSwATracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "doingSwA"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDoingSwA));
        }
        if (this.localEffectivePolicyTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "effectivePolicy"));


            elementList.add(this.localEffectivePolicy == null ? null : this.localEffectivePolicy);
        }
        if (this.localEnvelopeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "envelope"));


            elementList.add(this.localEnvelope == null ? null : this.localEnvelope);
        }
        if (this.localExecutedPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executedPhases"));


            elementList.add(this.localExecutedPhases == null ? null : this.localExecutedPhases);
        }
        if (this.localExecutedPhasesExplicitTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executedPhasesExplicit"));


            elementList.add(this.localExecutedPhasesExplicit == null ? null : this.localExecutedPhasesExplicit);
        }
        if (this.localExecutionChainTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executionChain"));


            elementList.add(this.localExecutionChain == null ? null : this.localExecutionChain);
        }
        if (this.localFailureReasonTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "failureReason"));

            elementList.add(
                this.localFailureReason == null ? null
                                                : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                    this.localFailureReason));
        }
        if (this.localFaultTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "fault"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFault));
        }
        if (this.localFaultToTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "faultTo"));


            elementList.add(this.localFaultTo == null ? null : this.localFaultTo);
        }
        if (this.localFromTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "from"));


            elementList.add(this.localFrom == null ? null : this.localFrom);
        }
        if (this.localHeaderPresentTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "headerPresent"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localHeaderPresent));
        }
        if (this.localInboundContentLengthTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "inboundContentLength"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localInboundContentLength));
        }
        if (this.localIncomingTransportNameTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "incomingTransportName"));

            elementList.add(
                this.localIncomingTransportName == null ? null
                                                        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                            this.localIncomingTransportName));
        }
        if (this.localIsSOAP11ExplicitTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "isSOAP11Explicit"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localIsSOAP11Explicit));
        }
        if (this.localLogCorrelationIDTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "logCorrelationID"));

            elementList.add(
                this.localLogCorrelationID == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localLogCorrelationID));
        }
        if (this.localLogIDStringTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "logIDString"));

            elementList.add(
                this.localLogIDString == null ? null
                                              : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                  this.localLogIDString));
        }
        if (this.localMessageIDTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "messageID"));

            elementList.add(
                this.localMessageID == null ? null
                                            : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                this.localMessageID));
        }
        if (this.localNewThreadRequiredTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "newThreadRequired"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localNewThreadRequired));
        }
        if (this.localOperationContextTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "operationContext"));


            elementList.add(this.localOperationContext == null ? null : this.localOperationContext);
        }
        if (this.localOptionsTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "options"));


            elementList.add(this.localOptions == null ? null : this.localOptions);
        }
        if (this.localOptionsExplicitTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "optionsExplicit"));


            elementList.add(this.localOptionsExplicit == null ? null : this.localOptionsExplicit);
        }
        if (this.localOutputWrittenTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "outputWritten"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localOutputWritten));
        }
        if (this.localPausedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "paused"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPaused));
        }
        if (this.localProcessingFaultTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "processingFault"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localProcessingFault));
        }
        if (this.localPropertiesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "properties"));


            elementList.add(this.localProperties == null ? null : this.localProperties);
        }
        if (this.localRelatesToTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relatesTo"));


            elementList.add(this.localRelatesTo == null ? null : this.localRelatesTo);
        }
        if (this.localRelationshipsTracker) {
            if (this.localRelationships != null) {
                for (final RelatesTo localRelationship : this.localRelationships) {

                    if (localRelationship != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relationships"));
                        elementList.add(localRelationship);
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relationships"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relationships"));
                elementList.add(this.localRelationships);

            }

        }
        if (this.localReplyToTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "replyTo"));


            elementList.add(this.localReplyTo == null ? null : this.localReplyTo);
        }
        if (this.localResponseWrittenTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "responseWritten"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localResponseWritten));
        }
        if (this.localRootContextTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext"));


            elementList.add(this.localRootContext == null ? null : this.localRootContext);
        }
        if (this.localSelfManagedDataMapExplicitTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "selfManagedDataMapExplicit"));


            elementList.add(this.localSelfManagedDataMapExplicit == null ? null : this.localSelfManagedDataMapExplicit);
        }
        if (this.localServerSideTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serverSide"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localServerSide));
        }
        if (this.localServiceContextTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceContext"));


            elementList.add(this.localServiceContext == null ? null : this.localServiceContext);
        }
        if (this.localServiceContextIDTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceContextID"));

            elementList.add(
                this.localServiceContextID == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localServiceContextID));
        }
        if (this.localServiceGroupContextTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContext"));


            elementList.add(this.localServiceGroupContext == null ? null : this.localServiceGroupContext);
        }
        if (this.localServiceGroupContextIdTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContextId"));

            elementList.add(
                this.localServiceGroupContextId == null ? null
                                                        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                            this.localServiceGroupContextId));
        }
        if (this.localSessionContextTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "sessionContext"));


            elementList.add(this.localSessionContext == null ? null : this.localSessionContext);
        }
        if (this.localSoapActionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "soapAction"));

            elementList.add(
                this.localSoapAction == null ? null
                                             : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                 this.localSoapAction));
        }
        if (this.localToTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "to"));


            elementList.add(this.localTo == null ? null : this.localTo);
        }
        if (this.localTransportInTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportIn"));


            elementList.add(this.localTransportIn == null ? null : this.localTransportIn);
        }
        if (this.localTransportOutTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportOut"));


            elementList.add(this.localTransportOut == null ? null : this.localTransportOut);
        }

        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
            attribList.toArray());



    }



    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {



        /**
         * static method to create the object Precondition: If this object is an element, the current or
         * next start element starts this object and any intervening reader events are ignorable If this
         * object is not an element, it is a complex type and the reader is at the event just after the
         * outer start element Postcondition: If this object is an element, the reader is positioned at its
         * end element If this object is a complex type, the reader is positioned at the end element of its
         * outer element
         */
        public static MessageContext parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final MessageContext object = new MessageContext();

            final int event;
            java.lang.String nillableValue = null;
            final java.lang.String prefix = "";
            final java.lang.String namespaceuri = "";
            try {

                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }


                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    final java.lang.String fullTypeName = reader.getAttributeValue(
                        "http://www.w3.org/2001/XMLSchema-instance", "type");
                    if (fullTypeName != null) {
                        java.lang.String nsPrefix = null;
                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                        }
                        nsPrefix = nsPrefix == null ? "" : nsPrefix;

                        final java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                        if (!"MessageContext".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (MessageContext) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                nsUri, type, reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list41 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "FLOW").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setFLOW(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setFLOW(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "SOAP11").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setSOAP11(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "WSAAction").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setWSAAction(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "WSAMessageId").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setWSAMessageId(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "attachmentMap").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAttachmentMap(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAttachmentMap(org.apache.axiom.attachments.xsd.Attachments.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisMessage").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisMessage(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisMessage(org.apache.axis2.description.xsd.AxisMessage.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisOperation").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisOperation(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisOperation(org.apache.axis2.description.xsd.AxisOperation.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisService").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisService(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisService(org.apache.axis2.description.xsd.AxisService.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisServiceGroup").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisServiceGroup(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisServiceGroup(
                            org.apache.axis2.description.xsd.AxisServiceGroup.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "configurationContext").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setConfigurationContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setConfigurationContext(
                            org.apache.axis2.context.xsd.ConfigurationContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "currentHandlerIndex").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setCurrentHandlerIndex(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setCurrentHandlerIndex(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "currentPhaseIndex").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setCurrentPhaseIndex(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setCurrentPhaseIndex(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "doingMTOM").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDoingMTOM(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "doingREST").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDoingREST(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "doingSwA").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDoingSwA(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "effectivePolicy").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setEffectivePolicy(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setEffectivePolicy(org.apache.neethi.xsd.Policy.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "envelope").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setEnvelope(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setEnvelope(org.apache.axiom.soap.xsd.SOAPEnvelope.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executedPhases").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setExecutedPhases(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setExecutedPhases(authclient.java.util.xsd.Iterator.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "executedPhasesExplicit").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setExecutedPhasesExplicit(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setExecutedPhasesExplicit(authclient.java.util.xsd.LinkedList.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "executionChain").equals(
                        reader.getName())) {

                    object.setExecutionChain(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        final org.apache.axiom.om.OMFactory fac = org.apache.axiom.om.OMAbstractFactory.getOMFactory();
                        final org.apache.axiom.om.OMNamespace omNs = fac.createOMNamespace(
                            "http://context.axis2.apache.org/xsd", "");
                        final org.apache.axiom.om.OMElement _valueFailureReason = fac.createOMElement("failureReason",
                            omNs);
                        _valueFailureReason.addChild(fac.createOMText(_valueFailureReason, content));
                        object.setFailureReason(_valueFailureReason);

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "fault").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setFault(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "faultTo").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setFaultTo(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setFaultTo(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "from").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setFrom(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setFrom(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "headerPresent").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setHeaderPresent(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "inboundContentLength").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setInboundContentLength(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setInboundContentLength(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "incomingTransportName").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setIncomingTransportName(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "isSOAP11Explicit").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setIsSOAP11Explicit(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "logCorrelationID").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setLogCorrelationID(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "logIDString").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setLogIDString(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "messageID").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setMessageID(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "newThreadRequired").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setNewThreadRequired(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "operationContext").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setOperationContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setOperationContext(org.apache.axis2.context.xsd.OperationContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "options").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setOptions(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setOptions(org.apache.axis2.client.xsd.Options.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "optionsExplicit").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setOptionsExplicit(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setOptionsExplicit(org.apache.axis2.client.xsd.Options.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "outputWritten").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setOutputWritten(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "paused").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setPaused(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "processingFault").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setProcessingFault(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "properties").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setProperties(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setProperties(authclient.java.util.xsd.Map.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relatesTo").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setRelatesTo(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setRelatesTo(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "relationships").equals(
                        reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list41.add(null);
                        reader.next();
                    } else {
                        list41.add(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone41 = false;
                    while (!loopDone41) {
                        // We should be at the end element, but make sure
                        while (!reader.isEndElement()) {
                            reader.next();
                        }
                        // Step out of this element
                        reader.next();
                        // Step to next element event.
                        while (!reader.isStartElement() && !reader.isEndElement()) {
                            reader.next();
                        }
                        if (reader.isEndElement()) {
                            // two continuous end elements means we are exiting the xml structure
                            loopDone41 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                "relationships").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list41.add(null);
                                    reader.next();
                                } else {
                                    list41.add(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
                                }
                            } else {
                                loopDone41 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setRelationships(
                        (org.apache.axis2.addressing.xsd.RelatesTo[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            org.apache.axis2.addressing.xsd.RelatesTo.class, list41));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "replyTo").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setReplyTo(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setReplyTo(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "responseWritten").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setResponseWritten(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setRootContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setRootContext(org.apache.axis2.context.xsd.ConfigurationContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "selfManagedDataMapExplicit").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSelfManagedDataMapExplicit(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSelfManagedDataMapExplicit(
                            authclient.java.util.xsd.LinkedHashMap.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serverSide").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setServerSide(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceContext").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setServiceContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setServiceContext(org.apache.axis2.context.xsd.ServiceContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceContextID").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setServiceContextID(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContext").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setServiceGroupContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setServiceGroupContext(
                            org.apache.axis2.context.xsd.ServiceGroupContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContextId").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setServiceGroupContextId(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "sessionContext").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSessionContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSessionContext(org.apache.axis2.context.xsd.SessionContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "soapAction").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setSoapAction(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {


                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "to").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTo(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTo(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportIn").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTransportIn(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTransportIn(
                            org.apache.axis2.description.xsd.TransportInDescription.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportOut").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTransportOut(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTransportOut(
                            org.apache.axis2.description.xsd.TransportOutDescription.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }

                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()) {
                    // A start element we are not expecting indicates a trailing invalid property
                    throw new org.apache.axis2.databinding.ADBException(
                        "Unexpected subelement " + reader.getLocalName());
                }



            } catch (final javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }// end of factory class



}

