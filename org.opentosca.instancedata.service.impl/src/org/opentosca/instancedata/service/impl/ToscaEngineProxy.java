package org.opentosca.instancedata.service.impl;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.toscaengine.service.IToscaEngineService;

/**
 * 
 * This class is a proxy to the toscaEngineService In a later refactoring (after some discussions) some methods of this
 * Proxy maybe directly ported to the ToscaEngine / ReferenceMapper
 * 
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */

//TODO: remove when no real logic is there anymore
public class ToscaEngineProxy {
	
	/**
	 * This method uses the toscaReferenceMapper of the given toscaEngineService to determine if the given csarID
	 * contains the serviceTemplate specified by serviceTemplateID
	 * @param toscaEngineService
	 * @param csarID
	 * @param serviceTemplateID
	 * 
	 * @return true, if the given ServiceTemplate exists in the CSAR specified by the input parameter
	 */
	public static boolean doesServiceTemplateExist(IToscaEngineService toscaEngineService, CSARID csarID,
			QName serviceTemplateID) {
		List<QName> serviceTemplateIDsContainedInCSAR = toscaEngineService.getToscaReferenceMapper()
				.getServiceTemplateIDsContainedInCSAR(csarID);
		
		if (serviceTemplateIDsContainedInCSAR == null) {
			return false;
		}
		
		return serviceTemplateIDsContainedInCSAR.contains(serviceTemplateID);
		
	}

}
