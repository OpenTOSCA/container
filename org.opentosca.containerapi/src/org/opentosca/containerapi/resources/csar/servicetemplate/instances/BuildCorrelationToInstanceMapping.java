package org.opentosca.containerapi.resources.csar.servicetemplate.instances;

import java.util.HashMap;
import java.util.Map;

public class BuildCorrelationToInstanceMapping {
	
	public static BuildCorrelationToInstanceMapping instance = new BuildCorrelationToInstanceMapping();
	
	private Map<String, Integer> correlationIdToServiceTemplateInstanceId = new HashMap<String, Integer>();
	
	
	private BuildCorrelationToInstanceMapping(){
	}
	
	public void correlateCorrelationIdToServiceTemplateInstanceId(String corrId, int serviceTemplateInstanceId){
		correlationIdToServiceTemplateInstanceId.put(corrId, serviceTemplateInstanceId);
	}
	
	public int getServiceTemplateInstanceIdForBuildPlanCorrelation(String corrId){
		return correlationIdToServiceTemplateInstanceId.get(corrId);
	}
	
	public boolean knowsCorrelationId(String buildPlanCorrId) {
		return correlationIdToServiceTemplateInstanceId.containsKey(buildPlanCorrId);
	}
	
}
