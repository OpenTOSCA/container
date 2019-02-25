package org.opentosca.bus.management.deployment.plugin.script;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.deployment.plugin.script.util.Messages;
import org.opentosca.bus.management.header.MBHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management Bus-Plug-in for the deployment of Script IAs.<br>
 * <br>
 *
 *
 *
 * Since Script IAs have to be executed on a host machine, they don´t have to be deployed.
 * Therefore, this Plug-in is only a wrapper for the supported types and capabilities. When the
 * deployment is invoked it just returns a wildcard endpoint. Likewise, it always returns success
 * when the undeployment is invoked.
 *
 *
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 *
 */
public class ManagementBusDeploymentPluginScript implements IManagementBusDeploymentPluginService {

    // In messages.properties defined plugin types and capabilities
    static final private String TYPES = Messages.DeploymentPluginScript_types;
    static final private String CAPABILITIES = Messages.DeploymentPluginScript_capabilities;

    static final private Logger LOG = LoggerFactory.getLogger(ManagementBusDeploymentPluginScript.class);

    @Override
    public Exchange invokeImplementationArtifactDeployment(final Exchange exchange) {
        URI endpoint = null;
        try {
            // return dummy endpoint for further processing without aborting due to missing endpoint
            endpoint = new URI("ManagementBusDeploymentPluginScript:ScriptEndpoint");
        }
        catch (final URISyntaxException e) {
            e.printStackTrace();
        }
        exchange.getIn().setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);
        return exchange;
    }

    @Override
    public Exchange invokeImplementationArtifactUndeployment(final Exchange exchange) {
        exchange.getIn().setHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), true);
        return exchange;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedTypes() {
        ManagementBusDeploymentPluginScript.LOG.debug("Getting Types: {}.", ManagementBusDeploymentPluginScript.TYPES);
        final List<String> types = new ArrayList<>();

        for (final String type : ManagementBusDeploymentPluginScript.TYPES.split("[,;]")) {
            types.add(type.trim());
        }
        return types;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilties() {
        ManagementBusDeploymentPluginScript.LOG.debug("Getting Plugin-Capabilities: {}.",
                                                      ManagementBusDeploymentPluginScript.CAPABILITIES);
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : ManagementBusDeploymentPluginScript.CAPABILITIES.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }
}
