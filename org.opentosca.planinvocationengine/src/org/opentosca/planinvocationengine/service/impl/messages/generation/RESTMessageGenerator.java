package org.opentosca.planinvocationengine.service.impl.messages.generation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTMessageGenerator {

    private final Logger LOG = LoggerFactory.getLogger(RESTMessageGenerator.class);
    private final String callbackAddress = "http://localhost:8090/callback";

    public Map<String, String> createRequest(CSARID csarID, QName planInputMessageID,
	List<TParameterDTO> inputParameter, String correlationID) {

	Map<String, String> map = new HashMap<String, String>();

	LOG.trace("Processing a list of {} parameters", inputParameter.size());
	for (TParameterDTO para : inputParameter) {
	    LOG.trace("Put in the parameter {} with value {}", para.getName(), para.getValue());

	    if (para.getType().equalsIgnoreCase("correlation")) {
		LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
		map.put(para.getName(), correlationID);
	    } else if (para.getType().equalsIgnoreCase("callbackaddress")) {
		LOG.debug("Found CallbackAddress Element! Put in CallbackAddress \"" + callbackAddress + "\".");
		map.put(para.getName(), callbackAddress);
	    } else if (para.getType().equalsIgnoreCase("csarName")) {
		LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
		map.put(para.getName(), csarID.toString());
	    } else if (para.getType().equalsIgnoreCase("containerApiAddress")) {
		LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \"" + Settings.CONTAINER_API
		    + "\".");
		map.put(para.getName(), Settings.CONTAINER_API);
	    } else {
		LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
		map.put(para.getName(), para.getValue());
	    }
	}

	return map;
    }

}
