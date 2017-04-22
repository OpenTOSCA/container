package org.opentosca.containerapi.instancedata;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.RelationInstance;
import org.opentosca.model.instancedata.ServiceInstance;

/**
 * This class checks for Existence of ServiceInstances and NodeInstances by calling the given InstanceDataService
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class ExistenceChecker {
	
	public static boolean existsServiceInstance(URI serviceInstanceID, IInstanceDataService service) {
		List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceID, null, null);
		//check if only one instance was returned - we dont verify because we assume the get Method returns the correct one!
		//we got really bad problems if this wouldnt work anyway
		if (serviceInstances != null && serviceInstances.size() == 1 && serviceInstances.get(0) != null) {
			return true;
		}
		
		//if the instance wasnt returned => it was not found = it doesnt exist
		return false;
	}
	
	public static boolean existsNodeInstance(URI nodeInstanceID, IInstanceDataService service) {
		List<NodeInstance> nodeInstances = service.getNodeInstances(nodeInstanceID, null, null, null);
		//check if only one instance was returned - we dont verify because we assume the get Method returns the correct one!
		//we got really bad problems if this wouldnt work anyway
		if (nodeInstances != null && nodeInstances.size() == 1 && nodeInstances.get(0) != null) {
			return true;
		}
		
		//if the instance wasnt returned => it was not found = it doesnt exist
		return false;
	}
	
	/**
	 * @param nodeInstanceID
	 * 		the id given (int) by the rest-service
	 * @param service
	 * 		initialized InstanceDataService
	 * @return the specified nodeInstance
	 * 
	 * @throws GenericRestException
	 * 		when specified nodeInstance doesn't exist
	 */
	public static NodeInstance checkNodeInstanceWithException(int nodeInstanceID, IInstanceDataService service) throws GenericRestException {
		List<NodeInstance> nodeInstances = service.getNodeInstances(IdConverter.nodeInstanceIDtoURI(nodeInstanceID), null, null, null);
		//check if only one instance was returned - we dont verify because we assume the get Method returns the correct one!
		//we got really bad problems if this wouldnt work anyway
		if (nodeInstances != null && nodeInstances.size() == 1 && nodeInstances.get(0) != null) {
			return nodeInstances.get(0);
		} else {
			throw new GenericRestException(Status.NOT_FOUND, "Specified nodeInstance with id: " + nodeInstanceID + " doesn't exist");
		}
		
	}
	
	public static RelationInstance checkRelationInstanceWithException(int relationInstanceID, IInstanceDataService service) throws GenericRestException {
		List<RelationInstance> relationInstances = service.getRelationInstances(IdConverter.relationInstanceIDtoURI(relationInstanceID), null, null, null);
		//check if only one instance was returned - we dont verify because we assume the get Method returns the correct one!
		//we got really bad problems if this wouldnt work anyway
		if (relationInstances != null && relationInstances.size() == 1 && relationInstances.get(0) != null) {
			return relationInstances.get(0);
		} else {
			throw new GenericRestException(Status.NOT_FOUND, "Specified relationInstance with id: " + relationInstanceID + " doesn't exist");
		}
		
	}
	
	public static ServiceInstance checkServiceInstanceWithException(int serviceInstanceID, IInstanceDataService service) throws GenericRestException {
		List<ServiceInstance> serviceInstances = service.getServiceInstances(IdConverter.serviceInstanceIDtoURI(serviceInstanceID), null, null);
		//check if only one instance was returned - we dont verify because we assume the get Method returns the correct one!
		//we got really bad problems if this wouldnt work anyway
		if (serviceInstances != null && serviceInstances.size() == 1 && serviceInstances.get(0) != null) {
			return serviceInstances.get(0);
		} else {
			throw new GenericRestException(Status.NOT_FOUND, "Specified serviceInstance with id: " + serviceInstanceID + " doesn't exist");
		}
	}
}
