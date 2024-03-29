//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.07.16 at 01:55:00 PM CEST
//

package org.opentosca.bus.management.api.soaphttp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for ReceiveNotifyPartners complex type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "receiveNotifyPartners", propOrder = {"planCorrelation", "planChorCorrelation", "csarID", "serviceTemplateIDNamespaceURI",
    "serviceTemplateIDLocalPart", "messageID", "params", "doc"})
public class ReceiveNotifyPartners {

    @XmlElement(name = "PlanCorrelationID")
    protected String planCorrelation;
    @XmlElement(name = "PlanChorCorrelationID")
    protected String planChorCorrelation;
    @XmlElement(name = "CsarID", required = true)
    protected String csarID;
    @XmlElement(name = "ServiceTemplateIDNamespaceURI", required = true)
    protected String serviceTemplateIDNamespaceURI;
    @XmlElement(name = "ServiceTemplateIDLocalPart", required = true)
    protected String serviceTemplateIDLocalPart;
    @XmlElement(name = "MessageID", required = true)
    protected String messageID;
    @XmlElement(name = "Params")
    protected ParamsMap params;
    @XmlElement(name = "Doc")
    protected Doc doc;

    /**
     * Gets the value of the PlanCorrelationID property.
     *
     * @return possible object is {@link String }
     */
    public String getPlanCorrelationID() {
        return this.planCorrelation;
    }

    /**
     * Sets the value of the PlanCorrelationID property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPlanCorrelationID(final String value) {
        this.planCorrelation = value;
    }

    /**
     * Gets the value of the csarID property.
     *
     * @return possible object is {@link String }
     */
    public String getCsarID() {
        return this.csarID;
    }

    /**
     * Sets the value of the csarID property.
     *
     * @param value allowed object is {@link String }
     */
    public void setCsarID(final String value) {
        this.csarID = value;
    }

    /**
     * Gets the value of the serviceTemplateIDNamespaceURI property.
     *
     * @return possible object is {@link String }
     */
    public String getServiceTemplateIDNamespaceURI() {
        return this.serviceTemplateIDNamespaceURI;
    }

    /**
     * Sets the value of the serviceTemplateIDNamespaceURI property.
     *
     * @param value allowed object is {@link String }
     */
    public void setServiceTemplateIDNamespaceURI(final String value) {
        this.serviceTemplateIDNamespaceURI = value;
    }

    /**
     * Gets the value of the serviceTemplateIDLocalPart property.
     *
     * @return possible object is {@link String }
     */
    public String getServiceTemplateIDLocalPart() {
        return this.serviceTemplateIDLocalPart;
    }

    /**
     * Sets the value of the serviceTemplateIDLocalPart property.
     *
     * @param value allowed object is {@link String }
     */
    public void setServiceTemplateIDLocalPart(final String value) {
        this.serviceTemplateIDLocalPart = value;
    }

    /**
     * Gets the value of the messageID property.
     *
     * @return possible object is {@link String }
     */
    public String getMessageID() {
        return this.messageID;
    }

    /**
     * Sets the value of the messageID property.
     *
     * @param value allowed object is {@link String }
     */
    public void setMessageID(final String value) {
        this.messageID = value;
    }

    /**
     * Gets the value of the params property.
     *
     * @return possible object is {@link ParamsMap }
     */
    public ParamsMap getParams() {
        return this.params;
    }

    /**
     * Sets the value of the params property.
     *
     * @param value allowed object is {@link ParamsMap }
     */
    public void setParams(final ParamsMap value) {
        this.params = value;
    }

    /**
     * Gets the value of the doc property.
     *
     * @return possible object is {@link Doc }
     */
    public Doc getDoc() {
        return this.doc;
    }

    /**
     * Sets the value of the doc property.
     *
     * @param value allowed object is {@link Doc }
     */
    public void setDoc(final Doc value) {
        this.doc = value;
    }

    /**
     * @return the planChorCorrelation
     */
    public String getPlanChorCorrelation() {
        return planChorCorrelation;
    }

    /**
     * @param planChorCorrelation the planChorCorrelation to set
     */
    public void setPlanChorCorrelation(String planChorCorrelation) {
        this.planChorCorrelation = planChorCorrelation;
    }
}
