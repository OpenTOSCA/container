
/**
 * AxisMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.description.xsd;
            

            /**
            *  AxisMessage bean class
            */
        
        public  class AxisMessage
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = AxisMessage
                Namespace URI = http://description.axis2.apache.org/xsd
                Namespace Prefix = ns19
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
                        * field for Direction
                        */

                        
                                    protected java.lang.String localDirection ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDirectionTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getDirection(){
                               return localDirection;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Direction
                               */
                               public void setDirection(java.lang.String param){
                            localDirectionTracker = true;
                                   
                                            this.localDirection=param;
                                    

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
                        * field for ElementQName
                        */

                        
                                    protected java.lang.Object localElementQName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localElementQNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getElementQName(){
                               return localElementQName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ElementQName
                               */
                               public void setElementQName(java.lang.Object param){
                            localElementQNameTracker = true;
                                   
                                            this.localElementQName=param;
                                    

                               }
                            

                        /**
                        * field for ExtensibilityAttributes
                        */

                        
                                    protected java.lang.Object localExtensibilityAttributes ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExtensibilityAttributesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getExtensibilityAttributes(){
                               return localExtensibilityAttributes;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ExtensibilityAttributes
                               */
                               public void setExtensibilityAttributes(java.lang.Object param){
                            localExtensibilityAttributesTracker = true;
                                   
                                            this.localExtensibilityAttributes=param;
                                    

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
                        * field for MessageFlow
                        */

                        
                                    protected java.lang.Object localMessageFlow ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessageFlowTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getMessageFlow(){
                               return localMessageFlow;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param MessageFlow
                               */
                               public void setMessageFlow(java.lang.Object param){
                            localMessageFlowTracker = true;
                                   
                                            this.localMessageFlow=param;
                                    

                               }
                            

                        /**
                        * field for MessagePartName
                        */

                        
                                    protected java.lang.String localMessagePartName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMessagePartNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getMessagePartName(){
                               return localMessagePartName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param MessagePartName
                               */
                               public void setMessagePartName(java.lang.String param){
                            localMessagePartNameTracker = true;
                                   
                                            this.localMessagePartName=param;
                                    

                               }
                            

                        /**
                        * field for Modulerefs
                        * This was an Array!
                        */

                        
                                    protected java.lang.String[] localModulerefs ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModulerefsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getModulerefs(){
                               return localModulerefs;
                           }

                           
                        


                               
                              /**
                               * validate the array for Modulerefs
                               */
                              protected void validateModulerefs(java.lang.String[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Modulerefs
                              */
                              public void setModulerefs(java.lang.String[] param){
                              
                                   validateModulerefs(param);

                               localModulerefsTracker = true;
                                      
                                      this.localModulerefs=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addModulerefs(java.lang.String param){
                                   if (localModulerefs == null){
                                   localModulerefs = new java.lang.String[]{};
                                   }

                            
                                 //update the setting tracker
                                localModulerefsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localModulerefs);
                               list.add(param);
                               this.localModulerefs =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

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
                        * field for PartName
                        */

                        
                                    protected java.lang.String localPartName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPartNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getPartName(){
                               return localPartName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PartName
                               */
                               public void setPartName(java.lang.String param){
                            localPartNameTracker = true;
                                   
                                            this.localPartName=param;
                                    

                               }
                            

                        /**
                        * field for PolicyUpdated
                        */

                        
                                    protected boolean localPolicyUpdated ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPolicyUpdatedTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getPolicyUpdated(){
                               return localPolicyUpdated;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PolicyUpdated
                               */
                               public void setPolicyUpdated(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localPolicyUpdatedTracker =
                                       true;
                                   
                                            this.localPolicyUpdated=param;
                                    

                               }
                            

                        /**
                        * field for SchemaElement
                        */

                        
                                    protected org.apache.ws.commons.schema.xsd.XmlSchemaElement localSchemaElement ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSchemaElementTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.ws.commons.schema.xsd.XmlSchemaElement
                           */
                           public  org.apache.ws.commons.schema.xsd.XmlSchemaElement getSchemaElement(){
                               return localSchemaElement;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SchemaElement
                               */
                               public void setSchemaElement(org.apache.ws.commons.schema.xsd.XmlSchemaElement param){
                            localSchemaElementTracker = true;
                                   
                                            this.localSchemaElement=param;
                                    

                               }
                            

                        /**
                        * field for SoapHeaders
                        */

                        
                                    protected java.lang.Object localSoapHeaders ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSoapHeadersTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getSoapHeaders(){
                               return localSoapHeaders;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SoapHeaders
                               */
                               public void setSoapHeaders(java.lang.Object param){
                            localSoapHeadersTracker = true;
                                   
                                            this.localSoapHeaders=param;
                                    

                               }
                            

                        /**
                        * field for Wrapped
                        */

                        
                                    protected boolean localWrapped ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localWrappedTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getWrapped(){
                               return localWrapped;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Wrapped
                               */
                               public void setWrapped(boolean param){
                            
                                       // setting primitive attribute tracker to true
                                       localWrappedTracker =
                                       true;
                                   
                                            this.localWrapped=param;
                                    

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
                           namespacePrefix+":AxisMessage",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "AxisMessage",
                           xmlWriter);
                   }

               
                   }
                if (localAxisOperationTracker){
                                    if (localAxisOperation==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisOperation", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localAxisOperation.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisOperation"),
                                        xmlWriter);
                                    }
                                } if (localDirectionTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "direction", xmlWriter);
                             

                                          if (localDirection==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localDirection);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localEffectivePolicyTracker){
                                    if (localEffectivePolicy==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "effectivePolicy", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localEffectivePolicy.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","effectivePolicy"),
                                        xmlWriter);
                                    }
                                } if (localElementQNameTracker){
                            
                            if (localElementQName!=null){
                                if (localElementQName instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localElementQName).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","elementQName"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "elementQName", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localElementQName, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "elementQName", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localExtensibilityAttributesTracker){
                            
                            if (localExtensibilityAttributes!=null){
                                if (localExtensibilityAttributes instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localExtensibilityAttributes).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","extensibilityAttributes"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "extensibilityAttributes", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localExtensibilityAttributes, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "extensibilityAttributes", xmlWriter);

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


                        } if (localMessageFlowTracker){
                            
                            if (localMessageFlow!=null){
                                if (localMessageFlow instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localMessageFlow).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageFlow"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageFlow", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localMessageFlow, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageFlow", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localMessagePartNameTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "messagePartName", xmlWriter);
                             

                                          if (localMessagePartName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localMessagePartName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localModulerefsTracker){
                             if (localModulerefs!=null) {
                                   namespace = "http://description.axis2.apache.org/xsd";
                                   for (int i = 0;i < localModulerefs.length;i++){
                                        
                                            if (localModulerefs[i] != null){
                                        
                                                writeStartElement(null, namespace, "modulerefs", xmlWriter);

                                            
                                                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModulerefs[i]));
                                                    
                                                xmlWriter.writeEndElement();
                                              
                                                } else {
                                                   
                                                           // write null attribute
                                                            namespace = "http://description.axis2.apache.org/xsd";
                                                            writeStartElement(null, namespace, "modulerefs", xmlWriter);
                                                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                            xmlWriter.writeEndElement();
                                                       
                                                }

                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "modulerefs", xmlWriter);

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
                             } if (localPartNameTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "partName", xmlWriter);
                             

                                          if (localPartName==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localPartName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPolicyUpdatedTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "policyUpdated", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("policyUpdated cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPolicyUpdated));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSchemaElementTracker){
                                    if (localSchemaElement==null){

                                        writeStartElement(null, "http://description.axis2.apache.org/xsd", "schemaElement", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localSchemaElement.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaElement"),
                                        xmlWriter);
                                    }
                                } if (localSoapHeadersTracker){
                            
                            if (localSoapHeaders!=null){
                                if (localSoapHeaders instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localSoapHeaders).serialize(
                                               new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","soapHeaders"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://description.axis2.apache.org/xsd", "soapHeaders", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localSoapHeaders, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://description.axis2.apache.org/xsd", "soapHeaders", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localWrappedTracker){
                                    namespace = "http://description.axis2.apache.org/xsd";
                                    writeStartElement(null, namespace, "wrapped", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("wrapped cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWrapped));
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

                 if (localAxisOperationTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "axisOperation"));
                            
                            
                                    elementList.add(localAxisOperation==null?null:
                                    localAxisOperation);
                                } if (localDirectionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "direction"));
                                 
                                         elementList.add(localDirection==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDirection));
                                    } if (localEffectivePolicyTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "effectivePolicy"));
                            
                            
                                    elementList.add(localEffectivePolicy==null?null:
                                    localEffectivePolicy);
                                } if (localElementQNameTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "elementQName"));
                            
                            
                                    elementList.add(localElementQName==null?null:
                                    localElementQName);
                                } if (localExtensibilityAttributesTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "extensibilityAttributes"));
                            
                            
                                    elementList.add(localExtensibilityAttributes==null?null:
                                    localExtensibilityAttributes);
                                } if (localKeyTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "key"));
                            
                            
                                    elementList.add(localKey==null?null:
                                    localKey);
                                } if (localMessageFlowTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "messageFlow"));
                            
                            
                                    elementList.add(localMessageFlow==null?null:
                                    localMessageFlow);
                                } if (localMessagePartNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "messagePartName"));
                                 
                                         elementList.add(localMessagePartName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessagePartName));
                                    } if (localModulerefsTracker){
                            if (localModulerefs!=null){
                                  for (int i = 0;i < localModulerefs.length;i++){
                                      
                                         if (localModulerefs[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "modulerefs"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModulerefs[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "modulerefs"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                              "modulerefs"));
                                    elementList.add(null);
                                
                            }

                        } if (localNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "name"));
                                 
                                         elementList.add(localName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localName));
                                    } if (localPartNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "partName"));
                                 
                                         elementList.add(localPartName==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPartName));
                                    } if (localPolicyUpdatedTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "policyUpdated"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPolicyUpdated));
                            } if (localSchemaElementTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "schemaElement"));
                            
                            
                                    elementList.add(localSchemaElement==null?null:
                                    localSchemaElement);
                                } if (localSoapHeadersTracker){
                            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "soapHeaders"));
                            
                            
                                    elementList.add(localSoapHeaders==null?null:
                                    localSoapHeaders);
                                } if (localWrappedTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                                                                      "wrapped"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWrapped));
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
        public static AxisMessage parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            AxisMessage object =
                new AxisMessage();

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
                    
                            if (!"AxisMessage".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (AxisMessage)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list9 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","axisOperation").equals(reader.getName())){
                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","direction").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setDirection(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","effectivePolicy").equals(reader.getName())){
                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","elementQName").equals(reader.getName())){
                                
                                     object.setElementQName(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","extensibilityAttributes").equals(reader.getName())){
                                
                                     object.setExtensibilityAttributes(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messageFlow").equals(reader.getName())){
                                
                                     object.setMessageFlow(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","messagePartName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setMessagePartName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","modulerefs").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                              if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                  list9.add(null);
                                                       
                                                  reader.next();
                                              } else {
                                            list9.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone9 = false;
                                            while(!loopDone9){
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
                                                    loopDone9 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","modulerefs").equals(reader.getName())){
                                                         
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list9.add(null);
                                                                   
                                                              reader.next();
                                                          } else {
                                                        list9.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone9 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            
                                                    object.setModulerefs((java.lang.String[])
                                                        list9.toArray(new java.lang.String[list9.size()]));
                                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","partName").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPartName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","policyUpdated").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPolicyUpdated(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","schemaElement").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setSchemaElement(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setSchemaElement(org.apache.ws.commons.schema.xsd.XmlSchemaElement.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","soapHeaders").equals(reader.getName())){
                                
                                     object.setSoapHeaders(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd","wrapped").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setWrapped(
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
           
    