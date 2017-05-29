package org.opentosca.container.api.dto;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TPlan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

@XmlRootElement(name = "Plan")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDTO extends ResourceSupport {

	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String planType;
	
	@XmlAttribute
	private String planLanguage;
	
	@XmlElement(name = "InputParameter")
	@XmlElementWrapper(name = "InputParameters")
	private List<TParameter> inputParameters = Lists.newArrayList();

	@XmlElement(name = "OutputParameter")
	@XmlElementWrapper(name = "OutputParameters")
	private List<TParameter> outputParameters = Lists.newArrayList();

	@XmlElement(name = "PlanModelReference")
	private String planModelReference;


	public PlanDTO() {
		
	}

	public PlanDTO(final TPlan plan) {
		this.id = plan.getId();
		this.name = plan.getName();
		this.planType = plan.getPlanType();
		this.planLanguage = plan.getPlanLanguage();
		this.inputParameters.addAll(plan.getInputParameters().getInputParameter().stream().map(p -> new TParameter(p)).collect(Collectors.toList()));
		this.outputParameters.addAll(plan.getOutputParameters().getOutputParameter().stream().map(p -> new TParameter(p)).collect(Collectors.toList()));
		this.planModelReference = plan.getPlanModelReference().getReference();
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPlanType() {
		return this.planType;
	}

	public void setPlanType(final String planType) {
		this.planType = planType;
	}

	public String getPlanLanguage() {
		return this.planLanguage;
	}

	public void setPlanLanguage(final String planLanguage) {
		this.planLanguage = planLanguage;
	}

	public List<TParameter> getInputParameters() {
		return this.inputParameters;
	}

	public void setInputParameters(final List<TParameter> inputParameters) {
		this.inputParameters = inputParameters;
	}

	public List<TParameter> getOutputParameters() {
		return this.outputParameters;
	}

	public void setOutputParameters(final List<TParameter> outputParameters) {
		this.outputParameters = outputParameters;
	}
	
	public String getPlanModelReference() {
		return this.planModelReference;
	}
	
	public void setPlanModelReference(final String planModelReference) {
		this.planModelReference = planModelReference;
	}


	public static final class Converter {
		
		public static TPlanDTO convert(final PlanDTO object) {
			final TPlanDTO plan = new TPlanDTO();
			
			plan.setId(QName.valueOf(object.getId()));
			plan.setName(object.getName());
			plan.setPlanLanguage(object.getPlanLanguage());
			plan.setPlanType(object.getPlanType());

			final TPlanDTO.InputParameters inputParameters = new TPlanDTO.InputParameters();
			for (final TParameter param : object.getInputParameters()) {
				inputParameters.getInputParameter().add(new TParameterDTO(param));
			}
			plan.setInputParameters(inputParameters);

			final TPlanDTO.OutputParameters outputParameters = new TPlanDTO.OutputParameters();
			for (final TParameter param : object.getOutputParameters()) {
				outputParameters.getOutputParameter().add(new TParameterDTO(param));
			}
			plan.setOutputParameters(outputParameters);

			return plan;
		}
		
		public static PlanDTO convert(final TPlanDTO object) {
			final PlanDTO plan = new PlanDTO();
			
			plan.setId(object.getId().toString());
			plan.setName(object.getName());
			plan.setPlanLanguage(object.getPlanLanguage());
			plan.setPlanType(object.getPlanType());
			
			final List<TParameter> inputParameters = object.getInputParameters().getInputParameter().stream().map(p -> {
				final TParameter parameter = new TParameter();
				parameter.setName(p.getName());
				parameter.setRequired(p.getRequired());
				parameter.setType(p.getType());
				parameter.setValue(p.getValue());
				return parameter;
			}).collect(Collectors.toList());
			plan.setInputParameters(inputParameters);
			
			final List<TParameter> outputParameters = object.getOutputParameters().getOutputParameter().stream().map(p -> {
				final TParameter parameter = new TParameter();
				parameter.setName(p.getName());
				parameter.setRequired(p.getRequired());
				parameter.setType(p.getType());
				parameter.setValue(p.getValue());
				return parameter;
			}).collect(Collectors.toList());
			plan.setInputParameters(outputParameters);
			
			return plan;
		}
	}
}
