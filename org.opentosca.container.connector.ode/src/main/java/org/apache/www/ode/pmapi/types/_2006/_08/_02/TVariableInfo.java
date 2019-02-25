/**
 * TVariableInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;


/**
 * Information about a variable (basically the value)
 */
public class TVariableInfo  implements java.io.Serializable {
    private org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef self;

    private org.apache.www.ode.pmapi.types._2006._08._02.TVariableInfoValue value;

    public TVariableInfo() {
    }

    public TVariableInfo(
           org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef self,
           org.apache.www.ode.pmapi.types._2006._08._02.TVariableInfoValue value) {
           this.self = self;
           this.value = value;
    }


    /**
     * Gets the self value for this TVariableInfo.
     * 
     * @return self
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef getSelf() {
        return self;
    }


    /**
     * Sets the self value for this TVariableInfo.
     * 
     * @param self
     */
    public void setSelf(org.apache.www.ode.pmapi.types._2006._08._02.TVariableRef self) {
        this.self = self;
    }


    /**
     * Gets the value value for this TVariableInfo.
     * 
     * @return value
     */
    public org.apache.www.ode.pmapi.types._2006._08._02.TVariableInfoValue getValue() {
        return value;
    }


    /**
     * Sets the value value for this TVariableInfo.
     * 
     * @param value
     */
    public void setValue(org.apache.www.ode.pmapi.types._2006._08._02.TVariableInfoValue value) {
        this.value = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TVariableInfo)) return false;
        TVariableInfo other = (TVariableInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.self==null && other.getSelf()==null) || 
             (this.self!=null &&
              this.self.equals(other.getSelf()))) &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue())));
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
        if (getSelf() != null) {
            _hashCode += getSelf().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TVariableInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tVariableInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("self");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "self"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tVariableRef"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", ">tVariableInfo>value"));
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
          new  org.apache.axis.encoding.ser.BeanSerializer(
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
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
