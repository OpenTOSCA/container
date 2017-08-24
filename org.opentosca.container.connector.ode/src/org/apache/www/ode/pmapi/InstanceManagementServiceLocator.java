/**
 * InstanceManagementServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi;

public class InstanceManagementServiceLocator extends org.apache.axis.client.Service implements org.apache.www.ode.pmapi.InstanceManagementService {

    public InstanceManagementServiceLocator() {
    }


    public InstanceManagementServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public InstanceManagementServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for InstanceManagementPort
    private java.lang.String InstanceManagementPort_address = "http://localhost:8080/ode/processes/InstanceManagement";

    public java.lang.String getInstanceManagementPortAddress() {
        return InstanceManagementPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String InstanceManagementPortWSDDServiceName = "InstanceManagementPort";

    public java.lang.String getInstanceManagementPortWSDDServiceName() {
        return InstanceManagementPortWSDDServiceName;
    }

    public void setInstanceManagementPortWSDDServiceName(java.lang.String name) {
        InstanceManagementPortWSDDServiceName = name;
    }

    public org.apache.www.ode.pmapi.InstanceManagementPortType getInstanceManagementPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(InstanceManagementPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getInstanceManagementPort(endpoint);
    }

    public org.apache.www.ode.pmapi.InstanceManagementPortType getInstanceManagementPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.www.ode.pmapi.InstanceManagementBindingStub _stub = new org.apache.www.ode.pmapi.InstanceManagementBindingStub(portAddress, this);
            _stub.setPortName(getInstanceManagementPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setInstanceManagementPortEndpointAddress(java.lang.String address) {
        InstanceManagementPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.apache.www.ode.pmapi.InstanceManagementPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.www.ode.pmapi.InstanceManagementBindingStub _stub = new org.apache.www.ode.pmapi.InstanceManagementBindingStub(new java.net.URL(InstanceManagementPort_address), this);
                _stub.setPortName(getInstanceManagementPortWSDDServiceName());
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
        if ("InstanceManagementPort".equals(inputPortName)) {
            return getInstanceManagementPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi", "InstanceManagementService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi", "InstanceManagementPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("InstanceManagementPort".equals(portName)) {
            setInstanceManagementPortEndpointAddress(address);
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
