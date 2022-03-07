package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.opentosca.container.core.model.csar.Csar;

/**
 * handle BPMN Activity for Docker Engine NodeTemplate
 * specifically  generating Input/Output Parameter for script "DataObject.groovy"
 */
public class DockerEngineHandler {

    // TODO: implement detail
    public boolean isProvisionableByDockerEngine(TNodeTemplate nodeTemplate, Csar csar) {
        return false;
    }
}
