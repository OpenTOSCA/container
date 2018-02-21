
/**
 * AuthenticationAdminStub.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:33:10 UTC)
 */
package org.wso2.carbon.core.services.authentication;

import javax.xml.namespace.QName;

/*
 * AuthenticationAdminStub java implementation
 */


public class AuthenticationAdminStub extends org.apache.axis2.client.Stub implements AuthenticationAdmin {
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
        this._service = new org.apache.axis2.description.AxisService("AuthenticationAdmin" + getUniqueSuffix());
        addAnonymousOperations();

        // creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        this._operations = new org.apache.axis2.description.AxisOperation[7];

        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
            "loginWithRememberMeOption"));
        this._service.addOperation(__operation);



        this._operations[0] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(
            new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "login"));
        this._service.addOperation(__operation);



        this._operations[1] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
            "getAuthenticatorName"));
        this._service.addOperation(__operation);



        this._operations[2] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(
            new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "getPriority"));
        this._service.addOperation(__operation);



        this._operations[3] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(
            new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "isDisabled"));
        this._service.addOperation(__operation);



        this._operations[4] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
            "authenticateWithRememberMe"));
        this._service.addOperation(__operation);



        this._operations[5] = __operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
            "loginWithRememberMeCookie"));
        this._service.addOperation(__operation);



        this._operations[6] = __operation;


    }

    // populates the faults
    private void populateFaults() {

        this.faultExceptionNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "AuthenticationAdminAuthenticationException"),
                "loginWithRememberMeOption"),
            "org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException");
        this.faultExceptionClassNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "AuthenticationAdminAuthenticationException"),
                "loginWithRememberMeOption"),
            "org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException");
        this.faultMessageMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "AuthenticationAdminAuthenticationException"),
                "loginWithRememberMeOption"),
            "org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException");

        this.faultExceptionNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "AuthenticationAdminAuthenticationException"),
                "login"),
            "org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException");
        this.faultExceptionClassNameMap.put(
            new org.apache.axis2.client.FaultMapKey(
                new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "AuthenticationAdminAuthenticationException"),
                "login"),
            "org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException");
        this.faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
            "http://authentication.services.core.carbon.wso2.org", "AuthenticationAdminAuthenticationException"),
            "login"), "org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException");



    }

    /**
     * Constructor that takes in a configContext
     */

    public AuthenticationAdminStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                                   final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }


    /**
     * Constructor that takes in a configContext and useseperate listner
     */
    public AuthenticationAdminStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public AuthenticationAdminStub(final org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {

        this(configurationContext,
             "https://192.168.178.21:9443/services/AuthenticationAdmin.AuthenticationAdminHttpsSoap11Endpoint/");

    }

    /**
     * Default Constructor
     */
    public AuthenticationAdminStub() throws org.apache.axis2.AxisFault {

        this("https://192.168.178.21:9443/services/AuthenticationAdmin.AuthenticationAdminHttpsSoap11Endpoint/");

    }

    /**
     * Constructor taking the target endpoint
     */
    public AuthenticationAdminStub(final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }



    /**
     * Auto generated method signature
     *
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#loginWithRememberMeOption
     * @param loginWithRememberMeOption14
     *
     * @throws org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException
     *         :
     */



    @Override
    public org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse loginWithRememberMeOption(

                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption loginWithRememberMeOption14)


        throws java.rmi.RemoteException


        , org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[0].getName());
            _operationClient.getOptions().setAction("urn:loginWithRememberMeOption");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), loginWithRememberMeOption14,
                optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "loginWithRememberMeOption")));

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
                org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse.class,
                getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "loginWithRememberMeOption"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "loginWithRememberMeOption"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "loginWithRememberMeOption"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException) {
                            throw (org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException) ex;
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startloginWithRememberMeOption
     * @param loginWithRememberMeOption14
     *
     */
    @Override
    public void startloginWithRememberMeOption(

                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption loginWithRememberMeOption14,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[0].getName());
        _operationClient.getOptions().setAction("urn:loginWithRememberMeOption");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), loginWithRememberMeOption14,
            optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                "loginWithRememberMeOption")));

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
                        org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultloginWithRememberMeOption(
                        (org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorloginWithRememberMeOption(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                "loginWithRememberMeOption"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "loginWithRememberMeOption"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "loginWithRememberMeOption"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException) {
                                    callback.receiveErrorloginWithRememberMeOption(ex);
                                    return;
                                }


                                callback.receiveErrorloginWithRememberMeOption(
                                    new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeOption(f);
                            }
                        } else {
                            callback.receiveErrorloginWithRememberMeOption(f);
                        }
                    } else {
                        callback.receiveErrorloginWithRememberMeOption(f);
                    }
                } else {
                    callback.receiveErrorloginWithRememberMeOption(error);
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
                    callback.receiveErrorloginWithRememberMeOption(axisFault);
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#login
     * @param login16
     *
     * @throws org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException
     *         :
     */



    @Override
    public org.wso2.carbon.core.services.authentication.LoginResponse login(

                    final org.wso2.carbon.core.services.authentication.Login login16)


        throws java.rmi.RemoteException


        , org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[1].getName());
            _operationClient.getOptions().setAction("urn:login");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), login16, optimizeContent(
                new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "login")));

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
                org.wso2.carbon.core.services.authentication.LoginResponse.class, getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.LoginResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "login"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "login"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "login"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        if (ex instanceof org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException) {
                            throw (org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException) ex;
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startlogin
     * @param login16
     *
     */
    @Override
    public void startlogin(

                    final org.wso2.carbon.core.services.authentication.Login login16,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[1].getName());
        _operationClient.getOptions().setAction("urn:login");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), login16, optimizeContent(
            new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "login")));

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
                        org.wso2.carbon.core.services.authentication.LoginResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultlogin((org.wso2.carbon.core.services.authentication.LoginResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorlogin(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "login"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "login"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "login"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});

                                if (ex instanceof org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException) {
                                    callback.receiveErrorlogin(ex);
                                    return;
                                }


                                callback.receiveErrorlogin(new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlogin(f);
                            }
                        } else {
                            callback.receiveErrorlogin(f);
                        }
                    } else {
                        callback.receiveErrorlogin(f);
                    }
                } else {
                    callback.receiveErrorlogin(error);
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
                    callback.receiveErrorlogin(axisFault);
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
     * Auto generated method signature
     *
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#getAuthenticatorName
     * @param getAuthenticatorName18
     *
     */



    @Override
    public org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse getAuthenticatorName(

                    final org.wso2.carbon.core.services.authentication.GetAuthenticatorName getAuthenticatorName18)


        throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[2].getName());
            _operationClient.getOptions().setAction("urn:getAuthenticatorName");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getAuthenticatorName18,
                optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "getAuthenticatorName")));

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
                org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse.class,
                getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getAuthenticatorName"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getAuthenticatorName"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getAuthenticatorName"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startgetAuthenticatorName
     * @param getAuthenticatorName18
     *
     */
    @Override
    public void startgetAuthenticatorName(

                    final org.wso2.carbon.core.services.authentication.GetAuthenticatorName getAuthenticatorName18,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[2].getName());
        _operationClient.getOptions().setAction("urn:getAuthenticatorName");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getAuthenticatorName18,
            optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                "getAuthenticatorName")));

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
                        org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultgetAuthenticatorName(
                        (org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorgetAuthenticatorName(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getAuthenticatorName"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "getAuthenticatorName"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "getAuthenticatorName"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});


                                callback.receiveErrorgetAuthenticatorName(
                                    new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetAuthenticatorName(f);
                            }
                        } else {
                            callback.receiveErrorgetAuthenticatorName(f);
                        }
                    } else {
                        callback.receiveErrorgetAuthenticatorName(f);
                    }
                } else {
                    callback.receiveErrorgetAuthenticatorName(error);
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
                    callback.receiveErrorgetAuthenticatorName(axisFault);
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#getPriority
     * @param getPriority20
     *
     */



    @Override
    public org.wso2.carbon.core.services.authentication.GetPriorityResponse getPriority(

                    final org.wso2.carbon.core.services.authentication.GetPriority getPriority20)


        throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[3].getName());
            _operationClient.getOptions().setAction("urn:getPriority");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getPriority20,
                optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "getPriority")));

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
                org.wso2.carbon.core.services.authentication.GetPriorityResponse.class,
                getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.GetPriorityResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getPriority"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getPriority"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getPriority"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startgetPriority
     * @param getPriority20
     *
     */
    @Override
    public void startgetPriority(

                    final org.wso2.carbon.core.services.authentication.GetPriority getPriority20,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[3].getName());
        _operationClient.getOptions().setAction("urn:getPriority");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getPriority20, optimizeContent(
            new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "getPriority")));

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
                        org.wso2.carbon.core.services.authentication.GetPriorityResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultgetPriority(
                        (org.wso2.carbon.core.services.authentication.GetPriorityResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorgetPriority(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getPriority"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getPriority"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getPriority"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});


                                callback.receiveErrorgetPriority(new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorgetPriority(f);
                            }
                        } else {
                            callback.receiveErrorgetPriority(f);
                        }
                    } else {
                        callback.receiveErrorgetPriority(f);
                    }
                } else {
                    callback.receiveErrorgetPriority(error);
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
                    callback.receiveErrorgetPriority(axisFault);
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#isDisabled
     * @param isDisabled22
     *
     */



    @Override
    public org.wso2.carbon.core.services.authentication.IsDisabledResponse isDisabled(

                    final org.wso2.carbon.core.services.authentication.IsDisabled isDisabled22)


        throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[4].getName());
            _operationClient.getOptions().setAction("urn:isDisabled");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), isDisabled22,
                optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "isDisabled")));

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
                org.wso2.carbon.core.services.authentication.IsDisabledResponse.class,
                getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.IsDisabledResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "isDisabled"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "isDisabled"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "isDisabled"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startisDisabled
     * @param isDisabled22
     *
     */
    @Override
    public void startisDisabled(

                    final org.wso2.carbon.core.services.authentication.IsDisabled isDisabled22,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[4].getName());
        _operationClient.getOptions().setAction("urn:isDisabled");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), isDisabled22, optimizeContent(
            new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org", "isDisabled")));

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
                        org.wso2.carbon.core.services.authentication.IsDisabledResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultisDisabled(
                        (org.wso2.carbon.core.services.authentication.IsDisabledResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorisDisabled(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "isDisabled"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "isDisabled"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "isDisabled"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});


                                callback.receiveErrorisDisabled(new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorisDisabled(f);
                            }
                        } else {
                            callback.receiveErrorisDisabled(f);
                        }
                    } else {
                        callback.receiveErrorisDisabled(f);
                    }
                } else {
                    callback.receiveErrorisDisabled(error);
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
                    callback.receiveErrorisDisabled(axisFault);
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#authenticateWithRememberMe
     * @param authenticateWithRememberMe24
     *
     */



    @Override
    public org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse authenticateWithRememberMe(

                    final org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe authenticateWithRememberMe24)


        throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[5].getName());
            _operationClient.getOptions().setAction("urn:authenticateWithRememberMe");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                authenticateWithRememberMe24,
                optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "authenticateWithRememberMe")));

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
                org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse.class,
                getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "authenticateWithRememberMe"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "authenticateWithRememberMe"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "authenticateWithRememberMe"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startauthenticateWithRememberMe
     * @param authenticateWithRememberMe24
     *
     */
    @Override
    public void startauthenticateWithRememberMe(

                    final org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe authenticateWithRememberMe24,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[5].getName());
        _operationClient.getOptions().setAction("urn:authenticateWithRememberMe");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), authenticateWithRememberMe24,
            optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                "authenticateWithRememberMe")));

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
                        org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultauthenticateWithRememberMe(
                        (org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorauthenticateWithRememberMe(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                "authenticateWithRememberMe"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "authenticateWithRememberMe"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "authenticateWithRememberMe"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});


                                callback.receiveErrorauthenticateWithRememberMe(
                                    new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorauthenticateWithRememberMe(f);
                            }
                        } else {
                            callback.receiveErrorauthenticateWithRememberMe(f);
                        }
                    } else {
                        callback.receiveErrorauthenticateWithRememberMe(f);
                    }
                } else {
                    callback.receiveErrorauthenticateWithRememberMe(error);
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
                    callback.receiveErrorauthenticateWithRememberMe(axisFault);
                }
            }
        });


        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if (this._operations[5].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            this._operations[5].setMessageReceiver(_callbackReceiver);
        }

        // execute the operation client
        _operationClient.execute(false);

    }

    /**
     * Auto generated method signature
     *
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#loginWithRememberMeCookie
     * @param loginWithRememberMeCookie26
     *
     */



    @Override
    public org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse loginWithRememberMeCookie(

                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie loginWithRememberMeCookie26)


        throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
                this._operations[6].getName());
            _operationClient.getOptions().setAction("urn:loginWithRememberMeCookie");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");


            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), loginWithRememberMeCookie26,
                optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                    "loginWithRememberMeCookie")));

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
                org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse.class,
                getEnvelopeNamespaces(_returnEnv));


            return (org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse) object;

        } catch (final org.apache.axis2.AxisFault f) {

            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "loginWithRememberMeCookie"))) {
                    // make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName = (java.lang.String) this.faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "loginWithRememberMeCookie"));
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        final java.lang.String messageClassName = (java.lang.String) this.faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "loginWithRememberMeCookie"));
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                            new java.lang.Class[] {messageClass});
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
     * @see org.wso2.carbon.core.services.authentication.AuthenticationAdmin#startloginWithRememberMeCookie
     * @param loginWithRememberMeCookie26
     *
     */
    @Override
    public void startloginWithRememberMeCookie(

                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie loginWithRememberMeCookie26,

                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminCallbackHandler callback)

        throws java.rmi.RemoteException {

        final org.apache.axis2.client.OperationClient _operationClient = this._serviceClient.createClient(
            this._operations[6].getName());
        _operationClient.getOptions().setAction("urn:loginWithRememberMeCookie");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



        addPropertyToOperationClient(_operationClient,
            org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");



        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();


        // Style is Doc.


        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), loginWithRememberMeCookie26,
            optimizeContent(new javax.xml.namespace.QName("http://authentication.services.core.carbon.wso2.org",
                "loginWithRememberMeCookie")));

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
                        org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse.class,
                        getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultloginWithRememberMeCookie(
                        (org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse) object);

                } catch (final org.apache.axis2.AxisFault e) {
                    callback.receiveErrorloginWithRememberMeCookie(e);
                }
            }

            @Override
            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (AuthenticationAdminStub.this.faultExceptionNameMap.containsKey(
                            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                "loginWithRememberMeCookie"))) {
                            // make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName = (java.lang.String) AuthenticationAdminStub.this.faultExceptionClassNameMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "loginWithRememberMeCookie"));
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                // message class
                                final java.lang.String messageClassName = (java.lang.String) AuthenticationAdminStub.this.faultMessageMap.get(
                                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
                                        "loginWithRememberMeCookie"));
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] {messageClass});
                                m.invoke(ex, new java.lang.Object[] {messageObject});


                                callback.receiveErrorloginWithRememberMeCookie(
                                    new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (final java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            } catch (final java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            } catch (final java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            } catch (final java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            } catch (final java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            } catch (final java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            } catch (final org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorloginWithRememberMeCookie(f);
                            }
                        } else {
                            callback.receiveErrorloginWithRememberMeCookie(f);
                        }
                    } else {
                        callback.receiveErrorloginWithRememberMeCookie(f);
                    }
                } else {
                    callback.receiveErrorloginWithRememberMeCookie(error);
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
                    callback.receiveErrorloginWithRememberMeCookie(axisFault);
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

    // https://192.168.178.21:9443/services/AuthenticationAdmin.AuthenticationAdminHttpsSoap11Endpoint/
    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(
                org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(
                org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.carbon.core.services.authentication.Login param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.Login.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.carbon.core.services.authentication.LoginResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.LoginResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.GetAuthenticatorName param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.GetAuthenticatorName.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(
                org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.carbon.core.services.authentication.GetPriority param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.GetPriority.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.GetPriorityResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.GetPriorityResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(final org.wso2.carbon.core.services.authentication.IsDisabled param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.IsDisabled.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.IsDisabledResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.IsDisabledResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(
                org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }

    private org.apache.axiom.om.OMElement toOM(
                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(
                org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(
                org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.Login param, final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                param.getOMElement(org.wso2.carbon.core.services.authentication.Login.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.GetAuthenticatorName param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(
                org.wso2.carbon.core.services.authentication.GetAuthenticatorName.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.GetPriority param, final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                param.getOMElement(org.wso2.carbon.core.services.authentication.GetPriority.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.IsDisabled param, final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                param.getOMElement(org.wso2.carbon.core.services.authentication.IsDisabled.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(
                org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (final org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }


    }


    /* methods to provide back word compatibility */



    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                    final org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie param,
                    final boolean optimizeContent)
        throws org.apache.axis2.AxisFault {


        try {

            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(
                org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie.MY_QNAME, factory));
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

            if (org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.LoginWithRememberMeOption.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.LoginWithRememberMeOptionResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException.class.equals(
                type)) {

                return org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.Login.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.Login.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.LoginResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.LoginResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException.class.equals(
                type)) {

                return org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationException.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.GetAuthenticatorName.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.GetAuthenticatorName.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.GetAuthenticatorNameResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.GetPriority.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.GetPriority.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.GetPriorityResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.GetPriorityResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.IsDisabled.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.IsDisabled.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.IsDisabledResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.IsDisabledResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMe.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.AuthenticateWithRememberMeResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookie.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

            if (org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse.class.equals(type)) {

                return org.wso2.carbon.core.services.authentication.LoginWithRememberMeCookieResponse.Factory.parse(
                    param.getXMLStreamReaderWithoutCaching());


            }

        } catch (final java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }



}
