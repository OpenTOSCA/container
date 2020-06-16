/**
 * CommunicationTypeExchange.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class CommunicationTypeExchange implements java.io.Serializable {
    private org.apache.www.ode.pmapi.types._2006._08._02.ExchangeType type;

    private java.util.Calendar createTime;

    private javax.xml.namespace.QName service;

    private java.lang.String operation;

    private java.lang.Object in;

    private java.lang.Object out;

    private org.apache.www.ode.pmapi.types._2006._08._02.FaultType fault;

    private org.apache.www.ode.pmapi.types._2006._08._02.FailureType failure;

    public CommunicationTypeExchange() {
    }

    public CommunicationTypeExchange(
        org.apache.www.ode.pmapi.types._2006._08._02.ExchangeType type,
        java.util.Calendar createTime,
        javax.xml.namespace.QName service,
        java.lang.String operation,
        java.lang.Object in,
        java.lang.Object out,
        org.apache.www.ode.pmapi.types._2006._08._02.FaultType fault,
        org.apache.www.ode.pmapi.types._2006._08._02.FailureType failure) {
        this.type = type;
        this.createTime = createTime;
        this.service = service;
        this.operation = operation;
        this.in = in;
        this.out = out;
        this.fault = fault;
        this.failure = failure;
    }

    /**
     * Gets the type value for this CommunicationTypeExchange.
     *
     * @return type
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.ExchangeType getType() {
        return type;
    }

    /**
     * Sets the type value for this CommunicationTypeExchange.
     *
     * @param type
     */
    public void setType(org.apache.www.ode.pmapi.types._2006._08._02.ExchangeType type) {
        this.type = type;
    }

    /**
     * Gets the createTime value for this CommunicationTypeExchange.
     *
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }

    /**
     * Sets the createTime value for this CommunicationTypeExchange.
     *
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets the service value for this CommunicationTypeExchange.
     *
     * @return service
     */
    public javax.xml.namespace.QName getService() {
        return service;
    }

    /**
     * Sets the service value for this CommunicationTypeExchange.
     *
     * @param service
     */
    public void setService(javax.xml.namespace.QName service) {
        this.service = service;
    }

    /**
     * Gets the operation value for this CommunicationTypeExchange.
     *
     * @return operation
     */
    public java.lang.String getOperation() {
        return operation;
    }

    /**
     * Sets the operation value for this CommunicationTypeExchange.
     *
     * @param operation
     */
    public void setOperation(java.lang.String operation) {
        this.operation = operation;
    }

    /**
     * Gets the in value for this CommunicationTypeExchange.
     *
     * @return in
     */
    public java.lang.Object getIn() {
        return in;
    }

    /**
     * Sets the in value for this CommunicationTypeExchange.
     *
     * @param in
     */
    public void setIn(java.lang.Object in) {
        this.in = in;
    }

    /**
     * Gets the out value for this CommunicationTypeExchange.
     *
     * @return out
     */
    public java.lang.Object getOut() {
        return out;
    }

    /**
     * Sets the out value for this CommunicationTypeExchange.
     *
     * @param out
     */
    public void setOut(java.lang.Object out) {
        this.out = out;
    }

    /**
     * Gets the fault value for this CommunicationTypeExchange.
     *
     * @return fault
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.FaultType getFault() {
        return fault;
    }

    /**
     * Sets the fault value for this CommunicationTypeExchange.
     *
     * @param fault
     */
    public void setFault(org.apache.www.ode.pmapi.types._2006._08._02.FaultType fault) {
        this.fault = fault;
    }

    /**
     * Gets the failure value for this CommunicationTypeExchange.
     *
     * @return failure
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.FailureType getFailure() {
        return failure;
    }

    /**
     * Sets the failure value for this CommunicationTypeExchange.
     *
     * @param failure
     */
    public void setFailure(org.apache.www.ode.pmapi.types._2006._08._02.FailureType failure) {
        this.failure = failure;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CommunicationTypeExchange)) return false;
        CommunicationTypeExchange other = (CommunicationTypeExchange) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.type == null && other.getType() == null) ||
                (this.type != null &&
                    this.type.equals(other.getType()))) &&
            ((this.createTime == null && other.getCreateTime() == null) ||
                (this.createTime != null &&
                    this.createTime.equals(other.getCreateTime()))) &&
            ((this.service == null && other.getService() == null) ||
                (this.service != null &&
                    this.service.equals(other.getService()))) &&
            ((this.operation == null && other.getOperation() == null) ||
                (this.operation != null &&
                    this.operation.equals(other.getOperation()))) &&
            ((this.in == null && other.getIn() == null) ||
                (this.in != null &&
                    this.in.equals(other.getIn()))) &&
            ((this.out == null && other.getOut() == null) ||
                (this.out != null &&
                    this.out.equals(other.getOut()))) &&
            ((this.fault == null && other.getFault() == null) ||
                (this.fault != null &&
                    this.fault.equals(other.getFault()))) &&
            ((this.failure == null && other.getFailure() == null) ||
                (this.failure != null &&
                    this.failure.equals(other.getFailure())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
        if (getService() != null) {
            _hashCode += getService().hashCode();
        }
        if (getOperation() != null) {
            _hashCode += getOperation().hashCode();
        }
        if (getIn() != null) {
            _hashCode += getIn().hashCode();
        }
        if (getOut() != null) {
            _hashCode += getOut().hashCode();
        }
        if (getFault() != null) {
            _hashCode += getFault().hashCode();
        }
        if (getFailure() != null) {
            _hashCode += getFailure().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CommunicationTypeExchange.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">CommunicationType>exchange"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "ExchangeType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "createTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("service");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "service"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "operation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("in");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "in"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("out");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "out"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fault");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "fault"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "FaultType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "failure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "FailureType"));
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
