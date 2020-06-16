/**
 * TDeploymentInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

/**
 * Information about a BPEL process
 *                 deployment.
 */
public class TDeploymentInfo implements java.io.Serializable {
    /* Deployment package the process was deployed into. */
    private java.lang.String _package;

    /* File name of BPEL document. */
    private java.lang.String document;

    /* Date the process was deployed. */
    private java.util.Calendar deployDate;

    /* The user that deployed this
     *                         process. */
    private java.lang.String deployer;

    public TDeploymentInfo() {
    }

    public TDeploymentInfo(
        java.lang.String _package,
        java.lang.String document,
        java.util.Calendar deployDate,
        java.lang.String deployer) {
        this._package = _package;
        this.document = document;
        this.deployDate = deployDate;
        this.deployer = deployer;
    }

    /**
     * Gets the _package value for this TDeploymentInfo.
     *
     * @return _package   * Deployment package the process was deployed into.
     */
    public java.lang.String get_package() {
        return _package;
    }

    /**
     * Sets the _package value for this TDeploymentInfo.
     *
     * @param _package   * Deployment package the process was deployed into.
     */
    public void set_package(java.lang.String _package) {
        this._package = _package;
    }

    /**
     * Gets the document value for this TDeploymentInfo.
     *
     * @return document   * File name of BPEL document.
     */
    public java.lang.String getDocument() {
        return document;
    }

    /**
     * Sets the document value for this TDeploymentInfo.
     *
     * @param document   * File name of BPEL document.
     */
    public void setDocument(java.lang.String document) {
        this.document = document;
    }

    /**
     * Gets the deployDate value for this TDeploymentInfo.
     *
     * @return deployDate   * Date the process was deployed.
     */
    public java.util.Calendar getDeployDate() {
        return deployDate;
    }

    /**
     * Sets the deployDate value for this TDeploymentInfo.
     *
     * @param deployDate   * Date the process was deployed.
     */
    public void setDeployDate(java.util.Calendar deployDate) {
        this.deployDate = deployDate;
    }

    /**
     * Gets the deployer value for this TDeploymentInfo.
     *
     * @return deployer   * The user that deployed this
     *                         process.
     */
    public java.lang.String getDeployer() {
        return deployer;
    }

    /**
     * Sets the deployer value for this TDeploymentInfo.
     *
     * @param deployer   * The user that deployed this
     *                         process.
     */
    public void setDeployer(java.lang.String deployer) {
        this.deployer = deployer;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TDeploymentInfo)) return false;
        TDeploymentInfo other = (TDeploymentInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this._package == null && other.get_package() == null) ||
                (this._package != null &&
                    this._package.equals(other.get_package()))) &&
            ((this.document == null && other.getDocument() == null) ||
                (this.document != null &&
                    this.document.equals(other.getDocument()))) &&
            ((this.deployDate == null && other.getDeployDate() == null) ||
                (this.deployDate != null &&
                    this.deployDate.equals(other.getDeployDate()))) &&
            ((this.deployer == null && other.getDeployer() == null) ||
                (this.deployer != null &&
                    this.deployer.equals(other.getDeployer())));
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
        if (get_package() != null) {
            _hashCode += get_package().hashCode();
        }
        if (getDocument() != null) {
            _hashCode += getDocument().hashCode();
        }
        if (getDeployDate() != null) {
            _hashCode += getDeployDate().hashCode();
        }
        if (getDeployer() != null) {
            _hashCode += getDeployer().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TDeploymentInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tDeploymentInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_package");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "package"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deployDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "deploy-date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deployer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "deployer"));
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
