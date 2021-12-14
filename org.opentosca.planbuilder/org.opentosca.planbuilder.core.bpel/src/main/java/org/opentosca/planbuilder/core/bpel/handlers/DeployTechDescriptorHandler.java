package org.opentosca.planbuilder.core.bpel.handlers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.DeploymentTechnologyDescriptor;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.plugins.context.DeployTechDescriptorMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployTechDescriptorHandler {
    private final static String SUFFIX_DEPLOYMENT_TECHNOLOGY_DESCRIPTOR = "dtDescriptor";

    private static final Logger LOG = LoggerFactory.getLogger(DeployTechDescriptorHandler.class);

    private final BPELPlanHandler planHandler;

    public DeployTechDescriptorHandler(BPELPlanHandler planHandler) {
        this.planHandler = planHandler;
    }

    public DeployTechDescriptorMapping initializeDescriptorsAsVariables(BPELPlan plan, TServiceTemplate serviceTemplate) {
        TTopologyTemplate topologyTemplate = Objects.requireNonNull(serviceTemplate.getTopologyTemplate(), "topology template is null");
        ObjectMapper objectMapper = new ObjectMapper();
        List<DeploymentTechnologyDescriptor> descriptors = ModelUtilities.extractDeploymentTechnologiesFromServiceTemplate(serviceTemplate, objectMapper);
        DeployTechDescriptorMapping deployTechDescriptorMapping = new DeployTechDescriptorMapping();
        for (DeploymentTechnologyDescriptor descriptor : descriptors) {
            LOG.debug("Adding variables for properties of deployment technology descriptor |{}|", descriptor.getId());
            for (Map.Entry<String, String> property : descriptor.getProperties().entrySet()) {
                String candidatePropVarName = createPropertyVariableName(serviceTemplate, descriptor, property.getKey());
                while (!this.planHandler.addStringVariable(candidatePropVarName, plan)) {
                    candidatePropVarName = this.createPropertyVariableName(serviceTemplate, descriptor, property.getKey());
                }
                final String finalPropVarName = candidatePropVarName;

                descriptor.getManagedIds()
                    .stream()
                    .map(nodeId -> topologyTemplate.getNodeTemplates()
                        .stream()
                        .filter(nodeTemplate -> Objects.equals(nodeTemplate.getId(), nodeId))
                        .findAny())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(nodeTemplate -> new PropertyVariable(serviceTemplate, nodeTemplate, finalPropVarName, property.getKey()))
                    .forEach(deployTechDescriptorMapping::addVarMapping);

                LOG.debug("Setting variable |{}| with value |{}|", finalPropVarName, property.getValue());
                this.planHandler.assignInitValueToVariable(finalPropVarName, property.getValue(), plan);
            }
        }
        return deployTechDescriptorMapping;
    }

    private String createPropertyVariableName(final TServiceTemplate serviceTemplate,
                                              final DeploymentTechnologyDescriptor descriptor, final String propertyName) {
        return ModelUtils.makeValidNCName(new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()).toString()) + "_"
            + ModelUtils.makeValidNCName(descriptor.getId()) + "_" + propertyName + "_" + SUFFIX_DEPLOYMENT_TECHNOLOGY_DESCRIPTOR
            + System.currentTimeMillis();
    }
}
