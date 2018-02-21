
/**
 * ActivityInfoType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * ActivityInfoType bean class
 */

public class ActivityInfoType implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = ActivityInfoType Namespace URI =
     * http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = 5072654299462275373L;

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
        this.localNameTracker = param != null;

        this.localName = param;


    }


    /**
     * field for Type
     */


    protected java.lang.String localType;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getType() {
        return this.localType;
    }



    /**
     * Auto generated setter method
     *
     * @param param Type
     */
    public void setType(final java.lang.String param) {

        this.localType = param;


    }


    /**
     * field for Aiid
     */


    protected java.lang.String localAiid;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getAiid() {
        return this.localAiid;
    }



    /**
     * Auto generated setter method
     *
     * @param param Aiid
     */
    public void setAiid(final java.lang.String param) {

        this.localAiid = param;


    }


    /**
     * field for Status
     */


    protected org.wso2.bps.management.schema.ActivityStatusType localStatus;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.ActivityStatusType
     */
    public org.wso2.bps.management.schema.ActivityStatusType getStatus() {
        return this.localStatus;
    }



    /**
     * Auto generated setter method
     *
     * @param param Status
     */
    public void setStatus(final org.wso2.bps.management.schema.ActivityStatusType param) {

        this.localStatus = param;


    }


    /**
     * field for DateEnabled
     */


    protected java.util.Calendar localDateEnabled;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDateEnabledTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getDateEnabled() {
        return this.localDateEnabled;
    }



    /**
     * Auto generated setter method
     *
     * @param param DateEnabled
     */
    public void setDateEnabled(final java.util.Calendar param) {
        this.localDateEnabledTracker = param != null;

        this.localDateEnabled = param;


    }


    /**
     * field for DateStarted
     */


    protected java.util.Calendar localDateStarted;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDateStartedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getDateStarted() {
        return this.localDateStarted;
    }



    /**
     * Auto generated setter method
     *
     * @param param DateStarted
     */
    public void setDateStarted(final java.util.Calendar param) {
        this.localDateStartedTracker = param != null;

        this.localDateStarted = param;


    }


    /**
     * field for DateCompleted
     */


    protected java.util.Calendar localDateCompleted;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDateCompletedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getDateCompleted() {
        return this.localDateCompleted;
    }



    /**
     * Auto generated setter method
     *
     * @param param DateCompleted
     */
    public void setDateCompleted(final java.util.Calendar param) {
        this.localDateCompletedTracker = param != null;

        this.localDateCompleted = param;


    }


    /**
     * field for Datedied
     */


    protected java.util.Calendar localDatedied;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDatediedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getDatedied() {
        return this.localDatedied;
    }



    /**
     * Auto generated setter method
     *
     * @param param Datedied
     */
    public void setDatedied(final java.util.Calendar param) {
        this.localDatediedTracker = param != null;

        this.localDatedied = param;


    }


    /**
     * field for Failure
     */


    protected org.wso2.bps.management.schema.FailureInfoType localFailure;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFailureTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.FailureInfoType
     */
    public org.wso2.bps.management.schema.FailureInfoType getFailure() {
        return this.localFailure;
    }



    /**
     * Auto generated setter method
     *
     * @param param Failure
     */
    public void setFailure(final org.wso2.bps.management.schema.FailureInfoType param) {
        this.localFailureTracker = param != null;

        this.localFailure = param;


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
                               namespacePrefix + ":ActivityInfoType", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ActivityInfoType",
                               xmlWriter);
            }


        }
        if (this.localNameTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "type", xmlWriter);


        if (this.localType == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localType);

        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "aiid", xmlWriter);


        if (this.localAiid == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("aiid cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localAiid);

        }

        xmlWriter.writeEndElement();

        if (this.localStatus == null) {
            throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
        }
        this.localStatus.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"),
                                   xmlWriter);
        if (this.localDateEnabledTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "dateEnabled", xmlWriter);


            if (this.localDateEnabled == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("dateEnabled cannot be null!!");

            } else {


                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateEnabled));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localDateStartedTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "dateStarted", xmlWriter);


            if (this.localDateStarted == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("dateStarted cannot be null!!");

            } else {


                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateStarted));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localDateCompletedTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "dateCompleted", xmlWriter);


            if (this.localDateCompleted == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("dateCompleted cannot be null!!");

            } else {


                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateCompleted));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localDatediedTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "datedied", xmlWriter);


            if (this.localDatedied == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("datedied cannot be null!!");

            } else {


                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDatedied));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFailureTracker) {
            if (this.localFailure == null) {
                throw new org.apache.axis2.databinding.ADBException("failure cannot be null!!");
            }
            this.localFailure.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "failure"), xmlWriter);
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

        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "name"));

            if (this.localName != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
            } else {
                throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");
            }
        }
        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "type"));

        if (this.localType != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localType));
        } else {
            throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "aiid"));

        if (this.localAiid != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localAiid));
        } else {
            throw new org.apache.axis2.databinding.ADBException("aiid cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"));


        if (this.localStatus == null) {
            throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
        }
        elementList.add(this.localStatus);
        if (this.localDateEnabledTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "dateEnabled"));

            if (this.localDateEnabled != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateEnabled));
            } else {
                throw new org.apache.axis2.databinding.ADBException("dateEnabled cannot be null!!");
            }
        }
        if (this.localDateStartedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "dateStarted"));

            if (this.localDateStarted != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateStarted));
            } else {
                throw new org.apache.axis2.databinding.ADBException("dateStarted cannot be null!!");
            }
        }
        if (this.localDateCompletedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "dateCompleted"));

            if (this.localDateCompleted != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateCompleted));
            } else {
                throw new org.apache.axis2.databinding.ADBException("dateCompleted cannot be null!!");
            }
        }
        if (this.localDatediedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "datedied"));

            if (this.localDatedied != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDatedied));
            } else {
                throw new org.apache.axis2.databinding.ADBException("datedied cannot be null!!");
            }
        }
        if (this.localFailureTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "failure"));


            if (this.localFailure == null) {
                throw new org.apache.axis2.databinding.ADBException("failure cannot be null!!");
            }
            elementList.add(this.localFailure);
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
        public static ActivityInfoType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final ActivityInfoType object = new ActivityInfoType();

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

                        if (!"ActivityInfoType".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (ActivityInfoType) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
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
                    "name").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "type").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    "aiid").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setAiid(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                    object.setStatus(org.wso2.bps.management.schema.ActivityStatusType.Factory.parse(reader));

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
                    "dateEnabled").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDateEnabled(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "dateStarted").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDateStarted(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "dateCompleted").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDateCompleted(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "datedied").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDatedied(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "failure").equals(reader.getName())) {

                    object.setFailure(org.wso2.bps.management.schema.FailureInfoType.Factory.parse(reader));

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



            }
            catch (final javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }// end of factory class



}

