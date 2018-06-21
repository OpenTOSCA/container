package org.opentosca.bus.management.deployment.plugin.tomcat;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.deployment.plugin.tomcat.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management Bus-Plug-in for the deployment of WAR IAs on an Apache Tomcat web server.<br>
 * <br>
 *
 *
 *
 * TODO
 *
 *
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 *
 */
public class ManagementBusDeploymentPluginTomcat implements IManagementBusDeploymentPluginService {

    // In messages.properties defined plugin types and capabilities
    static final private String TYPES = Messages.DeploymentPluginTomcat_types;
    static final private String CAPABILITIES = Messages.DeploymentPluginTomcat_capabilities;

    static final private Logger LOG = LoggerFactory.getLogger(ManagementBusDeploymentPluginTomcat.class);

    @Override
    public Exchange invokeImplementationArtifactDeployment(final Exchange exchange) {
        // TODO
        return null;
    }

    @Override
    public Exchange invokeImplementationArtifactUndeployment(final Exchange exchange) {
        // TODO
        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedTypes() {
        ManagementBusDeploymentPluginTomcat.LOG.debug("Getting Types: {}.", ManagementBusDeploymentPluginTomcat.TYPES);
        final List<String> types = new ArrayList<>();

        for (final String type : ManagementBusDeploymentPluginTomcat.TYPES.split("[,;]")) {
            types.add(type.trim());
        }
        return types;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilties() {
        ManagementBusDeploymentPluginTomcat.LOG.debug("Getting Plugin-Capabilities: {}.",
                                                      ManagementBusDeploymentPluginTomcat.CAPABILITIES);
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : ManagementBusDeploymentPluginTomcat.CAPABILITIES.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }
}
