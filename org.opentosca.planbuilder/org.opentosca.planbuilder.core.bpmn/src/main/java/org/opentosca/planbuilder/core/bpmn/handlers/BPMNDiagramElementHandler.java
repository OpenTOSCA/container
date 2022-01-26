package org.opentosca.planbuilder.core.bpmn.handlers;

import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramElement;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramType;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class is the facade for handling BPMN diagram element
 */
public class BPMNDiagramElementHandler {
    private final static Logger LOG = LoggerFactory.getLogger(BPMNDiagramElementHandler.class);
    private final static int WIDTH = 100;
    private final static int HEIGHT = 80;
    private final static int LENGTH = 100;

    /**
     * Create and diagram element from the input scope and add it to plan for later export of XML
     * @param startX: left waypoint in x-axis
     * @param startY: middle of left waypoint in y-axis
     * @param bpmnScope
     * @param bpmnPlan
     * @return
     */
    public BPMNDiagramElement createDiagramElementFromScope(int startX, int startY, BPMNScope bpmnScope, BPMNPlan bpmnPlan) {
        String postfix = "_di";

        LOG.debug("Creating diagram from bpmnScope: {}", bpmnScope.getId());
        BPMNDiagramElement dElement = null;
        if (bpmnScope.getBpmnScopeType() == BPMNScopeType.SEQUENCE_FLOW) {
            dElement = new BPMNDiagramElement(BPMNDiagramType.EDGE, startX, startY, bpmnScope.getId() +  "_di");
            dElement.setWaypointOut(startX + LENGTH, startY);
            dElement.setLength(LENGTH);
        } else {
            // for SHAPE, need to offset startY for boundary (top-left corner)
            dElement = new BPMNDiagramElement(BPMNDiagramType.SHAPE, startX, startY - HEIGHT / 2, bpmnScope.getId() + "_di");
            dElement.setHeight(HEIGHT);
            dElement.setWidth(WIDTH);
        }
        dElement.setRefScope(bpmnScope);
        bpmnPlan.addDiagramElement(dElement);

        // TODO: consider handle subprocess where the WIDTH should be proportional to elements inside subprocess
        // TODO: consider making event half the width and height of normal activity
        // TODO: consider variable length and width for difference diagram

        return dElement;
    }

    /**
     * Get the next position in x direction for connecting to this diagram
     * @param diagramElement
     * @return
     */
    public int getNextXpos(BPMNDiagramElement diagramElement) {
        // EDGE
        if (diagramElement.getType() == BPMNDiagramType.EDGE) {
            return diagramElement.getWaypointOutX();
        } else {
            return diagramElement.getXpos() + diagramElement.getWidth();
        }
    }

    /**
     * Get the next position in y direction for connecting to this diagram
     * @param diagramElement
     * @return
     */
    public int getNextYpos(BPMNDiagramElement diagramElement) {
        // EDGE
        if (diagramElement.getType() == BPMNDiagramType.EDGE) {
            return diagramElement.getWaypointOutY();
        } else {
            // for SHAPE, need to offset startY for boundary (top-left corner)
            return diagramElement.getYpos() + diagramElement.getHeight() / 2;
        }
    }
}
