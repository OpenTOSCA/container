
/**
 * EventInfo.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * EventInfo bean class
 */

public class EventInfo implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = EventInfo Namespace URI =
     * http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = -5740310387651463371L;

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
     * field for Timestamp
     */


    protected java.util.Calendar localTimestamp;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getTimestamp() {
        return this.localTimestamp;
    }



    /**
     * Auto generated setter method
     *
     * @param param Timestamp
     */
    public void setTimestamp(final java.util.Calendar param) {

        this.localTimestamp = param;


    }


    /**
     * field for ScopeId
     */


    protected long localScopeId;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localScopeIdTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getScopeId() {
        return this.localScopeId;
    }



    /**
     * Auto generated setter method
     *
     * @param param ScopeId
     */
    public void setScopeId(final long param) {

        // setting primitive attribute tracker to true
        this.localScopeIdTracker = param != java.lang.Long.MIN_VALUE;

        this.localScopeId = param;


    }


    /**
     * field for ScopeName
     */


    protected java.lang.String localScopeName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localScopeNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getScopeName() {
        return this.localScopeName;
    }



    /**
     * Auto generated setter method
     *
     * @param param ScopeName
     */
    public void setScopeName(final java.lang.String param) {
        this.localScopeNameTracker = param != null;

        this.localScopeName = param;


    }


    /**
     * field for ActivityId
     */


    protected long localActivityId;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localActivityIdTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getActivityId() {
        return this.localActivityId;
    }



    /**
     * Auto generated setter method
     *
     * @param param ActivityId
     */
    public void setActivityId(final long param) {

        // setting primitive attribute tracker to true
        this.localActivityIdTracker = param != java.lang.Long.MIN_VALUE;

        this.localActivityId = param;


    }


    /**
     * field for ActivityName
     */


    protected java.lang.String localActivityName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localActivityNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getActivityName() {
        return this.localActivityName;
    }



    /**
     * Auto generated setter method
     *
     * @param param ActivityName
     */
    public void setActivityName(final java.lang.String param) {
        this.localActivityNameTracker = param != null;

        this.localActivityName = param;


    }


    /**
     * field for ActivityType
     */


    protected java.lang.String localActivityType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localActivityTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getActivityType() {
        return this.localActivityType;
    }



    /**
     * Auto generated setter method
     *
     * @param param ActivityType
     */
    public void setActivityType(final java.lang.String param) {
        this.localActivityTypeTracker = param != null;

        this.localActivityType = param;


    }


    /**
     * field for IsRecoveryRequired
     */


    protected boolean localIsRecoveryRequired;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localIsRecoveryRequiredTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getIsRecoveryRequired() {
        return this.localIsRecoveryRequired;
    }



    /**
     * Auto generated setter method
     *
     * @param param IsRecoveryRequired
     */
    public void setIsRecoveryRequired(final boolean param) {

        // setting primitive attribute tracker to true
        this.localIsRecoveryRequiredTracker = true;

        this.localIsRecoveryRequired = param;


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
                               namespacePrefix + ":EventInfo", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "EventInfo", xmlWriter);
            }


        }

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "name", xmlWriter);


        if (this.localName == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localName);

        }

        xmlWriter.writeEndElement();

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
        writeStartElement(null, namespace, "line-number", xmlWriter);

        if (this.localLineNumber == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("line-number cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "timestamp", xmlWriter);


        if (this.localTimestamp == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("timestamp cannot be null!!");

        } else {


            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTimestamp));

        }

        xmlWriter.writeEndElement();
        if (this.localScopeIdTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "scope-id", xmlWriter);

            if (this.localScopeId == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("scope-id cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localScopeId));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localScopeNameTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "scope-name", xmlWriter);


            if (this.localScopeName == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("scope-name cannot be null!!");

            } else {


                xmlWriter.writeCharacters(this.localScopeName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localActivityIdTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "activity-id", xmlWriter);

            if (this.localActivityId == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("activity-id cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActivityId));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localActivityNameTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "activity-name", xmlWriter);


            if (this.localActivityName == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("activity-name cannot be null!!");

            } else {


                xmlWriter.writeCharacters(this.localActivityName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localActivityTypeTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "activity-type", xmlWriter);


            if (this.localActivityType == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("activity-type cannot be null!!");

            } else {


                xmlWriter.writeCharacters(this.localActivityType);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localIsRecoveryRequiredTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "isRecoveryRequired", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("isRecoveryRequired cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localIsRecoveryRequired));
            }

            xmlWriter.writeEndElement();
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


        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "name"));

        if (this.localName != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
        } else {
            throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "type"));

        if (this.localType != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localType));
        } else {
            throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "line-number"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "timestamp"));

        if (this.localTimestamp != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTimestamp));
        } else {
            throw new org.apache.axis2.databinding.ADBException("timestamp cannot be null!!");
        }
        if (this.localScopeIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "scope-id"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localScopeId));
        }
        if (this.localScopeNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "scope-name"));

            if (this.localScopeName != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localScopeName));
            } else {
                throw new org.apache.axis2.databinding.ADBException("scope-name cannot be null!!");
            }
        }
        if (this.localActivityIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "activity-id"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActivityId));
        }
        if (this.localActivityNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "activity-name"));

            if (this.localActivityName != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActivityName));
            } else {
                throw new org.apache.axis2.databinding.ADBException("activity-name cannot be null!!");
            }
        }
        if (this.localActivityTypeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "activity-type"));

            if (this.localActivityType != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActivityType));
            } else {
                throw new org.apache.axis2.databinding.ADBException("activity-type cannot be null!!");
            }
        }
        if (this.localIsRecoveryRequiredTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "isRecoveryRequired"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localIsRecoveryRequired));
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
        public static EventInfo parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final EventInfo object = new EventInfo();

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

                        if (!"EventInfo".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (EventInfo) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
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
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException(
                        "Unexpected subelement " + reader.getLocalName());
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
                    "line-number").equals(reader.getName())) {

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "timestamp").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setTimestamp(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

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
                    "scope-id").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setScopeId(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setScopeId(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "scope-name").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setScopeName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "activity-id").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setActivityId(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setActivityId(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "activity-name").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setActivityName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "activity-type").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setActivityType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "isRecoveryRequired").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setIsRecoveryRequired(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

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



            }
            catch (final javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }// end of factory class



}

