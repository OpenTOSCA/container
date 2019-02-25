package org.opentosca.container.core.engine.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.tosca.StaticTOSCANamespaces;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ImplementationArtifactResolver resolves references inside of TOSCA ImplementationArtifacts
 * according to the TOSCA specification wd14. Each found element and the document in which the
 * element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 *
 * Preconditions for resolving a ImplementationArtifact: Definitions has to be valid in all kind of
 * meanings.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class ImplementationArtifactResolver extends GenericResolver {

    private final Logger LOG = LoggerFactory.getLogger(ImplementationArtifactResolver.class);


    /**
     * Instantiate an object of the Resolver to resolve references inside of NodeTypeImplementations.
     * This constructor sets the ReferenceMapper which searches for references.
     *
     * @param referenceMapper
     */
    public ImplementationArtifactResolver(final ReferenceMapper referenceMapper) {
        super(referenceMapper);
    }

    /**
     * Resolves a ImplementationArtifact and stores the mapping into the ToscaReferenceMapper.
     *
     * @param implementationArtifact The ImplementationArtifact object.
     * @param ownerTargetNamespace the Namespace in which the ImplementationArtifact shall be
     * @param ownerName the Name of the NodeTypeImplementation or RelationshipTypeImplementation owning
     *        the ImplementationArtifact
     * @param iANumber the number of the ImplementationArtifact in the list of ImplementationArtifacts
     *        of the owner
     * @return true if an error occurred, false if not
     */
    public boolean resolve(final TImplementationArtifact implementationArtifact, final String ownerTargetNamespace,
                           final String ownerName, final int iANumber) {

        boolean errorOccurred = false;

        String implArtName =
            implementationArtifact.getOtherAttributes().get(new QName(StaticTOSCANamespaces.nsToscaExtension, "name"));

        // if the name attribute of the implementation artifact is not available
        if (null == implArtName) {
            implArtName = ownerName + StaticTOSCANamespaces.nameIANameExtension + iANumber;
            this.LOG.warn("One resolved implementation artifact of \"" + ownerName
                + "\" has no name attribute specified. Thus set the name \"" + implArtName + "\".");
            implementationArtifact.getOtherAttributes().put(new QName(StaticTOSCANamespaces.nsToscaExtension, "name"), implArtName);
            implementationArtifact.setName(implArtName);
        } else {
            this.LOG.debug("Found the implementation artifact name \"" + implArtName
                + "\" in the extension namespace of OpenTOSCA.");
            implementationArtifact.setName(implArtName);
        }

        this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(new QName(ownerTargetNamespace, implArtName),
                                                                     implementationArtifact);
        errorOccurred = errorOccurred
            || !this.referenceMapper.searchToscaElementByQNameWithName(implementationArtifact.getArtifactType(),
                                                                       ElementNamesEnum.ARTIFACTTYPE);
        if (implementationArtifact.getArtifactRef() != null
            && !implementationArtifact.getArtifactRef().toString().equals("")) {
            errorOccurred = errorOccurred
                || !this.referenceMapper.searchToscaElementByQNameWithID(implementationArtifact.getArtifactRef());
        }

        return errorOccurred;
    }
}
