
/**
 * BPELUploaderStub.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */
package org.wso2.carbon.bpel.deployer.services;

import javax.xml.namespace.QName;

/*
 * BPELUploaderStub java implementation
 */


public class BPELUploaderStub extends org.apache.axis2.client.Stub implements BPELUploader {
  protected org.apache.axis2.description.AxisOperation[] _operations;

  // hashmaps to keep the fault mapping
  private final java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
  private final java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
  private final java.util.HashMap faultMessageMap = new java.util.HashMap();

  private static int counter = 0;

  private static synchronized java.lang.String getUniqueSuffix() {
    // reset the counter if it is greater than 99999
    if (counter > 99999) {
      counter = 0;
    }
    counter = counter + 1;
    return java.lang.Long.toString(java.lang.System.currentTimeMillis()) + "_" + counter;
  }


  private void populateAxisService() throws org.apache.axis2.AxisFault {

    // creating the Service with a unique name
    this._service = new org.apache.axis2.description.AxisService("BPELUploader" + getUniqueSuffix());
    addAnonymousOperations();

    // creating the operations
    org.apache.axis2.description.AxisOperation __operation;

    this._operations = new org.apache.axis2.description.AxisOperation[1];

    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
      "uploadService"));
    this._service.addOperation(__operation);


    this._operations[0] = __operation;


  }

  // populates the faults
  private void populateFaults() {


  }

  /**
   * Constructor that takes in a configContext
   */

  public BPELUploaderStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                          final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
    this(configurationContext, targetEndpoint, false);
  }


  /**
   * Constructor that takes in a configContext and useseperate listner
   */
  public BPELUploaderStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                          final java.lang.String targetEndpoint,
                          final boolean useSeparateListener) throws org.apache.axis2.AxisFault {
    // To populate AxisService
    populateAxisService();
    populateFaults();

    this._serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, this._service);


    this._serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
    this._serviceClient.getOptions().setUseSeparateListener(useSeparateListener);


  }

  /**
   * Default Constructor
   */
  public BPELUploaderStub(final org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {

    this(configurationContext,
      "https://192.168.178.21:9443/services/BPELUploader.BPELUploaderHttpsSoap11Endpoint/");

  }

  /**
   * Default Constructor
   */
  public BPELUploaderStub() throws org.apache.axis2.AxisFault {

    this("https://192.168.178.21:9443/services/BPELUploader.BPELUploaderHttpsSoap11Endpoint/");

  }

  /**
   * Constructor taking the target endpoint
   */
  public BPELUploaderStub(final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
    this(null, targetEndpoint);
  }


  /**
   * Auto generated method signature
   *
   * @see org.wso2.carbon.bpel.deployer.services.BPELUploader#uploadService
   * @param uploadService3
   *
   */


  @Override
  public void uploadService(

    final org.wso2.carbon.bpel.deployer.services.UploadService uploadService3)


    throws java.rmi.RemoteException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[0].getName());
      _operationClient.getOptions().setAction("urn:uploadService");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), uploadService3,
        optimizeContent(new javax.xml.namespace.QName(
          "http://services.deployer.bpel.carbon.wso2.org", "uploadService")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      return;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "uploadService"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "uploadService"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "uploadService"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});


            throw new java.rmi.RemoteException(ex.getMessage(), ex);
          } catch (final java.lang.ClassCastException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (final java.lang.ClassNotFoundException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (final java.lang.NoSuchMethodException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (final java.lang.reflect.InvocationTargetException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (final java.lang.IllegalAccessException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (final java.lang.InstantiationException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          }
        } else {
          throw f;
        }
      } else {
        throw f;
      }
    } finally {
      if (_messageContext.getTransportOut() != null) {
        _messageContext.getTransportOut().getSender().cleanup(_messageContext);
      }
    }
  }

  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @see org.wso2.carbon.bpel.deployer.services.BPELUploader#startuploadService
   * @param uploadService3
   *
   */
  @Override
  public void startuploadService(

    final org.wso2.carbon.bpel.deployer.services.UploadService uploadService3,

    final org.wso2.carbon.bpel.deployer.services.BPELUploaderCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[0].getName());
    _operationClient.getOptions().setAction("urn:uploadService");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), uploadService3,
      optimizeContent(new javax.xml.namespace.QName("http://services.deployer.bpel.carbon.wso2.org",
        "uploadService")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    // Nothing to pass as the callback!!!


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[0].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[0].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }


  /**
   * A utility method that copies the namepaces from the SOAPEnvelope
   */
  private java.util.Map getEnvelopeNamespaces(final org.apache.axiom.soap.SOAPEnvelope env) {
    final java.util.Map returnMap = new java.util.HashMap();
    final java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
    while (namespaceIterator.hasNext()) {
      final org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
      returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
    }
    return returnMap;
  }


  private final javax.xml.namespace.QName[] opNameArray = null;

  private boolean optimizeContent(final javax.xml.namespace.QName opName) {


    if (this.opNameArray == null) {
      return false;
    }
    for (final QName element : this.opNameArray) {
      if (opName.equals(element)) {
        return true;
      }
    }
    return false;
  }

  // https://192.168.178.21:9443/services/BPELUploader.BPELUploaderHttpsSoap11Endpoint/
  private org.apache.axiom.om.OMElement toOM(final org.wso2.carbon.bpel.deployer.services.UploadService param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.carbon.bpel.deployer.services.UploadService.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.carbon.bpel.deployer.services.UploadService param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.carbon.bpel.deployer.services.UploadService.MY_QNAME,
          factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  /**
   * get the default envelope
   */
  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory) {
    return factory.getDefaultEnvelope();
  }


  private java.lang.Object fromOM(final org.apache.axiom.om.OMElement param, final java.lang.Class type,
                                  final java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {

    try {

      if (org.wso2.carbon.bpel.deployer.services.UploadService.class.equals(type)) {

        return org.wso2.carbon.bpel.deployer.services.UploadService.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

    } catch (final java.lang.Exception e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
    return null;
  }


}
