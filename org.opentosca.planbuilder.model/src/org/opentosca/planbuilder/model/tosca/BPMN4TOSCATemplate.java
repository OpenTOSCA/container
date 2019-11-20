package org.opentosca.planbuilder.model.tosca;

public class BPMN4TOSCATemplate {

    private String[] connection;
    private String id;
    private Parameter[] input;
    private String name;
    private String nodeInterface;
    private String nodeOperation;
    private String nodeTemplate;
    private Parameter[] output;
    private Position position;
    private NodeTemplate template;
    private String type;
    private String instanceType;

    public String[] getConnection() {
        return this.connection;
    }

    public String getId() {
        return this.id;
    }

    public Parameter[] getInput() {
        return this.input;
    }

    public String getName() {
        return this.name;
    }

    public String getNodeInterface() {
        return this.nodeInterface;
    }

    public String getNodeOperation() {
        return this.nodeOperation;
    }

    public String getNodeTemplate() {
        return this.nodeTemplate;
    }

    public Parameter[] getOutput() {
        return this.output;
    }

    public Position getPosition() {
        return this.position;
    }

    public NodeTemplate getTemplate() {
        return this.template;
    }

    public String getType() {
        return this.type;
    }

    public String getInstanceType() {
        return this.instanceType;
    }

}


class Position {
    private int left;
    private int top;

    public int getLeft() {
        return this.left;
    }

    public int getTop() {
        return this.top;
    }
}
