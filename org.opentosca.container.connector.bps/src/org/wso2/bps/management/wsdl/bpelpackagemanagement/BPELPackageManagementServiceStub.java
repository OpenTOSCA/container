
/**
 * BPELPackageManagementServiceStub.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */
package org.wso2.bps.management.wsdl.bpelpackagemanagement;

import javax.xml.namespace.QName;

/*
 * BPELPackageManagementServiceStub java implementation
 */


public class BPELPackageManagementServiceStub extends org.apache.axis2.client.Stub
                                              implements BPELPackageManagementService {
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
        this._service = new org.apache.axis2.description.AxisService(
            "BPELPackageManagementService" + getUniqueSuffix());
        addAnonymousOperations();

        // creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        this._operations = new org.apache.axis2.description.AxisOperation[2];

        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/BPELPackageManagement",
            "undeployBPELPackage"));
        this._service.addOperation(__operation);



        this._operations[0] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/BPELPackageManagement",
            "listDeployedPackagesPaginated"));
        this._service.addOperation(__operation);



        this._operations[1] = __operation;


    }

    // populates the faults
    private void populateFaults() {

        this.faultExceptionNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "packageManagementException"),
                "undeployBPELPackage"),
            "org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException");
        this.faultExceptionClassNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "packageManagementException"),
                "undeployBPELPackage"),
            "org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "packageManagementException"),
            "undeployBPELPackage"), "org.wso2.bps.management.schema.PackageManagementException");

        this.faultExceptionNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "packageManagementException"),
                "listDeployedPackagesPaginated"),
            "org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException");
        this.faultExceptionClassNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "packageManagementException"),
                "listDeployedPackagesPaginated"),
            "org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(
            new javax.xml.namespace.QName("http://wso2.org/bps/management/schema", "packageManagementException"),
            "listDeployedPackagesPaginated"), "org.wso2.bps.management.schema.PackageManagementException");



    }

    /**
     * Constructor that takes in a configContext
     */

    public BPELPackageManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                                            final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }


    /**
     * Constructor that takes in a configContext and useseperate listner
     */
    public BPELPackageManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public BPELPackageManagementServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {

        this(configurationContext, "http://localhost:9763/services/BPELPackageManagementService");

    }

    /**
     * Default Constructor
     */
    public BPELPackageManagementServiceStub() throws org.apache.axis2.AxisFault {

        this("http://localhost:9763/services/BPELPackageManagementService");

    }

    /**
     * Constructor taking the target endpoint
     */
    public BPELPackageManagementServiceStub(final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }



    /**
     * Auto generated method signature
     *
     * @see org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementService#undeployBPELPackage
     * @param undeployBPELPackage4
     *
     * @throws org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException :
     */



    @Override
    public org.wso2.bps.management.schema.UndeployStatus undeployBPELPackage(

                    final org.wso2.bps.management.schema.UndeployBPELPackage undeployBPELPackage4)


        throws java.rmi.RemoteException


        , org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[0].getName());
            _operationClient.getOptions().setAction("urn:undeployBPELPackage");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), undeployBPELPackage4,
                optimizeContent(new javax.xml.namespace.QName(
                    "http://wso2.org/bps/management/wsdl/BPELPackageManagement", "undeployBPELPackage")));

            // adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            // execute the operation client
            _operationClient.execute(true);


            final org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


            final java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                org.wso2.bps.management.schema.UndeployStatus.class, getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.bps.management.schema.UndeployStatus) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "undeployBPELPackage"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "undeployBPELPackage"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "undeployBPELPackage"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException) {
                            throw (org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException) ex;
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
     * @see org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementService#startundeployBPELPackage
     * @param undeployBPELPackage4
     *
     */
    @Override
    public void startundeployBPELPackage(

                    final org.wso2.bps.management.schema.UndeployBPELPackage undeployBPELPackage4,

                    final org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementServiceCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[0].getName());
        _operationClient.getOptions().setAction("urn:undeployBPELPackage");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), undeployBPELPackage4,
            optimizeContent(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/BPELPackageManagement",
                "undeployBPELPackage")));

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
                        org.wso2.bps.management.schema.UndeployStatus.class, getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultundeployBPELPackage((org.wso2.bps.management.schema.UndeployStatus) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorundeployBPELPackage(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (BPELPackageManagementServiceStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "undeployBPELPackage"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) BPELPackageManagementServiceStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "undeployBPELPackage"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) BPELPackageManagementServiceStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "undeployBPELPackage"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException) {
                                    callback.receiveErrorundeployBPELPackage(ex);
                                    return;
                                }


                                callback.receiveErrorundeployBPELPackage(
                                    new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorundeployBPELPackage(f);
                            }
                        } else {
                            callback.receiveErrorundeployBPELPackage(f);
                        }
                    } else {
                        callback.receiveErrorundeployBPELPackage(f);
                    }
                } else {
                    callback.receiveErrorundeployBPELPackage(error);
                }
            }

            @Override
            public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
                final org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(
                    faultContext);
                onError(fault);
            }

            @Override
            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (final org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorundeployBPELPackage(axisFault);
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
     * @see org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementService#listDeployedPackagesPaginated
     * @param listDeployedPackagesPaginated6
     *
     * @throws org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException :
     */



    @Override
    public org.wso2.bps.management.schema.DeployedPackagesPaginated listDeployedPackagesPaginated(

                    final org.wso2.bps.management.schema.ListDeployedPackagesPaginated listDeployedPackagesPaginated6)


        throws java.rmi.RemoteException


        , org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[1].getName());
            _operationClient.getOptions().setAction("urn:listDeployedPackagesPaginated");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                listDeployedPackagesPaginated6, optimizeContent(new javax.xml.namespace.QName(
                    "http://wso2.org/bps/management/wsdl/BPELPackageManagement", "listDeployedPackagesPaginated")));

            // adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            // execute the operation client
            _operationClient.execute(true);


            final org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


            final java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                org.wso2.bps.management.schema.DeployedPackagesPaginated.class, getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.bps.management.schema.DeployedPackagesPaginated) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "listDeployedPackagesPaginated"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                "listDeployedPackagesPaginated"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                "listDeployedPackagesPaginated"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException) {
                            throw (org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException) ex;
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
     * @see org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementService#startlistDeployedPackagesPaginated
     * @param listDeployedPackagesPaginated6
     *
     */
    @Override
    public void startlistDeployedPackagesPaginated(

                    final org.wso2.bps.management.schema.ListDeployedPackagesPaginated listDeployedPackagesPaginated6,

                    final org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementServiceCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[1].getName());
        _operationClient.getOptions().setAction("urn:listDeployedPackagesPaginated");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), listDeployedPackagesPaginated6,
            optimizeContent(new javax.xml.namespace.QName("http://wso2.org/bps/management/wsdl/BPELPackageManagement",
                "listDeployedPackagesPaginated")));

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
                        org.wso2.bps.management.schema.DeployedPackagesPaginated.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultlistDeployedPackagesPaginated(
                        (org.wso2.bps.management.schema.DeployedPackagesPaginated) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorlistDeployedPackagesPaginated(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (BPELPackageManagementServiceStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                "listDeployedPackagesPaginated"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) BPELPackageManagementServiceStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "listDeployedPackagesPaginated"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) BPELPackageManagementServiceStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "listDeployedPackagesPaginated"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException) {
                                    callback.receiveErrorlistDeployedPackagesPaginated(ex);
                                    return;
                                }


                                callback.receiveErrorlistDeployedPackagesPaginated(
                                    new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistDeployedPackagesPaginated(f);
                            }
                        } else {
                            callback.receiveErrorlistDeployedPackagesPaginated(f);
                        }
                    } else {
                        callback.receiveErrorlistDeployedPackagesPaginated(f);
                    }
                } else {
                    callback.receiveErrorlistDeployedPackagesPaginated(error);
                }
            }

            @Override
            public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
                final org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(
                    faultContext);
                onError(fault);
            }

            @Override
            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (final org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorlistDeployedPackagesPaginated(axisFault);
                }
            }
        });


        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if (this._operations[1].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            this._operations[1].setMessageReceiver(_callbackReceiver);
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

    // http://localhost:9763/services/BPELPackageManagementService
    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.UndeployBPELPackage param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.UndeployBPELPackage.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.UndeployStatus param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.UndeployStatus.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.PackageManagementException param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.PackageManagementException.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.ListDeployedPackagesPaginated param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.ListDeployedPackagesPaginated.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.bps.management.schema.DeployedPackagesPaginated param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.bps.management.schema.DeployedPackagesPaginated.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.bps.management.schema.UndeployBPELPackage param, final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                param.getOMElement(org.wso2.bps.management.schema.UndeployBPELPackage.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.bps.management.schema.ListDeployedPackagesPaginated param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                param.getOMElement(org.wso2.bps.management.schema.ListDeployedPackagesPaginated.MY_QNAME, factory));
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
                    final java.util.Map extraNamespaces)
        throws org.apache.axis2.AxisFault {

        try {

            if (org.wso2.bps.management.schema.UndeployBPELPackage.class.equals(type)) {

                return org.wso2.bps.management.schema.UndeployBPELPackage.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.UndeployStatus.class.equals(type)) {

                return org.wso2.bps.management.schema.UndeployStatus.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.PackageManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.PackageManagementException.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.ListDeployedPackagesPaginated.class.equals(type)) {

                return org.wso2.bps.management.schema.ListDeployedPackagesPaginated.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.DeployedPackagesPaginated.class.equals(type)) {

                return org.wso2.bps.management.schema.DeployedPackagesPaginated.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.bps.management.schema.PackageManagementException.class.equals(type)) {

                return org.wso2.bps.management.schema.PackageManagementException.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

        } catch (final java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }



}
