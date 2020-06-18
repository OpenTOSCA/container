package org.opentosca.container.core.tosca.extension;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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

    @XmlElement(name = "InputParameters")
    protected TPlanDTO.InputParameters inputParameters;
    @XmlElement(name = "OutputParameters")
    protected TPlanDTO.OutputParameters outputParameters;
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

        this.calculatedWCET = Long.valueOf(plan.getOtherAttributes().getOrDefault(new QName("http://opentosca.org", "WCET"), String.valueOf(0)));

        final org.eclipse.winery.model.tosca.TPlan.InputParameters serializedInputParams = plan.getInputParameters();
        if (null != serializedInputParams) {
            this.inputParameters = new InputParameters();
            for (final TParameter param : serializedInputParams.getInputParameter()) {
                this.inputParameters.getInputParameter().add(new TParameterDTO(param));
            }
        }

        final org.eclipse.winery.model.tosca.TPlan.OutputParameters serializedOutputParams = plan.getOutputParameters();
        if (null != serializedOutputParams) {
            this.outputParameters = new OutputParameters();
            for (final TParameter param : serializedOutputParams.getOutputParameter()) {
                this.outputParameters.getOutputParameter().add(new TParameterDTO(param));
            }
        }
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
     *
     * @return possible object is {@link TPlanDTO.InputParameters }
     */
    public TPlanDTO.@Nullable InputParameters getInputParameters() {
        if (null == this.inputParameters) {
            this.inputParameters = new TPlanDTO.InputParameters();
        }
        return this.inputParameters;
    }

    /**
     * Sets the value of the inputParameters property.
     *
     * @param value allowed object is {@link TPlanDTO.InputParameters }
     */
    public void setInputParameters(final TPlanDTO.InputParameters value) {
        this.inputParameters = value;
    }

    /**
     * Sets the value of the outputParameters property with the origin OutputParameters element.
     *
     * @param value allowed object is {@link TPlanDTO.OutputParameters }
     */
    public void setInputParameters(final TPlan.InputParameters value) {
        if (null != value) {
            this.inputParameters = new InputParameters();
            for (final TParameter param : value.getInputParameter()) {
                this.inputParameters.getInputParameter().add(new TParameterDTO(param));
            }
        }
    }

    /**
     * Gets the value of the outputParameters property.
     *
     * @return possible object is {@link TPlanDTO.OutputParameters }
     */
    public TPlanDTO.OutputParameters getOutputParameters() {
        if (null == this.outputParameters) {
            this.outputParameters = new TPlanDTO.OutputParameters();
        }
        return this.outputParameters;
    }

    /**
     * Sets the value of the outputParameters property.
     *
     * @param value allowed object is {@link TPlanDTO.OutputParameters }
     */
    public void setOutputParameters(final TPlanDTO.OutputParameters value) {
        this.outputParameters = value;
    }

    /**
     * Sets the value of the outputParameters property with the origin OutputParameters element.
     *
     * @param value allowed object is {@link TPlanDTO.OutputParameters }
     */
    public void setOutputParameters(final TPlan.OutputParameters value) {
        if (null != value) {
            this.outputParameters = new OutputParameters();
            for (final TParameter param : value.getOutputParameter()) {
                this.outputParameters.getOutputParameter().add(new TParameterDTO(param));
            }
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
        builder.append("Plan with ID \"" + this.id + "\", name \"" + this.name + "\", of type \"" + this.planType
            + "\", language \"" + this.planLanguage + "\" and input [");
        for (final TParameterDTO param : this.inputParameters.getInputParameter()) {
            builder.append("\"" + param.getName() + "\", ");
        }
        builder.append("]");
        for (final TParameterDTO param : this.outputParameters.getOutputParameter()) {
            builder.append("\"" + param.getName() + "\", ");
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
     *         &lt;element name="InputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"inputParameter"})
    public static class InputParameters {

        @XmlElement(name = "InputParameter", required = true)
        protected List<TParameterDTO> inputParameter;

        /**
         * Gets the value of the inputParameter property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
         * method for the inputParameter property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getInputParameter().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list {@link TParameter }
         */
        public List<TParameterDTO> getInputParameter() {
            if (this.inputParameter == null) {
                this.inputParameter = new ArrayList<>();
            }
            return this.inputParameter;
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
     *       &lt;sequence>
     *         &lt;element name="OutputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"outputParameter"})
    public static class OutputParameters {

        @XmlElement(name = "OutputParameter", required = true)
        protected List<TParameterDTO> outputParameter;

        /**
         * Gets the value of the outputParameter property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
         * method for the outputParameter property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getOutputParameter().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list {@link TParameter }
         */
        public List<TParameterDTO> getOutputParameter() {
            if (this.outputParameter == null) {
                this.outputParameter = new ArrayList<>();
            }
            return this.outputParameter;
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
