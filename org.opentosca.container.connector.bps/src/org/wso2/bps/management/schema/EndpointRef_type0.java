
/**
 * EndpointRef_type0.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;

import org.apache.axiom.om.OMElement;

/**
 * EndpointRef_type0 bean class
 */

public class EndpointRef_type0 implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = endpointRef_type0 Namespace URI
     * = http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = 4082172345318692557L;

    /**
     * field for PartnerLink
     */


    protected java.lang.String localPartnerLink;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getPartnerLink() {
        return this.localPartnerLink;
    }



    /**
     * Auto generated setter method
     *
     * @param param PartnerLink
     */
    public void setPartnerLink(final java.lang.String param) {

        this.localPartnerLink = param;


    }


    /**
     * field for Service
     */


    protected javax.xml.namespace.QName localService;


    /**
     * Auto generated getter method
     *
     * @return javax.xml.namespace.QName
     */
    public javax.xml.namespace.QName getService() {
        return this.localService;
    }



    /**
     * Auto generated setter method
     *
     * @param param Service
     */
    public void setService(final javax.xml.namespace.QName param) {

        this.localService = param;


    }


    /**
     * field for ServiceLocations
     */


    protected org.wso2.bps.management.schema.ServiceLocation localServiceLocations;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.bps.management.schema.ServiceLocation
     */
    public org.wso2.bps.management.schema.ServiceLocation getServiceLocations() {
        return this.localServiceLocations;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceLocations
     */
    public void setServiceLocations(final org.wso2.bps.management.schema.ServiceLocation param) {

        this.localServiceLocations = param;


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
        this.localExtraElement = (org.apache.axiom.om.OMElement[]) list.toArray(
            new org.apache.axiom.om.OMElement[list.size()]);

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
                    namespacePrefix + ":endpointRef_type0", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "endpointRef_type0",
                    xmlWriter);
            }


        }

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "partnerLink", xmlWriter);


        if (this.localPartnerLink == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("partnerLink cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localPartnerLink);

        }

        xmlWriter.writeEndElement();

        namespace = "http://wso2.org/bps/management/schema";
        writeStartElement(null, namespace, "service", xmlWriter);


        if (this.localService == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("service cannot be null!!");

        } else {


            writeQName(this.localService, xmlWriter);

        }

        xmlWriter.writeEndElement();

        if (this.localServiceLocations == null) {
            throw new org.apache.axis2.databinding.ADBException("serviceLocations cannot be null!!");
        }
        this.localServiceLocations.serialize(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "serviceLocations"), xmlWriter);
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


        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "partnerLink"));

        if (this.localPartnerLink != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPartnerLink));
        } else {
            throw new org.apache.axis2.databinding.ADBException("partnerLink cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "service"));

        if (this.localService != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localService));
        } else {
            throw new org.apache.axis2.databinding.ADBException("service cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "serviceLocations"));


        if (this.localServiceLocations == null) {
            throw new org.apache.axis2.databinding.ADBException("serviceLocations cannot be null!!");
        }
        elementList.add(this.localServiceLocations);
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
        public static EndpointRef_type0 parse(final javax.xml.stream.XMLStreamReader reader)
            throws java.lang.Exception {
            final EndpointRef_type0 object = new EndpointRef_type0();

            int event;
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

                        if (!"endpointRef_type0".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (EndpointRef_type0) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(
                                nsUri, type, reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list4 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "partnerLink").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setPartnerLink(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "service").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    final int index = content.indexOf(":");
                    if (index > 0) {
                        prefix = content.substring(0, index);
                    } else {
                        prefix = "";
                    }
                    namespaceuri = reader.getNamespaceURI(prefix);
                    object.setService(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://wso2.org/bps/management/schema",
                    "serviceLocations").equals(reader.getName())) {

                    object.setServiceLocations(org.wso2.bps.management.schema.ServiceLocation.Factory.parse(reader));

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

                    boolean loopDone4 = false;

                    while (!loopDone4) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event) {

                            // We need to wrap the reader so that it produces a fake START_DOCUEMENT event
                            final org.apache.axis2.databinding.utils.NamedStaxOMBuilder builder4 = new org.apache.axis2.databinding.utils.NamedStaxOMBuilder(
                                new org.apache.axis2.util.StreamWrapper(reader), reader.getName());

                            list4.add(builder4.getOMElement());
                            reader.next();
                            if (reader.isEndElement()) {
                                // we have two countinuos end elements
                                loopDone4 = true;
                            }

                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event) {
                            loopDone4 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setExtraElement(
                        (org.apache.axiom.om.OMElement[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            org.apache.axiom.om.OMElement.class, list4));

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

