package org.opentosca.bus.management.service.impl.util;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class determines on which OpenTOSCA Container instance an Implementation Artifact for a
 * certain service instance has to be deployed. It returns the host name of this Container instance
 * which can then be used by the deployment and invocation plug-ins to perform operations with the
 * Implementation Artifact.<br>
 * <br>
 *
 * To determine the correct Container, a matching with the instance data of the different available
 * Container instances is performed. Therefore, the infrastructure NodeType of the topology stack of
 * the IA is retrieved. Afterwards the matching of this NodeType with the instance data of the local
 * OpenTOSCA Container is done. If this is not successful, the matching request is distributed to
 * other Container via MQTT. In case there is also no match, the local Container is used as default
 * deployment location.<br>
 * <br>
 *
 * {@link Settings#OPENTOSCA_COLLABORATION_MODE} and the respective config.ini entry can be used to
 * control the matching. If the property is <tt>true</tt>, matching is performed. If it is set to
 * <tt>false</tt>, all IA deployments will be performed locally. Therefore, the performance can be
 * increased by this setting if distributed IA deployment is not needed.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class DeploymentDistributionDecisionMaker {

    private final static Logger LOG = LoggerFactory.getLogger(DeploymentDistributionDecisionMaker.class);

    /**
     * TODO
     */
    public static String getDeploymentLocation(final QName serviceTemplateID, final String nodeTemplateID) {

        // only perform matching if collaboration mode is turned on
        if (Settings.OPENTOSCA_COLLABORATION_MODE.equals("true")) {
            DeploymentDistributionDecisionMaker.LOG.debug("Deployment distribution decision for IAs from NodeTemplate: {}, ServiceTemplate: {}",
                                                          nodeTemplateID, serviceTemplateID);

            // TODO: local and remote instance data matching

            // default: return host name of local container
            return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        } else {
            DeploymentDistributionDecisionMaker.LOG.debug("Distributed IA deployment disabled. Using local deployment.");

            return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        }
    }

}
