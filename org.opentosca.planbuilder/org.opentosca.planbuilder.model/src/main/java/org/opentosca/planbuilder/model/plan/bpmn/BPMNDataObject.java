package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a data object which is the bpmn equivalent of a bpel variable. In bowie there are three
 * different types of data objects(service, node, relation).
 */
public class BPMNDataObject {
    private String id;
    private BPMNSubprocessType dataObjectType;
    private List<String> properties = new ArrayList<>();

    private String serviceInstanceURL;
    private String sourceInstanceURL;
    private String targetInstanceURL;
    private String nodeInstanceURL;
    private String relationshipInstanceURL;

    private String nodeTemplate;
    private String relationshipTemplate;

    private double x;
    private double y;

    public BPMNDataObject(final BPMNSubprocessType dataObjectType, final String id) {
        this.dataObjectType = dataObjectType;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public BPMNSubprocessType getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(final BPMNSubprocessType dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(final List<String> properties) {
        this.properties = properties;
    }

    public String getServiceInstanceURL() {
        return serviceInstanceURL;
    }

    public void setServiceInstanceURL(final String serviceInstanceURL) {
        this.serviceInstanceURL = serviceInstanceURL;
    }

    public String getSourceInstanceURL() {
        return sourceInstanceURL;
    }

    public void setSourceInstanceURL(final String sourceInstanceURL) {
        this.sourceInstanceURL = sourceInstanceURL;
    }

    public String getTargetInstanceURL() {
        return targetInstanceURL;
    }

    public void setTargetInstanceURL(final String targetInstanceURL) {
        this.targetInstanceURL = targetInstanceURL;
    }

    public String getNodeInstanceURL() {
        return nodeInstanceURL;
    }

    public void setNodeInstanceURL(final String nodeInstanceURL) {
        this.nodeInstanceURL = nodeInstanceURL;
    }

    public String getNodeTemplate() {
        return nodeTemplate;
    }

    public void setNodeTemplate(final String nodeTemplate) {
        this.nodeTemplate = nodeTemplate;
    }

    public String getRelationshipTemplate() {
        return relationshipTemplate;
    }

    public void setRelationshipTemplate(final String relationshipTemplate) {
        this.relationshipTemplate = relationshipTemplate;
    }

    public String getRelationshipInstanceURL() {
        return relationshipInstanceURL;
    }

    public void setRelationshipInstanceURL(final String relationshipInstanceURL) {
        this.relationshipInstanceURL = relationshipInstanceURL;
    }

    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }
}
