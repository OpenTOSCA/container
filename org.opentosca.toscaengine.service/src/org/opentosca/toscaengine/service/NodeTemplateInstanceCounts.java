package org.opentosca.toscaengine.service;

import java.util.HashMap;

import javax.xml.namespace.QName;

/**
 *  This class holds data for a NodeTemplate and it's min and maxOccurence
 *  This information is stored in a Hashmap. There the QName of the NodeTemplate is mapped to the Occurences of this NodeTemplate specified in the NodeTemplate
 * 
 *  minOccurence may not be <
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class NodeTemplateInstanceCounts {
	
	/**
	 * @author Marcus Eisele - marcus.eisele@gmail.com
	 * This class encapsulates the minInstances and maxInstances value
	 *
	 */
	public class InstanceCount {
		public int min;
		public int max;
		
		public final int UNBOUNDED = -1;
		
		public InstanceCount(int minInstances, String maxInstances) {
			this.min = minInstances;
			//need to construct the integer from this string
			int maxValue;
			if ("unbounded".equalsIgnoreCase(maxInstances)) {
				maxValue = -1;
			} else {
				maxValue = Integer.parseInt(maxInstances);
			}
			this.max = maxValue;
		}
		
	}
	
	private HashMap<QName, InstanceCount> nodeTemplateToOccurencesMap = new HashMap<QName, NodeTemplateInstanceCounts.InstanceCount>();
	
	public HashMap<QName, InstanceCount> getOccurenceInformationMap(){
		return nodeTemplateToOccurencesMap;
	}
	
	/**
	 * Add an additional entry for the given QName to the Hashmap (all previous entries for QNAME will be <b>overwritten</b>
	 * @param qnameOfNodeTemplate
	 * @param minInstances - number of minimalInstances of this specific NodeTemplate
	 * @param maxInstances - number of maximalInstances of this specific NodeTemplate. <b>unbounded is modelled as -1</b>
	 */
	public void addInstanceCount(QName qnameOfNodeTemplate, int minInstances, String maxInstances) {
		
		if (nodeTemplateToOccurencesMap.containsKey(qnameOfNodeTemplate)) {
			//TODO: logger which logs that the information was overwritten
		}
		
		//new instanceCount object
		InstanceCount iCount = new InstanceCount(minInstances, maxInstances);
		
		nodeTemplateToOccurencesMap.put(qnameOfNodeTemplate, iCount);
	}
	
}
