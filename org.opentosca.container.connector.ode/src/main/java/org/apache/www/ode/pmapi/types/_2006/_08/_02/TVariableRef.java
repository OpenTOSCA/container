/**
 * TVariableRef.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Reference to a variable.
 */
public class TVariableRef implements java.io.Serializable {
    private java.lang.String iid;  // attribute

    private java.lang.String siid;  // attribute

    private java.lang.String name;  // attribute

    public TVariableRef() {
    }

    public TVariableRef(
        java.lang.String iid,
        java.lang.String siid,
        java.lang.String name) {
        this.iid = iid;
        this.siid = siid;
        this.name = name;
    }

    /**
     * Gets the iid value for this TVariableRef.
     *
     * @return iid
     */
    public java.lang.String getIid() {
        return iid;
    }

    /**
     * Sets the iid value for this TVariableRef.
     *
     * @param iid
     */
    public void setIid(java.lang.String iid) {
        this.iid = iid;
    }

    /**
     * Gets the siid value for this TVariableRef.
     *
     * @return siid
     */
    public java.lang.String getSiid() {
        return siid;
    }

    /**
     * Sets the siid value for this TVariableRef.
     *
     * @param siid
     */
    public void setSiid(java.lang.String siid) {
        this.siid = siid;
    }

    /**
     * Gets the name value for this TVariableRef.
     *
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this TVariableRef.
     *
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TVariableRef)) return false;
        TVariableRef other = (TVariableRef) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.iid == null && other.getIid() == null) ||
                (this.iid != null &&
                    this.iid.equals(other.getIid()))) &&
            ((this.siid == null && other.getSiid() == null) ||
                (this.siid != null &&
                    this.siid.equals(other.getSiid()))) &&
            ((this.name == null && other.getName() == null) ||
                (this.name != null &&
                    this.name.equals(other.getName())));
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
        if (getIid() != null) {
            _hashCode += getIid().hashCode();
        }
        if (getSiid() != null) {
            _hashCode += getSiid().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TVariableRef.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tVariableRef"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("iid");
        attrField.setXmlName(new javax.xml.namespace.QName("", "iid"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("siid");
        attrField.setXmlName(new javax.xml.namespace.QName("", "siid"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("name");
        attrField.setXmlName(new javax.xml.namespace.QName("", "name"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
