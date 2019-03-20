
/**
 * InstanceManagementServiceStub.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */
package org.wso2.bps.management.wsdl.instancemanagement;

import javax.xml.namespace.QName;

/*
 * InstanceManagementServiceStub java implementation
 */


public class InstanceManagementServiceStub extends org.apache.axis2.client.Stub implements InstanceManagementService {
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
    this._service = new org.apache.axis2.description.AxisService("InstanceManagementService" + getUniqueSuffix());
    addAnonymousOperations();

    // creating the operations
    org.apache.axis2.description.AxisOperation __operation;

    this._operations = new org.apache.axis2.description.AxisOperation[11];

    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "getInstanceSummary"));
    this._service.addOperation(__operation);


    this._operations[0] = __operation;


    __operation = new org.apache.axis2.description.RobustOutOnlyAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "resumeInstance"));
    this._service.addOperation(__operation);


    this._operations[1] = __operation;


    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "getPaginatedInstanceList"));
    this._service.addOperation(__operation);


    this._operations[2] = __operation;


    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "getActivityLifeCycleFilter"));
    this._service.addOperation(__operation);


    this._operations[3] = __operation;


    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "getInstanceInfo"));
    this._service.addOperation(__operation);


    this._operations[4] = __operation;


    __operation = new org.apache.axis2.description.RobustOutOnlyAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "suspendInstance"));
    this._service.addOperation(__operation);


    this._operations[5] = __operation;


    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "deleteInstances"));
    this._service.addOperation(__operation);


    this._operations[6] = __operation;


    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "getInstanceInfoWithEvents"));
    this._service.addOperation(__operation);


    this._operations[7] = __operation;


    __operation = new org.apache.axis2.description.OutInAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "getLongRunningInstances"));
    this._service.addOperation(__operation);


    this._operations[8] = __operation;


    __operation = new org.apache.axis2.description.RobustOutOnlyAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "recoverActivity"));
    this._service.addOperation(__operation);


    this._operations[9] = __operation;


    __operation = new org.apache.axis2.description.RobustOutOnlyAxisOperation();


    __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/InstanceManagement",
      "terminateInstance"));
    this._service.addOperation(__operation);


    this._operations[10] = __operation;


  }

  // populates the faults
  private void populateFaults() {

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceSummary"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceSummary"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceSummary"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "resumeInstance"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "resumeInstance"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "resumeInstance"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getPaginatedInstanceList"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getPaginatedInstanceList"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getPaginatedInstanceList"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
        new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
        "getActivityLifeCycleFilter"),
      "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getActivityLifeCycleFilter"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getActivityLifeCycleFilter"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceInfo"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceInfo"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceInfo"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "suspendInstance"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "suspendInstance"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "suspendInstance"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "deleteInstances"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "deleteInstances"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "deleteInstances"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
        new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
        "getInstanceInfoWithEvents"),
      "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceInfoWithEvents"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getInstanceInfoWithEvents"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getLongRunningInstances"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getLongRunningInstances"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "getLongRunningInstances"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "recoverActivity"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "recoverActivity"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "recoverActivity"), "org.wso2.bps.management.schema.InstanceManagementException");

    this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "terminateInstance"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "terminateInstance"), "org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException");
    this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
      new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "instanceManagementException"),
      "terminateInstance"), "org.wso2.bps.management.schema.InstanceManagementException");


  }

  /**
   * Constructor that takes in a configContext
   */

  public InstanceManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                                       final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
    this(configurationContext, targetEndpoint, false);
  }


  /**
   * Constructor that takes in a configContext and useseperate listner
   */
  public InstanceManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
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
  public InstanceManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {

    this(configurationContext, "http://localhost:9763/services/InstanceManagementService");

  }

  /**
   * Default Constructor
   */
  public InstanceManagementServiceStub() throws org.apache.axis2.AxisFault {

    this("http://localhost:9763/services/InstanceManagementService");

  }

  /**
   * Constructor taking the target endpoint
   */
  public InstanceManagementServiceStub(final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
    this(null, targetEndpoint);
  }


  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#getInstanceSummary
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.InstanceSummaryE getInstanceSummary(

  )


    throws java.rmi.RemoteException


    , org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[0].getName());
      _operationClient.getOptions().setAction("urn:getInstanceSummary");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;

      // Style is taken to be "document". No input parameters
      // according to the WS-Basic profile in this case we have to send an empty soap message
      final org.apache.axiom.soap.SOAPFactory factory =
        getFactory(_operationClient.getOptions().getSoapVersionURI());
      env = factory.getDefaultEnvelope();

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object =
        fromOM(_returnEnv.getBody().getFirstElement(), org.wso2.bps.management.schema.InstanceSummaryE.class,
          getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.InstanceSummaryE) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "getInstanceSummary"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getInstanceSummary"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getInstanceSummary"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startgetInstanceSummary
   */
  @Override
  public void startgetInstanceSummary(


    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[0].getName());
    _operationClient.getOptions().setAction("urn:getInstanceSummary");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is taken to be "document". No input parameters
    // according to the WS-Basic profile in this case we have to send an empty soap message
    final org.apache.axiom.soap.SOAPFactory factory = getFactory(_operationClient.getOptions().getSoapVersionURI());
    env = factory.getDefaultEnvelope();

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object =
            fromOM(resultEnv.getBody().getFirstElement(),
              org.wso2.bps.management.schema.InstanceSummaryE.class, getEnvelopeNamespaces(resultEnv));
          callback.receiveResultgetInstanceSummary((org.wso2.bps.management.schema.InstanceSummaryE) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrorgetInstanceSummary(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "getInstanceSummary"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getInstanceSummary"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getInstanceSummary"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrorgetInstanceSummary(ex);
                  return;
                }


                callback.receiveErrorgetInstanceSummary(new java.rmi.RemoteException(ex.getMessage(),
                  ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceSummary(f);
              }
            } else {
              callback.receiveErrorgetInstanceSummary(f);
            }
          } else {
            callback.receiveErrorgetInstanceSummary(f);
          }
        } else {
          callback.receiveErrorgetInstanceSummary(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrorgetInstanceSummary(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[0].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[0].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }


  /**
   * Auto generated method signature
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */
  @Override
  public void resumeInstance(final org.wso2.bps.management.schema.ResumeInstance resumeInstance22

  ) throws java.rmi.RemoteException


    , org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;

    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[1].getName());
      _operationClient.getOptions().setAction("urn:resumeInstance");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");

      org.apache.axiom.soap.SOAPEnvelope env = null;
      _messageContext = new org.apache.axis2.context.MessageContext();


      // Style is Doc.


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), resumeInstance22,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "resumeInstance")));


      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // create message context with that soap envelope

      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      _operationClient.execute(true);


    } catch (final org.apache.axis2.AxisFault f) {
      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "resumeInstance"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "resumeInstance"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "resumeInstance"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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

    return;
  }

  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#getPaginatedInstanceList
   * @param getPaginatedInstanceListInput23
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.PaginatedInstanceList getPaginatedInstanceList(

    final org.wso2.bps.management.schema.GetPaginatedInstanceListInput getPaginatedInstanceListInput23)


    throws java.rmi.RemoteException


    ,
    org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[2].getName());
      _operationClient.getOptions().setAction("urn:getPaginatedInstanceList");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
        getPaginatedInstanceListInput23,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement",
          "getPaginatedInstanceList")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object =
        fromOM(_returnEnv.getBody().getFirstElement(),
          org.wso2.bps.management.schema.PaginatedInstanceList.class, getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.PaginatedInstanceList) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "getPaginatedInstanceList"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getPaginatedInstanceList"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getPaginatedInstanceList"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startgetPaginatedInstanceList
   * @param getPaginatedInstanceListInput23
   *
   */
  @Override
  public void startgetPaginatedInstanceList(

    final org.wso2.bps.management.schema.GetPaginatedInstanceListInput getPaginatedInstanceListInput23,

    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[2].getName());
    _operationClient.getOptions().setAction("urn:getPaginatedInstanceList");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getPaginatedInstanceListInput23,
      optimizeContent(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/wsdl/InstanceManagement", "getPaginatedInstanceList")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
            org.wso2.bps.management.schema.PaginatedInstanceList.class,
            getEnvelopeNamespaces(resultEnv));
          callback.receiveResultgetPaginatedInstanceList((org.wso2.bps.management.schema.PaginatedInstanceList) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrorgetPaginatedInstanceList(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "getPaginatedInstanceList"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getPaginatedInstanceList"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getPaginatedInstanceList"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrorgetPaginatedInstanceList(ex);
                  return;
                }


                callback.receiveErrorgetPaginatedInstanceList(new java.rmi.RemoteException(
                  ex.getMessage(), ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetPaginatedInstanceList(f);
              }
            } else {
              callback.receiveErrorgetPaginatedInstanceList(f);
            }
          } else {
            callback.receiveErrorgetPaginatedInstanceList(f);
          }
        } else {
          callback.receiveErrorgetPaginatedInstanceList(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrorgetPaginatedInstanceList(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[2].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[2].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }

  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#getActivityLifeCycleFilter
   * @param getActivityLifeCycleFilterIn25
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.ActivityLifeCycleEvents getActivityLifeCycleFilter(

    final org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn getActivityLifeCycleFilterIn25)


    throws java.rmi.RemoteException


    ,
    org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[3].getName());
      _operationClient.getOptions().setAction("urn:getActivityLifeCycleFilter");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
        getActivityLifeCycleFilterIn25,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement",
          "getActivityLifeCycleFilter")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object =
        fromOM(_returnEnv.getBody().getFirstElement(),
          org.wso2.bps.management.schema.ActivityLifeCycleEvents.class, getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.ActivityLifeCycleEvents) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "getActivityLifeCycleFilter"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getActivityLifeCycleFilter"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getActivityLifeCycleFilter"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startgetActivityLifeCycleFilter
   * @param getActivityLifeCycleFilterIn25
   *
   */
  @Override
  public void startgetActivityLifeCycleFilter(

    final org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn getActivityLifeCycleFilterIn25,

    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[3].getName());
    _operationClient.getOptions().setAction("urn:getActivityLifeCycleFilter");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getActivityLifeCycleFilterIn25,
      optimizeContent(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/wsdl/InstanceManagement", "getActivityLifeCycleFilter")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
            org.wso2.bps.management.schema.ActivityLifeCycleEvents.class,
            getEnvelopeNamespaces(resultEnv));
          callback.receiveResultgetActivityLifeCycleFilter((org.wso2.bps.management.schema.ActivityLifeCycleEvents) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrorgetActivityLifeCycleFilter(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "getActivityLifeCycleFilter"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getActivityLifeCycleFilter"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getActivityLifeCycleFilter"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrorgetActivityLifeCycleFilter(ex);
                  return;
                }


                callback.receiveErrorgetActivityLifeCycleFilter(new java.rmi.RemoteException(
                  ex.getMessage(), ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetActivityLifeCycleFilter(f);
              }
            } else {
              callback.receiveErrorgetActivityLifeCycleFilter(f);
            }
          } else {
            callback.receiveErrorgetActivityLifeCycleFilter(f);
          }
        } else {
          callback.receiveErrorgetActivityLifeCycleFilter(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrorgetActivityLifeCycleFilter(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[3].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[3].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }

  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#getInstanceInfo
   * @param getInstanceInfoIn27
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.InstanceInfo getInstanceInfo(

    final org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn27)


    throws java.rmi.RemoteException


    ,
    org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[4].getName());
      _operationClient.getOptions().setAction("urn:getInstanceInfo");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getInstanceInfoIn27,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "getInstanceInfo")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object =
        fromOM(_returnEnv.getBody().getFirstElement(), org.wso2.bps.management.schema.InstanceInfo.class,
          getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.InstanceInfo) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "getInstanceInfo"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getInstanceInfo"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getInstanceInfo"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startgetInstanceInfo
   * @param getInstanceInfoIn27
   *
   */
  @Override
  public void startgetInstanceInfo(

    final org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn27,

    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[4].getName());
    _operationClient.getOptions().setAction("urn:getInstanceInfo");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getInstanceInfoIn27,
      optimizeContent(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/wsdl/InstanceManagement", "getInstanceInfo")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object =
            fromOM(resultEnv.getBody().getFirstElement(), org.wso2.bps.management.schema.InstanceInfo.class,
              getEnvelopeNamespaces(resultEnv));
          callback.receiveResultgetInstanceInfo((org.wso2.bps.management.schema.InstanceInfo) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrorgetInstanceInfo(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "getInstanceInfo"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getInstanceInfo"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getInstanceInfo"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrorgetInstanceInfo(ex);
                  return;
                }


                callback.receiveErrorgetInstanceInfo(new java.rmi.RemoteException(ex.getMessage(), ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfo(f);
              }
            } else {
              callback.receiveErrorgetInstanceInfo(f);
            }
          } else {
            callback.receiveErrorgetInstanceInfo(f);
          }
        } else {
          callback.receiveErrorgetInstanceInfo(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrorgetInstanceInfo(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[4].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[4].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }


  /**
   * Auto generated method signature
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */
  @Override
  public void suspendInstance(final org.wso2.bps.management.schema.SuspendInstance suspendInstance29

  ) throws java.rmi.RemoteException


    , org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;

    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[5].getName());
      _operationClient.getOptions().setAction("urn:suspendInstance");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");

      org.apache.axiom.soap.SOAPEnvelope env = null;
      _messageContext = new org.apache.axis2.context.MessageContext();


      // Style is Doc.


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), suspendInstance29,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "suspendInstance")));


      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // create message context with that soap envelope

      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      _operationClient.execute(true);


    } catch (final org.apache.axis2.AxisFault f) {
      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "suspendInstance"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "suspendInstance"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "suspendInstance"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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

    return;
  }

  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#deleteInstances
   * @param deleteInstances30
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.DeleteInstanceResponse deleteInstances(

    final org.wso2.bps.management.schema.DeleteInstances deleteInstances30)


    throws java.rmi.RemoteException


    ,
    org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[6].getName());
      _operationClient.getOptions().setAction("urn:deleteInstances");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), deleteInstances30,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "deleteInstances")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object =
        fromOM(_returnEnv.getBody().getFirstElement(),
          org.wso2.bps.management.schema.DeleteInstanceResponse.class, getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.DeleteInstanceResponse) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "deleteInstances"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "deleteInstances"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "deleteInstances"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startdeleteInstances
   * @param deleteInstances30
   *
   */
  @Override
  public void startdeleteInstances(

    final org.wso2.bps.management.schema.DeleteInstances deleteInstances30,

    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[6].getName());
    _operationClient.getOptions().setAction("urn:deleteInstances");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), deleteInstances30,
      optimizeContent(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/wsdl/InstanceManagement", "deleteInstances")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
            org.wso2.bps.management.schema.DeleteInstanceResponse.class,
            getEnvelopeNamespaces(resultEnv));
          callback.receiveResultdeleteInstances((org.wso2.bps.management.schema.DeleteInstanceResponse) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrordeleteInstances(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "deleteInstances"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "deleteInstances"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "deleteInstances"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrordeleteInstances(ex);
                  return;
                }


                callback.receiveErrordeleteInstances(new java.rmi.RemoteException(ex.getMessage(), ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrordeleteInstances(f);
              }
            } else {
              callback.receiveErrordeleteInstances(f);
            }
          } else {
            callback.receiveErrordeleteInstances(f);
          }
        } else {
          callback.receiveErrordeleteInstances(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrordeleteInstances(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[6].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[6].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }

  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#getInstanceInfoWithEvents
   * @param getInstanceInfoIn32
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.InstanceInfoWithEvents getInstanceInfoWithEvents(

    final org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn32)


    throws java.rmi.RemoteException


    ,
    org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[7].getName());
      _operationClient.getOptions().setAction("urn:getInstanceInfoWithEvents");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getInstanceInfoIn32,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement",
          "getInstanceInfoWithEvents")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object =
        fromOM(_returnEnv.getBody().getFirstElement(),
          org.wso2.bps.management.schema.InstanceInfoWithEvents.class, getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.InstanceInfoWithEvents) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "getInstanceInfoWithEvents"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getInstanceInfoWithEvents"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getInstanceInfoWithEvents"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startgetInstanceInfoWithEvents
   * @param getInstanceInfoIn32
   *
   */
  @Override
  public void startgetInstanceInfoWithEvents(

    final org.wso2.bps.management.schema.GetInstanceInfoIn getInstanceInfoIn32,

    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[7].getName());
    _operationClient.getOptions().setAction("urn:getInstanceInfoWithEvents");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getInstanceInfoIn32,
      optimizeContent(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/wsdl/InstanceManagement", "getInstanceInfoWithEvents")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
            org.wso2.bps.management.schema.InstanceInfoWithEvents.class,
            getEnvelopeNamespaces(resultEnv));
          callback.receiveResultgetInstanceInfoWithEvents((org.wso2.bps.management.schema.InstanceInfoWithEvents) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrorgetInstanceInfoWithEvents(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "getInstanceInfoWithEvents"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getInstanceInfoWithEvents"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getInstanceInfoWithEvents"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrorgetInstanceInfoWithEvents(ex);
                  return;
                }


                callback.receiveErrorgetInstanceInfoWithEvents(new java.rmi.RemoteException(
                  ex.getMessage(), ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetInstanceInfoWithEvents(f);
              }
            } else {
              callback.receiveErrorgetInstanceInfoWithEvents(f);
            }
          } else {
            callback.receiveErrorgetInstanceInfoWithEvents(f);
          }
        } else {
          callback.receiveErrorgetInstanceInfoWithEvents(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrorgetInstanceInfoWithEvents(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[7].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[7].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }

  /**
   * Auto generated method signature
   *
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#getLongRunningInstances
   * @param getLongRunningInstancesInput34
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */


  @Override
  public org.wso2.bps.management.schema.GetLongRunningInstancesResponse getLongRunningInstances(

    final org.wso2.bps.management.schema.GetLongRunningInstancesInput getLongRunningInstancesInput34)


    throws java.rmi.RemoteException


    ,
    org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;
    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[8].getName());
      _operationClient.getOptions().setAction("urn:getLongRunningInstances");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");


      // create a message context
      _messageContext = new org.apache.axis2.context.MessageContext();


      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
        getLongRunningInstancesInput34, optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "getLongRunningInstances")));

      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);


      final org.apache.axis2.context.MessageContext _returnMessageContext =
        _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


      final java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
        org.wso2.bps.management.schema.GetLongRunningInstancesResponse.class,
        getEnvelopeNamespaces(_returnEnv));


      return (org.wso2.bps.management.schema.GetLongRunningInstancesResponse) object;

    } catch (final org.apache.axis2.AxisFault f) {

      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "getLongRunningInstances"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getLongRunningInstances"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "getLongRunningInstances"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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
   * @see org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementService#startgetLongRunningInstances
   * @param getLongRunningInstancesInput34
   *
   */
  @Override
  public void startgetLongRunningInstances(

    final org.wso2.bps.management.schema.GetLongRunningInstancesInput getLongRunningInstancesInput34,

    final org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementServiceCallbackHandler callback)

    throws java.rmi.RemoteException {

    final org.apache.axis2.client.OperationClient _operationClient =
      this._serviceClient.createClient(this._operations[8].getName());
    _operationClient.getOptions().setAction("urn:getLongRunningInstances");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


    addPropertyToOperationClient(_operationClient,
      org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
      "&");


    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


    // Style is Doc.


    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getLongRunningInstancesInput34,
      optimizeContent(new javax.xml.namespace.QName(
        "http://wso2.org/bps/management/wsdl/InstanceManagement", "getLongRunningInstances")));

    // adding SOAP soap_headers
    this._serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
      @Override
      public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
        try {
          final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

          final java.lang.Object object =
            fromOM(resultEnv.getBody().getFirstElement(),
              org.wso2.bps.management.schema.GetLongRunningInstancesResponse.class,
              getEnvelopeNamespaces(resultEnv));
          callback.receiveResultgetLongRunningInstances((org.wso2.bps.management.schema.GetLongRunningInstancesResponse) object);

        } catch (final org.apache.axis2.AxisFault e) {
          callback.receiveErrorgetLongRunningInstances(e);
        }
      }

      @Override
      public void onError(final java.lang.Exception error) {
        if (error instanceof org.apache.axis2.AxisFault) {
          final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
          final org.apache.axiom.om.OMElement faultElt = f.getDetail();
          if (faultElt != null) {
            if (InstanceManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
              faultElt.getQName(), "getLongRunningInstances"))) {
              // make the fault by reflection
              try {
                final java.lang.String exceptionClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getLongRunningInstances"));
                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                // message class
                final java.lang.String messageClassName =
                  (java.lang.String) InstanceManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                    faultElt.getQName(), "getLongRunningInstances"));
                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                final java.lang.reflect.Method m =
                  exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                m.invoke(ex, new java.lang.Object[] {messageObject});

                if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
                  callback.receiveErrorgetLongRunningInstances(ex);
                  return;
                }


                callback.receiveErrorgetLongRunningInstances(new java.rmi.RemoteException(
                  ex.getMessage(), ex));
              } catch (final java.lang.ClassCastException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              } catch (final java.lang.ClassNotFoundException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              } catch (final java.lang.NoSuchMethodException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              } catch (final java.lang.reflect.InvocationTargetException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              } catch (final java.lang.IllegalAccessException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              } catch (final java.lang.InstantiationException e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              } catch (final org.apache.axis2.AxisFault e) {
                // we cannot intantiate the class - throw the original Axis fault
                callback.receiveErrorgetLongRunningInstances(f);
              }
            } else {
              callback.receiveErrorgetLongRunningInstances(f);
            }
          } else {
            callback.receiveErrorgetLongRunningInstances(f);
          }
        } else {
          callback.receiveErrorgetLongRunningInstances(error);
        }
      }

      @Override
      public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
        final org.apache.axis2.AxisFault fault =
          org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
        onError(fault);
      }

      @Override
      public void onComplete() {
        try {
          _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        } catch (final org.apache.axis2.AxisFault axisFault) {
          callback.receiveErrorgetLongRunningInstances(axisFault);
        }
      }
    });


    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (this._operations[8].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      this._operations[8].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);

  }


  /**
   * Auto generated method signature
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */
  @Override
  public void recoverActivity(final org.wso2.bps.management.schema.RecoverActivity recoverActivity36

  ) throws java.rmi.RemoteException


    , org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;

    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[9].getName());
      _operationClient.getOptions().setAction("urn:recoverActivity");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");

      org.apache.axiom.soap.SOAPEnvelope env = null;
      _messageContext = new org.apache.axis2.context.MessageContext();


      // Style is Doc.


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), recoverActivity36,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "recoverActivity")));


      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // create message context with that soap envelope

      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      _operationClient.execute(true);


    } catch (final org.apache.axis2.AxisFault f) {
      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "recoverActivity"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "recoverActivity"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "recoverActivity"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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

    return;
  }


  /**
   * Auto generated method signature
   *
   * @throws org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException :
   */
  @Override
  public void terminateInstance(final org.wso2.bps.management.schema.TerminateInstance terminateInstance37

  ) throws java.rmi.RemoteException


    , org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException {
    org.apache.axis2.context.MessageContext _messageContext = null;

    try {
      final org.apache.axis2.client.OperationClient _operationClient =
        this._serviceClient.createClient(this._operations[10].getName());
      _operationClient.getOptions().setAction("urn:terminateInstance");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);


      addPropertyToOperationClient(_operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");

      org.apache.axiom.soap.SOAPEnvelope env = null;
      _messageContext = new org.apache.axis2.context.MessageContext();


      // Style is Doc.


      env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), terminateInstance37,
        optimizeContent(new javax.xml.namespace.QName(
          "http://wso2.org/bps/management/wsdl/InstanceManagement", "terminateInstance")));


      // adding SOAP soap_headers
      this._serviceClient.addHeadersToEnvelope(env);
      // create message context with that soap envelope

      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      _operationClient.execute(true);


    } catch (final org.apache.axis2.AxisFault f) {
      final org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
          "terminateInstance"))) {
          // make the fault by reflection
          try {
            final java.lang.String exceptionClassName =
              (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "terminateInstance"));
            final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
            final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
            // message class
            final java.lang.String messageClassName =
              (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                faultElt.getQName(), "terminateInstance"));
            final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
            final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
            final java.lang.reflect.Method m =
              exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
            m.invoke(ex, new java.lang.Object[] {messageObject});

            if (ex instanceof org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) {
              throw (org.wso2.bps.management.wsdl.instancemanagement.InstanceManagementException) ex;
            }


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

    return;
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

  // http://localhost:9763/services/InstanceManagementService
  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.InstanceSummaryE param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.InstanceSummaryE.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.InstanceManagementException param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.InstanceManagementException.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ResumeInstance param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.ResumeInstance.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetPaginatedInstanceListInput param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.GetPaginatedInstanceListInput.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.PaginatedInstanceList param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.PaginatedInstanceList.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ActivityLifeCycleEvents param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.ActivityLifeCycleEvents.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetInstanceInfoIn param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.GetInstanceInfoIn.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.InstanceInfo param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.InstanceInfo.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.SuspendInstance param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.SuspendInstance.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.DeleteInstances param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.DeleteInstances.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.DeleteInstanceResponse param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.DeleteInstanceResponse.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.InstanceInfoWithEvents param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.InstanceInfoWithEvents.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetLongRunningInstancesInput param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.GetLongRunningInstancesInput.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetLongRunningInstancesResponse param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.GetLongRunningInstancesResponse.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.RecoverActivity param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.RecoverActivity.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.TerminateInstance param,
                                             final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(org.wso2.bps.management.schema.TerminateInstance.MY_QNAME,
        org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.ResumeInstance param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.bps.management.schema.ResumeInstance.MY_QNAME, factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.GetPaginatedInstanceListInput param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.bps.management.schema.GetPaginatedInstanceListInput.MY_QNAME,
          factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn.MY_QNAME,
          factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.GetInstanceInfoIn param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.bps.management.schema.GetInstanceInfoIn.MY_QNAME,
          factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.SuspendInstance param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody().addChild(param.getOMElement(org.wso2.bps.management.schema.SuspendInstance.MY_QNAME,
        factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.DeleteInstances param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody().addChild(param.getOMElement(org.wso2.bps.management.schema.DeleteInstances.MY_QNAME,
        factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.GetLongRunningInstancesInput param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.bps.management.schema.GetLongRunningInstancesInput.MY_QNAME,
          factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.RecoverActivity param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody().addChild(param.getOMElement(org.wso2.bps.management.schema.RecoverActivity.MY_QNAME,
        factory));
      return emptyEnvelope;
    } catch (final org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }


  /* methods to provide back word compatibility */


  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                        final org.wso2.bps.management.schema.TerminateInstance param,
                                                        final boolean optimizeContent) throws org.apache.axis2.AxisFault {


    try {

      final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope.getBody()
        .addChild(param.getOMElement(org.wso2.bps.management.schema.TerminateInstance.MY_QNAME,
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

      if (org.wso2.bps.management.schema.InstanceSummaryE.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceSummaryE.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.ResumeInstance.class.equals(type)) {

        return org.wso2.bps.management.schema.ResumeInstance.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.GetPaginatedInstanceListInput.class.equals(type)) {

        return org.wso2.bps.management.schema.GetPaginatedInstanceListInput.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.PaginatedInstanceList.class.equals(type)) {

        return org.wso2.bps.management.schema.PaginatedInstanceList.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn.class.equals(type)) {

        return org.wso2.bps.management.schema.GetActivityLifeCycleFilterIn.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.ActivityLifeCycleEvents.class.equals(type)) {

        return org.wso2.bps.management.schema.ActivityLifeCycleEvents.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.GetInstanceInfoIn.class.equals(type)) {

        return org.wso2.bps.management.schema.GetInstanceInfoIn.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceInfo.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceInfo.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.SuspendInstance.class.equals(type)) {

        return org.wso2.bps.management.schema.SuspendInstance.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.DeleteInstances.class.equals(type)) {

        return org.wso2.bps.management.schema.DeleteInstances.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.DeleteInstanceResponse.class.equals(type)) {

        return org.wso2.bps.management.schema.DeleteInstanceResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.GetInstanceInfoIn.class.equals(type)) {

        return org.wso2.bps.management.schema.GetInstanceInfoIn.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceInfoWithEvents.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceInfoWithEvents.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.GetLongRunningInstancesInput.class.equals(type)) {

        return org.wso2.bps.management.schema.GetLongRunningInstancesInput.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.GetLongRunningInstancesResponse.class.equals(type)) {

        return org.wso2.bps.management.schema.GetLongRunningInstancesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.RecoverActivity.class.equals(type)) {

        return org.wso2.bps.management.schema.RecoverActivity.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.TerminateInstance.class.equals(type)) {

        return org.wso2.bps.management.schema.TerminateInstance.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (org.wso2.bps.management.schema.InstanceManagementException.class.equals(type)) {

        return org.wso2.bps.management.schema.InstanceManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

    } catch (final java.lang.Exception e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
    return null;
  }


}
