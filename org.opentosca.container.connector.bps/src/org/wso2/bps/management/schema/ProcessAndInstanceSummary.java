
/**
 * ProcessAndInstanceSummary.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * ProcessAndInstanceSummary bean class
 */

public class ProcessAndInstanceSummary implements org.apache.axis2.databinding.ADBBean {

    /**
     *
     */
    private static final long serialVersionUID = 6281794185660592200L;



    public static final javax.xml.namespace.QName MY_QNAME =
        new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "ProcessAndInstanceSummary", "ns1");



    /**
     * field for Instances
     */


    protected org.wso2.bps.management.schema.InstanceSummaryType localInstances;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.InstanceSummaryType
     */
    public org.wso2.bps.management.schema.InstanceSummaryType getInstances() {
        return this.localInstances;
    }



    /**
     * Auto generated setter method
     *
     * @param param Instances
     */
    public void setInstances(final org.wso2.bps.management.schema.InstanceSummaryType param) {

        this.localInstances = param;


    }


    /**
     * field for ActiveProcesses
     */


    protected int localActiveProcesses;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getActiveProcesses() {
        return this.localActiveProcesses;
    }



    /**
     * Auto generated setter method
     *
     * @param param ActiveProcesses
     */
    public void setActiveProcesses(final int param) {

        this.localActiveProcesses = param;


    }


    /**
     * field for RetiredProcesses
     */


    protected int localRetiredProcesses;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getRetiredProcesses() {
        return this.localRetiredProcesses;
    }



    /**
     * Auto generated setter method
     *
     * @param param RetiredProcesses
     */
    public void setRetiredProcesses(final int param) {

        this.localRetiredProcesses = param;


    }


    /**
     * field for DisabledProcesses
     */


    protected int localDisabledProcesses;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getDisabledProcesses() {
        return this.localDisabledProcesses;
    }



    /**
     * Auto generated setter method
     *
     * @param param DisabledProcesses
     */
    public void setDisabledProcesses(final int param) {

        this.localDisabledProcesses = param;


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
            new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

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
                               namespacePrefix + ":ProcessAndInstanceSummary", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ProcessAndInstanceSummary",
                               xmlWriter);
            }


        }

        if (this.localInstances == null) {
            throw new org.apache.axis2.databinding.ADBException("instances cannot be null!!");
        }
        this.localInstances.serialize(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
            "instances"), xmlWriter);

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "activeProcesses", xmlWriter);

        if (this.localActiveProcesses == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("activeProcesses cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActiveProcesses));
        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "retiredProcesses", xmlWriter);

        if (this.localRetiredProcesses == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("retiredProcesses cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localRetiredProcesses));
        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "disabledProcesses", xmlWriter);

        if (this.localDisabledProcesses == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("disabledProcesses cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDisabledProcesses));
        }

        xmlWriter.writeEndElement();

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


        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instances"));


        if (this.localInstances == null) {
            throw new org.apache.axis2.databinding.ADBException("instances cannot be null!!");
        }
        elementList.add(this.localInstances);

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "activeProcesses"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActiveProcesses));

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "retiredProcesses"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localRetiredProcesses));

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "disabledProcesses"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDisabledProcesses));


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
        public static ProcessAndInstanceSummary parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final ProcessAndInstanceSummary object = new ProcessAndInstanceSummary();

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

                        if (!"ProcessAndInstanceSummary".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (ProcessAndInstanceSummary) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
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
                    "instances").equals(reader.getName())) {

                    object.setInstances(org.wso2.bps.management.schema.InstanceSummaryType.Factory.parse(reader));

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
                    "activeProcesses").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setActiveProcesses(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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
                    "retiredProcesses").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setRetiredProcesses(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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
                    "disabledProcesses").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setDisabledProcesses(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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

