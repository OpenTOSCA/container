package org.opentosca.container.api.legacy.instancedata.utilities;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.instancedata.LinkBuilder;
import org.opentosca.container.portability.model.Artifacts;
import org.opentosca.container.portability.model.DeploymentArtifact;
import org.opentosca.container.portability.model.ImplementationArtifact;

/**
 * This class takes Artifacts with relative references and converts the references to absolute ones
 * which could be accessed from outside of the container
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class ArtifactAbsolutizer {

    /**
     * This method takes <code>org.opentosca.portability.service.model.Artifacts</code> and converts the
     * contained references to absolute ones (before they are like
     * <code>"/IA/ApacheWebserver/start.sh"</code> afterwards they will be external links f.ex.
     * <code>"http://localhost:1337/containerapi/csars/CSARID_1234/Content/IA/ApacheWebserver/start.sh"</code>
     *
     * This is used to ensure, that all external services can use those files
     *
     * @param uriInfo
     * @param csarID
     * @param artifacts
     */
    public static void absolutize(final UriInfo uriInfo, final String csarID, final Artifacts artifacts) {

        // TODO: create temp Artifacts to fallback if anything bad happens? (we
        // are working on the original object here - it will be permanently
        // altered if an error occurs during this operations)
        final List<DeploymentArtifact> das = artifacts.getDeploymentArtifact();
        final List<ImplementationArtifact> ias = artifacts.getImplementationArtifact();

        // null check of those lists because they sadly maybe null if only one
        // of both type is selected
        // we can't really work with empty lists because they will return in the
        // result as f.ex. </deploymentArtifacts> even when only selecting
        // implArtifacts
        if (das != null) {
            for (final DeploymentArtifact deploymentArtifact : das) {

                if (deploymentArtifact.getReferences() != null
                    && deploymentArtifact.getReferences().allReferences != null) {
                    final List<String> allDAReferences = deploymentArtifact.getReferences().allReferences;
                    for (int i = 0; i < allDAReferences.size(); i++) {
                        final String absolutizedRef = absolutizeReference(uriInfo, csarID, allDAReferences.get(i));
                        allDAReferences.set(i, absolutizedRef);
                    }
                }
            }
        }

        if (ias != null) {
            for (final ImplementationArtifact implArtifact : ias) {

                if (implArtifact.getReferences() != null && implArtifact.getReferences().allReferences != null) {
                    final List<String> allIAReferences = implArtifact.getReferences().allReferences;
                    for (int i = 0; i < allIAReferences.size(); i++) {
                        final String absolutizedRef = absolutizeReference(uriInfo, csarID, allIAReferences.get(i));
                        allIAReferences.set(i, absolutizedRef);
                    }
                }
            }
        }

    }

    private static String absolutizeReference(final UriInfo uriInfo, final String csarID,
                                              final String relativeReference) {
        return LinkBuilder.linkToFile(uriInfo, csarID, relativeReference).toString();
    }

}
