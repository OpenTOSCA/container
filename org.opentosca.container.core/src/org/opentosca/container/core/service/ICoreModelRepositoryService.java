package org.opentosca.container.core.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.eclipse.winery.model.tosca.TDefinitions;

/**
 * This interface provides methods to retrieve and store Tosca-XML files. It is meant to be used by
 * the Engines.
 */
@Deprecated
public interface ICoreModelRepositoryService {
    /**
     *
     * @return IDs of all Definitions of CSAR <code>csarID</code>.
     */
    public List<QName> getAllDefinitionsIDs(CSARID csarID);

    /**
     * @param definitionsID
     * @return Definitions with ID <code> definitionsID</code> of CSAR <code>csarID</code>. If it
     *         doesn't exist <code>null</code>.
     */
    public TDefinitions getDefinitions(CSARID csarID, QName definitionsID);
}
