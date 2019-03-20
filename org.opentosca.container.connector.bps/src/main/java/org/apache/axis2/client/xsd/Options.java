
/**
 * Options.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.client.xsd;

import org.apache.axis2.addressing.xsd.RelatesTo;

/**
 * Options bean class
 */

public class Options implements org.apache.axis2.databinding.ADBBean {
  /*
   * This type was generated from the piece of schema that had name = Options Namespace URI =
   * http://client.axis2.apache.org/xsd Namespace Prefix = ns29
   */


  /**
   *
   */
  private static final long serialVersionUID = -7123490050652853093L;

  /**
   * field for Action
   */


  protected java.lang.String localAction;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localActionTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getAction() {
    return this.localAction;
  }


  /**
   * Auto generated setter method
   *
   * @param param Action
   */
  public void setAction(final java.lang.String param) {
    this.localActionTracker = true;

    this.localAction = param;


  }


  /**
   * field for CallTransportCleanup
   */


  protected boolean localCallTransportCleanup;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localCallTransportCleanupTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getCallTransportCleanup() {
    return this.localCallTransportCleanup;
  }


  /**
   * Auto generated setter method
   *
   * @param param CallTransportCleanup
   */
  public void setCallTransportCleanup(final boolean param) {

    // setting primitive attribute tracker to true
    this.localCallTransportCleanupTracker = true;

    this.localCallTransportCleanup = param;


  }


  /**
   * field for ExceptionToBeThrownOnSOAPFault
   */


  protected boolean localExceptionToBeThrownOnSOAPFault;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localExceptionToBeThrownOnSOAPFaultTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getExceptionToBeThrownOnSOAPFault() {
    return this.localExceptionToBeThrownOnSOAPFault;
  }


