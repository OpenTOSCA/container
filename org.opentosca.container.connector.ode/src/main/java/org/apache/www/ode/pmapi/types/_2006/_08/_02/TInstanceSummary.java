/**
 * TInstanceSummary.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * A summary of the number of instances in each state.
 */
public class TInstanceSummary implements java.io.Serializable {
    private org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummaryInstances[] instances;

    private org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo failures;

    public TInstanceSummary() {
    }

    public TInstanceSummary(
        org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummaryInstances[] instances,
        org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo failures) {
        this.instances = instances;
        this.failures = failures;
    }

    /**
     * Gets the instances value for this TInstanceSummary.
     *
     * @return instances
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummaryInstances[] getInstances() {
        return instances;
    }

    /**
     * Sets the instances value for this TInstanceSummary.
     */
    public void setInstances(org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummaryInstances[] instances) {
        this.instances = instances;
    }

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummaryInstances getInstances(int i) {
        return this.instances[i];
    }

    public void setInstances(int i, org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummaryInstances _value) {
        this.instances[i] = _value;
    }

    /**
     * Gets the failures value for this TInstanceSummary.
     *
     * @return failures
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo getFailures() {
        return failures;
    }

    /**
     * Sets the failures value for this TInstanceSummary.
     */
    public void setFailures(org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo failures) {
        this.failures = failures;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TInstanceSummary)) return false;
        TInstanceSummary other = (TInstanceSummary) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.instances == null && other.getInstances() == null) ||
                (this.instances != null &&
                    java.util.Arrays.equals(this.instances, other.getInstances()))) &&
            ((this.failures == null && other.getFailures() == null) ||
                (this.failures != null &&
                    this.failures.equals(other.getFailures())));
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
        if (getInstances() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getInstances());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInstances(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFailures() != null) {
            _hashCode += getFailures().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TInstanceSummary.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tInstanceSummary"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instances");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "instances"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tInstanceSummary>instances"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failures");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "failures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFailuresInfo"));
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
