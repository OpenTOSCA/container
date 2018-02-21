

/**
 * BPELUploader.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */

package org.wso2.carbon.bpel.deployer.services;

/*
 * BPELUploader java interface
 */

public interface BPELUploader {


    /**
     * Auto generated method signature
     *
     * @param uploadService1
     *
     */


    public void uploadService(

                    org.wso2.carbon.bpel.deployer.services.UploadService uploadService1)
        throws java.rmi.RemoteException;


    /**
     * Auto generated method signature for Asynchronous Invocations
     *
     * @param uploadService1
     *
     */
    public void startuploadService(

                    org.wso2.carbon.bpel.deployer.services.UploadService uploadService1,

                    final org.wso2.carbon.bpel.deployer.services.BPELUploaderCallbackHandler callback)

        throws java.rmi.RemoteException;



    //
}
