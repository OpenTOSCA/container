package org.opentosca.container.control;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
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
@Deprecated
public interface IOpenToscaControlService {

    /**
     * This method invokes the processing of the TOSCA content of a certain CSAR.
     *
     * @param csarID ID which uniquely identifies a CSAR file.
     * @return Returns true for success, false for one or more errors.
     */
    public Boolean invokeTOSCAProcessing(CSARID csarID);

    /**
     * Invoke the deployment of ImplementationArtifacts.
     *
     * @param csarID ID which uniquely identifies a CSAR file.
     * @param serviceTemplateID ID of the ServiceTemplate to deploy.
     * @return Returns true for success, false for one or more errors.
     */
    public Boolean invokeIADeployment(CSARID csarID, QName serviceTemplateID);

    /**
     * Invoke the deployment of the Plans.
     *
     * @param csarID ID which uniquely identifies a CSAR file.
     * @param serviceTemplateID ID of the ServiceTemplate to deploy.
     * @return Returns true for success, false for one or more errors.
     */
    public Boolean invokePlanDeployment(CSARID csarID, QName serviceTemplateID);

    /**
     * Returns all the stored CSARs inside the OpenTosca Container.
     *
     * @return List of QNames which each represents a stored CSAR.
     */
    public Set<CSARID> getAllStoredCSARs();

    /**
     * This method deletes the stored contents of a certain CSAR inside of the container.
     *
     * @param csarID the ID of the CSAR which shall be deleted.
     * @return List of errors, if list is empty, no error occured
     */
    public List<String> deleteCSAR(CSARID csarID);

    /**
     * This method returns a list of the QNames contained in a specific CSAR.
     *
     * @param csarID the ID of the specific CSAR.
     * @return A list of the QName of ServiceTemplates if there are some contained in the given CSAR. An
     *         empty list of none are contained. Null if there is an error.
     */
    public List<QName> getAllContainedServiceTemplates(CSARID csarID);

    /**
     * Returns a Set of executable operations on a currently running deployment process of a CSAR.
     *
     * @param csarID ID which uniquely identifies a CSAR file.
     * @return Set of executable operations.
     */
    public Set<DeploymentProcessOperation> getExecutableDeploymentProcessOperations(CSARID csarID);

    /**
     * Sets the deployment state of a CSAR to STORED.
     *
     * @param csarID ID which uniquely identifies a CSAR file.
     * @return Returns true, if setting was successful, otherwise false.
     */
    public Boolean setDeploymentProcessStateStored(CSARID csarID);

    /**
     * Returns the current state of a deployment process of a CSAR.
     *
     * @param csarID ID which uniquely identifies a CSAR file.
     * @return Returns true for success, false for one or more errors.
     */
    public DeploymentProcessState getDeploymentProcessState(CSARID csarID);

    /**
     * Invokes the a process described due the parameter PublicPlan for the given CSAR.
     *
     * @param csarID the ID of the CSAR
     *
     * @param serviceTemplateInstanceID the instance id, or -1 if the plan is a build plan
     * @param plan which containes the data which with the process is invoked (including the message
     *        values).
     * @return
     * @throws UnsupportedEncodingException
     */
    public String invokePlanInvocation(CSARID csarID, QName serviceTemplateId, long serviceTemplateInstanceID,
                                       TPlanDTO plan) throws UnsupportedEncodingException;

    /**
     * Returns a list of Strings for active PublicPlans of a CSARInstance.
     *
     * @param csarInstanceID
     * @return list of active PublicPlans
     */
    public List<String> getCorrelationsOfServiceTemplateInstance(ServiceTemplateInstanceID csarInstanceID);

    /**
     * Returns a specific active PublicPlan of a CSARInstance
     *
     * @param csarInstanceID
     * @param correlationID
     * @return the Plan
     */
    public TPlanDTO getActivePlanOfInstance(ServiceTemplateInstanceID csarInstanceID, String correlationID);

    int getCSARInstanceIDForCorrelationID(String correlationID);

    void markAsProcessed(CsarId csarID);

}
