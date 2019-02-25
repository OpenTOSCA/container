
/**
 * XmlSchemaParticle.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.ws.commons.schema.xsd;

import org.w3c.dom.xsd.Attr;

/**
 * XmlSchemaParticle bean class
 */

public class XmlSchemaParticle extends org.apache.ws.commons.schema.xsd.XmlSchemaAnnotated
                               implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = XmlSchemaParticle Namespace URI
     * = http://schema.commons.ws.apache.org/xsd Namespace Prefix = ns22
     */


    /**
     *
     */
    private static final long serialVersionUID = 4868290789199550102L;

    /**
     * field for MaxOccurs
     */


    protected long localMaxOccurs;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMaxOccursTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getMaxOccurs() {
        return this.localMaxOccurs;
    }



    /**
     * Auto generated setter method
     *
     * @param param MaxOccurs
     */
    public void setMaxOccurs(final long param) {

        // setting primitive attribute tracker to true
        this.localMaxOccursTracker = param != java.lang.Long.MIN_VALUE;

        this.localMaxOccurs = param;


    }


    /**
     * field for MinOccurs
     */


    protected long localMinOccurs;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMinOccursTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getMinOccurs() {
        return this.localMinOccurs;
    }



    /**
     * Auto generated setter method
     *
     * @param param MinOccurs
     */
    public void setMinOccurs(final long param) {

        // setting primitive attribute tracker to true
        this.localMinOccursTracker = param != java.lang.Long.MIN_VALUE;

        this.localMinOccurs = param;


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


        final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://schema.commons.ws.apache.org/xsd");
        if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                           namespacePrefix + ":XmlSchemaParticle", xmlWriter);
        } else {
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "XmlSchemaParticle", xmlWriter);
        }

        if (this.localLineNumberTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "lineNumber", xmlWriter);

            if (this.localLineNumber == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("lineNumber cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localLinePositionTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "linePosition", xmlWriter);

            if (this.localLinePosition == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("linePosition cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLinePosition));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localMetaInfoMapTracker) {
            if (this.localMetaInfoMap == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "metaInfoMap", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localMetaInfoMap.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "metaInfoMap"), xmlWriter);
            }
        }
        if (this.localSourceURITracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "sourceURI", xmlWriter);


            if (this.localSourceURI == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localSourceURI);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localAnnotationTracker) {
            if (this.localAnnotation == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "annotation", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAnnotation.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "annotation"), xmlWriter);
            }
        }
        if (this.localIdTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "id", xmlWriter);


            if (this.localId == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localId);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localUnhandledAttributesTracker) {
            if (this.localUnhandledAttributes != null) {
                for (final Attr localUnhandledAttribute : this.localUnhandledAttributes) {
                    if (localUnhandledAttribute != null) {
                        localUnhandledAttribute.serialize(new javax.xml.namespace.QName(
                            "http://schema.commons.ws.apache.org/xsd", "unhandledAttributes"), xmlWriter);
                    } else {

                        writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "unhandledAttributes",
                                          xmlWriter);

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "unhandledAttributes", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }
        }
        if (this.localMaxOccursTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "maxOccurs", xmlWriter);

            if (this.localMaxOccurs == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("maxOccurs cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMaxOccurs));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localMinOccursTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "minOccurs", xmlWriter);

            if (this.localMinOccurs == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("minOccurs cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMinOccurs));
            }

            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://schema.commons.ws.apache.org/xsd")) {
            return "ns22";
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


        attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema-instance", "type"));
        attribList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "XmlSchemaParticle"));
        if (this.localLineNumberTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "lineNumber"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
        }
        if (this.localLinePositionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "linePosition"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLinePosition));
        }
        if (this.localMetaInfoMapTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "metaInfoMap"));


            elementList.add(this.localMetaInfoMap == null ? null : this.localMetaInfoMap);
        }
        if (this.localSourceURITracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "sourceURI"));

            elementList.add(this.localSourceURI == null ? null
                                                        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSourceURI));
        }
        if (this.localAnnotationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "annotation"));


            elementList.add(this.localAnnotation == null ? null : this.localAnnotation);
        }
        if (this.localIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "id"));

            elementList.add(this.localId == null ? null
                                                 : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localId));
        }
        if (this.localUnhandledAttributesTracker) {
            if (this.localUnhandledAttributes != null) {
                for (final Attr localUnhandledAttribute : this.localUnhandledAttributes) {

                    if (localUnhandledAttribute != null) {
                        elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                            "unhandledAttributes"));
                        elementList.add(localUnhandledAttribute);
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                            "unhandledAttributes"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "unhandledAttributes"));
                elementList.add(this.localUnhandledAttributes);

            }

        }
        if (this.localMaxOccursTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "maxOccurs"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMaxOccurs));
        }
        if (this.localMinOccursTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "minOccurs"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMinOccurs));
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
        public static XmlSchemaParticle parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final XmlSchemaParticle object = new XmlSchemaParticle();

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

                        if (!"XmlSchemaParticle".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (XmlSchemaParticle) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                                                                                                                      type,
                                                                                                                      reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list7 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "lineNumber").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setLineNumber(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setLineNumber(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "linePosition").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setLinePosition(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setLinePosition(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "metaInfoMap").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setMetaInfoMap(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setMetaInfoMap(authclient.java.util.xsd.Map.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "sourceURI").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setSourceURI(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "annotation").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAnnotation(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAnnotation(org.apache.ws.commons.schema.xsd.XmlSchemaAnnotation.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "id").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "unhandledAttributes").equals(reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list7.add(null);
                        reader.next();
                    } else {
                        list7.add(org.w3c.dom.xsd.Attr.Factory.parse(reader));
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone7 = false;
                    while (!loopDone7) {
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
                            loopDone7 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                                "unhandledAttributes").equals(reader.getName())) {

                                nillableValue =
                                    reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list7.add(null);
                                    reader.next();
                                } else {
                                    list7.add(org.w3c.dom.xsd.Attr.Factory.parse(reader));
                                }
                            } else {
                                loopDone7 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setUnhandledAttributes((org.w3c.dom.xsd.Attr[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(org.w3c.dom.xsd.Attr.class,
                                                                                                                                           list7));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "maxOccurs").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setMaxOccurs(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setMaxOccurs(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "minOccurs").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setMinOccurs(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setMinOccurs(java.lang.Long.MIN_VALUE);

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



            }
            catch (final javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }// end of factory class



}

