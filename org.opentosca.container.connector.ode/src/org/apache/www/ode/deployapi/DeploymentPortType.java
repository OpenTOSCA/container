/**
 * DeploymentPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.deployapi;

public interface DeploymentPortType extends java.rmi.Remote {
    public org.apache.www.ode.deployapi.DeployUnit deploy(java.lang.String name, org.apache.www.ode.deployapi._package _package) throws java.rmi.RemoteException;
    public boolean undeploy(javax.xml.namespace.QName packageName) throws java.rmi.RemoteException;
    public java.lang.String[] listDeployedPackages() throws java.rmi.RemoteException;
    public javax.xml.namespace.QName[] listProcesses(java.lang.String packageName) throws java.rmi.RemoteException;
    public java.lang.String getProcessPackage(javax.xml.namespace.QName processName) throws java.rmi.RemoteException;
}