  /**
   * Auto generated setter method
   *
   * @param param ExceptionToBeThrownOnSOAPFault
   */
  public void setExceptionToBeThrownOnSOAPFault(final boolean param) {

    // setting primitive attribute tracker to true
    this.localExceptionToBeThrownOnSOAPFaultTracker = true;

    this.localExceptionToBeThrownOnSOAPFault = param;


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
   * field for Listener
   */


  protected org.apache.axis2.transport.xsd.TransportListener localListener;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localListenerTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.transport.xsd.TransportListener
   */
  public org.apache.axis2.transport.xsd.TransportListener getListener() {
    return this.localListener;
  }


  /**
   * Auto generated setter method
   *
   * @param param Listener
   */
  public void setListener(final org.apache.axis2.transport.xsd.TransportListener param) {
    this.localListenerTracker = true;

    this.localListener = param;


  }


  /**
   * field for LogCorrelationIDString
   */


  protected java.lang.String localLogCorrelationIDString;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localLogCorrelationIDStringTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getLogCorrelationIDString() {
    return this.localLogCorrelationIDString;
  }


  /**
   * Auto generated setter method
   *
   * @param param LogCorrelationIDString
   */
  public void setLogCorrelationIDString(final java.lang.String param) {
    this.localLogCorrelationIDStringTracker = true;

    this.localLogCorrelationIDString = param;


  }


  /**
   * field for ManageSession
   */


  protected boolean localManageSession;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localManageSessionTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getManageSession() {
    return this.localManageSession;
  }


  /**
   * Auto generated setter method
   *
   * @param param ManageSession
   */
  public void setManageSession(final boolean param) {

    // setting primitive attribute tracker to true
    this.localManageSessionTracker = true;

    this.localManageSession = param;


  }


  /**
   * field for MessageId
   */


  protected java.lang.String localMessageId;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localMessageIdTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getMessageId() {
    return this.localMessageId;
  }


  /**
   * Auto generated setter method
   *
   * @param param MessageId
   */
  public void setMessageId(final java.lang.String param) {
    this.localMessageIdTracker = true;

    this.localMessageId = param;


  }


  /**
   * field for Parent
   */


  protected org.apache.axis2.client.xsd.Options localParent;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localParentTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.client.xsd.Options
   */
  public org.apache.axis2.client.xsd.Options getParent() {
    return this.localParent;
  }


  /**
   * Auto generated setter method
   *
   * @param param Parent
   */
  public void setParent(final org.apache.axis2.client.xsd.Options param) {
    this.localParentTracker = true;

    this.localParent = param;


  }


  /**
   * field for Password
   */


  protected java.lang.String localPassword;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localPasswordTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getPassword() {
    return this.localPassword;
  }


  /**
   * Auto generated setter method
   *
   * @param param Password
   */
  public void setPassword(final java.lang.String param) {
    this.localPasswordTracker = true;

    this.localPassword = param;


  }


  /**
   * field for Properties This was an Array!
   */


  protected java.lang.String[] localProperties;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localPropertiesTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String[]
   */
  public java.lang.String[] getProperties() {
    return this.localProperties;
  }


  /**
   * validate the array for Properties
   */
  protected void validateProperties(final java.lang.String[] param) {

  }


  /**
   * Auto generated setter method
   *
   * @param param Properties
   */
  public void setProperties(final java.lang.String[] param) {

    validateProperties(param);

    this.localPropertiesTracker = true;

    this.localProperties = param;
  }


  /**
   * Auto generated add method for the array for convenience
   *
   * @param param java.lang.String
   */
  public void addProperties(final java.lang.String param) {
    if (this.localProperties == null) {
      this.localProperties = new java.lang.String[] {};
    }


    // update the setting tracker
    this.localPropertiesTracker = true;


    final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localProperties);
    list.add(param);
    this.localProperties = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

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
    this.localRelationships =
      (org.apache.axis2.addressing.xsd.RelatesTo[]) list.toArray(new org.apache.axis2.addressing.xsd.RelatesTo[list.size()]);

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
   * field for SoapVersionURI
   */


  protected java.lang.String localSoapVersionURI;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSoapVersionURITracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSoapVersionURI() {
    return this.localSoapVersionURI;
  }


  /**
   * Auto generated setter method
   *
   * @param param SoapVersionURI
   */
  public void setSoapVersionURI(final java.lang.String param) {
    this.localSoapVersionURITracker = true;

    this.localSoapVersionURI = param;


  }


  /**
   * field for TimeOutInMilliSeconds
   */


  protected long localTimeOutInMilliSeconds;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localTimeOutInMilliSecondsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return long
   */
  public long getTimeOutInMilliSeconds() {
    return this.localTimeOutInMilliSeconds;
  }


  /**
   * Auto generated setter method
   *
   * @param param TimeOutInMilliSeconds
   */
  public void setTimeOutInMilliSeconds(final long param) {

    // setting primitive attribute tracker to true
    this.localTimeOutInMilliSecondsTracker = param != java.lang.Long.MIN_VALUE;

    this.localTimeOutInMilliSeconds = param;


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
   * field for TransportInProtocol
   */


  protected java.lang.String localTransportInProtocol;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localTransportInProtocolTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getTransportInProtocol() {
    return this.localTransportInProtocol;
  }


  /**
   * Auto generated setter method
   *
   * @param param TransportInProtocol
   */
  public void setTransportInProtocol(final java.lang.String param) {
    this.localTransportInProtocolTracker = true;

    this.localTransportInProtocol = param;


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
   * field for UseSeparateListener
   */


  protected boolean localUseSeparateListener;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localUseSeparateListenerTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getUseSeparateListener() {
    return this.localUseSeparateListener;
  }


  /**
   * Auto generated setter method
   *
   * @param param UseSeparateListener
   */
  public void setUseSeparateListener(final boolean param) {

    // setting primitive attribute tracker to true
    this.localUseSeparateListenerTracker = true;

    this.localUseSeparateListener = param;


  }


  /**
   * field for UserName
   */


  protected java.lang.String localUserName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localUserNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getUserName() {
    return this.localUserName;
  }


  /**
   * Auto generated setter method
   *
   * @param param UserName
   */
  public void setUserName(final java.lang.String param) {
    this.localUserNameTracker = true;

    this.localUserName = param;


  }


  /**
   *
   * @param parentQName
   * @param factory
   * @return org.apache.axiom.om.OMElement
   */
  @Override
  public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                                                    final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {


    final org.apache.axiom.om.OMDataSource dataSource =
      new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
    return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

  }

  @Override
  public void serialize(final javax.xml.namespace.QName parentQName,
                        final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
    org.apache.axis2.databinding.ADBException {
    serialize(parentQName, xmlWriter, false);
  }

  @Override
  public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter,
                        final boolean serializeType) throws javax.xml.stream.XMLStreamException,
    org.apache.axis2.databinding.ADBException {


    java.lang.String prefix = null;
    java.lang.String namespace = null;


    prefix = parentQName.getPrefix();
    namespace = parentQName.getNamespaceURI();
    writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

    if (serializeType) {


      final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://client.axis2.apache.org/xsd");
      if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Options",
          xmlWriter);
      } else {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Options", xmlWriter);
      }


    }
    if (this.localActionTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "action", xmlWriter);


      if (this.localAction == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localAction);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localCallTransportCleanupTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "callTransportCleanup", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("callTransportCleanup cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCallTransportCleanup));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localExceptionToBeThrownOnSOAPFaultTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "exceptionToBeThrownOnSOAPFault", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("exceptionToBeThrownOnSOAPFault cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localExceptionToBeThrownOnSOAPFault));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localFaultToTracker) {
      if (this.localFaultTo == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "faultTo", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localFaultTo.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "faultTo"), xmlWriter);
      }
    }
    if (this.localFromTracker) {
      if (this.localFrom == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "from", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localFrom.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "from"),
          xmlWriter);
      }
    }
    if (this.localListenerTracker) {
      if (this.localListener == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "listener", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localListener.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "listener"), xmlWriter);
      }
    }
    if (this.localLogCorrelationIDStringTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "logCorrelationIDString", xmlWriter);


      if (this.localLogCorrelationIDString == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localLogCorrelationIDString);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localManageSessionTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "manageSession", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("manageSession cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localManageSession));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localMessageIdTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "messageId", xmlWriter);


      if (this.localMessageId == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localMessageId);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localParentTracker) {
      if (this.localParent == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "parent", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localParent.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "parent"), xmlWriter);
      }
    }
    if (this.localPasswordTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "password", xmlWriter);


      if (this.localPassword == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localPassword);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localPropertiesTracker) {
      if (this.localProperties != null) {
        namespace = "http://client.axis2.apache.org/xsd";
        for (final String localPropertie : this.localProperties) {

          if (localPropertie != null) {

            writeStartElement(null, namespace, "properties", xmlWriter);


            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPropertie));

            xmlWriter.writeEndElement();

          } else {

            // write null attribute
            namespace = "http://client.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "properties", xmlWriter);
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();

          }

        }
      } else {

        // write the null attribute
        // write null attribute
        writeStartElement(null, "http://client.axis2.apache.org/xsd", "properties", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }

    }
    if (this.localRelatesToTracker) {
      if (this.localRelatesTo == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "relatesTo", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localRelatesTo.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "relatesTo"), xmlWriter);
      }
    }
    if (this.localRelationshipsTracker) {
      if (this.localRelationships != null) {
        for (final RelatesTo localRelationship : this.localRelationships) {
          if (localRelationship != null) {
            localRelationship.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
              "relationships"), xmlWriter);
          } else {

            writeStartElement(null, "http://client.axis2.apache.org/xsd", "relationships", xmlWriter);

            // write the nil attribute
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();

          }

        }
      } else {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "relationships", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }
    }
    if (this.localReplyToTracker) {
      if (this.localReplyTo == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "replyTo", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localReplyTo.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "replyTo"), xmlWriter);
      }
    }
    if (this.localSoapVersionURITracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "soapVersionURI", xmlWriter);


      if (this.localSoapVersionURI == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localSoapVersionURI);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localTimeOutInMilliSecondsTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "timeOutInMilliSeconds", xmlWriter);

      if (this.localTimeOutInMilliSeconds == java.lang.Long.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("timeOutInMilliSeconds cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTimeOutInMilliSeconds));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localToTracker) {
      if (this.localTo == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "to", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localTo.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "to"),
          xmlWriter);
      }
    }
    if (this.localTransportInTracker) {
      if (this.localTransportIn == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "transportIn", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localTransportIn.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "transportIn"), xmlWriter);
      }
    }
    if (this.localTransportInProtocolTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "transportInProtocol", xmlWriter);


      if (this.localTransportInProtocol == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localTransportInProtocol);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localTransportOutTracker) {
      if (this.localTransportOut == null) {

        writeStartElement(null, "http://client.axis2.apache.org/xsd", "transportOut", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localTransportOut.serialize(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "transportOut"), xmlWriter);
      }
    }
    if (this.localUseSeparateListenerTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "useSeparateListener", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("useSeparateListener cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUseSeparateListener));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localUserNameTracker) {
      namespace = "http://client.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "userName", xmlWriter);


      if (this.localUserName == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localUserName);

      }

      xmlWriter.writeEndElement();
    }
    xmlWriter.writeEndElement();


  }

  private static java.lang.String generatePrefix(final java.lang.String namespace) {
    if (namespace.equals("http://client.axis2.apache.org/xsd")) {
      return "ns29";
    }
    return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
  }

  /**
   * Utility method to write an element start tag.
   */
  private void writeStartElement(java.lang.String prefix, final java.lang.String namespace,
                                 final java.lang.String localPart,
                                 final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
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
                              final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
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
                              final java.lang.String attValue,
                              final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
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
                                   final javax.xml.namespace.QName qname,
                                   final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

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

  private void writeQName(final javax.xml.namespace.QName qname,
                          final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
    final java.lang.String namespaceURI = qname.getNamespaceURI();
    if (namespaceURI != null) {
      java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
      if (prefix == null) {
        prefix = generatePrefix(namespaceURI);
        xmlWriter.writeNamespace(prefix, namespaceURI);
        xmlWriter.setPrefix(prefix, namespaceURI);
      }

      if (prefix.trim().length() > 0) {
        xmlWriter.writeCharacters(prefix + ":"
          + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      } else {
        // i.e this is the default namespace
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }

    } else {
      xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
    }
  }

  private void writeQNames(final javax.xml.namespace.QName[] qnames,
                           final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

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
            stringToWrite.append(prefix).append(":")
              .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          } else {
            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
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
                                          final java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
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
  public javax.xml.stream.XMLStreamReader getPullParser(final javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {


    final java.util.ArrayList elementList = new java.util.ArrayList();
    final java.util.ArrayList attribList = new java.util.ArrayList();

    if (this.localActionTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "action"));

      elementList.add(this.localAction == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localAction));
    }
    if (this.localCallTransportCleanupTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
        "callTransportCleanup"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCallTransportCleanup));
    }
    if (this.localExceptionToBeThrownOnSOAPFaultTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
        "exceptionToBeThrownOnSOAPFault"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localExceptionToBeThrownOnSOAPFault));
    }
    if (this.localFaultToTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "faultTo"));


      elementList.add(this.localFaultTo == null ? null : this.localFaultTo);
    }
    if (this.localFromTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "from"));


      elementList.add(this.localFrom == null ? null : this.localFrom);
    }
    if (this.localListenerTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "listener"));


      elementList.add(this.localListener == null ? null : this.localListener);
    }
    if (this.localLogCorrelationIDStringTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
        "logCorrelationIDString"));

      elementList.add(this.localLogCorrelationIDString == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLogCorrelationIDString));
    }
    if (this.localManageSessionTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "manageSession"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localManageSession));
    }
    if (this.localMessageIdTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "messageId"));

      elementList.add(this.localMessageId == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMessageId));
    }
    if (this.localParentTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "parent"));


      elementList.add(this.localParent == null ? null : this.localParent);
    }
    if (this.localPasswordTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "password"));

      elementList.add(this.localPassword == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPassword));
    }
    if (this.localPropertiesTracker) {
      if (this.localProperties != null) {
        for (final String localPropertie : this.localProperties) {

          if (localPropertie != null) {
            elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
              "properties"));
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPropertie));
          } else {

            elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
              "properties"));
            elementList.add(null);

          }


        }
      } else {

        elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "properties"));
        elementList.add(null);

      }

    }
    if (this.localRelatesToTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "relatesTo"));


      elementList.add(this.localRelatesTo == null ? null : this.localRelatesTo);
    }
    if (this.localRelationshipsTracker) {
      if (this.localRelationships != null) {
        for (final RelatesTo localRelationship : this.localRelationships) {

          if (localRelationship != null) {
            elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
              "relationships"));
            elementList.add(localRelationship);
          } else {

            elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
              "relationships"));
            elementList.add(null);

          }

        }
      } else {

        elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "relationships"));
        elementList.add(this.localRelationships);

      }

    }
    if (this.localReplyToTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "replyTo"));


      elementList.add(this.localReplyTo == null ? null : this.localReplyTo);
    }
    if (this.localSoapVersionURITracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "soapVersionURI"));

      elementList.add(this.localSoapVersionURI == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSoapVersionURI));
    }
    if (this.localTimeOutInMilliSecondsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
        "timeOutInMilliSeconds"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTimeOutInMilliSeconds));
    }
    if (this.localToTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "to"));


      elementList.add(this.localTo == null ? null : this.localTo);
    }
    if (this.localTransportInTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "transportIn"));


      elementList.add(this.localTransportIn == null ? null : this.localTransportIn);
    }
    if (this.localTransportInProtocolTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "transportInProtocol"));

      elementList.add(this.localTransportInProtocol == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTransportInProtocol));
    }
    if (this.localTransportOutTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "transportOut"));


      elementList.add(this.localTransportOut == null ? null : this.localTransportOut);
    }
    if (this.localUseSeparateListenerTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "useSeparateListener"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUseSeparateListener));
    }
    if (this.localUserNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd", "userName"));

      elementList.add(this.localUserName == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUserName));
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
    public static Options parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
      final Options object = new Options();

      final int event;
      java.lang.String nillableValue = null;
      final java.lang.String prefix = "";
      final java.lang.String namespaceuri = "";
      try {

        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }


        if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
          final java.lang.String fullTypeName =
            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
          if (fullTypeName != null) {
            java.lang.String nsPrefix = null;
            if (fullTypeName.indexOf(":") > -1) {
              nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
            }
            nsPrefix = nsPrefix == null ? "" : nsPrefix;

            final java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

            if (!"Options".equals(type)) {
              // find namespace for the prefix
              final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
              return (Options) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri, type,
                reader);
            }


          }


        }


        // Note all attributes that were handled. Used to differ normal attributes
        // from anyAttributes.
        final java.util.Vector handledAttributes = new java.util.Vector();


        reader.next();

        final java.util.ArrayList list12 = new java.util.ArrayList();

        final java.util.ArrayList list14 = new java.util.ArrayList();


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "action").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setAction(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "callTransportCleanup").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setCallTransportCleanup(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "exceptionToBeThrownOnSOAPFault").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setExceptionToBeThrownOnSOAPFault(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "faultTo").equals(reader.getName())) {

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "from").equals(reader.getName())) {

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "listener").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setListener(null);
            reader.next();

            reader.next();

          } else {

            object.setListener(org.apache.axis2.transport.xsd.TransportListener.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "logCorrelationIDString").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setLogCorrelationIDString(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "manageSession").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setManageSession(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "messageId").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setMessageId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "parent").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setParent(null);
            reader.next();

            reader.next();

          } else {

            object.setParent(org.apache.axis2.client.xsd.Options.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "password").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "properties").equals(reader.getName())) {


          // Process the array and step past its final element's end.

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            list12.add(null);

            reader.next();
          } else {
            list12.add(reader.getElementText());
          }
          // loop until we find a start element that is not part of this array
          boolean loopDone12 = false;
          while (!loopDone12) {
            // Ensure we are at the EndElement
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
              loopDone12 = true;
            } else {
              if (new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
                "properties").equals(reader.getName())) {

                nillableValue =
                  reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                  list12.add(null);

                  reader.next();
                } else {
                  list12.add(reader.getElementText());
                }
              } else {
                loopDone12 = true;
              }
            }
          }
          // call the converter utility to convert and set the array

          object.setProperties((java.lang.String[]) list12.toArray(new java.lang.String[list12.size()]));

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "relatesTo").equals(reader.getName())) {

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "relationships").equals(reader.getName())) {


          // Process the array and step past its final element's end.

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            list14.add(null);
            reader.next();
          } else {
            list14.add(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
          }
          // loop until we find a start element that is not part of this array
          boolean loopDone14 = false;
          while (!loopDone14) {
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
              loopDone14 = true;
            } else {
              if (new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
                "relationships").equals(reader.getName())) {

                nillableValue =
                  reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                  list14.add(null);
                  reader.next();
                } else {
                  list14.add(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
                }
              } else {
                loopDone14 = true;
              }
            }
          }
          // call the converter utility to convert and set the array

          object.setRelationships((org.apache.axis2.addressing.xsd.RelatesTo[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(org.apache.axis2.addressing.xsd.RelatesTo.class,
            list14));

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "replyTo").equals(reader.getName())) {

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "soapVersionURI").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setSoapVersionURI(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "timeOutInMilliSeconds").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setTimeOutInMilliSeconds(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

          reader.next();

        } // End of if for expected property start element

        else {

          object.setTimeOutInMilliSeconds(java.lang.Long.MIN_VALUE);

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "to").equals(reader.getName())) {

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "transportIn").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setTransportIn(null);
            reader.next();

            reader.next();

          } else {

            object.setTransportIn(org.apache.axis2.description.xsd.TransportInDescription.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "transportInProtocol").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setTransportInProtocol(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "transportOut").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setTransportOut(null);
            reader.next();

            reader.next();

          } else {

            object.setTransportOut(org.apache.axis2.description.xsd.TransportOutDescription.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "useSeparateListener").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setUseSeparateListener(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://client.axis2.apache.org/xsd",
          "userName").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setUserName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement()) {
          // A start element we are not expecting indicates a trailing invalid
          // property
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

