package org.opentosca.planbuilder.export;

import java.util.HashMap;
import java.util.Map;

public class VinothekKnownParameters {
	
	private Map<String, String> knownParameterToXmlMapping = new HashMap<String, String>();
	
	
	public VinothekKnownParameters() {
		this.knownParameterToXmlMapping.put("csarName", "<org:csarName>%CSAR-NAME%</org:csarName>");
		this.knownParameterToXmlMapping.put("containerApi", "<org:containerApi>%CONTAINER-API%</org:containerApi>");
		this.knownParameterToXmlMapping.put("callbackUrl", "<org:callbackUrl>%CALLBACK-URL%</org:callbackUrl>");
		this.knownParameterToXmlMapping.put("CorrelationID", "<org:CorrelationID>%CORRELATION-ID%</org:CorrelationID>");
		// e.g. <ba:csarEntrypoint>http://localhost:1337/containerapi/CSARs/PhpMoodleAppTemplate.csar</ba:csarEntrypoint>
		this.knownParameterToXmlMapping.put("csarEntrypoint", "<org:csarEntrypoint>%CSARENTRYPOINT-URL%</org:csarEntrypoint>");
		// e.g. <ba:planCallbackAddress_invoker>http://169.254.178.214:9763/services/InvokerService/</ba:planCallbackAddress_invoker>
		this.knownParameterToXmlMapping.put("planCallbackAddress_invoker", "<org:planCallbackAddress_invoker>%PLANCALLBACKINVOKER-URL%</org:planCallbackAddress_invoker>");		
		// e.g. <org:instanceDataAPIUrl>http://localhost:1337/containerapi/instancedata</org:instanceDataAPIUrl>
		this.knownParameterToXmlMapping.put("instanceDataAPIUrl", "<org:instanceDataAPIUrl>%INSTANCEDATA-URL%</org:instanceDataAPIUrl>");
		
	}
	
	protected String createXmlElement(String parameterLocalName) {
		if (this.knownParameterToXmlMapping.containsKey(parameterLocalName)) {
			return this.knownParameterToXmlMapping.get(parameterLocalName);
		} else {
			return "<org:" + parameterLocalName + ">Please fill in</org:" + parameterLocalName + ">";
		}
	}
	
}
