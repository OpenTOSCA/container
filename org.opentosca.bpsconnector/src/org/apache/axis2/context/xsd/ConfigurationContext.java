
/**
 * ConfigurationContext.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.context.xsd;
            

            /**
            *  ConfigurationContext bean class
            */
        
        public  class ConfigurationContext
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = ConfigurationContext
                Namespace URI = http://context.axis2.apache.org/xsd
                Namespace Prefix = ns10
                */
            

                        /**
                        * field for AnyOperationContextRegistered
                        */

                        
                                    protected boolean localAnyOperationContextRegistered ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAnyOperationContextRegisteredTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getAnyOperationContextRegistered(){
                               return localAnyOperationContextRegistered;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AnyOperationContextRegistered
                               */
                               public void setAnyOperationContextRegistered(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localAnyOperationContextRegisteredTracker =
                                       true;
                                   
                                            this.localAnyOperationContextRegistered=param;
                                    

                               }
                            

                        /**
                        * field for AxisConfiguration
                        */

                        
                                    protected org.apache.axis2.engine.xsd.AxisConfiguration localAxisConfiguration ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAxisConfigurationTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.AxisConfiguration
                           */
                           public  org.apache.axis2.engine.xsd.AxisConfiguration getAxisConfiguration(){
                               return localAxisConfiguration;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AxisConfiguration
                               */
                               public void setAxisConfiguration(org.apache.axis2.engine.xsd.AxisConfiguration param){
                            localAxisConfigurationTracker = true;
                                   
                                            this.localAxisConfiguration=param;
                                    

                               }
                            

                        /**
                        * field for ContextRoot
                        */

                        
                                    protected java.lang.String localContextRoot ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localContextRootTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getContextRoot(){
                               return localContextRoot;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ContextRoot
                               */
                               public void setContextRoot(java.lang.String param){
                            localContextRootTracker = true;
                                   
                                            this.localContextRoot=param;
                                    

                               }
                            

                        /**
                        * field for ListenerManager
                        */

                        
                                    protected org.apache.axis2.engine.xsd.ListenerManager localListenerManager ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localListenerManagerTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.ListenerManager
                           */
                           public  org.apache.axis2.engine.xsd.ListenerManager getListenerManager(){
                               return localListenerManager;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ListenerManager
                               */
                               public void setListenerManager(org.apache.axis2.engine.xsd.ListenerManager param){
                            localListenerManagerTracker = true;
                                   
                                            this.localListenerManager=param;
                                    

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
                        * field for ServiceContextPath
                        */

                        
                                    protected java.lang.String localServiceContextPath ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceContextPathTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServiceContextPath(){
                               return localServiceContextPath;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceContextPath
                               */
                               public void setServiceContextPath(java.lang.String param){
                            localServiceContextPathTracker = true;
                                   
                                            this.localServiceContextPath=param;
                                    

                               }
                            

                        /**
                        * field for ServiceGroupContextIDs
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localServiceGroupContextIDs ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupContextIDsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getServiceGroupContextIDs(){
                               return localServiceGroupContextIDs;
                           }

                           
                        


                               
                              /**
                               * validate the array for ServiceGroupContextIDs
                               */
                              protected void validateServiceGroupContextIDs(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ServiceGroupContextIDs
                              */
                              public void setServiceGroupContextIDs(java.lang.String[] param){
                              
                                   validateServiceGroupContextIDs(param);

                               localServiceGroupContextIDsTracker = true;
                                      
                                      this.localServiceGroupContextIDs=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addServiceGroupContextIDs(java.lang.String param){
                                   if (localServiceGroupContextIDs == null){
                                   localServiceGroupContextIDs = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localServiceGroupContextIDsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localServiceGroupContextIDs);
                               list.add(param);
                               this.localServiceGroupContextIDs =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for ServiceGroupContextTimeoutInterval
                        */

                        
                                    protected long localServiceGroupContextTimeoutInterval ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupContextTimeoutIntervalTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getServiceGroupContextTimeoutInterval(){
                               return localServiceGroupContextTimeoutInterval;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroupContextTimeoutInterval
                               */
                               public void setServiceGroupContextTimeoutInterval(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localServiceGroupContextTimeoutIntervalTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localServiceGroupContextTimeoutInterval=param;
                                    

                               }
                            

                        /**
                        * field for ServiceGroupContextTimoutInterval
                        */

                        
                                    protected long localServiceGroupContextTimoutInterval ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupContextTimoutIntervalTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getServiceGroupContextTimoutInterval(){
                               return localServiceGroupContextTimoutInterval;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroupContextTimoutInterval
                               */
                               public void setServiceGroupContextTimoutInterval(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localServiceGroupContextTimoutIntervalTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localServiceGroupContextTimoutInterval=param;
                                    

                               }
                            

                        /**
                        * field for ServiceGroupContexts
                        */

                        
                                    protected java.lang.Object localServiceGroupContexts ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupContextsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getServiceGroupContexts(){
                               return localServiceGroupContexts;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroupContexts
                               */
                               public void setServiceGroupContexts(java.lang.Object param){
                            localServiceGroupContextsTracker = true;
                                   
                                            this.localServiceGroupContexts=param;
                                    

                               }
                            

                        /**
                        * field for ServicePath
                        */

                        
                                    protected java.lang.String localServicePath ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServicePathTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServicePath(){
                               return localServicePath;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServicePath
                               */
                               public void setServicePath(java.lang.String param){
                            localServicePathTracker = true;
                                   
                                            this.localServicePath=param;
                                    

                               }
                            

                        /**
                        * field for ThreadPool
                        */

                        
                                    protected org.apache.axis2.util.threadpool.xsd.ThreadFactory localThreadPool ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localThreadPoolTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.util.threadpool.xsd.ThreadFactory
                           */
                           public  org.apache.axis2.util.threadpool.xsd.ThreadFactory getThreadPool(){
                               return localThreadPool;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ThreadPool
                               */
                               public void setThreadPool(org.apache.axis2.util.threadpool.xsd.ThreadFactory param){
                            localThreadPoolTracker = true;
                                   
                                            this.localThreadPool=param;
                                    

                               }
                            

                        /**
                        * field for TransportManager
                        */

                        
                                    protected org.apache.axis2.engine.xsd.ListenerManager localTransportManager ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTransportManagerTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.engine.xsd.ListenerManager
                           */
                           public  org.apache.axis2.engine.xsd.ListenerManager getTransportManager(){
                               return localTransportManager;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TransportManager
                               */
                               public void setTransportManager(org.apache.axis2.engine.xsd.ListenerManager param){
                            localTransportManagerTracker = true;
                                   
                                            this.localTransportManager=param;
                                    

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
                           namespacePrefix+":ConfigurationContext",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "ConfigurationContext",
                           xmlWriter);
                   }

               
                   }
                if (localAnyOperationContextRegisteredTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "anyOperationContextRegistered", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("anyOperationContextRegistered cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAnyOperationContextRegistered));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localAxisConfigurationTracker){
                                    if (localAxisConfiguration==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisConfiguration", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisConfiguration.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisConfiguration"),
                                        xmlWriter);
                                    }
                                } if (localContextRootTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "contextRoot", xmlWriter);
                             

                                          if (localContextRoot==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localContextRoot);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localListenerManagerTracker){
                                    if (localListenerManager==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "listenerManager", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localListenerManager.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","listenerManager"),
                                        xmlWriter);
                                    }
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
                                } if (localServiceContextPathTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceContextPath", xmlWriter);
                             

                                          if (localServiceContextPath==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServiceContextPath);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceGroupContextIDsTracker){
                             if (localServiceGroupContextIDs!=null) {
                                   namespace = "http://context.axis2.apache.org/xsd";
                                   for (int i = 0;i < localServiceGroupContextIDs.length;i++){
                                        
                                            if (localServiceGroupContextIDs[i] != null){
                                        
                                                writeStartElement(null, namespace, "serviceGroupContextIDs", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextIDs[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://context.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "serviceGroupContextIDs", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContextIDs", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localServiceGroupContextTimeoutIntervalTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceGroupContextTimeoutInterval", xmlWriter);
                             
                                               if (localServiceGroupContextTimeoutInterval==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("serviceGroupContextTimeoutInterval cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextTimeoutInterval));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceGroupContextTimoutIntervalTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceGroupContextTimoutInterval", xmlWriter);
                             
                                               if (localServiceGroupContextTimoutInterval==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("serviceGroupContextTimoutInterval cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextTimoutInterval));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceGroupContextsTracker){
                            
                            if (localServiceGroupContexts!=null){
                                if (localServiceGroupContexts instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localServiceGroupContexts).serialize(
                                               new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContexts"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContexts", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localServiceGroupContexts, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://context.axis2.apache.org/xsd", "serviceGroupContexts", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localServicePathTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "servicePath", xmlWriter);
                             

                                          if (localServicePath==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServicePath);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localThreadPoolTracker){
                                    if (localThreadPool==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "threadPool", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localThreadPool.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","threadPool"),
                                        xmlWriter);
                                    }
                                } if (localTransportManagerTracker){
                                    if (localTransportManager==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "transportManager", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localTransportManager.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","transportManager"),
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

                 if (localAnyOperationContextRegisteredTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "anyOperationContextRegistered"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAnyOperationContextRegistered));
                            } if (localAxisConfigurationTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "axisConfiguration"));
                            
                            
                                    elementList.add(localAxisConfiguration==null?null:
                                    localAxisConfiguration);
                                } if (localContextRootTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "contextRoot"));
                                 
                                         elementList.add(localContextRoot==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localContextRoot));
                                    } if (localListenerManagerTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "listenerManager"));
                            
                            
                                    elementList.add(localListenerManager==null?null:
                                    localListenerManager);
                                } if (localRootContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "rootContext"));
                            
                            
                                    elementList.add(localRootContext==null?null:
                                    localRootContext);
                                } if (localServiceContextPathTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceContextPath"));
                                 
                                         elementList.add(localServiceContextPath==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceContextPath));
                                    } if (localServiceGroupContextIDsTracker){
                            if (localServiceGroupContextIDs!=null){
                                  for (int i = 0;i < localServiceGroupContextIDs.length;i++){
                                      
                                         if (localServiceGroupContextIDs[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                              "serviceGroupContextIDs"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextIDs[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                              "serviceGroupContextIDs"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                              "serviceGroupContextIDs"));
                                    elementList.add(null);
                                
                            }

                        } if (localServiceGroupContextTimeoutIntervalTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceGroupContextTimeoutInterval"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextTimeoutInterval));
                            } if (localServiceGroupContextTimoutIntervalTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceGroupContextTimoutInterval"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupContextTimoutInterval));
                            } if (localServiceGroupContextsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceGroupContexts"));
                            
                            
                                    elementList.add(localServiceGroupContexts==null?null:
                                    localServiceGroupContexts);
                                } if (localServicePathTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "servicePath"));
                                 
                                         elementList.add(localServicePath==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServicePath));
                                    } if (localThreadPoolTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "threadPool"));
                            
                            
                                    elementList.add(localThreadPool==null?null:
                                    localThreadPool);
                                } if (localTransportManagerTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "transportManager"));
                            
                            
                                    elementList.add(localTransportManager==null?null:
                                    localTransportManager);
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
        public static ConfigurationContext parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ConfigurationContext object =
                new ConfigurationContext();

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
                    
                            if (!"ConfigurationContext".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ConfigurationContext)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list7 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","anyOperationContextRegistered").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setAnyOperationContextRegistered(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisConfiguration").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setAxisConfiguration(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setAxisConfiguration(org.apache.axis2.engine.xsd.AxisConfiguration.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","contextRoot").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setContextRoot(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","listenerManager").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setListenerManager(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setListenerManager(org.apache.axis2.engine.xsd.ListenerManager.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceContextPath").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceContextPath(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContextIDs").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list7.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list7.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone7 = false;
                                            while(!loopDone7){
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
                                                    loopDone7 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContextIDs").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list7.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list7.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone7 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setServiceGroupContextIDs((java.lang.String[])
                                                        list7.toArray(new java.lang.String[list7.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContextTimeoutInterval").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceGroupContextTimeoutInterval(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setServiceGroupContextTimeoutInterval(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContextTimoutInterval").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceGroupContextTimoutInterval(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setServiceGroupContextTimoutInterval(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupContexts").equals(reader.getName())){
                                
                                     object.setServiceGroupContexts(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","servicePath").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServicePath(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","threadPool").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setThreadPool(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setThreadPool(org.apache.axis2.util.threadpool.xsd.ThreadFactory.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","transportManager").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setTransportManager(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setTransportManager(org.apache.axis2.engine.xsd.ListenerManager.Factory.parse(reader));
                                              
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
           
    