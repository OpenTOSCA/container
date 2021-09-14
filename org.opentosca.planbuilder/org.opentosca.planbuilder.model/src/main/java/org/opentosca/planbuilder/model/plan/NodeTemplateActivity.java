package org.opentosca.planbuilder.model.plan;

import org.eclipse.winery.model.tosca.TNodeTemplate;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class NodeTemplateActivity extends AbstractActivity {

    private final TNodeTemplate nodeTemplate;

    public NodeTemplateActivity(final String id, final ActivityType type, final TNodeTemplate nodeTemplate) {
        super(id, type);
        this.nodeTemplate = nodeTemplate;
    }

    public TNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }
}
