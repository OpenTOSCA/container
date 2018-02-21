//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2013.07.10 at 12:45:26 PM CEST
//
// TOSCA version: TOSCA-v1.0-cs02.xsd
//

package org.opentosca.container.core.tosca.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * <p>
 * Java class for tEntityTemplate complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tEntityTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' namespace='##other'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PropertyConstraints" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityTemplate", propOrder = {"properties", "propertyConstraints"})
@XmlSeeAlso({TArtifactTemplate.class, TPolicyTemplate.class, TNodeTemplate.class, TCapability.class,
             TRelationshipTemplate.class, TRequirement.class})
public abstract class TEntityTemplate extends TExtensibleElements {

    @XmlElement(name = "Properties")
    protected TEntityTemplate.Properties properties;
    @XmlElement(name = "PropertyConstraints")
    protected TEntityTemplate.PropertyConstraints propertyConstraints;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "type", required = true)
    protected QName type;


    /**
     * Gets the value of the properties property.
     *
     * @return possible object is {@link TEntityTemplate.Properties }
     *
     */
    public TEntityTemplate.Properties getProperties() {
        return this.properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value allowed object is {@link TEntityTemplate.Properties }
     *
     */
    public void setProperties(final TEntityTemplate.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the propertyConstraints property.
     *
     * @return possible object is {@link TEntityTemplate.PropertyConstraints }
     *
     */
    public TEntityTemplate.PropertyConstraints getPropertyConstraints() {
        return this.propertyConstraints;
    }

    /**
     * Sets the value of the propertyConstraints property.
     *
     * @param value allowed object is {@link TEntityTemplate.PropertyConstraints }
     *
     */
    public void setPropertyConstraints(final TEntityTemplate.PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link QName }
     *
     */
    public QName getType() {
        return this.type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setType(final QName value) {
        this.type = value;
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
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"any"})
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;


        /**
         * Gets the value of the any property.
         *
         * @return possible object is {@link Object } {@link Element }
         *
         */
        public Object getAny() {
            return this.any;
        }

        /**
         * Sets the value of the any property.
         *
         * @param value allowed object is {@link Object } {@link Element }
         *
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
     *       &lt;sequence>
     *         &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"propertyConstraint"})
    public static class PropertyConstraints {

        @XmlElement(name = "PropertyConstraint", required = true)
        protected List<TPropertyConstraint> propertyConstraint;


        /**
         * Gets the value of the propertyConstraint property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a snapshot. Therefore any
         * modification you make to the returned list will be present inside the JAXB object. This is why
         * there is not a <CODE>set</CODE> method for the propertyConstraint property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getPropertyConstraint().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list {@link TPropertyConstraint }
         *
         *
         */
        public List<TPropertyConstraint> getPropertyConstraint() {
            if (this.propertyConstraint == null) {
                this.propertyConstraint = new ArrayList<>();
            }
            return this.propertyConstraint;
        }

    }

}
