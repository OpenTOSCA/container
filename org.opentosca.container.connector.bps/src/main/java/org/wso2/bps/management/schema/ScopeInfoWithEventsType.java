
/**
 * ScopeInfoWithEventsType.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * ScopeInfoWithEventsType bean class
 */

public class ScopeInfoWithEventsType implements org.apache.axis2.databinding.ADBBean {
  /*
   * This type was generated from the piece of schema that had name = ScopeInfoWithEventsType
   * Namespace URI = http://wso2.org/bps/management/schema Namespace Prefix = ns1
   */


  /**
   *
   */
  private static final long serialVersionUID = -3264334215446866969L;

  /**
   * field for Siid
   */


  protected java.lang.String localSiid;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSiid() {
    return this.localSiid;
  }


  /**
   * Auto generated setter method
   *
   * @param param Siid
   */
  public void setSiid(final java.lang.String param) {

    this.localSiid = param;


  }


  /**
   * field for Name
   */


  protected java.lang.String localName;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getName() {
    return this.localName;
  }


  /**
   * Auto generated setter method
   *
   * @param param Name
   */
  public void setName(final java.lang.String param) {

    this.localName = param;


  }


  /**
   * field for Status
   */


  protected org.wso2.bps.management.schema.ScopeStatusType localStatus;


  /**
   * Auto generated getter method
   *
   * @return org.wso2.bps.management.schema.ScopeStatusType
   */
  public org.wso2.bps.management.schema.ScopeStatusType getStatus() {
    return this.localStatus;
  }


  /**
   * Auto generated setter method
   *
   * @param param Status
   */
  public void setStatus(final org.wso2.bps.management.schema.ScopeStatusType param) {

    this.localStatus = param;


  }


  /**
   * field for ChildrenWithEvents
   */


  protected org.wso2.bps.management.schema.ChildrenWithEvents_type0 localChildrenWithEvents;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localChildrenWithEventsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.wso2.bps.management.schema.ChildrenWithEvents_type0
   */
  public org.wso2.bps.management.schema.ChildrenWithEvents_type0 getChildrenWithEvents() {
    return this.localChildrenWithEvents;
  }


  /**
   * Auto generated setter method
   *
   * @param param ChildrenWithEvents
   */
  public void setChildrenWithEvents(final org.wso2.bps.management.schema.ChildrenWithEvents_type0 param) {
    this.localChildrenWithEventsTracker = param != null;

    this.localChildrenWithEvents = param;


  }


  /**
   * field for ActivitiesWithEvents
   */


  protected org.wso2.bps.management.schema.ActivitiesWithEvents_type0 localActivitiesWithEvents;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localActivitiesWithEventsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.wso2.bps.management.schema.ActivitiesWithEvents_type0
   */
  public org.wso2.bps.management.schema.ActivitiesWithEvents_type0 getActivitiesWithEvents() {
    return this.localActivitiesWithEvents;
  }


  /**
   * Auto generated setter method
   *
   * @param param ActivitiesWithEvents
   */
  public void setActivitiesWithEvents(final org.wso2.bps.management.schema.ActivitiesWithEvents_type0 param) {
    this.localActivitiesWithEventsTracker = param != null;

    this.localActivitiesWithEvents = param;


  }


  /**
   * field for VariablesWithEvents
   */


  protected org.wso2.bps.management.schema.VariablesWithEvents_type0 localVariablesWithEvents;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localVariablesWithEventsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.wso2.bps.management.schema.VariablesWithEvents_type0
   */
  public org.wso2.bps.management.schema.VariablesWithEvents_type0 getVariablesWithEvents() {
    return this.localVariablesWithEvents;
  }


  /**
   * Auto generated setter method
   *
   * @param param VariablesWithEvents
   */
  public void setVariablesWithEvents(final org.wso2.bps.management.schema.VariablesWithEvents_type0 param) {
    this.localVariablesWithEventsTracker = param != null;

    this.localVariablesWithEvents = param;


  }


  /**
   * field for CorrelationSets
   */


  protected org.wso2.bps.management.schema.CorrelationSets_type1 localCorrelationSets;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localCorrelationSetsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.wso2.bps.management.schema.CorrelationSets_type1
   */
  public org.wso2.bps.management.schema.CorrelationSets_type1 getCorrelationSets() {
    return this.localCorrelationSets;
  }


