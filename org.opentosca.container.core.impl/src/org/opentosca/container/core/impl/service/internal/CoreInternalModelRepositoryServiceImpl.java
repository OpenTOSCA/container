package org.opentosca.container.core.impl.service.internal;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.internal.ICoreInternalModelRepositoryService;
import org.opentosca.container.core.tosca.model.TDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * TODO: Completely remove Model Repository - needed Definitions should be directly fetched from the
 * TOSCA Reference Mapper in the TOSCA Engine.
 */
public class CoreInternalModelRepositoryServiceImpl implements ICoreInternalModelRepositoryService, CommandProvider {

    private IToscaEngineService toscaEngineService;

    /**
     * Logging
     */
    private final static Logger LOG = LoggerFactory.getLogger(CoreInternalModelRepositoryServiceImpl.class);

    @Override
    /**
     * {@inheritDoc}
     */
    public List<QName> getAllDefinitionsIDs(final CSARID csarID) {
        LOG.info("Getting IDs of all Definitions in CSAR \"{}\"...", csarID);

        if (this.toscaEngineService != null) {
            return this.toscaEngineService.getToscaReferenceMapper().getDefinitionIDsOfCSAR(csarID);
        }

        LOG.error("TOSCA Engine Service is not available! Can't get Definitions IDs of CSAR \"{}\"", csarID);
        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public TDefinitions getDefinitions(final CSARID csarID, final QName definitionsID) {

        LOG.info("Getting Definitions with ID \"{}\" in CSAR \"{}\"...", definitionsID.toString(), csarID.toString());

        if (this.toscaEngineService != null) {
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
        LOG.error("TOSCA Engine Service is not available! Can't get Definitions with ID \"{}\" of CSAR \"{}\"",
                  definitionsID.toString(), csarID.toString());
        return null;
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
