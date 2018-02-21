
/**
 * Attr.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.w3c.dom.xsd;


/**
 * Attr bean class
 */

public abstract class Attr implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = Attr Namespace URI =
     * http://dom.w3c.org/xsd Namespace Prefix = ns23
     */


    /**
     *
     */
    private static final long serialVersionUID = 6850975547297487217L;

    /**
     * field for Id
     */


    protected boolean localId;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localIdTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getId() {
        return this.localId;
    }



    /**
     * Auto generated setter method
     *
     * @param param Id
     */
    public void setId(final boolean param) {

        // setting primitive attribute tracker to true
        this.localIdTracker = true;

        this.localId = param;


    }


    /**
     * field for Name
     */


    protected java.lang.String localName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localNameTracker = false;


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
        this.localNameTracker = true;

        this.localName = param;


    }


    /**
     * field for OwnerElement
     */


    protected org.w3c.dom.xsd.Element localOwnerElement;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOwnerElementTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.w3c.dom.xsd.Element
     */
    public org.w3c.dom.xsd.Element getOwnerElement() {
        return this.localOwnerElement;
    }



    /**
     * Auto generated setter method
     *
     * @param param OwnerElement
     */
    public void setOwnerElement(final org.w3c.dom.xsd.Element param) {
        this.localOwnerElementTracker = true;

        this.localOwnerElement = param;


    }


    /**
     * field for SchemaTypeInfo
     */


    protected org.w3c.dom.xsd.TypeInfo localSchemaTypeInfo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSchemaTypeInfoTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.w3c.dom.xsd.TypeInfo
     */
    public org.w3c.dom.xsd.TypeInfo getSchemaTypeInfo() {
        return this.localSchemaTypeInfo;
    }



    /**
     * Auto generated setter method
     *
     * @param param SchemaTypeInfo
     */
    public void setSchemaTypeInfo(final org.w3c.dom.xsd.TypeInfo param) {
        this.localSchemaTypeInfoTracker = true;

        this.localSchemaTypeInfo = param;


    }


    /**
     * field for Specified
     */


    protected boolean localSpecified;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSpecifiedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getSpecified() {
        return this.localSpecified;
    }



    /**
     * Auto generated setter method
     *
     * @param param Specified
     */
    public void setSpecified(final boolean param) {

        // setting primitive attribute tracker to true
        this.localSpecifiedTracker = true;

        this.localSpecified = param;


    }


    /**
     * field for Value
     */


    protected java.lang.String localValue;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localValueTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getValue() {
        return this.localValue;
    }



    /**
     * Auto generated setter method
     *
     * @param param Value
     */
    public void setValue(final java.lang.String param) {
        this.localValueTracker = true;

        this.localValue = param;


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


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://dom.w3c.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Attr",
                    xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Attr", xmlWriter);
            }


        }
        if (this.localIdTracker) {
            namespace = "http://dom.w3c.org/xsd";
            writeStartElement(null, namespace, "id", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localId));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localNameTracker) {
            namespace = "http://dom.w3c.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localOwnerElementTracker) {
            if (this.localOwnerElement == null) {

                writeStartElement(null, "http://dom.w3c.org/xsd", "ownerElement", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localOwnerElement.serialize(
                    new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "ownerElement"), xmlWriter);
            }
        }
        if (this.localSchemaTypeInfoTracker) {
            if (this.localSchemaTypeInfo == null) {

                writeStartElement(null, "http://dom.w3c.org/xsd", "schemaTypeInfo", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSchemaTypeInfo.serialize(
                    new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "schemaTypeInfo"), xmlWriter);
            }
        }
        if (this.localSpecifiedTracker) {
            namespace = "http://dom.w3c.org/xsd";
            writeStartElement(null, namespace, "specified", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("specified cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSpecified));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localValueTracker) {
            namespace = "http://dom.w3c.org/xsd";
            writeStartElement(null, namespace, "value", xmlWriter);


            if (this.localValue == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localValue);

            }

            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://dom.w3c.org/xsd")) {
            return "ns23";
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

        if (this.localIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "id"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localId));
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localName));
        }
        if (this.localOwnerElementTracker) {
            elementList.add(new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "ownerElement"));


            elementList.add(this.localOwnerElement == null ? null : this.localOwnerElement);
        }
        if (this.localSchemaTypeInfoTracker) {
            elementList.add(new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "schemaTypeInfo"));


            elementList.add(this.localSchemaTypeInfo == null ? null : this.localSchemaTypeInfo);
        }
        if (this.localSpecifiedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "specified"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSpecified));
        }
        if (this.localValueTracker) {
            elementList.add(new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "value"));

            elementList.add(this.localValue == null ? null
                                                    : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                        this.localValue));
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
        public static Attr parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final Attr object = null;

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

                        if (!"Attr".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Attr) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri, type,
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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "id").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "name").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    && new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "ownerElement").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setOwnerElement(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setOwnerElement(org.w3c.dom.xsd.Element.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "schemaTypeInfo").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSchemaTypeInfo(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSchemaTypeInfo(org.w3c.dom.xsd.TypeInfo.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "specified").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setSpecified(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://dom.w3c.org/xsd", "value").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setValue(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

