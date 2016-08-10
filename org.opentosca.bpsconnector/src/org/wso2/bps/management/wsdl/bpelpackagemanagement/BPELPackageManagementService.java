

/**
 * BPELPackageManagementService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

    package org.wso2.bps.management.wsdl.bpelpackagemanagement;

    /*
     *  BPELPackageManagementService java interface
     */

    public interface BPELPackageManagementService {
          

        /**
          * Auto generated method signature
          * 
                    * @param undeployBPELPackage0
                
             * @throws org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException : 
         */

         
                     public org.wso2.bps.management.schema.UndeployStatus undeployBPELPackage(

                        org.wso2.bps.management.schema.UndeployBPELPackage undeployBPELPackage0)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param undeployBPELPackage0
            
          */
        public void startundeployBPELPackage(

            org.wso2.bps.management.schema.UndeployBPELPackage undeployBPELPackage0,

            final org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param listDeployedPackagesPaginated2
                
             * @throws org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException : 
         */

         
                     public org.wso2.bps.management.schema.DeployedPackagesPaginated listDeployedPackagesPaginated(

                        org.wso2.bps.management.schema.ListDeployedPackagesPaginated listDeployedPackagesPaginated2)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param listDeployedPackagesPaginated2
            
          */
        public void startlistDeployedPackagesPaginated(

            org.wso2.bps.management.schema.ListDeployedPackagesPaginated listDeployedPackagesPaginated2,

            final org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    