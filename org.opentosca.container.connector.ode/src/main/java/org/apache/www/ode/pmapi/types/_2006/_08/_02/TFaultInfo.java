/**
 * TFaultInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Type used for reporting faults.
 */
public class TFaultInfo implements java.io.Serializable {
    private javax.xml.namespace.QName name;

    private java.lang.String explanation;

    private int lineNumber;

    private int aiid;

    private org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfoData data;

    public TFaultInfo() {
    }

    public TFaultInfo(
        javax.xml.namespace.QName name,
        java.lang.String explanation,
        int lineNumber,
        int aiid,
        org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfoData data) {
        this.name = name;
        this.explanation = explanation;
        this.lineNumber = lineNumber;
        this.aiid = aiid;
        this.data = data;
    }

    /**
     * Gets the name value for this TFaultInfo.
     *
     * @return name
     */
    public javax.xml.namespace.QName getName() {
        return name;
    }

    /**
     * Sets the name value for this TFaultInfo.
     */
    public void setName(javax.xml.namespace.QName name) {
        this.name = name;
    }

    /**
     * Gets the explanation value for this TFaultInfo.
     *
     * @return explanation
     */
    public java.lang.String getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation value for this TFaultInfo.
     */
    public void setExplanation(java.lang.String explanation) {
        this.explanation = explanation;
    }

    /**
     * Gets the lineNumber value for this TFaultInfo.
     *
     * @return lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber value for this TFaultInfo.
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the aiid value for this TFaultInfo.
     *
     * @return aiid
     */
    public int getAiid() {
        return aiid;
    }

    /**
     * Sets the aiid value for this TFaultInfo.
     */
    public void setAiid(int aiid) {
        this.aiid = aiid;
    }

    /**
     * Gets the data value for this TFaultInfo.
     *
     * @return data
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfoData getData() {
        return data;
    }

    /**
     * Sets the data value for this TFaultInfo.
     */
    public void setData(org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfoData data) {
        this.data = data;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TFaultInfo)) return false;
        TFaultInfo other = (TFaultInfo) obj;
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
            ((this.explanation == null && other.getExplanation() == null) ||
                (this.explanation != null &&
                    this.explanation.equals(other.getExplanation()))) &&
            this.lineNumber == other.getLineNumber() &&
            this.aiid == other.getAiid() &&
            ((this.data == null && other.getData() == null) ||
                (this.data != null &&
                    this.data.equals(other.getData())));
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
        if (getExplanation() != null) {
            _hashCode += getExplanation().hashCode();
        }
        _hashCode += getLineNumber();
        _hashCode += getAiid();
        if (getData() != null) {
            _hashCode += getData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TFaultInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFaultInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("explanation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "explanation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lineNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "line-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aiid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "aiid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tFaultInfo>data"));
        elemField.setMinOccurs(0);
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
