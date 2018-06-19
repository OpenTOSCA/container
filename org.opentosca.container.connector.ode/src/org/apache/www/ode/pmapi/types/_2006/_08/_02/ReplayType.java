/**
 * ReplayType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class ReplayType  implements java.io.Serializable {
    private java.lang.String mock;

    private java.lang.String mockQuery;

    private java.lang.String live;

    public ReplayType() {
    }

    public ReplayType(
           java.lang.String mock,
           java.lang.String mockQuery,
           java.lang.String live) {
           this.mock = mock;
           this.mockQuery = mockQuery;
           this.live = live;
    }


    /**
     * Gets the mock value for this ReplayType.
     * 
     * @return mock
     */
    public java.lang.String getMock() {
        return mock;
    }


    /**
     * Sets the mock value for this ReplayType.
     * 
     * @param mock
     */
    public void setMock(java.lang.String mock) {
        this.mock = mock;
    }


    /**
     * Gets the mockQuery value for this ReplayType.
     * 
     * @return mockQuery
     */
    public java.lang.String getMockQuery() {
        return mockQuery;
    }


    /**
     * Sets the mockQuery value for this ReplayType.
     * 
     * @param mockQuery
     */
    public void setMockQuery(java.lang.String mockQuery) {
        this.mockQuery = mockQuery;
    }


    /**
     * Gets the live value for this ReplayType.
     * 
     * @return live
     */
    public java.lang.String getLive() {
        return live;
    }


    /**
     * Sets the live value for this ReplayType.
     * 
     * @param live
     */
    public void setLive(java.lang.String live) {
        this.live = live;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReplayType)) return false;
        ReplayType other = (ReplayType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mock==null && other.getMock()==null) || 
             (this.mock!=null &&
              this.mock.equals(other.getMock()))) &&
            ((this.mockQuery==null && other.getMockQuery()==null) || 
             (this.mockQuery!=null &&
              this.mockQuery.equals(other.getMockQuery()))) &&
            ((this.live==null && other.getLive()==null) || 
             (this.live!=null &&
              this.live.equals(other.getLive())));
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
        if (getMock() != null) {
            _hashCode += getMock().hashCode();
        }
        if (getMockQuery() != null) {
            _hashCode += getMockQuery().hashCode();
        }
        if (getLive() != null) {
            _hashCode += getLive().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReplayType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "ReplayType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mock");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "mock"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anySimpleType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mockQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "mockQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("live");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "live"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anySimpleType"));
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
