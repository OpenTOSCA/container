/**
 * FaultType.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class FaultType implements java.io.Serializable {
  private javax.xml.namespace.QName type;

  private java.lang.String explanation;

  private java.lang.Object message;

  public FaultType() {
  }

  public FaultType(
    javax.xml.namespace.QName type,
    java.lang.String explanation,
    java.lang.Object message) {
    this.type = type;
    this.explanation = explanation;
    this.message = message;
  }


  /**
   * Gets the type value for this FaultType.
   *
   * @return type
   */
  public javax.xml.namespace.QName getType() {
    return type;
  }


  /**
   * Sets the type value for this FaultType.
   *
   * @param type
   */
  public void setType(javax.xml.namespace.QName type) {
    this.type = type;
  }


  /**
   * Gets the explanation value for this FaultType.
   *
   * @return explanation
   */
  public java.lang.String getExplanation() {
    return explanation;
  }


  /**
   * Sets the explanation value for this FaultType.
   *
   * @param explanation
   */
  public void setExplanation(java.lang.String explanation) {
    this.explanation = explanation;
  }


  /**
   * Gets the message value for this FaultType.
   *
   * @return message
   */
  public java.lang.Object getMessage() {
    return message;
  }


  /**
   * Sets the message value for this FaultType.
   *
   * @param message
   */
  public void setMessage(java.lang.Object message) {
    this.message = message;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof FaultType)) return false;
    FaultType other = (FaultType) obj;
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
      ((this.explanation == null && other.getExplanation() == null) ||
        (this.explanation != null &&
          this.explanation.equals(other.getExplanation()))) &&
      ((this.message == null && other.getMessage() == null) ||
        (this.message != null &&
          this.message.equals(other.getMessage())));
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
    if (getExplanation() != null) {
      _hashCode += getExplanation().hashCode();
    }
    if (getMessage() != null) {
      _hashCode += getMessage().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(FaultType.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "FaultType"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("type");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "type"));
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
    elemField.setFieldName("message");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "message"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
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
