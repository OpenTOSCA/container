package org.opentosca.container.engine.ia.plugin;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.w3c.dom.Document;

/**
 * Interface for IAEnginePlugins.
 */
@NonNullByDefault
public interface IIAEnginePluginService {

    /**
     * Deploys an ImplementationArtifact.
     *
     * @param csarID
     * @param nodeTypeImpl
     * @param artifactType
     * @param artifactContent
     * @param properties
     * @param propertyConstraints
     * @param artifacts
     * @param requiredFeatures
     *
     * @return endpoint of deployed ImplementationArtifact ( <tt>endpoint == null</tt>, if deployment
     *         failed).
     */
    public URI deployImplementationArtifact(CSARID csarID, QName nodeTypeImpl, QName artifactType,
                                            Document artifactContent, @Nullable Document properties,
                                            List<TPropertyConstraint> propertyConstraints,
                                            List<AbstractArtifact> artifacts, List<String> requiredFeatures);

    /**
     * Undeploys an ImplementationArtifact.
     *
     * @param iaName
     * @param nodeTypeImpl
     * @param csarID
     * @param path
     *
     * @return of the specified IA was undeployed successfully.
     */
    public boolean undeployImplementationArtifact(String iaName, QName nodeTypeImpl, CSARID csarID, URI path);

    /**
     *
     * @return supported (file-)types of the plugin.
     */
    public List<String> getSupportedTypes();

    /**
     *
     * @return provided capabilities of the plugin.
     */
    public List<String> getCapabilties();

}
