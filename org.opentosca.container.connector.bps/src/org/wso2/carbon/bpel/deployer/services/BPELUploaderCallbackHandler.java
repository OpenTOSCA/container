
/**
 * BPELUploaderCallbackHandler.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */

package org.wso2.carbon.bpel.deployer.services;

/**
 * BPELUploaderCallbackHandler Callback class, Users can extend this class and implement their own
 * receiveResult and receiveError methods.
 */
public abstract class BPELUploaderCallbackHandler {



    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking Web service call is
     * finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data that will be avilable
     *        at the time this callback is called.
     */
    public BPELUploaderCallbackHandler(final Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public BPELUploaderCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return this.clientData;
    }


    /**
     * auto generated Axis2 call back method for uploadService method override this method for handling
     * normal response from uploadService operation
     */
    public void receiveResultuploadService() {}

    /**
     * auto generated Axis2 Error handler override this method for handling error response from
     * uploadService operation
     */
    public void receiveErroruploadService(final java.lang.Exception e) {}



}
