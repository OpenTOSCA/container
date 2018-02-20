package org.opentosca.container.api.util;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.id.CSARID;

public abstract class ModelUtil {
	
	public static boolean hasOpenRequirements(CSARID csarId, IToscaEngineService service) throws UserException, SystemException {
		QName serviceTemplateId = service.getServiceTemplatesInCSAR(csarId).get(0);
		
		List<String> nodeTemplateIds = service.getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId);
		List<String> relationshipTemplateIds = service.getRelationshipTemplatesOfServiceTemplate(csarId, serviceTemplateId);
		
		for (String nodeTemplateId : nodeTemplateIds) {
			List<QName> nodeReqs = service.getNodeTemplateRequirements(csarId, serviceTemplateId, nodeTemplateId);
			int foundRelations = 0;
			
			for (String relationshipTemplateId : relationshipTemplateIds) {
				QName relationReq = service.getRelationshipTemplateSource(csarId, serviceTemplateId, relationshipTemplateId);
				if (relationReq.getLocalPart().equals(nodeTemplateId)) {
					foundRelations++;
				}
			}
			
			if (foundRelations < nodeReqs.size()) {
				return true;
			}
			
		}
		return false;
	}
	
	
	
}
