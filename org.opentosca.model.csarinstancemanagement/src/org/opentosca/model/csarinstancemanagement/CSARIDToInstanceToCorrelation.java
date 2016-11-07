package org.opentosca.model.csarinstancemanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.core.model.csar.id.CSARID;

/**
 * Maps CSARIDs to CSARInstanceIDs to PublicPlan CorrelationIDs.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARIDToInstanceToCorrelation {
	
	// map of CSARID to CSARInstanceID to list of CorrelationIDs
	// TODO make persistent
	private final Map<CSARID, Map<CSARInstanceID, List<String>>> storageMap = new HashMap<CSARID, Map<CSARInstanceID, List<String>>>();
	
	
	/**
	 * Stores a new instance of a CSAR.
	 * 
	 * @param csarID the CSARID
	 * @param instanceID the InstanceID
	 */
	public CSARInstanceID storeNewCSARInstance(CSARID csarID) {
		
		int highest = 0;
		
		for (CSARInstanceID id : getInstanceMap(csarID).keySet()) {
			if (highest < id.getInstanceID()) {
				highest = id.getInstanceID();
			}
		}
		
		CSARInstanceID instance = new CSARInstanceID(csarID, highest + 1);
		
		getInstanceMap(csarID).put(instance, new ArrayList<String>());
		
		return instance;
	}
	
	/**
	 * Stores a new PublicPlan correlation for an instance of a CSAR.
	 * 
	 * @param csarID the CSARID
	 * @param instanceID the InstanceID
	 * @param correlationID the CorrelationID
	 */
	public void storeNewCorrelationForInstance(CSARID csarID, CSARInstanceID instanceID, String correlationID) {
		
		List<String> list = getCorrelationList(csarID, instanceID);
		if (null != list) {
			list.add(correlationID);
		}
	}
	
	public List<CSARInstanceID> getInstancesOfCSAR(CSARID csarID) {
		
		List<CSARInstanceID> returnList = new ArrayList<CSARInstanceID>();
		
		for (CSARInstanceID id : getInstanceMap(csarID).keySet()) {
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
	private Map<CSARInstanceID, List<String>> getInstanceMap(CSARID csarID) {
		if (!storageMap.containsKey(csarID)) {
			storageMap.put(csarID, new HashMap<CSARInstanceID, List<String>>());
		}
		return storageMap.get(csarID);
	}
	
	/**
	 * initializes and returns the instance to correlation map
	 * 
	 * @param csarID
	 * @return the map
	 */
	public List<String> getCorrelationList(CSARID csarID, CSARInstanceID instanceID) {
		if (null == getInstanceMap(csarID)) {
			storageMap.put(csarID, new HashMap<CSARInstanceID, List<String>>());
			storageMap.get(csarID).put(instanceID, new ArrayList<String>());
		}
		return storageMap.get(csarID).get(instanceID);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String ls = System.getProperty("line.separator");
		
		builder.append("Currently stored informations for instances and correlations:" + ls);
		for (CSARID csarID : storageMap.keySet()) {
			builder.append("CSAR \"" + csarID + "\":" + ls + "   ");
			for (CSARInstanceID instanceID : storageMap.get(csarID).keySet()) {
				builder.append("InstanceID \"" + instanceID + "\" with correlations: ");
				for (String correlation : getCorrelationList(csarID, instanceID)) {
					builder.append(correlation + ", ");
				}
				builder.append(ls);
			}
			builder.append(ls + ls);
		}
		
		return builder.toString();
	}
	
	public List<CSARID> getCSARList() {
		
		List<CSARID> returnList = new ArrayList<CSARID>();
		
		for (CSARID csarID : storageMap.keySet()) {
			returnList.add(csarID);
		}
		
		return returnList;
	}
	
	public boolean deleteCSAR(CSARID csarID) {
		return null != storageMap.remove(csarID);
	}
	
	public boolean deleteInstanceOfCSAR(CSARID csarID, CSARInstanceID instanceID) {
		if (storageMap.containsKey(csarID)) {
			return null != storageMap.get(csarID).remove(instanceID);
		}
		return false;
	}
	
}
