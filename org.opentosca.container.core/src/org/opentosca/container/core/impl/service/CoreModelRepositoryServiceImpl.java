package org.opentosca.container.core.impl.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreModelRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.winery.model.tosca.TDefinitions;

/**
 * This implementation currently acts as a Proxy to the Internal Core Model Repository service. It
 * can in future be used to modify the incoming parameters to fit another backend
 * interface/implementation
 *
 * @see ICoreInternalModelRepositoryService
 */
public class CoreModelRepositoryServiceImpl implements ICoreModelRepositoryService, CommandProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CoreModelRepositoryServiceImpl.class);

    private IToscaEngineService toscaEngineService;

    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    @Override
    public List<QName> getAllDefinitionsIDs(final CSARID csarID) {
        LOG.info("Getting IDs of all Definitions in CSAR \"{}\"...", csarID);
        if (this.toscaEngineService == null) {
            LOG.error("TOSCA Engine Service is not available! Can't get Definitions IDs of CSAR \"{}\"", csarID);
            return null;
        }
        return this.toscaEngineService.getToscaReferenceMapper().getDefinitionIDsOfCSAR(csarID);
    }

    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    @Override
    public TDefinitions getDefinitions(final CSARID csarID, final QName definitionsID) {
        LOG.info("Getting Definitions with ID \"{}\" in CSAR \"{}\"...", definitionsID.toString(), csarID.toString());

        if (this.toscaEngineService == null) {
            LOG.error("TOSCA Engine Service is not available! Can't get Definitions with ID \"{}\" of CSAR \"{}\"",
                      definitionsID.toString(), csarID.toString());
            return null;
        }
        final Object definitions =
            this.toscaEngineService.getToscaReferenceMapper().getJAXBReference(csarID, definitionsID);
        if (definitions instanceof TDefinitions) {
            return (TDefinitions) definitions;
        } else {
            LOG.error("Definitions with ID \"{}\" was not found in CSAR \"{}\"!", definitionsID.toString(),
                      csarID.toString());
            return null;
        }
    }
    
    public void bindToscaEngineService(final IToscaEngineService toscaEngineService) {
        this.toscaEngineService = toscaEngineService;
        LOG.debug("Tosca Engine Service bound.");
    }

    public void unbindToscaEngineService(final IToscaEngineService toscaEngineService) {
        this.toscaEngineService = toscaEngineService;
        LOG.debug("Tosca Engine Service unbound.");
    }

    /**
     * The following methods are OSGi-Console-Commands
     */
    @Override
    public String getHelp() {
        final StringBuilder help = new StringBuilder();
        help.append("--- OpenTOSCA Core Model Repository Management ---\n");
        help.append("\tgetAllDefinitionsIDs - Gets the IDs of all stored Definitions.\n");
        return help.toString();
    }
}
