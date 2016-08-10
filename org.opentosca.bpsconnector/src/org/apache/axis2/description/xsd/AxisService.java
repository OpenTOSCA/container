
/**
 * AxisService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.description.xsd;
            

            /**
            *  AxisService bean class
            */
        
        public  class AxisService
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = AxisService
                Namespace URI = http://description.axis2.apache.org/xsd
                Namespace Prefix = ns19
                */
            

                        /**
                        * field for EPRs
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localEPRs ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEPRsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getEPRs(){
                               return localEPRs;
                           }

                           
                        


                               
                              /**
                               * validate the array for EPRs
                               */
                              protected void validateEPRs(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param EPRs
                              */
                              public void setEPRs(java.lang.String[] param){
                              
                                   validateEPRs(param);

                               localEPRsTracker = true;
                                      
                                      this.localEPRs=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addEPRs(java.lang.String param){
                                   if (localEPRs == null){
                                   localEPRs = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localEPRsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localEPRs);
                               list.add(param);
                               this.localEPRs =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for WSAddressingFlag
                        */

                        
                                    protected java.lang.String localWSAddressingFlag ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWSAddressingFlagTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getWSAddressingFlag(){
                               return localWSAddressingFlag;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param WSAddressingFlag
                               */
                               public void setWSAddressingFlag(java.lang.String param){
                            localWSAddressingFlagTracker = true;
                                   
                                            this.localWSAddressingFlag=param;
                                    

                               }
                            

                        /**
                        * field for Active
                        */

                        
                                    protected boolean localActive ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localActiveTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getActive(){
                               return localActive;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Active
                               */
                               public void setActive(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localActiveTracker =
                                       true;
                                   
                                            this.localActive=param;
                                    

                               }
                            

                        /**
                        * field for AxisServiceGroup
                        */

                        
                                    protected org.apache.axis2.description.xsd.AxisServiceGroup localAxisServiceGroup ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAxisServiceGroupTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.AxisServiceGroup
                           */
                           public  org.apache.axis2.description.xsd.AxisServiceGroup getAxisServiceGroup(){
                               return localAxisServiceGroup;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AxisServiceGroup
                               */
                               public void setAxisServiceGroup(org.apache.axis2.description.xsd.AxisServiceGroup param){
                            localAxisServiceGroupTracker = true;
                                   
                                            this.localAxisServiceGroup=param;
                                    

                               }
                            

                        /**
                        * field for BindingName
                        */

                        
                                    protected java.lang.String localBindingName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localBindingNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getBindingName(){
                               return localBindingName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param BindingName
                               */
                               public void setBindingName(java.lang.String param){
                            localBindingNameTracker = true;
                                   
                                            this.localBindingName=param;
                                    

                               }
                            

                        /**
                        * field for ClassLoader
                        */

                        
                                    protected java.lang.Object localClassLoader ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localClassLoaderTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getClassLoader(){
                               return localClassLoader;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ClassLoader
                               */
                               public void setClassLoader(java.lang.Object param){
                            localClassLoaderTracker = true;
                                   
                                            this.localClassLoader=param;
                                    

                               }
                            

                        /**
                        * field for ClientSide
                        */

                        
                                    protected boolean localClientSide ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localClientSideTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getClientSide(){
                               return localClientSide;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ClientSide
                               */
                               public void setClientSide(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localClientSideTracker =
                                       true;
                                   
                                            this.localClientSide=param;
                                    

                               }
                            

                        /**
                        * field for ControlOperations
                        */

                        
                                    protected java.lang.Object localControlOperations ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localControlOperationsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getControlOperations(){
                               return localControlOperations;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ControlOperations
                               */
                               public void setControlOperations(java.lang.Object param){
                            localControlOperationsTracker = true;
                                   
                                            this.localControlOperations=param;
                                    

                               }
                            

                        /**
                        * field for CustomSchemaNamePrefix
                        */

                        
                                    protected java.lang.String localCustomSchemaNamePrefix ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCustomSchemaNamePrefixTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getCustomSchemaNamePrefix(){
                               return localCustomSchemaNamePrefix;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CustomSchemaNamePrefix
                               */
                               public void setCustomSchemaNamePrefix(java.lang.String param){
                            localCustomSchemaNamePrefixTracker = true;
                                   
                                            this.localCustomSchemaNamePrefix=param;
                                    

                               }
                            

                        /**
                        * field for CustomSchemaNameSuffix
                        */

                        
                                    protected java.lang.String localCustomSchemaNameSuffix ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCustomSchemaNameSuffixTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getCustomSchemaNameSuffix(){
                               return localCustomSchemaNameSuffix;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CustomSchemaNameSuffix
                               */
                               public void setCustomSchemaNameSuffix(java.lang.String param){
                            localCustomSchemaNameSuffixTracker = true;
                                   
                                            this.localCustomSchemaNameSuffix=param;
                                    

                               }
                            

                        /**
                        * field for CustomWsdl
                        */

                        
                                    protected boolean localCustomWsdl ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCustomWsdlTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getCustomWsdl(){
                               return localCustomWsdl;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CustomWsdl
                               */
                               public void setCustomWsdl(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localCustomWsdlTracker =
                                       true;
                                   
                                            this.localCustomWsdl=param;
                                    

                               }
                            

                        /**
                        * field for ElementFormDefault
                        */

                        
                                    protected boolean localElementFormDefault ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localElementFormDefaultTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getElementFormDefault(){
                               return localElementFormDefault;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ElementFormDefault
                               */
                               public void setElementFormDefault(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localElementFormDefaultTracker =
                                       true;
                                   
                                            this.localElementFormDefault=param;
                                    

                               }
                            

                        /**
                        * field for EnableAllTransports
                        */

                        
                                    protected boolean localEnableAllTransports ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEnableAllTransportsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getEnableAllTransports(){
                               return localEnableAllTransports;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EnableAllTransports
                               */
                               public void setEnableAllTransports(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localEnableAllTransportsTracker =
                                       true;
                                   
                                            this.localEnableAllTransports=param;
                                    

                               }
                            

                        /**
                        * field for EndpointName
                        */

                        
                                    protected java.lang.String localEndpointName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEndpointNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getEndpointName(){
                               return localEndpointName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EndpointName
                               */
                               public void setEndpointName(java.lang.String param){
                            localEndpointNameTracker = true;
                                   
                                            this.localEndpointName=param;
                                    

                               }
                            

                        /**
                        * field for EndpointURL
                        */

                        
                                    protected java.lang.String localEndpointURL ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEndpointURLTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getEndpointURL(){
                               return localEndpointURL;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EndpointURL
                               */
                               public void setEndpointURL(java.lang.String param){
                            localEndpointURLTracker = true;
                                   
                                            this.localEndpointURL=param;
                                    

                               }
                            

                        /**
                        * field for Endpoints
                        */

                        
                                    protected authclient.java.util.xsd.Map localEndpoints ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEndpointsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getEndpoints(){
                               return localEndpoints;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Endpoints
                               */
                               public void setEndpoints(authclient.java.util.xsd.Map param){
                            localEndpointsTracker = true;
                                   
                                            this.localEndpoints=param;
                                    

                               }
                            

                        /**
                        * field for ExcludeInfo
                        */

                        
                                    protected org.apache.axis2.deployment.util.xsd.ExcludeInfo localExcludeInfo ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExcludeInfoTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.deployment.util.xsd.ExcludeInfo
                           */
                           public  org.apache.axis2.deployment.util.xsd.ExcludeInfo getExcludeInfo(){
                               return localExcludeInfo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ExcludeInfo
                               */
                               public void setExcludeInfo(org.apache.axis2.deployment.util.xsd.ExcludeInfo param){
                            localExcludeInfoTracker = true;
                                   
                                            this.localExcludeInfo=param;
                                    

                               }
                            

                        /**
                        * field for ExposedTransports
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localExposedTransports ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExposedTransportsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getExposedTransports(){
                               return localExposedTransports;
                           }

                           
                        


                               
                              /**
                               * validate the array for ExposedTransports
                               */
                              protected void validateExposedTransports(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ExposedTransports
                              */
                              public void setExposedTransports(java.lang.String[] param){
                              
                                   validateExposedTransports(param);

                               localExposedTransportsTracker = true;
                                      
                                      this.localExposedTransports=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addExposedTransports(java.lang.String param){
                                   if (localExposedTransports == null){
                                   localExposedTransports = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localExposedTransportsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localExposedTransports);
                               list.add(param);
                               this.localExposedTransports =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for FileName
                        */

                        
                                    protected authclient.java.net.xsd.URL localFileName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFileNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.net.xsd.URL
                           */
                           public  authclient.java.net.xsd.URL getFileName(){
                               return localFileName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FileName
                               */
                               public void setFileName(authclient.java.net.xsd.URL param){
                            localFileNameTracker = true;
                                   
                                            this.localFileName=param;
                                    

                               }
                            

                        /**
                        * field for ImportedNamespaces
                        */

                        
                                    protected java.lang.Object localImportedNamespaces ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localImportedNamespacesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getImportedNamespaces(){
                               return localImportedNamespaces;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ImportedNamespaces
                               */
                               public void setImportedNamespaces(java.lang.Object param){
                            localImportedNamespacesTracker = true;
                                   
                                            this.localImportedNamespaces=param;
                                    

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
                        * field for LastUpdate
                        */

                        
                                    protected long localLastUpdate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLastUpdateTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getLastUpdate(){
                               return localLastUpdate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param LastUpdate
                               */
                               public void setLastUpdate(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localLastUpdateTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localLastUpdate=param;
                                    

                               }
                            

                        /**
                        * field for LastupdateE
                        */

                        
                                    protected long localLastupdateE ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLastupdateETracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getLastupdateE(){
                               return localLastupdateE;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param LastupdateE
                               */
                               public void setLastupdateE(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localLastupdateETracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localLastupdateE=param;
                                    

                               }
                            

                        /**
                        * field for MessageElementQNameToOperationMap
                        * This was an Array!
                        */

                        
                                    protected java.lang.Object[] localMessageElementQNameToOperationMap ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessageElementQNameToOperationMapTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object[]
                           */
                           public  java.lang.Object[] getMessageElementQNameToOperationMap(){
                               return localMessageElementQNameToOperationMap;
                           }

                           
                        


                               
                              /**
                               * validate the array for MessageElementQNameToOperationMap
                               */
                              protected void validateMessageElementQNameToOperationMap(java.lang.Object[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param MessageElementQNameToOperationMap
                              */
                              public void setMessageElementQNameToOperationMap(java.lang.Object[] param){
                              
                                   validateMessageElementQNameToOperationMap(param);

                               localMessageElementQNameToOperationMapTracker = true;
                                      
                                      this.localMessageElementQNameToOperationMap=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.Object
                             */
                             public void addMessageElementQNameToOperationMap(java.lang.Object param){
                                   if (localMessageElementQNameToOperationMap == null){
                                   localMessageElementQNameToOperationMap = new java.lang.Object[]{};
                                   }

                            
                                 //update the setting tracker
                                localMessageElementQNameToOperationMapTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localMessageElementQNameToOperationMap);
                               list.add(param);
                               this.localMessageElementQNameToOperationMap =
                             (java.lang.Object[])list.toArray(
                            new java.lang.Object[list.size()]);

                             }
                             

                        /**
                        * field for ModifyUserWSDLPortAddress
                        */

                        
                                    protected boolean localModifyUserWSDLPortAddress ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModifyUserWSDLPortAddressTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getModifyUserWSDLPortAddress(){
                               return localModifyUserWSDLPortAddress;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifyUserWSDLPortAddress
                               */
                               public void setModifyUserWSDLPortAddress(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localModifyUserWSDLPortAddressTracker =
                                       true;
                                   
                                            this.localModifyUserWSDLPortAddress=param;
                                    

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
                        * field for Name
                        */

                        
                                    protected java.lang.String localName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getName(){
                               return localName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Name
                               */
                               public void setName(java.lang.String param){
                            localNameTracker = true;
                                   
                                            this.localName=param;
                                    

                               }
                            

                        /**
                        * field for NameSpacesMap
                        */

                        
                                    protected authclient.java.util.xsd.Map localNameSpacesMap ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNameSpacesMapTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getNameSpacesMap(){
                               return localNameSpacesMap;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NameSpacesMap
                               */
                               public void setNameSpacesMap(authclient.java.util.xsd.Map param){
                            localNameSpacesMapTracker = true;
                                   
                                            this.localNameSpacesMap=param;
                                    

                               }
                            

                        /**
                        * field for NamespaceMap
                        */

                        
                                    protected authclient.java.util.xsd.Map localNamespaceMap ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNamespaceMapTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getNamespaceMap(){
                               return localNamespaceMap;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NamespaceMap
                               */
                               public void setNamespaceMap(authclient.java.util.xsd.Map param){
                            localNamespaceMapTracker = true;
                                   
                                            this.localNamespaceMap=param;
                                    

                               }
                            

                        /**
                        * field for ObjectSupplier
                        */

                        
                                    protected org.apache.axis2.engine.xsd.ObjectSupplier localObjectSupplier ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localObjectSupplierTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.ObjectSupplier
                           */
                           public  org.apache.axis2.engine.xsd.ObjectSupplier getObjectSupplier(){
                               return localObjectSupplier;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ObjectSupplier
                               */
                               public void setObjectSupplier(org.apache.axis2.engine.xsd.ObjectSupplier param){
                            localObjectSupplierTracker = true;
                                   
                                            this.localObjectSupplier=param;
                                    

                               }
                            

                        /**
                        * field for Operations
                        */

                        
                                    protected authclient.java.util.xsd.Iterator localOperations ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOperationsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Iterator
                           */
                           public  authclient.java.util.xsd.Iterator getOperations(){
                               return localOperations;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Operations
                               */
                               public void setOperations(authclient.java.util.xsd.Iterator param){
                            localOperationsTracker = true;
                                   
                                            this.localOperations=param;
                                    

                               }
                            

                        /**
                        * field for OperationsNameList
                        */

                        
                                    protected java.lang.Object localOperationsNameList ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOperationsNameListTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getOperationsNameList(){
                               return localOperationsNameList;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OperationsNameList
                               */
                               public void setOperationsNameList(java.lang.Object param){
                            localOperationsNameListTracker = true;
                                   
                                            this.localOperationsNameList=param;
                                    

                               }
                            

                        /**
                        * field for P2NMap
                        */

                        
                                    protected authclient.java.util.xsd.Map localP2NMap ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localP2NMapTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getP2NMap(){
                               return localP2NMap;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param P2NMap
                               */
                               public void setP2NMap(authclient.java.util.xsd.Map param){
                            localP2NMapTracker = true;
                                   
                                            this.localP2NMap=param;
                                    

                               }
                            

                        /**
                        * field for Parent
                        */

                        
                                    protected org.apache.axis2.description.xsd.AxisServiceGroup localParent ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localParentTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.AxisServiceGroup
                           */
                           public  org.apache.axis2.description.xsd.AxisServiceGroup getParent(){
                               return localParent;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Parent
                               */
                               public void setParent(org.apache.axis2.description.xsd.AxisServiceGroup param){
                            localParentTracker = true;
                                   
                                            this.localParent=param;
                                    

                               }
                            

                        /**
                        * field for PortTypeName
                        */

                        
                                    protected java.lang.String localPortTypeName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPortTypeNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getPortTypeName(){
                               return localPortTypeName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PortTypeName
                               */
                               public void setPortTypeName(java.lang.String param){
                            localPortTypeNameTracker = true;
                                   
                                            this.localPortTypeName=param;
                                    

                               }
                            

                        /**
                        * field for PublishedOperations
                        */

                        
                                    protected java.lang.Object localPublishedOperations ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPublishedOperationsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getPublishedOperations(){
                               return localPublishedOperations;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PublishedOperations
                               */
                               public void setPublishedOperations(java.lang.Object param){
                            localPublishedOperationsTracker = true;
                                   
                                            this.localPublishedOperations=param;
                                    

                               }
                            

                        /**
                        * field for SchemaLocationsAdjusted
                        */

                        
                                    protected boolean localSchemaLocationsAdjusted ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchemaLocationsAdjustedTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getSchemaLocationsAdjusted(){
                               return localSchemaLocationsAdjusted;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchemaLocationsAdjusted
                               */
                               public void setSchemaLocationsAdjusted(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localSchemaLocationsAdjustedTracker =
                                       true;
                                   
                                            this.localSchemaLocationsAdjusted=param;
                                    

                               }
                            

                        /**
                        * field for SchemaMappingTable
                        */

                        
                                    protected authclient.java.util.xsd.Map localSchemaMappingTable ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchemaMappingTableTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getSchemaMappingTable(){
                               return localSchemaMappingTable;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchemaMappingTable
                               */
                               public void setSchemaMappingTable(authclient.java.util.xsd.Map param){
                            localSchemaMappingTableTracker = true;
                                   
                                            this.localSchemaMappingTable=param;
                                    

                               }
                            

                        /**
                        * field for SchemaTargetNamespace
                        */

                        
                                    protected java.lang.String localSchemaTargetNamespace ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchemaTargetNamespaceTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSchemaTargetNamespace(){
                               return localSchemaTargetNamespace;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchemaTargetNamespace
                               */
                               public void setSchemaTargetNamespace(java.lang.String param){
                            localSchemaTargetNamespaceTracker = true;
                                   
                                            this.localSchemaTargetNamespace=param;
                                    

                               }
                            

                        /**
                        * field for SchemaTargetNamespacePrefix
                        */

                        
                                    protected java.lang.String localSchemaTargetNamespacePrefix ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchemaTargetNamespacePrefixTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSchemaTargetNamespacePrefix(){
                               return localSchemaTargetNamespacePrefix;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchemaTargetNamespacePrefix
                               */
                               public void setSchemaTargetNamespacePrefix(java.lang.String param){
                            localSchemaTargetNamespacePrefixTracker = true;
                                   
                                            this.localSchemaTargetNamespacePrefix=param;
                                    

                               }
                            

                        /**
                        * field for SchematargetNamespaceE
                        */

                        
                                    protected java.lang.String localSchematargetNamespaceE ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchematargetNamespaceETracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSchematargetNamespaceE(){
                               return localSchematargetNamespaceE;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchematargetNamespaceE
                               */
                               public void setSchematargetNamespaceE(java.lang.String param){
                            localSchematargetNamespaceETracker = true;
                                   
                                            this.localSchematargetNamespaceE=param;
                                    

                               }
                            

                        /**
                        * field for SchematargetNamespacePrefixE
                        */

                        
                                    protected java.lang.String localSchematargetNamespacePrefixE ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchematargetNamespacePrefixETracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSchematargetNamespacePrefixE(){
                               return localSchematargetNamespacePrefixE;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchematargetNamespacePrefixE
                               */
                               public void setSchematargetNamespacePrefixE(java.lang.String param){
                            localSchematargetNamespacePrefixETracker = true;
                                   
                                            this.localSchematargetNamespacePrefixE=param;
                                    

                               }
                            

                        /**
                        * field for Scope
                        */

                        
                                    protected java.lang.String localScope ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localScopeTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getScope(){
                               return localScope;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Scope
                               */
                               public void setScope(java.lang.String param){
                            localScopeTracker = true;
                                   
                                            this.localScope=param;
                                    

                               }
                            

                        /**
                        * field for ServiceDescription
                        */

                        
                                    protected java.lang.String localServiceDescription ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceDescriptionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServiceDescription(){
                               return localServiceDescription;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceDescription
                               */
                               public void setServiceDescription(java.lang.String param){
                            localServiceDescriptionTracker = true;
                                   
                                            this.localServiceDescription=param;
                                    

                               }
                            

                        /**
                        * field for ServiceLifeCycle
                        */

                        
                                    protected org.apache.axis2.engine.xsd.ServiceLifeCycle localServiceLifeCycle ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceLifeCycleTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.ServiceLifeCycle
                           */
                           public  org.apache.axis2.engine.xsd.ServiceLifeCycle getServiceLifeCycle(){
                               return localServiceLifeCycle;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceLifeCycle
                               */
                               public void setServiceLifeCycle(org.apache.axis2.engine.xsd.ServiceLifeCycle param){
                            localServiceLifeCycleTracker = true;
                                   
                                            this.localServiceLifeCycle=param;
                                    

                               }
                            

                        /**
                        * field for SetEndpointsToAllUsedBindings
                        */

                        
                                    protected boolean localSetEndpointsToAllUsedBindings ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSetEndpointsToAllUsedBindingsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getSetEndpointsToAllUsedBindings(){
                               return localSetEndpointsToAllUsedBindings;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SetEndpointsToAllUsedBindings
                               */
                               public void setSetEndpointsToAllUsedBindings(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localSetEndpointsToAllUsedBindingsTracker =
                                       true;
                                   
                                            this.localSetEndpointsToAllUsedBindings=param;
                                    

                               }
                            

                        /**
                        * field for SoapNsUri
                        */

                        
                                    protected java.lang.String localSoapNsUri ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSoapNsUriTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSoapNsUri(){
                               return localSoapNsUri;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SoapNsUri
                               */
                               public void setSoapNsUri(java.lang.String param){
                            localSoapNsUriTracker = true;
                                   
                                            this.localSoapNsUri=param;
                                    

                               }
                            

                        /**
                        * field for TargetNamespace
                        */

                        
                                    protected java.lang.String localTargetNamespace ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTargetNamespaceTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getTargetNamespace(){
                               return localTargetNamespace;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TargetNamespace
                               */
                               public void setTargetNamespace(java.lang.String param){
                            localTargetNamespaceTracker = true;
                                   
                                            this.localTargetNamespace=param;
                                    

                               }
                            

                        /**
                        * field for TargetNamespacePrefix
                        */

                        
                                    protected java.lang.String localTargetNamespacePrefix ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTargetNamespacePrefixTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getTargetNamespacePrefix(){
                               return localTargetNamespacePrefix;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TargetNamespacePrefix
                               */
                               public void setTargetNamespacePrefix(java.lang.String param){
                            localTargetNamespacePrefixTracker = true;
                                   
                                            this.localTargetNamespacePrefix=param;
                                    

                               }
                            

                        /**
                        * field for TypeTable
                        */

                        
                                    protected org.apache.axis2.description.java2wsdl.xsd.TypeTable localTypeTable ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTypeTableTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.java2wsdl.xsd.TypeTable
                           */
                           public  org.apache.axis2.description.java2wsdl.xsd.TypeTable getTypeTable(){
                               return localTypeTable;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TypeTable
                               */
                               public void setTypeTable(org.apache.axis2.description.java2wsdl.xsd.TypeTable param){
                            localTypeTableTracker = true;
                                   
                                            this.localTypeTable=param;
                                    

                               }
                            

                        /**
                        * field for UseDefaultChains
                        */

                        
                                    protected boolean localUseDefaultChains ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localUseDefaultChainsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getUseDefaultChains(){
                               return localUseDefaultChains;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param UseDefaultChains
                               */
                               public void setUseDefaultChains(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localUseDefaultChainsTracker =
                                       true;
                                   
                                            this.localUseDefaultChains=param;
                                    

                               }
                            

                        /**
                        * field for UseUserWSDL
                        */

                        
                                    protected boolean localUseUserWSDL ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localUseUserWSDLTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getUseUserWSDL(){
                               return localUseUserWSDL;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param UseUserWSDL
                               */
                               public void setUseUserWSDL(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localUseUserWSDLTracker =
                                       true;
                                   
                                            this.localUseUserWSDL=param;
                                    

                               }
                            

                        /**
                        * field for WsdlFound
                        */

                        
                                    protected boolean localWsdlFound ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWsdlFoundTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getWsdlFound(){
                               return localWsdlFound;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param WsdlFound
                               */
                               public void setWsdlFound(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localWsdlFoundTracker =
                                       true;
                                   
                                            this.localWsdlFound=param;
                                    

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
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://description.axis2.apache.org/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":AxisService",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "AxisService",
                           xmlWriter);
                   }

               
                   }
                if (localEPRsTracker){
                             if (localEPRs!=null) {
                                   namespace = "http://description.axis2.apache.org/xsd";
                                   for (int i = 0;i < localEPRs.length;i++){
                                        
                                            if (localEPRs[i] != null){
                                        
                                                writeStartElement(null, namespace, "EPRs", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEPRs[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://description.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "EPRs", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "EPRs", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localWSAddressingFlagTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "WSAddressingFlag", xmlWriter);
                             

                                          if (localWSAddressingFlag==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localWSAddressingFlag);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localActiveTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "active", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("active cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localActive));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localAxisServiceGroupTracker){
                                    if (localAxisServiceGroup==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisServiceGroup", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisServiceGroup.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisServiceGroup"),
                                        xmlWriter);
                                    }
                                } if (localBindingNameTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "bindingName", xmlWriter);
                             

                                          if (localBindingName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localBindingName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localClassLoaderTracker){
                            
                            if (localClassLoader!=null){
                                if (localClassLoader instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localClassLoader).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","classLoader"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "classLoader", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localClassLoader, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "classLoader", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localClientSideTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "clientSide", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("clientSide cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localClientSide));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localControlOperationsTracker){
                            
                            if (localControlOperations!=null){
                                if (localControlOperations instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localControlOperations).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","controlOperations"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "controlOperations", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localControlOperations, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "controlOperations", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localCustomSchemaNamePrefixTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "customSchemaNamePrefix", xmlWriter);
                             

                                          if (localCustomSchemaNamePrefix==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localCustomSchemaNamePrefix);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localCustomSchemaNameSuffixTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "customSchemaNameSuffix", xmlWriter);
                             

                                          if (localCustomSchemaNameSuffix==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localCustomSchemaNameSuffix);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localCustomWsdlTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "customWsdl", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("customWsdl cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCustomWsdl));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localElementFormDefaultTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "elementFormDefault", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("elementFormDefault cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localElementFormDefault));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localEnableAllTransportsTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "enableAllTransports", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("enableAllTransports cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEnableAllTransports));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localEndpointNameTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "endpointName", xmlWriter);
                             

                                          if (localEndpointName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localEndpointName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localEndpointURLTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "endpointURL", xmlWriter);
                             

                                          if (localEndpointURL==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localEndpointURL);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localEndpointsTracker){
                                    if (localEndpoints==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "endpoints", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localEndpoints.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","endpoints"),
                                        xmlWriter);
                                    }
                                } if (localExcludeInfoTracker){
                                    if (localExcludeInfo==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "excludeInfo", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localExcludeInfo.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","excludeInfo"),
                                        xmlWriter);
                                    }
                                } if (localExposedTransportsTracker){
                             if (localExposedTransports!=null) {
                                   namespace = "http://description.axis2.apache.org/xsd";
                                   for (int i = 0;i < localExposedTransports.length;i++){
                                        
                                            if (localExposedTransports[i] != null){
                                        
                                                writeStartElement(null, namespace, "exposedTransports", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localExposedTransports[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://description.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "exposedTransports", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "exposedTransports", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localFileNameTracker){
                                    if (localFileName==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "fileName", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localFileName.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","fileName"),
                                        xmlWriter);
                                    }
                                } if (localImportedNamespacesTracker){
                            
                            if (localImportedNamespaces!=null){
                                if (localImportedNamespaces instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localImportedNamespaces).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","importedNamespaces"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "importedNamespaces", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localImportedNamespaces, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "importedNamespaces", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localKeyTracker){
                            
                            if (localKey!=null){
                                if (localKey instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localKey).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","key"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localKey, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localLastUpdateTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "lastUpdate", xmlWriter);
                             
                                               if (localLastUpdate==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("lastUpdate cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLastUpdate));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localLastupdateETracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "lastupdate", xmlWriter);
                             
                                               if (localLastupdateE==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("lastupdate cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLastupdateE));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localMessageElementQNameToOperationMapTracker){
                            
                            if (localMessageElementQNameToOperationMap!=null){
                                 for (int i = 0;i < localMessageElementQNameToOperationMap.length;i++){
                                    if (localMessageElementQNameToOperationMap[i] != null){

                                           if (localMessageElementQNameToOperationMap[i] instanceof org.apache.axis2.databinding.ADBBean){
                                                ((org.apache.axis2.databinding.ADBBean)localMessageElementQNameToOperationMap[i]).serialize(
                                                           new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageElementQNameToOperationMap"),
                                                           xmlWriter,true);
                                            } else {
                                                writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageElementQNameToOperationMap", xmlWriter);
                                                org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localMessageElementQNameToOperationMap[i], xmlWriter);
                                                xmlWriter.writeEndElement();
                                             }

                                    } else {
                                       
                                            // write null attribute
                                            writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageElementQNameToOperationMap", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                        
                                    }
                                 }
                            } else {
                                 
                                        // write null attribute
                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageElementQNameToOperationMap", xmlWriter);

                                       // write the nil attribute
                                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                       xmlWriter.writeEndElement();
                                    
                            }

                        } if (localModifyUserWSDLPortAddressTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "modifyUserWSDLPortAddress", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("modifyUserWSDLPortAddress cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifyUserWSDLPortAddress));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localModulesTracker){
                            
                            if (localModules!=null){
                                if (localModules instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localModules).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","modules"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "modules", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localModules, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "modules", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localNameTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "name", xmlWriter);
                             

                                          if (localName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNameSpacesMapTracker){
                                    if (localNameSpacesMap==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "nameSpacesMap", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localNameSpacesMap.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","nameSpacesMap"),
                                        xmlWriter);
                                    }
                                } if (localNamespaceMapTracker){
                                    if (localNamespaceMap==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "namespaceMap", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localNamespaceMap.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","namespaceMap"),
                                        xmlWriter);
                                    }
                                } if (localObjectSupplierTracker){
                                    if (localObjectSupplier==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "objectSupplier", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localObjectSupplier.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","objectSupplier"),
                                        xmlWriter);
                                    }
                                } if (localOperationsTracker){
                                    if (localOperations==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "operations", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localOperations.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","operations"),
                                        xmlWriter);
                                    }
                                } if (localOperationsNameListTracker){
                            
                            if (localOperationsNameList!=null){
                                if (localOperationsNameList instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localOperationsNameList).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","operationsNameList"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "operationsNameList", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localOperationsNameList, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "operationsNameList", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localP2NMapTracker){
                                    if (localP2NMap==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "p2nMap", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localP2NMap.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","p2nMap"),
                                        xmlWriter);
                                    }
                                } if (localParentTracker){
                                    if (localParent==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "parent", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localParent.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","parent"),
                                        xmlWriter);
                                    }
                                } if (localPortTypeNameTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "portTypeName", xmlWriter);
                             

                                          if (localPortTypeName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localPortTypeName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPublishedOperationsTracker){
                            
                            if (localPublishedOperations!=null){
                                if (localPublishedOperations instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localPublishedOperations).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","publishedOperations"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "publishedOperations", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localPublishedOperations, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "publishedOperations", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localSchemaLocationsAdjustedTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "schemaLocationsAdjusted", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("schemaLocationsAdjusted cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSchemaLocationsAdjusted));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSchemaMappingTableTracker){
                                    if (localSchemaMappingTable==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "schemaMappingTable", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localSchemaMappingTable.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaMappingTable"),
                                        xmlWriter);
                                    }
                                } if (localSchemaTargetNamespaceTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "schemaTargetNamespace", xmlWriter);
                             

                                          if (localSchemaTargetNamespace==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSchemaTargetNamespace);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSchemaTargetNamespacePrefixTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "schemaTargetNamespacePrefix", xmlWriter);
                             

                                          if (localSchemaTargetNamespacePrefix==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSchemaTargetNamespacePrefix);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSchematargetNamespaceETracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "schematargetNamespace", xmlWriter);
                             

                                          if (localSchematargetNamespaceE==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSchematargetNamespaceE);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSchematargetNamespacePrefixETracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "schematargetNamespacePrefix", xmlWriter);
                             

                                          if (localSchematargetNamespacePrefixE==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSchematargetNamespacePrefixE);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localScopeTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "scope", xmlWriter);
                             

                                          if (localScope==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localScope);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceDescriptionTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceDescription", xmlWriter);
                             

                                          if (localServiceDescription==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServiceDescription);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceLifeCycleTracker){
                                    if (localServiceLifeCycle==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "serviceLifeCycle", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localServiceLifeCycle.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","serviceLifeCycle"),
                                        xmlWriter);
                                    }
                                } if (localSetEndpointsToAllUsedBindingsTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "setEndpointsToAllUsedBindings", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("setEndpointsToAllUsedBindings cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSetEndpointsToAllUsedBindings));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSoapNsUriTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "soapNsUri", xmlWriter);
                             

                                          if (localSoapNsUri==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSoapNsUri);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localTargetNamespaceTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "targetNamespace", xmlWriter);
                             

                                          if (localTargetNamespace==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localTargetNamespace);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localTargetNamespacePrefixTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "targetNamespacePrefix", xmlWriter);
                             

                                          if (localTargetNamespacePrefix==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localTargetNamespacePrefix);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localTypeTableTracker){
                                    if (localTypeTable==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "typeTable", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTypeTable.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","typeTable"),
                                        xmlWriter);
                                    }
                                } if (localUseDefaultChainsTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "useDefaultChains", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("useDefaultChains cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseDefaultChains));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localUseUserWSDLTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "useUserWSDL", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("useUserWSDL cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseUserWSDL));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localWsdlFoundTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "wsdlFound", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("wsdlFound cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWsdlFound));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://description.axis2.apache.org/xsd")){
                return "ns19";
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

                 if (localEPRsTracker){
                            if (localEPRs!=null){
                                  for (int i = 0;i < localEPRs.length;i++){
                                      
                                         if (localEPRs[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "EPRs"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEPRs[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "EPRs"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "EPRs"));
                                    elementList.add(null);
                                
                            }

                        } if (localWSAddressingFlagTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "WSAddressingFlag"));
                                 
                                         elementList.add(localWSAddressingFlag==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWSAddressingFlag));
                                    } if (localActiveTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "active"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localActive));
                            } if (localAxisServiceGroupTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "axisServiceGroup"));
                            
                            
                                    elementList.add(localAxisServiceGroup==null?null:
                                    localAxisServiceGroup);
                                } if (localBindingNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "bindingName"));
                                 
                                         elementList.add(localBindingName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBindingName));
                                    } if (localClassLoaderTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "classLoader"));
                            
                            
                                    elementList.add(localClassLoader==null?null:
                                    localClassLoader);
                                } if (localClientSideTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "clientSide"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localClientSide));
                            } if (localControlOperationsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "controlOperations"));
                            
                            
                                    elementList.add(localControlOperations==null?null:
                                    localControlOperations);
                                } if (localCustomSchemaNamePrefixTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "customSchemaNamePrefix"));
                                 
                                         elementList.add(localCustomSchemaNamePrefix==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCustomSchemaNamePrefix));
                                    } if (localCustomSchemaNameSuffixTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "customSchemaNameSuffix"));
                                 
                                         elementList.add(localCustomSchemaNameSuffix==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCustomSchemaNameSuffix));
                                    } if (localCustomWsdlTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "customWsdl"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCustomWsdl));
                            } if (localElementFormDefaultTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "elementFormDefault"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localElementFormDefault));
                            } if (localEnableAllTransportsTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "enableAllTransports"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEnableAllTransports));
                            } if (localEndpointNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "endpointName"));
                                 
                                         elementList.add(localEndpointName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEndpointName));
                                    } if (localEndpointURLTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "endpointURL"));
                                 
                                         elementList.add(localEndpointURL==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEndpointURL));
                                    } if (localEndpointsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "endpoints"));
                            
                            
                                    elementList.add(localEndpoints==null?null:
                                    localEndpoints);
                                } if (localExcludeInfoTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "excludeInfo"));
                            
                            
                                    elementList.add(localExcludeInfo==null?null:
                                    localExcludeInfo);
                                } if (localExposedTransportsTracker){
                            if (localExposedTransports!=null){
                                  for (int i = 0;i < localExposedTransports.length;i++){
                                      
                                         if (localExposedTransports[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "exposedTransports"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localExposedTransports[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "exposedTransports"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "exposedTransports"));
                                    elementList.add(null);
                                
                            }

                        } if (localFileNameTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "fileName"));
                            
                            
                                    elementList.add(localFileName==null?null:
                                    localFileName);
                                } if (localImportedNamespacesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "importedNamespaces"));
                            
                            
                                    elementList.add(localImportedNamespaces==null?null:
                                    localImportedNamespaces);
                                } if (localKeyTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "key"));
                            
                            
                                    elementList.add(localKey==null?null:
                                    localKey);
                                } if (localLastUpdateTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "lastUpdate"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLastUpdate));
                            } if (localLastupdateETracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "lastupdate"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLastupdateE));
                            } if (localMessageElementQNameToOperationMapTracker){
                             if (localMessageElementQNameToOperationMap!=null) {
                                 for (int i = 0;i < localMessageElementQNameToOperationMap.length;i++){

                                    if (localMessageElementQNameToOperationMap[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                          "messageElementQNameToOperationMap"));
                                         elementList.add(localMessageElementQNameToOperationMap[i]);
                                    } else {
                                        
                                                elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                          "messageElementQNameToOperationMap"));
                                                elementList.add(null);
                                            
                                    }

                                 }
                             } else {
                                 
                                        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                          "messageElementQNameToOperationMap"));
                                        elementList.add(localMessageElementQNameToOperationMap);
                                    
                             }

                        } if (localModifyUserWSDLPortAddressTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "modifyUserWSDLPortAddress"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifyUserWSDLPortAddress));
                            } if (localModulesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "modules"));
                            
                            
                                    elementList.add(localModules==null?null:
                                    localModules);
                                } if (localNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "name"));
                                 
                                         elementList.add(localName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localName));
                                    } if (localNameSpacesMapTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "nameSpacesMap"));
                            
                            
                                    elementList.add(localNameSpacesMap==null?null:
                                    localNameSpacesMap);
                                } if (localNamespaceMapTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "namespaceMap"));
                            
                            
                                    elementList.add(localNamespaceMap==null?null:
                                    localNamespaceMap);
                                } if (localObjectSupplierTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "objectSupplier"));
                            
                            
                                    elementList.add(localObjectSupplier==null?null:
                                    localObjectSupplier);
                                } if (localOperationsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "operations"));
                            
                            
                                    elementList.add(localOperations==null?null:
                                    localOperations);
                                } if (localOperationsNameListTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "operationsNameList"));
                            
                            
                                    elementList.add(localOperationsNameList==null?null:
                                    localOperationsNameList);
                                } if (localP2NMapTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "p2nMap"));
                            
                            
                                    elementList.add(localP2NMap==null?null:
                                    localP2NMap);
                                } if (localParentTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "parent"));
                            
                            
                                    elementList.add(localParent==null?null:
                                    localParent);
                                } if (localPortTypeNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "portTypeName"));
                                 
                                         elementList.add(localPortTypeName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPortTypeName));
                                    } if (localPublishedOperationsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "publishedOperations"));
                            
                            
                                    elementList.add(localPublishedOperations==null?null:
                                    localPublishedOperations);
                                } if (localSchemaLocationsAdjustedTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schemaLocationsAdjusted"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSchemaLocationsAdjusted));
                            } if (localSchemaMappingTableTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schemaMappingTable"));
                            
                            
                                    elementList.add(localSchemaMappingTable==null?null:
                                    localSchemaMappingTable);
                                } if (localSchemaTargetNamespaceTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schemaTargetNamespace"));
                                 
                                         elementList.add(localSchemaTargetNamespace==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSchemaTargetNamespace));
                                    } if (localSchemaTargetNamespacePrefixTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schemaTargetNamespacePrefix"));
                                 
                                         elementList.add(localSchemaTargetNamespacePrefix==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSchemaTargetNamespacePrefix));
                                    } if (localSchematargetNamespaceETracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schematargetNamespace"));
                                 
                                         elementList.add(localSchematargetNamespaceE==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSchematargetNamespaceE));
                                    } if (localSchematargetNamespacePrefixETracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schematargetNamespacePrefix"));
                                 
                                         elementList.add(localSchematargetNamespacePrefixE==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSchematargetNamespacePrefixE));
                                    } if (localScopeTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "scope"));
                                 
                                         elementList.add(localScope==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localScope));
                                    } if (localServiceDescriptionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "serviceDescription"));
                                 
                                         elementList.add(localServiceDescription==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceDescription));
                                    } if (localServiceLifeCycleTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "serviceLifeCycle"));
                            
                            
                                    elementList.add(localServiceLifeCycle==null?null:
                                    localServiceLifeCycle);
                                } if (localSetEndpointsToAllUsedBindingsTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "setEndpointsToAllUsedBindings"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSetEndpointsToAllUsedBindings));
                            } if (localSoapNsUriTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "soapNsUri"));
                                 
                                         elementList.add(localSoapNsUri==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSoapNsUri));
                                    } if (localTargetNamespaceTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "targetNamespace"));
                                 
                                         elementList.add(localTargetNamespace==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTargetNamespace));
                                    } if (localTargetNamespacePrefixTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "targetNamespacePrefix"));
                                 
                                         elementList.add(localTargetNamespacePrefix==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTargetNamespacePrefix));
                                    } if (localTypeTableTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "typeTable"));
                            
                            
                                    elementList.add(localTypeTable==null?null:
                                    localTypeTable);
                                } if (localUseDefaultChainsTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "useDefaultChains"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseDefaultChains));
                            } if (localUseUserWSDLTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "useUserWSDL"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseUserWSDL));
                            } if (localWsdlFoundTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "wsdlFound"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWsdlFound));
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
        public static AxisService parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            AxisService object =
                new AxisService();

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
                    
                            if (!"AxisService".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (AxisService)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list1 = new java.util.ArrayList();
                    
                        java.util.ArrayList list18 = new java.util.ArrayList();
                    
                        java.util.ArrayList list24 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","EPRs").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list1.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list1.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone1 = false;
                                            while(!loopDone1){
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
                                                    loopDone1 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","EPRs").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list1.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list1.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone1 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setEPRs((java.lang.String[])
                                                        list1.toArray(new java.lang.String[list1.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","WSAddressingFlag").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setWSAddressingFlag(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","active").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setActive(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisServiceGroup").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setAxisServiceGroup(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setAxisServiceGroup(org.apache.axis2.description.xsd.AxisServiceGroup.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","bindingName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setBindingName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","classLoader").equals(reader.getName())){
                                
                                     object.setClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","clientSide").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setClientSide(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","controlOperations").equals(reader.getName())){
                                
                                     object.setControlOperations(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","customSchemaNamePrefix").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCustomSchemaNamePrefix(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","customSchemaNameSuffix").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCustomSchemaNameSuffix(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","customWsdl").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCustomWsdl(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","elementFormDefault").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setElementFormDefault(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","enableAllTransports").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEnableAllTransports(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","endpointName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEndpointName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","endpointURL").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEndpointURL(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","endpoints").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setEndpoints(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setEndpoints(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","excludeInfo").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setExcludeInfo(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setExcludeInfo(org.apache.axis2.deployment.util.xsd.ExcludeInfo.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","exposedTransports").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list18.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list18.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone18 = false;
                                            while(!loopDone18){
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
                                                    loopDone18 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","exposedTransports").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list18.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list18.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone18 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setExposedTransports((java.lang.String[])
                                                        list18.toArray(new java.lang.String[list18.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","fileName").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setFileName(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setFileName(authclient.java.net.xsd.URL.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","importedNamespaces").equals(reader.getName())){
                                
                                     object.setImportedNamespaces(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","key").equals(reader.getName())){
                                
                                     object.setKey(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","lastUpdate").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLastUpdate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setLastUpdate(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","lastupdate").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLastupdateE(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setLastupdateE(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageElementQNameToOperationMap").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    

                                             boolean loopDone24=false;
                                             javax.xml.namespace.QName startQname24 = new javax.xml.namespace.QName(
                                                    "http://description.axis2.apache.org/xsd",
                                                    "messageElementQNameToOperationMap");

                                             while (!loopDone24){
                                                 event = reader.getEventType();
                                                 if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                                                         && startQname24.equals(reader.getName())){

                                                      
                                                      
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list24.add(null);
                                                              reader.next();
                                                          }else{
                                                      list24.add(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                                            org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                                       }
                                                 } else if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event &&
                                                            !startQname24.equals(reader.getName())){
                                                     loopDone24 = true;
                                                 }else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event &&
                                                           !startQname24.equals(reader.getName())){
                                                     loopDone24 = true;
                                                 }else if (javax.xml.stream.XMLStreamConstants.END_DOCUMENT == event){
                                                     loopDone24 = true;
                                                 }else{
                                                     reader.next();
                                                 }

                                             }

                                            
                                                    object.setMessageElementQNameToOperationMap(list24.toArray());
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","modifyUserWSDLPortAddress").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifyUserWSDLPortAddress(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","modules").equals(reader.getName())){
                                
                                     object.setModules(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","name").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","nameSpacesMap").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setNameSpacesMap(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setNameSpacesMap(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","namespaceMap").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setNamespaceMap(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setNamespaceMap(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","objectSupplier").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setObjectSupplier(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setObjectSupplier(org.apache.axis2.engine.xsd.ObjectSupplier.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","operations").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setOperations(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setOperations(authclient.java.util.xsd.Iterator.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","operationsNameList").equals(reader.getName())){
                                
                                     object.setOperationsNameList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","p2nMap").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setP2NMap(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setP2NMap(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","parent").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setParent(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setParent(org.apache.axis2.description.xsd.AxisServiceGroup.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","portTypeName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPortTypeName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","publishedOperations").equals(reader.getName())){
                                
                                     object.setPublishedOperations(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaLocationsAdjusted").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSchemaLocationsAdjusted(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaMappingTable").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setSchemaMappingTable(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setSchemaMappingTable(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaTargetNamespace").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSchemaTargetNamespace(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaTargetNamespacePrefix").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSchemaTargetNamespacePrefix(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schematargetNamespace").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSchematargetNamespaceE(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schematargetNamespacePrefix").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSchematargetNamespacePrefixE(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","scope").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setScope(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","serviceDescription").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceDescription(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","serviceLifeCycle").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setServiceLifeCycle(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setServiceLifeCycle(org.apache.axis2.engine.xsd.ServiceLifeCycle.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","setEndpointsToAllUsedBindings").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSetEndpointsToAllUsedBindings(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","soapNsUri").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSoapNsUri(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","targetNamespace").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setTargetNamespace(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","targetNamespacePrefix").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setTargetNamespacePrefix(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","typeTable").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTypeTable(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTypeTable(org.apache.axis2.description.java2wsdl.xsd.TypeTable.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","useDefaultChains").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setUseDefaultChains(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","useUserWSDL").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setUseUserWSDL(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","wsdlFound").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setWsdlFound(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
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
           
    