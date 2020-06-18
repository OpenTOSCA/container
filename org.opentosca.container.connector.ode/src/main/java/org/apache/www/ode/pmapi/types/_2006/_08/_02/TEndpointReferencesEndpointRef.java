/**
 * TEndpointReferencesEndpointRef.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TEndpointReferencesEndpointRef implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.apache.axis.message.MessageElement[] _any;

    private java.lang.String partnerLink;  // attribute

    private java.lang.String partnerRole;  // attribute

    private java.lang.String myRole;  // attribute

    public TEndpointReferencesEndpointRef() {
    }

    public TEndpointReferencesEndpointRef(
        org.apache.axis.message.MessageElement[] _any,
        java.lang.String partnerLink,
        java.lang.String partnerRole,
        java.lang.String myRole) {
        this._any = _any;
        this.partnerLink = partnerLink;
        this.partnerRole = partnerRole;
        this.myRole = myRole;
    }

    /**
     * Gets the _any value for this TEndpointReferencesEndpointRef.
     *
     * @return _any
     */
    public org.apache.axis.message.MessageElement[] get_any() {
        return _any;
    }

    /**
     * Sets the _any value for this TEndpointReferencesEndpointRef.
     */
    public void set_any(org.apache.axis.message.MessageElement[] _any) {
        this._any = _any;
    }

    /**
     * Gets the partnerLink value for this TEndpointReferencesEndpointRef.
     *
     * @return partnerLink
     */
    public java.lang.String getPartnerLink() {
        return partnerLink;
    }

    /**
     * Sets the partnerLink value for this TEndpointReferencesEndpointRef.
     */
    public void setPartnerLink(java.lang.String partnerLink) {
        this.partnerLink = partnerLink;
    }

    /**
     * Gets the partnerRole value for this TEndpointReferencesEndpointRef.
     *
     * @return partnerRole
     */
    public java.lang.String getPartnerRole() {
        return partnerRole;
    }

    /**
     * Sets the partnerRole value for this TEndpointReferencesEndpointRef.
     */
    public void setPartnerRole(java.lang.String partnerRole) {
        this.partnerRole = partnerRole;
    }

    /**
     * Gets the myRole value for this TEndpointReferencesEndpointRef.
     *
     * @return myRole
     */
    public java.lang.String getMyRole() {
        return myRole;
    }

    /**
     * Sets the myRole value for this TEndpointReferencesEndpointRef.
     */
    public void setMyRole(java.lang.String myRole) {
        this.myRole = myRole;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TEndpointReferencesEndpointRef)) return false;
        TEndpointReferencesEndpointRef other = (TEndpointReferencesEndpointRef) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this._any == null && other.get_any() == null) ||
                (this._any != null &&
                    java.util.Arrays.equals(this._any, other.get_any()))) &&
            ((this.partnerLink == null && other.getPartnerLink() == null) ||
                (this.partnerLink != null &&
                    this.partnerLink.equals(other.getPartnerLink()))) &&
            ((this.partnerRole == null && other.getPartnerRole() == null) ||
                (this.partnerRole != null &&
                    this.partnerRole.equals(other.getPartnerRole()))) &&
            ((this.myRole == null && other.getMyRole() == null) ||
                (this.myRole != null &&
                    this.myRole.equals(other.getMyRole())));
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
        if (get_any() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPartnerLink() != null) {
            _hashCode += getPartnerLink().hashCode();
        }
        if (getPartnerRole() != null) {
            _hashCode += getPartnerRole().hashCode();
        }
        if (getMyRole() != null) {
            _hashCode += getMyRole().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TEndpointReferencesEndpointRef.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tEndpointReferences>endpoint-ref"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("partnerLink");
        attrField.setXmlName(new javax.xml.namespace.QName("", "partner-link"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("partnerRole");
        attrField.setXmlName(new javax.xml.namespace.QName("", "partner-role"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("myRole");
        attrField.setXmlName(new javax.xml.namespace.QName("", "my-role"));
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
