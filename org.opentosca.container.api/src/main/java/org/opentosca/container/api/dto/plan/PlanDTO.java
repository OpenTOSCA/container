package org.opentosca.container.api.dto.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.extension.TParameterDTO;
import org.opentosca.container.core.extension.TPlanDTO;

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
    private List<TParameter> inputParameters = new ArrayList<>();

    @XmlElement(name = "OutputParameter")
    @XmlElementWrapper(name = "OutputParameters")
    private List<TParameter> outputParameters = new ArrayList<>();

    @XmlElement(name = "PlanModelReference")
    private String planModelReference;

    @XmlElement(name = "CalculatedWCET")
    private long calculatedWCET;

    public PlanDTO() {

    }

    public PlanDTO(final TPlan plan) {
        this.id = plan.getId();
        this.name = plan.getName();
        this.planType = plan.getPlanType();
        this.planLanguage = plan.getPlanLanguage();
        if (plan.getInputParameters() != null) {
            this.inputParameters.addAll(
                plan.getInputParameters().stream()
                    .map(TParameter::new)
                    .collect(Collectors.toList())
            );
        }

        if (plan.getOutputParameters() != null) {
            this.outputParameters.addAll(
                plan.getOutputParameters().stream()
                    .map(TParameter::new)
                    .collect(Collectors.toList())
            );
        }

        this.planModelReference = plan.getPlanModelReference().getReference();
        this.calculatedWCET = Long.parseLong(
            plan.getOtherAttributes()
                .getOrDefault(new QName("http://opentosca.org", "WCET"), "0")
        );
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

    public static final class Converter {

        public static TPlanDTO convert(final PlanDTO object) {
            final TPlanDTO plan = new TPlanDTO();

            plan.setId(QName.valueOf(object.getId()));
            plan.setName(object.getName());
            plan.setPlanLanguage(object.getPlanLanguage());
            plan.setPlanType(object.getPlanType());
            plan.setCalculatedWCET(object.getCalculatedWCET());

            plan.setInputParameters(
                object.getInputParameters().stream()
                    .map(TParameterDTO::new)
                    .collect(Collectors.toList())
            );
            plan.setOutputParameters(
                object.getOutputParameters().stream()
                    .map(TParameterDTO::new)
                    .collect(Collectors.toList())
            );

            return plan;
        }
    }
}
