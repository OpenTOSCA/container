
/**
 * ConfigurationContext.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.context.xsd;


/**
 * ConfigurationContext bean class
 */

public class ConfigurationContext implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = ConfigurationContext Namespace
     * URI = http://context.axis2.apache.org/xsd Namespace Prefix = ns10
     */


    /**
     *
     */
    private static final long serialVersionUID = 4407325029690339128L;

    /**
     * field for AnyOperationContextRegistered
     */


    protected boolean localAnyOperationContextRegistered;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAnyOperationContextRegisteredTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getAnyOperationContextRegistered() {
        return this.localAnyOperationContextRegistered;
    }



    /**
     * Auto generated setter method
     *
     * @param param AnyOperationContextRegistered
     */
    public void setAnyOperationContextRegistered(final boolean param) {

        // setting primitive attribute tracker to true
        this.localAnyOperationContextRegisteredTracker = true;

        this.localAnyOperationContextRegistered = param;


    }


    /**
     * field for AxisConfiguration
     */


    protected org.apache.axis2.engine.xsd.AxisConfiguration localAxisConfiguration;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisConfigurationTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.AxisConfiguration
     */
    public org.apache.axis2.engine.xsd.AxisConfiguration getAxisConfiguration() {
        return this.localAxisConfiguration;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisConfiguration
     */
    public void setAxisConfiguration(final org.apache.axis2.engine.xsd.AxisConfiguration param) {
        this.localAxisConfigurationTracker = true;

        this.localAxisConfiguration = param;


    }


    /**
     * field for ContextRoot
     */


    protected java.lang.String localContextRoot;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localContextRootTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getContextRoot() {
        return this.localContextRoot;
    }



    /**
     * Auto generated setter method
     *
     * @param param ContextRoot
     */
    public void setContextRoot(final java.lang.String param) {
        this.localContextRootTracker = true;

        this.localContextRoot = param;


    }


    /**
     * field for ListenerManager
     */


    protected org.apache.axis2.engine.xsd.ListenerManager localListenerManager;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localListenerManagerTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.ListenerManager
     */
    public org.apache.axis2.engine.xsd.ListenerManager getListenerManager() {
        return this.localListenerManager;
    }



    /**
     * Auto generated setter method
     *
     * @param param ListenerManager
     */
    public void setListenerManager(final org.apache.axis2.engine.xsd.ListenerManager param) {
        this.localListenerManagerTracker = true;

        this.localListenerManager = param;


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
     * field for ServiceContextPath
     */


    protected java.lang.String localServiceContextPath;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceContextPathTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getServiceContextPath() {
        return this.localServiceContextPath;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceContextPath
     */
    public void setServiceContextPath(final java.lang.String param) {
        this.localServiceContextPathTracker = true;

        this.localServiceContextPath = param;


    }


    /**
     * field for ServiceGroupContextIDs This was an Array!
     */


    protected java.lang.String[] localServiceGroupContextIDs;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextIDsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getServiceGroupContextIDs() {
        return this.localServiceGroupContextIDs;
    }



    /**
     * validate the array for ServiceGroupContextIDs
     */
    protected void validateServiceGroupContextIDs(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContextIDs
     */
    public void setServiceGroupContextIDs(final java.lang.String[] param) {

        validateServiceGroupContextIDs(param);

        this.localServiceGroupContextIDsTracker = true;

        this.localServiceGroupContextIDs = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addServiceGroupContextIDs(final java.lang.String param) {
        if (this.localServiceGroupContextIDs == null) {
            this.localServiceGroupContextIDs = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localServiceGroupContextIDsTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(
            this.localServiceGroupContextIDs);
        list.add(param);
        this.localServiceGroupContextIDs = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }


    /**
     * field for ServiceGroupContextTimeoutInterval
     */


    protected long localServiceGroupContextTimeoutInterval;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextTimeoutIntervalTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getServiceGroupContextTimeoutInterval() {
        return this.localServiceGroupContextTimeoutInterval;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContextTimeoutInterval
     */
    public void setServiceGroupContextTimeoutInterval(final long param) {

        // setting primitive attribute tracker to true
        this.localServiceGroupContextTimeoutIntervalTracker = param != java.lang.Long.MIN_VALUE;

        this.localServiceGroupContextTimeoutInterval = param;


    }


    /**
     * field for ServiceGroupContextTimoutInterval
     */


    protected long localServiceGroupContextTimoutInterval;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextTimoutIntervalTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getServiceGroupContextTimoutInterval() {
        return this.localServiceGroupContextTimoutInterval;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContextTimoutInterval
     */
    public void setServiceGroupContextTimoutInterval(final long param) {

        // setting primitive attribute tracker to true
        this.localServiceGroupContextTimoutIntervalTracker = param != java.lang.Long.MIN_VALUE;

        this.localServiceGroupContextTimoutInterval = param;


    }


    /**
     * field for ServiceGroupContexts
     */


    protected java.lang.Object localServiceGroupContexts;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupContextsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getServiceGroupContexts() {
        return this.localServiceGroupContexts;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroupContexts
     */
    public void setServiceGroupContexts(final java.lang.Object param) {
        this.localServiceGroupContextsTracker = true;

        this.localServiceGroupContexts = param;


    }


    /**
     * field for ServicePath
     */


    protected java.lang.String localServicePath;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServicePathTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getServicePath() {
        return this.localServicePath;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServicePath
     */
    public void setServicePath(final java.lang.String param) {
        this.localServicePathTracker = true;

        this.localServicePath = param;


    }


    /**
     * field for ThreadPool
     */


    protected org.apache.axis2.util.threadpool.xsd.ThreadFactory localThreadPool;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localThreadPoolTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.util.threadpool.xsd.ThreadFactory
     */
    public org.apache.axis2.util.threadpool.xsd.ThreadFactory getThreadPool() {
        return this.localThreadPool;
    }



    /**
     * Auto generated setter method
     *
     * @param param ThreadPool
     */
    public void setThreadPool(final org.apache.axis2.util.threadpool.xsd.ThreadFactory param) {
        this.localThreadPoolTracker = true;

        this.localThreadPool = param;


    }


    /**
     * field for TransportManager
     */


    protected org.apache.axis2.engine.xsd.ListenerManager localTransportManager;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransportManagerTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.ListenerManager
     */
    public org.apache.axis2.engine.xsd.ListenerManager getTransportManager() {
        return this.localTransportManager;
    }



    /**
     * Auto generated setter method
     *
     * @param param TransportManager
     */
    public void setTransportManager(final org.apache.axis2.engine.xsd.ListenerManager param) {
        this.localTransportManagerTracker = true;

        this.localTransportManager = param;


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
                    namespacePrefix + ":ConfigurationContext", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ConfigurationContext",
                    xmlWriter);
            }


        }
        if (this.localAnyOperationContextRegisteredTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "anyOperationContextRegistered", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("anyOperationContextRegistered cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    this.localAnyOperationContextRegistered));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localAxisConfigurationTracker) {
            if (this.localAxisConfiguration == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisConfiguration", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisConfiguration.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisConfiguration"),
                    xmlWriter);
            }
        }
        if (this.localContextRootTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "contextRoot", xmlWriter);


            if (this.localContextRoot == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localContextRoot);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localListenerManagerTracker) {
            if (this.localListenerManager == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "listenerManager", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localListenerManager.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "listenerManager"), xmlWriter);
            }
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
        if (this.localServiceContextPathTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "serviceContextPath", xmlWriter);


            if (this.localServiceContextPath == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localServiceContextPath);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localServiceGroupContextIDsTracker) {
            if (this.localServiceGroupContextIDs != null) {
                namespace = "http://context.axis2.apache.org/xsd";
                for (final String localServiceGroupContextID : this.localServiceGroupContextIDs) {

                    if (localServiceGroupContextID != null) {

                        writeStartElement(null, namespace, "serviceGroupContextIDs", xmlWriter);


                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            localServiceGroupContextID));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://context.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "serviceGroupContextIDs", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContextIDs", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localServiceGroupContextTimeoutIntervalTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "serviceGroupContextTimeoutInterval", xmlWriter);

            if (this.localServiceGroupContextTimeoutInterval == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException(
                    "serviceGroupContextTimeoutInterval cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    this.localServiceGroupContextTimeoutInterval));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localServiceGroupContextTimoutIntervalTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "serviceGroupContextTimoutInterval", xmlWriter);

            if (this.localServiceGroupContextTimoutInterval == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException(
                    "serviceGroupContextTimoutInterval cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    this.localServiceGroupContextTimoutInterval));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localServiceGroupContextsTracker) {

            if (this.localServiceGroupContexts != null) {
                if (this.localServiceGroupContexts instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localServiceGroupContexts).serialize(
                        new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContexts"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContexts", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localServiceGroupContexts,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContexts", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localServicePathTracker) {
            namespace = "http://context.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "servicePath", xmlWriter);


            if (this.localServicePath == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localServicePath);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localThreadPoolTracker) {
            if (this.localThreadPool == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "threadPool", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localThreadPool.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "threadPool"), xmlWriter);
            }
        }
        if (this.localTransportManagerTracker) {
            if (this.localTransportManager == null) {

                writeStartElement(null, "http://context.axis2.apache.org/xsd", "transportManager", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTransportManager.serialize(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportManager"),
                    xmlWriter);
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

        if (this.localAnyOperationContextRegisteredTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "anyOperationContextRegistered"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                this.localAnyOperationContextRegistered));
        }
        if (this.localAxisConfigurationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisConfiguration"));


            elementList.add(this.localAxisConfiguration == null ? null : this.localAxisConfiguration);
        }
        if (this.localContextRootTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "contextRoot"));

            elementList.add(
                this.localContextRoot == null ? null
                                              : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                  this.localContextRoot));
        }
        if (this.localListenerManagerTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "listenerManager"));


            elementList.add(this.localListenerManager == null ? null : this.localListenerManager);
        }
        if (this.localRootContextTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "rootContext"));


            elementList.add(this.localRootContext == null ? null : this.localRootContext);
        }
        if (this.localServiceContextPathTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceContextPath"));

            elementList.add(
                this.localServiceContextPath == null ? null
                                                     : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                         this.localServiceContextPath));
        }
        if (this.localServiceGroupContextIDsTracker) {
            if (this.localServiceGroupContextIDs != null) {
                for (final String localServiceGroupContextID : this.localServiceGroupContextIDs) {

                    if (localServiceGroupContextID != null) {
                        elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                            "serviceGroupContextIDs"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            localServiceGroupContextID));
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                            "serviceGroupContextIDs"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(
                    new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContextIDs"));
                elementList.add(null);

            }

        }
        if (this.localServiceGroupContextTimeoutIntervalTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                "serviceGroupContextTimeoutInterval"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                this.localServiceGroupContextTimeoutInterval));
        }
        if (this.localServiceGroupContextTimoutIntervalTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                "serviceGroupContextTimoutInterval"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                this.localServiceGroupContextTimoutInterval));
        }
        if (this.localServiceGroupContextsTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "serviceGroupContexts"));


            elementList.add(this.localServiceGroupContexts == null ? null : this.localServiceGroupContexts);
        }
        if (this.localServicePathTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "servicePath"));

            elementList.add(
                this.localServicePath == null ? null
                                              : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                  this.localServicePath));
        }
        if (this.localThreadPoolTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "threadPool"));


            elementList.add(this.localThreadPool == null ? null : this.localThreadPool);
        }
        if (this.localTransportManagerTracker) {
            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportManager"));


            elementList.add(this.localTransportManager == null ? null : this.localTransportManager);
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
        public static ConfigurationContext parse(final javax.xml.stream.XMLStreamReader reader)
            throws java.lang.Exception {
            final ConfigurationContext object = new ConfigurationContext();

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

                        if (!"ConfigurationContext".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (ConfigurationContext) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                nsUri, type, reader);
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "anyOperationContextRegistered").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setAnyOperationContextRegistered(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "axisConfiguration").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisConfiguration(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisConfiguration(
                            org.apache.axis2.engine.xsd.AxisConfiguration.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "contextRoot").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setContextRoot(
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
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "listenerManager").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setListenerManager(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setListenerManager(org.apache.axis2.engine.xsd.ListenerManager.Factory.parse(reader));

                        reader.next();
                    }
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
                    "serviceContextPath").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setServiceContextPath(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContextIDs").equals(reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list7.add(null);

                        reader.next();
                    } else {
                        list7.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone7 = false;
                    while (!loopDone7) {
                        // Ensure we are at the EndElement
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
                            if (new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                "serviceGroupContextIDs").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list7.add(null);

                                    reader.next();
                                } else {
                                    list7.add(reader.getElementText());
                                }
                            } else {
                                loopDone7 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setServiceGroupContextIDs(
                        (java.lang.String[]) list7.toArray(new java.lang.String[list7.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContextTimeoutInterval").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setServiceGroupContextTimeoutInterval(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setServiceGroupContextTimeoutInterval(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContextTimoutInterval").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setServiceGroupContextTimoutInterval(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setServiceGroupContextTimoutInterval(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                    "serviceGroupContexts").equals(reader.getName())) {

                    object.setServiceGroupContexts(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "servicePath").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setServicePath(
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
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "threadPool").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setThreadPool(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setThreadPool(org.apache.axis2.util.threadpool.xsd.ThreadFactory.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd", "transportManager").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTransportManager(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTransportManager(org.apache.axis2.engine.xsd.ListenerManager.Factory.parse(reader));

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

