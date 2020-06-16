/**
 * DeploymentService.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.deployapi;

public interface DeploymentService extends javax.xml.rpc.Service {
  public java.lang.String getDeploymentPortAddress();

  public org.apache.www.ode.deployapi.DeploymentPortType getDeploymentPort() throws javax.xml.rpc.ServiceException;

  public org.apache.www.ode.deployapi.DeploymentPortType getDeploymentPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
