/**
 * ResponseType.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class ResponseType implements java.io.Serializable {
  private java.lang.Object out;

  private org.apache.www.ode.pmapi.types._2006._08._02.FaultType fault;

  private org.apache.www.ode.pmapi.types._2006._08._02.FailureType failure;

  public ResponseType() {
  }

  public ResponseType(
    java.lang.Object out,
    org.apache.www.ode.pmapi.types._2006._08._02.FaultType fault,
    org.apache.www.ode.pmapi.types._2006._08._02.FailureType failure) {
    this.out = out;
    this.fault = fault;
    this.failure = failure;
  }


  /**
   * Gets the out value for this ResponseType.
   *
   * @return out
   */
  public java.lang.Object getOut() {
    return out;
  }


  /**
   * Sets the out value for this ResponseType.
   *
   * @param out
   */
  public void setOut(java.lang.Object out) {
    this.out = out;
  }


  /**
   * Gets the fault value for this ResponseType.
   *
   * @return fault
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.FaultType getFault() {
    return fault;
  }


  /**
   * Sets the fault value for this ResponseType.
   *
   * @param fault
   */
  public void setFault(org.apache.www.ode.pmapi.types._2006._08._02.FaultType fault) {
    this.fault = fault;
  }


  /**
   * Gets the failure value for this ResponseType.
   *
   * @return failure
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.FailureType getFailure() {
    return failure;
  }


  /**
   * Sets the failure value for this ResponseType.
   *
   * @param failure
   */
  public void setFailure(org.apache.www.ode.pmapi.types._2006._08._02.FailureType failure) {
    this.failure = failure;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof ResponseType)) return false;
    ResponseType other = (ResponseType) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true &&
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
    new org.apache.axis.description.TypeDesc(ResponseType.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "ResponseType"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
