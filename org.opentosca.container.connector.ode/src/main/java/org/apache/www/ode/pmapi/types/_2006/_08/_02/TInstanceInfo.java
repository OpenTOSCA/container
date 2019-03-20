/**
 * TInstanceInfo.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TInstanceInfo implements java.io.Serializable {
  /* The unique instance identifier. */
  private java.lang.String iid;

  /* Process id of the process to which this instance
   *                         belongs. */
  private java.lang.String pid;

  private javax.xml.namespace.QName processName;

  /* Root scope id (might not exist). */
  private org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef rootScope;

  /* Status of ths instance. */
  private org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus status;

  /* The date/time when the instance was
   *                         started. */
  private java.util.Calendar dtStarted;

  /* The date/time when the last activity
   *                         occured. */
  private java.util.Calendar dtLastActive;

  /* If present, indicates the date/time since which the
   *                         instance has been in an error state. */
  private java.util.Calendar dtErrorSince;

  /* Lists all correlation properties with their values that
   *                         are associated with this process instance. */
  private org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[] correlationProperties;

  /* Information about the events for this instance. If
   *                         absent, indicates events are not available. */
  private org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfoEventInfo eventInfo;

  /* If present, indicates the fault with which this
   *                         instance failed. */
  private org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfo faultInfo;

  private org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo failures;

  public TInstanceInfo() {
  }

  public TInstanceInfo(
    java.lang.String iid,
    java.lang.String pid,
    javax.xml.namespace.QName processName,
    org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef rootScope,
    org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus status,
    java.util.Calendar dtStarted,
    java.util.Calendar dtLastActive,
    java.util.Calendar dtErrorSince,
    org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[] correlationProperties,
    org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfoEventInfo eventInfo,
    org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfo faultInfo,
    org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo failures) {
    this.iid = iid;
    this.pid = pid;
    this.processName = processName;
    this.rootScope = rootScope;
    this.status = status;
    this.dtStarted = dtStarted;
    this.dtLastActive = dtLastActive;
    this.dtErrorSince = dtErrorSince;
    this.correlationProperties = correlationProperties;
    this.eventInfo = eventInfo;
    this.faultInfo = faultInfo;
    this.failures = failures;
  }


  /**
   * Gets the iid value for this TInstanceInfo.
   *
   * @return iid   * The unique instance identifier.
   */
  public java.lang.String getIid() {
    return iid;
  }


  /**
   * Sets the iid value for this TInstanceInfo.
   *
   * @param iid   * The unique instance identifier.
   */
  public void setIid(java.lang.String iid) {
    this.iid = iid;
  }


  /**
   * Gets the pid value for this TInstanceInfo.
   *
   * @return pid   * Process id of the process to which this instance
   *                         belongs.
   */
  public java.lang.String getPid() {
    return pid;
  }


  /**
   * Sets the pid value for this TInstanceInfo.
   *
   * @param pid   * Process id of the process to which this instance
   *                         belongs.
   */
  public void setPid(java.lang.String pid) {
    this.pid = pid;
  }


  /**
   * Gets the processName value for this TInstanceInfo.
   *
   * @return processName
   */
  public javax.xml.namespace.QName getProcessName() {
    return processName;
  }


  /**
   * Sets the processName value for this TInstanceInfo.
   *
   * @param processName
   */
  public void setProcessName(javax.xml.namespace.QName processName) {
    this.processName = processName;
  }


  /**
   * Gets the rootScope value for this TInstanceInfo.
   *
   * @return rootScope   * Root scope id (might not exist).
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef getRootScope() {
    return rootScope;
  }


  /**
   * Sets the rootScope value for this TInstanceInfo.
   *
   * @param rootScope   * Root scope id (might not exist).
   */
  public void setRootScope(org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef rootScope) {
    this.rootScope = rootScope;
  }


  /**
   * Gets the status value for this TInstanceInfo.
   *
   * @return status   * Status of ths instance.
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus getStatus() {
    return status;
  }


  /**
   * Sets the status value for this TInstanceInfo.
   *
   * @param status   * Status of ths instance.
   */
  public void setStatus(org.apache.www.ode.pmapi.types._2006._08._02.TInstanceStatus status) {
    this.status = status;
  }


  /**
   * Gets the dtStarted value for this TInstanceInfo.
   *
   * @return dtStarted   * The date/time when the instance was
   *                         started.
   */
  public java.util.Calendar getDtStarted() {
    return dtStarted;
  }


  /**
   * Sets the dtStarted value for this TInstanceInfo.
   *
   * @param dtStarted   * The date/time when the instance was
   *                         started.
   */
  public void setDtStarted(java.util.Calendar dtStarted) {
    this.dtStarted = dtStarted;
  }


  /**
   * Gets the dtLastActive value for this TInstanceInfo.
   *
   * @return dtLastActive   * The date/time when the last activity
   *                         occured.
   */
  public java.util.Calendar getDtLastActive() {
    return dtLastActive;
  }


  /**
   * Sets the dtLastActive value for this TInstanceInfo.
   *
   * @param dtLastActive   * The date/time when the last activity
   *                         occured.
   */
  public void setDtLastActive(java.util.Calendar dtLastActive) {
    this.dtLastActive = dtLastActive;
  }


  /**
   * Gets the dtErrorSince value for this TInstanceInfo.
   *
   * @return dtErrorSince   * If present, indicates the date/time since which the
   *                         instance has been in an error state.
   */
  public java.util.Calendar getDtErrorSince() {
    return dtErrorSince;
  }


  /**
   * Sets the dtErrorSince value for this TInstanceInfo.
   *
   * @param dtErrorSince   * If present, indicates the date/time since which the
   *                         instance has been in an error state.
   */
  public void setDtErrorSince(java.util.Calendar dtErrorSince) {
    this.dtErrorSince = dtErrorSince;
  }


  /**
   * Gets the correlationProperties value for this TInstanceInfo.
   *
   * @return correlationProperties   * Lists all correlation properties with their values that
   *                         are associated with this process instance.
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[] getCorrelationProperties() {
    return correlationProperties;
  }


  /**
   * Sets the correlationProperties value for this TInstanceInfo.
   *
   * @param correlationProperties   * Lists all correlation properties with their values that
   *                         are associated with this process instance.
   */
  public void setCorrelationProperties(org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[] correlationProperties) {
    this.correlationProperties = correlationProperties;
  }


  /**
   * Gets the eventInfo value for this TInstanceInfo.
   *
   * @return eventInfo   * Information about the events for this instance. If
   *                         absent, indicates events are not available.
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfoEventInfo getEventInfo() {
    return eventInfo;
  }


  /**
   * Sets the eventInfo value for this TInstanceInfo.
   *
   * @param eventInfo   * Information about the events for this instance. If
   *                         absent, indicates events are not available.
   */
  public void setEventInfo(org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfoEventInfo eventInfo) {
    this.eventInfo = eventInfo;
  }


  /**
   * Gets the faultInfo value for this TInstanceInfo.
   *
   * @return faultInfo   * If present, indicates the fault with which this
   *                         instance failed.
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfo getFaultInfo() {
    return faultInfo;
  }


  /**
   * Sets the faultInfo value for this TInstanceInfo.
   *
   * @param faultInfo   * If present, indicates the fault with which this
   *                         instance failed.
   */
  public void setFaultInfo(org.apache.www.ode.pmapi.types._2006._08._02.TFaultInfo faultInfo) {
    this.faultInfo = faultInfo;
  }


  /**
   * Gets the failures value for this TInstanceInfo.
   *
   * @return failures
   */
  public org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo getFailures() {
    return failures;
  }


  /**
   * Sets the failures value for this TInstanceInfo.
   *
   * @param failures
   */
  public void setFailures(org.apache.www.ode.pmapi.types._2006._08._02.TFailuresInfo failures) {
    this.failures = failures;
  }

  private java.lang.Object __equalsCalc = null;

  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TInstanceInfo)) return false;
    TInstanceInfo other = (TInstanceInfo) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true &&
      ((this.iid == null && other.getIid() == null) ||
        (this.iid != null &&
          this.iid.equals(other.getIid()))) &&
      ((this.pid == null && other.getPid() == null) ||
        (this.pid != null &&
          this.pid.equals(other.getPid()))) &&
      ((this.processName == null && other.getProcessName() == null) ||
        (this.processName != null &&
          this.processName.equals(other.getProcessName()))) &&
      ((this.rootScope == null && other.getRootScope() == null) ||
        (this.rootScope != null &&
          this.rootScope.equals(other.getRootScope()))) &&
      ((this.status == null && other.getStatus() == null) ||
        (this.status != null &&
          this.status.equals(other.getStatus()))) &&
      ((this.dtStarted == null && other.getDtStarted() == null) ||
        (this.dtStarted != null &&
          this.dtStarted.equals(other.getDtStarted()))) &&
      ((this.dtLastActive == null && other.getDtLastActive() == null) ||
        (this.dtLastActive != null &&
          this.dtLastActive.equals(other.getDtLastActive()))) &&
      ((this.dtErrorSince == null && other.getDtErrorSince() == null) ||
        (this.dtErrorSince != null &&
          this.dtErrorSince.equals(other.getDtErrorSince()))) &&
      ((this.correlationProperties == null && other.getCorrelationProperties() == null) ||
        (this.correlationProperties != null &&
          java.util.Arrays.equals(this.correlationProperties, other.getCorrelationProperties()))) &&
      ((this.eventInfo == null && other.getEventInfo() == null) ||
        (this.eventInfo != null &&
          this.eventInfo.equals(other.getEventInfo()))) &&
      ((this.faultInfo == null && other.getFaultInfo() == null) ||
        (this.faultInfo != null &&
          this.faultInfo.equals(other.getFaultInfo()))) &&
      ((this.failures == null && other.getFailures() == null) ||
        (this.failures != null &&
          this.failures.equals(other.getFailures())));
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
    if (getIid() != null) {
      _hashCode += getIid().hashCode();
    }
    if (getPid() != null) {
      _hashCode += getPid().hashCode();
    }
    if (getProcessName() != null) {
      _hashCode += getProcessName().hashCode();
    }
    if (getRootScope() != null) {
      _hashCode += getRootScope().hashCode();
    }
    if (getStatus() != null) {
      _hashCode += getStatus().hashCode();
    }
    if (getDtStarted() != null) {
      _hashCode += getDtStarted().hashCode();
    }
    if (getDtLastActive() != null) {
      _hashCode += getDtLastActive().hashCode();
    }
    if (getDtErrorSince() != null) {
      _hashCode += getDtErrorSince().hashCode();
    }
    if (getCorrelationProperties() != null) {
      for (int i = 0;
           i < java.lang.reflect.Array.getLength(getCorrelationProperties());
           i++) {
        java.lang.Object obj = java.lang.reflect.Array.get(getCorrelationProperties(), i);
        if (obj != null &&
          !obj.getClass().isArray()) {
          _hashCode += obj.hashCode();
        }
      }
    }
    if (getEventInfo() != null) {
      _hashCode += getEventInfo().hashCode();
    }
    if (getFaultInfo() != null) {
      _hashCode += getFaultInfo().hashCode();
    }
    if (getFailures() != null) {
      _hashCode += getFailures().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TInstanceInfo.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tInstanceInfo"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("iid");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "iid"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("pid");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "pid"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("processName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "process-name"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("rootScope");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "root-scope"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeRef"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("status");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "status"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tInstanceStatus"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("dtStarted");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-started"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("dtLastActive");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-last-active"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("dtErrorSince");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-error-since"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("correlationProperties");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "correlation-properties"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tCorrelationProperty"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "correlation-property"));
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("eventInfo");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "event-info"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tInstanceInfo>event-info"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("faultInfo");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "fault-info"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFaultInfo"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("failures");
    elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "failures"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFailuresInfo"));
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
