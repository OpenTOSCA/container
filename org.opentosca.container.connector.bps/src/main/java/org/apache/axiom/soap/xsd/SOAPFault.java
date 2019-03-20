
/**
 * SOAPFault.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axiom.soap.xsd;


/**
 * SOAPFault bean class
 */

public abstract class SOAPFault implements org.apache.axis2.databinding.ADBBean {
  /*
   * This type was generated from the piece of schema that had name = SOAPFault Namespace URI =
   * http://soap.axiom.apache.org/xsd Namespace Prefix = ns26
   */


  /**
   *
   */
  private static final long serialVersionUID = 6271602086574838142L;

  /**
   * field for Code
   */


  protected org.apache.axiom.soap.xsd.SOAPFaultCode localCode;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localCodeTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axiom.soap.xsd.SOAPFaultCode
   */
  public org.apache.axiom.soap.xsd.SOAPFaultCode getCode() {
    return this.localCode;
  }


  /**
   * Auto generated setter method
   *
   * @param param Code
   */
  public void setCode(final org.apache.axiom.soap.xsd.SOAPFaultCode param) {
    this.localCodeTracker = true;

    this.localCode = param;


  }


  /**
   * field for Detail
   */


  protected org.apache.axiom.soap.xsd.SOAPFaultDetail localDetail;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localDetailTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axiom.soap.xsd.SOAPFaultDetail
   */
  public org.apache.axiom.soap.xsd.SOAPFaultDetail getDetail() {
    return this.localDetail;
  }


  /**
   * Auto generated setter method
   *
   * @param param Detail
   */
  public void setDetail(final org.apache.axiom.soap.xsd.SOAPFaultDetail param) {
    this.localDetailTracker = true;

    this.localDetail = param;


  }


  /**
   * field for Exception
   */


  protected org.apache.axiom.om.OMElement localException;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localExceptionTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axiom.om.OMElement
   */
  public org.apache.axiom.om.OMElement getException() {
    return this.localException;
  }


  /**
   * Auto generated setter method
   *
   * @param param Exception
   */
  public void setException(final org.apache.axiom.om.OMElement param) {
    this.localExceptionTracker = true;

    this.localException = param;


  }


  /**
   * field for Node
   */


  protected org.apache.axiom.soap.xsd.SOAPFaultNode localNode;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localNodeTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axiom.soap.xsd.SOAPFaultNode
   */
  public org.apache.axiom.soap.xsd.SOAPFaultNode getNode() {
    return this.localNode;
  }


  /**
   * Auto generated setter method
   *
   * @param param Node
   */
  public void setNode(final org.apache.axiom.soap.xsd.SOAPFaultNode param) {
    this.localNodeTracker = true;

    this.localNode = param;


  }


  /**
   * field for Reason
   */


  protected org.apache.axiom.soap.xsd.SOAPFaultReason localReason;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localReasonTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axiom.soap.xsd.SOAPFaultReason
   */
  public org.apache.axiom.soap.xsd.SOAPFaultReason getReason() {
    return this.localReason;
  }


  /**
   * Auto generated setter method
   *
   * @param param Reason
   */
  public void setReason(final org.apache.axiom.soap.xsd.SOAPFaultReason param) {
    this.localReasonTracker = true;

    this.localReason = param;


  }


  /**
   * field for Role
   */


  protected org.apache.axiom.soap.xsd.SOAPFaultRole localRole;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localRoleTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axiom.soap.xsd.SOAPFaultRole
   */
  public org.apache.axiom.soap.xsd.SOAPFaultRole getRole() {
    return this.localRole;
  }


