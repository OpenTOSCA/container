
/**
 * AxisOperation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.description.xsd;
            

            /**
            *  AxisOperation bean class
            */
        
        public abstract class AxisOperation
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = AxisOperation
                Namespace URI = http://description.axis2.apache.org/xsd
                Namespace Prefix = ns19
                */
            

                        /**
                        * field for WSAMappingList
                        */

                        
                                    protected java.lang.Object localWSAMappingList ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWSAMappingListTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getWSAMappingList(){
                               return localWSAMappingList;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param WSAMappingList
                               */
                               public void setWSAMappingList(java.lang.Object param){
                            localWSAMappingListTracker = true;
                                   
                                            this.localWSAMappingList=param;
                                    

                               }
                            

                        /**
                        * field for AxisService
                        */

                        
                                    protected org.apache.axis2.description.xsd.AxisService localAxisService ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAxisServiceTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.AxisService
                           */
                           public  org.apache.axis2.description.xsd.AxisService getAxisService(){
                               return localAxisService;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AxisService
                               */
                               public void setAxisService(org.apache.axis2.description.xsd.AxisService param){
                            localAxisServiceTracker = true;
                                   
                                            this.localAxisService=param;
                                    

                               }
                            

                        /**
                        * field for AxisSpecificMEPConstant
                        */

                        
                                    protected int localAxisSpecificMEPConstant ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAxisSpecificMEPConstantTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getAxisSpecificMEPConstant(){
                               return localAxisSpecificMEPConstant;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AxisSpecificMEPConstant
                               */
                               public void setAxisSpecificMEPConstant(int param){
                            
                                       // setting primitive attribute tracker to true
                                       localAxisSpecificMEPConstantTracker =
                                       param != java.lang.Integer.MIN_VALUE;
                                   
                                            this.localAxisSpecificMEPConstant=param;
                                    

                               }
                            

                        /**
                        * field for ControlOperation
                        */

                        
                                    protected boolean localControlOperation ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localControlOperationTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getControlOperation(){
                               return localControlOperation;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ControlOperation
                               */
                               public void setControlOperation(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localControlOperationTracker =
                                       true;
                                   
                                            this.localControlOperation=param;
                                    

                               }
                            

                        /**
                        * field for FaultAction
                        */

                        
                                    protected java.lang.String localFaultAction ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultActionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getFaultAction(){
                               return localFaultAction;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FaultAction
                               */
                               public void setFaultAction(java.lang.String param){
                            localFaultActionTracker = true;
                                   
                                            this.localFaultAction=param;
                                    

                               }
                            

                        /**
                        * field for FaultActionNames
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localFaultActionNames ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultActionNamesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getFaultActionNames(){
                               return localFaultActionNames;
                           }

                           
                        


                               
                              /**
                               * validate the array for FaultActionNames
                               */
                              protected void validateFaultActionNames(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param FaultActionNames
                              */
                              public void setFaultActionNames(java.lang.String[] param){
                              
                                   validateFaultActionNames(param);

                               localFaultActionNamesTracker = true;
                                      
                                      this.localFaultActionNames=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addFaultActionNames(java.lang.String param){
                                   if (localFaultActionNames == null){
                                   localFaultActionNames = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localFaultActionNamesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localFaultActionNames);
                               list.add(param);
                               this.localFaultActionNames =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for FaultMessages
                        */

                        
                                    protected java.lang.Object localFaultMessages ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultMessagesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getFaultMessages(){
                               return localFaultMessages;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FaultMessages
                               */
                               public void setFaultMessages(java.lang.Object param){
                            localFaultMessagesTracker = true;
                                   
                                            this.localFaultMessages=param;
                                    

                               }
                            

                        /**
                        * field for InputAction
                        */

                        
                                    protected java.lang.String localInputAction ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInputActionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getInputAction(){
                               return localInputAction;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InputAction
                               */
                               public void setInputAction(java.lang.String param){
                            localInputActionTracker = true;
                                   
                                            this.localInputAction=param;
                                    

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
                        * field for MessageExchangePattern
                        */

                        
                                    protected java.lang.String localMessageExchangePattern ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessageExchangePatternTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getMessageExchangePattern(){
                               return localMessageExchangePattern;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param MessageExchangePattern
                               */
                               public void setMessageExchangePattern(java.lang.String param){
                            localMessageExchangePatternTracker = true;
                                   
                                            this.localMessageExchangePattern=param;
                                    

                               }
                            

                        /**
                        * field for MessageReceiver
                        */

                        
                                    protected org.apache.axis2.engine.xsd.MessageReceiver localMessageReceiver ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessageReceiverTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.MessageReceiver
                           */
                           public  org.apache.axis2.engine.xsd.MessageReceiver getMessageReceiver(){
                               return localMessageReceiver;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param MessageReceiver
                               */
                               public void setMessageReceiver(org.apache.axis2.engine.xsd.MessageReceiver param){
                            localMessageReceiverTracker = true;
                                   
                                            this.localMessageReceiver=param;
                                    

                               }
                            

                        /**
                        * field for Messages
                        */

                        
                                    protected authclient.java.util.xsd.Iterator localMessages ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessagesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Iterator
                           */
                           public  authclient.java.util.xsd.Iterator getMessages(){
                               return localMessages;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Messages
                               */
                               public void setMessages(authclient.java.util.xsd.Iterator param){
                            localMessagesTracker = true;
                                   
                                            this.localMessages=param;
                                    

                               }
                            

                        /**
                        * field for ModuleRefs
                        */

                        
                                    protected java.lang.Object localModuleRefs ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModuleRefsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getModuleRefs(){
                               return localModuleRefs;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModuleRefs
                               */
                               public void setModuleRefs(java.lang.Object param){
                            localModuleRefsTracker = true;
                                   
                                            this.localModuleRefs=param;
                                    

                               }
                            

                        /**
                        * field for Name
                        */

                        
                                    protected java.lang.Object localName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getName(){
                               return localName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Name
                               */
                               public void setName(java.lang.Object param){
                            localNameTracker = true;
                                   
                                            this.localName=param;
                                    

                               }
                            

                        /**
                        * field for OutputAction
                        */

                        
                                    protected java.lang.String localOutputAction ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOutputActionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOutputAction(){
                               return localOutputAction;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OutputAction
                               */
                               public void setOutputAction(java.lang.String param){
                            localOutputActionTracker = true;
                                   
                                            this.localOutputAction=param;
                                    

                               }
                            

                        /**
                        * field for PhasesInFaultFlow
                        */

                        
                                    protected java.lang.Object localPhasesInFaultFlow ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPhasesInFaultFlowTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getPhasesInFaultFlow(){
                               return localPhasesInFaultFlow;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PhasesInFaultFlow
                               */
                               public void setPhasesInFaultFlow(java.lang.Object param){
                            localPhasesInFaultFlowTracker = true;
                                   
                                            this.localPhasesInFaultFlow=param;
                                    

                               }
                            

                        /**
                        * field for PhasesOutFaultFlow
                        */

                        
                                    protected java.lang.Object localPhasesOutFaultFlow ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPhasesOutFaultFlowTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getPhasesOutFaultFlow(){
                               return localPhasesOutFaultFlow;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PhasesOutFaultFlow
                               */
                               public void setPhasesOutFaultFlow(java.lang.Object param){
                            localPhasesOutFaultFlowTracker = true;
                                   
                                            this.localPhasesOutFaultFlow=param;
                                    

                               }
                            

                        /**
                        * field for PhasesOutFlow
                        */

                        
                                    protected java.lang.Object localPhasesOutFlow ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPhasesOutFlowTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getPhasesOutFlow(){
                               return localPhasesOutFlow;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PhasesOutFlow
                               */
                               public void setPhasesOutFlow(java.lang.Object param){
                            localPhasesOutFlowTracker = true;
                                   
                                            this.localPhasesOutFlow=param;
                                    

                               }
                            

                        /**
                        * field for RemainingPhasesInFlow
                        */

                        
                                    protected java.lang.Object localRemainingPhasesInFlow ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRemainingPhasesInFlowTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getRemainingPhasesInFlow(){
                               return localRemainingPhasesInFlow;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param RemainingPhasesInFlow
                               */
                               public void setRemainingPhasesInFlow(java.lang.Object param){
                            localRemainingPhasesInFlowTracker = true;
                                   
                                            this.localRemainingPhasesInFlow=param;
                                    

                               }
                            

                        /**
                        * field for SoapAction
                        */

                        
                                    protected java.lang.String localSoapAction ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSoapActionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSoapAction(){
                               return localSoapAction;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SoapAction
                               */
                               public void setSoapAction(java.lang.String param){
                            localSoapActionTracker = true;
                                   
                                            this.localSoapAction=param;
                                    

                               }
                            

                        /**
                        * field for Style
                        */

                        
                                    protected java.lang.String localStyle ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localStyleTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getStyle(){
                               return localStyle;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Style
                               */
                               public void setStyle(java.lang.String param){
                            localStyleTracker = true;
                                   
                                            this.localStyle=param;
                                    

                               }
                            

                        /**
                        * field for WsamappingListE
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localWsamappingListE ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWsamappingListETracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getWsamappingListE(){
                               return localWsamappingListE;
                           }

                           
                        


                               
                              /**
                               * validate the array for WsamappingListE
                               */
                              protected void validateWsamappingListE(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param WsamappingListE
                              */
                              public void setWsamappingListE(java.lang.String[] param){
                              
                                   validateWsamappingListE(param);

                               localWsamappingListETracker = true;
                                      
                                      this.localWsamappingListE=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addWsamappingListE(java.lang.String param){
                                   if (localWsamappingListE == null){
                                   localWsamappingListE = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localWsamappingListETracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localWsamappingListE);
                               list.add(param);
                               this.localWsamappingListE =
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
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://description.axis2.apache.org/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":AxisOperation",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "AxisOperation",
                           xmlWriter);
                   }

               
                   }
                if (localWSAMappingListTracker){
                            
                            if (localWSAMappingList!=null){
                                if (localWSAMappingList instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localWSAMappingList).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","WSAMappingList"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "WSAMappingList", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localWSAMappingList, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "WSAMappingList", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localAxisServiceTracker){
                                    if (localAxisService==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisService", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisService.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisService"),
                                        xmlWriter);
                                    }
                                } if (localAxisSpecificMEPConstantTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "axisSpecificMEPConstant", xmlWriter);
                             
                                               if (localAxisSpecificMEPConstant==java.lang.Integer.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("axisSpecificMEPConstant cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAxisSpecificMEPConstant));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localControlOperationTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "controlOperation", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("controlOperation cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localControlOperation));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFaultActionTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "faultAction", xmlWriter);
                             

                                          if (localFaultAction==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localFaultAction);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFaultActionNamesTracker){
                             if (localFaultActionNames!=null) {
                                   namespace = "http://description.axis2.apache.org/xsd";
                                   for (int i = 0;i < localFaultActionNames.length;i++){
                                        
                                            if (localFaultActionNames[i] != null){
                                        
                                                writeStartElement(null, namespace, "faultActionNames", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultActionNames[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://description.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "faultActionNames", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultActionNames", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localFaultMessagesTracker){
                            
                            if (localFaultMessages!=null){
                                if (localFaultMessages instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localFaultMessages).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","faultMessages"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultMessages", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localFaultMessages, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "faultMessages", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localInputActionTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "inputAction", xmlWriter);
                             

                                          if (localInputAction==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localInputAction);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
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


                        } if (localMessageExchangePatternTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "messageExchangePattern", xmlWriter);
                             

                                          if (localMessageExchangePattern==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localMessageExchangePattern);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localMessageReceiverTracker){
                                    if (localMessageReceiver==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageReceiver", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localMessageReceiver.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageReceiver"),
                                        xmlWriter);
                                    }
                                } if (localMessagesTracker){
                                    if (localMessages==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "messages", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localMessages.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messages"),
                                        xmlWriter);
                                    }
                                } if (localModuleRefsTracker){
                            
                            if (localModuleRefs!=null){
                                if (localModuleRefs instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localModuleRefs).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","moduleRefs"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "moduleRefs", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localModuleRefs, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "moduleRefs", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localNameTracker){
                            
                            if (localName!=null){
                                if (localName instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localName).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","name"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "name", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localName, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "name", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localOutputActionTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "outputAction", xmlWriter);
                             

                                          if (localOutputAction==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOutputAction);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPhasesInFaultFlowTracker){
                            
                            if (localPhasesInFaultFlow!=null){
                                if (localPhasesInFaultFlow instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localPhasesInFaultFlow).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","phasesInFaultFlow"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesInFaultFlow", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localPhasesInFaultFlow, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesInFaultFlow", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localPhasesOutFaultFlowTracker){
                            
                            if (localPhasesOutFaultFlow!=null){
                                if (localPhasesOutFaultFlow instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localPhasesOutFaultFlow).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","phasesOutFaultFlow"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFaultFlow", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localPhasesOutFaultFlow, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFaultFlow", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localPhasesOutFlowTracker){
                            
                            if (localPhasesOutFlow!=null){
                                if (localPhasesOutFlow instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localPhasesOutFlow).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","phasesOutFlow"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFlow", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localPhasesOutFlow, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "phasesOutFlow", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localRemainingPhasesInFlowTracker){
                            
                            if (localRemainingPhasesInFlow!=null){
                                if (localRemainingPhasesInFlow instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localRemainingPhasesInFlow).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","remainingPhasesInFlow"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "remainingPhasesInFlow", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localRemainingPhasesInFlow, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "remainingPhasesInFlow", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localSoapActionTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "soapAction", xmlWriter);
                             

                                          if (localSoapAction==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSoapAction);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localStyleTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "style", xmlWriter);
                             

                                          if (localStyle==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localStyle);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localWsamappingListETracker){
                             if (localWsamappingListE!=null) {
                                   namespace = "http://description.axis2.apache.org/xsd";
                                   for (int i = 0;i < localWsamappingListE.length;i++){
                                        
                                            if (localWsamappingListE[i] != null){
                                        
                                                writeStartElement(null, namespace, "wsamappingList", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWsamappingListE[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://description.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "wsamappingList", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "wsamappingList", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

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

                 if (localWSAMappingListTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "WSAMappingList"));
                            
                            
                                    elementList.add(localWSAMappingList==null?null:
                                    localWSAMappingList);
                                } if (localAxisServiceTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "axisService"));
                            
                            
                                    elementList.add(localAxisService==null?null:
                                    localAxisService);
                                } if (localAxisSpecificMEPConstantTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "axisSpecificMEPConstant"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAxisSpecificMEPConstant));
                            } if (localControlOperationTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "controlOperation"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localControlOperation));
                            } if (localFaultActionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "faultAction"));
                                 
                                         elementList.add(localFaultAction==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultAction));
                                    } if (localFaultActionNamesTracker){
                            if (localFaultActionNames!=null){
                                  for (int i = 0;i < localFaultActionNames.length;i++){
                                      
                                         if (localFaultActionNames[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "faultActionNames"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultActionNames[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "faultActionNames"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "faultActionNames"));
                                    elementList.add(null);
                                
                            }

                        } if (localFaultMessagesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "faultMessages"));
                            
                            
                                    elementList.add(localFaultMessages==null?null:
                                    localFaultMessages);
                                } if (localInputActionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "inputAction"));
                                 
                                         elementList.add(localInputAction==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInputAction));
                                    } if (localKeyTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "key"));
                            
                            
                                    elementList.add(localKey==null?null:
                                    localKey);
                                } if (localMessageExchangePatternTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "messageExchangePattern"));
                                 
                                         elementList.add(localMessageExchangePattern==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessageExchangePattern));
                                    } if (localMessageReceiverTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "messageReceiver"));
                            
                            
                                    elementList.add(localMessageReceiver==null?null:
                                    localMessageReceiver);
                                } if (localMessagesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "messages"));
                            
                            
                                    elementList.add(localMessages==null?null:
                                    localMessages);
                                } if (localModuleRefsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "moduleRefs"));
                            
                            
                                    elementList.add(localModuleRefs==null?null:
                                    localModuleRefs);
                                } if (localNameTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "name"));
                            
                            
                                    elementList.add(localName==null?null:
                                    localName);
                                } if (localOutputActionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "outputAction"));
                                 
                                         elementList.add(localOutputAction==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOutputAction));
                                    } if (localPhasesInFaultFlowTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "phasesInFaultFlow"));
                            
                            
                                    elementList.add(localPhasesInFaultFlow==null?null:
                                    localPhasesInFaultFlow);
                                } if (localPhasesOutFaultFlowTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "phasesOutFaultFlow"));
                            
                            
                                    elementList.add(localPhasesOutFaultFlow==null?null:
                                    localPhasesOutFaultFlow);
                                } if (localPhasesOutFlowTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "phasesOutFlow"));
                            
                            
                                    elementList.add(localPhasesOutFlow==null?null:
                                    localPhasesOutFlow);
                                } if (localRemainingPhasesInFlowTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "remainingPhasesInFlow"));
                            
                            
                                    elementList.add(localRemainingPhasesInFlow==null?null:
                                    localRemainingPhasesInFlow);
                                } if (localSoapActionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "soapAction"));
                                 
                                         elementList.add(localSoapAction==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSoapAction));
                                    } if (localStyleTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "style"));
                                 
                                         elementList.add(localStyle==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localStyle));
                                    } if (localWsamappingListETracker){
                            if (localWsamappingListE!=null){
                                  for (int i = 0;i < localWsamappingListE.length;i++){
                                      
                                         if (localWsamappingListE[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "wsamappingList"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWsamappingListE[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "wsamappingList"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "wsamappingList"));
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
        public static AxisOperation parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            AxisOperation object =
                null;

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
                    
                            if (!"AxisOperation".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (AxisOperation)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        
                        throw new org.apache.axis2.databinding.ADBException("The an abstract class can not be instantiated !!!");
                    

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list6 = new java.util.ArrayList();
                    
                        java.util.ArrayList list22 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","WSAMappingList").equals(reader.getName())){
                                
                                     object.setWSAMappingList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisService").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setAxisService(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setAxisService(org.apache.axis2.description.xsd.AxisService.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisSpecificMEPConstant").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setAxisSpecificMEPConstant(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setAxisSpecificMEPConstant(java.lang.Integer.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","controlOperation").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setControlOperation(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","faultAction").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFaultAction(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","faultActionNames").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list6.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list6.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone6 = false;
                                            while(!loopDone6){
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
                                                    loopDone6 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","faultActionNames").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list6.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list6.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone6 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setFaultActionNames((java.lang.String[])
                                                        list6.toArray(new java.lang.String[list6.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","faultMessages").equals(reader.getName())){
                                
                                     object.setFaultMessages(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","inputAction").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setInputAction(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageExchangePattern").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setMessageExchangePattern(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageReceiver").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setMessageReceiver(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setMessageReceiver(org.apache.axis2.engine.xsd.MessageReceiver.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messages").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setMessages(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setMessages(authclient.java.util.xsd.Iterator.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","moduleRefs").equals(reader.getName())){
                                
                                     object.setModuleRefs(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","name").equals(reader.getName())){
                                
                                     object.setName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","outputAction").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOutputAction(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","phasesInFaultFlow").equals(reader.getName())){
                                
                                     object.setPhasesInFaultFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","phasesOutFaultFlow").equals(reader.getName())){
                                
                                     object.setPhasesOutFaultFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","phasesOutFlow").equals(reader.getName())){
                                
                                     object.setPhasesOutFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","remainingPhasesInFlow").equals(reader.getName())){
                                
                                     object.setRemainingPhasesInFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","soapAction").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSoapAction(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","style").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setStyle(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","wsamappingList").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list22.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list22.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone22 = false;
                                            while(!loopDone22){
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
                                                    loopDone22 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","wsamappingList").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list22.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list22.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone22 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setWsamappingListE((java.lang.String[])
                                                        list22.toArray(new java.lang.String[list22.size()]));
                                                
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
           
    