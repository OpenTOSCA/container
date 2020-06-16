package org.apache.www.ode.deployapi;

public class DeploymentPortTypeProxy implements org.apache.www.ode.deployapi.DeploymentPortType {
    private String _endpoint = null;
    private org.apache.www.ode.deployapi.DeploymentPortType deploymentPortType = null;

    public DeploymentPortTypeProxy() {
        _initDeploymentPortTypeProxy();
    }

    public DeploymentPortTypeProxy(String endpoint) {
        _endpoint = endpoint;
        _initDeploymentPortTypeProxy();
    }

    private void _initDeploymentPortTypeProxy() {
        try {
            deploymentPortType = (new org.apache.www.ode.deployapi.DeploymentServiceLocator()).getDeploymentPort();
            if (deploymentPortType != null) {
                if (_endpoint != null)
                    ((javax.xml.rpc.Stub) deploymentPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
                else
                    _endpoint = (String) ((javax.xml.rpc.Stub) deploymentPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
            }
        } catch (javax.xml.rpc.ServiceException serviceException) {
        }
    }

    public String getEndpoint() {
        return _endpoint;
    }

    public void setEndpoint(String endpoint) {
        _endpoint = endpoint;
        if (deploymentPortType != null)
            ((javax.xml.rpc.Stub) deploymentPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    }

    public org.apache.www.ode.deployapi.DeploymentPortType getDeploymentPortType() {
        if (deploymentPortType == null)
            _initDeploymentPortTypeProxy();
        return deploymentPortType;
    }

    public org.apache.www.ode.deployapi.DeployUnit deploy(java.lang.String name, org.apache.www.ode.deployapi._package _package) throws java.rmi.RemoteException {
        if (deploymentPortType == null)
            _initDeploymentPortTypeProxy();
        return deploymentPortType.deploy(name, _package);
    }

    public boolean undeploy(javax.xml.namespace.QName packageName) throws java.rmi.RemoteException {
        if (deploymentPortType == null)
            _initDeploymentPortTypeProxy();
        return deploymentPortType.undeploy(packageName);
    }

    public java.lang.String[] listDeployedPackages() throws java.rmi.RemoteException {
        if (deploymentPortType == null)
            _initDeploymentPortTypeProxy();
        return deploymentPortType.listDeployedPackages();
    }

    public javax.xml.namespace.QName[] listProcesses(java.lang.String packageName) throws java.rmi.RemoteException {
        if (deploymentPortType == null)
            _initDeploymentPortTypeProxy();
        return deploymentPortType.listProcesses(packageName);
    }

    public java.lang.String getProcessPackage(javax.xml.namespace.QName processName) throws java.rmi.RemoteException {
        if (deploymentPortType == null)
            _initDeploymentPortTypeProxy();
        return deploymentPortType.getProcessPackage(processName);
    }
}
