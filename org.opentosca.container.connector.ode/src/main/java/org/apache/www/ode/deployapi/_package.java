/**
 * _package.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.deployapi;

public class _package implements java.io.Serializable {
  private org.w3.www._2005._05.xmlmime.Base64Binary zip;

  public _package() {
  }

  public _package(
    org.w3.www._2005._05.xmlmime.Base64Binary zip) {
    this.zip = zip;
  }


  /**
   * Gets the zip value for this _package.
   *
   * @return zip
   */
  public org.w3.www._2005._05.xmlmime.Base64Binary getZip() {
    return zip;
  }


  /**
   * Sets the zip value for this _package.
   *
   * @param zip
   */
  public void setZip(org.w3.www._2005._05.xmlmime.Base64Binary zip) {
    this.zip = zip;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof _package)) return false;
    _package other = (_package) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true &&
      ((this.zip == null && other.getZip() == null) ||
        (this.zip != null &&
          this.zip.equals(other.getZip())));
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
    if (getZip() != null) {
      _hashCode += getZip().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(_package.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/deployapi", "package"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("zip");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/deployapi", "zip"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/05/xmlmime", "base64Binary"));
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
