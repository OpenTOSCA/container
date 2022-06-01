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

    public BPMNDataObject(BPMNSubprocessType dataObjectType, String id) {
        this.dataObjectType = dataObjectType;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BPMNSubprocessType getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(BPMNSubprocessType dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public String getServiceInstanceURL() {
        return serviceInstanceURL;
    }

    public void setServiceInstanceURL(String serviceInstanceURL) {
        this.serviceInstanceURL = serviceInstanceURL;
    }

    public String getSourceInstanceURL() {
        return sourceInstanceURL;
    }

    public void setSourceInstanceURL(String sourceInstanceURL) {
        this.sourceInstanceURL = sourceInstanceURL;
    }

    public String getTargetInstanceURL() {
        return targetInstanceURL;
    }

    public void setTargetInstanceURL(String targetInstanceURL) {
        this.targetInstanceURL = targetInstanceURL;
    }

    public String getNodeInstanceURL() {
        return nodeInstanceURL;
    }

    public void setNodeInstanceURL(String nodeInstanceURL) {
        this.nodeInstanceURL = nodeInstanceURL;
    }

    public String getNodeTemplate() {
        return nodeTemplate;
    }

    public void setNodeTemplate(String nodeTemplate) {
        this.nodeTemplate = nodeTemplate;
    }

    public String getRelationshipTemplate() {
        return relationshipTemplate;
    }

    public void setRelationshipTemplate(String relationshipTemplate) {
        this.relationshipTemplate = relationshipTemplate;
    }

    public String getRelationshipInstanceURL() {
        return relationshipInstanceURL;
    }

    public void setRelationshipInstanceURL(String relationshipInstanceURL) {
        this.relationshipInstanceURL = relationshipInstanceURL;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
