
/**
 * UploadService.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.carbon.bpel.deployer.services;

import org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem;

/**
 * UploadService bean class
 */

public class UploadService implements org.apache.axis2.databinding.ADBBean {

  /**
   *
   */
  private static final long serialVersionUID = 4338247998026638950L;


  public static final javax.xml.namespace.QName MY_QNAME =
    new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org", "uploadService", "ns2");


  /**
   * field for FileItems This was an Array!
   */


  protected org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[] localFileItems;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localFileItemsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[]
   */
  public org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[] getFileItems() {
    return this.localFileItems;
  }


  /**
   * validate the array for FileItems
   */
  protected void validateFileItems(final org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[] param) {

  }


  /**
   * Auto generated setter method
   *
   * @param param FileItems
   */
  public void setFileItems(final org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[] param) {

    validateFileItems(param);

    this.localFileItemsTracker = true;

    this.localFileItems = param;
  }


  /**
   * Auto generated add method for the array for convenience
   *
   * @param param org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem
   */
  public void addFileItems(final org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem param) {
    if (this.localFileItems == null) {
      this.localFileItems = new org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[] {};
    }


    // update the setting tracker
    this.localFileItemsTracker = true;


    final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localFileItems);
    list.add(param);
    this.localFileItems =
      (org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[]) list.toArray(new org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[list.size()]);

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
      new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
    return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

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
        registerPrefix(xmlWriter, "http://services.deployer.bpel.carbon.wso2.org");
      if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
          namespacePrefix + ":uploadService", xmlWriter);
      } else {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "uploadService", xmlWriter);
      }


    }
    if (this.localFileItemsTracker) {
      if (this.localFileItems != null) {
        for (final UploadedFileItem localFileItem : this.localFileItems) {
          if (localFileItem != null) {
            localFileItem.serialize(new javax.xml.namespace.QName(
              "http://services.deployer.bpel.carbon.wso2.org", "fileItems"), xmlWriter);
          } else {

            writeStartElement(null, "http://services.deployer.bpel.carbon.wso2.org", "fileItems",
              xmlWriter);

            // write the nil attribute
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();

          }

        }
      } else {

        writeStartElement(null, "http://services.deployer.bpel.carbon.wso2.org", "fileItems", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }
    }
    xmlWriter.writeEndElement();


  }

  private static java.lang.String generatePrefix(final java.lang.String namespace) {
    if (namespace.equals("http://services.deployer.bpel.carbon.wso2.org")) {
      return "ns2";
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

    if (this.localFileItemsTracker) {
      if (this.localFileItems != null) {
        for (final UploadedFileItem localFileItem : this.localFileItems) {

          if (localFileItem != null) {
            elementList.add(new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
              "fileItems"));
            elementList.add(localFileItem);
          } else {

            elementList.add(new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
              "fileItems"));
            elementList.add(null);

          }

        }
      } else {

        elementList.add(new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
          "fileItems"));
        elementList.add(this.localFileItems);

      }

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
    public static UploadService parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
      final UploadService object = new UploadService();

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

            if (!"uploadService".equals(type)) {
              // find namespace for the prefix
              final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
              return (UploadService) org.wso2.carbon.bpel.deployer.services.types.xsd.ExtensionMapper.getTypeObject(nsUri,
                type,
                reader);
            }


          }


        }


        // Note all attributes that were handled. Used to differ normal attributes
        // from anyAttributes.
        final java.util.Vector handledAttributes = new java.util.Vector();


        reader.next();

        final java.util.ArrayList list1 = new java.util.ArrayList();


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement()
          && new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
          "fileItems").equals(reader.getName())) {


          // Process the array and step past its final element's end.

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            list1.add(null);
            reader.next();
          } else {
            list1.add(org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem.Factory.parse(reader));
          }
          // loop until we find a start element that is not part of this array
          boolean loopDone1 = false;
          while (!loopDone1) {
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
              loopDone1 = true;
            } else {
              if (new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
                "fileItems").equals(reader.getName())) {

                nillableValue =
                  reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                  list1.add(null);
                  reader.next();
                } else {
                  list1.add(org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem.Factory.parse(reader));
                }
              } else {
                loopDone1 = true;
              }
            }
          }
          // call the converter utility to convert and set the array

          object.setFileItems((org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem.class,
            list1));

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

