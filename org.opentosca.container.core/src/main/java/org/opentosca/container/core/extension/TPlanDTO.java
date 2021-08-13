package org.opentosca.container.core.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;

import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;

@XmlRootElement(name = "Plan")
@XmlAccessorType(XmlAccessType.FIELD)
public class TPlanDTO {

    @XmlElementWrapper(name = "InputParameters")
    @XmlElement(name = "InputParameter", required = true)
    protected List<TParameterDTO> inputParameters;
    @XmlElementWrapper(name = "OutputParameters")
    @XmlElement(name = "OutputParameter", required = true)
    protected List<TParameterDTO> outputParameters;
    @XmlAttribute(name = "id", required = true)
    // @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    // @XmlID
    @XmlSchemaType(name = "ID")
    protected QName id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "planType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planType;
    @XmlAttribute(name = "planLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planLanguage;

    @XmlAttribute(name = "calculatedWCET")
    @XmlSchemaType(name = "anyURI")
    protected long calculatedWCET;

    public TPlanDTO(final TPlan plan, final String namespace) {
        this.id = new QName(namespace, plan.getId());
        this.name = plan.getName();
        this.planType = plan.getPlanType();
        this.planLanguage = plan.getPlanLanguage();

        this.calculatedWCET = Long.parseLong(
            plan.getOtherAttributes()
                .getOrDefault(new QName("http://opentosca.org", "WCET"), String.valueOf(0))
        );

        this.setInputParametersFromOriginalModel(plan.getInputParameters());
        this.setInputParametersFromOriginalModel(plan.getOutputParameters());
    }

    public TPlanDTO() {
    }

    public QName getId() {
        return this.id;
    }

    public void setId(final QName id) {
        this.id = id;
    }

    /**
     * Gets the value of the inputParameters property.
     */
    public List<TParameterDTO>  getInputParameters() {
        if (null == this.inputParameters) {
            this.inputParameters = new ArrayList<>();
        }
        return this.inputParameters;
    }

    /**
     * Sets the value of the inputParameters property.
     */
    public void setInputParameters(final List<TParameterDTO> value) {
        this.inputParameters = value;
    }

    /**
     * Sets the value of the inputParameters property with the origin OutputParameters element.
     */
    public void setInputParametersFromOriginalModel(final List<TParameter> serializedInputParams) {
        if (null != serializedInputParams) {
            this.inputParameters = serializedInputParams.stream()
                .map(TParameterDTO::new)
                .collect(Collectors.toList());
        }
    }

    /**
     * Gets the value of the outputParameters property.
     */
    public List<TParameterDTO> getOutputParameters() {
        if (null == this.outputParameters) {
            this.outputParameters = new ArrayList<>();
        }
        return this.outputParameters;
    }

    /**
     * Sets the value of the outputParameters property.
     */
    public void setOutputParameters(final List<TParameterDTO>  value) {
        this.outputParameters = value;
    }

    /**
     * Sets the value of the outputParameters property with the origin OutputParameters element.
     */
    public void setOutputParametersFromOriginalModel(final List<TParameter>  serializedOutputParams) {
        if (null != serializedOutputParams) {
            this.outputParameters = serializedOutputParams.stream()
                .map(TParameterDTO::new)
                .collect(Collectors.toList());
        }
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the planType property.
     *
     * @return possible object is {@link String }
     */
    public String getPlanType() {
        return this.planType;
    }

    /**
     * Sets the value of the planType property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPlanType(final String value) {
        this.planType = value;
    }

    /**
     * Gets the value of the planLanguage property.
     *
     * @return possible object is {@link String }
     */
    public String getPlanLanguage() {
        return this.planLanguage;
    }

    /**
     * Sets the value of the planLanguage property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPlanLanguage(final String value) {
        this.planLanguage = value;
    }

    public long getCalculatedWCET() {
        return this.calculatedWCET;
    }

    public void setCalculatedWCET(final long calculatedWCET) {
        this.calculatedWCET = calculatedWCET;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Plan with ID \"").append(this.id)
            .append("\", name \"").append(this.name)
            .append("\", of type \"").append(this.planType)
            .append("\", language \"").append(this.planLanguage)
            .append("\" and input [");
        for (final TParameterDTO param : this.inputParameters) {
            builder.append("\"").append(param.getName()).append("\", ");
        }
        builder.append("]");
        for (final TParameterDTO param : this.outputParameters) {
            builder.append("\"").append(param.getName()).append("\", ");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     *
     * <p>
     * The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any processContents='lax' namespace='##other'/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"any"})
    public static class PlanModel {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         *
         * @return possible object is {@link Object } {@link Element }
         */
        public Object getAny() {
            return this.any;
        }

        /**
         * Sets the value of the any property.
         *
         * @param value allowed object is {@link Object } {@link Element }
         */
        public void setAny(final Object value) {
            this.any = value;
        }
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     *
     * <p>
     * The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="reference" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PlanModelReference {

        @XmlAttribute(name = "reference", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String reference;

        /**
         * Gets the value of the reference property.
         *
         * @return possible object is {@link String }
         */
        public String getReference() {
            return this.reference;
        }

        /**
         * Sets the value of the reference property.
         *
         * @param value allowed object is {@link String }
         */
        public void setReference(final String value) {
            this.reference = value;
        }
    }
}
