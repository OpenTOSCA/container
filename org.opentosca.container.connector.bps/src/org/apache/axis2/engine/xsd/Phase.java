
/**
 * Phase.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.engine.xsd;


/**
 * Phase bean class
 */

public class Phase implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = Phase Namespace URI =
     * http://engine.axis2.apache.org/xsd Namespace Prefix = ns8
     */


    /**
     *
     */
    private static final long serialVersionUID = 4470390831534784463L;

    /**
     * field for HandlerCount
     */


    protected int localHandlerCount;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localHandlerCountTracker = false;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getHandlerCount() {
        return this.localHandlerCount;
    }



    /**
     * Auto generated setter method
     *
     * @param param HandlerCount
     */
    public void setHandlerCount(final int param) {

        // setting primitive attribute tracker to true
        this.localHandlerCountTracker = param != java.lang.Integer.MIN_VALUE;

        this.localHandlerCount = param;


    }


    /**
     * field for HandlerDesc
     */


    protected org.apache.axis2.description.xsd.HandlerDescription localHandlerDesc;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localHandlerDescTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.HandlerDescription
     */
    public org.apache.axis2.description.xsd.HandlerDescription getHandlerDesc() {
        return this.localHandlerDesc;
    }



    /**
     * Auto generated setter method
     *
     * @param param HandlerDesc
     */
    public void setHandlerDesc(final org.apache.axis2.description.xsd.HandlerDescription param) {
        this.localHandlerDescTracker = true;

        this.localHandlerDesc = param;


    }


    /**
     * field for Handlers
     */


    protected java.lang.Object localHandlers;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localHandlersTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getHandlers() {
        return this.localHandlers;
    }



    /**
     * Auto generated setter method
     *
     * @param param Handlers
     */
    public void setHandlers(final java.lang.Object param) {
        this.localHandlersTracker = true;

        this.localHandlers = param;


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
     * field for PhaseFirst
     */


    protected org.apache.axis2.engine.xsd.Handler localPhaseFirst;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhaseFirstTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.Handler
     */
    public org.apache.axis2.engine.xsd.Handler getPhaseFirst() {
        return this.localPhaseFirst;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhaseFirst
     */
    public void setPhaseFirst(final org.apache.axis2.engine.xsd.Handler param) {
        this.localPhaseFirstTracker = true;

        this.localPhaseFirst = param;


    }


    /**
     * field for PhaseLast
     */


    protected org.apache.axis2.engine.xsd.Handler localPhaseLast;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhaseLastTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.Handler
     */
    public org.apache.axis2.engine.xsd.Handler getPhaseLast() {
        return this.localPhaseLast;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhaseLast
     */
    public void setPhaseLast(final org.apache.axis2.engine.xsd.Handler param) {
        this.localPhaseLastTracker = true;

        this.localPhaseLast = param;


    }


    /**
     * field for PhaseName
     */


    protected java.lang.String localPhaseName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhaseNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getPhaseName() {
        return this.localPhaseName;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhaseName
     */
    public void setPhaseName(final java.lang.String param) {
        this.localPhaseNameTracker = true;

        this.localPhaseName = param;


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


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://engine.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Phase",
                    xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Phase", xmlWriter);
            }


        }
        if (this.localHandlerCountTracker) {
            namespace = "http://engine.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "handlerCount", xmlWriter);

            if (this.localHandlerCount == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("handlerCount cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localHandlerCount));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localHandlerDescTracker) {
            if (this.localHandlerDesc == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "handlerDesc", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localHandlerDesc.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlerDesc"), xmlWriter);
            }
        }
        if (this.localHandlersTracker) {

            if (this.localHandlers != null) {
                if (this.localHandlers instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localHandlers).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlers"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "handlers", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localHandlers, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "handlers", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localNameTracker) {
            namespace = "http://engine.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localPhaseFirstTracker) {
            if (this.localPhaseFirst == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "phaseFirst", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localPhaseFirst.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseFirst"), xmlWriter);
            }
        }
        if (this.localPhaseLastTracker) {
            if (this.localPhaseLast == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "phaseLast", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localPhaseLast.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseLast"), xmlWriter);
            }
        }
        if (this.localPhaseNameTracker) {
            namespace = "http://engine.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "phaseName", xmlWriter);


            if (this.localPhaseName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localPhaseName);

            }

            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://engine.axis2.apache.org/xsd")) {
            return "ns8";
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

        if (this.localHandlerCountTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlerCount"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localHandlerCount));
        }
        if (this.localHandlerDescTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlerDesc"));


            elementList.add(this.localHandlerDesc == null ? null : this.localHandlerDesc);
        }
        if (this.localHandlersTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlers"));


            elementList.add(this.localHandlers == null ? null : this.localHandlers);
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localName));
        }
        if (this.localPhaseFirstTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseFirst"));


            elementList.add(this.localPhaseFirst == null ? null : this.localPhaseFirst);
        }
        if (this.localPhaseLastTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseLast"));


            elementList.add(this.localPhaseLast == null ? null : this.localPhaseLast);
        }
        if (this.localPhaseNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseName"));

            elementList.add(
                this.localPhaseName == null ? null
                                            : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                this.localPhaseName));
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
        public static Phase parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final Phase object = new Phase();

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

                        if (!"Phase".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Phase) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri, type,
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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlerCount").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setHandlerCount(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setHandlerCount(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlerDesc").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setHandlerDesc(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setHandlerDesc(
                            org.apache.axis2.description.xsd.HandlerDescription.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "handlers").equals(
                        reader.getName())) {

                    object.setHandlers(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "name").equals(
                        reader.getName())) {

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
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseFirst").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setPhaseFirst(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setPhaseFirst(org.apache.axis2.engine.xsd.Handler.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseLast").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setPhaseLast(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setPhaseLast(org.apache.axis2.engine.xsd.Handler.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phaseName").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setPhaseName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

