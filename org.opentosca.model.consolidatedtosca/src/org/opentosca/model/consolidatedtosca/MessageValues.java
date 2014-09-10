package org.opentosca.model.consolidatedtosca;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageValues {
	
	private Map<String, String> mapWithValues;
	
	
	public MessageValues() {
		this.mapWithValues = new LinkedHashMap<String, String>();
	}
	
	public boolean addValue(String name, String value) {
		
		if (this.mapWithValues.containsKey(name)) {
			return false;
		}
		
		this.mapWithValues.put(name, value);
		return true;
		
	}
	
	public Map<String, String> getParameterWithValues() {
		return this.mapWithValues;
	}
}
