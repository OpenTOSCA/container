
/**
 * InstanceInfoType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * InstanceInfoType bean class
 */

public class InstanceInfoType implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = InstanceInfoType Namespace URI =
     * http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = -8437408050417562227L;

    /**
     * field for Iid
     */


    protected java.lang.String localIid;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getIid() {
        return this.localIid;
    }



    /**
     * Auto generated setter method
     *
     * @param param Iid
     */
    public void setIid(final java.lang.String param) {

        this.localIid = param;


    }


    /**
     * field for Pid
     */


    protected java.lang.String localPid;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getPid() {
        return this.localPid;
    }



    /**
     * Auto generated setter method
     *
     * @param param Pid
     */
    public void setPid(final java.lang.String param) {

        this.localPid = param;


    }


    /**
     * field for RootScope
     */


    protected org.wso2.bps.management.schema.ScopeInfoType localRootScope;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRootScopeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.ScopeInfoType
     */
    public org.wso2.bps.management.schema.ScopeInfoType getRootScope() {
        return this.localRootScope;
    }



    /**
     * Auto generated setter method
     *
     * @param param RootScope
     */
    public void setRootScope(final org.wso2.bps.management.schema.ScopeInfoType param) {
        this.localRootScopeTracker = param != null;

        this.localRootScope = param;


    }


    /**
     * field for Status
     */


    protected org.wso2.bps.management.schema.InstanceStatus localStatus;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.InstanceStatus
     */
    public org.wso2.bps.management.schema.InstanceStatus getStatus() {
        return this.localStatus;
    }



    /**
     * Auto generated setter method
     *
     * @param param Status
     */
    public void setStatus(final org.wso2.bps.management.schema.InstanceStatus param) {

        this.localStatus = param;


    }


    /**
     * field for DateStarted
     */


    protected java.util.Calendar localDateStarted;


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

        this.localDateStarted = param;


    }


    /**
     * field for DateLastActive
     */


    protected java.util.Calendar localDateLastActive;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDateLastActiveTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getDateLastActive() {
        return this.localDateLastActive;
    }



    /**
     * Auto generated setter method
     *
     * @param param DateLastActive
     */
    public void setDateLastActive(final java.util.Calendar param) {
        this.localDateLastActiveTracker = param != null;

        this.localDateLastActive = param;


    }


    /**
     * field for DateErrorSince
     */


    protected java.util.Calendar localDateErrorSince;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDateErrorSinceTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getDateErrorSince() {
        return this.localDateErrorSince;
    }



    /**
     * Auto generated setter method
     *
     * @param param DateErrorSince
     */
    public void setDateErrorSince(final java.util.Calendar param) {
        this.localDateErrorSinceTracker = param != null;

        this.localDateErrorSince = param;


    }


    /**
     * field for FaultInfo
     */


    protected org.wso2.bps.management.schema.FaultInfoType localFaultInfo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultInfoTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.FaultInfoType
     */
    public org.wso2.bps.management.schema.FaultInfoType getFaultInfo() {
        return this.localFaultInfo;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultInfo
     */
    public void setFaultInfo(final org.wso2.bps.management.schema.FaultInfoType param) {
        this.localFaultInfoTracker = param != null;

        this.localFaultInfo = param;


    }


    /**
     * field for FailuresInfo
     */


    protected org.wso2.bps.management.schema.FailuresInfoType localFailuresInfo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFailuresInfoTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.FailuresInfoType
     */
    public org.wso2.bps.management.schema.FailuresInfoType getFailuresInfo() {
        return this.localFailuresInfo;
    }



    /**
     * Auto generated setter method
     *
     * @param param FailuresInfo
     */
    public void setFailuresInfo(final org.wso2.bps.management.schema.FailuresInfoType param) {
        this.localFailuresInfoTracker = param != null;

        this.localFailuresInfo = param;


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
                               namespacePrefix + ":InstanceInfoType", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "InstanceInfoType",
                               xmlWriter);
            }


        }

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "iid", xmlWriter);


        if (this.localIid == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("iid cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localIid);

        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "pid", xmlWriter);


        if (this.localPid == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("pid cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localPid);

        }

        xmlWriter.writeEndElement();
        if (this.localRootScopeTracker) {
            if (this.localRootScope == null) {
                throw new org.apache.axis2.databinding.ADBException("rootScope cannot be null!!");
            }
            this.localRootScope.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "rootScope"), xmlWriter);
        }
        if (this.localStatus == null) {
            throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
        }
        this.localStatus.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"),
                                   xmlWriter);

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "dateStarted", xmlWriter);


        if (this.localDateStarted == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("dateStarted cannot be null!!");

        } else {


            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateStarted));

        }

        xmlWriter.writeEndElement();
        if (this.localDateLastActiveTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "dateLastActive", xmlWriter);


            if (this.localDateLastActive == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("dateLastActive cannot be null!!");

            } else {


                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateLastActive));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localDateErrorSinceTracker) {
            namespace = "http://wso2.org/bps/management/schema";
            writeStartElement(null, namespace, "dateErrorSince", xmlWriter);


            if (this.localDateErrorSince == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("dateErrorSince cannot be null!!");

            } else {


                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateErrorSince));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultInfoTracker) {
            if (this.localFaultInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("faultInfo cannot be null!!");
            }
            this.localFaultInfo.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "faultInfo"), xmlWriter);
        }
        if (this.localFailuresInfoTracker) {
            if (this.localFailuresInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("failuresInfo cannot be null!!");
            }
            this.localFailuresInfo.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "failuresInfo"), xmlWriter);
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


        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "iid"));

        if (this.localIid != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localIid));
        } else {
            throw new org.apache.axis2.databinding.ADBException("iid cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "pid"));

        if (this.localPid != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPid));
        } else {
            throw new org.apache.axis2.databinding.ADBException("pid cannot be null!!");
        }
        if (this.localRootScopeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "rootScope"));


            if (this.localRootScope == null) {
                throw new org.apache.axis2.databinding.ADBException("rootScope cannot be null!!");
            }
            elementList.add(this.localRootScope);
        }
        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"));


        if (this.localStatus == null) {
            throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
        }
        elementList.add(this.localStatus);

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "dateStarted"));

        if (this.localDateStarted != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateStarted));
        } else {
            throw new org.apache.axis2.databinding.ADBException("dateStarted cannot be null!!");
        }
        if (this.localDateLastActiveTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "dateLastActive"));

            if (this.localDateLastActive != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateLastActive));
            } else {
                throw new org.apache.axis2.databinding.ADBException("dateLastActive cannot be null!!");
            }
        }
        if (this.localDateErrorSinceTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "dateErrorSince"));

            if (this.localDateErrorSince != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDateErrorSince));
            } else {
                throw new org.apache.axis2.databinding.ADBException("dateErrorSince cannot be null!!");
            }
        }
        if (this.localFaultInfoTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "faultInfo"));


            if (this.localFaultInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("faultInfo cannot be null!!");
            }
            elementList.add(this.localFaultInfo);
        }
        if (this.localFailuresInfoTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "failuresInfo"));


            if (this.localFailuresInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("failuresInfo cannot be null!!");
            }
            elementList.add(this.localFailuresInfo);
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
        public static InstanceInfoType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final InstanceInfoType object = new InstanceInfoType();

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

                        if (!"InstanceInfoType".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (InstanceInfoType) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
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
                    "iid").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setIid(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    "pid").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setPid(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    "rootScope").equals(reader.getName())) {

                    object.setRootScope(org.wso2.bps.management.schema.ScopeInfoType.Factory.parse(reader));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "status").equals(reader.getName())) {

                    object.setStatus(org.wso2.bps.management.schema.InstanceStatus.Factory.parse(reader));

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
                    "dateStarted").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDateStarted(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

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
                    "dateLastActive").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDateLastActive(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "dateErrorSince").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDateErrorSince(org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "faultInfo").equals(reader.getName())) {

                    object.setFaultInfo(org.wso2.bps.management.schema.FaultInfoType.Factory.parse(reader));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "failuresInfo").equals(reader.getName())) {

                    object.setFailuresInfo(org.wso2.bps.management.schema.FailuresInfoType.Factory.parse(reader));

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

