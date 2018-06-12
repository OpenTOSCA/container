
/**
 * ProcessManagementServiceStub.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */
package org.wso2.bps.management.wsdl.processmanagement;

import javax.xml.namespace.QName;

/*
 * ProcessManagementServiceStub java implementation
 */


public class ProcessManagementServiceStub extends org.apache.axis2.client.Stub implements ProcessManagementService {
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
        this._service = new org.apache.axis2.description.AxisService("ProcessManagementService" + getUniqueSuffix());
        addAnonymousOperations();

        // creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        this._operations = new org.apache.axis2.description.AxisOperation[5];

        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/ProcessManagement",
            "getAllProcesses"));
        this._service.addOperation(__operation);



        this._operations[0] = __operation;


        __operation = new org.apache.axis2.description.RobustOutOnlyAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/ProcessManagement",
            "retireProcess"));
        this._service.addOperation(__operation);



        this._operations[1] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/ProcessManagement",
            "getPaginatedProcessList"));
        this._service.addOperation(__operation);



        this._operations[2] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/ProcessManagement",
            "getProcessInfo"));
        this._service.addOperation(__operation);



        this._operations[3] = __operation;


        __operation = new org.apache.axis2.description.RobustOutOnlyAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/ProcessManagement",
            "activateProcess"));
        this._service.addOperation(__operation);



        this._operations[4] = __operation;


    }

    // populates the faults
    private void populateFaults() {

        this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getAllProcesses"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getAllProcesses"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getAllProcesses"), "org.wso2.bps.management.schema.ProcessManagementException");

        this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "retireProcess"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "retireProcess"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "retireProcess"), "org.wso2.bps.management.schema.ProcessManagementException");

        this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getPaginatedProcessList"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getPaginatedProcessList"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getPaginatedProcessList"), "org.wso2.bps.management.schema.ProcessManagementException");

        this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getProcessInfo"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getProcessInfo"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "getProcessInfo"), "org.wso2.bps.management.schema.ProcessManagementException");

        this.faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "activateProcess"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "activateProcess"), "org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "processManagementException"),
            "activateProcess"), "org.wso2.bps.management.schema.ProcessManagementException");



    }

    /**
     * Constructor that takes in a configContext
     */

    public ProcessManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                                        final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }


    /**
     * Constructor that takes in a configContext and useseperate listner
     */
    public ProcessManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public ProcessManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {

        this(configurationContext, "http://localhost:9763/services/ProcessManagementService");

    }

    /**
     * Default Constructor
     */
    public ProcessManagementServiceStub() throws org.apache.axis2.AxisFault {

        this("http://localhost:9763/services/ProcessManagementService");

    }

    /**
     * Constructor taking the target endpoint
     */
    public ProcessManagementServiceStub(final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }



    /**
     * Auto generated method signature
     *
     * @see org.wso2.bps.management.wsdl.processmanagement.ProcessManagementService#getAllProcesses
     * @param getAllProcesses8
     *
     * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException :
     */



    @Override
    public org.wso2.bps.management.schema.ProcessIDList getAllProcesses(

                                                                        final org.wso2.bps.management.schema.GetAllProcesses getAllProcesses8)


                                                                                                                                               throws java.rmi.RemoteException


                                                                                                                                               ,
                                                                                                                                               org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                this._serviceClient.createClient(this._operations[0].getName());
            _operationClient.getOptions().setAction("urn:getAllProcesses");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getAllProcesses8,
                             optimizeContent(new javax.xml.namespace.QName(
                                 "http://wso2.org/bps/management/wsdl/ProcessManagement", "getAllProcesses")));

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
                fromOM(_returnEnv.getBody().getFirstElement(), org.wso2.bps.management.schema.ProcessIDList.class,
                       getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.bps.management.schema.ProcessIDList) object;

        }
        catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                    "getAllProcesses"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                            (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "getAllProcesses"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName =
                            (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "getAllProcesses"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                            exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                            throw (org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) ex;
                        }


                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (final java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
        finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature for Asynchronous Invocations
     *
     * @see org.wso2.bps.management.wsdl.processmanagement.ProcessManagementService#startgetAllProcesses
     * @param getAllProcesses8
     *
     */
    @Override
    public void startgetAllProcesses(

                                     final org.wso2.bps.management.schema.GetAllProcesses getAllProcesses8,

                                     final org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceCallbackHandler callback)

                                                                                                                                            throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient =
            this._serviceClient.createClient(this._operations[0].getName());
        _operationClient.getOptions().setAction("urn:getAllProcesses");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getAllProcesses8,
                         optimizeContent(new javax.xml.namespace.QName(
                             "http://wso2.org/bps/management/wsdl/ProcessManagement", "getAllProcesses")));

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
                               org.wso2.bps.management.schema.ProcessIDList.class, getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultgetAllProcesses((org.wso2.bps.management.schema.ProcessIDList) object);

                }
                catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorgetAllProcesses(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (ProcessManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
                            faultElt.getQName(), "getAllProcesses"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName =
                                    (java.lang.String) ProcessManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "getAllProcesses"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName =
                                    (java.lang.String) ProcessManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "getAllProcesses"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m =
                                    exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                                    callback.receiveErrorgetAllProcesses(ex);
                                    return;
                                }


                                callback.receiveErrorgetAllProcesses(new java.rmi.RemoteException(ex.getMessage(), ex));
                            }
                            catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                            catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                            catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                            catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                            catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                            catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                            catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAllProcesses(f);
                            }
                        } else {
                            callback.receiveErrorgetAllProcesses(f);
                        }
                    } else {
                        callback.receiveErrorgetAllProcesses(f);
                    }
                } else {
                    callback.receiveErrorgetAllProcesses(error);
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
                }
                catch (final org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorgetAllProcesses(axisFault);
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
     * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException :
     */
    @Override
    public void retireProcess(final org.wso2.bps.management.schema.RetireProcessIn retireProcessIn10

    ) throws java.rmi.RemoteException


      , org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;

        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                this._serviceClient.createClient(this._operations[1].getName());
            _operationClient.getOptions().setAction("urn:retireProcess");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");

            org.apache.axiom.soap.SOAPEnvelope env = null;
            _messageContext = new org.apache.axis2.context.MessageContext();


            // Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), retireProcessIn10,
                             optimizeContent(new javax.xml.namespace.QName(
                                 "http://wso2.org/bps/management/wsdl/ProcessManagement", "retireProcess")));


            // adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // create message context with that soap envelope

            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            _operationClient.execute(true);


        }
        catch (final org.apache.axis2.AxisFault f) {
            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                    "retireProcess"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                            (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "retireProcess"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName =
                            (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "retireProcess"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                            exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                            throw (org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) ex;
                        }


                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (final java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
        finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }

        return;
    }

    /**
     * Auto generated method signature
     *
     * @see org.wso2.bps.management.wsdl.processmanagement.ProcessManagementService#getPaginatedProcessList
     * @param getPaginatedProcessListInput11
     *
     * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException :
     */



    @Override
    public org.wso2.bps.management.schema.PaginatedProcessInfoList getPaginatedProcessList(

                                                                                           final org.wso2.bps.management.schema.GetPaginatedProcessListInput getPaginatedProcessListInput11)


                                                                                                                                                                                             throws java.rmi.RemoteException


                                                                                                                                                                                             ,
                                                                                                                                                                                             org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                this._serviceClient.createClient(this._operations[2].getName());
            _operationClient.getOptions().setAction("urn:getPaginatedProcessList");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                             getPaginatedProcessListInput11, optimizeContent(new javax.xml.namespace.QName(
                                 "http://wso2.org/bps/management/wsdl/ProcessManagement", "getPaginatedProcessList")));

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
                                                   org.wso2.bps.management.schema.PaginatedProcessInfoList.class,
                                                   getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.bps.management.schema.PaginatedProcessInfoList) object;

        }
        catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                    "getPaginatedProcessList"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                            (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "getPaginatedProcessList"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName =
                            (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "getPaginatedProcessList"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                            exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                            throw (org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) ex;
                        }


                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (final java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
        finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature for Asynchronous Invocations
     *
     * @see org.wso2.bps.management.wsdl.processmanagement.ProcessManagementService#startgetPaginatedProcessList
     * @param getPaginatedProcessListInput11
     *
     */
    @Override
    public void startgetPaginatedProcessList(

                                             final org.wso2.bps.management.schema.GetPaginatedProcessListInput getPaginatedProcessListInput11,

                                             final org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceCallbackHandler callback)

                                                                                                                                                    throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient =
            this._serviceClient.createClient(this._operations[2].getName());
        _operationClient.getOptions().setAction("urn:getPaginatedProcessList");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getPaginatedProcessListInput11,
                         optimizeContent(new javax.xml.namespace.QName(
                             "http://wso2.org/bps/management/wsdl/ProcessManagement", "getPaginatedProcessList")));

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
                               org.wso2.bps.management.schema.PaginatedProcessInfoList.class,
                               getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultgetPaginatedProcessList((org.wso2.bps.management.schema.PaginatedProcessInfoList) object);

                }
                catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorgetPaginatedProcessList(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (ProcessManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
                            faultElt.getQName(), "getPaginatedProcessList"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName =
                                    (java.lang.String) ProcessManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "getPaginatedProcessList"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName =
                                    (java.lang.String) ProcessManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "getPaginatedProcessList"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m =
                                    exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                                    callback.receiveErrorgetPaginatedProcessList(ex);
                                    return;
                                }


                                callback.receiveErrorgetPaginatedProcessList(new java.rmi.RemoteException(
                                    ex.getMessage(), ex));
                            }
                            catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                            catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                            catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                            catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                            catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                            catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                            catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPaginatedProcessList(f);
                            }
                        } else {
                            callback.receiveErrorgetPaginatedProcessList(f);
                        }
                    } else {
                        callback.receiveErrorgetPaginatedProcessList(f);
                    }
                } else {
                    callback.receiveErrorgetPaginatedProcessList(error);
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
                }
                catch (final org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorgetPaginatedProcessList(axisFault);
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
     * @see org.wso2.bps.management.wsdl.processmanagement.ProcessManagementService#getProcessInfo
     * @param getProcessInfoIn13
     *
     * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException :
     */



    @Override
    public org.wso2.bps.management.schema.ProcessInfo getProcessInfo(

                                                                     final org.wso2.bps.management.schema.GetProcessInfoIn getProcessInfoIn13)


                                                                                                                                               throws java.rmi.RemoteException


                                                                                                                                               ,
                                                                                                                                               org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                this._serviceClient.createClient(this._operations[3].getName());
            _operationClient.getOptions().setAction("urn:getProcessInfo");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getProcessInfoIn13,
                             optimizeContent(new javax.xml.namespace.QName(
                                 "http://wso2.org/bps/management/wsdl/ProcessManagement", "getProcessInfo")));

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
                fromOM(_returnEnv.getBody().getFirstElement(), org.wso2.bps.management.schema.ProcessInfo.class,
                       getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.bps.management.schema.ProcessInfo) object;

        }
        catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                    "getProcessInfo"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                            (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "getProcessInfo"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName =
                            (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "getProcessInfo"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                            exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                            throw (org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) ex;
                        }


                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (final java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
        finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature for Asynchronous Invocations
     *
     * @see org.wso2.bps.management.wsdl.processmanagement.ProcessManagementService#startgetProcessInfo
     * @param getProcessInfoIn13
     *
     */
    @Override
    public void startgetProcessInfo(

                                    final org.wso2.bps.management.schema.GetProcessInfoIn getProcessInfoIn13,

                                    final org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceCallbackHandler callback)

                                                                                                                                           throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient =
            this._serviceClient.createClient(this._operations[3].getName());
        _operationClient.getOptions().setAction("urn:getProcessInfo");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getProcessInfoIn13,
                         optimizeContent(new javax.xml.namespace.QName(
                             "http://wso2.org/bps/management/wsdl/ProcessManagement", "getProcessInfo")));

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
                        fromOM(resultEnv.getBody().getFirstElement(), org.wso2.bps.management.schema.ProcessInfo.class,
                               getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultgetProcessInfo((org.wso2.bps.management.schema.ProcessInfo) object);

                }
                catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorgetProcessInfo(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (ProcessManagementServiceStub.this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
                            faultElt.getQName(), "getProcessInfo"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName =
                                    (java.lang.String) ProcessManagementServiceStub.this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "getProcessInfo"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName =
                                    (java.lang.String) ProcessManagementServiceStub.this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "getProcessInfo"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m =
                                    exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                                    callback.receiveErrorgetProcessInfo(ex);
                                    return;
                                }


                                callback.receiveErrorgetProcessInfo(new java.rmi.RemoteException(ex.getMessage(), ex));
                            }
                            catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                            catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                            catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                            catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                            catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                            catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                            catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetProcessInfo(f);
                            }
                        } else {
                            callback.receiveErrorgetProcessInfo(f);
                        }
                    } else {
                        callback.receiveErrorgetProcessInfo(f);
                    }
                } else {
                    callback.receiveErrorgetProcessInfo(error);
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
                }
                catch (final org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorgetProcessInfo(axisFault);
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
     * @throws org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException :
     */
    @Override
    public void activateProcess(final org.wso2.bps.management.schema.ActivateProcessIn activateProcessIn15

    ) throws java.rmi.RemoteException


      , org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;

        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                this._serviceClient.createClient(this._operations[4].getName());
            _operationClient.getOptions().setAction("urn:activateProcess");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");

            org.apache.axiom.soap.SOAPEnvelope env = null;
            _messageContext = new org.apache.axis2.context.MessageContext();


            // Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), activateProcessIn15,
                             optimizeContent(new javax.xml.namespace.QName(
                                 "http://wso2.org/bps/management/wsdl/ProcessManagement", "activateProcess")));


            // adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // create message context with that soap envelope

            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            _operationClient.execute(true);


        }
        catch (final org.apache.axis2.AxisFault f) {
            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                    "activateProcess"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                            (java.lang.String) this.faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "activateProcess"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName =
                            (java.lang.String) this.faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "activateProcess"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                            exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) {
                            throw (org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException) ex;
                        }


                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (final java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (final java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
        finally {
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

    // http://localhost:9763/services/ProcessManagementService
    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetAllProcesses param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.GetAllProcesses.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ProcessIDList param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.ProcessIDList.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ProcessManagementException param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.ProcessManagementException.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.RetireProcessIn param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.RetireProcessIn.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetPaginatedProcessListInput param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.GetPaginatedProcessListInput.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.PaginatedProcessInfoList param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.PaginatedProcessInfoList.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.GetProcessInfoIn param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.GetProcessInfoIn.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ProcessInfo param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.ProcessInfo.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ActivateProcessIn param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.ActivateProcessIn.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final org.wso2.bps.management.schema.GetAllProcesses param,
                                                          final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(org.wso2.bps.management.schema.GetAllProcesses.MY_QNAME,
                                                                factory));
            return emptyEnvelope;
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final org.wso2.bps.management.schema.RetireProcessIn param,
                                                          final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(org.wso2.bps.management.schema.RetireProcessIn.MY_QNAME,
                                                                factory));
            return emptyEnvelope;
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final org.wso2.bps.management.schema.GetPaginatedProcessListInput param,
                                                          final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(org.wso2.bps.management.schema.GetPaginatedProcessListInput.MY_QNAME,
                                                      factory));
            return emptyEnvelope;
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final org.wso2.bps.management.schema.GetProcessInfoIn param,
                                                          final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(org.wso2.bps.management.schema.GetProcessInfoIn.MY_QNAME,
                                                      factory));
            return emptyEnvelope;
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final org.wso2.bps.management.schema.ActivateProcessIn param,
                                                          final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(org.wso2.bps.management.schema.ActivateProcessIn.MY_QNAME,
                                                      factory));
            return emptyEnvelope;
        }
        catch (final org.apache.axis2.databinding.ADBException e) {
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

            if (org.wso2.bps.management.schema.GetAllProcesses.class.equals(type)) {

                return org.wso2.bps.management.schema.GetAllProcesses.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessIDList.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessIDList.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.RetireProcessIn.class.equals(type)) {

                return org.wso2.bps.management.schema.RetireProcessIn.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.GetPaginatedProcessListInput.class.equals(type)) {

                return org.wso2.bps.management.schema.GetPaginatedProcessListInput.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.PaginatedProcessInfoList.class.equals(type)) {

                return org.wso2.bps.management.schema.PaginatedProcessInfoList.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.GetProcessInfoIn.class.equals(type)) {

                return org.wso2.bps.management.schema.GetProcessInfoIn.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessInfo.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessInfo.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ActivateProcessIn.class.equals(type)) {

                return org.wso2.bps.management.schema.ActivateProcessIn.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ProcessManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.ProcessManagementException.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

        }
        catch (final java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }



}
