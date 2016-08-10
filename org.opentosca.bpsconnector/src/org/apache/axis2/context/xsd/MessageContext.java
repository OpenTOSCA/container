
/**
 * MessageContext.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.context.xsd;
            

            /**
            *  MessageContext bean class
            */
        
        public  class MessageContext
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = MessageContext
                Namespace URI = http://context.axis2.apache.org/xsd
                Namespace Prefix = ns10
                */
            

                        /**
                        * field for FLOW
                        */

                        
                                    protected int localFLOW ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFLOWTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getFLOW(){
                               return localFLOW;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FLOW
                               */
                               public void setFLOW(int param){
                            
                                       // setting primitive attribute tracker to true
                                       localFLOWTracker =
                                       param != java.lang.Integer.MIN_VALUE;
                                   
                                            this.localFLOW=param;
                                    

                               }
                            

                        /**
                        * field for SOAP11
                        */

                        
                                    protected boolean localSOAP11 ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSOAP11Tracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getSOAP11(){
                               return localSOAP11;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SOAP11
                               */
                               public void setSOAP11(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localSOAP11Tracker =
                                       true;
                                   
                                            this.localSOAP11=param;
                                    

                               }
                            

                        /**
                        * field for WSAAction
                        */

                        
                                    protected java.lang.String localWSAAction ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWSAActionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getWSAAction(){
                               return localWSAAction;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param WSAAction
                               */
                               public void setWSAAction(java.lang.String param){
                            localWSAActionTracker = true;
                                   
                                            this.localWSAAction=param;
                                    

                               }
                            

                        /**
                        * field for WSAMessageId
                        */

                        
                                    protected java.lang.String localWSAMessageId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWSAMessageIdTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getWSAMessageId(){
                               return localWSAMessageId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param WSAMessageId
                               */
                               public void setWSAMessageId(java.lang.String param){
                            localWSAMessageIdTracker = true;
                                   
                                            this.localWSAMessageId=param;
                                    

                               }
                            

                        /**
                        * field for AttachmentMap
                        */

                        
                                    protected org.apache.axiom.attachments.xsd.Attachments localAttachmentMap ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAttachmentMapTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axiom.attachments.xsd.Attachments
                           */
                           public  org.apache.axiom.attachments.xsd.Attachments getAttachmentMap(){
                               return localAttachmentMap;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AttachmentMap
                               */
                               public void setAttachmentMap(org.apache.axiom.attachments.xsd.Attachments param){
                            localAttachmentMapTracker = true;
                                   
                                            this.localAttachmentMap=param;
                                    

                               }
                            

                        /**
                        * field for AxisMessage
                        */

                        
                                    protected org.apache.axis2.description.xsd.AxisMessage localAxisMessage ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAxisMessageTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.AxisMessage
                           */
                           public  org.apache.axis2.description.xsd.AxisMessage getAxisMessage(){
                               return localAxisMessage;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AxisMessage
                               */
                               public void setAxisMessage(org.apache.axis2.description.xsd.AxisMessage param){
                            localAxisMessageTracker = true;
                                   
                                            this.localAxisMessage=param;
                                    

                               }
                            

                        /**
                        * field for AxisOperation
                        */

                        
                                    protected org.apache.axis2.description.xsd.AxisOperation localAxisOperation ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAxisOperationTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.AxisOperation
                           */
                           public  org.apache.axis2.description.xsd.AxisOperation getAxisOperation(){
                               return localAxisOperation;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AxisOperation
                               */
                               public void setAxisOperation(org.apache.axis2.description.xsd.AxisOperation param){
                            localAxisOperationTracker = true;
                                   
                                            this.localAxisOperation=param;
                                    

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
                        * field for ConfigurationContext
                        */

                        
                                    protected org.apache.axis2.context.xsd.ConfigurationContext localConfigurationContext ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localConfigurationContextTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.context.xsd.ConfigurationContext
                           */
                           public  org.apache.axis2.context.xsd.ConfigurationContext getConfigurationContext(){
                               return localConfigurationContext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ConfigurationContext
                               */
                               public void setConfigurationContext(org.apache.axis2.context.xsd.ConfigurationContext param){
                            localConfigurationContextTracker = true;
                                   
                                            this.localConfigurationContext=param;
                                    

                               }
                            

                        /**
                        * field for CurrentHandlerIndex
                        */

                        
                                    protected int localCurrentHandlerIndex ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCurrentHandlerIndexTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getCurrentHandlerIndex(){
                               return localCurrentHandlerIndex;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CurrentHandlerIndex
                               */
                               public void setCurrentHandlerIndex(int param){
                            
                                       // setting primitive attribute tracker to true
                                       localCurrentHandlerIndexTracker =
                                       param != java.lang.Integer.MIN_VALUE;
                                   
                                            this.localCurrentHandlerIndex=param;
                                    

                               }
                            

                        /**
                        * field for CurrentPhaseIndex
                        */

                        
                                    protected int localCurrentPhaseIndex ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCurrentPhaseIndexTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getCurrentPhaseIndex(){
                               return localCurrentPhaseIndex;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CurrentPhaseIndex
                               */
                               public void setCurrentPhaseIndex(int param){
                            
                                       // setting primitive attribute tracker to true
                                       localCurrentPhaseIndexTracker =
                                       param != java.lang.Integer.MIN_VALUE;
                                   
                                            this.localCurrentPhaseIndex=param;
                                    

                               }
                            

                        /**
                        * field for DoingMTOM
                        */

                        
                                    protected boolean localDoingMTOM ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDoingMTOMTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getDoingMTOM(){
                               return localDoingMTOM;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param DoingMTOM
                               */
                               public void setDoingMTOM(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localDoingMTOMTracker =
                                       true;
                                   
                                            this.localDoingMTOM=param;
                                    

                               }
                            

                        /**
                        * field for DoingREST
                        */

                        
                                    protected boolean localDoingREST ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDoingRESTTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getDoingREST(){
                               return localDoingREST;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param DoingREST
                               */
                               public void setDoingREST(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localDoingRESTTracker =
                                       true;
                                   
                                            this.localDoingREST=param;
                                    

                               }
                            

                        /**
                        * field for DoingSwA
                        */

                        
                                    protected boolean localDoingSwA ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDoingSwATracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getDoingSwA(){
                               return localDoingSwA;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param DoingSwA
                               */
                               public void setDoingSwA(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localDoingSwATracker =
                                       true;
                                   
                                            this.localDoingSwA=param;
                                    

                               }
                            

                        /**
                        * field for EffectivePolicy
                        */

                        
                                    protected org.apache.neethi.xsd.Policy localEffectivePolicy ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEffectivePolicyTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.neethi.xsd.Policy
                           */
                           public  org.apache.neethi.xsd.Policy getEffectivePolicy(){
                               return localEffectivePolicy;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EffectivePolicy
                               */
                               public void setEffectivePolicy(org.apache.neethi.xsd.Policy param){
                            localEffectivePolicyTracker = true;
                                   
                                            this.localEffectivePolicy=param;
                                    

                               }
                            

                        /**
                        * field for Envelope
                        */

                        
                                    protected org.apache.axiom.soap.xsd.SOAPEnvelope localEnvelope ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localEnvelopeTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axiom.soap.xsd.SOAPEnvelope
                           */
                           public  org.apache.axiom.soap.xsd.SOAPEnvelope getEnvelope(){
                               return localEnvelope;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Envelope
                               */
                               public void setEnvelope(org.apache.axiom.soap.xsd.SOAPEnvelope param){
                            localEnvelopeTracker = true;
                                   
                                            this.localEnvelope=param;
                                    

                               }
                            

                        /**
                        * field for ExecutedPhases
                        */

                        
                                    protected authclient.java.util.xsd.Iterator localExecutedPhases ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExecutedPhasesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Iterator
                           */
                           public  authclient.java.util.xsd.Iterator getExecutedPhases(){
                               return localExecutedPhases;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ExecutedPhases
                               */
                               public void setExecutedPhases(authclient.java.util.xsd.Iterator param){
                            localExecutedPhasesTracker = true;
                                   
                                            this.localExecutedPhases=param;
                                    

                               }
                            

                        /**
                        * field for ExecutedPhasesExplicit
                        */

                        
                                    protected authclient.java.util.xsd.LinkedList localExecutedPhasesExplicit ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExecutedPhasesExplicitTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.LinkedList
                           */
                           public  authclient.java.util.xsd.LinkedList getExecutedPhasesExplicit(){
                               return localExecutedPhasesExplicit;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ExecutedPhasesExplicit
                               */
                               public void setExecutedPhasesExplicit(authclient.java.util.xsd.LinkedList param){
                            localExecutedPhasesExplicitTracker = true;
                                   
                                            this.localExecutedPhasesExplicit=param;
                                    

                               }
                            

                        /**
                        * field for ExecutionChain
                        */

                        
                                    protected java.lang.Object localExecutionChain ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExecutionChainTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getExecutionChain(){
                               return localExecutionChain;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ExecutionChain
                               */
                               public void setExecutionChain(java.lang.Object param){
                            localExecutionChainTracker = true;
                                   
                                            this.localExecutionChain=param;
                                    

                               }
                            

                        /**
                        * field for FailureReason
                        */

                        
                                    protected org.apache.axiom.om.OMElement localFailureReason ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFailureReasonTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axiom.om.OMElement
                           */
                           public  org.apache.axiom.om.OMElement getFailureReason(){
                               return localFailureReason;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FailureReason
                               */
                               public void setFailureReason(org.apache.axiom.om.OMElement param){
                            localFailureReasonTracker = true;
                                   
                                            this.localFailureReason=param;
                                    

                               }
                            

                        /**
                        * field for Fault
                        */

                        
                                    protected boolean localFault ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getFault(){
                               return localFault;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Fault
                               */
                               public void setFault(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localFaultTracker =
                                       true;
                                   
                                            this.localFault=param;
                                    

                               }
                            

                        /**
                        * field for FaultTo
                        */

                        
                                    protected org.apache.axis2.addressing.xsd.EndpointReference localFaultTo ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFaultToTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.addressing.xsd.EndpointReference
                           */
                           public  org.apache.axis2.addressing.xsd.EndpointReference getFaultTo(){
                               return localFaultTo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FaultTo
                               */
                               public void setFaultTo(org.apache.axis2.addressing.xsd.EndpointReference param){
                            localFaultToTracker = true;
                                   
                                            this.localFaultTo=param;
                                    

                               }
                            

                        /**
                        * field for From
                        */

                        
                                    protected org.apache.axis2.addressing.xsd.EndpointReference localFrom ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFromTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.addressing.xsd.EndpointReference
                           */
                           public  org.apache.axis2.addressing.xsd.EndpointReference getFrom(){
                               return localFrom;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param From
                               */
                               public void setFrom(org.apache.axis2.addressing.xsd.EndpointReference param){
                            localFromTracker = true;
                                   
                                            this.localFrom=param;
                                    

                               }
                            

                        /**
                        * field for HeaderPresent
                        */

                        
                                    protected boolean localHeaderPresent ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localHeaderPresentTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getHeaderPresent(){
                               return localHeaderPresent;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param HeaderPresent
                               */
                               public void setHeaderPresent(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localHeaderPresentTracker =
                                       true;
                                   
                                            this.localHeaderPresent=param;
                                    

                               }
                            

                        /**
                        * field for InboundContentLength
                        */

                        
                                    protected long localInboundContentLength ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInboundContentLengthTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getInboundContentLength(){
                               return localInboundContentLength;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InboundContentLength
                               */
                               public void setInboundContentLength(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localInboundContentLengthTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localInboundContentLength=param;
                                    

                               }
                            

                        /**
                        * field for IncomingTransportName
                        */

                        
                                    protected java.lang.String localIncomingTransportName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localIncomingTransportNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getIncomingTransportName(){
                               return localIncomingTransportName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param IncomingTransportName
                               */
                               public void setIncomingTransportName(java.lang.String param){
                            localIncomingTransportNameTracker = true;
                                   
                                            this.localIncomingTransportName=param;
                                    

                               }
                            

                        /**
                        * field for IsSOAP11Explicit
                        */

                        
                                    protected boolean localIsSOAP11Explicit ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localIsSOAP11ExplicitTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getIsSOAP11Explicit(){
                               return localIsSOAP11Explicit;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param IsSOAP11Explicit
                               */
                               public void setIsSOAP11Explicit(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localIsSOAP11ExplicitTracker =
                                       true;
                                   
                                            this.localIsSOAP11Explicit=param;
                                    

                               }
                            

                        /**
                        * field for LogCorrelationID
                        */

                        
                                    protected java.lang.String localLogCorrelationID ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLogCorrelationIDTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getLogCorrelationID(){
                               return localLogCorrelationID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param LogCorrelationID
                               */
                               public void setLogCorrelationID(java.lang.String param){
                            localLogCorrelationIDTracker = true;
                                   
                                            this.localLogCorrelationID=param;
                                    

                               }
                            

                        /**
                        * field for LogIDString
                        */

                        
                                    protected java.lang.String localLogIDString ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLogIDStringTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getLogIDString(){
                               return localLogIDString;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param LogIDString
                               */
                               public void setLogIDString(java.lang.String param){
                            localLogIDStringTracker = true;
                                   
                                            this.localLogIDString=param;
                                    

                               }
                            

                        /**
                        * field for MessageID
                        */

                        
                                    protected java.lang.String localMessageID ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessageIDTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getMessageID(){
                               return localMessageID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param MessageID
                               */
                               public void setMessageID(java.lang.String param){
                            localMessageIDTracker = true;
                                   
                                            this.localMessageID=param;
                                    

                               }
                            

                        /**
                        * field for NewThreadRequired
                        */

                        
                                    protected boolean localNewThreadRequired ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewThreadRequiredTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getNewThreadRequired(){
                               return localNewThreadRequired;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewThreadRequired
                               */
                               public void setNewThreadRequired(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localNewThreadRequiredTracker =
                                       true;
                                   
                                            this.localNewThreadRequired=param;
                                    

                               }
                            

                        /**
                        * field for OperationContext
                        */

                        
                                    protected org.apache.axis2.context.xsd.OperationContext localOperationContext ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOperationContextTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.context.xsd.OperationContext
                           */
                           public  org.apache.axis2.context.xsd.OperationContext getOperationContext(){
                               return localOperationContext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OperationContext
                               */
                               public void setOperationContext(org.apache.axis2.context.xsd.OperationContext param){
                            localOperationContextTracker = true;
                                   
                                            this.localOperationContext=param;
                                    

                               }
                            

                        /**
                        * field for Options
                        */

                        
                                    protected org.apache.axis2.client.xsd.Options localOptions ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOptionsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.client.xsd.Options
                           */
                           public  org.apache.axis2.client.xsd.Options getOptions(){
                               return localOptions;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Options
                               */
                               public void setOptions(org.apache.axis2.client.xsd.Options param){
                            localOptionsTracker = true;
                                   
                                            this.localOptions=param;
                                    

                               }
                            

                        /**
                        * field for OptionsExplicit
                        */

                        
                                    protected org.apache.axis2.client.xsd.Options localOptionsExplicit ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOptionsExplicitTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.client.xsd.Options
                           */
                           public  org.apache.axis2.client.xsd.Options getOptionsExplicit(){
                               return localOptionsExplicit;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OptionsExplicit
                               */
                               public void setOptionsExplicit(org.apache.axis2.client.xsd.Options param){
                            localOptionsExplicitTracker = true;
                                   
                                            this.localOptionsExplicit=param;
                                    

                               }
                            

                        /**
                        * field for OutputWritten
                        */

                        
                                    protected boolean localOutputWritten ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOutputWrittenTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getOutputWritten(){
                               return localOutputWritten;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OutputWritten
                               */
                               public void setOutputWritten(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localOutputWrittenTracker =
                                       true;
                                   
                                            this.localOutputWritten=param;
                                    

                               }
                            

                        /**
                        * field for Paused
                        */

                        
                                    protected boolean localPaused ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPausedTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getPaused(){
                               return localPaused;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Paused
                               */
                               public void setPaused(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localPausedTracker =
                                       true;
                                   
                                            this.localPaused=param;
                                    

                               }
                            

                        /**
                        * field for ProcessingFault
                        */

                        
                                    protected boolean localProcessingFault ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localProcessingFaultTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getProcessingFault(){
                               return localProcessingFault;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ProcessingFault
                               */
                               public void setProcessingFault(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localProcessingFaultTracker =
                                       true;
                                   
                                            this.localProcessingFault=param;
                                    

                               }
                            

                        /**
                        * field for Properties
                        */

                        
                                    protected authclient.java.util.xsd.Map localProperties ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPropertiesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Map
                           */
                           public  authclient.java.util.xsd.Map getProperties(){
                               return localProperties;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Properties
                               */
                               public void setProperties(authclient.java.util.xsd.Map param){
                            localPropertiesTracker = true;
                                   
                                            this.localProperties=param;
                                    

                               }
                            

                        /**
                        * field for RelatesTo
                        */

                        
                                    protected org.apache.axis2.addressing.xsd.RelatesTo localRelatesTo ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRelatesToTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.addressing.xsd.RelatesTo
                           */
                           public  org.apache.axis2.addressing.xsd.RelatesTo getRelatesTo(){
                               return localRelatesTo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param RelatesTo
                               */
                               public void setRelatesTo(org.apache.axis2.addressing.xsd.RelatesTo param){
                            localRelatesToTracker = true;
                                   
                                            this.localRelatesTo=param;
                                    

                               }
                            

                        /**
                        * field for Relationships
                        * This was an Array!
                        */

                        
                                    protected org.apache.axis2.addressing.xsd.RelatesTo[] localRelationships ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRelationshipsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.addressing.xsd.RelatesTo[]
                           */
                           public  org.apache.axis2.addressing.xsd.RelatesTo[] getRelationships(){
                               return localRelationships;
                           }

                           
                        


                               
                              /**
                               * validate the array for Relationships
                               */
                              protected void validateRelationships(org.apache.axis2.addressing.xsd.RelatesTo[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Relationships
                              */
                              public void setRelationships(org.apache.axis2.addressing.xsd.RelatesTo[] param){
                              
                                   validateRelationships(param);

                               localRelationshipsTracker = true;
                                      
                                      this.localRelationships=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param org.apache.axis2.addressing.xsd.RelatesTo
                             */
                             public void addRelationships(org.apache.axis2.addressing.xsd.RelatesTo param){
                                   if (localRelationships == null){
                                   localRelationships = new org.apache.axis2.addressing.xsd.RelatesTo[]{};
                                   }

                            
                                 //update the setting tracker
                                localRelationshipsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localRelationships);
                               list.add(param);
                               this.localRelationships =
                             (org.apache.axis2.addressing.xsd.RelatesTo[])list.toArray(
                            new org.apache.axis2.addressing.xsd.RelatesTo[list.size()]);

                             }
                             

                        /**
                        * field for ReplyTo
                        */

                        
                                    protected org.apache.axis2.addressing.xsd.EndpointReference localReplyTo ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localReplyToTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.addressing.xsd.EndpointReference
                           */
                           public  org.apache.axis2.addressing.xsd.EndpointReference getReplyTo(){
                               return localReplyTo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ReplyTo
                               */
                               public void setReplyTo(org.apache.axis2.addressing.xsd.EndpointReference param){
                            localReplyToTracker = true;
                                   
                                            this.localReplyTo=param;
                                    

                               }
                            

                        /**
                        * field for ResponseWritten
                        */

                        
                                    protected boolean localResponseWritten ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localResponseWrittenTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getResponseWritten(){
                               return localResponseWritten;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ResponseWritten
                               */
                               public void setResponseWritten(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localResponseWrittenTracker =
                                       true;
                                   
                                            this.localResponseWritten=param;
                                    

                               }
                            

                        /**
                        * field for RootContext
                        */

                        
                                    protected org.apache.axis2.context.xsd.ConfigurationContext localRootContext ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRootContextTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.context.xsd.ConfigurationContext
                           */
                           public  org.apache.axis2.context.xsd.ConfigurationContext getRootContext(){
                               return localRootContext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param RootContext
                               */
                               public void setRootContext(org.apache.axis2.context.xsd.ConfigurationContext param){
                            localRootContextTracker = true;
                                   
                                            this.localRootContext=param;
                                    

                               }
                            

                        /**
                        * field for SelfManagedDataMapExplicit
                        */

                        
                                    protected authclient.java.util.xsd.LinkedHashMap localSelfManagedDataMapExplicit ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSelfManagedDataMapExplicitTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.LinkedHashMap
                           */
                           public  authclient.java.util.xsd.LinkedHashMap getSelfManagedDataMapExplicit(){
                               return localSelfManagedDataMapExplicit;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SelfManagedDataMapExplicit
                               */
                               public void setSelfManagedDataMapExplicit(authclient.java.util.xsd.LinkedHashMap param){
                            localSelfManagedDataMapExplicitTracker = true;
                                   
                                            this.localSelfManagedDataMapExplicit=param;
                                    

                               }
                            

                        /**
                        * field for ServerSide
                        */

                        
                                    protected boolean localServerSide ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServerSideTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getServerSide(){
                               return localServerSide;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServerSide
                               */
                               public void setServerSide(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localServerSideTracker =
                                       true;
                                   
                                            this.localServerSide=param;
                                    

                               }
                            

                        /**
                        * field for ServiceContext
                        */

                        
                                    protected org.apache.axis2.context.xsd.ServiceContext localServiceContext ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceContextTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.context.xsd.ServiceContext
                           */
                           public  org.apache.axis2.context.xsd.ServiceContext getServiceContext(){
                               return localServiceContext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceContext
                               */
                               public void setServiceContext(org.apache.axis2.context.xsd.ServiceContext param){
                            localServiceContextTracker = true;
                                   
                                            this.localServiceContext=param;
                                    

                               }
                            

                        /**
                        * field for ServiceContextID
                        */

                        
                                    protected java.lang.String localServiceContextID ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceContextIDTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServiceContextID(){
                               return localServiceContextID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceContextID
                               */
                               public void setServiceContextID(java.lang.String param){
                            localServiceContextIDTracker = true;
                                   
                                            this.localServiceContextID=param;
                                    

                               }
                            

                        /**
                        * field for ServiceGroupContext
                        */

                        
                                    protected org.apache.axis2.context.xsd.ServiceGroupContext localServiceGroupContext ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupContextTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.context.xsd.ServiceGroupContext
                           */
                           public  org.apache.axis2.context.xsd.ServiceGroupContext getServiceGroupContext(){
                               return localServiceGroupContext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroupContext
                               */
                               public void setServiceGroupContext(org.apache.axis2.context.xsd.ServiceGroupContext param){
                            localServiceGroupContextTracker = true;
                                   
                                            this.localServiceGroupContext=param;
                                    

                               }
                            

                        /**
                        * field for ServiceGroupContextId
                        */

                        
                                    protected java.lang.String localServiceGroupContextId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupContextIdTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServiceGroupContextId(){
                               return localServiceGroupContextId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroupContextId
                               */
                               public void setServiceGroupContextId(java.lang.String param){
                            localServiceGroupContextIdTracker = true;
                                   
                                            this.localServiceGroupContextId=param;
                                    

                               }
                            

                        /**
                        * field for SessionContext
                        */

                        
                                    protected org.apache.axis2.context.xsd.SessionContext localSessionContext ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSessionContextTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.context.xsd.SessionContext
                           */
                           public  org.apache.axis2.context.xsd.SessionContext getSessionContext(){
                               return localSessionContext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SessionContext
                               */
                               public void setSessionContext(org.apache.axis2.context.xsd.SessionContext param){
                            localSessionContextTracker = true;
                                   
                                            this.localSessionContext=param;
                                    

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
                        * field for To
                        */

                        
                                    protected org.apache.axis2.addressing.xsd.EndpointReference localTo ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localToTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.addressing.xsd.EndpointReference
                           */
                           public  org.apache.axis2.addressing.xsd.EndpointReference getTo(){
                               return localTo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param To
                               */
                               public void setTo(org.apache.axis2.addressing.xsd.EndpointReference param){
                            localToTracker = true;
                                   
                                            this.localTo=param;
                                    

                               }
                            

                        /**
                        * field for TransportIn
                        */

                        
                                    protected org.apache.axis2.description.xsd.TransportInDescription localTransportIn ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransportInTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.TransportInDescription
                           */
                           public  org.apache.axis2.description.xsd.TransportInDescription getTransportIn(){
                               return localTransportIn;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TransportIn
                               */
                               public void setTransportIn(org.apache.axis2.description.xsd.TransportInDescription param){
                            localTransportInTracker = true;
                                   
                                            this.localTransportIn=param;
                                    

                               }
                            

                        /**
                        * field for TransportOut
                        */

                        
                                    protected org.apache.axis2.description.xsd.TransportOutDescription localTransportOut ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransportOutTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.description.xsd.TransportOutDescription
                           */
                           public  org.apache.axis2.description.xsd.TransportOutDescription getTransportOut(){
                               return localTransportOut;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TransportOut
                               */
                               public void setTransportOut(org.apache.axis2.description.xsd.TransportOutDescription param){
                            localTransportOutTracker = true;
                                   
                                            this.localTransportOut=param;
                                    

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
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://context.axis2.apache.org/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":MessageContext",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "MessageContext",
                           xmlWriter);
                   }

               
                   }
                if (localFLOWTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "FLOW", xmlWriter);
                             
                                               if (localFLOW==java.lang.Integer.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("FLOW cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFLOW));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSOAP11Tracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "SOAP11", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("SOAP11 cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSOAP11));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localWSAActionTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "WSAAction", xmlWriter);
                             

                                          if (localWSAAction==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localWSAAction);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localWSAMessageIdTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "WSAMessageId", xmlWriter);
                             

                                          if (localWSAMessageId==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localWSAMessageId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localAttachmentMapTracker){
                                    if (localAttachmentMap==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "attachmentMap", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAttachmentMap.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","attachmentMap"),
                                        xmlWriter);
                                    }
                                } if (localAxisMessageTracker){
                                    if (localAxisMessage==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisMessage", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisMessage.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisMessage"),
                                        xmlWriter);
                                    }
                                } if (localAxisOperationTracker){
                                    if (localAxisOperation==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisOperation", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisOperation.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisOperation"),
                                        xmlWriter);
                                    }
                                } if (localAxisServiceTracker){
                                    if (localAxisService==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisService", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisService.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisService"),
                                        xmlWriter);
                                    }
                                } if (localAxisServiceGroupTracker){
                                    if (localAxisServiceGroup==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisServiceGroup", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisServiceGroup.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisServiceGroup"),
                                        xmlWriter);
                                    }
                                } if (localConfigurationContextTracker){
                                    if (localConfigurationContext==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "configurationContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localConfigurationContext.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","configurationContext"),
                                        xmlWriter);
                                    }
                                } if (localCurrentHandlerIndexTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "currentHandlerIndex", xmlWriter);
                             
                                               if (localCurrentHandlerIndex==java.lang.Integer.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("currentHandlerIndex cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCurrentHandlerIndex));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localCurrentPhaseIndexTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "currentPhaseIndex", xmlWriter);
                             
                                               if (localCurrentPhaseIndex==java.lang.Integer.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("currentPhaseIndex cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCurrentPhaseIndex));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localDoingMTOMTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "doingMTOM", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("doingMTOM cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDoingMTOM));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localDoingRESTTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "doingREST", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("doingREST cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDoingREST));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localDoingSwATracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "doingSwA", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("doingSwA cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDoingSwA));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localEffectivePolicyTracker){
                                    if (localEffectivePolicy==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "effectivePolicy", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localEffectivePolicy.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","effectivePolicy"),
                                        xmlWriter);
                                    }
                                } if (localEnvelopeTracker){
                                    if (localEnvelope==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "envelope", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localEnvelope.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","envelope"),
                                        xmlWriter);
                                    }
                                } if (localExecutedPhasesTracker){
                                    if (localExecutedPhases==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "executedPhases", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localExecutedPhases.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","executedPhases"),
                                        xmlWriter);
                                    }
                                } if (localExecutedPhasesExplicitTracker){
                                    if (localExecutedPhasesExplicit==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "executedPhasesExplicit", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localExecutedPhasesExplicit.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","executedPhasesExplicit"),
                                        xmlWriter);
                                    }
                                } if (localExecutionChainTracker){
                            
                            if (localExecutionChain!=null){
                                if (localExecutionChain instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localExecutionChain).serialize(
                                               new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","executionChain"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://context.axis2.apache.org/xsd", "executionChain", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localExecutionChain, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://context.axis2.apache.org/xsd", "executionChain", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localFailureReasonTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "failureReason", xmlWriter);
                             

                                          if (localFailureReason==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        localFailureReason.serialize(xmlWriter);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFaultTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "fault", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fault cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFault));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFaultToTracker){
                                    if (localFaultTo==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "faultTo", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localFaultTo.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","faultTo"),
                                        xmlWriter);
                                    }
                                } if (localFromTracker){
                                    if (localFrom==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "from", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localFrom.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","from"),
                                        xmlWriter);
                                    }
                                } if (localHeaderPresentTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "headerPresent", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("headerPresent cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localHeaderPresent));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localInboundContentLengthTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "inboundContentLength", xmlWriter);
                             
                                               if (localInboundContentLength==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("inboundContentLength cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInboundContentLength));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localIncomingTransportNameTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "incomingTransportName", xmlWriter);
                             

                                          if (localIncomingTransportName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localIncomingTransportName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localIsSOAP11ExplicitTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "isSOAP11Explicit", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("isSOAP11Explicit cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsSOAP11Explicit));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localLogCorrelationIDTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "logCorrelationID", xmlWriter);
                             

                                          if (localLogCorrelationID==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localLogCorrelationID);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localLogIDStringTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "logIDString", xmlWriter);
                             

                                          if (localLogIDString==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localLogIDString);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localMessageIDTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "messageID", xmlWriter);
                             

                                          if (localMessageID==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localMessageID);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewThreadRequiredTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "newThreadRequired", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("newThreadRequired cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewThreadRequired));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOperationContextTracker){
                                    if (localOperationContext==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "operationContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localOperationContext.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","operationContext"),
                                        xmlWriter);
                                    }
                                } if (localOptionsTracker){
                                    if (localOptions==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "options", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localOptions.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","options"),
                                        xmlWriter);
                                    }
                                } if (localOptionsExplicitTracker){
                                    if (localOptionsExplicit==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "optionsExplicit", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localOptionsExplicit.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","optionsExplicit"),
                                        xmlWriter);
                                    }
                                } if (localOutputWrittenTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "outputWritten", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("outputWritten cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOutputWritten));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPausedTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "paused", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("paused cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPaused));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localProcessingFaultTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "processingFault", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("processingFault cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProcessingFault));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPropertiesTracker){
                                    if (localProperties==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "properties", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localProperties.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","properties"),
                                        xmlWriter);
                                    }
                                } if (localRelatesToTracker){
                                    if (localRelatesTo==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "relatesTo", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localRelatesTo.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","relatesTo"),
                                        xmlWriter);
                                    }
                                } if (localRelationshipsTracker){
                                       if (localRelationships!=null){
                                            for (int i = 0;i < localRelationships.length;i++){
                                                if (localRelationships[i] != null){
                                                 localRelationships[i].serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","relationships"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                            writeStartElement(null, "http://context.axis2.apache.org/xsd", "relationships", xmlWriter);

                                                           // write the nil attribute
                                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                           xmlWriter.writeEndElement();
                                                    
                                                }

                                            }
                                     } else {
                                        
                                                writeStartElement(null, "http://context.axis2.apache.org/xsd", "relationships", xmlWriter);

                                               // write the nil attribute
                                               writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                               xmlWriter.writeEndElement();
                                        
                                    }
                                 } if (localReplyToTracker){
                                    if (localReplyTo==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "replyTo", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localReplyTo.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","replyTo"),
                                        xmlWriter);
                                    }
                                } if (localResponseWrittenTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "responseWritten", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("responseWritten cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localResponseWritten));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localRootContextTracker){
                                    if (localRootContext==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "rootContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localRootContext.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","rootContext"),
                                        xmlWriter);
                                    }
                                } if (localSelfManagedDataMapExplicitTracker){
                                    if (localSelfManagedDataMapExplicit==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "selfManagedDataMapExplicit", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localSelfManagedDataMapExplicit.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","selfManagedDataMapExplicit"),
                                        xmlWriter);
                                    }
                                } if (localServerSideTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serverSide", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("serverSide cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServerSide));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceContextTracker){
                                    if (localServiceContext==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localServiceContext.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceContext"),
                                        xmlWriter);
                                    }
                                } if (localServiceContextIDTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceContextID", xmlWriter);
                             

                                          if (localServiceContextID==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServiceContextID);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceGroupContextTracker){
                                    if (localServiceGroupContext==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localServiceGroupContext.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContext"),
                                        xmlWriter);
                                    }
                                } if (localServiceGroupContextIdTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceGroupContextId", xmlWriter);
                             

                                          if (localServiceGroupContextId==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServiceGroupContextId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSessionContextTracker){
                                    if (localSessionContext==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "sessionContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localSessionContext.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","sessionContext"),
                                        xmlWriter);
                                    }
                                } if (localSoapActionTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "soapAction", xmlWriter);
                             

                                          if (localSoapAction==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSoapAction);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localToTracker){
                                    if (localTo==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "to", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTo.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","to"),
                                        xmlWriter);
                                    }
                                } if (localTransportInTracker){
                                    if (localTransportIn==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "transportIn", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTransportIn.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","transportIn"),
                                        xmlWriter);
                                    }
                                } if (localTransportOutTracker){
                                    if (localTransportOut==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "transportOut", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTransportOut.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","transportOut"),
                                        xmlWriter);
                                    }
                                }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://context.axis2.apache.org/xsd")){
                return "ns10";
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

                 if (localFLOWTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "FLOW"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFLOW));
                            } if (localSOAP11Tracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "SOAP11"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSOAP11));
                            } if (localWSAActionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "WSAAction"));
                                 
                                         elementList.add(localWSAAction==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWSAAction));
                                    } if (localWSAMessageIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "WSAMessageId"));
                                 
                                         elementList.add(localWSAMessageId==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWSAMessageId));
                                    } if (localAttachmentMapTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "attachmentMap"));
                            
                            
                                    elementList.add(localAttachmentMap==null?null:
                                    localAttachmentMap);
                                } if (localAxisMessageTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "axisMessage"));
                            
                            
                                    elementList.add(localAxisMessage==null?null:
                                    localAxisMessage);
                                } if (localAxisOperationTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "axisOperation"));
                            
                            
                                    elementList.add(localAxisOperation==null?null:
                                    localAxisOperation);
                                } if (localAxisServiceTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "axisService"));
                            
                            
                                    elementList.add(localAxisService==null?null:
                                    localAxisService);
                                } if (localAxisServiceGroupTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "axisServiceGroup"));
                            
                            
                                    elementList.add(localAxisServiceGroup==null?null:
                                    localAxisServiceGroup);
                                } if (localConfigurationContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "configurationContext"));
                            
                            
                                    elementList.add(localConfigurationContext==null?null:
                                    localConfigurationContext);
                                } if (localCurrentHandlerIndexTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "currentHandlerIndex"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCurrentHandlerIndex));
                            } if (localCurrentPhaseIndexTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "currentPhaseIndex"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCurrentPhaseIndex));
                            } if (localDoingMTOMTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "doingMTOM"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDoingMTOM));
                            } if (localDoingRESTTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "doingREST"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDoingREST));
                            } if (localDoingSwATracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "doingSwA"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDoingSwA));
                            } if (localEffectivePolicyTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "effectivePolicy"));
                            
                            
                                    elementList.add(localEffectivePolicy==null?null:
                                    localEffectivePolicy);
                                } if (localEnvelopeTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "envelope"));
                            
                            
                                    elementList.add(localEnvelope==null?null:
                                    localEnvelope);
                                } if (localExecutedPhasesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "executedPhases"));
                            
                            
                                    elementList.add(localExecutedPhases==null?null:
                                    localExecutedPhases);
                                } if (localExecutedPhasesExplicitTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "executedPhasesExplicit"));
                            
                            
                                    elementList.add(localExecutedPhasesExplicit==null?null:
                                    localExecutedPhasesExplicit);
                                } if (localExecutionChainTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "executionChain"));
                            
                            
                                    elementList.add(localExecutionChain==null?null:
                                    localExecutionChain);
                                } if (localFailureReasonTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "failureReason"));
                                 
                                         elementList.add(localFailureReason==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFailureReason));
                                    } if (localFaultTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "fault"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFault));
                            } if (localFaultToTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "faultTo"));
                            
                            
                                    elementList.add(localFaultTo==null?null:
                                    localFaultTo);
                                } if (localFromTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "from"));
                            
                            
                                    elementList.add(localFrom==null?null:
                                    localFrom);
                                } if (localHeaderPresentTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "headerPresent"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localHeaderPresent));
                            } if (localInboundContentLengthTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "inboundContentLength"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInboundContentLength));
                            } if (localIncomingTransportNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "incomingTransportName"));
                                 
                                         elementList.add(localIncomingTransportName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIncomingTransportName));
                                    } if (localIsSOAP11ExplicitTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "isSOAP11Explicit"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsSOAP11Explicit));
                            } if (localLogCorrelationIDTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "logCorrelationID"));
                                 
                                         elementList.add(localLogCorrelationID==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLogCorrelationID));
                                    } if (localLogIDStringTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "logIDString"));
                                 
                                         elementList.add(localLogIDString==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLogIDString));
                                    } if (localMessageIDTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "messageID"));
                                 
                                         elementList.add(localMessageID==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessageID));
                                    } if (localNewThreadRequiredTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "newThreadRequired"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewThreadRequired));
                            } if (localOperationContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "operationContext"));
                            
                            
                                    elementList.add(localOperationContext==null?null:
                                    localOperationContext);
                                } if (localOptionsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "options"));
                            
                            
                                    elementList.add(localOptions==null?null:
                                    localOptions);
                                } if (localOptionsExplicitTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "optionsExplicit"));
                            
                            
                                    elementList.add(localOptionsExplicit==null?null:
                                    localOptionsExplicit);
                                } if (localOutputWrittenTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "outputWritten"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOutputWritten));
                            } if (localPausedTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "paused"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPaused));
                            } if (localProcessingFaultTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "processingFault"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProcessingFault));
                            } if (localPropertiesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "properties"));
                            
                            
                                    elementList.add(localProperties==null?null:
                                    localProperties);
                                } if (localRelatesToTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "relatesTo"));
                            
                            
                                    elementList.add(localRelatesTo==null?null:
                                    localRelatesTo);
                                } if (localRelationshipsTracker){
                             if (localRelationships!=null) {
                                 for (int i = 0;i < localRelationships.length;i++){

                                    if (localRelationships[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                          "relationships"));
                                         elementList.add(localRelationships[i]);
                                    } else {
                                        
                                                elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                          "relationships"));
                                                elementList.add(null);
                                            
                                    }

                                 }
                             } else {
                                 
                                        elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                          "relationships"));
                                        elementList.add(localRelationships);
                                    
                             }

                        } if (localReplyToTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "replyTo"));
                            
                            
                                    elementList.add(localReplyTo==null?null:
                                    localReplyTo);
                                } if (localResponseWrittenTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "responseWritten"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localResponseWritten));
                            } if (localRootContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "rootContext"));
                            
                            
                                    elementList.add(localRootContext==null?null:
                                    localRootContext);
                                } if (localSelfManagedDataMapExplicitTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "selfManagedDataMapExplicit"));
                            
                            
                                    elementList.add(localSelfManagedDataMapExplicit==null?null:
                                    localSelfManagedDataMapExplicit);
                                } if (localServerSideTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serverSide"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServerSide));
                            } if (localServiceContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceContext"));
                            
                            
                                    elementList.add(localServiceContext==null?null:
                                    localServiceContext);
                                } if (localServiceContextIDTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceContextID"));
                                 
                                         elementList.add(localServiceContextID==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceContextID));
                                    } if (localServiceGroupContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceGroupContext"));
                            
                            
                                    elementList.add(localServiceGroupContext==null?null:
                                    localServiceGroupContext);
                                } if (localServiceGroupContextIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceGroupContextId"));
                                 
                                         elementList.add(localServiceGroupContextId==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextId));
                                    } if (localSessionContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "sessionContext"));
                            
                            
                                    elementList.add(localSessionContext==null?null:
                                    localSessionContext);
                                } if (localSoapActionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "soapAction"));
                                 
                                         elementList.add(localSoapAction==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSoapAction));
                                    } if (localToTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "to"));
                            
                            
                                    elementList.add(localTo==null?null:
                                    localTo);
                                } if (localTransportInTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "transportIn"));
                            
                            
                                    elementList.add(localTransportIn==null?null:
                                    localTransportIn);
                                } if (localTransportOutTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "transportOut"));
                            
                            
                                    elementList.add(localTransportOut==null?null:
                                    localTransportOut);
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
        public static MessageContext parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            MessageContext object =
                new MessageContext();

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
                    
                            if (!"MessageContext".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (MessageContext)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list41 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","FLOW").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFLOW(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFLOW(java.lang.Integer.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","SOAP11").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSOAP11(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","WSAAction").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setWSAAction(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","WSAMessageId").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setWSAMessageId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","attachmentMap").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setAttachmentMap(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setAttachmentMap(org.apache.axiom.attachments.xsd.Attachments.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisMessage").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setAxisMessage(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setAxisMessage(org.apache.axis2.description.xsd.AxisMessage.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisOperation").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setAxisOperation(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setAxisOperation(org.apache.axis2.description.xsd.AxisOperation.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisService").equals(reader.getName())){
                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisServiceGroup").equals(reader.getName())){
                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","configurationContext").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setConfigurationContext(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setConfigurationContext(org.apache.axis2.context.xsd.ConfigurationContext.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","currentHandlerIndex").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCurrentHandlerIndex(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setCurrentHandlerIndex(java.lang.Integer.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","currentPhaseIndex").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCurrentPhaseIndex(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setCurrentPhaseIndex(java.lang.Integer.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","doingMTOM").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setDoingMTOM(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","doingREST").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setDoingREST(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","doingSwA").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setDoingSwA(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","effectivePolicy").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setEffectivePolicy(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setEffectivePolicy(org.apache.neethi.xsd.Policy.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","envelope").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setEnvelope(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setEnvelope(org.apache.axiom.soap.xsd.SOAPEnvelope.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","executedPhases").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setExecutedPhases(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setExecutedPhases(authclient.java.util.xsd.Iterator.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","executedPhasesExplicit").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setExecutedPhasesExplicit(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setExecutedPhasesExplicit(authclient.java.util.xsd.LinkedList.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","executionChain").equals(reader.getName())){
                                
                                     object.setExecutionChain(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                   if (reader.isStartElement()){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                                org.apache.axiom.om.OMFactory fac = org.apache.axiom.om.OMAbstractFactory.getOMFactory();
                                                org.apache.axiom.om.OMNamespace omNs = fac.createOMNamespace("http://context.axis2.apache.org/xsd", "");
                                                org.apache.axiom.om.OMElement _valueFailureReason = fac.createOMElement("failureReason", omNs);
                                                _valueFailureReason.addChild(fac.createOMText(_valueFailureReason, content));
                                                object.setFailureReason(_valueFailureReason);
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","fault").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFault(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","faultTo").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setFaultTo(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setFaultTo(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","from").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setFrom(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setFrom(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","headerPresent").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setHeaderPresent(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","inboundContentLength").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setInboundContentLength(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setInboundContentLength(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","incomingTransportName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setIncomingTransportName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","isSOAP11Explicit").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setIsSOAP11Explicit(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","logCorrelationID").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLogCorrelationID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","logIDString").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLogIDString(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","messageID").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setMessageID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","newThreadRequired").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewThreadRequired(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","operationContext").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setOperationContext(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setOperationContext(org.apache.axis2.context.xsd.OperationContext.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","options").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setOptions(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setOptions(org.apache.axis2.client.xsd.Options.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","optionsExplicit").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setOptionsExplicit(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setOptionsExplicit(org.apache.axis2.client.xsd.Options.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","outputWritten").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOutputWritten(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","paused").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPaused(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","processingFault").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setProcessingFault(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","properties").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setProperties(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setProperties(authclient.java.util.xsd.Map.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","relatesTo").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setRelatesTo(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setRelatesTo(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","relationships").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list41.add(null);
                                                              reader.next();
                                                          } else {
                                                        list41.add(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
                                                                }
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone41 = false;
                                                        while(!loopDone41){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone41 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","relationships").equals(reader.getName())){
                                                                    
                                                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                                          list41.add(null);
                                                                          reader.next();
                                                                      } else {
                                                                    list41.add(org.apache.axis2.addressing.xsd.RelatesTo.Factory.parse(reader));
                                                                        }
                                                                }else{
                                                                    loopDone41 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setRelationships((org.apache.axis2.addressing.xsd.RelatesTo[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                org.apache.axis2.addressing.xsd.RelatesTo.class,
                                                                list41));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","replyTo").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setReplyTo(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setReplyTo(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","responseWritten").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setResponseWritten(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","rootContext").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setRootContext(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setRootContext(org.apache.axis2.context.xsd.ConfigurationContext.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","selfManagedDataMapExplicit").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setSelfManagedDataMapExplicit(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setSelfManagedDataMapExplicit(authclient.java.util.xsd.LinkedHashMap.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serverSide").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServerSide(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceContext").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setServiceContext(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setServiceContext(org.apache.axis2.context.xsd.ServiceContext.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceContextID").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceContextID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContext").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setServiceGroupContext(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setServiceGroupContext(org.apache.axis2.context.xsd.ServiceGroupContext.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContextId").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceGroupContextId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","sessionContext").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setSessionContext(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setSessionContext(org.apache.axis2.context.xsd.SessionContext.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","soapAction").equals(reader.getName())){
                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","to").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTo(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTo(org.apache.axis2.addressing.xsd.EndpointReference.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","transportIn").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTransportIn(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTransportIn(org.apache.axis2.description.xsd.TransportInDescription.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","transportOut").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTransportOut(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTransportOut(org.apache.axis2.description.xsd.TransportOutDescription.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
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
           
    