package org.opentosca.container.engine.ia;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.csar.Csar;

/**
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * This interface defines a high-level method for deploying Implementation Artifacts through the
 * Implementation Artifact Engine (IAEngine).
 * <p>
 * The IAEngine will analyze all Implementation Artifacts specified under a given NodeTemplate and
 * its NodeTypes. During this process the Required Capabilities of a specific Implementation
 * Artifact are evaluated and if they are met by the Container and/or matching plug-ins the
 * Implementation Artifact is then passed to a bound plug-in supporting the specified operation.
 *
 * If the plug-in succeeds in deploying the Implementation Artifact, it will return an Endpoint URL
 * which is then passed to the Endpoint Service which stores the Endpoint in a database.
 *
 * @see org.opentosca.container.engine.ia.impl.IAEngineServiceImpl
 * @see org.opentosca.container.engine.ia.plugin.IIAEnginePluginService
 * @see org.opentosca.container.engine.ia.impl.IAEngineCapabilityChecker
 * @see org.opentosca.model.tosca.TImplementationArtifact
 * @see org.opentosca.model.tosca.TNodeTemplate
 * @see org.opentosca.model.tosca.TNodeType
 * @see org.opentosca.model.tosca.TOperation
 * @see org.opentosca.core.endpoint.service.ICoreEndpointService
 */

@NonNullByDefault
public interface IIAEngineService {

    /**
     * Deploy Implementation Artifacts.<br>
     * <br>
     *
     * This method should be called to deploy Implementation Artifacts.<br>
     * It will also check if an Implementation Artifact needs to be deployed or if it is sufficient to
     * store just a new endpoint with updated information.
     *
     * @param serviceTemplateID - ID of the ServiceTemplate to be processed.
     * @param csarID - ID of the CSAR file currently being processed.
     * @return Name of Implementation Artifacts that where not successfully deployed, <code>empty</code>
     *         if all Artifacts were deployed or <code>null</code> if TNodeType Object was null.
     */
    public List<String> deployImplementationArtifacts(Csar csar, TServiceTemplate serviceTemplate);

    /**
     * Undeploy Implementation Artifacts.<br>
     * <br>
     *
     * This method should be called to undeploy Implementation Artifacts.<br>
     *
     *
     * @param csarID - ID of the CSAR file of which the IAs should be undeployed.
     * @return <code>true</code> if all IAs were undeployed successfully. Otherwise <code>null</code>.
     */
    public boolean undeployImplementationArtifacts(Csar csar);
}
