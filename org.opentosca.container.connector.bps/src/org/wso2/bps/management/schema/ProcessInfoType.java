
/**
 * ProcessInfoType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

/**
 * ProcessInfoType bean class
 */

public class ProcessInfoType implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = ProcessInfoType Namespace URI =
     * http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = 8513222610797331327L;

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
     * field for Version
     */


    protected long localVersion;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getVersion() {
        return this.localVersion;
    }



    /**
     * Auto generated setter method
     *
     * @param param Version
     */
    public void setVersion(final long param) {

        this.localVersion = param;


    }


    /**
     * field for Status
     */


    protected org.wso2.bps.management.schema.ProcessStatus localStatus;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.ProcessStatus
     */
    public org.wso2.bps.management.schema.ProcessStatus getStatus() {
        return this.localStatus;
    }



    /**
     * Auto generated setter method
     *
     * @param param Status
     */
    public void setStatus(final org.wso2.bps.management.schema.ProcessStatus param) {

        this.localStatus = param;


    }


    /**
     * field for OlderVersion
     */


    protected int localOlderVersion;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getOlderVersion() {
        return this.localOlderVersion;
    }



    /**
     * Auto generated setter method
     *
     * @param param OlderVersion
     */
    public void setOlderVersion(final int param) {

        this.localOlderVersion = param;


    }


    /**
     * field for DefinitionInfo
     */


    protected org.wso2.bps.management.schema.DefinitionInfo localDefinitionInfo;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.DefinitionInfo
     */
    public org.wso2.bps.management.schema.DefinitionInfo getDefinitionInfo() {
        return this.localDefinitionInfo;
    }



    /**
     * Auto generated setter method
     *
     * @param param DefinitionInfo
     */
    public void setDefinitionInfo(final org.wso2.bps.management.schema.DefinitionInfo param) {

        this.localDefinitionInfo = param;


    }


    /**
     * field for DeploymentInfo
     */


    protected org.wso2.bps.management.schema.DeploymentInfo localDeploymentInfo;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.DeploymentInfo
     */
    public org.wso2.bps.management.schema.DeploymentInfo getDeploymentInfo() {
        return this.localDeploymentInfo;
    }



    /**
     * Auto generated setter method
     *
     * @param param DeploymentInfo
     */
    public void setDeploymentInfo(final org.wso2.bps.management.schema.DeploymentInfo param) {

        this.localDeploymentInfo = param;


    }


    /**
     * field for InstanceSummary
     */


    protected org.wso2.bps.management.schema.InstanceSummary localInstanceSummary;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInstanceSummaryTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.InstanceSummary
     */
    public org.wso2.bps.management.schema.InstanceSummary getInstanceSummary() {
        return this.localInstanceSummary;
    }



    /**
     * Auto generated setter method
     *
     * @param param InstanceSummary
     */
    public void setInstanceSummary(final org.wso2.bps.management.schema.InstanceSummary param) {
        this.localInstanceSummaryTracker = param != null;

        this.localInstanceSummary = param;


    }


    /**
     * field for Properties
     */


    protected org.wso2.bps.management.schema.ProcessProperties localProperties;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPropertiesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.ProcessProperties
     */
    public org.wso2.bps.management.schema.ProcessProperties getProperties() {
        return this.localProperties;
    }



    /**
     * Auto generated setter method
     *
     * @param param Properties
     */
    public void setProperties(final org.wso2.bps.management.schema.ProcessProperties param) {
        this.localPropertiesTracker = param != null;

        this.localProperties = param;


    }


    /**
     * field for Endpoints
     */


    protected org.wso2.bps.management.schema.EndpointReferencesType localEndpoints;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.EndpointReferencesType
     */
    public org.wso2.bps.management.schema.EndpointReferencesType getEndpoints() {
        return this.localEndpoints;
    }



    /**
     * Auto generated setter method
     *
     * @param param Endpoints
     */
    public void setEndpoints(final org.wso2.bps.management.schema.EndpointReferencesType param) {

        this.localEndpoints = param;


    }


    /**
     * field for ExtraElement This was an Array!
     */


    protected org.apache.axiom.om.OMElement[] localExtraElement;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExtraElementTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMElement[]
     */
    public org.apache.axiom.om.OMElement[] getExtraElement() {
        return this.localExtraElement;
    }



    /**
     * validate the array for ExtraElement
     */
    protected void validateExtraElement(final org.apache.axiom.om.OMElement[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param ExtraElement
     */
    public void setExtraElement(final org.apache.axiom.om.OMElement[] param) {

        validateExtraElement(param);

        this.localExtraElementTracker = param != null;

        this.localExtraElement = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axiom.om.OMElement
     */
    public void addExtraElement(final org.apache.axiom.om.OMElement param) {
        if (this.localExtraElement == null) {
            this.localExtraElement = new org.apache.axiom.om.OMElement[] {};
        }


        // update the setting tracker
        this.localExtraElementTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localExtraElement);
        list.add(param);
        this.localExtraElement =
            (org.apache.axiom.om.OMElement[]) list.toArray(new org.apache.axiom.om.OMElement[list.size()]);

    }


    /**
     * field for ExtraAttributes This was an Attribute! This was an Array!
     */


    protected org.apache.axiom.om.OMAttribute[] localExtraAttributes;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMAttribute[]
     */
    public org.apache.axiom.om.OMAttribute[] getExtraAttributes() {
        return this.localExtraAttributes;
    }



    /**
     * validate the array for ExtraAttributes
     */
    protected void validateExtraAttributes(final org.apache.axiom.om.OMAttribute[] param) {

        if (param != null && param.length > 1) {
            throw new java.lang.RuntimeException();
        }

        if (param != null && param.length < 1) {
            throw new java.lang.RuntimeException();
        }

    }


    /**
     * Auto generated setter method
     *
     * @param param ExtraAttributes
     */
    public void setExtraAttributes(final org.apache.axiom.om.OMAttribute[] param) {

        validateExtraAttributes(param);


        this.localExtraAttributes = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axiom.om.OMAttribute
     */
    public void addExtraAttributes(final org.apache.axiom.om.OMAttribute param) {
        if (this.localExtraAttributes == null) {
            this.localExtraAttributes = new org.apache.axiom.om.OMAttribute[] {};
        }



        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localExtraAttributes);
        list.add(param);
        this.localExtraAttributes =
            (org.apache.axiom.om.OMAttribute[]) list.toArray(new org.apache.axiom.om.OMAttribute[list.size()]);

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
                               namespacePrefix + ":ProcessInfoType", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ProcessInfoType",
                               xmlWriter);
            }


        }

        if (this.localExtraAttributes != null) {
            for (final OMAttribute localExtraAttribute : this.localExtraAttributes) {
                writeAttribute(localExtraAttribute.getNamespace().getName(), localExtraAttribute.getLocalName(),
                               localExtraAttribute.getAttributeValue(), xmlWriter);
            }
        }

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "pid", xmlWriter);


        if (this.localPid == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("pid cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localPid);

        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "version", xmlWriter);

        if (this.localVersion == java.lang.Long.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("version cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localVersion));
        }

        xmlWriter.writeEndElement();

        if (this.localStatus == null) {
            throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
        }
        this.localStatus.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"),
                                   xmlWriter);

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "olderVersion", xmlWriter);

        if (this.localOlderVersion == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("olderVersion cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localOlderVersion));
        }

        xmlWriter.writeEndElement();

        if (this.localDefinitionInfo == null) {
            throw new org.apache.axis2.databinding.ADBException("definitionInfo cannot be null!!");
        }
        this.localDefinitionInfo.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
            "definitionInfo"), xmlWriter);

        if (this.localDeploymentInfo == null) {
            throw new org.apache.axis2.databinding.ADBException("deploymentInfo cannot be null!!");
        }
        this.localDeploymentInfo.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
            "deploymentInfo"), xmlWriter);
        if (this.localInstanceSummaryTracker) {
            if (this.localInstanceSummary == null) {
                throw new org.apache.axis2.databinding.ADBException("instanceSummary cannot be null!!");
            }
            this.localInstanceSummary.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "instanceSummary"), xmlWriter);
        }
        if (this.localPropertiesTracker) {
            if (this.localProperties == null) {
                throw new org.apache.axis2.databinding.ADBException("properties cannot be null!!");
            }
            this.localProperties.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                "properties"), xmlWriter);
        }
        if (this.localEndpoints == null) {
            throw new org.apache.axis2.databinding.ADBException("endpoints cannot be null!!");
        }
        this.localEndpoints.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
            "endpoints"), xmlWriter);
        if (this.localExtraElementTracker) {

            if (this.localExtraElement != null) {
                for (final OMElement element : this.localExtraElement) {
                    if (element != null) {
                        element.serialize(xmlWriter);
                    } else {

                        // we have to do nothing since minOccures zero

                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("extraElement cannot be null!!");
            }
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


        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "pid"));

        if (this.localPid != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPid));
        } else {
            throw new org.apache.axis2.databinding.ADBException("pid cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "version"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localVersion));

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "status"));


        if (this.localStatus == null) {
            throw new org.apache.axis2.databinding.ADBException("status cannot be null!!");
        }
        elementList.add(this.localStatus);

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "olderVersion"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localOlderVersion));

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "definitionInfo"));


        if (this.localDefinitionInfo == null) {
            throw new org.apache.axis2.databinding.ADBException("definitionInfo cannot be null!!");
        }
        elementList.add(this.localDefinitionInfo);

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "deploymentInfo"));


        if (this.localDeploymentInfo == null) {
            throw new org.apache.axis2.databinding.ADBException("deploymentInfo cannot be null!!");
        }
        elementList.add(this.localDeploymentInfo);
        if (this.localInstanceSummaryTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceSummary"));


            if (this.localInstanceSummary == null) {
                throw new org.apache.axis2.databinding.ADBException("instanceSummary cannot be null!!");
            }
            elementList.add(this.localInstanceSummary);
        }
        if (this.localPropertiesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "properties"));


            if (this.localProperties == null) {
                throw new org.apache.axis2.databinding.ADBException("properties cannot be null!!");
            }
            elementList.add(this.localProperties);
        }
        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "endpoints"));


        if (this.localEndpoints == null) {
            throw new org.apache.axis2.databinding.ADBException("endpoints cannot be null!!");
        }
        elementList.add(this.localEndpoints);
        if (this.localExtraElementTracker) {
            if (this.localExtraElement != null) {
                for (final OMElement element : this.localExtraElement) {
                    if (element != null) {
                        elementList.add(new javax.xml.namespace.QName("", "extraElement"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));
                    } else {

                        // have to do nothing

                    }

                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("extraElement cannot be null!!");
            }
        }
        for (final OMAttribute localExtraAttribute : this.localExtraAttributes) {
            attribList.add(org.apache.axis2.databinding.utils.Constants.OM_ATTRIBUTE_KEY);
            attribList.add(localExtraAttribute);
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
        public static ProcessInfoType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final ProcessInfoType object = new ProcessInfoType();

            int event;
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

                        if (!"ProcessInfoType".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (ProcessInfoType) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
                                                                                                                                   type,
                                                                                                                                   reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                // now run through all any or extra attributes
                // which were not reflected until now
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (!handledAttributes.contains(reader.getAttributeLocalName(i))) {
                        // this is an anyAttribute and we create
                        // an OMAttribute for this
                        final org.apache.axiom.om.impl.llom.OMAttributeImpl attr =
                            new org.apache.axiom.om.impl.llom.OMAttributeImpl(reader.getAttributeLocalName(i),
                                new org.apache.axiom.om.impl.dom.NamespaceImpl(reader.getAttributeNamespace(i),
                                    reader.getAttributePrefix(i)),
                                reader.getAttributeValue(i), org.apache.axiom.om.OMAbstractFactory.getOMFactory());

                        // and add it to the extra attributes

                        object.addExtraAttributes(attr);


                    }
                }


                reader.next();

                final java.util.ArrayList list10 = new java.util.ArrayList();


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
                    "version").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setVersion(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

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

                    object.setStatus(org.wso2.bps.management.schema.ProcessStatus.Factory.parse(reader));

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
                    "olderVersion").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setOlderVersion(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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
                    "definitionInfo").equals(reader.getName())) {

                    object.setDefinitionInfo(org.wso2.bps.management.schema.DefinitionInfo.Factory.parse(reader));

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
                    "deploymentInfo").equals(reader.getName())) {

                    object.setDeploymentInfo(org.wso2.bps.management.schema.DeploymentInfo.Factory.parse(reader));

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
                    "instanceSummary").equals(reader.getName())) {

                    object.setInstanceSummary(org.wso2.bps.management.schema.InstanceSummary.Factory.parse(reader));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "properties").equals(reader.getName())) {

                    object.setProperties(org.wso2.bps.management.schema.ProcessProperties.Factory.parse(reader));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "endpoints").equals(reader.getName())) {

                    object.setEndpoints(org.wso2.bps.management.schema.EndpointReferencesType.Factory.parse(reader));

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

                if (reader.isStartElement()) {



                    // Process the array and step past its final element's end.

                    boolean loopDone10 = false;

                    while (!loopDone10) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event) {

                            // We need to wrap the reader so that it produces a fake START_DOCUEMENT event
                            final org.apache.axis2.databinding.utils.NamedStaxOMBuilder builder10 =
                                new org.apache.axis2.databinding.utils.NamedStaxOMBuilder(
                                    new org.apache.axis2.util.StreamWrapper(reader), reader.getName());

                            list10.add(builder10.getOMElement());
                            reader.next();
                            if (reader.isEndElement()) {
                                // we have two countinuos end elements
                                loopDone10 = true;
                            }

                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event) {
                            loopDone10 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setExtraElement((org.apache.axiom.om.OMElement[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(org.apache.axiom.om.OMElement.class,
                                                                                                                                             list10));

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

