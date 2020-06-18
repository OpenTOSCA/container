/**
 * TInstanceInfoEventInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TInstanceInfoEventInfo implements java.io.Serializable {
    /* Indicates the datetime of the first event */
    private int count;

    /* Indicates the datetime of the first event */
    private java.util.Calendar firstDtime;

    /* Indicates the datetime of the last event. */
    private java.util.Calendar lastDtime;

    public TInstanceInfoEventInfo() {
    }

    public TInstanceInfoEventInfo(
        int count,
        java.util.Calendar firstDtime,
        java.util.Calendar lastDtime) {
        this.count = count;
        this.firstDtime = firstDtime;
        this.lastDtime = lastDtime;
    }

    /**
     * Gets the count value for this TInstanceInfoEventInfo.
     *
     * @return count   * Indicates the datetime of the first event
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count value for this TInstanceInfoEventInfo.
     *
     * @param count * Indicates the datetime of the first event
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Gets the firstDtime value for this TInstanceInfoEventInfo.
     *
     * @return firstDtime   * Indicates the datetime of the first event
     */
    public java.util.Calendar getFirstDtime() {
        return firstDtime;
    }

    /**
     * Sets the firstDtime value for this TInstanceInfoEventInfo.
     *
     * @param firstDtime * Indicates the datetime of the first event
     */
    public void setFirstDtime(java.util.Calendar firstDtime) {
        this.firstDtime = firstDtime;
    }

    /**
     * Gets the lastDtime value for this TInstanceInfoEventInfo.
     *
     * @return lastDtime   * Indicates the datetime of the last event.
     */
    public java.util.Calendar getLastDtime() {
        return lastDtime;
    }

    /**
     * Sets the lastDtime value for this TInstanceInfoEventInfo.
     *
     * @param lastDtime * Indicates the datetime of the last event.
     */
    public void setLastDtime(java.util.Calendar lastDtime) {
        this.lastDtime = lastDtime;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TInstanceInfoEventInfo)) return false;
        TInstanceInfoEventInfo other = (TInstanceInfoEventInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            this.count == other.getCount() &&
            ((this.firstDtime == null && other.getFirstDtime() == null) ||
                (this.firstDtime != null &&
                    this.firstDtime.equals(other.getFirstDtime()))) &&
            ((this.lastDtime == null && other.getLastDtime() == null) ||
                (this.lastDtime != null &&
                    this.lastDtime.equals(other.getLastDtime())));
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
        _hashCode += getCount();
        if (getFirstDtime() != null) {
            _hashCode += getFirstDtime().hashCode();
        }
        if (getLastDtime() != null) {
            _hashCode += getLastDtime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TInstanceInfoEventInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tInstanceInfo>event-info"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("count");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstDtime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "first-dtime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastDtime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "last-dtime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
