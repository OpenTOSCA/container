package org.opentosca.bus.management.service.impl.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Analyzes if a given Implementation Artifact is deployable, meaning checking if the required
 * capabilities of the Implementation Artifact are met by the container and/or available plug-ins
 * (plan + deployment).<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 */

@Service
public class DeploymentPluginCapabilityChecker {

  private final ICoreCapabilityService capabilityService;

  @Inject
  public DeploymentPluginCapabilityChecker(ICoreCapabilityService capabilityService) {
    this.capabilityService = capabilityService;
  }

  /**
   * Checks if required features are met by chosen plug-in or container and plan.
   *
   * @param requiredFeatures the set of features to be satisfied
   * @param plugin           the deployment plug-in
   * @return true if all requiredFeatures are met, false otherwise
   */
  public boolean capabilitiesAreMet(final List<String> requiredFeatures,
                                           final IManagementBusDeploymentPluginService plugin) {
    if (requiredFeatures.isEmpty()) {
      return true;
    }

    // get all provided capabilities
    final List<String> capabilities = new ArrayList<>();
    capabilities.addAll(getContainerAndPlanCapabilities());
    capabilities.addAll(plugin.getCapabilties());

    // remove all required features that are satisfied by a capability
    for (final Iterator<String> itReqCaps = requiredFeatures.iterator(); itReqCaps.hasNext(); ) {
      final String reqCap = itReqCaps.next();
      if (capabilities.contains(reqCap)) {
        itReqCaps.remove();
      }
    }

    // return true if no further requested feature exists
    return requiredFeatures.isEmpty();

  }

  /**
   * Returns container and plan capabilities from the CoreCapabilitiyService.
   *
   * @return container and plan capabilities in one merged list.
   */
  private List<String> getContainerAndPlanCapabilities() {

    final List<String> conAndPlanCaps = new ArrayList<>();

    final List<String> containerCaps = capabilityService.getCapabilities(ProviderType.CONTAINER.toString(), ProviderType.CONTAINER);
    final Map<String, List<String>> planPluginsCaps = capabilityService.getCapabilities(ProviderType.PLAN_PLUGIN);

    conAndPlanCaps.addAll(containerCaps);

    for (final String planPlugin : planPluginsCaps.keySet()) {
      conAndPlanCaps.addAll(planPluginsCaps.get(planPlugin));
    }

    return conAndPlanCaps;
  }

}
