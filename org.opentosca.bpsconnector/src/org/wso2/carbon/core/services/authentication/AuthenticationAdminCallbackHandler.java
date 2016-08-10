
/**
 * AuthenticationAdminCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

    package org.wso2.carbon.core.services.authentication;

    /**
     *  AuthenticationAdminCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class AuthenticationAdminCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public AuthenticationAdminCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public AuthenticationAdminCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for loginWithRememberMeOption method
            * override this method for handling normal response from loginWithRememberMeOption operation
            */
           public void receiveResultloginWithRememberMeOption(
                    org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from loginWithRememberMeOption operation
           */
            public void receiveErrorloginWithRememberMeOption(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for login method
            * override this method for handling normal response from login operation
            */
           public void receiveResultlogin(
                    org.wso2.carbon.core.services.authentication.LoginResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from login operation
           */
            public void receiveErrorlogin(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAuthenticatorName method
            * override this method for handling normal response from getAuthenticatorName operation
            */
           public void receiveResultgetAuthenticatorName(
                    org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAuthenticatorName operation
           */
            public void receiveErrorgetAuthenticatorName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPriority method
            * override this method for handling normal response from getPriority operation
            */
           public void receiveResultgetPriority(
                    org.wso2.carbon.core.services.authentication.GetPriorityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPriority operation
           */
            public void receiveErrorgetPriority(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for isDisabled method
            * override this method for handling normal response from isDisabled operation
            */
           public void receiveResultisDisabled(
                    org.wso2.carbon.core.services.authentication.IsDisabledResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from isDisabled operation
           */
            public void receiveErrorisDisabled(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for authenticateWithRememberMe method
            * override this method for handling normal response from authenticateWithRememberMe operation
            */
           public void receiveResultauthenticateWithRememberMe(
                    org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from authenticateWithRememberMe operation
           */
            public void receiveErrorauthenticateWithRememberMe(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for loginWithRememberMeCookie method
            * override this method for handling normal response from loginWithRememberMeCookie operation
            */
           public void receiveResultloginWithRememberMeCookie(
                    org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from loginWithRememberMeCookie operation
           */
            public void receiveErrorloginWithRememberMeCookie(java.lang.Exception e) {
            }
                


    }
    