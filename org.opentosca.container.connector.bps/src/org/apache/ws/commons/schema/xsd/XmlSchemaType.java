
/**
 * XmlSchemaType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.ws.commons.schema.xsd;

import org.w3c.dom.xsd.Attr;

/**
 * XmlSchemaType bean class
 */

public class XmlSchemaType extends org.apache.ws.commons.schema.xsd.XmlSchemaAnnotated
                           implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = XmlSchemaType Namespace URI =
     * http://schema.commons.ws.apache.org/xsd Namespace Prefix = ns22
     */


    /**
     *
     */
    private static final long serialVersionUID = -3811256173958320914L;

    /**
     * field for QName
     */


    protected java.lang.Object localQName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localQNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getQName() {
        return this.localQName;
    }



    /**
     * Auto generated setter method
     *
     * @param param QName
     */
    public void setQName(final java.lang.Object param) {
        this.localQNameTracker = true;

        this.localQName = param;


    }


    /**
     * field for BaseSchemaType
     */


    protected java.lang.Object localBaseSchemaType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localBaseSchemaTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getBaseSchemaType() {
        return this.localBaseSchemaType;
    }



    /**
     * Auto generated setter method
     *
     * @param param BaseSchemaType
     */
    public void setBaseSchemaType(final java.lang.Object param) {
        this.localBaseSchemaTypeTracker = true;

        this.localBaseSchemaType = param;


    }


    /**
     * field for BaseSchemaTypeName
     */


    protected java.lang.Object localBaseSchemaTypeName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localBaseSchemaTypeNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getBaseSchemaTypeName() {
        return this.localBaseSchemaTypeName;
    }



    /**
     * Auto generated setter method
     *
     * @param param BaseSchemaTypeName
     */
    public void setBaseSchemaTypeName(final java.lang.Object param) {
        this.localBaseSchemaTypeNameTracker = true;

        this.localBaseSchemaTypeName = param;


    }


    /**
     * field for DataType
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaDatatype localDataType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDataTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaDatatype
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaDatatype getDataType() {
        return this.localDataType;
    }



    /**
     * Auto generated setter method
     *
     * @param param DataType
     */
    public void setDataType(final org.apache.ws.commons.schema.xsd.XmlSchemaDatatype param) {
        this.localDataTypeTracker = true;

        this.localDataType = param;


    }


    /**
     * field for DeriveBy
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod localDeriveBy;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDeriveByTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod getDeriveBy() {
        return this.localDeriveBy;
    }



    /**
     * Auto generated setter method
     *
     * @param param DeriveBy
     */
    public void setDeriveBy(final org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod param) {
        this.localDeriveByTracker = true;

        this.localDeriveBy = param;


    }


    /**
     * field for _final
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod local_final;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean local_finalTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod get_final() {
        return this.local_final;
    }



    /**
     * Auto generated setter method
     *
     * @param param _final
     */
    public void set_final(final org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod param) {
        this.local_finalTracker = true;

        this.local_final = param;


    }


    /**
     * field for FinalResolved
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod localFinalResolved;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFinalResolvedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod getFinalResolved() {
        return this.localFinalResolved;
    }



    /**
     * Auto generated setter method
     *
     * @param param FinalResolved
     */
    public void setFinalResolved(final org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod param) {
        this.localFinalResolvedTracker = true;

        this.localFinalResolved = param;


    }


    /**
     * field for Mixed
     */


    protected boolean localMixed;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMixedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getMixed() {
        return this.localMixed;
    }



    /**
     * Auto generated setter method
     *
     * @param param Mixed
     */
    public void setMixed(final boolean param) {

        // setting primitive attribute tracker to true
        this.localMixedTracker = true;

        this.localMixed = param;


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


        final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://schema.commons.ws.apache.org/xsd");
        if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                           namespacePrefix + ":XmlSchemaType", xmlWriter);
        } else {
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "XmlSchemaType", xmlWriter);
        }

        if (this.localLineNumberTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "lineNumber", xmlWriter);

            if (this.localLineNumber == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("lineNumber cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localLinePositionTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "linePosition", xmlWriter);

            if (this.localLinePosition == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("linePosition cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLinePosition));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localMetaInfoMapTracker) {
            if (this.localMetaInfoMap == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "metaInfoMap", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localMetaInfoMap.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "metaInfoMap"), xmlWriter);
            }
        }
        if (this.localSourceURITracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "sourceURI", xmlWriter);


            if (this.localSourceURI == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localSourceURI);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localAnnotationTracker) {
            if (this.localAnnotation == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "annotation", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAnnotation.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "annotation"), xmlWriter);
            }
        }
        if (this.localIdTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "id", xmlWriter);


            if (this.localId == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localId);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localUnhandledAttributesTracker) {
            if (this.localUnhandledAttributes != null) {
                for (final Attr localUnhandledAttribute : this.localUnhandledAttributes) {
                    if (localUnhandledAttribute != null) {
                        localUnhandledAttribute.serialize(new javax.xml.namespace.QName(
                            "http://schema.commons.ws.apache.org/xsd", "unhandledAttributes"), xmlWriter);
                    } else {

                        writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "unhandledAttributes",
                                          xmlWriter);

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "unhandledAttributes", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }
        }
        if (this.localQNameTracker) {

            if (this.localQName != null) {
                if (this.localQName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localQName).serialize(new javax.xml.namespace.QName(
                        "http://schema.commons.ws.apache.org/xsd", "QName"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "QName", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localQName, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "QName", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localBaseSchemaTypeTracker) {

            if (this.localBaseSchemaType != null) {
                if (this.localBaseSchemaType instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localBaseSchemaType).serialize(new javax.xml.namespace.QName(
                        "http://schema.commons.ws.apache.org/xsd", "baseSchemaType"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "baseSchemaType", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localBaseSchemaType,
                                                                                      xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "baseSchemaType", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localBaseSchemaTypeNameTracker) {

            if (this.localBaseSchemaTypeName != null) {
                if (this.localBaseSchemaTypeName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localBaseSchemaTypeName).serialize(new javax.xml.namespace.QName(
                        "http://schema.commons.ws.apache.org/xsd", "baseSchemaTypeName"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "baseSchemaTypeName", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localBaseSchemaTypeName,
                                                                                      xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "baseSchemaTypeName", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localDataTypeTracker) {
            if (this.localDataType == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "dataType", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localDataType.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "dataType"), xmlWriter);
            }
        }
        if (this.localDeriveByTracker) {
            if (this.localDeriveBy == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "deriveBy", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localDeriveBy.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "deriveBy"), xmlWriter);
            }
        }
        if (this.local_finalTracker) {
            if (this.local_final == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "final", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.local_final.serialize(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "final"), xmlWriter);
            }
        }
        if (this.localFinalResolvedTracker) {
            if (this.localFinalResolved == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "finalResolved", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localFinalResolved.serialize(new javax.xml.namespace.QName(
                    "http://schema.commons.ws.apache.org/xsd", "finalResolved"), xmlWriter);
            }
        }
        if (this.localMixedTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "mixed", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("mixed cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMixed));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localNameTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://schema.commons.ws.apache.org/xsd")) {
            return "ns22";
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


        attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema-instance", "type"));
        attribList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "XmlSchemaType"));
        if (this.localLineNumberTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "lineNumber"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
        }
        if (this.localLinePositionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "linePosition"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLinePosition));
        }
        if (this.localMetaInfoMapTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "metaInfoMap"));


            elementList.add(this.localMetaInfoMap == null ? null : this.localMetaInfoMap);
        }
        if (this.localSourceURITracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "sourceURI"));

            elementList.add(this.localSourceURI == null ? null
                                                        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSourceURI));
        }
        if (this.localAnnotationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "annotation"));


            elementList.add(this.localAnnotation == null ? null : this.localAnnotation);
        }
        if (this.localIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "id"));

            elementList.add(this.localId == null ? null
                                                 : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localId));
        }
        if (this.localUnhandledAttributesTracker) {
            if (this.localUnhandledAttributes != null) {
                for (final Attr localUnhandledAttribute : this.localUnhandledAttributes) {

                    if (localUnhandledAttribute != null) {
                        elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                            "unhandledAttributes"));
                        elementList.add(localUnhandledAttribute);
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                            "unhandledAttributes"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "unhandledAttributes"));
                elementList.add(this.localUnhandledAttributes);

            }

        }
        if (this.localQNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "QName"));


            elementList.add(this.localQName == null ? null : this.localQName);
        }
        if (this.localBaseSchemaTypeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "baseSchemaType"));


            elementList.add(this.localBaseSchemaType == null ? null : this.localBaseSchemaType);
        }
        if (this.localBaseSchemaTypeNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                "baseSchemaTypeName"));


            elementList.add(this.localBaseSchemaTypeName == null ? null : this.localBaseSchemaTypeName);
        }
        if (this.localDataTypeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "dataType"));


            elementList.add(this.localDataType == null ? null : this.localDataType);
        }
        if (this.localDeriveByTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "deriveBy"));


            elementList.add(this.localDeriveBy == null ? null : this.localDeriveBy);
        }
        if (this.local_finalTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "final"));


            elementList.add(this.local_final == null ? null : this.local_final);
        }
        if (this.localFinalResolvedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "finalResolved"));


            elementList.add(this.localFinalResolved == null ? null : this.localFinalResolved);
        }
        if (this.localMixedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "mixed"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMixed));
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
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
        public static XmlSchemaType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final XmlSchemaType object = new XmlSchemaType();

            final int event;
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

                        if (!"XmlSchemaType".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (XmlSchemaType) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                                                                                                                  type,
                                                                                                                  reader);
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "lineNumber").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setLineNumber(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setLineNumber(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "linePosition").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setLinePosition(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setLinePosition(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "metaInfoMap").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setMetaInfoMap(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setMetaInfoMap(authclient.java.util.xsd.Map.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "sourceURI").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setSourceURI(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "annotation").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAnnotation(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAnnotation(org.apache.ws.commons.schema.xsd.XmlSchemaAnnotation.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "id").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "unhandledAttributes").equals(reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list7.add(null);
                        reader.next();
                    } else {
                        list7.add(org.w3c.dom.xsd.Attr.Factory.parse(reader));
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone7 = false;
                    while (!loopDone7) {
                        // We should be at the end element, but make sure
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
                            if (new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                                "unhandledAttributes").equals(reader.getName())) {

                                nillableValue =
                                    reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list7.add(null);
                                    reader.next();
                                } else {
                                    list7.add(org.w3c.dom.xsd.Attr.Factory.parse(reader));
                                }
                            } else {
                                loopDone7 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setUnhandledAttributes((org.w3c.dom.xsd.Attr[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(org.w3c.dom.xsd.Attr.class,
                                                                                                                                           list7));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "QName").equals(reader.getName())) {

                    object.setQName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                      org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "baseSchemaType").equals(reader.getName())) {

                    object.setBaseSchemaType(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                               org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "baseSchemaTypeName").equals(reader.getName())) {

                    object.setBaseSchemaTypeName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                                   org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "dataType").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setDataType(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setDataType(org.apache.ws.commons.schema.xsd.XmlSchemaDatatype.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "deriveBy").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setDeriveBy(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setDeriveBy(org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "final").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.set_final(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.set_final(org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "finalResolved").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setFinalResolved(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setFinalResolved(org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "mixed").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setMixed(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
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

