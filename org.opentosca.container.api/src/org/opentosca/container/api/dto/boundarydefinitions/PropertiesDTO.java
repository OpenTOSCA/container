package org.opentosca.container.api.dto.boundarydefinitions;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.tosca.model.TPropertyMapping;

import com.google.common.collect.Lists;

@XmlRootElement(name = "Properties")
public class PropertiesDTO extends ResourceSupport {

	private String xmlFragment;
	
	private List<TPropertyMapping> propertyMappings = Lists.newArrayList();


	@XmlElement(name = "XmlFragment")
	public String getXmlFragment() {
		return this.xmlFragment;
	}
	
	public void setXmlFragment(final String xmlFragment) {
		this.xmlFragment = xmlFragment;
	}
	
	@XmlElement(name = "PropertyMapping")
	@XmlElementWrapper(name = "PropertyMappings")
	public List<TPropertyMapping> getPropertyMappings() {
		return this.propertyMappings;
	}
	
	public void setPropertyMappings(final List<TPropertyMapping> propertyMappings) {
		this.propertyMappings = propertyMappings;
	}
}
