/**
 * TCorrelationProperty.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TCorrelationProperty implements java.io.Serializable, org.apache.axis.encoding.SimpleType, org.apache.axis.encoding.MixedContentType {
  private java.lang.String _value;

  private org.apache.axis.message.MessageElement[] _any;  // attribute

  private java.lang.String csetid;  // attribute

  private javax.xml.namespace.QName propertyName;  // attribute

  public TCorrelationProperty() {
  }

  // Simple Types must have a String constructor
  public TCorrelationProperty(java.lang.String _value) {
    this._value = _value;
  }

  // Simple Types must have a toString for serializing the value
  public java.lang.String toString() {
    return _value;
  }


  /**
   * Gets the _value value for this TCorrelationProperty.
   *
   * @return _value
   */
  public java.lang.String get_value() {
    return _value;
  }


  /**
   * Sets the _value value for this TCorrelationProperty.
   *
   * @param _value
   */
  public void set_value(java.lang.String _value) {
    this._value = _value;
  }


  /**
   * Gets the _any value for this TCorrelationProperty.
   *
   * @return _any
   */
  public org.apache.axis.message.MessageElement[] get_any() {
    return _any;
  }


  /**
   * Sets the _any value for this TCorrelationProperty.
   *
   * @param _any
   */
  public void set_any(org.apache.axis.message.MessageElement[] _any) {
    this._any = _any;
  }


  /**
   * Gets the csetid value for this TCorrelationProperty.
   *
   * @return csetid
   */
  public java.lang.String getCsetid() {
    return csetid;
  }


  /**
   * Sets the csetid value for this TCorrelationProperty.
   *
   * @param csetid
   */
  public void setCsetid(java.lang.String csetid) {
    this.csetid = csetid;
  }


  /**
   * Gets the propertyName value for this TCorrelationProperty.
   *
   * @return propertyName
   */
  public javax.xml.namespace.QName getPropertyName() {
    return propertyName;
  }


  /**
   * Sets the propertyName value for this TCorrelationProperty.
   *
   * @param propertyName
   */
  public void setPropertyName(javax.xml.namespace.QName propertyName) {
    this.propertyName = propertyName;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TCorrelationProperty)) return false;
    TCorrelationProperty other = (TCorrelationProperty) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true &&
      ((this._value == null && other.get_value() == null) ||
        (this._value != null &&
          this._value.equals(other.get_value()))) &&
      ((this._any == null && other.get_any() == null) ||
        (this._any != null &&
          java.util.Arrays.equals(this._any, other.get_any()))) &&
      ((this.csetid == null && other.getCsetid() == null) ||
        (this.csetid != null &&
          this.csetid.equals(other.getCsetid()))) &&
      ((this.propertyName == null && other.getPropertyName() == null) ||
        (this.propertyName != null &&
          this.propertyName.equals(other.getPropertyName())));
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
    if (get_value() != null) {
      _hashCode += get_value().hashCode();
    }
    if (get_any() != null) {
      for (int i = 0;
           i < java.lang.reflect.Array.getLength(get_any());
           i++) {
        java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
        if (obj != null &&
          !obj.getClass().isArray()) {
          _hashCode += obj.hashCode();
        }
      }
    }
    if (getCsetid() != null) {
      _hashCode += getCsetid().hashCode();
    }
    if (getPropertyName() != null) {
      _hashCode += getPropertyName().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TCorrelationProperty.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tCorrelationProperty"));
    org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
    attrField.setFieldName("csetid");
    attrField.setXmlName(new javax.xml.namespace.QName("", "csetid"));
    attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    typeDesc.addFieldDesc(attrField);
    attrField = new org.apache.axis.description.AttributeDesc();
    attrField.setFieldName("propertyName");
    attrField.setXmlName(new javax.xml.namespace.QName("", "propertyName"));
    attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
    typeDesc.addFieldDesc(attrField);
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("_value");
    elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
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
      new org.apache.axis.encoding.ser.SimpleSerializer(
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
      new org.apache.axis.encoding.ser.SimpleDeserializer(
        _javaType, _xmlType, typeDesc);
  }

}
