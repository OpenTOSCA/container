package org.opentosca.container.api.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "PlanInstanceResources")
public class PlanInstanceListDTO extends ResourceSupport {

	@JsonProperty
	@XmlElement(name = "PlanInstance")
	@XmlElementWrapper(name = "PlanInstances")
	private final List<PlanInstanceDTO> planInstances = Lists.newArrayList();


	public void add(final PlanInstanceDTO... planInstances) {
		this.planInstances.addAll(Arrays.asList(planInstances));
	}

	public void add(final Collection<PlanInstanceDTO> planInstances) {
		this.planInstances.addAll(planInstances);
	}
}
