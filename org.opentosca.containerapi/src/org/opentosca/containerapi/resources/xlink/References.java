package org.opentosca.containerapi.resources.xlink;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * Holds a list 'List<Reference>' and provides XML-representation.<br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Christian Endres - christian.endres@iaas.uni-stuttgart.de
 * 
 */
public class References {
	
	
	protected List<Reference> reference;
	
	
	public References() {
	}
	
	public List<Reference> getReference() {
		if (reference == null) {
			reference = new ArrayList<Reference>();
		}
		return reference;
	}
	
	public String getXMLString() {
		
		StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		xml.append("<References");
		xml.append(" ");
		xml.append(XLinkConstants.XMLNS + "=\"" + XLinkConstants.XLINK_NAMESPACE + "\"");
		xml.append(" ");
		xml.append(">");
		
		for (Reference ref : getReference()) {
			xml.append(ref.toXml());
		}
		
		xml.append("</References>");
		
		return xml.toString();
	}
	
	public String getJSONString() {
		
		JsonObject json = new JsonObject();
		JsonObject refs = new JsonObject();
		
		for (Reference ref : getReference()){
			refs.add("Reference", ref.toJson());
		}
		json.add("References", refs);
		
		return json.getAsString();
	}
}
