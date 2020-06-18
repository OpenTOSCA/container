/**
 * CommunicationType.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class CommunicationType implements java.io.Serializable {
    private javax.xml.namespace.QName processType;

    private java.lang.Boolean rollbackOnFault;

    private org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeServiceConfig[] serviceConfig;

    private org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeExchange[] exchange;

    public CommunicationType() {
    }

    public CommunicationType(
        javax.xml.namespace.QName processType,
        java.lang.Boolean rollbackOnFault,
        org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeServiceConfig[] serviceConfig,
        org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeExchange[] exchange) {
        this.processType = processType;
        this.rollbackOnFault = rollbackOnFault;
        this.serviceConfig = serviceConfig;
        this.exchange = exchange;
    }

    /**
     * Gets the processType value for this CommunicationType.
     *
     * @return processType
     */
    public javax.xml.namespace.QName getProcessType() {
        return processType;
    }

    /**
     * Sets the processType value for this CommunicationType.
     */
    public void setProcessType(javax.xml.namespace.QName processType) {
        this.processType = processType;
    }

    /**
     * Gets the rollbackOnFault value for this CommunicationType.
     *
     * @return rollbackOnFault
     */
    public java.lang.Boolean getRollbackOnFault() {
        return rollbackOnFault;
    }

    /**
     * Sets the rollbackOnFault value for this CommunicationType.
     */
    public void setRollbackOnFault(java.lang.Boolean rollbackOnFault) {
        this.rollbackOnFault = rollbackOnFault;
    }

    /**
     * Gets the serviceConfig value for this CommunicationType.
     *
     * @return serviceConfig
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeServiceConfig[] getServiceConfig() {
        return serviceConfig;
    }

    /**
     * Sets the serviceConfig value for this CommunicationType.
     */
    public void setServiceConfig(org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeServiceConfig[] serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeServiceConfig getServiceConfig(int i) {
        return this.serviceConfig[i];
    }

    public void setServiceConfig(int i, org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeServiceConfig _value) {
        this.serviceConfig[i] = _value;
    }

    /**
     * Gets the exchange value for this CommunicationType.
     *
     * @return exchange
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeExchange[] getExchange() {
        return exchange;
    }

    /**
     * Sets the exchange value for this CommunicationType.
     */
    public void setExchange(org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeExchange[] exchange) {
        this.exchange = exchange;
    }

    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeExchange getExchange(int i) {
        return this.exchange[i];
    }

    public void setExchange(int i, org.apache.www.ode.pmapi.types._2006._08._02.CommunicationTypeExchange _value) {
        this.exchange[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CommunicationType)) return false;
        CommunicationType other = (CommunicationType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.processType == null && other.getProcessType() == null) ||
                (this.processType != null &&
                    this.processType.equals(other.getProcessType()))) &&
            ((this.rollbackOnFault == null && other.getRollbackOnFault() == null) ||
                (this.rollbackOnFault != null &&
                    this.rollbackOnFault.equals(other.getRollbackOnFault()))) &&
            ((this.serviceConfig == null && other.getServiceConfig() == null) ||
                (this.serviceConfig != null &&
                    java.util.Arrays.equals(this.serviceConfig, other.getServiceConfig()))) &&
            ((this.exchange == null && other.getExchange() == null) ||
                (this.exchange != null &&
                    java.util.Arrays.equals(this.exchange, other.getExchange())));
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
        if (getProcessType() != null) {
            _hashCode += getProcessType().hashCode();
        }
        if (getRollbackOnFault() != null) {
            _hashCode += getRollbackOnFault().hashCode();
        }
        if (getServiceConfig() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getServiceConfig());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getServiceConfig(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExchange() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getExchange());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExchange(), i);
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
        new org.apache.axis.description.TypeDesc(CommunicationType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "CommunicationType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "processType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rollbackOnFault");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "rollbackOnFault"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceConfig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "serviceConfig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">CommunicationType>serviceConfig"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchange");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "exchange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">CommunicationType>exchange"));
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