  /**
   * Auto generated setter method
   *
   * @param param Role
   */
  public void setRole(final org.apache.axiom.soap.xsd.SOAPFaultRole param) {
    this.localRoleTracker = true;

    this.localRole = param;


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


      final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://soap.axiom.apache.org/xsd");
      if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
          namespacePrefix + ":SOAPFault", xmlWriter);
      } else {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "SOAPFault", xmlWriter);
      }


    }
    if (this.localCodeTracker) {
      if (this.localCode == null) {

        writeStartElement(null, "http://soap.axiom.apache.org/xsd", "code", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localCode.serialize(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "code"),
          xmlWriter);
      }
    }
    if (this.localDetailTracker) {
      if (this.localDetail == null) {

        writeStartElement(null, "http://soap.axiom.apache.org/xsd", "detail", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localDetail.serialize(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "detail"),
          xmlWriter);
      }
    }
    if (this.localExceptionTracker) {
      namespace = "http://soap.axiom.apache.org/xsd";
      writeStartElement(null, namespace, "exception", xmlWriter);


      if (this.localException == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        this.localException.serialize(xmlWriter);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localNodeTracker) {
      if (this.localNode == null) {

        writeStartElement(null, "http://soap.axiom.apache.org/xsd", "node", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localNode.serialize(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "node"),
          xmlWriter);
      }
    }
    if (this.localReasonTracker) {
      if (this.localReason == null) {

        writeStartElement(null, "http://soap.axiom.apache.org/xsd", "reason", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localReason.serialize(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "reason"),
          xmlWriter);
      }
    }
    if (this.localRoleTracker) {
      if (this.localRole == null) {

        writeStartElement(null, "http://soap.axiom.apache.org/xsd", "role", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localRole.serialize(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "role"),
          xmlWriter);
      }
    }
    xmlWriter.writeEndElement();


  }

  private static java.lang.String generatePrefix(final java.lang.String namespace) {
    if (namespace.equals("http://soap.axiom.apache.org/xsd")) {
      return "ns26";
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

    if (this.localCodeTracker) {
      elementList.add(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "code"));


      elementList.add(this.localCode == null ? null : this.localCode);
    }
    if (this.localDetailTracker) {
      elementList.add(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "detail"));


      elementList.add(this.localDetail == null ? null : this.localDetail);
    }
    if (this.localExceptionTracker) {
      elementList.add(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "exception"));

      elementList.add(this.localException == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localException));
    }
    if (this.localNodeTracker) {
      elementList.add(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "node"));


      elementList.add(this.localNode == null ? null : this.localNode);
    }
    if (this.localReasonTracker) {
      elementList.add(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "reason"));


      elementList.add(this.localReason == null ? null : this.localReason);
    }
    if (this.localRoleTracker) {
      elementList.add(new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd", "role"));


      elementList.add(this.localRole == null ? null : this.localRole);
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
    public static SOAPFault parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
      final SOAPFault object = null;

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

            if (!"SOAPFault".equals(type)) {
              // find namespace for the prefix
              final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
              return (SOAPFault) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                type,
                reader);
            }

            throw new org.apache.axis2.databinding.ADBException(
              "The an abstract class can not be instantiated !!!");


          }


        }


        // Note all attributes that were handled. Used to differ normal attributes
        // from anyAttributes.
        final java.util.Vector handledAttributes = new java.util.Vector();


        reader.next();


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd",
          "code").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setCode(null);
            reader.next();

            reader.next();

          } else {

            object.setCode(org.apache.axiom.soap.xsd.SOAPFaultCode.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd",
          "detail").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setDetail(null);
            reader.next();

            reader.next();

          } else {

            object.setDetail(org.apache.axiom.soap.xsd.SOAPFaultDetail.Factory.parse(reader));

            reader.next();
          }
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
            final org.apache.axiom.om.OMNamespace omNs =
              fac.createOMNamespace("http://soap.axiom.apache.org/xsd", "");
            final org.apache.axiom.om.OMElement _valueException = fac.createOMElement("exception", omNs);
            _valueException.addChild(fac.createOMText(_valueException, content));
            object.setException(_valueException);

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd",
          "node").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setNode(null);
            reader.next();

            reader.next();

          } else {

            object.setNode(org.apache.axiom.soap.xsd.SOAPFaultNode.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd",
          "reason").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setReason(null);
            reader.next();

            reader.next();

          } else {

            object.setReason(org.apache.axiom.soap.xsd.SOAPFaultReason.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://soap.axiom.apache.org/xsd",
          "role").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setRole(null);
            reader.next();

            reader.next();

          } else {

            object.setRole(org.apache.axiom.soap.xsd.SOAPFaultRole.Factory.parse(reader));

            reader.next();
          }
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

