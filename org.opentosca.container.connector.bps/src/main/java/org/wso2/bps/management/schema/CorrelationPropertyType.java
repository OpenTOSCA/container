
/**
 * CorrelationPropertyType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.bps.management.schema;


/**
 * CorrelationPropertyType bean class
 */

public class CorrelationPropertyType implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = CorrelationPropertyType
     * Namespace URI = http://wso2.org/bps/management/schema Namespace Prefix = ns1
     */


    /**
     *
     */
    private static final long serialVersionUID = 5735747781258041450L;
    /**
     * field for String
     */


    protected java.lang.String localString;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getString() {
        return this.localString;
    }



    /**
     * Auto generated setter method
     *
     * @param param String
     */
    public void setString(final java.lang.String param) {

        this.localString = param;


    }


    @Override
    public java.lang.String toString() {

        return this.localString.toString();

    }


    /**
     * field for Csetid This was an Attribute!
     */


    protected java.lang.String localCsetid;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getCsetid() {
        return this.localCsetid;
    }



    /**
     * Auto generated setter method
     *
     * @param param Csetid
     */
    public void setCsetid(final java.lang.String param) {

        this.localCsetid = param;


    }


    /**
     * field for PropertyName This was an Attribute!
     */


    protected javax.xml.namespace.QName localPropertyName;


    /**
     * Auto generated getter method
     *
     * @return javax.xml.namespace.QName
     */
    public javax.xml.namespace.QName getPropertyName() {
        return this.localPropertyName;
    }



    /**
     * Auto generated setter method
     *
     * @param param PropertyName
     */
    public void setPropertyName(final javax.xml.namespace.QName param) {

        this.localPropertyName = param;


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
                               namespacePrefix + ":CorrelationPropertyType", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "CorrelationPropertyType",
                               xmlWriter);
            }


        }

        if (this.localCsetid != null) {

            writeAttribute("", "csetid",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCsetid),
                           xmlWriter);


        }

        else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localCsetid is null");
        }

        if (this.localPropertyName != null) {

            writeQNameAttribute("", "propertyName", this.localPropertyName, xmlWriter);


        }

        else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localPropertyName is null");
        }


        if (this.localString == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("string cannot be null!!");

        } else {


            xmlWriter.writeCharacters(this.localString);

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



        elementList.add(org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT);

        if (this.localString != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localString));
        } else {
            throw new org.apache.axis2.databinding.ADBException("string cannot be null!!");
        }

        attribList.add(new javax.xml.namespace.QName("", "csetid"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCsetid));

        attribList.add(new javax.xml.namespace.QName("", "propertyName"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPropertyName));


        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
            attribList.toArray());



    }



    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {



        public static CorrelationPropertyType fromString(final java.lang.String value,
                                                         final java.lang.String namespaceURI) {
            final CorrelationPropertyType returnValue = new CorrelationPropertyType();

            returnValue.setString(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(value));


            return returnValue;
        }

        public static CorrelationPropertyType fromString(final javax.xml.stream.XMLStreamReader xmlStreamReader,
                                                         final java.lang.String content) {
            if (content.indexOf(":") > -1) {
                final java.lang.String prefix = content.substring(0, content.indexOf(":"));
                final java.lang.String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
                return CorrelationPropertyType.Factory.fromString(content, namespaceUri);
            } else {
                return CorrelationPropertyType.Factory.fromString(content, "");
            }
        }



        /**
         * static method to create the object Precondition: If this object is an element, the current or
         * next start element starts this object and any intervening reader events are ignorable If this
         * object is not an element, it is a complex type and the reader is at the event just after the
         * outer start element Postcondition: If this object is an element, the reader is positioned at its
         * end element If this object is a complex type, the reader is positioned at the end element of its
         * outer element
         */
        public static CorrelationPropertyType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final CorrelationPropertyType object = new CorrelationPropertyType();

            final int event;
            final java.lang.String nillableValue = null;
            java.lang.String prefix = "";
            java.lang.String namespaceuri = "";
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

                        if (!"CorrelationPropertyType".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (CorrelationPropertyType) org.wso2.bps.management.wsdl.instancemanagement.ExtensionMapper.getTypeObject(nsUri,
                                                                                                                                           type,
                                                                                                                                           reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                // handle attribute "csetid"
                final java.lang.String tempAttribCsetid =

                    reader.getAttributeValue(null, "csetid");

                if (tempAttribCsetid != null) {
                    final java.lang.String content = tempAttribCsetid;

                    object.setCsetid(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribCsetid));

                } else {

                    throw new org.apache.axis2.databinding.ADBException("Required attribute csetid is missing");

                }
                handledAttributes.add("csetid");

                // handle attribute "propertyName"
                final java.lang.String tempAttribPropertyName =

                    reader.getAttributeValue(null, "propertyName");

                if (tempAttribPropertyName != null) {
                    final java.lang.String content = tempAttribPropertyName;

                    final int index = tempAttribPropertyName.indexOf(":");
                    if (index > -1) {
                        prefix = tempAttribPropertyName.substring(0, index);
                    } else {
                        // i.e this is in default namesace
                        prefix = "";
                    }
                    namespaceuri = reader.getNamespaceURI(prefix);

                    object.setPropertyName(org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(tempAttribPropertyName,
                                                                                                           namespaceuri));

                } else {

                    throw new org.apache.axis2.databinding.ADBException("Required attribute propertyName is missing");

                }
                handledAttributes.add("propertyName");

                while (!reader.isEndElement()) {
                    if (reader.isStartElement() || reader.hasText()) {

                        if (reader.isStartElement() || reader.hasText()) {

                            final java.lang.String content = reader.getElementText();

                            object.setString(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        } // End of if for expected property start element

                        else {
                            // A start element we are not expecting indicates an invalid parameter was passed
                            throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getLocalName());
                        }

                    } else {
                        reader.next();
                    }
                } // end of while loop



            }
            catch (final javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }// end of factory class



}

