/**
 * ManagementFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi;

public class ManagementFault extends org.apache.axis.AxisFault {
    public java.lang.String managementFault;
    public java.lang.String getManagementFault() {
        return this.managementFault;
    }

    public ManagementFault() {
    }

    public ManagementFault(java.lang.Exception target) {
        super(target);
    }

    public ManagementFault(java.lang.String message, java.lang.Throwable t) {
        super(message, t);
    }

      public ManagementFault(java.lang.String managementFault) {
        this.managementFault = managementFault;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, managementFault);
    }
}
