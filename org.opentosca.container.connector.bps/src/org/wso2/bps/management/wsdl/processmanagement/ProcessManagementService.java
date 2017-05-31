

/**
 * ProcessManagementService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

    package org.wso2.bps.management.wsdl.processmanagement;

    /*
     *  ProcessManagementService java interface
     */

    public interface ProcessManagementService {
          

        /**
          * Auto generated method signature
          * 
                    * @param getAllProcesses0
                
             * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException : 
         */

         
                     public org.wso2.bps.management.schema.ProcessIDList getAllProcesses(

                        org.wso2.bps.management.schema.GetAllProcesses getAllProcesses0)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getAllProcesses0
            
          */
        public void startgetAllProcesses(

            org.wso2.bps.management.schema.GetAllProcesses getAllProcesses0,

            final org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
                 * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException : 
         */
        public void  retireProcess(
         org.wso2.bps.management.schema.RetireProcessIn retireProcessIn2

        ) throws java.rmi.RemoteException
        
        
               ,org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException;

        

        /**
          * Auto generated method signature
          * 
                    * @param getPaginatedProcessListInput3
                
             * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException : 
         */

         
                     public org.wso2.bps.management.schema.PaginatedProcessInfoList getPaginatedProcessList(

                        org.wso2.bps.management.schema.GetPaginatedProcessListInput getPaginatedProcessListInput3)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getPaginatedProcessListInput3
            
          */
        public void startgetPaginatedProcessList(

            org.wso2.bps.management.schema.GetPaginatedProcessListInput getPaginatedProcessListInput3,

            final org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getProcessInfoIn5
                
             * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException : 
         */

         
                     public org.wso2.bps.management.schema.ProcessInfo getProcessInfo(

                        org.wso2.bps.management.schema.GetProcessInfoIn getProcessInfoIn5)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getProcessInfoIn5
            
          */
        public void startgetProcessInfo(

            org.wso2.bps.management.schema.GetProcessInfoIn getProcessInfoIn5,

            final org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
                 * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException : 
         */
        public void  activateProcess(
         org.wso2.bps.management.schema.ActivateProcessIn activateProcessIn7

        ) throws java.rmi.RemoteException
        
        
               ,org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException;

        

        
       //
       }
    