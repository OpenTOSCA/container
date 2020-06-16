package org.apache.www.ode.pmapi;

public class ProcessManagementPortTypeProxy implements org.apache.www.ode.pmapi.ProcessManagementPortType {
  private String _endpoint = null;
  private org.apache.www.ode.pmapi.ProcessManagementPortType processManagementPortType = null;

  public ProcessManagementPortTypeProxy() {
    _initProcessManagementPortTypeProxy();
  }

  public ProcessManagementPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initProcessManagementPortTypeProxy();
  }

  private void _initProcessManagementPortTypeProxy() {
    try {
      processManagementPortType = (new org.apache.www.ode.pmapi.ProcessManagementServiceLocator()).getProcessManagementPort();
      if (processManagementPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub) processManagementPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String) ((javax.xml.rpc.Stub) processManagementPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }

    } catch (javax.xml.rpc.ServiceException serviceException) {
    }
  }

  public String getEndpoint() {
    return _endpoint;
  }

  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (processManagementPortType != null)
      ((javax.xml.rpc.Stub) processManagementPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

  }

  public org.apache.www.ode.pmapi.ProcessManagementPortType getProcessManagementPortType() {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType;
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo[] listProcesses(java.lang.String filter, java.lang.String orderKeys) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.listProcesses(filter, orderKeys);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo[] listAllProcesses() throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.listAllProcesses();
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo[] listProcessesCustom(java.lang.String filter, java.lang.String orderKeys, java.lang.String customizer) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.listProcessesCustom(filter, orderKeys, customizer);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo getProcessInfo(javax.xml.namespace.QName pid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.getProcessInfo(pid);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo getProcessInfoCustom(javax.xml.namespace.QName pid, java.lang.String customizer) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.getProcessInfoCustom(pid, customizer);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo setProcessProperty(javax.xml.namespace.QName pid, javax.xml.namespace.QName propertyName, java.lang.String propertyValue) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.setProcessProperty(pid, propertyName, propertyValue);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo setProcessPropertyNode(javax.xml.namespace.QName pid, javax.xml.namespace.QName propertyName, java.lang.Object propertyValue) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.setProcessPropertyNode(pid, propertyName, propertyValue);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo getExtensibilityElements(javax.xml.namespace.QName pid, org.apache.www.ode.pmapi.AidsType aids) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.getExtensibilityElements(pid, aids);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo activate(javax.xml.namespace.QName pid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.activate(pid);
  }

  public org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo setRetired(javax.xml.namespace.QName pid, boolean retired) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault {
    if (processManagementPortType == null)
      _initProcessManagementPortTypeProxy();
    return processManagementPortType.setRetired(pid, retired);
  }


}
