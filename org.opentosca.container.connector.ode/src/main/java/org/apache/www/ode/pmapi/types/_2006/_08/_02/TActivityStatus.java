/**
 * TActivityStatus.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TActivityStatus implements java.io.Serializable {
  private java.lang.String _value_;
  private static java.util.HashMap _table_ = new java.util.HashMap();

  // Constructor
  protected TActivityStatus(java.lang.String value) {
    _value_ = value;
    _table_.put(_value_, this);
  }

  public static final java.lang.String _ENABLED = "ENABLED";
  public static final java.lang.String _STARTED = "STARTED";
  public static final java.lang.String _COMPLETED = "COMPLETED";
  public static final java.lang.String _FAILURE = "FAILURE";
  public static final TActivityStatus ENABLED = new TActivityStatus(_ENABLED);
  public static final TActivityStatus STARTED = new TActivityStatus(_STARTED);
  public static final TActivityStatus COMPLETED = new TActivityStatus(_COMPLETED);
  public static final TActivityStatus FAILURE = new TActivityStatus(_FAILURE);

  public java.lang.String getValue() {
    return _value_;
  }

  public static TActivityStatus fromValue(java.lang.String value)
    throws java.lang.IllegalArgumentException {
    TActivityStatus enumeration = (TActivityStatus)
      _table_.get(value);
    if (enumeration == null) throw new java.lang.IllegalArgumentException();
    return enumeration;
  }

  public static TActivityStatus fromString(java.lang.String value)
    throws java.lang.IllegalArgumentException {
    return fromValue(value);
  }

  public boolean equals(java.lang.Object obj) {
    return (obj == this);
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public java.lang.String toString() {
    return _value_;
  }

  public java.lang.Object readResolve() throws java.io.ObjectStreamException {
    return fromValue(_value_);
  }

  public static org.apache.axis.encoding.Serializer getSerializer(
    java.lang.String mechType,
    java.lang.Class _javaType,
    javax.xml.namespace.QName _xmlType) {
    return
      new org.apache.axis.encoding.ser.EnumSerializer(
        _javaType, _xmlType);
  }

  public static org.apache.axis.encoding.Deserializer getDeserializer(
    java.lang.String mechType,
    java.lang.Class _javaType,
    javax.xml.namespace.QName _xmlType) {
    return
      new org.apache.axis.encoding.ser.EnumDeserializer(
        _javaType, _xmlType);
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TActivityStatus.class);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tActivityStatus"));
  }

  /**
   * Return type metadata object
   */
  public static org.apache.axis.description.TypeDesc getTypeDesc() {
    return typeDesc;
  }

}
