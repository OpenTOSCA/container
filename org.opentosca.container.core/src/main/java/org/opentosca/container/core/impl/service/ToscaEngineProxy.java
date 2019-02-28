package org.opentosca.container.core.impl.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.springframework.stereotype.Service;

/**
 * This class is a proxy to the toscaEngineService In a later refactoring (after some discussions)
 * some methods of this Proxy maybe directly ported to the ToscaEngine / ReferenceMapper
 */
@Service
// TODO: remove when no real logic is there anymore
public class ToscaEngineProxy {

    /**
     * This method uses the toscaReferenceMapper of the given toscaEngineService to determine if the
     * given csarID contains the serviceTemplate specified by serviceTemplateID
     *
     * @param toscaEngineService
     * @param csarID
     * @param serviceTemplateID
     *
     * @return true, if the given ServiceTemplate exists in the CSAR specified by the input parameter
     */
    public static boolean doesServiceTemplateExist(final IToscaEngineService toscaEngineService, final CSARID csarID,
                                                   final QName serviceTemplateID) {
        final List<QName> serviceTemplateIDsContainedInCSAR =
            toscaEngineService.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarID);

        if (serviceTemplateIDsContainedInCSAR == null) {
            return false;
        }

        for (final QName serviceTemplateId : serviceTemplateIDsContainedInCSAR) {
            if (serviceTemplateID.equals(serviceTemplateId)) {
                return true;
            }
        }

        return serviceTemplateIDsContainedInCSAR.contains(serviceTemplateID);

    }

}
