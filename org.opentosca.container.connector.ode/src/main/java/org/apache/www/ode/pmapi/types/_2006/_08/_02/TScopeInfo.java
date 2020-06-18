/**
 * TScopeInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TScopeInfo implements java.io.Serializable {
    /* Scope instance identifier. */
    private java.lang.String siid;

    /* Scope name. */
    private java.lang.String name;

    /* Scope instance identifier. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus status;

    /* Parent scope reference. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef parentScopeRef;

    private org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef[] children;

    private org.apache.www.ode.pmapi.types._2006._08._02.TActivityInfo[] activities;

    private org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef[] variables;

    /* Lists all correlation correlation sets associated with
     *                         this scope with their valued correlation properties. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[][] correlationSets;

    /* Endpoint references. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] endpoints;

    public TScopeInfo() {
    }

    public TScopeInfo(
        java.lang.String siid,
        java.lang.String name,
        org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus status,
        org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef parentScopeRef,
        org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef[] children,
        org.apache.www.ode.pmapi.types._2006._08._02.TActivityInfo[] activities,
        org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef[] variables,
        org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[][] correlationSets,
        org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] endpoints) {
        this.siid = siid;
        this.name = name;
        this.status = status;
        this.parentScopeRef = parentScopeRef;
        this.children = children;
        this.activities = activities;
        this.variables = variables;
        this.correlationSets = correlationSets;
        this.endpoints = endpoints;
    }

    /**
     * Gets the siid value for this TScopeInfo.
     *
     * @return siid   * Scope instance identifier.
     */
    public java.lang.String getSiid() {
        return siid;
    }

    /**
     * Sets the siid value for this TScopeInfo.
     *
     * @param siid * Scope instance identifier.
     */
    public void setSiid(java.lang.String siid) {
        this.siid = siid;
    }

    /**
     * Gets the name value for this TScopeInfo.
     *
     * @return name   * Scope name.
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this TScopeInfo.
     *
     * @param name * Scope name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the status value for this TScopeInfo.
     *
     * @return status   * Scope instance identifier.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus getStatus() {
        return status;
    }

    /**
     * Sets the status value for this TScopeInfo.
     *
     * @param status * Scope instance identifier.
     */
    public void setStatus(org.apache.www.ode.pmapi.types._2006._08._02.TScopeStatus status) {
        this.status = status;
    }

    /**
     * Gets the parentScopeRef value for this TScopeInfo.
     *
     * @return parentScopeRef   * Parent scope reference.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef getParentScopeRef() {
        return parentScopeRef;
    }

    /**
     * Sets the parentScopeRef value for this TScopeInfo.
     *
     * @param parentScopeRef * Parent scope reference.
     */
    public void setParentScopeRef(org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef parentScopeRef) {
        this.parentScopeRef = parentScopeRef;
    }

    /**
     * Gets the children value for this TScopeInfo.
     *
     * @return children
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef[] getChildren() {
        return children;
    }

    /**
     * Sets the children value for this TScopeInfo.
     */
    public void setChildren(org.apache.www.ode.pmapi.types._2006._08._02.TScopeRef[] children) {
        this.children = children;
    }

    /**
     * Gets the activities value for this TScopeInfo.
     *
     * @return activities
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TActivityInfo[] getActivities() {
        return activities;
    }

    /**
     * Sets the activities value for this TScopeInfo.
     */
    public void setActivities(org.apache.www.ode.pmapi.types._2006._08._02.TActivityInfo[] activities) {
        this.activities = activities;
    }

    /**
     * Gets the variables value for this TScopeInfo.
     *
     * @return variables
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef[] getVariables() {
        return variables;
    }

    /**
     * Sets the variables value for this TScopeInfo.
     */
    public void setVariables(org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef[] variables) {
        this.variables = variables;
    }

    /**
     * Gets the correlationSets value for this TScopeInfo.
     *
     * @return correlationSets   * Lists all correlation correlation sets associated with this scope with their valued
     * correlation properties.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[][] getCorrelationSets() {
        return correlationSets;
    }

    /**
     * Sets the correlationSets value for this TScopeInfo.
     *
     * @param correlationSets * Lists all correlation correlation sets associated with this scope with their valued
     *                        correlation properties.
     */
    public void setCorrelationSets(org.apache.www.ode.pmapi.types._2006._08._02.TCorrelationProperty[][] correlationSets) {
        this.correlationSets = correlationSets;
    }

    /**
     * Gets the endpoints value for this TScopeInfo.
     *
     * @return endpoints   * Endpoint references.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] getEndpoints() {
        return endpoints;
    }

    /**
     * Sets the endpoints value for this TScopeInfo.
     *
     * @param endpoints * Endpoint references.
     */
    public void setEndpoints(org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] endpoints) {
        this.endpoints = endpoints;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TScopeInfo)) return false;
        TScopeInfo other = (TScopeInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.siid == null && other.getSiid() == null) ||
                (this.siid != null &&
                    this.siid.equals(other.getSiid()))) &&
            ((this.name == null && other.getName() == null) ||
                (this.name != null &&
                    this.name.equals(other.getName()))) &&
            ((this.status == null && other.getStatus() == null) ||
                (this.status != null &&
                    this.status.equals(other.getStatus()))) &&
            ((this.parentScopeRef == null && other.getParentScopeRef() == null) ||
                (this.parentScopeRef != null &&
                    this.parentScopeRef.equals(other.getParentScopeRef()))) &&
            ((this.children == null && other.getChildren() == null) ||
                (this.children != null &&
                    java.util.Arrays.equals(this.children, other.getChildren()))) &&
            ((this.activities == null && other.getActivities() == null) ||
                (this.activities != null &&
                    java.util.Arrays.equals(this.activities, other.getActivities()))) &&
            ((this.variables == null && other.getVariables() == null) ||
                (this.variables != null &&
                    java.util.Arrays.equals(this.variables, other.getVariables()))) &&
            ((this.correlationSets == null && other.getCorrelationSets() == null) ||
                (this.correlationSets != null &&
                    java.util.Arrays.equals(this.correlationSets, other.getCorrelationSets()))) &&
            ((this.endpoints == null && other.getEndpoints() == null) ||
                (this.endpoints != null &&
                    java.util.Arrays.equals(this.endpoints, other.getEndpoints())));
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
        if (getSiid() != null) {
            _hashCode += getSiid().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getParentScopeRef() != null) {
            _hashCode += getParentScopeRef().hashCode();
        }
        if (getChildren() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getChildren());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChildren(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getActivities() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getActivities());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getActivities(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getVariables() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getVariables());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVariables(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCorrelationSets() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getCorrelationSets());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCorrelationSets(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEndpoints() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getEndpoints());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEndpoints(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TScopeInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "siid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeStatus"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentScopeRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "parent-scope-ref"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("children");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "children"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tScopeRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "child-ref"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activities");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activities"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-info"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("variables");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "variables"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tVariableRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "variable-ref"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correlationSets");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "correlation-sets"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">>tScopeInfo>correlation-sets>correlation-set"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "correlation-set"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endpoints");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "endpoints"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tEndpointReferences>endpoint-ref"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "endpoint-ref"));
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
