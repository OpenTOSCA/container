
/**
 * BPELPackageManagementServiceCallbackHandler.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */

package org.wso2.bps.management.wsdl.bpelpackagemanagement;

/**
 * BPELPackageManagementServiceCallbackHandler Callback class, Users can extend this class and
 * implement their own receiveResult and receiveError methods.
 */
public abstract class BPELPackageManagementServiceCallbackHandler {


  protected Object clientData;

  /**
   * User can pass in any object that needs to be accessed once the NonBlocking Web service call is
   * finished and appropriate method of this CallBack is called.
   *
   * @param clientData Object mechanism by which the user can pass in user data that will be avilable
   *        at the time this callback is called.
   */
  public BPELPackageManagementServiceCallbackHandler(final Object clientData) {
    this.clientData = clientData;
  }

  /**
   * Please use this constructor if you don't want to set any clientData
   */
  public BPELPackageManagementServiceCallbackHandler() {
    this.clientData = null;
  }

  /**
   * Get the client data
   */

  public Object getClientData() {
    return this.clientData;
  }


  /**
   * auto generated Axis2 call back method for undeployBPELPackage method override this method for
   * handling normal response from undeployBPELPackage operation
   */
  public void receiveResultundeployBPELPackage(final org.wso2.bps.management.schema.UndeployStatus result) {
  }

  /**
   * auto generated Axis2 Error handler override this method for handling error response from
   * undeployBPELPackage operation
   */
  public void receiveErrorundeployBPELPackage(final java.lang.Exception e) {
  }

  /**
   * auto generated Axis2 call back method for listDeployedPackagesPaginated method override this
   * method for handling normal response from listDeployedPackagesPaginated operation
   */
  public void receiveResultlistDeployedPackagesPaginated(final org.wso2.bps.management.schema.DeployedPackagesPaginated result) {
  }

  /**
   * auto generated Axis2 Error handler override this method for handling error response from
   * listDeployedPackagesPaginated operation
   */
  public void receiveErrorlistDeployedPackagesPaginated(final java.lang.Exception e) {
  }


}
