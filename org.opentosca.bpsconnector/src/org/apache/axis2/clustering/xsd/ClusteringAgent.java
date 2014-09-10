
/**
 * ClusteringAgent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: SNAPSHOT  Built on : Nov 10, 2010 (06:34:21 UTC)
 */

            
                package org.apache.axis2.clustering.xsd;
            

            /**
            *  ClusteringAgent bean class
            */
        
        public abstract class ClusteringAgent
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = ClusteringAgent
                Namespace URI = http://clustering.axis2.apache.org/xsd
                Namespace Prefix = ns13
                */
            

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
                        * field for Domains
                        */

                        
                                    protected authclient.java.util.xsd.Set localDomains ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDomainsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return authclient.java.util.xsd.Set
                           */
                           public  authclient.java.util.xsd.Set getDomains(){
                               return localDomains;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Domains
                               */
                               public void setDomains(authclient.java.util.xsd.Set param){
                            localDomainsTracker = true;
                                   
                                            this.localDomains=param;
                                    

                               }
                            

                        /**
                        * field for Members
                        */

                        
                                    protected java.lang.Object localMembers ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMembersTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.Object
                           */
                           public  java.lang.Object getMembers(){
                               return localMembers;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Members
                               */
                               public void setMembers(java.lang.Object param){
                            localMembersTracker = true;
                                   
                                            this.localMembers=param;
                                    

                               }
                            

                        /**
                        * field for NodeManager
                        */

                        
                                    protected org.apache.axis2.clustering.management.xsd.NodeManager localNodeManager ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNodeManagerTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.clustering.management.xsd.NodeManager
                           */
                           public  org.apache.axis2.clustering.management.xsd.NodeManager getNodeManager(){
                               return localNodeManager;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NodeManager
                               */
                               public void setNodeManager(org.apache.axis2.clustering.management.xsd.NodeManager param){
                            localNodeManagerTracker = true;
                                   
                                            this.localNodeManager=param;
                                    

                               }
                            

                        /**
                        * field for StateManager
                        */

                        
                                    protected org.apache.axis2.clustering.state.xsd.StateManager localStateManager ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localStateManagerTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.apache.axis2.clustering.state.xsd.StateManager
                           */
                           public  org.apache.axis2.clustering.state.xsd.StateManager getStateManager(){
                               return localStateManager;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param StateManager
                               */
                               public void setStateManager(org.apache.axis2.clustering.state.xsd.StateManager param){
                            localStateManagerTracker = true;
                                   
                                            this.localStateManager=param;
                                    

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
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://clustering.axis2.apache.org/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":ClusteringAgent",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "ClusteringAgent",
                           xmlWriter);
                   }

               
                   }
                if (localConfigurationContextTracker){
                                    if (localConfigurationContext==null){

                                        writeStartElement(null, "http://clustering.axis2.apache.org/xsd", "configurationContext", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localConfigurationContext.serialize(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","configurationContext"),
                                        xmlWriter);
                                    }
                                } if (localDomainsTracker){
                                    if (localDomains==null){

                                        writeStartElement(null, "http://clustering.axis2.apache.org/xsd", "domains", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localDomains.serialize(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","domains"),
                                        xmlWriter);
                                    }
                                } if (localMembersTracker){
                            
                            if (localMembers!=null){
                                if (localMembers instanceof org.apache.axis2.databinding.ADBBean){
                                    ((org.apache.axis2.databinding.ADBBean)localMembers).serialize(
                                               new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","members"),
                                               xmlWriter,true);
                                 } else {
                                    writeStartElement(null, "http://clustering.axis2.apache.org/xsd", "members", xmlWriter);
                                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localMembers, xmlWriter);
                                    xmlWriter.writeEndElement();
                                 }
                            } else {
                                
                                        // write null attribute
                                           writeStartElement(null, "http://clustering.axis2.apache.org/xsd", "members", xmlWriter);

                                           // write the nil attribute
                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                           xmlWriter.writeEndElement();
                                    
                            }


                        } if (localNodeManagerTracker){
                                    if (localNodeManager==null){

                                        writeStartElement(null, "http://clustering.axis2.apache.org/xsd", "nodeManager", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localNodeManager.serialize(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","nodeManager"),
                                        xmlWriter);
                                    }
                                } if (localStateManagerTracker){
                                    if (localStateManager==null){

                                        writeStartElement(null, "http://clustering.axis2.apache.org/xsd", "stateManager", xmlWriter);

                                       // write the nil attribute
                                      writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                      xmlWriter.writeEndElement();
                                    }else{
                                     localStateManager.serialize(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","stateManager"),
                                        xmlWriter);
                                    }
                                }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://clustering.axis2.apache.org/xsd")){
                return "ns13";
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

                 if (localConfigurationContextTracker){
                            elementList.add(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd",
                                                                      "configurationContext"));
                            
                            
                                    elementList.add(localConfigurationContext==null?null:
                                    localConfigurationContext);
                                } if (localDomainsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd",
                                                                      "domains"));
                            
                            
                                    elementList.add(localDomains==null?null:
                                    localDomains);
                                } if (localMembersTracker){
                            elementList.add(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd",
                                                                      "members"));
                            
                            
                                    elementList.add(localMembers==null?null:
                                    localMembers);
                                } if (localNodeManagerTracker){
                            elementList.add(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd",
                                                                      "nodeManager"));
                            
                            
                                    elementList.add(localNodeManager==null?null:
                                    localNodeManager);
                                } if (localStateManagerTracker){
                            elementList.add(new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd",
                                                                      "stateManager"));
                            
                            
                                    elementList.add(localStateManager==null?null:
                                    localStateManager);
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
        public static ClusteringAgent parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ClusteringAgent object =
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
                    
                            if (!"ClusteringAgent".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ClusteringAgent)org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        
                        throw new org.apache.axis2.databinding.ADBException("The an abstract class can not be instantiated !!!");
                    

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","configurationContext").equals(reader.getName())){
                                
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","domains").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setDomains(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setDomains(authclient.java.util.xsd.Set.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","members").equals(reader.getName())){
                                
                                     object.setMembers(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                                org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                                       
                                         reader.next();
                                     
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","nodeManager").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setNodeManager(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setNodeManager(org.apache.axis2.clustering.management.xsd.NodeManager.Factory.parse(reader));
                                              
                                        reader.next();
                                    }
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://clustering.axis2.apache.org/xsd","stateManager").equals(reader.getName())){
                                
                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                          object.setStateManager(null);
                                          reader.next();
                                            
                                            reader.next();
                                          
                                      }else{
                                    
                                                object.setStateManager(org.apache.axis2.clustering.state.xsd.StateManager.Factory.parse(reader));
                                              
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
           
    