
/**
 * TransportInDescription.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.description.xsd;


/**
 * TransportInDescription bean class
 */

public class TransportInDescription implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = TransportInDescription Namespace
     * URI = http://description.axis2.apache.org/xsd Namespace Prefix = ns19
     */


    /**
     *
     */
    private static final long serialVersionUID = -9068494219950011240L;

    /**
     * field for FaultFlow
     */


    protected org.apache.axis2.description.xsd.Flow localFaultFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.Flow
     */
    public org.apache.axis2.description.xsd.Flow getFaultFlow() {
        return this.localFaultFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultFlow
     */
    public void setFaultFlow(final org.apache.axis2.description.xsd.Flow param) {
        this.localFaultFlowTracker = true;

        this.localFaultFlow = param;


    }


    /**
     * field for FaultPhase
     */


    protected org.apache.axis2.engine.xsd.Phase localFaultPhase;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultPhaseTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.Phase
     */
    public org.apache.axis2.engine.xsd.Phase getFaultPhase() {
        return this.localFaultPhase;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultPhase
     */
    public void setFaultPhase(final org.apache.axis2.engine.xsd.Phase param) {
        this.localFaultPhaseTracker = true;

        this.localFaultPhase = param;


    }


    /**
     * field for InFlow
     */


    protected org.apache.axis2.description.xsd.Flow localInFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.Flow
     */
    public org.apache.axis2.description.xsd.Flow getInFlow() {
        return this.localInFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param InFlow
     */
    public void setInFlow(final org.apache.axis2.description.xsd.Flow param) {
        this.localInFlowTracker = true;

        this.localInFlow = param;


    }


    /**
     * field for InPhase
     */


    protected org.apache.axis2.engine.xsd.Phase localInPhase;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInPhaseTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.Phase
     */
    public org.apache.axis2.engine.xsd.Phase getInPhase() {
        return this.localInPhase;
    }



    /**
     * Auto generated setter method
     *
     * @param param InPhase
     */
    public void setInPhase(final org.apache.axis2.engine.xsd.Phase param) {
        this.localInPhaseTracker = true;

        this.localInPhase = param;


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
     * field for Parameters
     */


    protected java.lang.Object localParameters;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localParametersTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getParameters() {
        return this.localParameters;
    }



    /**
     * Auto generated setter method
     *
     * @param param Parameters
     */
    public void setParameters(final java.lang.Object param) {
        this.localParametersTracker = true;

        this.localParameters = param;


    }


    /**
     * field for Receiver
     */


    protected org.apache.axis2.transport.xsd.TransportListener localReceiver;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localReceiverTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.transport.xsd.TransportListener
     */
    public org.apache.axis2.transport.xsd.TransportListener getReceiver() {
        return this.localReceiver;
    }



    /**
     * Auto generated setter method
     *
     * @param param Receiver
     */
    public void setReceiver(final org.apache.axis2.transport.xsd.TransportListener param) {
        this.localReceiverTracker = true;

        this.localReceiver = param;


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


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                "http://description.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":TransportInDescription", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "TransportInDescription",
                    xmlWriter);
            }


        }
        if (this.localFaultFlowTracker) {
            if (this.localFaultFlow == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localFaultFlow.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultFlow"), xmlWriter);
            }
        }
        if (this.localFaultPhaseTracker) {
            if (this.localFaultPhase == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultPhase", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localFaultPhase.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultPhase"), xmlWriter);
            }
        }
        if (this.localInFlowTracker) {
            if (this.localInFlow == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "inFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localInFlow.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inFlow"), xmlWriter);
            }
        }
        if (this.localInPhaseTracker) {
            if (this.localInPhase == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "inPhase", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localInPhase.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inPhase"), xmlWriter);
            }
        }
        if (this.localNameTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localParametersTracker) {

            if (this.localParameters != null) {
                if (this.localParameters instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localParameters).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "parameters"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "parameters", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localParameters, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "parameters", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localReceiverTracker) {
            if (this.localReceiver == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "receiver", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localReceiver.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "receiver"), xmlWriter);
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

        if (this.localFaultFlowTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultFlow"));


            elementList.add(this.localFaultFlow == null ? null : this.localFaultFlow);
        }
        if (this.localFaultPhaseTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultPhase"));


            elementList.add(this.localFaultPhase == null ? null : this.localFaultPhase);
        }
        if (this.localInFlowTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inFlow"));


            elementList.add(this.localInFlow == null ? null : this.localInFlow);
        }
        if (this.localInPhaseTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inPhase"));


            elementList.add(this.localInPhase == null ? null : this.localInPhase);
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localName));
        }
        if (this.localParametersTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "parameters"));


            elementList.add(this.localParameters == null ? null : this.localParameters);
        }
        if (this.localReceiverTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "receiver"));


            elementList.add(this.localReceiver == null ? null : this.localReceiver);
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
        public static TransportInDescription parse(final javax.xml.stream.XMLStreamReader reader)
            throws java.lang.Exception {
            final TransportInDescription object = new TransportInDescription();

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

                        if (!"TransportInDescription".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (TransportInDescription) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                nsUri, type, reader);
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
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultFlow").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setFaultFlow(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setFaultFlow(org.apache.axis2.description.xsd.Flow.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultPhase").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setFaultPhase(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setFaultPhase(org.apache.axis2.engine.xsd.Phase.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inFlow").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setInFlow(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setInFlow(org.apache.axis2.description.xsd.Flow.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inPhase").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setInPhase(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setInPhase(org.apache.axis2.engine.xsd.Phase.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name").equals(
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
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "parameters").equals(
                        reader.getName())) {

                    object.setParameters(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "receiver").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setReceiver(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setReceiver(org.apache.axis2.transport.xsd.TransportListener.Factory.parse(reader));

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

