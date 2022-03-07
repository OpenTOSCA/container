package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.opentosca.container.core.model.csar.Csar;



/**
 * handle BPMN Activity for NodeTemplate (Type: Software-Component, Application) hosting on Docker Container
 * with the interface "ContainerManagementInterface" and operations "runScript" and "transferFile"
 * , which are executed on Docker Container instead of NodeTemplate
 * specifically generating Input/Output Parameter for script "CallNodeOperation.groovy"
 * reference in BPEL module: org.opentosca.planbuilder/org.opentosca.planbuilder.type.plugin.patternbased
 * /src/main/java/org/opentosca/planbuilder/type/plugin/patternbased/bpel/LifecyclePatternBasedHandler.java
 */
public class ContainerPatternHandler {

    // TODO: implement detail
    public boolean isProvisionableByContainerPattern(TNodeTemplate nodeTemplate, Csar csar) {
        // see if NodeTemplate is connected to a DockerContainer
        return false;
    }
}
