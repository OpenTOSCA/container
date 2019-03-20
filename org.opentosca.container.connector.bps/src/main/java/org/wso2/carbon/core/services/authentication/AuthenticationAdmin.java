

/**
 * AuthenticationAdmin.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */

package org.wso2.carbon.core.services.authentication;

/*
 * AuthenticationAdmin java interface
 */

public interface AuthenticationAdmin {


  /**
   * Auto generated method signature
   *
   * @param loginWithRememberMeOption0
   *
   * @throws org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException
   *         :
   */


  public org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse loginWithRememberMeOption(

    org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption loginWithRememberMeOption0) throws java.rmi.RemoteException

    ,
    org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param loginWithRememberMeOption0
   *
   */
  public void startloginWithRememberMeOption(

    org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption loginWithRememberMeOption0,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  /**
   * Auto generated method signature
   *
   * @param login2
   *
   * @throws org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException
   *         :
   */


  public org.wso2.carbon.core.services.authentication.LoginResponse login(

    org.wso2.carbon.core.services.authentication.Login login2) throws java.rmi.RemoteException

    ,
    org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param login2
   *
   */
  public void startlogin(

    org.wso2.carbon.core.services.authentication.Login login2,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  /**
   * Auto generated method signature
   *
   * @param getAuthenticatorName4
   *
   */


  public org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse getAuthenticatorName(

    org.wso2.carbon.core.services.authentication.GetAuthenticatorName getAuthenticatorName4) throws java.rmi.RemoteException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param getAuthenticatorName4
   *
   */
  public void startgetAuthenticatorName(

    org.wso2.carbon.core.services.authentication.GetAuthenticatorName getAuthenticatorName4,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  /**
   * Auto generated method signature
   *
   * @param getPriority6
   *
   */


  public org.wso2.carbon.core.services.authentication.GetPriorityResponse getPriority(

    org.wso2.carbon.core.services.authentication.GetPriority getPriority6) throws java.rmi.RemoteException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param getPriority6
   *
   */
  public void startgetPriority(

    org.wso2.carbon.core.services.authentication.GetPriority getPriority6,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  /**
   * Auto generated method signature
   *
   * @param isDisabled8
   *
   */


  public org.wso2.carbon.core.services.authentication.IsDisabledResponse isDisabled(

    org.wso2.carbon.core.services.authentication.IsDisabled isDisabled8) throws java.rmi.RemoteException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param isDisabled8
   *
   */
  public void startisDisabled(

    org.wso2.carbon.core.services.authentication.IsDisabled isDisabled8,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  /**
   * Auto generated method signature
   *
   * @param authenticateWithRememberMe10
   *
   */


  public org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse authenticateWithRememberMe(

    org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe authenticateWithRememberMe10) throws java.rmi.RemoteException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param authenticateWithRememberMe10
   *
   */
  public void startauthenticateWithRememberMe(

    org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe authenticateWithRememberMe10,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  /**
   * Auto generated method signature
   *
   * @param loginWithRememberMeCookie12
   *
   */


  public org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse loginWithRememberMeCookie(

    org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie loginWithRememberMeCookie12) throws java.rmi.RemoteException;


  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @param loginWithRememberMeCookie12
   *
   */
  public void startloginWithRememberMeCookie(

    org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie loginWithRememberMeCookie12,

    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

    throws java.rmi.RemoteException;


  //
}
