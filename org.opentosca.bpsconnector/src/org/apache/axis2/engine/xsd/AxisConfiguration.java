
/**
 * AxisConfiguration.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.engine.xsd;
            

            /**
            *  AxisConfiguration bean class
            */
        
        public  class AxisConfiguration
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = AxisConfiguration
                Namespace URI = http://engine.axis2.apache.org/xsd
                Namespace Prefix = ns8
                */
            

                        /**
                        * field for ChildFirstClassLoading
                        */

                        
                                    protected boolean localChildFirstClassLoading ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChildFirstClassLoadingTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getChildFirstClassLoading(){
                               return localChildFirstClassLoading;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ChildFirstClassLoading
                               */
                               public void setChildFirstClassLoading(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localChildFirstClassLoadingTracker =
                                       true;
                                   
                                            this.localChildFirstClassLoading=param;
                                    

                               }
                            

                        /**
                        * field for ClusteringAgent
                        */

                        
                                    protected org.apache.axis2.clustering.xsd.ClusteringAgent localClusteringAgent ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localClusteringAgentTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.clustering.xsd.ClusteringAgent
                           */
                           public  org.apache.axis2.clustering.xsd.ClusteringAgent getClusteringAgent(){
                               return localClusteringAgent;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ClusteringAgent
                               */
                               public void setClusteringAgent(org.apache.axis2.clustering.xsd.ClusteringAgent param){
                            localClusteringAgentTracker = true;
                                   
                                            this.localClusteringAgent=param;
                                    

                               }
                            

                        /**
                        * field for Configurator
                        */

                        
                                    protected org.apache.axis2.engine.xsd.AxisConfigurator localConfigurator ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localConfiguratorTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.AxisConfigurator
                           */
                           public  org.apache.axis2.engine.xsd.AxisConfigurator getConfigurator(){
                               return localConfigurator;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Configurator
                               */
                               public void setConfigurator(org.apache.axis2.engine.xsd.AxisConfigurator param){
                            localConfiguratorTracker = true;
                                   
                                            this.localConfigurator=param;
                                    

                               }
                            

                        /**
                        * field for FaultyModules
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localFaultyModules ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultyModulesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getFaultyModules(){
                               return localFaultyModules;
                           }

                           
                        


                               
                              /**
                               * validate the array for FaultyModules
                               */
                              protected void validateFaultyModules(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param FaultyModules
                              */
                              public void setFaultyModules(java.lang.String[] param){
                              
                                   validateFaultyModules(param);

                               localFaultyModulesTracker = true;
                                      
                                      this.localFaultyModules=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addFaultyModules(java.lang.String param){
                                   if (localFaultyModules == null){
                                   localFaultyModules = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localFaultyModulesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localFaultyModules);
                               list.add(param);
                               this.localFaultyModules =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for FaultyServices
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localFaultyServices ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultyServicesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getFaultyServices(){
                               return localFaultyServices;
                           }

                           
                        


                               
                              /**
                               * validate the array for FaultyServices
                               */
                              protected void validateFaultyServices(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param FaultyServices
                              */
                              public void setFaultyServices(java.lang.String[] param){
                              
                                   validateFaultyServices(param);

                               localFaultyServicesTracker = true;
                                      
                                      this.localFaultyServices=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addFaultyServices(java.lang.String param){
                                   if (localFaultyServices == null){
                                   localFaultyServices = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localFaultyServicesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localFaultyServices);
                               list.add(param);
                               this.localFaultyServices =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for FaultyServicesDuetoModules
                        */

                        
                                    protected authclient.java.util.xsd.Map localFaultyServicesDuetoModules ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultyServicesDuetoModulesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getFaultyServicesDuetoModules(){
                               return localFaultyServicesDuetoModules;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FaultyServicesDuetoModules
                               */
                               public void setFaultyServicesDuetoModules(authclient.java.util.xsd.Map param){
                            localFaultyServicesDuetoModulesTracker = true;
                                   
                                            this.localFaultyServicesDuetoModules=param;
                                    

                               }
                            

                        /**
                        * field for GlobalModules
                        */

                        
                                    protected java.lang.Object localGlobalModules ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localGlobalModulesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getGlobalModules(){
                               return localGlobalModules;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param GlobalModules
                               */
                               public void setGlobalModules(java.lang.Object param){
                            localGlobalModulesTracker = true;
                                   
                                            this.localGlobalModules=param;
                                    

                               }
                            

                        /**
                        * field for GlobalOutPhase
                        */

                        
                                    protected java.lang.Object localGlobalOutPhase ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localGlobalOutPhaseTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getGlobalOutPhase(){
                               return localGlobalOutPhase;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param GlobalOutPhase
                               */
                               public void setGlobalOutPhase(java.lang.Object param){
                            localGlobalOutPhaseTracker = true;
                                   
                                            this.localGlobalOutPhase=param;
                                    

                               }
                            

                        /**
                        * field for InFaultFlowPhases
                        */

                        
                                    protected java.lang.Object localInFaultFlowPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInFaultFlowPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getInFaultFlowPhases(){
                               return localInFaultFlowPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InFaultFlowPhases
                               */
                               public void setInFaultFlowPhases(java.lang.Object param){
                            localInFaultFlowPhasesTracker = true;
                                   
                                            this.localInFaultFlowPhases=param;
                                    

                               }
                            

                        /**
                        * field for InFaultPhases
                        */

                        
                                    protected java.lang.Object localInFaultPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInFaultPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getInFaultPhases(){
                               return localInFaultPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InFaultPhases
                               */
                               public void setInFaultPhases(java.lang.Object param){
                            localInFaultPhasesTracker = true;
                                   
                                            this.localInFaultPhases=param;
                                    

                               }
                            

                        /**
                        * field for InFlowPhases
                        */

                        
                                    protected java.lang.Object localInFlowPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInFlowPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getInFlowPhases(){
                               return localInFlowPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InFlowPhases
                               */
                               public void setInFlowPhases(java.lang.Object param){
                            localInFlowPhasesTracker = true;
                                   
                                            this.localInFlowPhases=param;
                                    

                               }
                            

                        /**
                        * field for InPhasesUptoAndIncludingPostDispatch
                        */

                        
                                    protected java.lang.Object localInPhasesUptoAndIncludingPostDispatch ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInPhasesUptoAndIncludingPostDispatchTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getInPhasesUptoAndIncludingPostDispatch(){
                               return localInPhasesUptoAndIncludingPostDispatch;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InPhasesUptoAndIncludingPostDispatch
                               */
                               public void setInPhasesUptoAndIncludingPostDispatch(java.lang.Object param){
                            localInPhasesUptoAndIncludingPostDispatchTracker = true;
                                   
                                            this.localInPhasesUptoAndIncludingPostDispatch=param;
                                    

                               }
                            

                        /**
                        * field for Key
                        */

                        
                                    protected java.lang.Object localKey ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localKeyTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getKey(){
                               return localKey;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Key
                               */
                               public void setKey(java.lang.Object param){
                            localKeyTracker = true;
                                   
                                            this.localKey=param;
                                    

                               }
                            

                        /**
                        * field for LocalPolicyAssertions
                        * This was an Array!
                        */

                        
                                    protected java.lang.Object[] localLocalPolicyAssertions ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLocalPolicyAssertionsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object[]
                           */
                           public  java.lang.Object[] getLocalPolicyAssertions(){
                               return localLocalPolicyAssertions;
                           }

                           
                        


                               
                              /**
                               * validate the array for LocalPolicyAssertions
                               */
                              protected void validateLocalPolicyAssertions(java.lang.Object[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param LocalPolicyAssertions
                              */
                              public void setLocalPolicyAssertions(java.lang.Object[] param){
                              
                                   validateLocalPolicyAssertions(param);

                               localLocalPolicyAssertionsTracker = true;
                                      
                                      this.localLocalPolicyAssertions=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.Object
                             */
                             public void addLocalPolicyAssertions(java.lang.Object param){
                                   if (localLocalPolicyAssertions == null){
                                   localLocalPolicyAssertions = new java.lang.Object[]{};
                                   }

                            
                                 //update the setting tracker
                                localLocalPolicyAssertionsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localLocalPolicyAssertions);
                               list.add(param);
                               this.localLocalPolicyAssertions =
                             (java.lang.Object[])list.toArray(
                            new java.lang.Object[list.size()]);

                             }
                             

                        /**
                        * field for ModuleClassLoader
                        */

                        
                                    protected java.lang.Object localModuleClassLoader ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModuleClassLoaderTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getModuleClassLoader(){
                               return localModuleClassLoader;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModuleClassLoader
                               */
                               public void setModuleClassLoader(java.lang.Object param){
                            localModuleClassLoaderTracker = true;
                                   
                                            this.localModuleClassLoader=param;
                                    

                               }
                            

                        /**
                        * field for Modules
                        */

                        
                                    protected java.lang.Object localModules ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModulesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getModules(){
                               return localModules;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Modules
                               */
                               public void setModules(java.lang.Object param){
                            localModulesTracker = true;
                                   
                                            this.localModules=param;
                                    

                               }
                            

                        /**
                        * field for ObserversList
                        */

                        
                                    protected java.lang.Object localObserversList ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localObserversListTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getObserversList(){
                               return localObserversList;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ObserversList
                               */
                               public void setObserversList(java.lang.Object param){
                            localObserversListTracker = true;
                                   
                                            this.localObserversList=param;
                                    

                               }
                            

                        /**
                        * field for OutFaultFlowPhases
                        */

                        
                                    protected java.lang.Object localOutFaultFlowPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOutFaultFlowPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getOutFaultFlowPhases(){
                               return localOutFaultFlowPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OutFaultFlowPhases
                               */
                               public void setOutFaultFlowPhases(java.lang.Object param){
                            localOutFaultFlowPhasesTracker = true;
                                   
                                            this.localOutFaultFlowPhases=param;
                                    

                               }
                            

                        /**
                        * field for OutFaultPhases
                        */

                        
                                    protected java.lang.Object localOutFaultPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOutFaultPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getOutFaultPhases(){
                               return localOutFaultPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OutFaultPhases
                               */
                               public void setOutFaultPhases(java.lang.Object param){
                            localOutFaultPhasesTracker = true;
                                   
                                            this.localOutFaultPhases=param;
                                    

                               }
                            

                        /**
                        * field for OutFlowPhases
                        */

                        
                                    protected java.lang.Object localOutFlowPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOutFlowPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getOutFlowPhases(){
                               return localOutFlowPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OutFlowPhases
                               */
                               public void setOutFlowPhases(java.lang.Object param){
                            localOutFlowPhasesTracker = true;
                                   
                                            this.localOutFlowPhases=param;
                                    

                               }
                            

                        /**
                        * field for PhasesInfo
                        */

                        
                                    protected org.apache.axis2.deployment.util.xsd.PhasesInfo localPhasesInfo ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPhasesInfoTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.deployment.util.xsd.PhasesInfo
                           */
                           public  org.apache.axis2.deployment.util.xsd.PhasesInfo getPhasesInfo(){
                               return localPhasesInfo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PhasesInfo
                               */
                               public void setPhasesInfo(org.apache.axis2.deployment.util.xsd.PhasesInfo param){
                            localPhasesInfoTracker = true;
                                   
                                            this.localPhasesInfo=param;
                                    

                               }
                            

                        /**
                        * field for Repository
                        */

                        
                                    protected authclient.java.net.xsd.URL localRepository ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRepositoryTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.net.xsd.URL
                           */
                           public  authclient.java.net.xsd.URL getRepository(){
                               return localRepository;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Repository
                               */
                               public void setRepository(authclient.java.net.xsd.URL param){
                            localRepositoryTracker = true;
                                   
                                            this.localRepository=param;
                                    

                               }
                            

                        /**
                        * field for SecretResolver
                        */

                        
                                    protected org.wso2.securevault.xsd.SecretResolver localSecretResolver ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSecretResolverTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.wso2.securevault.xsd.SecretResolver
                           */
                           public  org.wso2.securevault.xsd.SecretResolver getSecretResolver(){
                               return localSecretResolver;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SecretResolver
                               */
                               public void setSecretResolver(org.wso2.securevault.xsd.SecretResolver param){
                            localSecretResolverTracker = true;
                                   
                                            this.localSecretResolver=param;
                                    

                               }
                            

                        /**
                        * field for ServiceClassLoader
                        */

                        
                                    protected java.lang.Object localServiceClassLoader ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceClassLoaderTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getServiceClassLoader(){
                               return localServiceClassLoader;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceClassLoader
                               */
                               public void setServiceClassLoader(java.lang.Object param){
                            localServiceClassLoaderTracker = true;
                                   
                                            this.localServiceClassLoader=param;
                                    

                               }
                            

                        /**
                        * field for ServiceGroups
                        */

                        
                                    protected authclient.java.util.xsd.Iterator localServiceGroups ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Iterator
                           */
                           public  authclient.java.util.xsd.Iterator getServiceGroups(){
                               return localServiceGroups;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroups
                               */
                               public void setServiceGroups(authclient.java.util.xsd.Iterator param){
                            localServiceGroupsTracker = true;
                                   
                                            this.localServiceGroups=param;
                                    

                               }
                            

                        /**
                        * field for Services
                        */

                        
                                    protected java.lang.Object localServices ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServicesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getServices(){
                               return localServices;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Services
                               */
                               public void setServices(java.lang.Object param){
                            localServicesTracker = true;
                                   
                                            this.localServices=param;
                                    

                               }
                            

                        /**
                        * field for Start
                        */

                        
                                    protected boolean localStart ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localStartTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getStart(){
                               return localStart;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Start
                               */
                               public void setStart(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localStartTracker =
                                       true;
                                   
                                            this.localStart=param;
                                    

                               }
                            

                        /**
                        * field for SystemClassLoader
                        */

                        
                                    protected java.lang.Object localSystemClassLoader ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSystemClassLoaderTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getSystemClassLoader(){
                               return localSystemClassLoader;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SystemClassLoader
                               */
                               public void setSystemClassLoader(java.lang.Object param){
                            localSystemClassLoaderTracker = true;
                                   
                                            this.localSystemClassLoader=param;
                                    

                               }
                            

                        /**
                        * field for TargetResolverChain
                        */

                        
                                    protected org.apache.axis2.util.xsd.TargetResolver localTargetResolverChain ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTargetResolverChainTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.util.xsd.TargetResolver
                           */
                           public  org.apache.axis2.util.xsd.TargetResolver getTargetResolverChain(){
                               return localTargetResolverChain;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TargetResolverChain
                               */
                               public void setTargetResolverChain(org.apache.axis2.util.xsd.TargetResolver param){
                            localTargetResolverChainTracker = true;
                                   
                                            this.localTargetResolverChain=param;
                                    

                               }
                            

                        /**
                        * field for TransactionConfig
                        */

                        
                                    protected org.apache.axis2.transaction.xsd.TransactionConfiguration localTransactionConfig ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransactionConfigTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.transaction.xsd.TransactionConfiguration
                           */
                           public  org.apache.axis2.transaction.xsd.TransactionConfiguration getTransactionConfig(){
                               return localTransactionConfig;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TransactionConfig
                               */
                               public void setTransactionConfig(org.apache.axis2.transaction.xsd.TransactionConfiguration param){
                            localTransactionConfigTracker = true;
                                   
                                            this.localTransactionConfig=param;
                                    

                               }
                            

                        /**
                        * field for TransactionConfiguration
                        */

                        
                                    protected org.apache.axis2.transaction.xsd.TransactionConfiguration localTransactionConfiguration ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransactionConfigurationTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.transaction.xsd.TransactionConfiguration
                           */
                           public  org.apache.axis2.transaction.xsd.TransactionConfiguration getTransactionConfiguration(){
                               return localTransactionConfiguration;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TransactionConfiguration
                               */
                               public void setTransactionConfiguration(org.apache.axis2.transaction.xsd.TransactionConfiguration param){
                            localTransactionConfigurationTracker = true;
                                   
                                            this.localTransactionConfiguration=param;
                                    

                               }
                            

                        /**
                        * field for TransportsIn
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localTransportsIn ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransportsInTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getTransportsIn(){
                               return localTransportsIn;
                           }

                           
                        


                               
                              /**
                               * validate the array for TransportsIn
                               */
                              protected void validateTransportsIn(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param TransportsIn
                              */
                              public void setTransportsIn(java.lang.String[] param){
                              
                                   validateTransportsIn(param);

                               localTransportsInTracker = true;
                                      
                                      this.localTransportsIn=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addTransportsIn(java.lang.String param){
                                   if (localTransportsIn == null){
                                   localTransportsIn = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localTransportsInTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localTransportsIn);
                               list.add(param);
                               this.localTransportsIn =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for TransportsOut
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localTransportsOut ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransportsOutTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getTransportsOut(){
                               return localTransportsOut;
                           }

                           
                        


                               
                              /**
                               * validate the array for TransportsOut
                               */
                              protected void validateTransportsOut(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param TransportsOut
                              */
                              public void setTransportsOut(java.lang.String[] param){
                              
                                   validateTransportsOut(param);

                               localTransportsOutTracker = true;
                                      
                                      this.localTransportsOut=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addTransportsOut(java.lang.String param){
                                   if (localTransportsOut == null){
                                   localTransportsOut = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localTransportsOutTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localTransportsOut);
                               list.add(param);
                               this.localTransportsOut =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
               org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,parentQName);
               return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
               parentQName,factory,dataSource);
            
       }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               javax.xml.stream.XMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();
                    writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://engine.axis2.apache.org/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":AxisConfiguration",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "AxisConfiguration",
                           xmlWriter);
                   }

               
                   }
                if (localChildFirstClassLoadingTracker){
                                    namespace = "http://engine.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "childFirstClassLoading", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("childFirstClassLoading cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localChildFirstClassLoading));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localClusteringAgentTracker){
                                    if (localClusteringAgent==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "clusteringAgent", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localClusteringAgent.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","clusteringAgent"),
                                        xmlWriter);
                                    }
                                } if (localConfiguratorTracker){
                                    if (localConfigurator==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "configurator", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localConfigurator.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","configurator"),
                                        xmlWriter);
                                    }
                                } if (localFaultyModulesTracker){
                             if (localFaultyModules!=null) {
                                   namespace = "http://engine.axis2.apache.org/xsd";
                                   for (int i = 0;i < localFaultyModules.length;i++){
                                        
                                            if (localFaultyModules[i] != null){
                                        
                                                writeStartElement(null, namespace, "faultyModules", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyModules[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://engine.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "faultyModules", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "faultyModules", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localFaultyServicesTracker){
                             if (localFaultyServices!=null) {
                                   namespace = "http://engine.axis2.apache.org/xsd";
                                   for (int i = 0;i < localFaultyServices.length;i++){
                                        
                                            if (localFaultyServices[i] != null){
                                        
                                                writeStartElement(null, namespace, "faultyServices", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyServices[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://engine.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "faultyServices", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "faultyServices", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localFaultyServicesDuetoModulesTracker){
                                    if (localFaultyServicesDuetoModules==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "faultyServicesDuetoModules", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localFaultyServicesDuetoModules.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","faultyServicesDuetoModules"),
                                        xmlWriter);
                                    }
                                } if (localGlobalModulesTracker){
                            
                            if (localGlobalModules!=null){
                                if (localGlobalModules instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localGlobalModules).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","globalModules"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalModules", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localGlobalModules, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalModules", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localGlobalOutPhaseTracker){
                            
                            if (localGlobalOutPhase!=null){
                                if (localGlobalOutPhase instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localGlobalOutPhase).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","globalOutPhase"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalOutPhase", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localGlobalOutPhase, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalOutPhase", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localInFaultFlowPhasesTracker){
                            
                            if (localInFaultFlowPhases!=null){
                                if (localInFaultFlowPhases instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localInFaultFlowPhases).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inFaultFlowPhases"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultFlowPhases", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localInFaultFlowPhases, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultFlowPhases", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localInFaultPhasesTracker){
                            
                            if (localInFaultPhases!=null){
                                if (localInFaultPhases instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localInFaultPhases).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inFaultPhases"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultPhases", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localInFaultPhases, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultPhases", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localInFlowPhasesTracker){
                            
                            if (localInFlowPhases!=null){
                                if (localInFlowPhases instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localInFlowPhases).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inFlowPhases"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFlowPhases", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localInFlowPhases, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFlowPhases", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localInPhasesUptoAndIncludingPostDispatchTracker){
                            
                            if (localInPhasesUptoAndIncludingPostDispatch!=null){
                                if (localInPhasesUptoAndIncludingPostDispatch instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localInPhasesUptoAndIncludingPostDispatch).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inPhasesUptoAndIncludingPostDispatch"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inPhasesUptoAndIncludingPostDispatch", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localInPhasesUptoAndIncludingPostDispatch, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inPhasesUptoAndIncludingPostDispatch", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localKeyTracker){
                            
                            if (localKey!=null){
                                if (localKey instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localKey).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","key"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "key", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localKey, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "key", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localLocalPolicyAssertionsTracker){
                            
                            if (localLocalPolicyAssertions!=null){
                                 for (int i = 0;i < localLocalPolicyAssertions.length;i++){
                                    if (localLocalPolicyAssertions[i] != null){

                                           if (localLocalPolicyAssertions[i] instanceof org.apache.axis2.databinding.ADBBean){
                                                ((org.apache.axis2.databinding.ADBBean)localLocalPolicyAssertions[i]).serialize(
                                                           new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","localPolicyAssertions"),
                                                           xmlWriter,true);
                                            } else {
                                                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "localPolicyAssertions", xmlWriter);
                                                org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localLocalPolicyAssertions[i], xmlWriter);
                                                xmlWriter.writeEndElement();
                                             }

                                    } else {
                                       
                                            // write null attribute
                                            writeStartElement(null, "http://engine.axis2.apache.org/xsd", "localPolicyAssertions", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                        
                                    }
                                 }
                            } else {
                                 
                                        // write null attribute
                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "localPolicyAssertions", xmlWriter);

                                       // write the nil attribute
                                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                       xmlWriter.writeEndElement();
                                    
                            }

                        } if (localModuleClassLoaderTracker){
                            
                            if (localModuleClassLoader!=null){
                                if (localModuleClassLoader instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localModuleClassLoader).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","moduleClassLoader"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "moduleClassLoader", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localModuleClassLoader, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "moduleClassLoader", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localModulesTracker){
                            
                            if (localModules!=null){
                                if (localModules instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localModules).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","modules"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "modules", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localModules, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "modules", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localObserversListTracker){
                            
                            if (localObserversList!=null){
                                if (localObserversList instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localObserversList).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","observersList"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "observersList", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localObserversList, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "observersList", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localOutFaultFlowPhasesTracker){
                            
                            if (localOutFaultFlowPhases!=null){
                                if (localOutFaultFlowPhases instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localOutFaultFlowPhases).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","outFaultFlowPhases"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultFlowPhases", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localOutFaultFlowPhases, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultFlowPhases", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localOutFaultPhasesTracker){
                            
                            if (localOutFaultPhases!=null){
                                if (localOutFaultPhases instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localOutFaultPhases).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","outFaultPhases"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultPhases", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localOutFaultPhases, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultPhases", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localOutFlowPhasesTracker){
                            
                            if (localOutFlowPhases!=null){
                                if (localOutFlowPhases instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localOutFlowPhases).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","outFlowPhases"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFlowPhases", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localOutFlowPhases, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFlowPhases", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localPhasesInfoTracker){
                                    if (localPhasesInfo==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "phasesInfo", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localPhasesInfo.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","phasesInfo"),
                                        xmlWriter);
                                    }
                                } if (localRepositoryTracker){
                                    if (localRepository==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "repository", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localRepository.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","repository"),
                                        xmlWriter);
                                    }
                                } if (localSecretResolverTracker){
                                    if (localSecretResolver==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "secretResolver", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localSecretResolver.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","secretResolver"),
                                        xmlWriter);
                                    }
                                } if (localServiceClassLoaderTracker){
                            
                            if (localServiceClassLoader!=null){
                                if (localServiceClassLoader instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localServiceClassLoader).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","serviceClassLoader"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "serviceClassLoader", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localServiceClassLoader, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "serviceClassLoader", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localServiceGroupsTracker){
                                    if (localServiceGroups==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "serviceGroups", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localServiceGroups.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","serviceGroups"),
                                        xmlWriter);
                                    }
                                } if (localServicesTracker){
                            
                            if (localServices!=null){
                                if (localServices instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localServices).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","services"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "services", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localServices, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "services", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localStartTracker){
                                    namespace = "http://engine.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "start", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("start cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localStart));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSystemClassLoaderTracker){
                            
                            if (localSystemClassLoader!=null){
                                if (localSystemClassLoader instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localSystemClassLoader).serialize(
                                               new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","systemClassLoader"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "systemClassLoader", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localSystemClassLoader, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "systemClassLoader", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localTargetResolverChainTracker){
                                    if (localTargetResolverChain==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "targetResolverChain", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTargetResolverChain.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","targetResolverChain"),
                                        xmlWriter);
                                    }
                                } if (localTransactionConfigTracker){
                                    if (localTransactionConfig==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transactionConfig", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTransactionConfig.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transactionConfig"),
                                        xmlWriter);
                                    }
                                } if (localTransactionConfigurationTracker){
                                    if (localTransactionConfiguration==null){

                                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transactionConfiguration", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTransactionConfiguration.serialize(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transactionConfiguration"),
                                        xmlWriter);
                                    }
                                } if (localTransportsInTracker){
                             if (localTransportsIn!=null) {
                                   namespace = "http://engine.axis2.apache.org/xsd";
                                   for (int i = 0;i < localTransportsIn.length;i++){
                                        
                                            if (localTransportsIn[i] != null){
                                        
                                                writeStartElement(null, namespace, "transportsIn", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTransportsIn[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://engine.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "transportsIn", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transportsIn", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localTransportsOutTracker){
                             if (localTransportsOut!=null) {
                                   namespace = "http://engine.axis2.apache.org/xsd";
                                   for (int i = 0;i < localTransportsOut.length;i++){
                                        
                                            if (localTransportsOut[i] != null){
                                        
                                                writeStartElement(null, namespace, "transportsOut", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTransportsOut[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://engine.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "transportsOut", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transportsOut", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://engine.axis2.apache.org/xsd")){
                return "ns8";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }
        
        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName,attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace,attName,attValue);
            }
        }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localChildFirstClassLoadingTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "childFirstClassLoading"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localChildFirstClassLoading));
                            } if (localClusteringAgentTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "clusteringAgent"));
                            
                            
                                    elementList.add(localClusteringAgent==null?null:
                                    localClusteringAgent);
                                } if (localConfiguratorTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "configurator"));
                            
                            
                                    elementList.add(localConfigurator==null?null:
                                    localConfigurator);
                                } if (localFaultyModulesTracker){
                            if (localFaultyModules!=null){
                                  for (int i = 0;i < localFaultyModules.length;i++){
                                      
                                         if (localFaultyModules[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "faultyModules"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyModules[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "faultyModules"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "faultyModules"));
                                    elementList.add(null);
                                
                            }

                        } if (localFaultyServicesTracker){
                            if (localFaultyServices!=null){
                                  for (int i = 0;i < localFaultyServices.length;i++){
                                      
                                         if (localFaultyServices[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "faultyServices"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyServices[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "faultyServices"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "faultyServices"));
                                    elementList.add(null);
                                
                            }

                        } if (localFaultyServicesDuetoModulesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "faultyServicesDuetoModules"));
                            
                            
                                    elementList.add(localFaultyServicesDuetoModules==null?null:
                                    localFaultyServicesDuetoModules);
                                } if (localGlobalModulesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "globalModules"));
                            
                            
                                    elementList.add(localGlobalModules==null?null:
                                    localGlobalModules);
                                } if (localGlobalOutPhaseTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "globalOutPhase"));
                            
                            
                                    elementList.add(localGlobalOutPhase==null?null:
                                    localGlobalOutPhase);
                                } if (localInFaultFlowPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "inFaultFlowPhases"));
                            
                            
                                    elementList.add(localInFaultFlowPhases==null?null:
                                    localInFaultFlowPhases);
                                } if (localInFaultPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "inFaultPhases"));
                            
                            
                                    elementList.add(localInFaultPhases==null?null:
                                    localInFaultPhases);
                                } if (localInFlowPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "inFlowPhases"));
                            
                            
                                    elementList.add(localInFlowPhases==null?null:
                                    localInFlowPhases);
                                } if (localInPhasesUptoAndIncludingPostDispatchTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "inPhasesUptoAndIncludingPostDispatch"));
                            
                            
                                    elementList.add(localInPhasesUptoAndIncludingPostDispatch==null?null:
                                    localInPhasesUptoAndIncludingPostDispatch);
                                } if (localKeyTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "key"));
                            
                            
                                    elementList.add(localKey==null?null:
                                    localKey);
                                } if (localLocalPolicyAssertionsTracker){
                             if (localLocalPolicyAssertions!=null) {
                                 for (int i = 0;i < localLocalPolicyAssertions.length;i++){

                                    if (localLocalPolicyAssertions[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                          "localPolicyAssertions"));
                                         elementList.add(localLocalPolicyAssertions[i]);
                                    } else {
                                        
                                                elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                          "localPolicyAssertions"));
                                                elementList.add(null);
                                            
                                    }

                                 }
                             } else {
                                 
                                        elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                          "localPolicyAssertions"));
                                        elementList.add(localLocalPolicyAssertions);
                                    
                             }

                        } if (localModuleClassLoaderTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "moduleClassLoader"));
                            
                            
                                    elementList.add(localModuleClassLoader==null?null:
                                    localModuleClassLoader);
                                } if (localModulesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "modules"));
                            
                            
                                    elementList.add(localModules==null?null:
                                    localModules);
                                } if (localObserversListTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "observersList"));
                            
                            
                                    elementList.add(localObserversList==null?null:
                                    localObserversList);
                                } if (localOutFaultFlowPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "outFaultFlowPhases"));
                            
                            
                                    elementList.add(localOutFaultFlowPhases==null?null:
                                    localOutFaultFlowPhases);
                                } if (localOutFaultPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "outFaultPhases"));
                            
                            
                                    elementList.add(localOutFaultPhases==null?null:
                                    localOutFaultPhases);
                                } if (localOutFlowPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "outFlowPhases"));
                            
                            
                                    elementList.add(localOutFlowPhases==null?null:
                                    localOutFlowPhases);
                                } if (localPhasesInfoTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "phasesInfo"));
                            
                            
                                    elementList.add(localPhasesInfo==null?null:
                                    localPhasesInfo);
                                } if (localRepositoryTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "repository"));
                            
                            
                                    elementList.add(localRepository==null?null:
                                    localRepository);
                                } if (localSecretResolverTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "secretResolver"));
                            
                            
                                    elementList.add(localSecretResolver==null?null:
                                    localSecretResolver);
                                } if (localServiceClassLoaderTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "serviceClassLoader"));
                            
                            
                                    elementList.add(localServiceClassLoader==null?null:
                                    localServiceClassLoader);
                                } if (localServiceGroupsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "serviceGroups"));
                            
                            
                                    elementList.add(localServiceGroups==null?null:
                                    localServiceGroups);
                                } if (localServicesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "services"));
                            
                            
                                    elementList.add(localServices==null?null:
                                    localServices);
                                } if (localStartTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "start"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localStart));
                            } if (localSystemClassLoaderTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "systemClassLoader"));
                            
                            
                                    elementList.add(localSystemClassLoader==null?null:
                                    localSystemClassLoader);
                                } if (localTargetResolverChainTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "targetResolverChain"));
                            
                            
                                    elementList.add(localTargetResolverChain==null?null:
                                    localTargetResolverChain);
                                } if (localTransactionConfigTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "transactionConfig"));
                            
                            
                                    elementList.add(localTransactionConfig==null?null:
                                    localTransactionConfig);
                                } if (localTransactionConfigurationTracker){
                            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                      "transactionConfiguration"));
                            
                            
                                    elementList.add(localTransactionConfiguration==null?null:
                                    localTransactionConfiguration);
                                } if (localTransportsInTracker){
                            if (localTransportsIn!=null){
                                  for (int i = 0;i < localTransportsIn.length;i++){
                                      
                                         if (localTransportsIn[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "transportsIn"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTransportsIn[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "transportsIn"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "transportsIn"));
                                    elementList.add(null);
                                
                            }

                        } if (localTransportsOutTracker){
                            if (localTransportsOut!=null){
                                  for (int i = 0;i < localTransportsOut.length;i++){
                                      
                                         if (localTransportsOut[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "transportsOut"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTransportsOut[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "transportsOut"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                                                              "transportsOut"));
                                    elementList.add(null);
                                
                            }

                        }

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static AxisConfiguration parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            AxisConfiguration object =
                new AxisConfiguration();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"AxisConfiguration".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (AxisConfiguration)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list4 = new java.util.ArrayList();
                    
                        java.util.ArrayList list5 = new java.util.ArrayList();
                    
                        java.util.ArrayList list14 = new java.util.ArrayList();
                    
                        java.util.ArrayList list32 = new java.util.ArrayList();
                    
                        java.util.ArrayList list33 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","childFirstClassLoading").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setChildFirstClassLoading(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","clusteringAgent").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setClusteringAgent(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setClusteringAgent(org.apache.axis2.clustering.xsd.ClusteringAgent.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","configurator").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setConfigurator(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setConfigurator(org.apache.axis2.engine.xsd.AxisConfigurator.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","faultyModules").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list4.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list4.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone4 = false;
                                            while(!loopDone4){
                                                // Ensure we are at the EndElement
                                                while (!reader.isEndElement()){
                                                    reader.next();
                                                }
                                                // Step out of this element
                                                reader.next();
                                                // Step to next element event.
                                                while (!reader.isStartElement() && !reader.isEndElement())
                                                    reader.next();
                                                if (reader.isEndElement()){
                                                    //two continuous end elements means we are exiting the xml structure
                                                    loopDone4 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","faultyModules").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list4.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list4.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone4 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setFaultyModules((java.lang.String[])
                                                        list4.toArray(new java.lang.String[list4.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","faultyServices").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list5.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list5.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone5 = false;
                                            while(!loopDone5){
                                                // Ensure we are at the EndElement
                                                while (!reader.isEndElement()){
                                                    reader.next();
                                                }
                                                // Step out of this element
                                                reader.next();
                                                // Step to next element event.
                                                while (!reader.isStartElement() && !reader.isEndElement())
                                                    reader.next();
                                                if (reader.isEndElement()){
                                                    //two continuous end elements means we are exiting the xml structure
                                                    loopDone5 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","faultyServices").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list5.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list5.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone5 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setFaultyServices((java.lang.String[])
                                                        list5.toArray(new java.lang.String[list5.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","faultyServicesDuetoModules").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setFaultyServicesDuetoModules(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setFaultyServicesDuetoModules(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","globalModules").equals(reader.getName())){
                                
                                     object.setGlobalModules(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","globalOutPhase").equals(reader.getName())){
                                
                                     object.setGlobalOutPhase(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inFaultFlowPhases").equals(reader.getName())){
                                
                                     object.setInFaultFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inFaultPhases").equals(reader.getName())){
                                
                                     object.setInFaultPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inFlowPhases").equals(reader.getName())){
                                
                                     object.setInFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","inPhasesUptoAndIncludingPostDispatch").equals(reader.getName())){
                                
                                     object.setInPhasesUptoAndIncludingPostDispatch(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","key").equals(reader.getName())){
                                
                                     object.setKey(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","localPolicyAssertions").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    

                                             boolean loopDone14=false;
                                             javax.xml.namespace.QName startQname14 = new javax.xml.namespace.QName(
                                                    "http://engine.axis2.apache.org/xsd",
                                                    "localPolicyAssertions");

                                             while (!loopDone14){
                                                 event = reader.getEventType();
                                                 if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                                                         && startQname14.equals(reader.getName())){

                                                      
                                                      
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list14.add(null);
                                                              reader.next();
                                                          }else{
                                                      list14.add(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                            org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                                       }
                                                 } else if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event &&
                                                            !startQname14.equals(reader.getName())){
                                                     loopDone14 = true;
                                                 }else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event &&
                                                           !startQname14.equals(reader.getName())){
                                                     loopDone14 = true;
                                                 }else if (javax.xml.stream.XMLStreamConstants.END_DOCUMENT == event){
                                                     loopDone14 = true;
                                                 }else{
                                                     reader.next();
                                                 }

                                             }

                                            
                                                    object.setLocalPolicyAssertions(list14.toArray());
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","moduleClassLoader").equals(reader.getName())){
                                
                                     object.setModuleClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","modules").equals(reader.getName())){
                                
                                     object.setModules(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","observersList").equals(reader.getName())){
                                
                                     object.setObserversList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","outFaultFlowPhases").equals(reader.getName())){
                                
                                     object.setOutFaultFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","outFaultPhases").equals(reader.getName())){
                                
                                     object.setOutFaultPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","outFlowPhases").equals(reader.getName())){
                                
                                     object.setOutFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","phasesInfo").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setPhasesInfo(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setPhasesInfo(org.apache.axis2.deployment.util.xsd.PhasesInfo.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","repository").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setRepository(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setRepository(authclient.java.net.xsd.URL.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","secretResolver").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setSecretResolver(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setSecretResolver(org.wso2.securevault.xsd.SecretResolver.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","serviceClassLoader").equals(reader.getName())){
                                
                                     object.setServiceClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","serviceGroups").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setServiceGroups(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setServiceGroups(authclient.java.util.xsd.Iterator.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","services").equals(reader.getName())){
                                
                                     object.setServices(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","start").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setStart(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","systemClassLoader").equals(reader.getName())){
                                
                                     object.setSystemClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","targetResolverChain").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTargetResolverChain(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTargetResolverChain(org.apache.axis2.util.xsd.TargetResolver.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transactionConfig").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTransactionConfig(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTransactionConfig(org.apache.axis2.transaction.xsd.TransactionConfiguration.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transactionConfiguration").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTransactionConfiguration(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTransactionConfiguration(org.apache.axis2.transaction.xsd.TransactionConfiguration.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transportsIn").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list32.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list32.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone32 = false;
                                            while(!loopDone32){
                                                // Ensure we are at the EndElement
                                                while (!reader.isEndElement()){
                                                    reader.next();
                                                }
                                                // Step out of this element
                                                reader.next();
                                                // Step to next element event.
                                                while (!reader.isStartElement() && !reader.isEndElement())
                                                    reader.next();
                                                if (reader.isEndElement()){
                                                    //two continuous end elements means we are exiting the xml structure
                                                    loopDone32 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transportsIn").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list32.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list32.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone32 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setTransportsIn((java.lang.String[])
                                                        list32.toArray(new java.lang.String[list32.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transportsOut").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list33.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list33.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone33 = false;
                                            while(!loopDone33){
                                                // Ensure we are at the EndElement
                                                while (!reader.isEndElement()){
                                                    reader.next();
                                                }
                                                // Step out of this element
                                                reader.next();
                                                // Step to next element event.
                                                while (!reader.isStartElement() && !reader.isEndElement())
                                                    reader.next();
                                                if (reader.isEndElement()){
                                                    //two continuous end elements means we are exiting the xml structure
                                                    loopDone33 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd","transportsOut").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list33.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list33.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone33 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setTransportsOut((java.lang.String[])
                                                        list33.toArray(new java.lang.String[list33.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
    