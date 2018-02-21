package org.opentosca.container.core.engine.impl.resolver;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.tosca.model.Definitions;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.opentosca.container.core.tosca.model.TPolicyTemplate;

public class PolicyTemplateResolver extends GenericResolver {

    public PolicyTemplateResolver(final ReferenceMapper referenceMapper) {
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

            // PolicyTemplate
            if (element instanceof TPolicyTemplate) {

                final TPolicyTemplate policyTemplate = (TPolicyTemplate) element;
                this.referenceMapper.storeJAXBObjectIntoToscaReferenceMapper(
                    new QName(definitions.getTargetNamespace(), policyTemplate.getId()), policyTemplate);

                // resolve the PolicyType
                if (policyTemplate.getType() != null) {
                    errorOccurred = errorOccurred
                        || !this.referenceMapper.searchToscaElementByQNameWithName(policyTemplate.getType(),
                            ElementNamesEnum.POLICYTYPE);
                }

                // Properties
                // nothing to do here

                // PropertyConstraints
                // nothing to do here

            }
        }
        return errorOccurred;
    }
}
