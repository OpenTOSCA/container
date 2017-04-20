package org.opentosca.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.model.csar.id.CSARID;
import org.w3c.dom.Document;

/**
 * Maps a CSARID to a list of DOM Documents of parsed WSDLs.
 */
public class CsarIDToWSDLDocuments implements Map<CSARID, List<Document>> {
	
	private Map<CSARID, List<Document>> csarIDToWSDLDocumentsMap = new HashMap<>();
	
	
	@Override
	public void clear() {
		
		this.csarIDToWSDLDocumentsMap.clear();
	}
	
	@Override
	public boolean containsKey(final Object arg0) {
		
		return this.csarIDToWSDLDocumentsMap.containsKey(arg0);
	}
	
	@Override
	public boolean containsValue(final Object arg0) {
		
		return this.csarIDToWSDLDocumentsMap.containsValue(arg0);
	}
	
	@Override
	public Set<java.util.Map.Entry<CSARID, List<Document>>> entrySet() {
		
		return this.csarIDToWSDLDocumentsMap.entrySet();
	}
	
	@Override
	public List<Document> get(final Object arg0) {
		
		return this.csarIDToWSDLDocumentsMap.get(arg0);
	}
	
	@Override
	public boolean isEmpty() {
		
		return this.csarIDToWSDLDocumentsMap.isEmpty();
	}
	
	@Override
	public Set<CSARID> keySet() {
		
		return this.csarIDToWSDLDocumentsMap.keySet();
	}
	
	@Override
	public List<Document> put(final CSARID arg0, final List<Document> arg1) {
		
		final List<Document> result = this.csarIDToWSDLDocumentsMap.put(arg0, arg1);
		return result;
	}
	
	@Override
	public void putAll(final java.util.Map<? extends CSARID, ? extends java.util.List<Document>> arg0) {
		
		this.csarIDToWSDLDocumentsMap.putAll(arg0);
	}
	
	@Override
	public List<Document> remove(final Object arg0) {
		
		final List<Document> result = this.csarIDToWSDLDocumentsMap.remove(arg0);
		return result;
	}
	
	@Override
	public int size() {
		
		return this.csarIDToWSDLDocumentsMap.size();
	}
	
	@Override
	public Collection<List<Document>> values() {
		
		return this.csarIDToWSDLDocumentsMap.values();
	}
	
	public Map<CSARID, List<Document>> getMap() {
		
		return this.csarIDToWSDLDocumentsMap;
	}
	
	public void setMap(final Map<CSARID, List<Document>> map) {
		
		this.csarIDToWSDLDocumentsMap = map;
	}
	
}
