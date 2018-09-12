package org.opentosca.container.api.dto.plan;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "PlanInstanceResources")
public class PlanInstanceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "PlanInstance")
    @XmlElementWrapper(name = "PlanInstances")
    private final List<PlanInstanceDTO> planInstances = Lists.newArrayList();


    @ApiModelProperty(name = "plan_instances")
    public List<PlanInstanceDTO> getPlanInstances() {
        return this.planInstances;
    }

    public void add(final PlanInstanceDTO... planInstances) {
        this.planInstances.addAll(Arrays.asList(planInstances));
    }

    public void add(final Collection<PlanInstanceDTO> planInstances) {
        this.planInstances.addAll(planInstances);
    }
}
