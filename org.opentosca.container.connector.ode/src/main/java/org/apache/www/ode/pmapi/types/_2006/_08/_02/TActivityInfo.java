/**
 * TActivityInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Information about an activity.
 */
public class TActivityInfo implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String type;

    private java.lang.String aiid;

    private org.apache.www.ode.pmapi.types._2006._08._02.TActivityStatus status;

    /* ID for scope in which this activity is executing. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef scope;

    /* The date/time when the activity was enabled. */
    private java.util.Calendar dtEnabled;

    /* The date/time when the activity was
     *                         started. */
    private java.util.Calendar dtStarted;

    /* The date/time when the activity was
     *                         completed. */
    private java.util.Calendar dtCompleted;

    /* Indicates activity is in the failure state and requires recovery. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TFailureInfo failure;

    public TActivityInfo() {
    }

    public TActivityInfo(
        java.lang.String name,
        java.lang.String type,
        java.lang.String aiid,
        org.apache.www.ode.pmapi.types._2006._08._02.TActivityStatus status,
        org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef scope,
        java.util.Calendar dtEnabled,
        java.util.Calendar dtStarted,
        java.util.Calendar dtCompleted,
        org.apache.www.ode.pmapi.types._2006._08._02.TFailureInfo failure) {
        this.name = name;
        this.type = type;
        this.aiid = aiid;
        this.status = status;
        this.scope = scope;
        this.dtEnabled = dtEnabled;
        this.dtStarted = dtStarted;
        this.dtCompleted = dtCompleted;
        this.failure = failure;
    }

    /**
     * Gets the name value for this TActivityInfo.
     *
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this TActivityInfo.
     *
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the type value for this TActivityInfo.
     *
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     * Sets the type value for this TActivityInfo.
     *
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

    /**
     * Gets the aiid value for this TActivityInfo.
     *
     * @return aiid
     */
    public java.lang.String getAiid() {
        return aiid;
    }

    /**
     * Sets the aiid value for this TActivityInfo.
     *
     * @param aiid
     */
    public void setAiid(java.lang.String aiid) {
        this.aiid = aiid;
    }

    /**
     * Gets the status value for this TActivityInfo.
     *
     * @return status
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TActivityStatus getStatus() {
        return status;
    }

    /**
     * Sets the status value for this TActivityInfo.
     *
     * @param status
     */
    public void setStatus(org.apache.www.ode.pmapi.types._2006._08._02.TActivityStatus status) {
        this.status = status;
    }

    /**
     * Gets the scope value for this TActivityInfo.
     *
     * @return scope   * ID for scope in which this activity is executing.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef getScope() {
        return scope;
    }

    /**
     * Sets the scope value for this TActivityInfo.
     *
     * @param scope   * ID for scope in which this activity is executing.
     */
    public void setScope(org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef scope) {
        this.scope = scope;
    }

    /**
     * Gets the dtEnabled value for this TActivityInfo.
     *
     * @return dtEnabled   * The date/time when the activity was enabled.
     */
    public java.util.Calendar getDtEnabled() {
        return dtEnabled;
    }

    /**
     * Sets the dtEnabled value for this TActivityInfo.
     *
     * @param dtEnabled   * The date/time when the activity was enabled.
     */
    public void setDtEnabled(java.util.Calendar dtEnabled) {
        this.dtEnabled = dtEnabled;
    }

    /**
     * Gets the dtStarted value for this TActivityInfo.
     *
     * @return dtStarted   * The date/time when the activity was
     *                         started.
     */
    public java.util.Calendar getDtStarted() {
        return dtStarted;
    }

    /**
     * Sets the dtStarted value for this TActivityInfo.
     *
     * @param dtStarted   * The date/time when the activity was
     *                         started.
     */
    public void setDtStarted(java.util.Calendar dtStarted) {
        this.dtStarted = dtStarted;
    }

    /**
     * Gets the dtCompleted value for this TActivityInfo.
     *
     * @return dtCompleted   * The date/time when the activity was
     *                         completed.
     */
    public java.util.Calendar getDtCompleted() {
        return dtCompleted;
    }

    /**
     * Sets the dtCompleted value for this TActivityInfo.
     *
     * @param dtCompleted   * The date/time when the activity was
     *                         completed.
     */
    public void setDtCompleted(java.util.Calendar dtCompleted) {
        this.dtCompleted = dtCompleted;
    }

    /**
     * Gets the failure value for this TActivityInfo.
     *
     * @return failure   * Indicates activity is in the failure state and requires recovery.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TFailureInfo getFailure() {
        return failure;
    }

    /**
     * Sets the failure value for this TActivityInfo.
     *
     * @param failure   * Indicates activity is in the failure state and requires recovery.
     */
    public void setFailure(org.apache.www.ode.pmapi.types._2006._08._02.TFailureInfo failure) {
        this.failure = failure;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TActivityInfo)) return false;
        TActivityInfo other = (TActivityInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.name == null && other.getName() == null) ||
                (this.name != null &&
                    this.name.equals(other.getName()))) &&
            ((this.type == null && other.getType() == null) ||
                (this.type != null &&
                    this.type.equals(other.getType()))) &&
            ((this.aiid == null && other.getAiid() == null) ||
                (this.aiid != null &&
                    this.aiid.equals(other.getAiid()))) &&
            ((this.status == null && other.getStatus() == null) ||
                (this.status != null &&
                    this.status.equals(other.getStatus()))) &&
            ((this.scope == null && other.getScope() == null) ||
                (this.scope != null &&
                    this.scope.equals(other.getScope()))) &&
            ((this.dtEnabled == null && other.getDtEnabled() == null) ||
                (this.dtEnabled != null &&
                    this.dtEnabled.equals(other.getDtEnabled()))) &&
            ((this.dtStarted == null && other.getDtStarted() == null) ||
                (this.dtStarted != null &&
                    this.dtStarted.equals(other.getDtStarted()))) &&
            ((this.dtCompleted == null && other.getDtCompleted() == null) ||
                (this.dtCompleted != null &&
                    this.dtCompleted.equals(other.getDtCompleted()))) &&
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getAiid() != null) {
            _hashCode += getAiid().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getScope() != null) {
            _hashCode += getScope().hashCode();
        }
        if (getDtEnabled() != null) {
            _hashCode += getDtEnabled().hashCode();
        }
        if (getDtStarted() != null) {
            _hashCode += getDtStarted().hashCode();
        }
        if (getDtCompleted() != null) {
            _hashCode += getDtCompleted().hashCode();
        }
        if (getFailure() != null) {
            _hashCode += getFailure().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TActivityInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tActivityInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aiid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "aiid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tActivityStatus"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scope");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "scope"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dtEnabled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-enabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dtStarted");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-started"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dtCompleted");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "dt-completed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "failure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tFailureInfo"));
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
