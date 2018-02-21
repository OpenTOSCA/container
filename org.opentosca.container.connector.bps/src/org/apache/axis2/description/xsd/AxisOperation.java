
/**
 * AxisOperation.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.description.xsd;


/**
 * AxisOperation bean class
 */

public abstract class AxisOperation implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = AxisOperation Namespace URI =
     * http://description.axis2.apache.org/xsd Namespace Prefix = ns19
     */


    /**
     *
     */
    private static final long serialVersionUID = 3269888011500769352L;

    /**
     * field for WSAMappingList
     */


    protected java.lang.Object localWSAMappingList;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localWSAMappingListTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getWSAMappingList() {
        return this.localWSAMappingList;
    }



    /**
     * Auto generated setter method
     *
     * @param param WSAMappingList
     */
    public void setWSAMappingList(final java.lang.Object param) {
        this.localWSAMappingListTracker = true;

        this.localWSAMappingList = param;


    }


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
     * field for AxisSpecificMEPConstant
     */


    protected int localAxisSpecificMEPConstant;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAxisSpecificMEPConstantTracker = false;


    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getAxisSpecificMEPConstant() {
        return this.localAxisSpecificMEPConstant;
    }



    /**
     * Auto generated setter method
     *
     * @param param AxisSpecificMEPConstant
     */
    public void setAxisSpecificMEPConstant(final int param) {

        // setting primitive attribute tracker to true
        this.localAxisSpecificMEPConstantTracker = param != java.lang.Integer.MIN_VALUE;

        this.localAxisSpecificMEPConstant = param;


    }


    /**
     * field for ControlOperation
     */


    protected boolean localControlOperation;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localControlOperationTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getControlOperation() {
        return this.localControlOperation;
    }



    /**
     * Auto generated setter method
     *
     * @param param ControlOperation
     */
    public void setControlOperation(final boolean param) {

        // setting primitive attribute tracker to true
        this.localControlOperationTracker = true;

        this.localControlOperation = param;


    }


    /**
     * field for FaultAction
     */


    protected java.lang.String localFaultAction;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultActionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getFaultAction() {
        return this.localFaultAction;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultAction
     */
    public void setFaultAction(final java.lang.String param) {
        this.localFaultActionTracker = true;

        this.localFaultAction = param;


    }


    /**
     * field for FaultActionNames This was an Array!
     */


    protected java.lang.String[] localFaultActionNames;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultActionNamesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getFaultActionNames() {
        return this.localFaultActionNames;
    }



    /**
     * validate the array for FaultActionNames
     */
    protected void validateFaultActionNames(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param FaultActionNames
     */
    public void setFaultActionNames(final java.lang.String[] param) {

        validateFaultActionNames(param);

        this.localFaultActionNamesTracker = true;

        this.localFaultActionNames = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addFaultActionNames(final java.lang.String param) {
        if (this.localFaultActionNames == null) {
            this.localFaultActionNames = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localFaultActionNamesTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localFaultActionNames);
        list.add(param);
        this.localFaultActionNames = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }


    /**
     * field for FaultMessages
     */


    protected java.lang.Object localFaultMessages;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultMessagesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getFaultMessages() {
        return this.localFaultMessages;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultMessages
     */
    public void setFaultMessages(final java.lang.Object param) {
        this.localFaultMessagesTracker = true;

        this.localFaultMessages = param;


    }


    /**
     * field for InputAction
     */


    protected java.lang.String localInputAction;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInputActionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getInputAction() {
        return this.localInputAction;
    }



    /**
     * Auto generated setter method
     *
     * @param param InputAction
     */
    public void setInputAction(final java.lang.String param) {
        this.localInputActionTracker = true;

        this.localInputAction = param;


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
     * field for MessageExchangePattern
     */


    protected java.lang.String localMessageExchangePattern;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessageExchangePatternTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getMessageExchangePattern() {
        return this.localMessageExchangePattern;
    }



    /**
     * Auto generated setter method
     *
     * @param param MessageExchangePattern
     */
    public void setMessageExchangePattern(final java.lang.String param) {
        this.localMessageExchangePatternTracker = true;

        this.localMessageExchangePattern = param;


    }


    /**
     * field for MessageReceiver
     */


    protected org.apache.axis2.engine.xsd.MessageReceiver localMessageReceiver;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessageReceiverTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.MessageReceiver
     */
    public org.apache.axis2.engine.xsd.MessageReceiver getMessageReceiver() {
        return this.localMessageReceiver;
    }



    /**
     * Auto generated setter method
     *
     * @param param MessageReceiver
     */
    public void setMessageReceiver(final org.apache.axis2.engine.xsd.MessageReceiver param) {
        this.localMessageReceiverTracker = true;

        this.localMessageReceiver = param;


    }


    /**
     * field for Messages
     */


    protected authclient.java.util.xsd.Iterator localMessages;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessagesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Iterator
     */
    public authclient.java.util.xsd.Iterator getMessages() {
        return this.localMessages;
    }



    /**
     * Auto generated setter method
     *
     * @param param Messages
     */
    public void setMessages(final authclient.java.util.xsd.Iterator param) {
        this.localMessagesTracker = true;

        this.localMessages = param;


    }


    /**
     * field for ModuleRefs
     */


    protected java.lang.Object localModuleRefs;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localModuleRefsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getModuleRefs() {
        return this.localModuleRefs;
    }



    /**
     * Auto generated setter method
     *
     * @param param ModuleRefs
     */
    public void setModuleRefs(final java.lang.Object param) {
        this.localModuleRefsTracker = true;

        this.localModuleRefs = param;


    }


    /**
     * field for Name
     */


    protected java.lang.Object localName;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localNameTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getName() {
        return this.localName;
    }



    /**
     * Auto generated setter method
     *
     * @param param Name
     */
    public void setName(final java.lang.Object param) {
        this.localNameTracker = true;

        this.localName = param;


    }


    /**
     * field for OutputAction
     */


    protected java.lang.String localOutputAction;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOutputActionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getOutputAction() {
        return this.localOutputAction;
    }



    /**
     * Auto generated setter method
     *
     * @param param OutputAction
     */
    public void setOutputAction(final java.lang.String param) {
        this.localOutputActionTracker = true;

        this.localOutputAction = param;


    }


    /**
     * field for PhasesInFaultFlow
     */


    protected java.lang.Object localPhasesInFaultFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhasesInFaultFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getPhasesInFaultFlow() {
        return this.localPhasesInFaultFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhasesInFaultFlow
     */
    public void setPhasesInFaultFlow(final java.lang.Object param) {
        this.localPhasesInFaultFlowTracker = true;

        this.localPhasesInFaultFlow = param;


    }


    /**
     * field for PhasesOutFaultFlow
     */


    protected java.lang.Object localPhasesOutFaultFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhasesOutFaultFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getPhasesOutFaultFlow() {
        return this.localPhasesOutFaultFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhasesOutFaultFlow
     */
    public void setPhasesOutFaultFlow(final java.lang.Object param) {
        this.localPhasesOutFaultFlowTracker = true;

        this.localPhasesOutFaultFlow = param;


    }


    /**
     * field for PhasesOutFlow
     */


    protected java.lang.Object localPhasesOutFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhasesOutFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getPhasesOutFlow() {
        return this.localPhasesOutFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhasesOutFlow
     */
    public void setPhasesOutFlow(final java.lang.Object param) {
        this.localPhasesOutFlowTracker = true;

        this.localPhasesOutFlow = param;


    }


    /**
     * field for RemainingPhasesInFlow
     */


    protected java.lang.Object localRemainingPhasesInFlow;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRemainingPhasesInFlowTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getRemainingPhasesInFlow() {
        return this.localRemainingPhasesInFlow;
    }



    /**
     * Auto generated setter method
     *
     * @param param RemainingPhasesInFlow
     */
    public void setRemainingPhasesInFlow(final java.lang.Object param) {
        this.localRemainingPhasesInFlowTracker = true;

        this.localRemainingPhasesInFlow = param;


    }


    /**
     * field for SoapAction
     */


    protected java.lang.String localSoapAction;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSoapActionTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getSoapAction() {
        return this.localSoapAction;
    }



    /**
     * Auto generated setter method
     *
     * @param param SoapAction
     */
    public void setSoapAction(final java.lang.String param) {
        this.localSoapActionTracker = true;

        this.localSoapAction = param;


    }


    /**
     * field for Style
     */


    protected java.lang.String localStyle;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localStyleTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getStyle() {
        return this.localStyle;
    }



    /**
     * Auto generated setter method
     *
     * @param param Style
     */
    public void setStyle(final java.lang.String param) {
        this.localStyleTracker = true;

        this.localStyle = param;


    }


    /**
     * field for WsamappingListE This was an Array!
     */


    protected java.lang.String[] localWsamappingListE;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localWsamappingListETracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getWsamappingListE() {
        return this.localWsamappingListE;
    }



    /**
     * validate the array for WsamappingListE
     */
    protected void validateWsamappingListE(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param WsamappingListE
     */
    public void setWsamappingListE(final java.lang.String[] param) {

        validateWsamappingListE(param);

        this.localWsamappingListETracker = true;

        this.localWsamappingListE = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addWsamappingListE(final java.lang.String param) {
        if (this.localWsamappingListE == null) {
            this.localWsamappingListE = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localWsamappingListETracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localWsamappingListE);
        list.add(param);
        this.localWsamappingListE = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

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


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                "http://description.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":AxisOperation", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "AxisOperation", xmlWriter);
            }


        }
        if (this.localWSAMappingListTracker) {

            if (this.localWSAMappingList != null) {
                if (this.localWSAMappingList instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localWSAMappingList).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "WSAMappingList"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "WSAMappingList", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localWSAMappingList,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "WSAMappingList", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localAxisServiceTracker) {
            if (this.localAxisService == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisService", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localAxisService.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "axisService"), xmlWriter);
            }
        }
        if (this.localAxisSpecificMEPConstantTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "axisSpecificMEPConstant", xmlWriter);

            if (this.localAxisSpecificMEPConstant == java.lang.Integer.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("axisSpecificMEPConstant cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    this.localAxisSpecificMEPConstant));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localControlOperationTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "controlOperation", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("controlOperation cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localControlOperation));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultActionTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "faultAction", xmlWriter);


            if (this.localFaultAction == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localFaultAction);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultActionNamesTracker) {
            if (this.localFaultActionNames != null) {
                namespace = "http://description.axis2.apache.org/xsd";
                for (final String localFaultActionName : this.localFaultActionNames) {

                    if (localFaultActionName != null) {

                        writeStartElement(null, namespace, "faultActionNames", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultActionName));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://description.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "faultActionNames", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultActionNames", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localFaultMessagesTracker) {

            if (this.localFaultMessages != null) {
                if (this.localFaultMessages instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localFaultMessages).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultMessages"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultMessages", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localFaultMessages,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultMessages", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localInputActionTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "inputAction", xmlWriter);


            if (this.localInputAction == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localInputAction);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localKeyTracker) {

            if (this.localKey != null) {
                if (this.localKey instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localKey).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "key"), xmlWriter,
                        true);
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
        if (this.localMessageExchangePatternTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "messageExchangePattern", xmlWriter);


            if (this.localMessageExchangePattern == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localMessageExchangePattern);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localMessageReceiverTracker) {
            if (this.localMessageReceiver == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageReceiver", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localMessageReceiver.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messageReceiver"),
                    xmlWriter);
            }
        }
        if (this.localMessagesTracker) {
            if (this.localMessages == null) {

                writeStartElement(null, "http://description.axis2.apache.org/xsd", "messages", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localMessages.serialize(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messages"), xmlWriter);
            }
        }
        if (this.localModuleRefsTracker) {

            if (this.localModuleRefs != null) {
                if (this.localModuleRefs instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localModuleRefs).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "moduleRefs"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "moduleRefs", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localModuleRefs, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "moduleRefs", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localNameTracker) {

            if (this.localName != null) {
                if (this.localName instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localName).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "name", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localName, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "name", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localOutputActionTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "outputAction", xmlWriter);


            if (this.localOutputAction == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localOutputAction);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localPhasesInFaultFlowTracker) {

            if (this.localPhasesInFaultFlow != null) {
                if (this.localPhasesInFaultFlow instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localPhasesInFaultFlow).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesInFaultFlow"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesInFaultFlow", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localPhasesInFaultFlow,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesInFaultFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localPhasesOutFaultFlowTracker) {

            if (this.localPhasesOutFaultFlow != null) {
                if (this.localPhasesOutFaultFlow instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localPhasesOutFaultFlow).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesOutFaultFlow"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFaultFlow", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localPhasesOutFaultFlow,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFaultFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localPhasesOutFlowTracker) {

            if (this.localPhasesOutFlow != null) {
                if (this.localPhasesOutFlow instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localPhasesOutFlow).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesOutFlow"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFlow", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localPhasesOutFlow,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localRemainingPhasesInFlowTracker) {

            if (this.localRemainingPhasesInFlow != null) {
                if (this.localRemainingPhasesInFlow instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localRemainingPhasesInFlow).serialize(
                        new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                            "remainingPhasesInFlow"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "remainingPhasesInFlow",
                        xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localRemainingPhasesInFlow,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "remainingPhasesInFlow", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localSoapActionTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "soapAction", xmlWriter);


            if (this.localSoapAction == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localSoapAction);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localStyleTracker) {
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "style", xmlWriter);


            if (this.localStyle == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localStyle);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localWsamappingListETracker) {
            if (this.localWsamappingListE != null) {
                namespace = "http://description.axis2.apache.org/xsd";
                for (final String element : this.localWsamappingListE) {

                    if (element != null) {

                        writeStartElement(null, namespace, "wsamappingList", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://description.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "wsamappingList", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://description.axis2.apache.org/xsd", "wsamappingList", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

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

        if (this.localWSAMappingListTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "WSAMappingList"));


            elementList.add(this.localWSAMappingList == null ? null : this.localWSAMappingList);
        }
        if (this.localAxisServiceTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "axisService"));


            elementList.add(this.localAxisService == null ? null : this.localAxisService);
        }
        if (this.localAxisSpecificMEPConstantTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "axisSpecificMEPConstant"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localAxisSpecificMEPConstant));
        }
        if (this.localControlOperationTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "controlOperation"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localControlOperation));
        }
        if (this.localFaultActionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultAction"));

            elementList.add(
                this.localFaultAction == null ? null
                                              : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                  this.localFaultAction));
        }
        if (this.localFaultActionNamesTracker) {
            if (this.localFaultActionNames != null) {
                for (final String localFaultActionName : this.localFaultActionNames) {

                    if (localFaultActionName != null) {
                        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                            "faultActionNames"));
                        elementList.add(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultActionName));
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                            "faultActionNames"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultActionNames"));
                elementList.add(null);

            }

        }
        if (this.localFaultMessagesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultMessages"));


            elementList.add(this.localFaultMessages == null ? null : this.localFaultMessages);
        }
        if (this.localInputActionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inputAction"));

            elementList.add(
                this.localInputAction == null ? null
                                              : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                  this.localInputAction));
        }
        if (this.localKeyTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "key"));


            elementList.add(this.localKey == null ? null : this.localKey);
        }
        if (this.localMessageExchangePatternTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messageExchangePattern"));

            elementList.add(
                this.localMessageExchangePattern == null ? null
                                                         : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                             this.localMessageExchangePattern));
        }
        if (this.localMessageReceiverTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messageReceiver"));


            elementList.add(this.localMessageReceiver == null ? null : this.localMessageReceiver);
        }
        if (this.localMessagesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messages"));


            elementList.add(this.localMessages == null ? null : this.localMessages);
        }
        if (this.localModuleRefsTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "moduleRefs"));


            elementList.add(this.localModuleRefs == null ? null : this.localModuleRefs);
        }
        if (this.localNameTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name"));


            elementList.add(this.localName == null ? null : this.localName);
        }
        if (this.localOutputActionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "outputAction"));

            elementList.add(
                this.localOutputAction == null ? null
                                               : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                   this.localOutputAction));
        }
        if (this.localPhasesInFaultFlowTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesInFaultFlow"));


            elementList.add(this.localPhasesInFaultFlow == null ? null : this.localPhasesInFaultFlow);
        }
        if (this.localPhasesOutFaultFlowTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesOutFaultFlow"));


            elementList.add(this.localPhasesOutFaultFlow == null ? null : this.localPhasesOutFaultFlow);
        }
        if (this.localPhasesOutFlowTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesOutFlow"));


            elementList.add(this.localPhasesOutFlow == null ? null : this.localPhasesOutFlow);
        }
        if (this.localRemainingPhasesInFlowTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "remainingPhasesInFlow"));


            elementList.add(this.localRemainingPhasesInFlow == null ? null : this.localRemainingPhasesInFlow);
        }
        if (this.localSoapActionTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "soapAction"));

            elementList.add(
                this.localSoapAction == null ? null
                                             : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                 this.localSoapAction));
        }
        if (this.localStyleTracker) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "style"));

            elementList.add(this.localStyle == null ? null
                                                    : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                        this.localStyle));
        }
        if (this.localWsamappingListETracker) {
            if (this.localWsamappingListE != null) {
                for (final String element : this.localWsamappingListE) {

                    if (element != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "wsamappingList"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "wsamappingList"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(
                    new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "wsamappingList"));
                elementList.add(null);

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
        public static AxisOperation parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final AxisOperation object = null;

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

                        if (!"AxisOperation".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (AxisOperation) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                                type, reader);
                        }

                        throw new org.apache.axis2.databinding.ADBException(
                            "The an abstract class can not be instantiated !!!");


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list6 = new java.util.ArrayList();

                final java.util.ArrayList list22 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "WSAMappingList").equals(reader.getName())) {

                    object.setWSAMappingList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "axisService").equals(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "axisSpecificMEPConstant").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setAxisSpecificMEPConstant(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setAxisSpecificMEPConstant(java.lang.Integer.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "controlOperation").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setControlOperation(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultAction").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setFaultAction(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "faultActionNames").equals(reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list6.add(null);

                        reader.next();
                    } else {
                        list6.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone6 = false;
                    while (!loopDone6) {
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
                            loopDone6 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                "faultActionNames").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list6.add(null);

                                    reader.next();
                                } else {
                                    list6.add(reader.getElementText());
                                }
                            } else {
                                loopDone6 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setFaultActionNames((java.lang.String[]) list6.toArray(new java.lang.String[list6.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "faultMessages").equals(
                        reader.getName())) {

                    object.setFaultMessages(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "inputAction").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setInputAction(
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
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "key").equals(
                        reader.getName())) {

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
                    "messageExchangePattern").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setMessageExchangePattern(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "messageReceiver").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setMessageReceiver(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setMessageReceiver(org.apache.axis2.engine.xsd.MessageReceiver.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "messages").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setMessages(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setMessages(authclient.java.util.xsd.Iterator.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "moduleRefs").equals(
                        reader.getName())) {

                    object.setModuleRefs(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name").equals(
                        reader.getName())) {

                    object.setName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "outputAction").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setOutputAction(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "phasesInFaultFlow").equals(reader.getName())) {

                    object.setPhasesInFaultFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "phasesOutFaultFlow").equals(reader.getName())) {

                    object.setPhasesOutFaultFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "phasesOutFlow").equals(
                        reader.getName())) {

                    object.setPhasesOutFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                    "remainingPhasesInFlow").equals(reader.getName())) {

                    object.setRemainingPhasesInFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "soapAction").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setSoapAction(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "style").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setStyle(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

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
                    "wsamappingList").equals(reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list22.add(null);

                        reader.next();
                    } else {
                        list22.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone22 = false;
                    while (!loopDone22) {
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
                            loopDone22 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                "wsamappingList").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list22.add(null);

                                    reader.next();
                                } else {
                                    list22.add(reader.getElementText());
                                }
                            } else {
                                loopDone22 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setWsamappingListE((java.lang.String[]) list22.toArray(new java.lang.String[list22.size()]));

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

