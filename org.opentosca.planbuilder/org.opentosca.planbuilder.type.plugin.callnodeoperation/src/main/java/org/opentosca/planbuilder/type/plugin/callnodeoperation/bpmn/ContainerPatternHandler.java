package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn.EnginePatternHandler.getDockerEngineNode;


/**
 * <p>
 * handle BPMN Activity for NodeTemplate (Type: Software-Component, Application) hosting on Docker Container
 * with the interface "ContainerManagementInterface" and operations "runScript" and "transferFile"
 * , which are executed on Docker Container instead of NodeTemplate
 * specifically generating Input/Output Parameter for script "CallNodeOperation.groovy"
 * reference in BPEL module: org.opentosca.planbuilder/org.opentosca.planbuilder.type.plugin.patternbased
 * /src/main/java/org/opentosca/planbuilder/type/plugin/patternbased/bpel/LifecyclePatternBasedHandler.java
 * </p>
 * Copyright 2021 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kuang-Yu Li - st169971@stud.uni-stuttgart.de
 */
public class ContainerPatternHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerPatternHandler.class);

    public static final String INPUT_PARAM_HOSTNODE_NAME = "HostNodeTemplate";
    public static final String INPUT_PARAM_TARGETNODE_NAME = "TargetNodeTemplate";
    public static final String INPUT_PARAM_SCRIPT = "Script";

    public boolean isProvisionableByContainerPattern(TNodeTemplate nodeTemplate, Csar csar) {
        // see if NodeTemplate is hosted on a DockerContainer
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodes, csar);
        for (final TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedContainerNodeType(node.getType())) {
                return true;
            }
        }
        return false;
    }

    public boolean handleCreate(BPMNScope callNodeOperationTask, BPMNPlanContext context) {

        if (callNodeOperationTask.getNodeTemplate() == null) {
            LOG.debug("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        final TNodeTemplate nodeTemplate = callNodeOperationTask.getNodeTemplate();

        if (nodeTemplate.getProperties() == null) {
            LOG.error("NodeTemplate doesn't contain any property");
            return false;
        }

        // fetch DockerContainer
        final TNodeTemplate dockerContainerNode = getDockerContainerNode(nodeTemplate, context.getCsar());

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = getDockerEngineNode(nodeTemplate, context.getCsar());

        if (dockerContainerNode == null) {
            LOG.error("Couldn't fetch DockerContainer to install given NodeTemplate");
        }

        Map<String, String> enginePropMap = ModelUtils.asMap(dockerEngineNode.getProperties());
        List<String> inputPropList = new ArrayList<>();

        callNodeOperationTask.addInputparameter("ServiceInstanceID", "${ServiceInstanceURL}");
        callNodeOperationTask.addInputparameter("CsarID", context.getCsar().id().csarName());
        callNodeOperationTask.addInputparameter("ServiceTemplateID", context.getServiceTemplate().getId());
        callNodeOperationTask.addInputparameter("Interface", Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER);
        callNodeOperationTask.addInputparameter("Operation", Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT);
        // host node is for executing the operation
        callNodeOperationTask.addInputparameter(INPUT_PARAM_HOSTNODE_NAME, dockerContainerNode.getId());
        // target node is for setting node instance property
        callNodeOperationTask.addInputparameter(INPUT_PARAM_TARGETNODE_NAME, nodeTemplate.getId());

        // Input: DockerEngine, DockerEngineCertificate, ContainerID, Script
        String[] engineProperties = new String[] {
            DockerEngineConstants.PROPERTY_DOCKER_ENGINE_URL,
            DockerEngineConstants.PROPERTY_DOCKER_ENGINE_CERTIFICATE
        };

        inputPropList.addAll(this.createInputParameterFromProperties(engineProperties, enginePropMap, callNodeOperationTask));

        // reading runtime property from unique variable for ContainerID
        callNodeOperationTask.addInputparameter(DockerContainerConstants.PROPERTY_CONTAINER_ID,
            "${" + dockerContainerNode.getId() + "_" + DockerContainerConstants.PROPERTY_CONTAINER_ID + "}");

        final Collection<TNodeTypeImplementation> nodeTypeImpls = ModelUtils.findNodeTypeImplementation(nodeTemplate, context.getCsar());

        if (nodeTypeImpls.isEmpty()) {
            LOG.warn("No implementations available for NodeTemplate {} , can't generate Provisioning logic",
                nodeTemplate.getId());
            return false;
        }

        boolean iaFound = false;
        for (TNodeTypeImplementation nodeTypeImpl : nodeTypeImpls) {
           List<TImplementationArtifact> ias = nodeTypeImpl.getImplementationArtifacts();
           String id = nodeTypeImpl.getName();
           for (TImplementationArtifact ia : ias) {
               if (ia.getInterfaceName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER) &&
                   ia.getOperationName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)) {
                   iaFound = true;
                   String iaName = String.valueOf(ia.getArtifactRef());
                   callNodeOperationTask.addInputparameter(INPUT_PREFIX + INPUT_PARAM_SCRIPT,
                       "IA!" + id + "#" + iaName);
                   inputPropList.add(INPUT_PARAM_SCRIPT);
                   break;
               }
           }

           if (iaFound) {
               break;
           }
        }

        collectPropAsInputParameter(inputPropList, callNodeOperationTask);
        return true;
    }

    /**
     * reuses the same logic in org/opentosca/planbuilder/type/plugin/dockercontainer/core/DockerContainerTypePlugin.java
     * @param nodeTemplate
     * @param csar
     * @return
     */
    public static TNodeTemplate getDockerContainerNode(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodes, csar);

        for (final TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedContainerNodeType(node.getType())) {
                return node;
            }
        }
        return null;
    }
}
