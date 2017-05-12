package org.opentosca.container.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.opentosca.container.core.tosca.model.TParameter;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TPlan.PlanModelReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Decorator class for {@link TPlan} to provide a proper JSON representation.
 */
@XmlRootElement(name = "Plan")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDTO extends ResourceSupport {

	@XmlTransient
	private TPlan plan;


	protected PlanDTO() {
		
	}

	public PlanDTO(final TPlan plan) {
		this.plan = plan;
	}

	@XmlAttribute(name = "id")
	public String getId() {
		return this.plan.getId();
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return this.plan.getName();
	}

	@XmlAttribute(name = "planType")
	public String getPlanType() {
		return this.plan.getPlanType();
	}
	
	@XmlAttribute(name = "planLanguage")
	public String getPlanLanguage() {
		return this.plan.getPlanLanguage();
	}
	
	@XmlElement(name = "InputParameter")
	@XmlElementWrapper(name = "InputParameters")
	public List<TParameter> getInputParameters() {
		return this.plan.getInputParameters().getInputParameter();
	}
	
	@XmlElement(name = "OutputParameter")
	@XmlElementWrapper(name = "OutputParameters")
	public List<TParameter> getOutputParameters() {
		return this.plan.getOutputParameters().getOutputParameter();
	}
	
	@XmlTransient
	public String getPlanModelReference() {
		return this.plan.getPlanModelReference().getReference();
	}
	
	@JsonIgnore
	@XmlElement(name = "PlanModelReference")
	public PlanModelReference getPlanModelReferenceForXml() {
		return this.plan.getPlanModelReference();
	}
}
