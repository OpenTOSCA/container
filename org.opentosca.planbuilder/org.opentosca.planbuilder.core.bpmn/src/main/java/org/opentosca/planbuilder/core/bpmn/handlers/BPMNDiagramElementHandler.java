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
    // width of shape
    private final static int WIDTH = 100;
    // height of shape
    private final static int HEIGHT = 80;
    // length of edge
    private final static int LENGTH = 100;
    // buffer length for subprocess shape for both width and height
    private final static int LENGTH_BUFFER_SUBPROCESS = 100;

    /**
     * Create BPMN diagram instance from the input scope and add it to plan for later transform to XML element
     * Scope Type should have 1-to-1
     * @param startX: left waypoint in x-axis
     * @param startY: middle of left waypoint in y-axis
     * @param bpmnScope
     * @param bpmnPlan
     * @return
     */
    // TODO: consider making event half the width and height of normal activity
    // TODO: consider variable length and width for difference diagram
    public BPMNDiagramElement createDiagramElementFromScope(int startX, int startY, BPMNScope bpmnScope, BPMNPlan bpmnPlan) {
        String postfix = "_di";
        LOG.debug("Creating diagram from bpmnScope: {}", bpmnScope.getId());
        BPMNDiagramElement diagramInstance = null;
        if (bpmnScope.getBpmnScopeType() == BPMNScopeType.SEQUENCE_FLOW) {
            diagramInstance = new BPMNDiagramElement(BPMNDiagramType.EDGE, startX, startY, bpmnScope.getId() + postfix);
            diagramInstance.setWaypointOut(startX + LENGTH, startY);
            diagramInstance.setLength(LENGTH);
        // Only handle the shape of subprocess, other subprocess element will be handled separately with recursive
        } else if (bpmnScope.getBpmnScopeType() == BPMNScopeType.SUBPROCESS) {
            int spHeight = 2 * LENGTH_BUFFER_SUBPROCESS;
            int spWidth = 2 * LENGTH_BUFFER_SUBPROCESS;
            // Calculate the width and length of shape size accumulated by  elements in subprocess
            for (BPMNScope subElement : bpmnScope.getSubprocessBPMNScopes()) {
                if (subElement.getBpmnScopeType() == BPMNScopeType.SEQUENCE_FLOW) {
                    spWidth += LENGTH;
                } else {
                    spWidth += WIDTH;
                }
            }

            diagramInstance = new BPMNDiagramElement(BPMNDiagramType.SHAPE, startX, startY - spHeight / 2, bpmnScope.getId() + postfix);
            diagramInstance.setHeight(spHeight);
            diagramInstance.setWidth(spWidth);
            diagramInstance.setBufferLength(LENGTH_BUFFER_SUBPROCESS);
        } else {
            // for SHAPE, need to offset startY for boundary (top-left corner)
            diagramInstance = new BPMNDiagramElement(BPMNDiagramType.SHAPE, startX, startY - HEIGHT / 2, bpmnScope.getId() + postfix);
            diagramInstance.setHeight(HEIGHT);
            diagramInstance.setWidth(WIDTH);
        }

        diagramInstance.setRefScope(bpmnScope);

        // diagram instance is flatten, no recursive structure required
        bpmnPlan.addDiagramElement(diagramInstance);

        return diagramInstance;
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
