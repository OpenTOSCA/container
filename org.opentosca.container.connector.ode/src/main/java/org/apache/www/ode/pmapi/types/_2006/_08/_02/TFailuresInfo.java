/**
 * TFailuresInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Indicates one or more activities are in the failure
 *                 state and require recovery.
 */
public class TFailuresInfo implements java.io.Serializable {
    /* Date/time of last failure. */
    private java.util.Calendar dtFailure;

    /* Number of activities in failure state. */
    private int count;

    public TFailuresInfo() {
    }

    public TFailuresInfo(
        java.util.Calendar dtFailure,
        int count) {
        this.dtFailure = dtFailure;
        this.count = count;
    }

    /**
     * Gets the dtFailure value for this TFailuresInfo.
     *
     * @return dtFailure   * Date/time of last failure.
     */
    public java.util.Calendar getDtFailure() {
        return dtFailure;
    }

    /**
     * Sets the dtFailure value for this TFailuresInfo.
     *
     * @param dtFailure   * Date/time of last failure.
     */
    public void setDtFailure(java.util.Calendar dtFailure) {
        this.dtFailure = dtFailure;
    }

    /**
     * Gets the count value for this TFailuresInfo.
     *
     * @return count   * Number of activities in failure state.
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count value for this TFailuresInfo.
     *
     * @param count   * Number of activities in failure state.
     */
    public void setCount(int count) {
        this.count = count;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TFailuresInfo)) return false;
        TFailuresInfo other = (TFailuresInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.dtFailure == null && other.getDtFailure() == null) ||
                (this.dtFailure != null &&
                    this.dtFailure.equals(other.getDtFailure()))) &&
            this.count == other.getCount();
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
        if (getDtFailure() != null) {
            _hashCode += getDtFailure().hashCode();
        }
        _hashCode += getCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TFailuresInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFailuresInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dtFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-failure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("count");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
