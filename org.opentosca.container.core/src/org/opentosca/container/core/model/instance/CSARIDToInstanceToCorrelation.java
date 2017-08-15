package org.opentosca.container.core.model.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Maps CSARIDs to CSARInstanceIDs to PublicPlan CorrelationIDs.
 */
@Deprecated
public class CSARIDToInstanceToCorrelation {

  // map of CSARID to CSARInstanceID to list of CorrelationIDs
  // TODO make persistent
  private final Map<CSARID, Map<ServiceTemplateInstanceID, List<String>>> storageMap =
      new HashMap<CSARID, Map<ServiceTemplateInstanceID, List<String>>>();


  /**
   * Stores a new instance of a CSAR.
   *
   * @param csarID the CSARID
   * @param instanceID the InstanceID
   */
  public ServiceTemplateInstanceID storeNewCSARInstance(final CSARID csarID,
      final QName serviceTemplateId) {

    int highest = 0;

    for (final ServiceTemplateInstanceID id : this.getInstanceMap(csarID).keySet()) {
      if (highest < id.getInstanceID()) {
        highest = id.getInstanceID();
      }
    }

    final ServiceTemplateInstanceID instance =
        new ServiceTemplateInstanceID(csarID, serviceTemplateId, highest + 1);

    this.getInstanceMap(csarID).put(instance, new ArrayList<String>());

    return instance;
  }

  /**
   * Stores a new PublicPlan correlation for an instance of a CSAR.
   *
   * @param csarID the CSARID
   * @param instanceID the InstanceID
   * @param correlationID the CorrelationID
   */
  public void storeNewCorrelationForInstance(final CSARID csarID,
      final ServiceTemplateInstanceID instanceID, final String correlationID) {

    final List<String> list = this.getCorrelationList(csarID, instanceID);
    if (null != list) {
      list.add(correlationID);
    }
  }

  public List<ServiceTemplateInstanceID> getInstancesOfCSAR(final CSARID csarID) {

    final List<ServiceTemplateInstanceID> returnList = new ArrayList<>();

    for (final ServiceTemplateInstanceID id : this.getInstanceMap(csarID).keySet()) {
      returnList.add(id);
    }

    return returnList;

  }

  /**
   * initializes and returns the instance to correlation map
   *
   * @param csarID
   * @return the map
   */
  private Map<ServiceTemplateInstanceID, List<String>> getInstanceMap(final CSARID csarID) {
    if (!this.storageMap.containsKey(csarID)) {
      this.storageMap.put(csarID, new HashMap<ServiceTemplateInstanceID, List<String>>());
    }
    return this.storageMap.get(csarID);
  }

  /**
   * initializes and returns the instance to correlation map
   *
   * @param csarID
   * @return the map
   */
  public List<String> getCorrelationList(final CSARID csarID,
      final ServiceTemplateInstanceID instanceID) {
    if (null == this.getInstanceMap(csarID)) {
      this.storageMap.put(csarID, new HashMap<ServiceTemplateInstanceID, List<String>>());
      this.storageMap.get(csarID).put(instanceID, new ArrayList<String>());
    }
    return this.storageMap.get(csarID).get(instanceID);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();

    final String ls = System.getProperty("line.separator");

    builder.append("Currently stored informations for instances and correlations:" + ls);
    for (final CSARID csarID : this.storageMap.keySet()) {
      builder.append("CSAR \"" + csarID + "\":" + ls + "   ");
      for (final ServiceTemplateInstanceID instanceID : this.storageMap.get(csarID).keySet()) {
        builder.append("InstanceID \"" + instanceID + "\" with correlations: ");
        for (final String correlation : this.getCorrelationList(csarID, instanceID)) {
          builder.append(correlation + ", ");
        }
        builder.append(ls);
      }
      builder.append(ls + ls);
    }

    return builder.toString();
  }

  public List<CSARID> getCSARList() {

    final List<CSARID> returnList = new ArrayList<CSARID>();

    for (final CSARID csarID : this.storageMap.keySet()) {
      returnList.add(csarID);
    }

    return returnList;
  }

  public boolean deleteCSAR(final CSARID csarID) {
    return null != this.storageMap.remove(csarID);
  }

  public boolean deleteInstanceOfCSAR(final CSARID csarID,
      final ServiceTemplateInstanceID instanceID) {
    if (this.storageMap.containsKey(csarID)) {
      return null != this.storageMap.get(csarID).remove(instanceID);
    }
    return false;
  }

}
