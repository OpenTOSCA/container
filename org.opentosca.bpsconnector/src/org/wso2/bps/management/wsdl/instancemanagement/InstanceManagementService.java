

/**
 * InstanceManagementService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

    package org.wso2.bps.management.wsdl.instancemanagement;

    /*
     *  InstanceManagementService java interface
     */

    public interface InstanceManagementService {
          

        /**
          * Auto generated method signature
          * 
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.InstanceSummaryE getInstanceSummary(

                        )
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
          */
        public void startgetInstanceSummary(

            

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
                 * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */
        public void  resumeInstance(
         org.wso2.bps.management.schema.ResumeInstance resumeInstance4

        ) throws java.rmi.RemoteException
        
        
               ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        

        /**
          * Auto generated method signature
          * 
                    * @param getPaginatedInstanceListInput5
                
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.PaginatedInstanceList getPaginatedInstanceList(

                        org.wso2.bps.management.schema.GetPaginatedInstanceListInput getPaginatedInstanceListInput5)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getPaginatedInstanceListInput5
            
          */
        public void startgetPaginatedInstanceList(

            org.wso2.bps.management.schema.GetPaginatedInstanceListInput getPaginatedInstanceListInput5,

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getActivityLifeCycleFilterIn7
                
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.ActivityLifeCycleEvents getActivityLifeCycleFilter(

                        org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn getActivityLifeCycleFilterIn7)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getActivityLifeCycleFilterIn7
            
          */
        public void startgetActivityLifeCycleFilter(

            org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn getActivityLifeCycleFilterIn7,

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getInstanceInfoIn9
                
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.InstanceInfo getInstanceInfo(

                        org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn9)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getInstanceInfoIn9
            
          */
        public void startgetInstanceInfo(

            org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn9,

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
                 * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */
        public void  suspendInstance(
         org.wso2.bps.management.schema.SuspendInstance suspendInstance11

        ) throws java.rmi.RemoteException
        
        
               ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        

        /**
          * Auto generated method signature
          * 
                    * @param deleteInstances12
                
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.DeleteInstanceResponse deleteInstances(

                        org.wso2.bps.management.schema.DeleteInstances deleteInstances12)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param deleteInstances12
            
          */
        public void startdeleteInstances(

            org.wso2.bps.management.schema.DeleteInstances deleteInstances12,

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getInstanceInfoIn14
                
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.InstanceInfoWithEvents getInstanceInfoWithEvents(

                        org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn14)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getInstanceInfoIn14
            
          */
        public void startgetInstanceInfoWithEvents(

            org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn14,

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getLongRunningInstancesInput16
                
             * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */

         
                     public org.wso2.bps.management.schema.GetLongRunningInstancesResponse getLongRunningInstances(

                        org.wso2.bps.management.schema.GetLongRunningInstancesInput getLongRunningInstancesInput16)
                        throws java.rmi.RemoteException
             
          ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getLongRunningInstancesInput16
            
          */
        public void startgetLongRunningInstances(

            org.wso2.bps.management.schema.GetLongRunningInstancesInput getLongRunningInstancesInput16,

            final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
                 * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */
        public void  recoverActivity(
         org.wso2.bps.management.schema.RecoverActivity recoverActivity18

        ) throws java.rmi.RemoteException
        
        
               ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
                 * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException : 
         */
        public void  terminateInstance(
         org.wso2.bps.management.schema.TerminateInstance terminateInstance19

        ) throws java.rmi.RemoteException
        
        
               ,org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException;

        

        
       //
       }
    