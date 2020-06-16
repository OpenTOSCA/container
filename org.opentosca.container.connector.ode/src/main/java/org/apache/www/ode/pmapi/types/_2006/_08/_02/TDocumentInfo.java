/**
 * TDocumentInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Information about a document.
 */
public class TDocumentInfo implements java.io.Serializable {
    /* Name of the document. */
    private java.lang.String name;

    /* Type of document (e.g.
     *                         WSDL/BPEL/etc...). */
    private org.apache.axis.types.URI type;

    /* URL where the document can be
     *                         retrieved. */
    private org.apache.axis.types.URI source;

    public TDocumentInfo() {
    }

    public TDocumentInfo(
        java.lang.String name,
        org.apache.axis.types.URI type,
        org.apache.axis.types.URI source) {
        this.name = name;
        this.type = type;
        this.source = source;
    }

    /**
     * Gets the name value for this TDocumentInfo.
     *
     * @return name   * Name of the document.
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this TDocumentInfo.
     *
     * @param name * Name of the document.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the type value for this TDocumentInfo.
     *
     * @return type   * Type of document (e.g. WSDL/BPEL/etc...).
     */
    public org.apache.axis.types.URI getType() {
        return type;
    }

    /**
     * Sets the type value for this TDocumentInfo.
     *
     * @param type * Type of document (e.g. WSDL/BPEL/etc...).
     */
    public void setType(org.apache.axis.types.URI type) {
        this.type = type;
    }

    /**
     * Gets the source value for this TDocumentInfo.
     *
     * @return source   * URL where the document can be retrieved.
     */
    public org.apache.axis.types.URI getSource() {
        return source;
    }

    /**
     * Sets the source value for this TDocumentInfo.
     *
     * @param source * URL where the document can be retrieved.
     */
    public void setSource(org.apache.axis.types.URI source) {
        this.source = source;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TDocumentInfo)) return false;
        TDocumentInfo other = (TDocumentInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.name == null && other.getName() == null) ||
                (this.name != null &&
                    this.name.equals(other.getName()))) &&
            ((this.type == null && other.getType() == null) ||
                (this.type != null &&
                    this.type.equals(other.getType()))) &&
            ((this.source == null && other.getSource() == null) ||
                (this.source != null &&
                    this.source.equals(other.getSource())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TDocumentInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tDocumentInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
        java.lang.String mechType,
        java.lang.Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return
            new org.apache.axis.encoding.ser.BeanSerializer(
                _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
        java.lang.String mechType,
        java.lang.Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return
            new org.apache.axis.encoding.ser.BeanDeserializer(
                _javaType, _xmlType, typeDesc);
    }
}
