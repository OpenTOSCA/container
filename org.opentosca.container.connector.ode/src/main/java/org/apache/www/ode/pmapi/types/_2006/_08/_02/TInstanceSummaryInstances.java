/**
 * TInstanceSummaryInstances.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TInstanceSummaryInstances implements java.io.Serializable {
  private org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus state;  // attribute

  private int count;  // attribute

  public TInstanceSummaryInstances() {
  }

  public TInstanceSummaryInstances(
    org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus state,
    int count) {
    this.state = state;
    this.count = count;
  }


  /**
   * Gets the state value for this TInstanceSummaryInstances.
   *
   * @return state
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus getState() {
    return state;
  }


  /**
   * Sets the state value for this TInstanceSummaryInstances.
   *
   * @param state
   */
  public void setState(org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus state) {
    this.state = state;
  }


  /**
   * Gets the count value for this TInstanceSummaryInstances.
   *
   * @return count
   */
  public int getCount() {
    return count;
  }


  /**
   * Sets the count value for this TInstanceSummaryInstances.
   *
   * @param count
   */
  public void setCount(int count) {
    this.count = count;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TInstanceSummaryInstances)) return false;
    TInstanceSummaryInstances other = (TInstanceSummaryInstances) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true &&
      ((this.state == null && other.getState() == null) ||
        (this.state != null &&
          this.state.equals(other.getState()))) &&
      this.count == other.getCount();
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
    if (getState() != null) {
      _hashCode += getState().hashCode();
    }
    _hashCode += getCount();
    __hashCodeCalc = false;
    return _hashCode;
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TInstanceSummaryInstances.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tInstanceSummary>instances"));
    org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
    attrField.setFieldName("state");
    attrField.setXmlName(new javax.xml.namespace.QName("", "state"));
    attrField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tInstanceStatus"));
    typeDesc.addFieldDesc(attrField);
    attrField = new org.apache.axis.description.AttributeDesc();
    attrField.setFieldName("count");
    attrField.setXmlName(new javax.xml.namespace.QName("", "count"));
    attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    typeDesc.addFieldDesc(attrField);
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
