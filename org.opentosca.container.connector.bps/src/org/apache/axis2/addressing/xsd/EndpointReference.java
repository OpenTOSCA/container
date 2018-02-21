
/**
 * EndpointReference.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.addressing.xsd;


/**
 * EndpointReference bean class
 */

public class EndpointReference implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = EndpointReference Namespace URI
     * = http://addressing.axis2.apache.org/xsd Namespace Prefix = ns27
     */


    /**
     *
     */
    private static final long serialVersionUID = -1460379303439663252L;

    /**
     * field for WSAddressingAnonymous
     */


    protected boolean localWSAddressingAnonymous;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localWSAddressingAnonymousTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getWSAddressingAnonymous() {
        return this.localWSAddressingAnonymous;
    }



    /**
     * Auto generated setter method
     *
     * @param param WSAddressingAnonymous
     */
    public void setWSAddressingAnonymous(final boolean param) {

        // setting primitive attribute tracker to true
        this.localWSAddressingAnonymousTracker = true;

        this.localWSAddressingAnonymous = param;


    }


    /**
     * field for Address
     */


    protected java.lang.String localAddress;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAddressTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getAddress() {
        return this.localAddress;
    }



    /**
     * Auto generated setter method
     *
     * @param param Address
     */
    public void setAddress(final java.lang.String param) {
        this.localAddressTracker = true;

        this.localAddress = param;


    }


    /**
     * field for AddressAttributes
     */


    protected java.lang.Object localAddressAttributes;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAddressAttributesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getAddressAttributes() {
        return this.localAddressAttributes;
    }



    /**
     * Auto generated setter method
     *
     * @param param AddressAttributes
     */
    public void setAddressAttributes(final java.lang.Object param) {
        this.localAddressAttributesTracker = true;

        this.localAddressAttributes = param;


    }


    /**
     * field for AllReferenceParameters
     */


    protected authclient.java.util.xsd.Map localAllReferenceParameters;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAllReferenceParametersTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Map
     */
    public authclient.java.util.xsd.Map getAllReferenceParameters() {
        return this.localAllReferenceParameters;
    }



    /**
     * Auto generated setter method
     *
     * @param param AllReferenceParameters
     */
    public void setAllReferenceParameters(final authclient.java.util.xsd.Map param) {
        this.localAllReferenceParametersTracker = true;

        this.localAllReferenceParameters = param;


    }


    /**
     * field for Attributes
     */


    protected java.lang.Object localAttributes;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAttributesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getAttributes() {
        return this.localAttributes;
    }



    /**
     * Auto generated setter method
     *
     * @param param Attributes
     */
    public void setAttributes(final java.lang.Object param) {
        this.localAttributesTracker = true;

        this.localAttributes = param;


    }


    /**
     * field for ExtensibleElements This was an Array!
     */


    protected java.lang.Object[] localExtensibleElements;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExtensibleElementsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object[]
     */
    public java.lang.Object[] getExtensibleElements() {
        return this.localExtensibleElements;
    }



    /**
     * validate the array for ExtensibleElements
     */
    protected void validateExtensibleElements(final java.lang.Object[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param ExtensibleElements
     */
    public void setExtensibleElements(final java.lang.Object[] param) {

        validateExtensibleElements(param);

        this.localExtensibleElementsTracker = true;

        this.localExtensibleElements = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.Object
     */
    public void addExtensibleElements(final java.lang.Object param) {
        if (this.localExtensibleElements == null) {
            this.localExtensibleElements = new java.lang.Object[] {};
        }


        // update the setting tracker
        this.localExtensibleElementsTracker = true;


        final java.util.List list =
            org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localExtensibleElements);
        list.add(param);
        this.localExtensibleElements = list.toArray(new java.lang.Object[list.size()]);

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
     * field for MetaData
     */


    protected java.lang.Object localMetaData;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMetaDataTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getMetaData() {
        return this.localMetaData;
    }



    /**
     * Auto generated setter method
     *
     * @param param MetaData
     */
    public void setMetaData(final java.lang.Object param) {
        this.localMetaDataTracker = true;

        this.localMetaData = param;


    }


    /**
     * field for MetadataAttributes
     */


    protected java.lang.Object localMetadataAttributes;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMetadataAttributesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getMetadataAttributes() {
        return this.localMetadataAttributes;
    }



    /**
     * Auto generated setter method
     *
     * @param param MetadataAttributes
     */
    public void setMetadataAttributes(final java.lang.Object param) {
        this.localMetadataAttributesTracker = true;

        this.localMetadataAttributes = param;


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
     * field for ReferenceParameters This was an Array!
     */


    protected java.lang.Object[] localReferenceParameters;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localReferenceParametersTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object[]
     */
    public java.lang.Object[] getReferenceParameters() {
        return this.localReferenceParameters;
    }



    /**
     * validate the array for ReferenceParameters
     */
    protected void validateReferenceParameters(final java.lang.Object[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param ReferenceParameters
     */
    public void setReferenceParameters(final java.lang.Object[] param) {

        validateReferenceParameters(param);

        this.localReferenceParametersTracker = true;

        this.localReferenceParameters = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.Object
     */
    public void addReferenceParameters(final java.lang.Object param) {
        if (this.localReferenceParameters == null) {
            this.localReferenceParameters = new java.lang.Object[] {};
        }


        // update the setting tracker
        this.localReferenceParametersTracker = true;


        final java.util.List list =
            org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localReferenceParameters);
        list.add(param);
        this.localReferenceParameters = list.toArray(new java.lang.Object[list.size()]);

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


            final java.lang.String namespacePrefix =
                registerPrefix(xmlWriter, "http://addressing.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":EndpointReference", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "EndpointReference",
                               xmlWriter);
            }


        }
        if (this.localWSAddressingAnonymousTracker) {
            namespace = "http://addressing.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "WSAddressingAnonymous", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("WSAddressingAnonymous cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWSAddressingAnonymous));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localAddressTracker) {
            namespace = "http://addressing.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "address", xmlWriter);


            if (this.localAddress == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localAddress);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localAddressAttributesTracker) {

            if (this.localAddressAttributes != null) {
                if (this.localAddressAttributes instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localAddressAttributes).serialize(new javax.xml.namespace.QName(
                        "http://addressing.axis2.apache.org/xsd", "addressAttributes"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "addressAttributes", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localAddressAttributes,
                                                                                      xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "addressAttributes", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localAllReferenceParametersTracker) {
            if (this.localAllReferenceParameters == null) {

                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "allReferenceParameters", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAllReferenceParameters.serialize(new javax.xml.namespace.QName(
                    "http://addressing.axis2.apache.org/xsd", "allReferenceParameters"), xmlWriter);
            }
        }
        if (this.localAttributesTracker) {

            if (this.localAttributes != null) {
                if (this.localAttributes instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localAttributes).serialize(new javax.xml.namespace.QName(
                        "http://addressing.axis2.apache.org/xsd", "attributes"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "attributes", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localAttributes, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "attributes", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localExtensibleElementsTracker) {

            if (this.localExtensibleElements != null) {
                for (final Object localExtensibleElement : this.localExtensibleElements) {
                    if (localExtensibleElement != null) {

                        if (localExtensibleElement instanceof org.apache.axis2.databinding.ADBBean) {
                            ((org.apache.axis2.databinding.ADBBean) localExtensibleElement).serialize(new javax.xml.namespace.QName(
                                "http://addressing.axis2.apache.org/xsd", "extensibleElements"), xmlWriter, true);
                        } else {
                            writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "extensibleElements",
                                              xmlWriter);
                            org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localExtensibleElement,
                                                                                              xmlWriter);
                            xmlWriter.writeEndElement();
                        }

                    } else {

                        // write null attribute
                        writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "extensibleElements",
                                          xmlWriter);

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "extensibleElements", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localLogCorrelationIDStringTracker) {
            namespace = "http://addressing.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "logCorrelationIDString", xmlWriter);


            if (this.localLogCorrelationIDString == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localLogCorrelationIDString);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localMetaDataTracker) {

            if (this.localMetaData != null) {
                if (this.localMetaData instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localMetaData).serialize(new javax.xml.namespace.QName(
                        "http://addressing.axis2.apache.org/xsd", "metaData"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "metaData", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localMetaData, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "metaData", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localMetadataAttributesTracker) {

            if (this.localMetadataAttributes != null) {
                if (this.localMetadataAttributes instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localMetadataAttributes).serialize(new javax.xml.namespace.QName(
                        "http://addressing.axis2.apache.org/xsd", "metadataAttributes"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "metadataAttributes", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localMetadataAttributes,
                                                                                      xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "metadataAttributes", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localNameTracker) {
            namespace = "http://addressing.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localReferenceParametersTracker) {

            if (this.localReferenceParameters != null) {
                for (final Object localReferenceParameter : this.localReferenceParameters) {
                    if (localReferenceParameter != null) {

                        if (localReferenceParameter instanceof org.apache.axis2.databinding.ADBBean) {
                            ((org.apache.axis2.databinding.ADBBean) localReferenceParameter).serialize(new javax.xml.namespace.QName(
                                "http://addressing.axis2.apache.org/xsd", "referenceParameters"), xmlWriter, true);
                        } else {
                            writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "referenceParameters",
                                              xmlWriter);
                            org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localReferenceParameter,
                                                                                              xmlWriter);
                            xmlWriter.writeEndElement();
                        }

                    } else {

                        // write null attribute
                        writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "referenceParameters",
                                          xmlWriter);

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://addressing.axis2.apache.org/xsd", "referenceParameters", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://addressing.axis2.apache.org/xsd")) {
            return "ns27";
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

        if (this.localWSAddressingAnonymousTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                "WSAddressingAnonymous"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWSAddressingAnonymous));
        }
        if (this.localAddressTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd", "address"));

            elementList.add(this.localAddress == null ? null
                                                      : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localAddress));
        }
        if (this.localAddressAttributesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                "addressAttributes"));


            elementList.add(this.localAddressAttributes == null ? null : this.localAddressAttributes);
        }
        if (this.localAllReferenceParametersTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                "allReferenceParameters"));


            elementList.add(this.localAllReferenceParameters == null ? null : this.localAllReferenceParameters);
        }
        if (this.localAttributesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd", "attributes"));


            elementList.add(this.localAttributes == null ? null : this.localAttributes);
        }
        if (this.localExtensibleElementsTracker) {
            if (this.localExtensibleElements != null) {
                for (final Object localExtensibleElement : this.localExtensibleElements) {

                    if (localExtensibleElement != null) {
                        elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                            "extensibleElements"));
                        elementList.add(localExtensibleElement);
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                            "extensibleElements"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "extensibleElements"));
                elementList.add(this.localExtensibleElements);

            }

        }
        if (this.localLogCorrelationIDStringTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                "logCorrelationIDString"));

            elementList.add(this.localLogCorrelationIDString == null ? null
                                                                     : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLogCorrelationIDString));
        }
        if (this.localMetaDataTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd", "metaData"));


            elementList.add(this.localMetaData == null ? null : this.localMetaData);
        }
        if (this.localMetadataAttributesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                "metadataAttributes"));


            elementList.add(this.localMetadataAttributes == null ? null : this.localMetadataAttributes);
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
        }
        if (this.localReferenceParametersTracker) {
            if (this.localReferenceParameters != null) {
                for (final Object localReferenceParameter : this.localReferenceParameters) {

                    if (localReferenceParameter != null) {
                        elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                            "referenceParameters"));
                        elementList.add(localReferenceParameter);
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                            "referenceParameters"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "referenceParameters"));
                elementList.add(this.localReferenceParameters);

            }

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
        public static EndpointReference parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final EndpointReference object = new EndpointReference();

            int event;
            java.lang.String nillableValue = null;
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

                        if (!"EndpointReference".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (EndpointReference) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                                                                                                                      type,
                                                                                                                      reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list6 = new java.util.ArrayList();

                final java.util.ArrayList list11 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "WSAddressingAnonymous").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setWSAddressingAnonymous(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "address").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setAddress(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "addressAttributes").equals(reader.getName())) {

                    object.setAddressAttributes(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                                  org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "allReferenceParameters").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAllReferenceParameters(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAllReferenceParameters(authclient.java.util.xsd.Map.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "attributes").equals(reader.getName())) {

                    object.setAttributes(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                           org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "extensibleElements").equals(reader.getName())) {



                    // Process the array and step past its final element's end.


                    boolean loopDone6 = false;
                    final javax.xml.namespace.QName startQname6 =
                        new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd", "extensibleElements");

                    while (!loopDone6) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                            && startQname6.equals(reader.getName())) {



                            nillableValue =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                list6.add(null);
                                reader.next();
                            } else {
                                list6.add(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                            org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                            }
                        } else if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                            && !startQname6.equals(reader.getName())) {
                            loopDone6 = true;
                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event
                            && !startQname6.equals(reader.getName())) {
                            loopDone6 = true;
                        } else if (javax.xml.stream.XMLStreamConstants.END_DOCUMENT == event) {
                            loopDone6 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setExtensibleElements(list6.toArray());

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "logCorrelationIDString").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setLogCorrelationIDString(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "metaData").equals(reader.getName())) {

                    object.setMetaData(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                         org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "metadataAttributes").equals(reader.getName())) {

                    object.setMetadataAttributes(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                                   org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "name").equals(reader.getName())) {

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd",
                    "referenceParameters").equals(reader.getName())) {



                    // Process the array and step past its final element's end.


                    boolean loopDone11 = false;
                    final javax.xml.namespace.QName startQname11 =
                        new javax.xml.namespace.QName("http://addressing.axis2.apache.org/xsd", "referenceParameters");

                    while (!loopDone11) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                            && startQname11.equals(reader.getName())) {



                            nillableValue =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                list11.add(null);
                                reader.next();
                            } else {
                                list11.add(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                             org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                            }
                        } else if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                            && !startQname11.equals(reader.getName())) {
                            loopDone11 = true;
                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event
                            && !startQname11.equals(reader.getName())) {
                            loopDone11 = true;
                        } else if (javax.xml.stream.XMLStreamConstants.END_DOCUMENT == event) {
                            loopDone11 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setReferenceParameters(list11.toArray());

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

