
/**
 * XmlSchemaElement.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.ws.commons.schema.xsd;

import org.w3c.dom.xsd.Attr;

/**
 * XmlSchemaElement bean class
 */

public class XmlSchemaElement extends org.apache.ws.commons.schema.xsd.XmlSchemaParticle
                              implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = XmlSchemaElement Namespace URI =
     * http://schema.commons.ws.apache.org/xsd Namespace Prefix = ns22
     */


    /**
     *
     */
    private static final long serialVersionUID = -5251329982547667556L;

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
     * field for _abstract
     */


    protected boolean local_abstract;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean local_abstractTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean get_abstract() {
        return this.local_abstract;
    }



    /**
     * Auto generated setter method
     *
     * @param param _abstract
     */
    public void set_abstract(final boolean param) {

        // setting primitive attribute tracker to true
        this.local_abstractTracker = true;

        this.local_abstract = param;


    }


    /**
     * field for Block
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod localBlock;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localBlockTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod getBlock() {
        return this.localBlock;
    }



    /**
     * Auto generated setter method
     *
     * @param param Block
     */
    public void setBlock(final org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod param) {
        this.localBlockTracker = true;

        this.localBlock = param;


    }


    /**
     * field for BlockResolved
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod localBlockResolved;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localBlockResolvedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod getBlockResolved() {
        return this.localBlockResolved;
    }



    /**
     * Auto generated setter method
     *
     * @param param BlockResolved
     */
    public void setBlockResolved(final org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod param) {
        this.localBlockResolvedTracker = true;

        this.localBlockResolved = param;


    }


    /**
     * field for Constraints
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaObjectCollection localConstraints;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localConstraintsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaObjectCollection
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaObjectCollection getConstraints() {
        return this.localConstraints;
    }



    /**
     * Auto generated setter method
     *
     * @param param Constraints
     */
    public void setConstraints(final org.apache.ws.commons.schema.xsd.XmlSchemaObjectCollection param) {
        this.localConstraintsTracker = true;

        this.localConstraints = param;


    }


    /**
     * field for DefaultValue
     */


    protected java.lang.String localDefaultValue;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDefaultValueTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getDefaultValue() {
        return this.localDefaultValue;
    }



    /**
     * Auto generated setter method
     *
     * @param param DefaultValue
     */
    public void setDefaultValue(final java.lang.String param) {
        this.localDefaultValueTracker = true;

        this.localDefaultValue = param;


    }


    /**
     * field for ElementType
     */


    protected java.lang.Object localElementType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localElementTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getElementType() {
        return this.localElementType;
    }



    /**
     * Auto generated setter method
     *
     * @param param ElementType
     */
    public void setElementType(final java.lang.Object param) {
        this.localElementTypeTracker = true;

        this.localElementType = param;


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
     * field for FixedValue
     */


    protected java.lang.String localFixedValue;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFixedValueTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getFixedValue() {
        return this.localFixedValue;
    }



    /**
     * Auto generated setter method
     *
     * @param param FixedValue
     */
    public void setFixedValue(final java.lang.String param) {
        this.localFixedValueTracker = true;

        this.localFixedValue = param;


    }


    /**
     * field for Form
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaForm localForm;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFormTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaForm
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaForm getForm() {
        return this.localForm;
    }



    /**
     * Auto generated setter method
     *
     * @param param Form
     */
    public void setForm(final org.apache.ws.commons.schema.xsd.XmlSchemaForm param) {
        this.localFormTracker = true;

        this.localForm = param;


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
     * field for Nillable
     */


    protected boolean localNillable;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localNillableTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getNillable() {
        return this.localNillable;
    }



    /**
     * Auto generated setter method
     *
     * @param param Nillable
     */
    public void setNillable(final boolean param) {

        // setting primitive attribute tracker to true
        this.localNillableTracker = true;

        this.localNillable = param;


    }


    /**
     * field for RefName
     */


    protected java.lang.Object localRefName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRefNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getRefName() {
        return this.localRefName;
    }



    /**
     * Auto generated setter method
     *
     * @param param RefName
     */
    public void setRefName(final java.lang.Object param) {
        this.localRefNameTracker = true;

        this.localRefName = param;


    }


    /**
     * field for SchemaType
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaType localSchemaType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSchemaTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaType
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaType getSchemaType() {
        return this.localSchemaType;
    }



    /**
     * Auto generated setter method
     *
     * @param param SchemaType
     */
    public void setSchemaType(final org.apache.ws.commons.schema.xsd.XmlSchemaType param) {
        this.localSchemaTypeTracker = true;

        this.localSchemaType = param;


    }


    /**
     * field for SchemaTypeName
     */


    protected java.lang.Object localSchemaTypeName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSchemaTypeNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getSchemaTypeName() {
        return this.localSchemaTypeName;
    }



    /**
     * Auto generated setter method
     *
     * @param param SchemaTypeName
     */
    public void setSchemaTypeName(final java.lang.Object param) {
        this.localSchemaTypeNameTracker = true;

        this.localSchemaTypeName = param;


    }


    /**
     * field for SubstitutionGroup
     */


    protected java.lang.Object localSubstitutionGroup;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSubstitutionGroupTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getSubstitutionGroup() {
        return this.localSubstitutionGroup;
    }



    /**
     * Auto generated setter method
     *
     * @param param SubstitutionGroup
     */
    public void setSubstitutionGroup(final java.lang.Object param) {
        this.localSubstitutionGroupTracker = true;

        this.localSubstitutionGroup = param;


    }


    /**
     * field for Type
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaType localType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaType
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaType getType() {
        return this.localType;
    }



    /**
     * Auto generated setter method
     *
     * @param param Type
     */
    public void setType(final org.apache.ws.commons.schema.xsd.XmlSchemaType param) {
        this.localTypeTracker = true;

        this.localType = param;


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


        final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://schema.commons.ws.apache.org/xsd");
        if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                namespacePrefix + ":XmlSchemaElement", xmlWriter);
        } else {
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "XmlSchemaElement", xmlWriter);
        }

        if (this.localLineNumberTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "lineNumber", xmlWriter);

            if (this.localLineNumber == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("lineNumber cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLineNumber));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localLinePositionTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "linePosition", xmlWriter);

            if (this.localLinePosition == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("linePosition cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLinePosition));
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
                this.localMetaInfoMap.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "metaInfoMap"), xmlWriter);
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
                this.localAnnotation.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "annotation"), xmlWriter);
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
        if (this.localMaxOccursTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "maxOccurs", xmlWriter);

            if (this.localMaxOccurs == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("maxOccurs cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMaxOccurs));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localMinOccursTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "minOccurs", xmlWriter);

            if (this.localMinOccurs == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("minOccurs cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMinOccurs));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localQNameTracker) {

            if (this.localQName != null) {
                if (this.localQName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localQName).serialize(
                        new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "QName"), xmlWriter,
                        true);
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
        if (this.local_abstractTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "abstract", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("abstract cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.local_abstract));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localBlockTracker) {
            if (this.localBlock == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "block", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localBlock.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "block"), xmlWriter);
            }
        }
        if (this.localBlockResolvedTracker) {
            if (this.localBlockResolved == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "blockResolved", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localBlockResolved.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "blockResolved"),
                    xmlWriter);
            }
        }
        if (this.localConstraintsTracker) {
            if (this.localConstraints == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "constraints", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localConstraints.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "constraints"), xmlWriter);
            }
        }
        if (this.localDefaultValueTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "defaultValue", xmlWriter);


            if (this.localDefaultValue == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localDefaultValue);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localElementTypeTracker) {

            if (this.localElementType != null) {
                if (this.localElementType instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localElementType).serialize(
                        new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "elementType"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "elementType", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localElementType, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "elementType", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.local_finalTracker) {
            if (this.local_final == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "final", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.local_final.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "final"), xmlWriter);
            }
        }
        if (this.localFixedValueTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "fixedValue", xmlWriter);


            if (this.localFixedValue == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localFixedValue);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFormTracker) {
            if (this.localForm == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "form", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localForm.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "form"), xmlWriter);
            }
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
        if (this.localNillableTracker) {
            namespace = "http://schema.commons.ws.apache.org/xsd";
            writeStartElement(null, namespace, "nillable", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("nillable cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localNillable));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localRefNameTracker) {

            if (this.localRefName != null) {
                if (this.localRefName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localRefName).serialize(
                        new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "refName"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "refName", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localRefName, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "refName", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localSchemaTypeTracker) {
            if (this.localSchemaType == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "schemaType", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSchemaType.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "schemaType"), xmlWriter);
            }
        }
        if (this.localSchemaTypeNameTracker) {

            if (this.localSchemaTypeName != null) {
                if (this.localSchemaTypeName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localSchemaTypeName).serialize(
                        new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "schemaTypeName"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "schemaTypeName", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localSchemaTypeName,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "schemaTypeName", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localSubstitutionGroupTracker) {

            if (this.localSubstitutionGroup != null) {
                if (this.localSubstitutionGroup instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localSubstitutionGroup).serialize(
                        new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "substitutionGroup"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "substitutionGroup", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localSubstitutionGroup,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "substitutionGroup", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localTypeTracker) {
            if (this.localType == null) {

                writeStartElement(null, "http://schema.commons.ws.apache.org/xsd", "type", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localType.serialize(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "type"), xmlWriter);
            }
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


        attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema-instance", "type"));
        attribList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "XmlSchemaElement"));
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

            elementList.add(
                this.localSourceURI == null ? null
                                            : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                this.localSourceURI));
        }
        if (this.localAnnotationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "annotation"));


            elementList.add(this.localAnnotation == null ? null : this.localAnnotation);
        }
        if (this.localIdTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "id"));

            elementList.add(
                this.localId == null ? null
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

                elementList.add(
                    new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "unhandledAttributes"));
                elementList.add(this.localUnhandledAttributes);

            }

        }
        if (this.localMaxOccursTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "maxOccurs"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMaxOccurs));
        }
        if (this.localMinOccursTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "minOccurs"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMinOccurs));
        }
        if (this.localQNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "QName"));


            elementList.add(this.localQName == null ? null : this.localQName);
        }
        if (this.local_abstractTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "abstract"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.local_abstract));
        }
        if (this.localBlockTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "block"));


            elementList.add(this.localBlock == null ? null : this.localBlock);
        }
        if (this.localBlockResolvedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "blockResolved"));


            elementList.add(this.localBlockResolved == null ? null : this.localBlockResolved);
        }
        if (this.localConstraintsTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "constraints"));


            elementList.add(this.localConstraints == null ? null : this.localConstraints);
        }
        if (this.localDefaultValueTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "defaultValue"));

            elementList.add(
                this.localDefaultValue == null ? null
                                               : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                   this.localDefaultValue));
        }
        if (this.localElementTypeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "elementType"));


            elementList.add(this.localElementType == null ? null : this.localElementType);
        }
        if (this.local_finalTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "final"));


            elementList.add(this.local_final == null ? null : this.local_final);
        }
        if (this.localFixedValueTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "fixedValue"));

            elementList.add(
                this.localFixedValue == null ? null
                                             : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                 this.localFixedValue));
        }
        if (this.localFormTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "form"));


            elementList.add(this.localForm == null ? null : this.localForm);
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                       this.localName));
        }
        if (this.localNillableTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "nillable"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localNillable));
        }
        if (this.localRefNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "refName"));


            elementList.add(this.localRefName == null ? null : this.localRefName);
        }
        if (this.localSchemaTypeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "schemaType"));


            elementList.add(this.localSchemaType == null ? null : this.localSchemaType);
        }
        if (this.localSchemaTypeNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "schemaTypeName"));


            elementList.add(this.localSchemaTypeName == null ? null : this.localSchemaTypeName);
        }
        if (this.localSubstitutionGroupTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "substitutionGroup"));


            elementList.add(this.localSubstitutionGroup == null ? null : this.localSubstitutionGroup);
        }
        if (this.localTypeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "type"));


            elementList.add(this.localType == null ? null : this.localType);
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
        public static XmlSchemaElement parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final XmlSchemaElement object = new XmlSchemaElement();

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

                        if (!"XmlSchemaElement".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (XmlSchemaElement) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "lineNumber").equals(
                        reader.getName())) {

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "linePosition").equals(
                        reader.getName())) {

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "metaInfoMap").equals(
                        reader.getName())) {

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "sourceURI").equals(
                        reader.getName())) {

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

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "annotation").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAnnotation(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAnnotation(
                            org.apache.ws.commons.schema.xsd.XmlSchemaAnnotation.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "id").equals(
                        reader.getName())) {

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

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
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

                    object.setUnhandledAttributes(
                        (org.w3c.dom.xsd.Attr[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            org.w3c.dom.xsd.Attr.class, list7));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "maxOccurs").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setMaxOccurs(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setMaxOccurs(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "minOccurs").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setMinOccurs(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setMinOccurs(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "QName").equals(
                        reader.getName())) {

                    object.setQName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "abstract").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.set_abstract(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "block").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setBlock(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setBlock(
                            org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "blockResolved").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setBlockResolved(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setBlockResolved(
                            org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "constraints").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setConstraints(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setConstraints(
                            org.apache.ws.commons.schema.xsd.XmlSchemaObjectCollection.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "defaultValue").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setDefaultValue(
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
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "elementType").equals(
                        reader.getName())) {

                    object.setElementType(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "final").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.set_final(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.set_final(
                            org.apache.ws.commons.schema.xsd.XmlSchemaDerivationMethod.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "fixedValue").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setFixedValue(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "form").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setForm(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setForm(org.apache.ws.commons.schema.xsd.XmlSchemaForm.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "name").equals(
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
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "nillable").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setNillable(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "refName").equals(
                        reader.getName())) {

                    object.setRefName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "schemaType").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSchemaType(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSchemaType(org.apache.ws.commons.schema.xsd.XmlSchemaType.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "schemaTypeName").equals(reader.getName())) {

                    object.setSchemaTypeName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd",
                    "substitutionGroup").equals(reader.getName())) {

                    object.setSubstitutionGroup(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://schema.commons.ws.apache.org/xsd", "type").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setType(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setType(org.apache.ws.commons.schema.xsd.XmlSchemaType.Factory.parse(reader));

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

