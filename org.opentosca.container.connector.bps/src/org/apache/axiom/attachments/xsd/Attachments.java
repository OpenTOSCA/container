
/**
 * Attachments.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axiom.attachments.xsd;


/**
 * Attachments bean class
 */

public class Attachments implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = Attachments Namespace URI =
     * http://attachments.axiom.apache.org/xsd Namespace Prefix = ns6
     */


    /**
     *
     */
    private static final long serialVersionUID = 8749144206757981806L;

    /**
     * field for SOAPPartContentID
     */


    protected java.lang.String localSOAPPartContentID;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSOAPPartContentIDTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getSOAPPartContentID() {
        return this.localSOAPPartContentID;
    }



    /**
     * Auto generated setter method
     *
     * @param param SOAPPartContentID
     */
    public void setSOAPPartContentID(final java.lang.String param) {
        this.localSOAPPartContentIDTracker = true;

        this.localSOAPPartContentID = param;


    }


    /**
     * field for SOAPPartContentType
     */


    protected java.lang.String localSOAPPartContentType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSOAPPartContentTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getSOAPPartContentType() {
        return this.localSOAPPartContentType;
    }



    /**
     * Auto generated setter method
     *
     * @param param SOAPPartContentType
     */
    public void setSOAPPartContentType(final java.lang.String param) {
        this.localSOAPPartContentTypeTracker = true;

        this.localSOAPPartContentType = param;


    }


    /**
     * field for SOAPPartInputStream
     */


    protected authclient.java.io.xsd.InputStream localSOAPPartInputStream;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSOAPPartInputStreamTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.io.xsd.InputStream
     */
    public authclient.java.io.xsd.InputStream getSOAPPartInputStream() {
        return this.localSOAPPartInputStream;
    }



    /**
     * Auto generated setter method
     *
     * @param param SOAPPartInputStream
     */
    public void setSOAPPartInputStream(final authclient.java.io.xsd.InputStream param) {
        this.localSOAPPartInputStreamTracker = true;

        this.localSOAPPartInputStream = param;


    }


    /**
     * field for AllContentIDs This was an Array!
     */


    protected java.lang.String[] localAllContentIDs;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAllContentIDsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getAllContentIDs() {
        return this.localAllContentIDs;
    }



    /**
     * validate the array for AllContentIDs
     */
    protected void validateAllContentIDs(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param AllContentIDs
     */
    public void setAllContentIDs(final java.lang.String[] param) {

        validateAllContentIDs(param);

        this.localAllContentIDsTracker = true;

        this.localAllContentIDs = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addAllContentIDs(final java.lang.String param) {
        if (this.localAllContentIDs == null) {
            this.localAllContentIDs = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localAllContentIDsTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localAllContentIDs);
        list.add(param);
        this.localAllContentIDs = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }


    /**
     * field for AttachmentSpecType
     */


    protected java.lang.String localAttachmentSpecType;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localAttachmentSpecTypeTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public java.lang.String getAttachmentSpecType() {
        return this.localAttachmentSpecType;
    }



    /**
     * Auto generated setter method
     *
     * @param param AttachmentSpecType
     */
    public void setAttachmentSpecType(final java.lang.String param) {
        this.localAttachmentSpecTypeTracker = true;

        this.localAttachmentSpecType = param;


    }


    /**
     * field for ContentIDList
     */


    protected java.lang.Object localContentIDList;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localContentIDListTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getContentIDList() {
        return this.localContentIDList;
    }



    /**
     * Auto generated setter method
     *
     * @param param ContentIDList
     */
    public void setContentIDList(final java.lang.Object param) {
        this.localContentIDListTracker = true;

        this.localContentIDList = param;


    }


    /**
     * field for ContentIDSet
     */


    protected authclient.java.util.xsd.Set localContentIDSet;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localContentIDSetTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Set
     */
    public authclient.java.util.xsd.Set getContentIDSet() {
        return this.localContentIDSet;
    }



    /**
     * Auto generated setter method
     *
     * @param param ContentIDSet
     */
    public void setContentIDSet(final authclient.java.util.xsd.Set param) {
        this.localContentIDSetTracker = true;

        this.localContentIDSet = param;


    }


    /**
     * field for ContentLength
     */


    protected long localContentLength;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localContentLengthTracker = false;


    /**
     * Auto generated getter method
     *
     * @return long
     */
    public long getContentLength() {
        return this.localContentLength;
    }



    /**
     * Auto generated setter method
     *
     * @param param ContentLength
     */
    public void setContentLength(final long param) {

        // setting primitive attribute tracker to true
        this.localContentLengthTracker = param != java.lang.Long.MIN_VALUE;

        this.localContentLength = param;


    }


    /**
     * field for IncomingAttachmentStreams
     */


    protected org.apache.axiom.attachments.xsd.IncomingAttachmentStreams localIncomingAttachmentStreams;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localIncomingAttachmentStreamsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.attachments.xsd.IncomingAttachmentStreams
     */
    public org.apache.axiom.attachments.xsd.IncomingAttachmentStreams getIncomingAttachmentStreams() {
        return this.localIncomingAttachmentStreams;
    }



    /**
     * Auto generated setter method
     *
     * @param param IncomingAttachmentStreams
     */
    public void setIncomingAttachmentStreams(final org.apache.axiom.attachments.xsd.IncomingAttachmentStreams param) {
        this.localIncomingAttachmentStreamsTracker = true;

        this.localIncomingAttachmentStreams = param;


    }


    /**
     * field for IncomingAttachmentsAsSingleStream
     */


    protected authclient.java.io.xsd.InputStream localIncomingAttachmentsAsSingleStream;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localIncomingAttachmentsAsSingleStreamTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.io.xsd.InputStream
     */
    public authclient.java.io.xsd.InputStream getIncomingAttachmentsAsSingleStream() {
        return this.localIncomingAttachmentsAsSingleStream;
    }



    /**
     * Auto generated setter method
     *
     * @param param IncomingAttachmentsAsSingleStream
     */
    public void setIncomingAttachmentsAsSingleStream(final authclient.java.io.xsd.InputStream param) {
        this.localIncomingAttachmentsAsSingleStreamTracker = true;

        this.localIncomingAttachmentsAsSingleStream = param;


    }


    /**
     * field for LifecycleManager
     */


    protected org.apache.axiom.attachments.lifecycle.xsd.LifecycleManager localLifecycleManager;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localLifecycleManagerTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.attachments.lifecycle.xsd.LifecycleManager
     */
    public org.apache.axiom.attachments.lifecycle.xsd.LifecycleManager getLifecycleManager() {
        return this.localLifecycleManager;
    }



    /**
     * Auto generated setter method
     *
     * @param param LifecycleManager
     */
    public void setLifecycleManager(final org.apache.axiom.attachments.lifecycle.xsd.LifecycleManager param) {
        this.localLifecycleManagerTracker = true;

        this.localLifecycleManager = param;


    }


    /**
     * field for Map
     */


    protected authclient.java.util.xsd.Map localMap;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localMapTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Map
     */
    public authclient.java.util.xsd.Map getMap() {
        return this.localMap;
    }



    /**
     * Auto generated setter method
     *
     * @param param Map
     */
    public void setMap(final authclient.java.util.xsd.Map param) {
        this.localMapTracker = true;

        this.localMap = param;


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
                "http://attachments.axiom.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":Attachments", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Attachments", xmlWriter);
            }


        }
        if (this.localSOAPPartContentIDTracker) {
            namespace = "http://attachments.axiom.apache.org/xsd";
            writeStartElement(null, namespace, "SOAPPartContentID", xmlWriter);


            if (this.localSOAPPartContentID == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localSOAPPartContentID);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localSOAPPartContentTypeTracker) {
            namespace = "http://attachments.axiom.apache.org/xsd";
            writeStartElement(null, namespace, "SOAPPartContentType", xmlWriter);


            if (this.localSOAPPartContentType == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localSOAPPartContentType);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localSOAPPartInputStreamTracker) {
            if (this.localSOAPPartInputStream == null) {

                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "SOAPPartInputStream", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSOAPPartInputStream.serialize(
                    new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "SOAPPartInputStream"),
                    xmlWriter);
            }
        }
        if (this.localAllContentIDsTracker) {
            if (this.localAllContentIDs != null) {
                namespace = "http://attachments.axiom.apache.org/xsd";
                for (final String localAllContentID : this.localAllContentIDs) {

                    if (localAllContentID != null) {

                        writeStartElement(null, namespace, "allContentIDs", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAllContentID));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://attachments.axiom.apache.org/xsd";
                        writeStartElement(null, namespace, "allContentIDs", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "allContentIDs", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localAttachmentSpecTypeTracker) {
            namespace = "http://attachments.axiom.apache.org/xsd";
            writeStartElement(null, namespace, "attachmentSpecType", xmlWriter);


            if (this.localAttachmentSpecType == null) {
                // write the nil attribute

                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

            } else {


                xmlWriter.writeCharacters(this.localAttachmentSpecType);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localContentIDListTracker) {

            if (this.localContentIDList != null) {
                if (this.localContentIDList instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localContentIDList).serialize(
                        new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentIDList"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "contentIDList", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localContentIDList,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "contentIDList", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localContentIDSetTracker) {
            if (this.localContentIDSet == null) {

                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "contentIDSet", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localContentIDSet.serialize(
                    new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentIDSet"),
                    xmlWriter);
            }
        }
        if (this.localContentLengthTracker) {
            namespace = "http://attachments.axiom.apache.org/xsd";
            writeStartElement(null, namespace, "contentLength", xmlWriter);

            if (this.localContentLength == java.lang.Long.MIN_VALUE) {

                throw new org.apache.axis2.databinding.ADBException("contentLength cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localContentLength));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localIncomingAttachmentStreamsTracker) {
            if (this.localIncomingAttachmentStreams == null) {

                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "incomingAttachmentStreams",
                    xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localIncomingAttachmentStreams.serialize(new javax.xml.namespace.QName(
                    "http://attachments.axiom.apache.org/xsd", "incomingAttachmentStreams"), xmlWriter);
            }
        }
        if (this.localIncomingAttachmentsAsSingleStreamTracker) {
            if (this.localIncomingAttachmentsAsSingleStream == null) {

                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "incomingAttachmentsAsSingleStream",
                    xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localIncomingAttachmentsAsSingleStream.serialize(new javax.xml.namespace.QName(
                    "http://attachments.axiom.apache.org/xsd", "incomingAttachmentsAsSingleStream"), xmlWriter);
            }
        }
        if (this.localLifecycleManagerTracker) {
            if (this.localLifecycleManager == null) {

                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "lifecycleManager", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localLifecycleManager.serialize(
                    new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "lifecycleManager"),
                    xmlWriter);
            }
        }
        if (this.localMapTracker) {
            if (this.localMap == null) {

                writeStartElement(null, "http://attachments.axiom.apache.org/xsd", "map", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localMap.serialize(new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "map"),
                    xmlWriter);
            }
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://attachments.axiom.apache.org/xsd")) {
            return "ns6";
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

        if (this.localSOAPPartContentIDTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "SOAPPartContentID"));

            elementList.add(
                this.localSOAPPartContentID == null ? null
                                                    : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                        this.localSOAPPartContentID));
        }
        if (this.localSOAPPartContentTypeTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "SOAPPartContentType"));

            elementList.add(
                this.localSOAPPartContentType == null ? null
                                                      : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                          this.localSOAPPartContentType));
        }
        if (this.localSOAPPartInputStreamTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "SOAPPartInputStream"));


            elementList.add(this.localSOAPPartInputStream == null ? null : this.localSOAPPartInputStream);
        }
        if (this.localAllContentIDsTracker) {
            if (this.localAllContentIDs != null) {
                for (final String localAllContentID : this.localAllContentIDs) {

                    if (localAllContentID != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "allContentIDs"));
                        elementList.add(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAllContentID));
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "allContentIDs"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(
                    new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "allContentIDs"));
                elementList.add(null);

            }

        }
        if (this.localAttachmentSpecTypeTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "attachmentSpecType"));

            elementList.add(
                this.localAttachmentSpecType == null ? null
                                                     : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                                         this.localAttachmentSpecType));
        }
        if (this.localContentIDListTracker) {
            elementList.add(new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentIDList"));


            elementList.add(this.localContentIDList == null ? null : this.localContentIDList);
        }
        if (this.localContentIDSetTracker) {
            elementList.add(new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentIDSet"));


            elementList.add(this.localContentIDSet == null ? null : this.localContentIDSet);
        }
        if (this.localContentLengthTracker) {
            elementList.add(new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentLength"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localContentLength));
        }
        if (this.localIncomingAttachmentStreamsTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "incomingAttachmentStreams"));


            elementList.add(this.localIncomingAttachmentStreams == null ? null : this.localIncomingAttachmentStreams);
        }
        if (this.localIncomingAttachmentsAsSingleStreamTracker) {
            elementList.add(new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                "incomingAttachmentsAsSingleStream"));


            elementList.add(
                this.localIncomingAttachmentsAsSingleStream == null ? null
                                                                    : this.localIncomingAttachmentsAsSingleStream);
        }
        if (this.localLifecycleManagerTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "lifecycleManager"));


            elementList.add(this.localLifecycleManager == null ? null : this.localLifecycleManager);
        }
        if (this.localMapTracker) {
            elementList.add(new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "map"));


            elementList.add(this.localMap == null ? null : this.localMap);
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
        public static Attachments parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final Attachments object = new Attachments();

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

                        if (!"Attachments".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Attachments) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                                type, reader);
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "SOAPPartContentID").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setSOAPPartContentID(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "SOAPPartContentType").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setSOAPPartContentType(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "SOAPPartInputStream").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSOAPPartInputStream(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSOAPPartInputStream(authclient.java.io.xsd.InputStream.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "allContentIDs").equals(
                        reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list4.add(null);

                        reader.next();
                    } else {
                        list4.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone4 = false;
                    while (!loopDone4) {
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
                            loopDone4 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                                "allContentIDs").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list4.add(null);

                                    reader.next();
                                } else {
                                    list4.add(reader.getElementText());
                                }
                            } else {
                                loopDone4 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setAllContentIDs((java.lang.String[]) list4.toArray(new java.lang.String[list4.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "attachmentSpecType").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                        final java.lang.String content = reader.getElementText();

                        object.setAttachmentSpecType(
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
                    && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentIDList").equals(
                        reader.getName())) {

                    object.setContentIDList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentIDSet").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setContentIDSet(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setContentIDSet(authclient.java.util.xsd.Set.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "contentLength").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setContentLength(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                    object.setContentLength(java.lang.Long.MIN_VALUE);

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "incomingAttachmentStreams").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setIncomingAttachmentStreams(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setIncomingAttachmentStreams(
                            org.apache.axiom.attachments.xsd.IncomingAttachmentStreams.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "incomingAttachmentsAsSingleStream").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setIncomingAttachmentsAsSingleStream(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setIncomingAttachmentsAsSingleStream(
                            authclient.java.io.xsd.InputStream.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd",
                    "lifecycleManager").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setLifecycleManager(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setLifecycleManager(
                            org.apache.axiom.attachments.lifecycle.xsd.LifecycleManager.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://attachments.axiom.apache.org/xsd", "map").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setMap(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setMap(authclient.java.util.xsd.Map.Factory.parse(reader));

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

