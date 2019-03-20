/**
 * DeploymentServiceLocator.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.deployapi;

public class DeploymentServiceLocator extends org.apache.axis.client.Service implements org.apache.www.ode.deployapi.DeploymentService {

  public DeploymentServiceLocator() {
  }


  public DeploymentServiceLocator(org.apache.axis.EngineConfiguration config) {
    super(config);
  }

  public DeploymentServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
    super(wsdlLoc, sName);
  }

  // Use to get a proxy class for DeploymentPort
  private java.lang.String DeploymentPort_address = "http://localhost:8080/ode/processes/DeploymentService";

  public java.lang.String getDeploymentPortAddress() {
    return DeploymentPort_address;
  }

  // The WSDD service name defaults to the port name.
  private java.lang.String DeploymentPortWSDDServiceName = "DeploymentPort";

  public java.lang.String getDeploymentPortWSDDServiceName() {
    return DeploymentPortWSDDServiceName;
  }

  public void setDeploymentPortWSDDServiceName(java.lang.String name) {
    DeploymentPortWSDDServiceName = name;
  }

  public org.apache.www.ode.deployapi.DeploymentPortType getDeploymentPort() throws javax.xml.rpc.ServiceException {
    java.net.URL endpoint;
    try {
      endpoint = new java.net.URL(DeploymentPort_address);
    } catch (java.net.MalformedURLException e) {
      throw new javax.xml.rpc.ServiceException(e);
    }
    return getDeploymentPort(endpoint);
  }

  public org.apache.www.ode.deployapi.DeploymentPortType getDeploymentPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
    try {
      org.apache.www.ode.deployapi.DeploymentBindingStub _stub = new org.apache.www.ode.deployapi.DeploymentBindingStub(portAddress, this);
      _stub.setPortName(getDeploymentPortWSDDServiceName());
      return _stub;
    } catch (org.apache.axis.AxisFault e) {
      return null;
    }
  }

  public void setDeploymentPortEndpointAddress(java.lang.String address) {
    DeploymentPort_address = address;
  }

  /**
   * For the given interface, get the stub implementation.
   * If this service has no port for the given interface,
   * then ServiceException is thrown.
   */
  public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
    try {
      if (org.apache.www.ode.deployapi.DeploymentPortType.class.isAssignableFrom(serviceEndpointInterface)) {
        org.apache.www.ode.deployapi.DeploymentBindingStub _stub = new org.apache.www.ode.deployapi.DeploymentBindingStub(new java.net.URL(DeploymentPort_address), this);
        _stub.setPortName(getDeploymentPortWSDDServiceName());
        return _stub;
      }
    } catch (java.lang.Throwable t) {
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
    if ("DeploymentPort".equals(inputPortName)) {
      return getDeploymentPort();
    } else {
      java.rmi.Remote _stub = getPort(serviceEndpointInterface);
      ((org.apache.axis.client.Stub) _stub).setPortName(portName);
      return _stub;
    }
  }

  public javax.xml.namespace.QName getServiceName() {
    return new javax.xml.namespace.QName("http://www.apache.org/ode/deployapi", "DeploymentService");
  }

  private java.util.HashSet ports = null;

  public java.util.Iterator getPorts() {
    if (ports == null) {
      ports = new java.util.HashSet();
      ports.add(new javax.xml.namespace.QName("http://www.apache.org/ode/deployapi", "DeploymentPort"));
    }
    return ports.iterator();
  }

  /**
   * Set the endpoint address for the specified port name.
   */
  public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

    if ("DeploymentPort".equals(portName)) {
      setDeploymentPortEndpointAddress(address);
    } else { // Unknown Port Name
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
