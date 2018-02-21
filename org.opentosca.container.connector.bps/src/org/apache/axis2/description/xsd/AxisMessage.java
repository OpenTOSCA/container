
/**
 * AxisMessage.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.description.xsd;


/**
 * AxisMessage bean class
 */

public class AxisMessage implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = AxisMessage Namespace URI =
     * http://description.axis2.apache.org/xsd Namespace Prefix = ns19
     */


    /**
     *
     */
    private static final long serialVersionUID = -3847681205553387581L;

    /**
     * field for AxisOperation
     */


    protected org.apache.axis2.description.xsd.AxisOperation localAxisOperation;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisOperationTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.description.xsd.AxisOperation
     */
    public org.apache.axis2.description.xsd.AxisOperation getAxisOperation() {
        return this.localAxisOperation;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisOperation
     */
    public void setAxisOperation(final org.apache.axis2.description.xsd.AxisOperation param) {
        this.localAxisOperationTracker = true;

        this.localAxisOperation = param;


    }


    /**
     * field for Direction
     */


    protected java.lang.String localDirection;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localDirectionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getDirection() {
        return this.localDirection;
    }



    /**
     * Auto generated setter method
     *
     * @param param Direction
     */
    public void setDirection(final java.lang.String param) {
        this.localDirectionTracker = true;

        this.localDirection = param;


    }


    /**
     * field for EffectivePolicy
     */


    protected org.apache.neethi.xsd.Policy localEffectivePolicy;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localEffectivePolicyTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.neethi.xsd.Policy
     */
    public org.apache.neethi.xsd.Policy getEffectivePolicy() {
        return this.localEffectivePolicy;
    }



    /**
     * Auto generated setter method
     *
     * @param param EffectivePolicy
     */
    public void setEffectivePolicy(final org.apache.neethi.xsd.Policy param) {
        this.localEffectivePolicyTracker = true;

        this.localEffectivePolicy = param;


    }


    /**
     * field for ElementQName
     */


    protected java.lang.Object localElementQName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localElementQNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getElementQName() {
        return this.localElementQName;
    }



    /**
     * Auto generated setter method
     *
     * @param param ElementQName
     */
    public void setElementQName(final java.lang.Object param) {
        this.localElementQNameTracker = true;

        this.localElementQName = param;


    }


    /**
     * field for ExtensibilityAttributes
     */


    protected java.lang.Object localExtensibilityAttributes;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localExtensibilityAttributesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getExtensibilityAttributes() {
        return this.localExtensibilityAttributes;
    }



    /**
     * Auto generated setter method
     *
     * @param param ExtensibilityAttributes
     */
    public void setExtensibilityAttributes(final java.lang.Object param) {
        this.localExtensibilityAttributesTracker = true;

        this.localExtensibilityAttributes = param;


    }


    /**
     * field for Key
     */


    protected java.lang.Object localKey;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localKeyTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getKey() {
        return this.localKey;
    }



    /**
     * Auto generated setter method
     *
     * @param param Key
     */
    public void setKey(final java.lang.Object param) {
        this.localKeyTracker = true;

        this.localKey = param;


    }


    /**
     * field for MessageFlow
     */


    protected java.lang.Object localMessageFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessageFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getMessageFlow() {
        return this.localMessageFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param MessageFlow
     */
    public void setMessageFlow(final java.lang.Object param) {
        this.localMessageFlowTracker = true;

        this.localMessageFlow = param;


    }


    /**
     * field for MessagePartName
     */


    protected java.lang.String localMessagePartName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessagePartNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getMessagePartName() {
        return this.localMessagePartName;
    }



    /**
     * Auto generated setter method
     *
     * @param param MessagePartName
     */
    public void setMessagePartName(final java.lang.String param) {
        this.localMessagePartNameTracker = true;

        this.localMessagePartName = param;


    }


    /**
     * field for Modulerefs This was an Array!
     */


    protected java.lang.String[] localModulerefs;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localModulerefsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getModulerefs() {
        return this.localModulerefs;
    }



    /**
     * validate the array for Modulerefs
     */
    protected void validateModulerefs(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param Modulerefs
     */
    public void setModulerefs(final java.lang.String[] param) {

        validateModulerefs(param);

        this.localModulerefsTracker = true;

        this.localModulerefs = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addModulerefs(final java.lang.String param) {
        if (this.localModulerefs == null) {
            this.localModulerefs = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localModulerefsTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localModulerefs);
        list.add(param);
        this.localModulerefs = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

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
     * field for PartName
     */


    protected java.lang.String localPartName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPartNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getPartName() {
        return this.localPartName;
    }



    /**
     * Auto generated setter method
     *
     * @param param PartName
     */
    public void setPartName(final java.lang.String param) {
        this.localPartNameTracker = true;

        this.localPartName = param;


    }


    /**
     * field for PolicyUpdated
     */


    protected boolean localPolicyUpdated;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPolicyUpdatedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getPolicyUpdated() {
        return this.localPolicyUpdated;
    }



    /**
     * Auto generated setter method
     *
     * @param param PolicyUpdated
     */
    public void setPolicyUpdated(final boolean param) {

        // setting primitive attribute tracker to true
        this.localPolicyUpdatedTracker = true;

        this.localPolicyUpdated = param;


    }


    /**
     * field for SchemaElement
     */


    protected org.apache.ws.commons.schema.xsd.XmlSchemaElement localSchemaElement;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSchemaElementTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.ws.commons.schema.xsd.XmlSchemaElement
     */
    public org.apache.ws.commons.schema.xsd.XmlSchemaElement getSchemaElement() {
        return this.localSchemaElement;
    }



    /**
     * Auto generated setter method
     *
     * @param param SchemaElement
     */
    public void setSchemaElement(final org.apache.ws.commons.schema.xsd.XmlSchemaElement param) {
        this.localSchemaElementTracker = true;

        this.localSchemaElement = param;


    }


    /**
     * field for SoapHeaders
     */


    protected java.lang.Object localSoapHeaders;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSoapHeadersTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getSoapHeaders() {
        return this.localSoapHeaders;
    }



    /**
     * Auto generated setter method
     *
     * @param param SoapHeaders
     */
    public void setSoapHeaders(final java.lang.Object param) {
        this.localSoapHeadersTracker = true;

        this.localSoapHeaders = param;


    }


    /**
     * field for Wrapped
     */


    protected boolean localWrapped;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localWrappedTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getWrapped() {
        return this.localWrapped;
    }



    /**
     * Auto generated setter method
     *
     * @param param Wrapped
     */
    public void setWrapped(final boolean param) {

        // setting primitive attribute tracker to true
        this.localWrappedTracker = true;

        this.localWrapped = param;


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
                registerPrefix(xmlWriter, "http://description.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":AxisMessage", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "AxisMessage", xmlWriter);
            }


        }
        if (this.localAxisOperationTracker) {
            if (this.localAxisOperation == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisOperation", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisOperation.serialize(new javax.xml.namespace.QName(
                    "http://description.axis2.apache.org/xsd", "axisOperation"), xmlWriter);
            }
        }
        if (this.localDirectionTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "direction", xmlWriter);


            if (this.localDirection == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localDirection);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localEffectivePolicyTracker) {
            if (this.localEffectivePolicy == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "effectivePolicy", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localEffectivePolicy.serialize(new javax.xml.namespace.QName(
                    "http://description.axis2.apache.org/xsd", "effectivePolicy"), xmlWriter);
            }
        }
        if (this.localElementQNameTracker) {

            if (this.localElementQName != null) {
                if (this.localElementQName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localElementQName).serialize(new javax.xml.namespace.QName(
                        "http://description.axis2.apache.org/xsd", "elementQName"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "elementQName", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localElementQName,
                                                                                      xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "elementQName", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localExtensibilityAttributesTracker) {

            if (this.localExtensibilityAttributes != null) {
                if (this.localExtensibilityAttributes instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localExtensibilityAttributes).serialize(new javax.xml.namespace.QName(
                        "http://description.axis2.apache.org/xsd", "extensibilityAttributes"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "extensibilityAttributes",
                                      xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localExtensibilityAttributes,
                                                                                      xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "extensibilityAttributes",
                                  xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localKeyTracker) {

            if (this.localKey != null) {
                if (this.localKey instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localKey).serialize(new javax.xml.namespace.QName(
                        "http://description.axis2.apache.org/xsd", "key"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localKey, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localMessageFlowTracker) {

            if (this.localMessageFlow != null) {
                if (this.localMessageFlow instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localMessageFlow).serialize(new javax.xml.namespace.QName(
                        "http://description.axis2.apache.org/xsd", "messageFlow"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageFlow", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localMessageFlow, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localMessagePartNameTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "messagePartName", xmlWriter);


            if (this.localMessagePartName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localMessagePartName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localModulerefsTracker) {
            if (this.localModulerefs != null) {
                namespace = "http://description.axis2.apache.org/xsd";
                for (final String localModuleref : this.localModulerefs) {

                    if (localModuleref != null) {

                        writeStartElement(null, namespace, "modulerefs", xmlWriter);


                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModuleref));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://description.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "modulerefs", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "modulerefs", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localNameTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "name", xmlWriter);


            if (this.localName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localPartNameTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "partName", xmlWriter);


            if (this.localPartName == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localPartName);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localPolicyUpdatedTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "policyUpdated", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("policyUpdated cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPolicyUpdated));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localSchemaElementTracker) {
            if (this.localSchemaElement == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "schemaElement", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSchemaElement.serialize(new javax.xml.namespace.QName(
                    "http://description.axis2.apache.org/xsd", "schemaElement"), xmlWriter);
            }
        }
        if (this.localSoapHeadersTracker) {

            if (this.localSoapHeaders != null) {
                if (this.localSoapHeaders instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localSoapHeaders).serialize(new javax.xml.namespace.QName(
                        "http://description.axis2.apache.org/xsd", "soapHeaders"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "soapHeaders", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localSoapHeaders, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "soapHeaders", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localWrappedTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "wrapped", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("wrapped cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWrapped));
            }

            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://description.axis2.apache.org/xsd")) {
            return "ns19";
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

        if (this.localAxisOperationTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "axisOperation"));


            elementList.add(this.localAxisOperation == null ? null : this.localAxisOperation);
        }
        if (this.localDirectionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "direction"));

            elementList.add(this.localDirection == null ? null
                                                        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localDirection));
        }
        if (this.localEffectivePolicyTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                "effectivePolicy"));


            elementList.add(this.localEffectivePolicy == null ? null : this.localEffectivePolicy);
        }
        if (this.localElementQNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "elementQName"));


            elementList.add(this.localElementQName == null ? null : this.localElementQName);
        }
        if (this.localExtensibilityAttributesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                "extensibilityAttributes"));


            elementList.add(this.localExtensibilityAttributes == null ? null : this.localExtensibilityAttributes);
        }
        if (this.localKeyTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "key"));


            elementList.add(this.localKey == null ? null : this.localKey);
        }
        if (this.localMessageFlowTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messageFlow"));


            elementList.add(this.localMessageFlow == null ? null : this.localMessageFlow);
        }
        if (this.localMessagePartNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                "messagePartName"));

            elementList.add(this.localMessagePartName == null ? null
                                                              : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMessagePartName));
        }
        if (this.localModulerefsTracker) {
            if (this.localModulerefs != null) {
                for (final String localModuleref : this.localModulerefs) {

                    if (localModuleref != null) {
                        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                            "modulerefs"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModuleref));
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                            "modulerefs"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "modulerefs"));
                elementList.add(null);

            }

        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name"));

            elementList.add(this.localName == null ? null
                                                   : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
        }
        if (this.localPartNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "partName"));

            elementList.add(this.localPartName == null ? null
                                                       : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPartName));
        }
        if (this.localPolicyUpdatedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "policyUpdated"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPolicyUpdated));
        }
        if (this.localSchemaElementTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "schemaElement"));


            elementList.add(this.localSchemaElement == null ? null : this.localSchemaElement);
        }
        if (this.localSoapHeadersTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "soapHeaders"));


            elementList.add(this.localSoapHeaders == null ? null : this.localSoapHeaders);
        }
        if (this.localWrappedTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "wrapped"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWrapped));
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
        public static AxisMessage parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final AxisMessage object = new AxisMessage();

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

                        if (!"AxisMessage".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (AxisMessage) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                                                                                                                type,
                                                                                                                reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list9 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "axisOperation").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setAxisOperation(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setAxisOperation(org.apache.axis2.description.xsd.AxisOperation.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "direction").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setDirection(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "effectivePolicy").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setEffectivePolicy(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setEffectivePolicy(org.apache.neethi.xsd.Policy.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "elementQName").equals(reader.getName())) {

                    object.setElementQName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                             org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "extensibilityAttributes").equals(reader.getName())) {

                    object.setExtensibilityAttributes(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "key").equals(reader.getName())) {

                    object.setKey(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                    org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "messageFlow").equals(reader.getName())) {

                    object.setMessageFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "messagePartName").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setMessagePartName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "modulerefs").equals(reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list9.add(null);

                        reader.next();
                    } else {
                        list9.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone9 = false;
                    while (!loopDone9) {
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
                            loopDone9 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                "modulerefs").equals(reader.getName())) {

                                nillableValue =
                                    reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list9.add(null);

                                    reader.next();
                                } else {
                                    list9.add(reader.getElementText());
                                }
                            } else {
                                loopDone9 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setModulerefs((java.lang.String[]) list9.toArray(new java.lang.String[list9.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "partName").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setPartName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "policyUpdated").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setPolicyUpdated(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "schemaElement").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSchemaElement(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSchemaElement(org.apache.ws.commons.schema.xsd.XmlSchemaElement.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "soapHeaders").equals(reader.getName())) {

                    object.setSoapHeaders(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                                                            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "wrapped").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setWrapped(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

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

