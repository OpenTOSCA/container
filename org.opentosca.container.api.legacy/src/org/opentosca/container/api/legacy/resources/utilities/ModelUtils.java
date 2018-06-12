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

    public static QName getEntryServiceTemplate(final CSARID csarId) throws UserException, SystemException {
        final ICoreFileService fileService = FileRepositoryServiceHandler.getFileHandler();
        final CSARContent content = fileService.getCSAR(csarId);
        final Definitions def =
            ToscaServiceHandler.getIXMLSerializer().unmarshal(content.getRootTOSCA().getFileAsInputStream());
        for (final TExtensibleElements el : def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (el instanceof TServiceTemplate) {
                final TServiceTemplate st = (TServiceTemplate) el;
                return new QName(st.getTargetNamespace(), st.getId());

            }
        }
        return null;
    }

    public static boolean hasBuildPlan(final CSARID csarId) throws UserException, SystemException {
        final Map<PlanTypes, LinkedHashMap<QName, TPlan>> plans =
            ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarId);

        if (plans == null) {
            return false;
        }

        return plans.containsKey(PlanTypes.BUILD) & !plans.get(PlanTypes.BUILD).isEmpty() ? true : false;
    }

    public static boolean hasOpenRequirements(final CSARID csarId) throws UserException, SystemException {
        final QName serviceTemplateId = ModelUtils.getEntryServiceTemplate(csarId);

        final List<String> nodeTemplateIds =
            ToscaServiceHandler.getToscaEngineService().getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId);
        final List<String> relationshipTemplateIds =
            ToscaServiceHandler.getToscaEngineService().getRelationshipTemplatesOfServiceTemplate(csarId,
                                                                                                  serviceTemplateId);

        for (final String nodeTemplateId : nodeTemplateIds) {
            final List<QName> nodeReqs =
                ToscaServiceHandler.getToscaEngineService().getNodeTemplateRequirements(csarId, serviceTemplateId,
                                                                                        nodeTemplateId);
            int foundRelations = 0;

            for (final String relationshipTemplateId : relationshipTemplateIds) {
                final QName relationReq =
                    ToscaServiceHandler.getToscaEngineService().getRelationshipTemplateSource(csarId, serviceTemplateId,
                                                                                              relationshipTemplateId);
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

    public static boolean hasTerminationPlan(final CSARID csarId) throws UserException, SystemException {
        final Map<PlanTypes, LinkedHashMap<QName, TPlan>> plans =
            ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarId);

        if (plans == null) {
            return false;
        }

        return plans.containsKey(PlanTypes.TERMINATION) & !plans.get(PlanTypes.TERMINATION).isEmpty() ? true : false;
    }

}
