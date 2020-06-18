/**
 * TProcessInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Information about a BPEL process.
 */
public class TProcessInfo implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    /* The unique name/id of the process. */
    private java.lang.String pid;

    /* Process status. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TProcessStatus status;

    /* Process version. */
    private long version;

    /* Information about the process
     *                         definition. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TDefinitionInfo definitionInfo;

    /* Information about the process
     *                         deployment. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TDeploymentInfo deploymentInfo;

    /* Summary of the instances belonging to this
     *                         process. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummary instanceSummary;

    /* Process properties. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TProcessPropertiesProperty[] properties;

    /* Endpoint references. */
    private org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] endpoints;

    /* Process documents */
    private org.apache.www.ode.pmapi.types._2006._08._02.TDocumentInfo[] documents;

    private org.apache.axis.message.MessageElement[] _any;

    public TProcessInfo() {
    }

    public TProcessInfo(
        java.lang.String pid,
        org.apache.www.ode.pmapi.types._2006._08._02.TProcessStatus status,
        long version,
        org.apache.www.ode.pmapi.types._2006._08._02.TDefinitionInfo definitionInfo,
        org.apache.www.ode.pmapi.types._2006._08._02.TDeploymentInfo deploymentInfo,
        org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummary instanceSummary,
        org.apache.www.ode.pmapi.types._2006._08._02.TProcessPropertiesProperty[] properties,
        org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] endpoints,
        org.apache.www.ode.pmapi.types._2006._08._02.TDocumentInfo[] documents,
        org.apache.axis.message.MessageElement[] _any) {
        this.pid = pid;
        this.status = status;
        this.version = version;
        this.definitionInfo = definitionInfo;
        this.deploymentInfo = deploymentInfo;
        this.instanceSummary = instanceSummary;
        this.properties = properties;
        this.endpoints = endpoints;
        this.documents = documents;
        this._any = _any;
    }

    /**
     * Gets the pid value for this TProcessInfo.
     *
     * @return pid   * The unique name/id of the process.
     */
    public java.lang.String getPid() {
        return pid;
    }

    /**
     * Sets the pid value for this TProcessInfo.
     *
     * @param pid * The unique name/id of the process.
     */
    public void setPid(java.lang.String pid) {
        this.pid = pid;
    }

    /**
     * Gets the status value for this TProcessInfo.
     *
     * @return status   * Process status.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TProcessStatus getStatus() {
        return status;
    }

    /**
     * Sets the status value for this TProcessInfo.
     *
     * @param status * Process status.
     */
    public void setStatus(org.apache.www.ode.pmapi.types._2006._08._02.TProcessStatus status) {
        this.status = status;
    }

    /**
     * Gets the version value for this TProcessInfo.
     *
     * @return version   * Process version.
     */
    public long getVersion() {
        return version;
    }

    /**
     * Sets the version value for this TProcessInfo.
     *
     * @param version * Process version.
     */
    public void setVersion(long version) {
        this.version = version;
    }

    /**
     * Gets the definitionInfo value for this TProcessInfo.
     *
     * @return definitionInfo   * Information about the process definition.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TDefinitionInfo getDefinitionInfo() {
        return definitionInfo;
    }

    /**
     * Sets the definitionInfo value for this TProcessInfo.
     *
     * @param definitionInfo * Information about the process definition.
     */
    public void setDefinitionInfo(org.apache.www.ode.pmapi.types._2006._08._02.TDefinitionInfo definitionInfo) {
        this.definitionInfo = definitionInfo;
    }

    /**
     * Gets the deploymentInfo value for this TProcessInfo.
     *
     * @return deploymentInfo   * Information about the process deployment.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TDeploymentInfo getDeploymentInfo() {
        return deploymentInfo;
    }

    /**
     * Sets the deploymentInfo value for this TProcessInfo.
     *
     * @param deploymentInfo * Information about the process deployment.
     */
    public void setDeploymentInfo(org.apache.www.ode.pmapi.types._2006._08._02.TDeploymentInfo deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
    }

    /**
     * Gets the instanceSummary value for this TProcessInfo.
     *
     * @return instanceSummary   * Summary of the instances belonging to this process.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummary getInstanceSummary() {
        return instanceSummary;
    }

    /**
     * Sets the instanceSummary value for this TProcessInfo.
     *
     * @param instanceSummary * Summary of the instances belonging to this process.
     */
    public void setInstanceSummary(org.apache.www.ode.pmapi.types._2006._08._02.TInstanceSummary instanceSummary) {
        this.instanceSummary = instanceSummary;
    }

    /**
     * Gets the properties value for this TProcessInfo.
     *
     * @return properties   * Process properties.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TProcessPropertiesProperty[] getProperties() {
        return properties;
    }

    /**
     * Sets the properties value for this TProcessInfo.
     *
     * @param properties * Process properties.
     */
    public void setProperties(org.apache.www.ode.pmapi.types._2006._08._02.TProcessPropertiesProperty[] properties) {
        this.properties = properties;
    }

    /**
     * Gets the endpoints value for this TProcessInfo.
     *
     * @return endpoints   * Endpoint references.
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] getEndpoints() {
        return endpoints;
    }

    /**
     * Sets the endpoints value for this TProcessInfo.
     *
     * @param endpoints * Endpoint references.
     */
    public void setEndpoints(org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef[] endpoints) {
        this.endpoints = endpoints;
    }

    /**
     * Gets the documents value for this TProcessInfo.
     *
     * @return documents   * Process documents
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TDocumentInfo[] getDocuments() {
        return documents;
    }

    /**
     * Sets the documents value for this TProcessInfo.
     *
     * @param documents * Process documents
     */
    public void setDocuments(org.apache.www.ode.pmapi.types._2006._08._02.TDocumentInfo[] documents) {
        this.documents = documents;
    }

    /**
     * Gets the _any value for this TProcessInfo.
     *
     * @return _any
     */
    public org.apache.axis.message.MessageElement[] get_any() {
        return _any;
    }

    /**
     * Sets the _any value for this TProcessInfo.
     */
    public void set_any(org.apache.axis.message.MessageElement[] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TProcessInfo)) return false;
        TProcessInfo other = (TProcessInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.pid == null && other.getPid() == null) ||
                (this.pid != null &&
                    this.pid.equals(other.getPid()))) &&
            ((this.status == null && other.getStatus() == null) ||
                (this.status != null &&
                    this.status.equals(other.getStatus()))) &&
            this.version == other.getVersion() &&
            ((this.definitionInfo == null && other.getDefinitionInfo() == null) ||
                (this.definitionInfo != null &&
                    this.definitionInfo.equals(other.getDefinitionInfo()))) &&
            ((this.deploymentInfo == null && other.getDeploymentInfo() == null) ||
                (this.deploymentInfo != null &&
                    this.deploymentInfo.equals(other.getDeploymentInfo()))) &&
            ((this.instanceSummary == null && other.getInstanceSummary() == null) ||
                (this.instanceSummary != null &&
                    this.instanceSummary.equals(other.getInstanceSummary()))) &&
            ((this.properties == null && other.getProperties() == null) ||
                (this.properties != null &&
                    java.util.Arrays.equals(this.properties, other.getProperties()))) &&
            ((this.endpoints == null && other.getEndpoints() == null) ||
                (this.endpoints != null &&
                    java.util.Arrays.equals(this.endpoints, other.getEndpoints()))) &&
            ((this.documents == null && other.getDocuments() == null) ||
                (this.documents != null &&
                    java.util.Arrays.equals(this.documents, other.getDocuments()))) &&
            ((this._any == null && other.get_any() == null) ||
                (this._any != null &&
                    java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getPid() != null) {
            _hashCode += getPid().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        _hashCode += new Long(getVersion()).hashCode();
        if (getDefinitionInfo() != null) {
            _hashCode += getDefinitionInfo().hashCode();
        }
        if (getDeploymentInfo() != null) {
            _hashCode += getDeploymentInfo().hashCode();
        }
        if (getInstanceSummary() != null) {
            _hashCode += getInstanceSummary().hashCode();
        }
        if (getProperties() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getProperties());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getProperties(), i);
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
        if (getDocuments() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getDocuments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocuments(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TProcessInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tProcessInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "pid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tProcessStatus"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definitionInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "definition-info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tDefinitionInfo"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deploymentInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "deployment-info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tDeploymentInfo"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instanceSummary");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "instance-summary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tInstanceSummary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("properties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "properties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tProcessProperties>property"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "property"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endpoints");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "endpoints"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tEndpointReferences>endpoint-ref"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "endpoint-ref"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "documents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tDocumentInfo"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "document"));
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
