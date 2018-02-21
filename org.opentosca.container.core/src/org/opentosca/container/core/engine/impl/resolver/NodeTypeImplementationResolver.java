package org.opentosca.container.core.engine.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.ToscaEngineServiceImpl;
import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.model.Definitions;
import org.opentosca.container.core.tosca.model.TDeploymentArtifact;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.opentosca.container.core.tosca.model.TImplementationArtifact;
import org.opentosca.container.core.tosca.model.TNodeTypeImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The NodeTypeImplementationResolver resolves references inside of TOSCA NodeTypeImplementations
 * according to the TOSCA specification wd14. Each found element and the document in which the
 * element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 *
 * Preconditions for resolving a NodeTypeImplementation: Definitions has to be valid in all kind of
 * meanings.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class NodeTypeImplementationResolver extends GenericResolver {

    private final Logger LOG = LoggerFactory.getLogger(NodeTypeImplementationResolver.class);

    private final CSARID csarID;


    /**
     * Instantiate an object of the Resolver to resolve references inside of NodeTypeImplementations.
     * This constructor sets the ReferenceMapper which searches for references.
     *
     * @param referenceMapper
     * @param csarID
     */
    public NodeTypeImplementationResolver(final ReferenceMapper referenceMapper, final CSARID csarID) {
        super(referenceMapper);
        this.csarID = csarID;
    }

    /**
     * Resolves all NodeTypeImplementations inside of a Definitions and stores the mapping into the
     * ToscaReferenceMapper.
     *
     * @param definitions The Definitions object.
     * @return true if an error occurred, false if not
     */
    public boolean resolve(final Definitions definitions) {

        boolean errorOccurred = false;

        for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (element instanceof TNodeTypeImplementation) {

                final TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) element;

                // store the NodeTypeImplementation
                String targetNamespace;
                if (nodeTypeImplementation.getTargetNamespace() != null
                    && !nodeTypeImplementation.getTargetNamespace().equals("")) {
                    targetNamespace = nodeTypeImplementation.getTargetNamespace();
                } else {
                    targetNamespace = definitions.getTargetNamespace();
                }

                this.LOG.debug("Resolve the NodeTypeImplementation \"" + targetNamespace + ":"
                    + nodeTypeImplementation.getName() + "\".");

                // is the NodeType known
                if (!ToscaEngineServiceImpl.toscaReferenceMapper.containsReferenceInsideCSAR(this.csarID,
                    nodeTypeImplementation.getNodeType())) {
                    this.LOG.error("The NodeTypeImplementation \"" + targetNamespace + ":"
                        + nodeTypeImplementation.getName() + "\" refers to the NodeType \""
                        + nodeTypeImplementation.getNodeType() + "\" which was not found.");
                }

                // Tags
                // nothing to do

                // DerivedFrom
                if (nodeTypeImplementation.getDerivedFrom() != null) {
                    errorOccurred = errorOccurred || !this.referenceMapper.searchToscaElementByQNameWithName(
                        nodeTypeImplementation.getDerivedFrom().getNodeTypeImplementationRef(),
                        ElementNamesEnum.NODETYPEIMPLEMENTATION);
                }

                // RequiredContainerFeatures
                // nothing to do here

                // ImplementationArtifacts
                if (nodeTypeImplementation.getImplementationArtifacts() != null) {
                    for (final TImplementationArtifact implementationArtifact : nodeTypeImplementation.getImplementationArtifacts()
                                                                                                      .getImplementationArtifact()) {
                        final int iANumber = nodeTypeImplementation.getImplementationArtifacts()
                                                                   .getImplementationArtifact()
                                                                   .indexOf(implementationArtifact);
                        errorOccurred = errorOccurred
                            || new ImplementationArtifactResolver(this.referenceMapper).resolve(implementationArtifact,
                                targetNamespace, nodeTypeImplementation.getName(), iANumber);
                    }
                }

                // DeploymentArtifacts
                if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
                    for (final TDeploymentArtifact deploymentArtifact : nodeTypeImplementation.getDeploymentArtifacts()
                                                                                              .getDeploymentArtifact()) {
                        errorOccurred = errorOccurred || new DeploymentArtifactResolver(this.referenceMapper).resolve(
                            deploymentArtifact, targetNamespace);
                    }
                }

                this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(
                    new QName(targetNamespace, nodeTypeImplementation.getName()), nodeTypeImplementation);
            }
        }
        return errorOccurred;
    }
}
