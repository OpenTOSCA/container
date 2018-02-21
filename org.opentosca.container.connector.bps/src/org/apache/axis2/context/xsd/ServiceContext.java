
/**
 * ServiceContext.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.context.xsd;


/**
 * ServiceContext bean class
 */

public class ServiceContext implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = ServiceContext Namespace URI =
     * http://context.axis2.apache.org/xsd Namespace Prefix = ns10
     */


    /**
     *
     */
    private static final long serialVersionUID = -5976323813817411394L;

    /**
     * field for AxisService
     */


    protected org.apache.axis2.description.xsd.AxisService localAxisService;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisServiceTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.AxisService
     */
    public org.apache.axis2.description.xsd.AxisService getAxisService() {
        return this.localAxisService;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisService
     */
    public void setAxisService(final org.apache.axis2.description.xsd.AxisService param) {
        this.localAxisServiceTracker = true;

        this.localAxisService = param;


    }


    /**
     * field for CachingOperationContext
     */


    protected boolean localCachingOperationContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localCachingOperationContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getCachingOperationContext() {
        return this.localCachingOperationContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param CachingOperationContext
     */
    public void setCachingOperationContext(final boolean param) {

        // setting primitive attribute tracker to true
        this.localCachingOperationContextTracker = true;

        this.localCachingOperationContext = param;


    }


    /**
     * field for ConfigurationContext
     */


    protected org.apache.axis2.context.xsd.ConfigurationContext localConfigurationContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localConfigurationContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ConfigurationContext
     */
    public org.apache.axis2.context.xsd.ConfigurationContext getConfigurationContext() {
        return this.localConfigurationContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param ConfigurationContext
     */
    public void setConfigurationContext(final org.apache.axis2.context.xsd.ConfigurationContext param) {
        this.localConfigurationContextTracker = true;

        this.localConfigurationContext = param;


    }


    /**
     * field for GroupName
     */


    protected java.lang.String localGroupName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localGroupNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getGroupName() {
        return this.localGroupName;
    }



    /**
     * Auto generated setter method
     *
     * @param param GroupName
     */
    public void setGroupName(final java.lang.String param) {
        this.localGroupNameTracker = true;

        this.localGroupName = param;


    }


    /**
     * field for LastOperationContext
     */


    protected org.apache.axis2.context.xsd.OperationContext localLastOperationContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localLastOperationContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.OperationContext
     */
    public org.apache.axis2.context.xsd.OperationContext getLastOperationContext() {
        return this.localLastOperationContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param LastOperationContext
     */
    public void setLastOperationContext(final org.apache.axis2.context.xsd.OperationContext param) {
        this.localLastOperationContextTracker = true;

        this.localLastOperationContext = param;


    }


    /**
     * field for LogCorrelationIDString
     */


    protected java.lang.String localLogCorrelationIDString;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localLogCorrelationIDStringTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getLogCorrelationIDString() {
        return this.localLogCorrelationIDString;
    }



    /**
     * Auto generated setter method
     *
     * @param param LogCorrelationIDString
     */
    public void setLogCorrelationIDString(final java.lang.String param) {
        this.localLogCorrelationIDStringTracker = true;

        this.localLogCorrelationIDString = param;


    }


    /**
     * field for MyEPR
     */


    protected org.apache.axis2.addressing.xsd.EndpointReference localMyEPR;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMyEPRTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.EndpointReference
     */
    public org.apache.axis2.addressing.xsd.EndpointReference getMyEPR() {
        return this.localMyEPR;
    }



    /**
     * Auto generated setter method
     *
     * @param param MyEPR
     */
    public void setMyEPR(final org.apache.axis2.addressing.xsd.EndpointReference param) {
        this.localMyEPRTracker = true;

        this.localMyEPR = param;


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
     * field for RootContext
     */


    protected org.apache.axis2.context.xsd.ConfigurationContext localRootContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRootContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ConfigurationContext
     */
    public org.apache.axis2.context.xsd.ConfigurationContext getRootContext() {
        return this.localRootContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param RootContext
     */
    public void setRootContext(final org.apache.axis2.context.xsd.ConfigurationContext param) {
        this.localRootContextTracker = true;

        this.localRootContext = param;


    }


    /**
     * field for ServiceGroupContext
     */


    protected org.apache.axis2.context.xsd.ServiceGroupContext localServiceGroupContext;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.context.xsd.ServiceGroupContext
     */
    public org.apache.axis2.context.xsd.ServiceGroupContext getServiceGroupContext() {
        return this.localServiceGroupContext;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContext
     */
    public void setServiceGroupContext(final org.apache.axis2.context.xsd.ServiceGroupContext param) {
        this.localServiceGroupContextTracker = true;

        this.localServiceGroupContext = param;


    }


    /**
     * field for TargetEPR
     */


    protected org.apache.axis2.addressing.xsd.EndpointReference localTargetEPR;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTargetEPRTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.addressing.xsd.EndpointReference
     */
    public org.apache.axis2.addressing.xsd.EndpointReference getTargetEPR() {
        return this.localTargetEPR;
    }



    /**
     * Auto generated setter method
     *
     * @param param TargetEPR
     */
    public void setTargetEPR(final org.apache.axis2.addressing.xsd.EndpointReference param) {
        this.localTargetEPRTracker = true;

        this.localTargetEPR = param;


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


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://context.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":ServiceContext", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ServiceContext", xmlWriter);
            }


        }
        if (this.localAxisServiceTracker) {
            if (this.localAxisService == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisService", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisService.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisService"), xmlWriter);
            }
        }
        if (this.localCachingOperationContextTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "cachingOperationContext", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("cachingOperationContext cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    this.localCachingOperationContext));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localConfigurationContextTracker) {
            if (this.localConfigurationContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "configurationContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localConfigurationContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "configurationContext"),
                    xmlWriter);
            }
        }
        if (this.localGroupNameTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "groupName", xmlWriter);


            if (this.localGroupName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localGroupName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localLastOperationContextTracker) {
            if (this.localLastOperationContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "lastOperationContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localLastOperationContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "lastOperationContext"),
                    xmlWriter);
            }
        }
        if (this.localLogCorrelationIDStringTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "logCorrelationIDString", xmlWriter);


            if (this.localLogCorrelationIDString == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localLogCorrelationIDString);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localMyEPRTracker) {
            if (this.localMyEPR == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "myEPR", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localMyEPR.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "myEPR"),
                    xmlWriter);
            }
        }
        if (this.localNameTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localRootContextTracker) {
            if (this.localRootContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "rootContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localRootContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext"), xmlWriter);
            }
        }
        if (this.localServiceGroupContextTracker) {
            if (this.localServiceGroupContext == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContext", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localServiceGroupContext.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContext"),
                    xmlWriter);
            }
        }
        if (this.localTargetEPRTracker) {
            if (this.localTargetEPR == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "targetEPR", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTargetEPR.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "targetEPR"), xmlWriter);
            }
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://context.axis2.apache.org/xsd")) {
            return "ns10";
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

        if (this.localAxisServiceTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisService"));


            elementList.add(this.localAxisService == null ? null : this.localAxisService);
        }
        if (this.localCachingOperationContextTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "cachingOperationContext"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCachingOperationContext));
        }
        if (this.localConfigurationContextTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "configurationContext"));


            elementList.add(this.localConfigurationContext == null ? null : this.localConfigurationContext);
        }
        if (this.localGroupNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "groupName"));

            elementList.add(
                this.localGroupName == null ? null
                                            : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                this.localGroupName));
        }
        if (this.localLastOperationContextTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "lastOperationContext"));


            elementList.add(this.localLastOperationContext == null ? null : this.localLastOperationContext);
        }
        if (this.localLogCorrelationIDStringTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "logCorrelationIDString"));

            elementList.add(
                this.localLogCorrelationIDString == null ? null
                                                         : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                             this.localLogCorrelationIDString));
        }
        if (this.localMyEPRTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "myEPR"));


            elementList.add(this.localMyEPR == null ? null : this.localMyEPR);
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localName));
        }
        if (this.localRootContextTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext"));


            elementList.add(this.localRootContext == null ? null : this.localRootContext);
        }
        if (this.localServiceGroupContextTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContext"));


            elementList.add(this.localServiceGroupContext == null ? null : this.localServiceGroupContext);
        }
        if (this.localTargetEPRTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "targetEPR"));


            elementList.add(this.localTargetEPR == null ? null : this.localTargetEPR);
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
        public static ServiceContext parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final ServiceContext object = new ServiceContext();

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

                        if (!"ServiceContext".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (ServiceContext) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
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
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisService").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisService(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisService(org.apache.axis2.description.xsd.AxisService.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "cachingOperationContext").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setCachingOperationContext(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "configurationContext").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setConfigurationContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setConfigurationContext(
                            org.apache.axis2.context.xsd.ConfigurationContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "groupName").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setGroupName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "lastOperationContext").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setLastOperationContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setLastOperationContext(
                            org.apache.axis2.context.xsd.OperationContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "logCorrelationIDString").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setLogCorrelationIDString(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "myEPR").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setMyEPR(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setMyEPR(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "name").equals(
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
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setRootContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setRootContext(org.apache.axis2.context.xsd.ConfigurationContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContext").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setServiceGroupContext(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setServiceGroupContext(
                            org.apache.axis2.context.xsd.ServiceGroupContext.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "targetEPR").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTargetEPR(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTargetEPR(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));

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

