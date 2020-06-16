/**
 * InstanceManagementPortType.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi;

public interface InstanceManagementPortType extends java.rmi.Remote {
    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo[] listInstances(java.lang.String filter, java.lang.String order, int limit) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo[] listInstancesSummary(java.lang.String filter, java.lang.String order, int limit) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo[] queryInstances(java.lang.String payload) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo[] listAllInstances() throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo[] listAllInstancesWithLimit(int payload) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo getInstanceInfo(long iid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeInfo getScopeInfo(long siid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TScopeInfo getScopeInfoWithActivity(long sid, boolean activityInfo) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TVariableInfo getVariableInfo(java.lang.String sid, java.lang.String varName) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TVariableInfo setVariable(java.lang.String sid, java.lang.String varName, java.lang.Object value) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TEventInfo[] listEvents(java.lang.String instanceFilter, java.lang.String eventFilter, int maxCount) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.ListType getEventTimeline(java.lang.String instanceFilter, java.lang.String eventFilter) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo suspend(long iid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo resume(long iid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo terminate(long iid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo fault(long iid) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.ListType delete(java.lang.String filter) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.TInstanceInfo recoverActivity(long iid, long aid, java.lang.String action) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public long[] replay(org.apache.www.ode.pmapi.types._2006._08._02.Replay replay) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;

    public org.apache.www.ode.pmapi.types._2006._08._02.CommunicationType[] getCommunication(long[] getCommunication) throws java.rmi.RemoteException, org.apache.www.ode.pmapi.ManagementFault;
}
