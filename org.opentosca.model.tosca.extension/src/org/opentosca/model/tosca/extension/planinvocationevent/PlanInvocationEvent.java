package org.opentosca.model.tosca.extension.planinvocationevent;

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
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;

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
    @XmlAttribute(name = "isActive", required = true)
    protected boolean isActive;
    @XmlAttribute(name = "hasFailed", required = true)
    protected boolean hasFailed;

    public PlanInvocationEvent() {
    }

    public PlanInvocationEvent(String csarID, TPlanDTO dto, String
	correlationID, int csarInstanceID, String ifaceName,
	String opName, QName inputMessageID, QName outputMessageID, boolean
	active, boolean failed) {
	inputParameter = new ArrayList<TParameterDTO>();
	inputParameter.addAll(dto.getInputParameters().getInputParameter());
	outputParameter = new ArrayList<TParameterDTO>();
	outputParameter.addAll(dto.getOutputParameters().getOutputParameter());
	csarid = csarID;
	planType = dto.getPlanType();
	planCorrelationID = correlationID;
	planID = dto.getId();
	planName = dto.getName();
	this.csarInstanceID = csarInstanceID;
	interfaceName = ifaceName;
	operationName = opName;
	this.inputMessageID = inputMessageID;
	this.outputMessageID = outputMessageID;
	planLanguage = dto.getPlanLanguage();
	isActive = active;
	hasFailed = failed;
    }

    /**
     * Gets the value of the inputParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the inputParameter property.
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
     * Objects of the following type(s) are allowed in the list {@link Parameter
     * }
     * 
     * 
     */
    public List<TParameterDTO> getInputParameter() {
	if (inputParameter == null) {
	    inputParameter = new ArrayList<TParameterDTO>();
	}
	return inputParameter;
    }

    /**
     * Gets the value of the outputParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the outputParameter property.
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
     * Objects of the following type(s) are allowed in the list {@link Parameter
     * }
     * 
     * 
     */
    public List<TParameterDTO> getOutputParameter() {
	if (outputParameter == null) {
	    outputParameter = new ArrayList<TParameterDTO>();
	}
	return outputParameter;
    }

    /**
     * Gets the value of the csarid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCSARID() {
	return csarid;
    }

    /**
     * Sets the value of the csarid property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCSARID(String value) {
	csarid = value;
    }

    /**
     * Gets the value of the planType property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPlanType() {
	return planType;
    }

    /**
     * Sets the value of the planType property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPlanType(String value) {
	planType = value;
    }

    /**
     * Gets the value of the internalPlanID property.
     * 
     */
    public String getPlanCorrelationID() {
	return planCorrelationID;
    }

    /**
     * Sets the value of the internalPlanID property.
     * 
     */
    public void setPlanCorrelationID(String value) {
	planCorrelationID = value;
    }

    /**
     * Gets the value of the planID property.
     * 
     * @return possible object is {@link QName }
     * 
     */
    public QName getPlanID() {
	return planID;
    }

    /**
     * Sets the value of the planID property.
     * 
     * @param value
     *            allowed object is {@link QName }
     * 
     */
    public void setPlanID(QName value) {
	planID = value;
    }

    /**
     * Gets the value of the internalInstanceInternalID property.
     * 
     */
    public int getCSARInstanceID() {
	return csarInstanceID;
    }

    /**
     * Sets the value of the internalInstanceInternalID property.
     * 
     */
    public void setCSARInstanceID(int value) {
	csarInstanceID = value;
    }

    /**
     * Gets the value of the interfaceName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getInterfaceName() {
	return interfaceName;
    }

    /**
     * Sets the value of the interfaceName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setInterfaceName(String value) {
	interfaceName = value;
    }

    /**
     * Gets the value of the operationName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getOperationName() {
	return operationName;
    }

    /**
     * Sets the value of the operationName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setOperationName(String value) {
	operationName = value;
    }

    /**
     * Gets the value of the inputMessageID property.
     * 
     * @return possible object is {@link QName }
     * 
     */
    public QName getInputMessageID() {
	return inputMessageID;
    }

    /**
     * Sets the value of the inputMessageID property.
     * 
     * @param value
     *            allowed object is {@link QName }
     * 
     */
    public void setInputMessageID(QName value) {
	inputMessageID = value;
    }

    /**
     * Gets the value of the outputMessageID property.
     * 
     * @return possible object is {@link QName }
     * 
     */
    public QName getOutputMessageID() {
	return outputMessageID;
    }

    /**
     * Sets the value of the outputMessageID property.
     * 
     * @param value
     *            allowed object is {@link QName }
     * 
     */
    public void setOutputMessageID(QName value) {
	outputMessageID = value;
    }

    /**
     * Gets the value of the planLanguage property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPlanLanguage() {
	return planLanguage;
    }

    /**
     * Sets the value of the planLanguage property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPlanLanguage(String value) {
	planLanguage = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     */
    public boolean isIsActive() {
	return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     */
    public void setIsActive(boolean value) {
	isActive = value;
    }

    /**
     * Gets the value of the hasFailed property.
     * 
     */
    public boolean isHasFailed() {
	return hasFailed;
    }

    /**
     * Sets the value of the hasFailed property.
     * 
     */
    public void setHasFailed(boolean value) {
	hasFailed = value;
    }

    public String getPlanName() {
	return planName;
    }

    public void setPlanName(String planName) {
	this.planName = planName;
    }
}
