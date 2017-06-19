
/**
 * InstanceManagementServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:33:10 UTC)
 */

    package org.wso2.bps.management.wsdl.instancemanagement;

    /**
     *  InstanceManagementServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class InstanceManagementServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public InstanceManagementServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public InstanceManagementServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getInstanceSummary method
            * override this method for handling normal response from getInstanceSummary operation
            */
           public void receiveResultgetInstanceSummary(
                    org.wso2.bps.management.schema.InstanceSummaryE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getInstanceSummary operation
           */
            public void receiveErrorgetInstanceSummary(java.lang.Exception e) {
            }
                
               // No methods generated for meps other than in-out
                
           /**
            * auto generated Axis2 call back method for getPaginatedInstanceList method
            * override this method for handling normal response from getPaginatedInstanceList operation
            */
           public void receiveResultgetPaginatedInstanceList(
                    org.wso2.bps.management.schema.PaginatedInstanceList result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPaginatedInstanceList operation
           */
            public void receiveErrorgetPaginatedInstanceList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getActivityLifeCycleFilter method
            * override this method for handling normal response from getActivityLifeCycleFilter operation
            */
           public void receiveResultgetActivityLifeCycleFilter(
                    org.wso2.bps.management.schema.ActivityLifeCycleEvents result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getActivityLifeCycleFilter operation
           */
            public void receiveErrorgetActivityLifeCycleFilter(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getInstanceInfo method
            * override this method for handling normal response from getInstanceInfo operation
            */
           public void receiveResultgetInstanceInfo(
                    org.wso2.bps.management.schema.InstanceInfo result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getInstanceInfo operation
           */
            public void receiveErrorgetInstanceInfo(java.lang.Exception e) {
            }
                
               // No methods generated for meps other than in-out
                
           /**
            * auto generated Axis2 call back method for deleteInstances method
            * override this method for handling normal response from deleteInstances operation
            */
           public void receiveResultdeleteInstances(
                    org.wso2.bps.management.schema.DeleteInstanceResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteInstances operation
           */
            public void receiveErrordeleteInstances(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getInstanceInfoWithEvents method
            * override this method for handling normal response from getInstanceInfoWithEvents operation
            */
           public void receiveResultgetInstanceInfoWithEvents(
                    org.wso2.bps.management.schema.InstanceInfoWithEvents result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getInstanceInfoWithEvents operation
           */
            public void receiveErrorgetInstanceInfoWithEvents(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLongRunningInstances method
            * override this method for handling normal response from getLongRunningInstances operation
            */
           public void receiveResultgetLongRunningInstances(
                    org.wso2.bps.management.schema.GetLongRunningInstancesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLongRunningInstances operation
           */
            public void receiveErrorgetLongRunningInstances(java.lang.Exception e) {
            }
                
               // No methods generated for meps other than in-out
                
               // No methods generated for meps other than in-out
                


    }
    