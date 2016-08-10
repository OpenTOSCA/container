
/**
 * ProcessManagementException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

package org.wso2.bps.management.wsdl.processmanagement;

public class ProcessManagementException extends java.lang.Exception{

    private static final long serialVersionUID = 1330000259067L;
    
    private org.wso2.bps.management.schema.ProcessManagementException faultMessage;

    
        public ProcessManagementException() {
            super("ProcessManagementException");
        }

        public ProcessManagementException(java.lang.String s) {
           super(s);
        }

        public ProcessManagementException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public ProcessManagementException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.wso2.bps.management.schema.ProcessManagementException msg){
       faultMessage = msg;
    }
    
    public org.wso2.bps.management.schema.ProcessManagementException getFaultMessage(){
       return faultMessage;
    }
}
    