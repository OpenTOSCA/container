package org.opentosca.container.api.legacy.resources.utilities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.model.Definitions;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TServiceTemplate;

public class ModelUtils {
	
	public static boolean hasOpenRequirements(CSARID csarId) throws UserException, SystemException {
		QName serviceTemplateId = ModelUtils.getEntryServiceTemplate(csarId);
		
		List<String> nodeTemplateIds = ToscaServiceHandler.getToscaEngineService().getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId);
		List<String> relationshipTemplateIds = ToscaServiceHandler.getToscaEngineService().getRelationshipTemplatesOfServiceTemplate(csarId, serviceTemplateId);
		
		for (String nodeTemplateId : nodeTemplateIds) {
			List<QName> nodeReqs = ToscaServiceHandler.getToscaEngineService().getNodeTemplateRequirements(csarId, serviceTemplateId, nodeTemplateId);
			int foundRelations = 0;
			
			for (String relationshipTemplateId : relationshipTemplateIds) {
				QName relationReq = ToscaServiceHandler.getToscaEngineService().getRelationshipTemplateSource(csarId, serviceTemplateId, relationshipTemplateId);
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
	
	public static boolean hasBuildPlan(CSARID csarId) throws UserException, SystemException {
		QName serviceTemplateId = ModelUtils.getEntryServiceTemplate(csarId);
		
		Map<PlanTypes, LinkedHashMap<QName, TPlan>> plans = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarId);
		
		if (plans == null)
			return false;
		
		return plans.containsKey(PlanTypes.BUILD);
	}
	
	public static boolean hasTerminationPlan(CSARID csarId) throws UserException, SystemException {
		QName serviceTemplateId = ModelUtils.getEntryServiceTemplate(csarId);
		
		Map<PlanTypes, LinkedHashMap<QName, TPlan>> plans = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarId);
		
		return plans.containsKey(PlanTypes.TERMINATION);
	}
	
	public static QName getEntryServiceTemplate(CSARID csarId) throws UserException, SystemException {
		ICoreFileService fileService = FileRepositoryServiceHandler.getFileHandler();
		CSARContent content = fileService.getCSAR(csarId);
		Definitions def = ToscaServiceHandler.getIXMLSerializer().unmarshal(content.getRootTOSCA().getFileAsInputStream());
		for (TExtensibleElements el : def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			if (el instanceof TServiceTemplate) {
				TServiceTemplate st = (TServiceTemplate) el;
				return new QName(st.getTargetNamespace(), st.getId());
				
			}
		}
		return null;
	}
	
}
