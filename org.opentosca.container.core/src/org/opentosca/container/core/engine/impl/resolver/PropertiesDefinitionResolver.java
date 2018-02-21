package org.opentosca.container.core.engine.impl.resolver;

import org.opentosca.container.core.tosca.model.TEntityType.PropertiesDefinition;

/**
 * The PropertiesDefinitionResolver resolves references inside of TOSCA PropertiesDefinitions
 * according to the TOSCA specification wd14. Each found element and the document in which the
 * element is nested is stored by the
 * org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 *
 * Preconditions for resolving a PropertiesDefinition: Definitions has to be valid in all kind of
 * meanings.
 *
 * Copyright 2012 Christian Endres
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 *
 */
public class PropertiesDefinitionResolver extends GenericResolver {

    /**
     * Instantiate an object of the Resolver to resolve references inside of PropertiesDefinitions. This
     * constructor sets the ReferenceMapper which searches for references.
     *
     * @param referenceMapper
     */
    public PropertiesDefinitionResolver(final ReferenceMapper referenceMapper) {
        super(referenceMapper);
    }

    /**
     * Resolves all PropertiesDefinitions inside of a Definitions and stores the mapping into the
     * ToscaReferenceMapper.
     *
     * @param definitions The Definitions object.
     * @return true if an error occurred, false if not
     */
    public boolean resolve(final PropertiesDefinition propertiesDefinition) {
        if (propertiesDefinition.getElement() != null ^ propertiesDefinition.getType() != null) {
            if (propertiesDefinition.getElement() != null) {
                this.referenceMapper.searchXMLElement(propertiesDefinition.getElement());
            } else {
                this.referenceMapper.searchXMLType(propertiesDefinition.getType());
            }
        } else {
            return true;
        }

        return false;
    }
}
