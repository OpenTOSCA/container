package org.opentosca.planbuilder.model.plan.bpmn;


/**
 * <p>
 * This class is the placeholder for BPMN diagram elements before
 * it is generated to XML snippet
 * </p>
 * Copyright 2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kuang-Yu Li - st169971@stud.uni-stuttgart.de
 */
public class BPMNDiagramElement {

    public BPMNDiagramType type;
    public String id;
    // From spec, BPMN DI instance(s) and the referenced BPMN model are REQUIRED.
    public BPMNScope refScope;
    // start position for Edge and Shape
    private int xWaypointIn;
    private int yWaypointIn;
    // Only Edge has way point out
    private int xWaypointOut;
    private int yWaypointOut;

    // only edge contains
    public int length;

    // only shape contains
    public int width;
    public int height;

    // only for subprocess shape
    public int bufferLength;

    public BPMNDiagramElement(BPMNDiagramType type, int startX, int startY, String id) {
        this.type = type;
        this.id = id;
        setWaypointIn(startX, startY);
    }

    public BPMNDiagramType getType() {
        return type;
    }

    public void setType(BPMNDiagramType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BPMNScope getRefScope() {
        return refScope;
    }

    public void setRefScope(BPMNScope refScope) {
        this.refScope = refScope;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWaypointIn(int startX, int startY) {
        xWaypointIn = startX;
        yWaypointIn = startY;
    }

    public void setWaypointOut(int x, int y) {
        xWaypointOut = x;
        yWaypointOut = y;
    }

    public int getWaypointOutX() {
        return xWaypointOut;
    }

    public int getWaypointOutY() {
        return yWaypointOut;
    }

    public int getXpos() {
        return xWaypointIn;
    }

    public int getYpos() {
        return yWaypointIn;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }
}
