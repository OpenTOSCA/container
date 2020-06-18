package org.opentosca.container.core.next.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = DeploymentTestResult.TABLE_NAME)
public class DeploymentTestResult extends PersistenceObject {

    public static final String TABLE_NAME = "DEPLOYMENT_TEST_RESULT";

    private static final long serialVersionUID = 7456157949253267729L;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeploymentTestState state = DeploymentTestState.UNKNOWN;

    private String output;

    @ManyToOne
    @JoinColumn(name = "DEPLOYMENT_TEST_ID")
    @JsonIgnore
    private DeploymentTest deploymentTest;

    @ManyToOne
    @JoinColumn(name = "NODE_TEMPLATE_INSTANCE_ID")
    @JsonIgnoreProperties( {"state", "service_template_instance", "incoming_relations", "outgoing_relations",
        "properties"})
    private NodeTemplateInstance nodeTemplateInstance;

    public DeploymentTestResult() {
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return super.getId();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(final Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(final Date end) {
        this.end = end;
    }

    public DeploymentTestState getState() {
        return this.state;
    }

    public void setState(final DeploymentTestState state) {
        this.state = state;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(final String output) {
        this.output = output;
    }

    public DeploymentTest geDeploymentTest() {
        return this.deploymentTest;
    }

    public void setDeploymentTest(final DeploymentTest deploymentTest) {
        this.deploymentTest = deploymentTest;
        if (!deploymentTest.getDeploymentTestResults().contains(this)) {
            deploymentTest.getDeploymentTestResults().add(this);
        }
    }

    public NodeTemplateInstance getNodeTemplateInstance() {
        return this.nodeTemplateInstance;
    }

    public void setNodeTemplateInstance(final NodeTemplateInstance nodeTemplateInstance) {
        this.nodeTemplateInstance = nodeTemplateInstance;
        if (!nodeTemplateInstance.getDeploymentTestResults().contains(this)) {
            nodeTemplateInstance.getDeploymentTestResults().add(this);
        }
    }

    public void start() {
        this.start = new Date();
    }

    public void append(final String output) {
        if (this.output == null) {
            this.output = output;
        } else {
            this.output = new StringBuilder(this.output).append(System.getProperty("line.separator")).append(output).toString();
        }
    }

    public void failed() {
        this.end = new Date();
        if (this.start == null) {
            this.start = this.end;
        }
        this.state = DeploymentTestState.FAILED;
    }

    public void success() {
        this.end = new Date();
        if (this.start == null) {
            this.start = this.end;
        }
        this.state = DeploymentTestState.SUCCESS;
    }

    @Override
    public String toString() {
        return "[duration=" + (this.end.getTime() - this.start.getTime()) + ", state=" + this.state + ", output="
            + this.output + "]";
    }
}
