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
 *
 * The instance of this interface is used by org.opentosca.containerapi which invokes each step in
 * the deployment process. For handling the states of processing of each CSAR, this component uses
 * the org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService to read and set
 * the current state of a certain CSAR and provides a HashSet with the possible process invocations
 * for a certain CSAR.
 */
public interface OpenToscaControlService {

    /**
     * This method invokes the processing of the TOSCA content of a certain CSAR.
     *
     * @param csar the CSAR to process
     * @return true for success, false for one or more errors.
     */
    public boolean invokeToscaProcessing(CsarId csar);
    
    /**
     * Registers the implementation artifacts for a given ServiceTemplate of a given CSAR
     *
     * @param csar the CSAR owning the ServiceTemplate to deploy
     * @param serviceTemplate the ServiceTemplate to deploy
     * @return true for success, false for one or more errors.
     */
    public boolean registerImplementationArtifacts(CsarId csar, ServiceTemplateId serviceTemplate);
    /**
     * Registers the plans for a given ServiceTemplate of a given CSAR
     *
     * @param csar the CSAR owning the ServiceTemplate to deploy
     * @param serviceTemplate the ServiceTemplate to deploy
     * @return true for success, false for one or more errors.
     */
    public boolean generatePlans(CsarId csar, ServiceTemplateId serviceTemplate);
    
//    public Set<CsarId> storedCsars();
    public List<String> deleteCsar(CsarId csar);
    
//    public List<ServiceTemplateId> serviceTemplatesOf(CsarId csar);
    public Set<DeploymentProcessOperation> executableDeploymentProcessOperations(CsarId csar);
    
    public boolean declareStored(CsarId csar);
    public DeploymentProcessState currentDeploymentProcessState(CsarId csar);
    
    /**
     * @throws UnsupportedEncodingException 
     * @deprecated {@link #invokePlanInvocation(CsarId, TServiceTemplate, int, TPlanDTO)}
     */
    @Deprecated
    public String invokePlanInvocation(CsarId csar, QName serviceTemplateID, int instanceId, TPlanDTO plan) throws UnsupportedEncodingException;
    // FIXME evaluate using winery's TPlan instead
    public String invokePlanInvocation(CsarId csar, TServiceTemplate serviceTemplate, long instanceId, TPlanDTO plan) throws UnsupportedEncodingException;
    
    public List<String> correlationsForServiceTemplateInstance(CsarId csar, TServiceTemplate serviceTemplate, long instanceId);
    // FIXME evaluate using winery's TPlan instead
    public TPlanDTO getActivePlanOfInstance(CsarId csar, ServiceTemplateId serviceTemplate, long instanceId, String correlationId);
    long instanceIdOfCorrelation(String correlationId);

    /**
     * @deprecated {@link #invokeIADeployment(CsarId, TServiceTemplate)}
     */
    @Deprecated
    public boolean invokeIADeployment(CsarId csarId, QName serviceTemplate);
    boolean invokeIADeployment(CsarId csarId, TServiceTemplate serviceTemplate);
    /**
     * @deprecated {@link #invokePlanDeployment(CsarId, TServiceTemplate)} 
     */
    @Deprecated
    public boolean invokePlanDeployment(CsarId csarId, QName serviceTemplateId);
    boolean invokePlanDeployment(CsarId csar, TServiceTemplate serviceTemplate);

    @Deprecated
    public List<QName> getAllContainedServiceTemplates(CsarId csarid);
    
}
