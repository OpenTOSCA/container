package org.opentosca.container.core.engine.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.tosca.model.Definitions;
import org.opentosca.container.core.tosca.model.TArtifactTemplate;
import org.opentosca.container.core.tosca.model.TExtensibleElements;

public class ArtifactTemplateResolver extends GenericResolver {

    public ArtifactTemplateResolver(final ReferenceMapper referenceMapper) {
        super(referenceMapper);
    }

    /**
     *
     * @param definitions
     * @return true if an error occurred, false if not
     */
    public boolean resolve(final Definitions definitions) {

        boolean errorOccurred = false;

        for (final TExtensibleElements element : definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {

            // ArtifactTemplate
            if (element instanceof TArtifactTemplate) {

                final TArtifactTemplate artifactTemplate = (TArtifactTemplate) element;
                this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(
                    new QName(definitions.getTargetNamespace(), artifactTemplate.getId()), artifactTemplate);

                // resolve the ArtifactType
                if (artifactTemplate.getType() != null && !artifactTemplate.getType().toString().equals("")) {
                    errorOccurred = errorOccurred
                        || !this.referenceMapper.searchToscaElementByQNameWithName(artifactTemplate.getType(),
                            ElementNamesEnum.ARTIFACTTYPE);
                }

                // Properties
                // nothing to do here

                // PropertyConstraints
                // nothing to do here

                // ArtifactReferences
                // if (artifactTemplate.getArtifactReferences() != null) {
                // for (TArtifactReference artifactReference :
                // artifactTemplate.getArtifactReferences().getArtifactReference())
                // {
                // this is done by the ToscaEngine with the method
                // getFilesOfAArtifactTemplate
                // }
                // }

            }
        }
        return errorOccurred;
    }
}
