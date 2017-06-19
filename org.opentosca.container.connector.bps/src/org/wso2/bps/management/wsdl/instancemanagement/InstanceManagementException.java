
/**
 * InstanceManagementException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

package org.wso2.bps.management.wsdl.instancemanagement;

public class InstanceManagementException extends java.lang.Exception{

    private static final long serialVersionUID = 1330000308258L;
    
    private org.wso2.bps.management.schema.InstanceManagementException faultMessage;

    
        public InstanceManagementException() {
            super("InstanceManagementException");
        }

        public InstanceManagementException(java.lang.String s) {
           super(s);
        }

        public InstanceManagementException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public InstanceManagementException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.wso2.bps.management.schema.InstanceManagementException msg){
       faultMessage = msg;
    }
    
    public org.wso2.bps.management.schema.InstanceManagementException getFaultMessage(){
       return faultMessage;
    }
}
    