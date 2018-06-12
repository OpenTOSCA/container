package org.opentosca.container.core.next.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = DeploymentTest.TABLE_NAME)
public class DeploymentTest extends PersistenceObject {

    public static final String TABLE_NAME = "DEPLOYMENT_TEST";

    private static final long serialVersionUID = 4929775787088737689L;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeploymentTestState state = DeploymentTestState.UNKNOWN;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "deploymentTest", cascade = {CascadeType.ALL})
    private List<DeploymentTestResult> deploymentTestResults = Lists.newArrayList();

    @ManyToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
    @JsonIgnore
    private ServiceTemplateInstance serviceTemplateInstance;


    public DeploymentTest() {
        this.timestamp = new Date();
    }


    @Override
    @JsonIgnore
    public Long getId() {
        return super.getId();
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public DeploymentTestState getState() {
        return this.state;
    }

    public void setState(final DeploymentTestState state) {
        this.state = state;
    }

    public List<DeploymentTestResult> getDeploymentTestResults() {
        return this.deploymentTestResults;
    }

    public void setDeploymentTestResults(final List<DeploymentTestResult> deploymentTestResults) {
        this.deploymentTestResults = deploymentTestResults;
    }

    public void addDeploymentTestResult(final DeploymentTestResult deploymentTestResult) {
        this.deploymentTestResults.add(deploymentTestResult);
        if (deploymentTestResult.geDeploymentTest() != this) {
            deploymentTestResult.setDeploymentTest(this);
        }
    }

    public ServiceTemplateInstance getServiceTemplateInstance() {
        return this.serviceTemplateInstance;
    }

    public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
        this.serviceTemplateInstance = serviceTemplateInstance;
        if (!serviceTemplateInstance.getDeploymentTests().contains(this)) {
            serviceTemplateInstance.getDeploymentTests().add(this);
        }
    }

    public Map<String, Object> getStatistics() {
        final Map<String, Object> stats = Maps.newHashMap();
        stats.put("total", this.deploymentTestResults.size());
        stats.put("success", countJobsByState(DeploymentTestState.SUCCESS));
        stats.put("failed", countJobsByState(DeploymentTestState.FAILED));
        stats.put("unknown", countJobsByState(DeploymentTestState.UNKNOWN));
        return stats;
    }

    private long countJobsByState(final DeploymentTestState state) {
        return this.deploymentTestResults.stream().filter(r -> r.getState().equals(state)).count();
    }
}
