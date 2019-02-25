/**
 * ProcessManagementServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi;

public class ProcessManagementServiceLocator extends org.apache.axis.client.Service implements org.apache.www.ode.pmapi.ProcessManagementService {

    public ProcessManagementServiceLocator() {
    }


    public ProcessManagementServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProcessManagementServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ProcessManagementPort
    private java.lang.String ProcessManagementPort_address = "http://localhost:8080/ode/processes/ProcessManagement";

    public java.lang.String getProcessManagementPortAddress() {
        return ProcessManagementPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProcessManagementPortWSDDServiceName = "ProcessManagementPort";

    public java.lang.String getProcessManagementPortWSDDServiceName() {
        return ProcessManagementPortWSDDServiceName;
    }

    public void setProcessManagementPortWSDDServiceName(java.lang.String name) {
        ProcessManagementPortWSDDServiceName = name;
    }

    public org.apache.www.ode.pmapi.ProcessManagementPortType getProcessManagementPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ProcessManagementPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProcessManagementPort(endpoint);
    }

    public org.apache.www.ode.pmapi.ProcessManagementPortType getProcessManagementPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.www.ode.pmapi.ProcessManagementBindingStub _stub = new org.apache.www.ode.pmapi.ProcessManagementBindingStub(portAddress, this);
            _stub.setPortName(getProcessManagementPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProcessManagementPortEndpointAddress(java.lang.String address) {
        ProcessManagementPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.apache.www.ode.pmapi.ProcessManagementPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.www.ode.pmapi.ProcessManagementBindingStub _stub = new org.apache.www.ode.pmapi.ProcessManagementBindingStub(new java.net.URL(ProcessManagementPort_address), this);
                _stub.setPortName(getProcessManagementPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ProcessManagementPort".equals(inputPortName)) {
            return getProcessManagementPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi", "ProcessManagementService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi", "ProcessManagementPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ProcessManagementPort".equals(portName)) {
            setProcessManagementPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
