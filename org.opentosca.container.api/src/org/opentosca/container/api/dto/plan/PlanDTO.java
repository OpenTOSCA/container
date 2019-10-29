package org.opentosca.container.api.dto.plan;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TBoolean;
import org.opentosca.container.core.tosca.model.TPlan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Plan")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDTO extends ResourceSupport {

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "planType")
    private String planType;

    @XmlAttribute(name = "planLanguage")
    private String planLanguage;

    @XmlElement(name = "InputParameter")
    @XmlElementWrapper(name = "InputParameters")
    private List<TParameter> inputParameters = Lists.newArrayList();

    @XmlElement(name = "OutputParameter")
    @XmlElementWrapper(name = "OutputParameters")
    private List<TParameter> outputParameters = Lists.newArrayList();

    @XmlElement(name = "PlanModelReference")
    private String planModelReference;

    @XmlElement(name = "PlanModelReference")
    private long calculatedWCET;

    @XmlElement(name = "PlanModelReference")
    private long timeAvailable;

    public PlanDTO() {

    }

    public PlanDTO(final TPlan plan) {
        this.id = plan.getId();
        this.name = plan.getName();
        this.planType = plan.getPlanType();
        this.planLanguage = plan.getPlanLanguage();
        this.inputParameters.addAll(plan.getInputParameters().getInputParameter().stream().map(p -> new TParameter(p))
                                        .collect(Collectors.toList()));
        final TParameter timeAvailableParam = new TParameter();
        timeAvailableParam.setName("AvailableTimeForExecution(Optional)");
        timeAvailableParam.setRequired(TBoolean.NO);
        timeAvailableParam.setType("String");
        this.inputParameters.add(timeAvailableParam);
        this.outputParameters.addAll(plan.getOutputParameters().getOutputParameter().stream()
                                         .map(p -> new TParameter(p)).collect(Collectors.toList()));
        this.planModelReference = plan.getPlanModelReference().getReference();
        this.calculatedWCET = plan.getCalculatedWCET();
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

    @ApiModelProperty(name = "plan_type")
    public String getPlanType() {
        return this.planType;
    }

    public void setPlanType(final String planType) {
        this.planType = planType;
    }

    @ApiModelProperty(name = "plan_language")
    public String getPlanLanguage() {
        return this.planLanguage;
    }

    public void setPlanLanguage(final String planLanguage) {
        this.planLanguage = planLanguage;
    }

    @ApiModelProperty(name = "input_parameters")
    public List<TParameter> getInputParameters() {
        return this.inputParameters;
    }

    public void setInputParameters(final List<TParameter> inputParameters) {
        this.inputParameters = inputParameters;
    }

    @ApiModelProperty(name = "output_parameters")
    public List<TParameter> getOutputParameters() {
        return this.outputParameters;
    }

    public void setOutputParameters(final List<TParameter> outputParameters) {
        this.outputParameters = outputParameters;
    }

    @ApiModelProperty(name = "plan_model_reference")
    public String getPlanModelReference() {
        return this.planModelReference;
    }

    public void setPlanModelReference(final String planModelReference) {
        this.planModelReference = planModelReference;
    }

    @ApiModelProperty(name = "calculated_wcet")
    public long getCalculatedWCET() {
        return this.calculatedWCET;
    }

    public void setCalculatedWCET(final long calculatedWCET) {
        this.calculatedWCET = calculatedWCET;
    }

    @ApiModelProperty(name = "time_available")
    public long getTimeAvailable() {
        return this.timeAvailable;
    }

    public void setTimeAvailable(final long timeAvailable) {
        this.timeAvailable = timeAvailable;
    }


    public static final class Converter {

        public static TPlanDTO convert(final PlanDTO object) {
            final TPlanDTO plan = new TPlanDTO();

            plan.setId(QName.valueOf(object.getId()));
            plan.setName(object.getName());
            plan.setPlanLanguage(object.getPlanLanguage());
            plan.setPlanType(object.getPlanType());
            plan.setCalculatedWCET(object.getCalculatedWCET());
            plan.setTimeAvailable(object.getTimeAvailable());

            final TPlanDTO.InputParameters inputParameters = new TPlanDTO.InputParameters();
            for (final TParameter param : object.getInputParameters()) {
                inputParameters.getInputParameter().add(new TParameterDTO(param));
            }
            final TParameter myParam = new TParameter();
            myParam.setName("TimeAvailableTest");
            myParam.setRequired(TBoolean.NO);
            myParam.setType("testType");
            inputParameters.getInputParameter().add(new TParameterDTO(myParam));
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
            plan.setCalculatedWCET(object.getCalculatedWCET());
            plan.setTimeAvailable(object.getTimeAvailable());

            final List<TParameter> inputParameters = object.getInputParameters().getInputParameter().stream().map(p -> {
                final TParameter parameter = new TParameter();
                parameter.setName(p.getName());
                parameter.setRequired(p.getRequired());
                parameter.setType(p.getType());
                parameter.setValue(p.getValue());
                return parameter;
            }).collect(Collectors.toList());
            plan.setInputParameters(inputParameters);

            final List<TParameter> outputParameters =
                object.getOutputParameters().getOutputParameter().stream().map(p -> {
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
