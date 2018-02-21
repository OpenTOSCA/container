package org.opentosca.container.engine.ia.plugin.script;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.tosca.model.TPropertyConstraint;
import org.opentosca.container.engine.ia.plugin.IIAEnginePluginService;
import org.opentosca.container.engine.ia.plugin.script.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * IAEnginePlugin for Scripts.
 *
 * Since scripts dont have to be deployed, this plugin just ensure that script-IAs won't be marked
 * as failed.
 *
 * @see ICoreFileService
 */
public class IAEnginePluginScriptServiceImpl implements IIAEnginePluginService {

    // In messages.properties defined plugin-type and capabilities .
    static final private String TYPES = Messages.ScriptIAEnginePlugin_types;
    static final private String CAPABILITIES = Messages.ScriptIAEnginePlugin_capabilities;

    static final private Logger LOG = LoggerFactory.getLogger(IAEnginePluginScriptServiceImpl.class);


    @Override
    public URI deployImplementationArtifact(final CSARID csarID, final QName nodeTypeImplementationID,
                    final QName artifactType, final Document artifactContent, final Document properties,
                    final List<TPropertyConstraint> propertyConstraints, final List<AbstractArtifact> artifacts,
                    final List<String> requiredFeatures) {

        // Maybe some checks can be done here. (ScriptLanguage supported?,
        // Script defined?, Script contained in csar file?, SI-Script-Plugin
        // available...)
        URI uri = null;
        try {
            uri = new URI("si:ScriptPlugin");
        } catch (final URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedTypes() {
        IAEnginePluginScriptServiceImpl.LOG.debug("Getting Types: {}.", IAEnginePluginScriptServiceImpl.TYPES);
        final List<String> types = new ArrayList<>();

        for (final String type : IAEnginePluginScriptServiceImpl.TYPES.split("[,;]")) {
            types.add(type.trim());
        }
        return types;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilties() {
        IAEnginePluginScriptServiceImpl.LOG.debug("Getting Plugin-Capabilities: {}.",
            IAEnginePluginScriptServiceImpl.CAPABILITIES);
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : IAEnginePluginScriptServiceImpl.CAPABILITIES.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }

    @Override
    public boolean undeployImplementationArtifact(final String iaName, final QName nodeTypeImpl, final CSARID csarID,
                    final URI path) {
        // TODO
        return true;
    }

}
