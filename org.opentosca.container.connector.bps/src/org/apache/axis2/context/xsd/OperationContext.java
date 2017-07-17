
/**
 * OperationContext.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.context.xsd;
            

            /**
            *  OperationContext bean class
            */
        
        public  class OperationContext
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = OperationContext
                Namespace URI = http://context.axis2.apache.org/xsd
                Namespace Prefix = ns10
                */
            

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
                        * field for Complete
                        */

                        
                                    protected boolean localComplete ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCompleteTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getComplete(){
                               return localComplete;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Complete
                               */
                               public void setComplete(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localCompleteTracker =
                                       true;
                                   
                                            this.localComplete=param;
                                    

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
                        * field for Key
                        */

                        
                                    protected java.lang.String localKey ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localKeyTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getKey(){
                               return localKey;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Key
                               */
                               public void setKey(java.lang.String param){
                            localKeyTracker = true;
                                   
                                            this.localKey=param;
                                    

                               }
                            

                        /**
                        * field for LogCorrelationIDString
                        */

                        
                                    protected java.lang.String localLogCorrelationIDString ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLogCorrelationIDStringTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getLogCorrelationIDString(){
                               return localLogCorrelationIDString;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param LogCorrelationIDString
                               */
                               public void setLogCorrelationIDString(java.lang.String param){
                            localLogCorrelationIDStringTracker = true;
                                   
                                            this.localLogCorrelationIDString=param;
                                    

                               }
                            

                        /**
                        * field for MessageContexts
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localMessageContexts ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessageContextsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getMessageContexts(){
                               return localMessageContexts;
                           }

                           
                        


                               
                              /**
                               * validate the array for MessageContexts
                               */
                              protected void validateMessageContexts(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param MessageContexts
                              */
                              public void setMessageContexts(java.lang.String[] param){
                              
                                   validateMessageContexts(param);

                               localMessageContextsTracker = true;
                                      
                                      this.localMessageContexts=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addMessageContexts(java.lang.String param){
                                   if (localMessageContexts == null){
                                   localMessageContexts = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localMessageContextsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localMessageContexts);
                               list.add(param);
                               this.localMessageContexts =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for OperationName
                        */

                        
                                    protected java.lang.String localOperationName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOperationNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOperationName(){
                               return localOperationName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OperationName
                               */
                               public void setOperationName(java.lang.String param){
                            localOperationNameTracker = true;
                                   
                                            this.localOperationName=param;
                                    

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
                        * field for ServiceGroupName
                        */

                        
                                    protected java.lang.String localServiceGroupName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceGroupNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServiceGroupName(){
                               return localServiceGroupName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceGroupName
                               */
                               public void setServiceGroupName(java.lang.String param){
                            localServiceGroupNameTracker = true;
                                   
                                            this.localServiceGroupName=param;
                                    

                               }
                            

                        /**
                        * field for ServiceName
                        */

                        
                                    protected java.lang.String localServiceName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localServiceNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServiceName(){
                               return localServiceName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServiceName
                               */
                               public void setServiceName(java.lang.String param){
                            localServiceNameTracker = true;
                                   
                                            this.localServiceName=param;
                                    

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
                           namespacePrefix+":OperationContext",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "OperationContext",
                           xmlWriter);
                   }

               
                   }
                if (localAxisOperationTracker){
                                    if (localAxisOperation==null){

                                        writeStartElement(null, "http://context.axis2.apache.org/xsd", "axisOperation", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisOperation.serialize(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","axisOperation"),
                                        xmlWriter);
                                    }
                                } if (localCompleteTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "complete", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("complete cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localComplete));
                                               }
                                    
                                   xmlWriter.writeEndElement();
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
                                } if (localKeyTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "key", xmlWriter);
                             

                                          if (localKey==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localKey);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localLogCorrelationIDStringTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "logCorrelationIDString", xmlWriter);
                             

                                          if (localLogCorrelationIDString==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localLogCorrelationIDString);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localMessageContextsTracker){
                             if (localMessageContexts!=null) {
                                   namespace = "http://context.axis2.apache.org/xsd";
                                   for (int i = 0;i < localMessageContexts.length;i++){
                                        
                                            if (localMessageContexts[i] != null){
                                        
                                                writeStartElement(null, namespace, "messageContexts", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessageContexts[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://context.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "messageContexts", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://context.axis2.apache.org/xsd", "messageContexts", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                             }

                        } if (localOperationNameTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "operationName", xmlWriter);
                             

                                          if (localOperationName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOperationName);
                                            
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
                                } if (localServiceGroupNameTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceGroupName", xmlWriter);
                             

                                          if (localServiceGroupName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServiceGroupName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localServiceNameTracker){
                                    namespace = "http://context.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "serviceName", xmlWriter);
                             

                                          if (localServiceName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localServiceName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
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

                 if (localAxisOperationTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "axisOperation"));
                            
                            
                                    elementList.add(localAxisOperation==null?null:
                                    localAxisOperation);
                                } if (localCompleteTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "complete"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localComplete));
                            } if (localConfigurationContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "configurationContext"));
                            
                            
                                    elementList.add(localConfigurationContext==null?null:
                                    localConfigurationContext);
                                } if (localKeyTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "key"));
                                 
                                         elementList.add(localKey==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localKey));
                                    } if (localLogCorrelationIDStringTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "logCorrelationIDString"));
                                 
                                         elementList.add(localLogCorrelationIDString==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLogCorrelationIDString));
                                    } if (localMessageContextsTracker){
                            if (localMessageContexts!=null){
                                  for (int i = 0;i < localMessageContexts.length;i++){
                                      
                                         if (localMessageContexts[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                              "messageContexts"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessageContexts[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                              "messageContexts"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                              "messageContexts"));
                                    elementList.add(null);
                                
                            }

                        } if (localOperationNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "operationName"));
                                 
                                         elementList.add(localOperationName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOperationName));
                                    } if (localRootContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "rootContext"));
                            
                            
                                    elementList.add(localRootContext==null?null:
                                    localRootContext);
                                } if (localServiceContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceContext"));
                            
                            
                                    elementList.add(localServiceContext==null?null:
                                    localServiceContext);
                                } if (localServiceGroupNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceGroupName"));
                                 
                                         elementList.add(localServiceGroupName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceGroupName));
                                    } if (localServiceNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd",
                                                                      "serviceName"));
                                 
                                         elementList.add(localServiceName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServiceName));
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
        public static OperationContext parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            OperationContext object =
                new OperationContext();

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
                    
                            if (!"OperationContext".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (OperationContext)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list6 = new java.util.ArrayList();
                    
                                    
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","complete").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setComplete(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","key").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setKey(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","logCorrelationIDString").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLogCorrelationIDString(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","messageContexts").equals(reader.getName())){
                                
                                    
                                    
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
                                                    if (new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","messageContexts").equals(reader.getName())){
                                                         
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
                                            
                                                    object.setMessageContexts((java.lang.String[])
                                                        list6.toArray(new java.lang.String[list6.size()]));
                                                
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","operationName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOperationName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceGroupName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceGroupName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://context.axis2.apache.org/xsd","serviceName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setServiceName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
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
           
    