  /**
   * Auto generated setter method
   *
   * @param param CorrelationSets
   */
  public void setCorrelationSets(final org.wso2.bps.management.schema.CorrelationSets_type1 param) {
    this.localCorrelationSetsTracker = param != null;

    this.localCorrelationSets = param;


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


      final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://wso2.org/bps/management/schema");
      if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
          namespacePrefix + ":ScopeInfoWithEventsType", xmlWriter);
      } else {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ScopeInfoWithEventsType",
          xmlWriter);
      }


    }

    namespace = "http://wso2.org/bps/management/schema";
    writeStartElement(null, namespace, "siid", xmlWriter);


    if (this.localSiid == null) {
      // write the nil attribute

      throw new org.apache.axis2.databinding.ADBException("siid cannot be null!!");

    } else {


      xmlWriter.writeCharacters(this.localSiid);

    }

    xmlWriter.writeEndElement();

    namespace = "http://wso2.org/bps/management/schema";
    writeStartElement(null, namespace, "name", xmlWriter);


    if (this.localName == null) {
      // write the nil attribute

      throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");

    } else {


      xmlWriter.writeCharacters(this.localName);

    }

    xmlWriter.writeEndElement();

    if (this.localStatus == null) {
      throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
    }
    this.localStatus.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"),
      xmlWriter);
    if (this.localChildrenWithEventsTracker) {
      if (this.localChildrenWithEvents == null) {
        throw new org.apache.axis2.databinding.ADBException("childrenWithEvents cannot be null!!");
      }
      this.localChildrenWithEvents.serialize(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/schema", "childrenWithEvents"), xmlWriter);
    }
    if (this.localActivitiesWithEventsTracker) {
      if (this.localActivitiesWithEvents == null) {
        throw new org.apache.axis2.databinding.ADBException("activitiesWithEvents cannot be null!!");
      }
      this.localActivitiesWithEvents.serialize(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/schema", "activitiesWithEvents"), xmlWriter);
    }
    if (this.localVariablesWithEventsTracker) {
      if (this.localVariablesWithEvents == null) {
        throw new org.apache.axis2.databinding.ADBException("variablesWithEvents cannot be null!!");
      }
      this.localVariablesWithEvents.serialize(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/schema", "variablesWithEvents"), xmlWriter);
    }
    if (this.localCorrelationSetsTracker) {
      if (this.localCorrelationSets == null) {
        throw new org.apache.axis2.databinding.ADBException("correlationSets cannot be null!!");
      }
      this.localCorrelationSets.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
        "correlationSets"), xmlWriter);
    }
    xmlWriter.writeEndElement();


  }

  private static java.lang.String generatePrefix(final java.lang.String namespace) {
    if (namespace.equals("http://wso2.org/bps/management/schema")) {
      return "ns1";
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


    elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "siid"));

    if (this.localSiid != null) {
      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSiid));
    } else {
      throw new org.apache.axis2.databinding.ADBException("siid cannot be null!!");
    }

    elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "name"));

    if (this.localName != null) {
      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
    } else {
      throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");
    }

    elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"));


    if (this.localStatus == null) {
      throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
    }
    elementList.add(this.localStatus);
    if (this.localChildrenWithEventsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
        "childrenWithEvents"));


      if (this.localChildrenWithEvents == null) {
        throw new org.apache.axis2.databinding.ADBException("childrenWithEvents cannot be null!!");
      }
      elementList.add(this.localChildrenWithEvents);
    }
    if (this.localActivitiesWithEventsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
        "activitiesWithEvents"));


      if (this.localActivitiesWithEvents == null) {
        throw new org.apache.axis2.databinding.ADBException("activitiesWithEvents cannot be null!!");
      }
      elementList.add(this.localActivitiesWithEvents);
    }
    if (this.localVariablesWithEventsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
        "variablesWithEvents"));


      if (this.localVariablesWithEvents == null) {
        throw new org.apache.axis2.databinding.ADBException("variablesWithEvents cannot be null!!");
      }
      elementList.add(this.localVariablesWithEvents);
    }
    if (this.localCorrelationSetsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "correlationSets"));


      if (this.localCorrelationSets == null) {
        throw new org.apache.axis2.databinding.ADBException("correlationSets cannot be null!!");
      }
      elementList.add(this.localCorrelationSets);
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
    public static ScopeInfoWithEventsType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
      final ScopeInfoWithEventsType object = new ScopeInfoWithEventsType();

      final int event;
      final java.lang.String nillableValue = null;
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

            if (!"ScopeInfoWithEventsType".equals(type)) {
              // find namespace for the prefix
              final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
              return (ScopeInfoWithEventsType) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
                type,
                reader);
            }


          }


        }


        // Note all attributes that were handled. Used to differ normal attributes
        // from anyAttributes.
        final java.util.Vector handledAttributes = new java.util.Vector();


        reader.next();


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "siid").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setSiid(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          reader.next();

        } // End of if for expected property start element

        else {
          // A start element we are not expecting indicates an invalid parameter was passed
          throw new org.apache.axis2.databinding.ADBException(
            "Unexpected subelement " + reader.getLocalName());
        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "name").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          reader.next();

        } // End of if for expected property start element

        else {
          // A start element we are not expecting indicates an invalid parameter was passed
          throw new org.apache.axis2.databinding.ADBException(
            "Unexpected subelement " + reader.getLocalName());
        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "status").equals(reader.getName())) {

          object.setStatus(org.wso2.bps.management.schema.ScopeStatusType.Factory.parse(reader));

          reader.next();

        } // End of if for expected property start element

        else {
          // A start element we are not expecting indicates an invalid parameter was passed
          throw new org.apache.axis2.databinding.ADBException(
            "Unexpected subelement " + reader.getLocalName());
        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "childrenWithEvents").equals(reader.getName())) {

          object.setChildrenWithEvents(org.wso2.bps.management.schema.ChildrenWithEvents_type0.Factory.parse(reader));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "activitiesWithEvents").equals(reader.getName())) {

          object.setActivitiesWithEvents(org.wso2.bps.management.schema.ActivitiesWithEvents_type0.Factory.parse(reader));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "variablesWithEvents").equals(reader.getName())) {

          object.setVariablesWithEvents(org.wso2.bps.management.schema.VariablesWithEvents_type0.Factory.parse(reader));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
          "correlationSets").equals(reader.getName())) {

          object.setCorrelationSets(org.wso2.bps.management.schema.CorrelationSets_type1.Factory.parse(reader));

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

