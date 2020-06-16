/**
 * TScopeRef.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Reference to a scope.
 */
public class TScopeRef implements java.io.Serializable {
    private java.lang.String siid;  // attribute

    private java.lang.String name;  // attribute

    private java.lang.String modelId;  // attribute

    private org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus status;  // attribute

    public TScopeRef() {
    }

    public TScopeRef(
        java.lang.String siid,
        java.lang.String name,
        java.lang.String modelId,
        org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus status) {
        this.siid = siid;
        this.name = name;
        this.modelId = modelId;
        this.status = status;
    }

    /**
     * Gets the siid value for this TScopeRef.
     *
     * @return siid
     */
    public java.lang.String getSiid() {
        return siid;
    }

    /**
     * Sets the siid value for this TScopeRef.
     *
     * @param siid
     */
    public void setSiid(java.lang.String siid) {
        this.siid = siid;
    }

    /**
     * Gets the name value for this TScopeRef.
     *
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this TScopeRef.
     *
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the modelId value for this TScopeRef.
     *
     * @return modelId
     */
    public java.lang.String getModelId() {
        return modelId;
    }

    /**
     * Sets the modelId value for this TScopeRef.
     *
     * @param modelId
     */
    public void setModelId(java.lang.String modelId) {
        this.modelId = modelId;
    }

    /**
     * Gets the status value for this TScopeRef.
     *
     * @return status
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus getStatus() {
        return status;
    }

    /**
     * Sets the status value for this TScopeRef.
     *
     * @param status
     */
    public void setStatus(org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TScopeRef)) return false;
        TScopeRef other = (TScopeRef) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.siid == null && other.getSiid() == null) ||
                (this.siid != null &&
                    this.siid.equals(other.getSiid()))) &&
            ((this.name == null && other.getName() == null) ||
                (this.name != null &&
                    this.name.equals(other.getName()))) &&
            ((this.modelId == null && other.getModelId() == null) ||
                (this.modelId != null &&
                    this.modelId.equals(other.getModelId()))) &&
            ((this.status == null && other.getStatus() == null) ||
                (this.status != null &&
                    this.status.equals(other.getStatus())));
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
        if (getSiid() != null) {
            _hashCode += getSiid().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getModelId() != null) {
            _hashCode += getModelId().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TScopeRef.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeRef"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("siid");
        attrField.setXmlName(new javax.xml.namespace.QName("", "siid"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("name");
        attrField.setXmlName(new javax.xml.namespace.QName("", "name"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("modelId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "modelId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("status");
        attrField.setXmlName(new javax.xml.namespace.QName("", "status"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeStatus"));
        typeDesc.addFieldDesc(attrField);
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
