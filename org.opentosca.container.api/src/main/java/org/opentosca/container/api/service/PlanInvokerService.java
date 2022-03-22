package org.opentosca.container.api.service;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.deployment.checks.DeploymentTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// TODO: remove DTOs and move to control package
@Service
public class PlanInvokerService {

    private static final Logger logger = LoggerFactory.getLogger(PlanInvokerService.class);
    private final OpenToscaControlService controlService;
    private final DeploymentTestService deploymentTestService;

    @Inject
    public PlanInvokerService(OpenToscaControlService controlService, DeploymentTestService deploymentTestService) {
        this.controlService = controlService;
        this.deploymentTestService = deploymentTestService;
    }

    public String invokePlan(Csar csar, TServiceTemplate serviceTemplate, Long serviceTemplateInstanceId, String planId, List<TParameter> parameters, PlanType... planTypes) {
        TPlan plan = csar.plans().stream()
            .filter(tplan -> tplan.getId().equals(planId)
                && Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Plan \"" + planId + "\" could not be found"));

        final PlanDTO dto = new PlanDTO(plan);

        dto.setId(plan.getId());
        enhanceInputParameters(parameters);
        dto.setInputParameters(parameters);

        final String correlationId = controlService.invokePlanInvocation(csar.id(), serviceTemplate,
            serviceTemplateInstanceId,
            PlanDTO.Converter.convert(dto));
        if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD)
            && Boolean.parseBoolean(Settings.OPENTOSCA_DEPLOYMENT_TESTS)) {
            logger.debug("Plan \"{}\" is a build plan, so we schedule deployment tests...", plan.getName());
            this.deploymentTestService.runAfterPlan(csar.id(), correlationId);
        }
        return correlationId;
    }

    // @TODO move or merge this with MBJavaAPI#createRequestBody
    private void enhanceInputParameters(List<TParameter> parameters) {
        // set "meta" params
        for (final TParameter param : parameters) {
            if (param.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT)
                && param.getValue() != null && param.getValue().isEmpty()) {
                final String containerRepoUrl = Settings.getSetting("org.opentosca.container.connector.winery.url");
                param.setValue(containerRepoUrl);
            }
        }
    }
}
