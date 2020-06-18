/**
 * MockQueryRequest.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class MockQueryRequest implements java.io.Serializable {
    private java.util.Calendar createTime;

    private javax.xml.namespace.QName service;

    private java.lang.String operation;

    private java.lang.Object in;

    private org.apache.www.ode.pmapi.types._2006._08._02.MockQueryRequestPattern pattern;

    public MockQueryRequest() {
    }

    public MockQueryRequest(
        java.util.Calendar createTime,
        javax.xml.namespace.QName service,
        java.lang.String operation,
        java.lang.Object in,
        org.apache.www.ode.pmapi.types._2006._08._02.MockQueryRequestPattern pattern) {
        this.createTime = createTime;
        this.service = service;
        this.operation = operation;
        this.in = in;
        this.pattern = pattern;
    }

    /**
     * Gets the createTime value for this MockQueryRequest.
     *
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }

    /**
     * Sets the createTime value for this MockQueryRequest.
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets the service value for this MockQueryRequest.
     *
     * @return service
     */
    public javax.xml.namespace.QName getService() {
        return service;
    }

    /**
     * Sets the service value for this MockQueryRequest.
     */
    public void setService(javax.xml.namespace.QName service) {
        this.service = service;
    }

    /**
     * Gets the operation value for this MockQueryRequest.
     *
     * @return operation
     */
    public java.lang.String getOperation() {
        return operation;
    }

    /**
     * Sets the operation value for this MockQueryRequest.
     */
    public void setOperation(java.lang.String operation) {
        this.operation = operation;
    }

    /**
     * Gets the in value for this MockQueryRequest.
     *
     * @return in
     */
    public java.lang.Object getIn() {
        return in;
    }

    /**
     * Sets the in value for this MockQueryRequest.
     */
    public void setIn(java.lang.Object in) {
        this.in = in;
    }

    /**
     * Gets the pattern value for this MockQueryRequest.
     *
     * @return pattern
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.MockQueryRequestPattern getPattern() {
        return pattern;
    }

    /**
     * Sets the pattern value for this MockQueryRequest.
     */
    public void setPattern(org.apache.www.ode.pmapi.types._2006._08._02.MockQueryRequestPattern pattern) {
        this.pattern = pattern;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MockQueryRequest)) return false;
        MockQueryRequest other = (MockQueryRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
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
            ((this.pattern == null && other.getPattern() == null) ||
                (this.pattern != null &&
                    this.pattern.equals(other.getPattern())));
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
        if (getPattern() != null) {
            _hashCode += getPattern().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MockQueryRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">mockQueryRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("pattern");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "pattern"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">>mockQueryRequest>pattern"));
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
