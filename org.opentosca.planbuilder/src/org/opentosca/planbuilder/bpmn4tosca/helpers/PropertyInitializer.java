package org.opentosca.planbuilder.bpmn4tosca.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PropertyInitializer {

	private Map<String, Map<String, String>> propertiesMap = new HashMap<>();

	public PropertyInitializer(AbstractNodeTemplate template) {
		this.initializeProperties(template);
	}

	public Map<String, String> getPropertiesByTemplateId(String id) {
		return this.propertiesMap.getOrDefault(id, new HashMap<>());
	}

	public String getValueByTemplateIdAndPropertyName(String id, String propertyName) {
		return this.getPropertiesByTemplateId(id).getOrDefault(propertyName, "");
	}

	public void addNodeProperties(final AbstractNodeTemplate template) {
		this.initializeProperties(template);
	}

	public String findFirst(String key) {
		return getEntries().filter(value -> value.getKey().equals(key)).map(value -> value.getValue()).findFirst()
				.orElse(null);
	}

	public String findAny(List<String> keys) {
		return this.getEntries().filter(entry -> keys.contains(entry.getKey())).map(entry -> entry.getValue()).findAny()
				.orElse(null);
	}

	private Stream<Map.Entry<String, String>> getEntries() {
		return this.propertiesMap.values().stream().flatMap(entry -> entry.entrySet().stream());
	}

	private void initializeProperties(AbstractNodeTemplate template) {
		AbstractProperties properties = template.getProperties();
		this.populatePropertiesMap(properties, template);
	}

	private void populatePropertiesMap(final AbstractProperties properties, final AbstractNodeTemplate nodeTemplate) {
		final Element propertyElement = properties.getDOMElement();
		this.propertiesMap.putIfAbsent(nodeTemplate.getId(), new HashMap<>());
		for (int i = 0; i < propertyElement.getChildNodes().getLength(); i++) {

			if (propertyElement.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
				continue;
			}

			String propName = propertyElement.getChildNodes().item(i).getLocalName();
			String nodeId = nodeTemplate.getId();
			String value = "";

			for (int j = 0; j < propertyElement.getChildNodes().item(i).getChildNodes().getLength(); j++) {
				if (propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
					value += propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeValue();
				}
			}

			this.propertiesMap.get(nodeId).put(propName, value);
		}
	}

}
