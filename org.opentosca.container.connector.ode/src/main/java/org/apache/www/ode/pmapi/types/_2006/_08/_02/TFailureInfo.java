/**
 * TFailureInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Indicates activity is in the failure state and requires recovery.
 */
public class TFailureInfo implements java.io.Serializable {
    /* The date/time when failure occurred. */
    private java.util.Calendar dtFailure;

    /* Number of retries. */
    private int retries;

    /* Reason for failure. */
    private java.lang.String reason;

    /* Allowed recovery actions (space separated list of action names). */
    private java.lang.String actions;

    public TFailureInfo() {
    }

    public TFailureInfo(
        java.util.Calendar dtFailure,
        int retries,
        java.lang.String reason,
        java.lang.String actions) {
        this.dtFailure = dtFailure;
        this.retries = retries;
        this.reason = reason;
        this.actions = actions;
    }

    /**
     * Gets the dtFailure value for this TFailureInfo.
     *
     * @return dtFailure   * The date/time when failure occurred.
     */
    public java.util.Calendar getDtFailure() {
        return dtFailure;
    }

    /**
     * Sets the dtFailure value for this TFailureInfo.
     *
     * @param dtFailure   * The date/time when failure occurred.
     */
    public void setDtFailure(java.util.Calendar dtFailure) {
        this.dtFailure = dtFailure;
    }

    /**
     * Gets the retries value for this TFailureInfo.
     *
     * @return retries   * Number of retries.
     */
    public int getRetries() {
        return retries;
    }

    /**
     * Sets the retries value for this TFailureInfo.
     *
     * @param retries   * Number of retries.
     */
    public void setRetries(int retries) {
        this.retries = retries;
    }

    /**
     * Gets the reason value for this TFailureInfo.
     *
     * @return reason   * Reason for failure.
     */
    public java.lang.String getReason() {
        return reason;
    }

    /**
     * Sets the reason value for this TFailureInfo.
     *
     * @param reason   * Reason for failure.
     */
    public void setReason(java.lang.String reason) {
        this.reason = reason;
    }

    /**
     * Gets the actions value for this TFailureInfo.
     *
     * @return actions   * Allowed recovery actions (space separated list of action names).
     */
    public java.lang.String getActions() {
        return actions;
    }

    /**
     * Sets the actions value for this TFailureInfo.
     *
     * @param actions   * Allowed recovery actions (space separated list of action names).
     */
    public void setActions(java.lang.String actions) {
        this.actions = actions;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TFailureInfo)) return false;
        TFailureInfo other = (TFailureInfo) obj;
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
            this.retries == other.getRetries() &&
            ((this.reason == null && other.getReason() == null) ||
                (this.reason != null &&
                    this.reason.equals(other.getReason()))) &&
            ((this.actions == null && other.getActions() == null) ||
                (this.actions != null &&
                    this.actions.equals(other.getActions())));
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
        _hashCode += getRetries();
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        if (getActions() != null) {
            _hashCode += getActions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TFailureInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFailureInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dtFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-failure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retries");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "retries"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "reason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "actions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
