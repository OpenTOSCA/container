/**
 * Replay.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class Replay implements java.io.Serializable {
    private long[] upgradeInstance;

    private long[] replaceInstance;

    private org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType[] restoreInstance;

    public Replay() {
    }

    public Replay(
        long[] upgradeInstance,
        long[] replaceInstance,
        org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType[] restoreInstance) {
        this.upgradeInstance = upgradeInstance;
        this.replaceInstance = replaceInstance;
        this.restoreInstance = restoreInstance;
    }

    /**
     * Gets the upgradeInstance value for this Replay.
     *
     * @return upgradeInstance
     */
    public long[] getUpgradeInstance() {
        return upgradeInstance;
    }

    /**
     * Sets the upgradeInstance value for this Replay.
     */
    public void setUpgradeInstance(long[] upgradeInstance) {
        this.upgradeInstance = upgradeInstance;
    }

    public long getUpgradeInstance(int i) {
        return this.upgradeInstance[i];
    }

    public void setUpgradeInstance(int i, long _value) {
        this.upgradeInstance[i] = _value;
    }

    /**
     * Gets the replaceInstance value for this Replay.
     *
     * @return replaceInstance
     */
    public long[] getReplaceInstance() {
        return replaceInstance;
    }

    /**
     * Sets the replaceInstance value for this Replay.
     */
    public void setReplaceInstance(long[] replaceInstance) {
        this.replaceInstance = replaceInstance;
    }

    public long getReplaceInstance(int i) {
        return this.replaceInstance[i];
    }

    public void setReplaceInstance(int i, long _value) {
        this.replaceInstance[i] = _value;
    }

    /**
     * Gets the restoreInstance value for this Replay.
     *
     * @return restoreInstance
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType[] getRestoreInstance() {
        return restoreInstance;
    }

    /**
     * Sets the restoreInstance value for this Replay.
     */
    public void setRestoreInstance(org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType[] restoreInstance) {
        this.restoreInstance = restoreInstance;
    }

    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType getRestoreInstance(int i) {
        return this.restoreInstance[i];
    }

    public void setRestoreInstance(int i, org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType _value) {
        this.restoreInstance[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Replay)) return false;
        Replay other = (Replay) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.upgradeInstance == null && other.getUpgradeInstance() == null) ||
                (this.upgradeInstance != null &&
                    java.util.Arrays.equals(this.upgradeInstance, other.getUpgradeInstance()))) &&
            ((this.replaceInstance == null && other.getReplaceInstance() == null) ||
                (this.replaceInstance != null &&
                    java.util.Arrays.equals(this.replaceInstance, other.getReplaceInstance()))) &&
            ((this.restoreInstance == null && other.getRestoreInstance() == null) ||
                (this.restoreInstance != null &&
                    java.util.Arrays.equals(this.restoreInstance, other.getRestoreInstance())));
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
        if (getUpgradeInstance() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getUpgradeInstance());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUpgradeInstance(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReplaceInstance() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getReplaceInstance());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReplaceInstance(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRestoreInstance() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getRestoreInstance());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRestoreInstance(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Replay.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "Replay"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("upgradeInstance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "upgradeInstance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replaceInstance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "replaceInstance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("restoreInstance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "restoreInstance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "CommunicationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
