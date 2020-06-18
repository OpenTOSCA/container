package org.opentosca.container.control;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.container.core.tosca.extension.TPlanDTO;

/**
 * Interface of the control of the OpenTosca Container.
 * <p>
 * The instance of this interface is used by org.opentosca.containerapi which invokes each step in the deployment
 * process. For handling the states of processing of each CSAR, this component uses the
 * org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService to read and set the current state of a
 * certain CSAR and provides a HashSet with the possible process invocations for a certain CSAR.
 */
public interface OpenToscaControlService {

    /**
     * This method invokes the processing of the TOSCA content of a certain CSAR.
     *
     * @param csar the CSAR to process
     * @return true for success, false for one or more errors.
     */
    boolean invokeToscaProcessing(CsarId csar);

    /**
     * Registers the plans for a given ServiceTemplate of a given CSAR
     *
     * @param csar            the CSAR owning the ServiceTemplate to deploy
     * @param serviceTemplate the ServiceTemplate to deploy
     * @return true for success, false for one or more errors.
     */
    boolean generatePlans(CsarId csar, ServiceTemplateId serviceTemplate);

    List<String> deleteCsar(CsarId csar);

    Set<DeploymentProcessOperation> executableDeploymentProcessOperations(CsarId csar);

    boolean declareStored(CsarId csar);

    DeploymentProcessState currentDeploymentProcessState(CsarId csar);

    /**
     * @deprecated {@link #invokePlanInvocation(CsarId, TServiceTemplate, int, TPlanDTO)}
     */
    @Deprecated
    String invokePlanInvocation(CsarId csar, QName serviceTemplateID, int instanceId, TPlanDTO plan) throws UnsupportedEncodingException;

    // FIXME evaluate using winery's TPlan instead
    String invokePlanInvocation(CsarId csar, TServiceTemplate serviceTemplate, long instanceId, TPlanDTO plan);

    /**
     * @deprecated {@link #invokePlanDeployment(CsarId, TServiceTemplate)}
     */
    @Deprecated
    boolean invokePlanDeployment(CsarId csarId, QName serviceTemplateId);

    boolean invokePlanDeployment(CsarId csar, TServiceTemplate serviceTemplate);

    @Deprecated
    List<QName> getAllContainedServiceTemplates(CsarId csarid);
}
