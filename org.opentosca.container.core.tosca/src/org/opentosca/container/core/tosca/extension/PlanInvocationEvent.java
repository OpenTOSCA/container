package org.opentosca.container.core.tosca.extension;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;

import org.omg.Dynamic.Parameter;

@XmlRootElement(name = "PlanInvocationEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanInvocationEvent {

    @XmlElement(name = "InputParameter", namespace = "http://www.opentosca.org/ConsolidatedTOSCA", required = true)
    protected List<TParameterDTO> inputParameter;
    @XmlElement(name = "OutputParameter", namespace = "http://www.opentosca.org/ConsolidatedTOSCA", required = true)
    protected List<TParameterDTO> outputParameter;
    @XmlAttribute(name = "CSARID", required = true)
    protected String csarid;
    @XmlAttribute(name = "PlanType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planType;
    @XmlAttribute(name = "PlanCorrelationID", required = true)
    protected String planCorrelationID;
    @XmlAttribute(name = "PlanID", required = true)
    protected QName planID;
    @XmlAttribute(name = "PlanName", required = true)
    protected String planName;
    @XmlAttribute(name = "CSARInstanceID", required = true)
    protected int csarInstanceID;
    @XmlAttribute(name = "InterfaceName", required = true)
    protected String interfaceName;
    @XmlAttribute(name = "OperationName", required = true)
    protected String operationName;
    @XmlAttribute(name = "InputMessageID", required = true)
    protected QName inputMessageID;
    @XmlAttribute(name = "OutputMessageID", required = true)
    protected QName outputMessageID;
    @XmlAttribute(name = "PlanLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planLanguage;

    @XmlAttribute(name = "CalculatedWCET")
    @XmlSchemaType(name = "anyURI")
    protected long calculatedWCET;

    @XmlAttribute(name = "isActive", required = true)
    protected boolean isActive;
    @XmlAttribute(name = "hasFailed", required = true)
    protected boolean hasFailed;

    public PlanInvocationEvent() {}

    public PlanInvocationEvent(final String csarID, final TPlanDTO dto, final String correlationID,
                               final int csarInstanceID, final String ifaceName, final String opName,
                               final QName inputMessageID, final QName outputMessageID, final boolean active,
                               final boolean failed) {
        this.inputParameter = new ArrayList<>();
        this.inputParameter.addAll(dto.getInputParameters().getInputParameter());
        this.outputParameter = new ArrayList<>();
        this.outputParameter.addAll(dto.getOutputParameters().getOutputParameter());
        this.csarid = csarID;
        this.planType = dto.getPlanType();
        this.planCorrelationID = correlationID;
        this.planID = dto.getId();
        this.planName = dto.getName();
        this.csarInstanceID = csarInstanceID;
        this.interfaceName = ifaceName;
        this.operationName = opName;
        this.inputMessageID = inputMessageID;
        this.outputMessageID = outputMessageID;
        this.planLanguage = dto.getPlanLanguage();
        this.calculatedWCET = dto.getCalculatedWCET();
        this.isActive = active;
        this.hasFailed = failed;
    }

    /**
     * Gets the value of the inputParameter property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the inputParameter property.
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
     * Objects of the following type(s) are allowed in the list {@link Parameter }
     *
     *
     */
    public List<TParameterDTO> getInputParameter() {
        if (this.inputParameter == null) {
            this.inputParameter = new ArrayList<>();
        }
        return this.inputParameter;
    }

    /**
     * Gets the value of the outputParameter property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the outputParameter property.
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
     * Objects of the following type(s) are allowed in the list {@link Parameter }
     *
     *
     */
    public List<TParameterDTO> getOutputParameter() {
        if (this.outputParameter == null) {
            this.outputParameter = new ArrayList<>();
        }
        return this.outputParameter;
    }

    /**
     * Gets the value of the csarid property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCSARID() {
        return this.csarid;
    }

    /**
     * Sets the value of the csarid property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCSARID(final String value) {
        this.csarid = value;
    }

    /**
     * Gets the value of the planType property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getPlanType() {
        return this.planType;
    }

    /**
     * Sets the value of the planType property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPlanType(final String value) {
        this.planType = value;
    }

    /**
     * Gets the value of the internalPlanID property.
     *
     */
    public String getPlanCorrelationID() {
        return this.planCorrelationID;
    }

    /**
     * Sets the value of the internalPlanID property.
     *
     */
    public void setPlanCorrelationID(final String value) {
        this.planCorrelationID = value;
    }

    /**
     * Gets the value of the planID property.
     *
     * @return possible object is {@link QName }
     *
     */
    public QName getPlanID() {
        return this.planID;
    }

    /**
     * Sets the value of the planID property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setPlanID(final QName value) {
        this.planID = value;
    }

    /**
     * Gets the value of the internalInstanceInternalID property.
     *
     */
    public int getCSARInstanceID() {
        return this.csarInstanceID;
    }

    /**
     * Sets the value of the internalInstanceInternalID property.
     *
     */
    public void setCSARInstanceID(final int value) {
        this.csarInstanceID = value;
    }

    /**
     * Gets the value of the interfaceName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getInterfaceName() {
        return this.interfaceName;
    }

    /**
     * Sets the value of the interfaceName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setInterfaceName(final String value) {
        this.interfaceName = value;
    }

    /**
     * Gets the value of the operationName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOperationName() {
        return this.operationName;
    }

    /**
     * Sets the value of the operationName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOperationName(final String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the inputMessageID property.
     *
     * @return possible object is {@link QName }
     *
     */
    public QName getInputMessageID() {
        return this.inputMessageID;
    }

    /**
     * Sets the value of the inputMessageID property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setInputMessageID(final QName value) {
        this.inputMessageID = value;
    }

    /**
     * Gets the value of the outputMessageID property.
     *
     * @return possible object is {@link QName }
     *
     */
    public QName getOutputMessageID() {
        return this.outputMessageID;
    }

    /**
     * Sets the value of the outputMessageID property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setOutputMessageID(final QName value) {
        this.outputMessageID = value;
    }

    /**
     * Gets the value of the planLanguage property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getPlanLanguage() {
        return this.planLanguage;
    }


    public long getCalculatedWCET() {
        return this.calculatedWCET;
    }

    public void setCalculatedWCET(final long calculatedWCET) {
        this.calculatedWCET = calculatedWCET;
    }

    /**
     * Sets the value of the planLanguage property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPlanLanguage(final String value) {
        this.planLanguage = value;
    }

    /**
     * Gets the value of the isActive property.
     *
     */
    public boolean isIsActive() {
        return this.isActive;
    }

    /**
     * Sets the value of the isActive property.
     *
     */
    public void setIsActive(final boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the hasFailed property.
     *
     */
    public boolean isHasFailed() {
        return this.hasFailed;
    }

    /**
     * Sets the value of the hasFailed property.
     *
     */
    public void setHasFailed(final boolean value) {
        this.hasFailed = value;
    }

    public String getPlanName() {
        return this.planName;
    }

    public void setPlanName(final String planName) {
        this.planName = planName;
    }
}
