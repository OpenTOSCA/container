
/**
 * AuthenticationAdminAuthenticationExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

package org.wso2.carbon.core.services.authentication;

public class AuthenticationAdminAuthenticationExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1329838889320L;
    
    private org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException faultMessage;

    
        public AuthenticationAdminAuthenticationExceptionException() {
            super("AuthenticationAdminAuthenticationExceptionException");
        }

        public AuthenticationAdminAuthenticationExceptionException(java.lang.String s) {
           super(s);
        }

        public AuthenticationAdminAuthenticationExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public AuthenticationAdminAuthenticationExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException msg){
       faultMessage = msg;
    }
    
    public org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException getFaultMessage(){
       return faultMessage;
    }
}
    