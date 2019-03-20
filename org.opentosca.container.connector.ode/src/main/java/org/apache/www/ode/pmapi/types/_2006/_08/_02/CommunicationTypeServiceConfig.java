/**
 * CommunicationTypeServiceConfig.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class CommunicationTypeServiceConfig implements java.io.Serializable {
  private javax.xml.namespace.QName service;

  private org.apache.www.ode.pmapi.types._2006._08._02.ReplayType replayType;

  public CommunicationTypeServiceConfig() {
  }

  public CommunicationTypeServiceConfig(
    javax.xml.namespace.QName service,
    org.apache.www.ode.pmapi.types._2006._08._02.ReplayType replayType) {
    this.service = service;
    this.replayType = replayType;
  }


  /**
   * Gets the service value for this CommunicationTypeServiceConfig.
   *
   * @return service
   */
  public javax.xml.namespace.QName getService() {
    return service;
  }


  /**
   * Sets the service value for this CommunicationTypeServiceConfig.
   *
   * @param service
   */
  public void setService(javax.xml.namespace.QName service) {
    this.service = service;
  }


  /**
   * Gets the replayType value for this CommunicationTypeServiceConfig.
   *
   * @return replayType
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.ReplayType getReplayType() {
    return replayType;
  }


  /**
   * Sets the replayType value for this CommunicationTypeServiceConfig.
   *
   * @param replayType
   */
  public void setReplayType(org.apache.www.ode.pmapi.types._2006._08._02.ReplayType replayType) {
    this.replayType = replayType;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof CommunicationTypeServiceConfig)) return false;
    CommunicationTypeServiceConfig other = (CommunicationTypeServiceConfig) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true &&
      ((this.service == null && other.getService() == null) ||
        (this.service != null &&
          this.service.equals(other.getService()))) &&
      ((this.replayType == null && other.getReplayType() == null) ||
        (this.replayType != null &&
          this.replayType.equals(other.getReplayType())));
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
    if (getService() != null) {
      _hashCode += getService().hashCode();
    }
    if (getReplayType() != null) {
      _hashCode += getReplayType().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(CommunicationTypeServiceConfig.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">CommunicationType>serviceConfig"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("service");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "service"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("replayType");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "replayType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "ReplayType"));
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
