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

@XmlRootElement(name = "PlanInstnaceEventResources")
public class PlanInstanceEventListDTO extends ResourceSupport {
	@JsonProperty
	@XmlElement(name = "PlanInstnaceEvent")
	@XmlElementWrapper(name = "PlanInstnaceEvents")
	private final List<PlanInstanceEventDTO> planInstanceEvents = Lists.newArrayList();

	public PlanInstanceEventListDTO() {
		
	}
	
	public PlanInstanceEventListDTO(Collection<PlanInstanceEventDTO> events) {
		this.planInstanceEvents.addAll(events);
	}

	public void add(final PlanInstanceEventDTO... planInstanceEvents) {
		this.planInstanceEvents.addAll(Arrays.asList(planInstanceEvents));
	}

}
