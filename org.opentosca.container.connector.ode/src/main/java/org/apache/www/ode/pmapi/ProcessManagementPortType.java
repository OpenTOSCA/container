/**
 * ProcessManagementPortType.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi;

public interface ProcessManagementPortType extends java.rmi.Remote {
  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo[] listProcesses(java.lang.String filter, java.lang.String orderKeys) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo[] listAllProcesses() throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo[] listProcessesCustom(java.lang.String filter, java.lang.String orderKeys, java.lang.String customizer) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo getProcessInfo(javax.xml.namespace.QName pid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo getProcessInfoCustom(javax.xml.namespace.QName pid, java.lang.String customizer) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo setProcessProperty(javax.xml.namespace.QName pid, javax.xml.namespace.QName propertyName, java.lang.String propertyValue) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo setProcessPropertyNode(javax.xml.namespace.QName pid, javax.xml.namespace.QName propertyName, java.lang.Object propertyValue) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo getExtensibilityElements(javax.xml.namespace.QName pid, org.apache.www.ode.pmapi.AidsType aids) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo activate(javax.xml.namespace.QName pid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo setRetired(javax.xml.namespace.QName pid, boolean retired) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;
}
