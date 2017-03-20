package org.opentosca.containerapi.resources.utilities;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TServiceTemplate;

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
