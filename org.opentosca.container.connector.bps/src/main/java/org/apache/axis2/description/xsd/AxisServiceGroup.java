
/**
 * AxisServiceGroup.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.description.xsd;


/**
 * AxisServiceGroup bean class
 */

public class AxisServiceGroup implements org.apache.axis2.databinding.ADBBean {
  /*
   * This type was generated from the piece of schema that had name = AxisServiceGroup Namespace URI =
   * http://description.axis2.apache.org/xsd Namespace Prefix = ns19
   */


  /**
   *
   */
  private static final long serialVersionUID = 6082642256039931997L;

  /**
   * field for AxisDescription
   */


  protected org.apache.axis2.engine.xsd.AxisConfiguration localAxisDescription;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localAxisDescriptionTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.engine.xsd.AxisConfiguration
   */
  public org.apache.axis2.engine.xsd.AxisConfiguration getAxisDescription() {
    return this.localAxisDescription;
  }


  /**
   * Auto generated setter method
   *
   * @param param AxisDescription
   */
  public void setAxisDescription(final org.apache.axis2.engine.xsd.AxisConfiguration param) {
    this.localAxisDescriptionTracker = true;

    this.localAxisDescription = param;


  }


  /**
   * field for FoundWebResources
   */


  protected boolean localFoundWebResources;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localFoundWebResourcesTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getFoundWebResources() {
    return this.localFoundWebResources;
  }


  /**
   * Auto generated setter method
   *
   * @param param FoundWebResources
   */
  public void setFoundWebResources(final boolean param) {

    // setting primitive attribute tracker to true
    this.localFoundWebResourcesTracker = true;

    this.localFoundWebResources = param;


  }


  /**
   * field for Key
   */


  protected java.lang.Object localKey;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localKeyTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getKey() {
    return this.localKey;
  }


  /**
   * Auto generated setter method
   *
   * @param param Key
   */
  public void setKey(final java.lang.Object param) {
    this.localKeyTracker = true;

    this.localKey = param;


  }


  /**
   * field for ModuleRefs
   */


  protected java.lang.Object localModuleRefs;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localModuleRefsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getModuleRefs() {
    return this.localModuleRefs;
  }


  /**
   * Auto generated setter method
   *
   * @param param ModuleRefs
   */
  public void setModuleRefs(final java.lang.Object param) {
    this.localModuleRefsTracker = true;

    this.localModuleRefs = param;


  }


  /**
   * field for ServiceGroupClassLoader
   */


  protected java.lang.Object localServiceGroupClassLoader;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localServiceGroupClassLoaderTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getServiceGroupClassLoader() {
    return this.localServiceGroupClassLoader;
  }


  /**
   * Auto generated setter method
   *
   * @param param ServiceGroupClassLoader
   */
  public void setServiceGroupClassLoader(final java.lang.Object param) {
    this.localServiceGroupClassLoaderTracker = true;

    this.localServiceGroupClassLoader = param;


  }


  /**
   * field for ServiceGroupName
   */


  protected java.lang.String localServiceGroupName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localServiceGroupNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getServiceGroupName() {
    return this.localServiceGroupName;
  }


  /**
   * Auto generated setter method
   *
   * @param param ServiceGroupName
   */
  public void setServiceGroupName(final java.lang.String param) {
    this.localServiceGroupNameTracker = true;

    this.localServiceGroupName = param;


  }


  /**
   * field for Services
   */


  protected authclient.java.util.xsd.Iterator localServices;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localServicesTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Iterator
   */
  public authclient.java.util.xsd.Iterator getServices() {
    return this.localServices;
  }


  /**
   * Auto generated setter method
   *
   * @param param Services
   */
  public void setServices(final authclient.java.util.xsd.Iterator param) {
    this.localServicesTracker = true;

    this.localServices = param;


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


      final java.lang.String namespacePrefix =
        registerPrefix(xmlWriter, "http://description.axis2.apache.org/xsd");
      if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
          namespacePrefix + ":AxisServiceGroup", xmlWriter);
      } else {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "AxisServiceGroup",
          xmlWriter);
      }


    }
    if (this.localAxisDescriptionTracker) {
      if (this.localAxisDescription == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisDescription", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localAxisDescription.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "axisDescription"), xmlWriter);
      }
    }
    if (this.localFoundWebResourcesTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "foundWebResources", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("foundWebResources cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFoundWebResources));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localKeyTracker) {

      if (this.localKey != null) {
        if (this.localKey instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localKey).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "key"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localKey, xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localModuleRefsTracker) {

      if (this.localModuleRefs != null) {
        if (this.localModuleRefs instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localModuleRefs).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "moduleRefs"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "moduleRefs", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localModuleRefs, xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "moduleRefs", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localServiceGroupClassLoaderTracker) {

      if (this.localServiceGroupClassLoader != null) {
        if (this.localServiceGroupClassLoader instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localServiceGroupClassLoader).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "serviceGroupClassLoader"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "serviceGroupClassLoader",
            xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localServiceGroupClassLoader,
            xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "serviceGroupClassLoader",
          xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localServiceGroupNameTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "serviceGroupName", xmlWriter);


      if (this.localServiceGroupName == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localServiceGroupName);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localServicesTracker) {
      if (this.localServices == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "services", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localServices.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "services"), xmlWriter);
      }
    }
    xmlWriter.writeEndElement();


  }

  private static java.lang.String generatePrefix(final java.lang.String namespace) {
    if (namespace.equals("http://description.axis2.apache.org/xsd")) {
      return "ns19";
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

    if (this.localAxisDescriptionTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "axisDescription"));


      elementList.add(this.localAxisDescription == null ? null : this.localAxisDescription);
    }
    if (this.localFoundWebResourcesTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "foundWebResources"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFoundWebResources));
    }
    if (this.localKeyTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "key"));


      elementList.add(this.localKey == null ? null : this.localKey);
    }
    if (this.localModuleRefsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "moduleRefs"));


      elementList.add(this.localModuleRefs == null ? null : this.localModuleRefs);
    }
    if (this.localServiceGroupClassLoaderTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "serviceGroupClassLoader"));


      elementList.add(this.localServiceGroupClassLoader == null ? null : this.localServiceGroupClassLoader);
    }
    if (this.localServiceGroupNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "serviceGroupName"));

      elementList.add(this.localServiceGroupName == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localServiceGroupName));
    }
    if (this.localServicesTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "services"));


      elementList.add(this.localServices == null ? null : this.localServices);
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
    public static AxisServiceGroup parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
      final AxisServiceGroup object = new AxisServiceGroup();

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

            if (!"AxisServiceGroup".equals(type)) {
              // find namespace for the prefix
              final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
              return (AxisServiceGroup) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "axisDescription").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setAxisDescription(null);
            reader.next();

            reader.next();

          } else {

            object.setAxisDescription(org.apache.axis2.engine.xsd.AxisConfiguration.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "foundWebResources").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setFoundWebResources(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "key").equals(reader.getName())) {

          object.setKey(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "moduleRefs").equals(reader.getName())) {

          object.setModuleRefs(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "serviceGroupClassLoader").equals(reader.getName())) {

          object.setServiceGroupClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "serviceGroupName").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setServiceGroupName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "services").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setServices(null);
            reader.next();

            reader.next();

          } else {

            object.setServices(authclient.java.util.xsd.Iterator.Factory.parse(reader));

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

