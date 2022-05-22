package org.opentosca.planbuilder.core.bpmn.handlers;

import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.ModelInstance;
import org.w3c.dom.Element;

import java.util.HashMap;

/**
 * This class creates the flow elements.
 */
public class DrawFlow {

    public static BpmnPlane drawFlow(BpmnPlane plane, ModelInstance modelInstance, Element sfElement, HashMap<String, SequenceReferencePoints> refPoints) {

        String sourceRef = sfElement.getAttribute("sourceRef");
        System.out.println("SOURCEREF");
        System.out.println(sourceRef);
        String targetRef = sfElement.getAttribute("targetRef");
        SequenceReferencePoints srp = refPoints.get(sourceRef);

        double xExit = srp.getXExit();
        double yExit = srp.getYExit();
        srp = refPoints.get(targetRef);
        double xEntry = srp.getXEntry();
        double yEntry = srp.getYEntry();

        BpmnEdge bpmnEdge = modelInstance.newInstance(BpmnEdge.class);
        BaseElement element = modelInstance.getModelElementById(sfElement.getAttribute("id"));
        bpmnEdge.setBpmnElement(element);
        double xExitWaypoint = new Double(xExit);
        double yExitWaypoint = new Double(yExit);
        Waypoint wp = modelInstance.newInstance(Waypoint.class);
        wp.setX(xExitWaypoint);
        wp.setY(yExitWaypoint);
        bpmnEdge.addChildElement(wp);

        System.out.println("TYPENAME");
        System.out.println(refPoints.keySet());
        if (sourceRef.contains("Boundary")) {
            double xEdgeWaypoint = new Double(xExit);
            double yEdgeWaypoint = new Double(yEntry);
            wp = modelInstance.newInstance(Waypoint.class);
            wp.setX(xEdgeWaypoint);
            wp.setY(yEdgeWaypoint);
            bpmnEdge.addChildElement(wp);
        }
        double xEntryWaypoint = new Double(xEntry);
        double yEntryWaypoint = new Double(yEntry);
        wp = modelInstance.newInstance(Waypoint.class);
        wp.setX(xEntryWaypoint);
        wp.setY(yEntryWaypoint);

        bpmnEdge.addChildElement(wp);

        plane.addChildElement(bpmnEdge);

        return plane;
    }
}
