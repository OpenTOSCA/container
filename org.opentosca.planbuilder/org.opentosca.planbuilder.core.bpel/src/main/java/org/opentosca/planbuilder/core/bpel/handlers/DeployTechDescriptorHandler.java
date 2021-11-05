package org.opentosca.planbuilder.core.bpel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.plugins.context.DeployTechDescriptorMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployTechDescriptorHandler {
    private static final String TAG_DEPLOYMENT_TECHNOLOGIES = "jsonDeploymentTechnologies";
    private final static String SUFFIX_DEPLOYMENT_TECHNOLOGY_DESCRIPTOR = "dtDescriptor";

    private static final Logger LOG = LoggerFactory.getLogger(DeployTechDescriptorHandler.class);

    private final BPELPlanHandler planHandler;

    public DeployTechDescriptorHandler(BPELPlanHandler planHandler) {
        this.planHandler = planHandler;
    }

    // TODO use the method provided in ModelUtilities
    private static List<DeploymentTechnologyDescriptor> extractDeploymentTechnologiesFromServiceTemplate(
        TServiceTemplate serviceTemplate, ObjectMapper objectMapper) {
        return Optional.ofNullable(serviceTemplate.getTags())
            .map(tags -> extractDeploymentTechnologiesFromTags(tags, objectMapper))
            .orElseGet(ArrayList::new);
    }

    // TODO use the method provided in ModelUtilities
    private static List<DeploymentTechnologyDescriptor> extractDeploymentTechnologiesFromTags(
        List<TTag> tags, ObjectMapper objectMapper) {
        return Optional.ofNullable(tags)
            .flatMap(tTags -> tTags.stream()
                .filter(tTag -> Objects.equals(tTag.getName(), TAG_DEPLOYMENT_TECHNOLOGIES))
                .findAny())
            .map(TTag::getValue)
            .map(s -> {
                CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, DeploymentTechnologyDescriptor.class);
                try {
                    return objectMapper.<List<DeploymentTechnologyDescriptor>>readValue(s, collectionType);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException("Deployment technologies tag could not be parsed as JSON", e);
                }
            })
            .orElseGet(ArrayList::new);
    }

    public DeployTechDescriptorMapping initializeDescriptorsAsVariables(BPELPlan plan, TServiceTemplate serviceTemplate) {
        TTopologyTemplate topologyTemplate = Objects.requireNonNull(serviceTemplate.getTopologyTemplate(), "topology template is null");
        ObjectMapper objectMapper = new ObjectMapper();
        List<DeploymentTechnologyDescriptor> descriptors = extractDeploymentTechnologiesFromServiceTemplate(serviceTemplate, objectMapper);
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

    // TODO use the class provided in winery model
    private static class DeploymentTechnologyDescriptor {
        private String id;
        private String technologyId;
        private List<String> managedIds;
        private Map<String, String> properties;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTechnologyId() {
            return technologyId;
        }

        public void setTechnologyId(String technologyId) {
            this.technologyId = technologyId;
        }

        public List<String> getManagedIds() {
            return managedIds;
        }

        public void setManagedIds(List<String> managedIds) {
            this.managedIds = managedIds;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }
}
