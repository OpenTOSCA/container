
/**
 * FaultInfoType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * FaultInfoType bean class
 */

public class FaultInfoType implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = FaultInfoType Namespace URI =
     * http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = 6962379999326731464L;

    /**
     * field for Name
     */


    protected javax.xml.namespace.QName localName;


    /**
     * Auto generated getter method
     *
     * @return javax.xml.namespace.QName
     */
    public javax.xml.namespace.QName getName() {
        return this.localName;
    }



    /**
     * Auto generated setter method
     *
     * @param param Name
     */
    public void setName(final javax.xml.namespace.QName param) {

        this.localName = param;


    }


    /**
     * field for Explanation
     */


    protected java.lang.String localExplanation;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExplanationTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getExplanation() {
        return this.localExplanation;
    }



    /**
     * Auto generated setter method
     *
     * @param param Explanation
     */
    public void setExplanation(final java.lang.String param) {
        this.localExplanationTracker = param != null;

        this.localExplanation = param;


    }


    /**
     * field for LineNumber
     */


    protected int localLineNumber;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getLineNumber() {
        return this.localLineNumber;
    }



    /**
     * Auto generated setter method
     *
     * @param param LineNumber
     */
    public void setLineNumber(final int param) {

        this.localLineNumber = param;


    }


    /**
     * field for Aiid
     */


    protected int localAiid;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getAiid() {
        return this.localAiid;
    }



    /**
     * Auto generated setter method
     *
     * @param param Aiid
     */
    public void setAiid(final int param) {

        this.localAiid = param;


    }


    /**
     * field for Data
     */


    protected org.wso2.bps.management.schema.Data_type0 localData;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDataTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.Data_type0
     */
    public org.wso2.bps.management.schema.Data_type0 getData() {
        return this.localData;
    }



    /**
     * Auto generated setter method
     *
     * @param param Data
     */
    public void setData(final org.wso2.bps.management.schema.Data_type0 param) {
        this.localDataTracker = param != null;

        this.localData = param;


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


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://wso2.org/bps/management/schema");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":FaultInfoType", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "FaultInfoType", xmlWriter);
            }


        }

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "name", xmlWriter);


        if (this.localName == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");

        } else {


            writeQName(this.localName, xmlWriter);

        }

        xmlWriter.writeEndElement();
        if (this.localExplanationTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "explanation", xmlWriter);


            if (this.localExplanation == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("explanation cannot be null!!");

            } else {


                xmlWriter.writeCharacters(this.localExplanation);

            }

            xmlWriter.writeEndElement();
        }
        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "lineNumber", xmlWriter);

        if (this.localLineNumber == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("lineNumber cannot be null!!");

        } else {
            xmlWriter.writeCharacters(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "aiid", xmlWriter);

        if (this.localAiid == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("aiid cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localAiid));
        }

        xmlWriter.writeEndElement();
        if (this.localDataTracker) {
            if (this.localData == null) {
                throw new org.apache.axis2.databinding.ADBException("data cannot be null!!");
            }
            this.localData.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "data"),
                xmlWriter);
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


        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "name"));

        if (this.localName != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
        } else {
            throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");
        }
        if (this.localExplanationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "explanation"));

            if (this.localExplanation != null) {
                elementList.add(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localExplanation));
            } else {
                throw new org.apache.axis2.databinding.ADBException("explanation cannot be null!!");
            }
        }
        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "lineNumber"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "aiid"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localAiid));
        if (this.localDataTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "data"));


            if (this.localData == null) {
                throw new org.apache.axis2.databinding.ADBException("data cannot be null!!");
            }
            elementList.add(this.localData);
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
        public static FaultInfoType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final FaultInfoType object = new FaultInfoType();

            final int event;
            final java.lang.String nillableValue = null;
            java.lang.String prefix = "";
            java.lang.String namespaceuri = "";
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

                        if (!"FaultInfoType".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (FaultInfoType) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(
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
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "name").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    final int index = content.indexOf(":");
                    if (index > 0) {
                        prefix = content.substring(0, index);
                    } else {
                        prefix = "";
                    }
                    namespaceuri = reader.getNamespaceURI(prefix);
                    object.setName(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(content, namespaceuri));

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "explanation").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setExplanation(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "lineNumber").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setLineNumber(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "aiid").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setAiid(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "data").equals(
                        reader.getName())) {

                    object.setData(org.wso2.bps.management.schema.Data_type0.Factory.parse(reader));

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

