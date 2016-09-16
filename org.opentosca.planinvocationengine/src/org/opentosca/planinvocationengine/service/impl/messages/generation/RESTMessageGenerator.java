package org.opentosca.planinvocationengine.service.impl.messages.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.planinvocationengine.service.impl.ServiceHandler;
import org.opentosca.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class RESTMessageGenerator {
	
	
	private final Logger LOG = LoggerFactory.getLogger(RESTMessageGenerator.class);
	
	
	public Map<String, String> createRequest(CSARID csarID, QName planInputMessageID, List<TParameterDTO> inputParameter, String correlationID) {
		
		Map<String, String> map = new HashMap<String, String>();
		List<Document> docs = new ArrayList<Document>();
		
		List<QName> serviceTemplates = ServiceHandler.toscaEngineService.getServiceTemplatesInCSAR(csarID);
		for (QName serviceTemplate : serviceTemplates) {
			List<String> nodeTemplates = ServiceHandler.toscaEngineService.getNodeTemplatesOfServiceTemplate(csarID, serviceTemplate);
			
			for (String nodeTemplate : nodeTemplates) {
				Document doc = ServiceHandler.toscaEngineService.getPropertiesOfNodeTemplate(csarID, serviceTemplate, nodeTemplate);
				if (null != doc) {
					docs.add(doc);
					LOG.trace("Found property document: {}", ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(doc, false));
				}
			}
		}
		
		LOG.trace("Processing a list of {} parameters", inputParameter.size());
		for (TParameterDTO para : inputParameter) {
			LOG.trace("Put in the parameter {} with value \"{}\".", para.getName(), para.getValue());
			
			if (para.getType().equalsIgnoreCase("correlation")) {
				LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
				map.put(para.getName(), correlationID);
			} else if (para.getName().equalsIgnoreCase("csarName")) {
				LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
				map.put(para.getName(), csarID.toString());
			} else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
				LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \"" + Settings.CONTAINER_API + "\".");
				map.put(para.getName(), Settings.CONTAINER_API);
			} else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
				LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_INSTANCEDATA_API + "\".");
				map.put(para.getName(), Settings.CONTAINER_INSTANCEDATA_API);
			} else {
				if (para.getName() == null || para.getValue().equals("")) {
					LOG.debug("The parameter \"" + para.getName() + "\" has an empty value, thus search in the properties.");
					String value = "";
					for (Document doc : docs) {
						NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
						LOG.trace("Found {} nodes.", nodes.getLength());
						if (nodes.getLength() > 0) {
							value = nodes.item(0).getTextContent();
							LOG.debug("Found value {}", value);
							break;
						}
					}
					if (value.equals("")){
						LOG.debug("No value found.");
					}
					map.put(para.getName(), value);
				} else {
					LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
					map.put(para.getName(), para.getValue());
				}
			}
		}
		
		return map;
	}
	
}
