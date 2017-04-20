package org.opentosca.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;

/**
 * Maps a CSARID to a list of QNames of TOSCA Service Templates.
 */
public class CSARIDToServiceTemplateIDsMap implements Map<CSARID, List<QName>> {
	
	private Map<CSARID, List<QName>> serviceTemplatesMap = new HashMap<>();
	
	
	@Override
	public void clear() {
		this.serviceTemplatesMap.clear();
	}
	
	@Override
	public boolean containsKey(final Object key) {
		
		return this.serviceTemplatesMap.containsKey(key);
	}
	
	@Override
	public boolean containsValue(final Object value) {
		
		return this.serviceTemplatesMap.containsValue(value);
	}
	
	@Override
	public Set<java.util.Map.Entry<CSARID, List<QName>>> entrySet() {
		
		return this.serviceTemplatesMap.entrySet();
	}
	
	@Override
	public List<QName> get(final Object key) {
		
		return this.serviceTemplatesMap.get(key);
	}
	
	@Override
	public boolean isEmpty() {
		
		return this.serviceTemplatesMap.isEmpty();
	}
	
	@Override
	public Set<CSARID> keySet() {
		
		return this.serviceTemplatesMap.keySet();
	}
	
	@Override
	public List<QName> put(final CSARID key, final List<QName> value) {
		
		final List<QName> result = this.serviceTemplatesMap.put(key, value);
		return result;
	}
	
	@Override
	public void putAll(final Map<? extends CSARID, ? extends List<QName>> m) {
		
		this.serviceTemplatesMap.putAll(m);
		
	}
	
	@Override
	public List<QName> remove(final Object key) {
		
		final List<QName> result = this.serviceTemplatesMap.remove(key);
		return result;
	}
	
	@Override
	public int size() {
		
		return this.serviceTemplatesMap.size();
	}
	
	@Override
	public Collection<List<QName>> values() {
		
		return this.serviceTemplatesMap.values();
	}
	
	public Map<CSARID, List<QName>> getServiceTemplatesMap() {
		//
		return this.serviceTemplatesMap;
	}
	
	public void setServiceTemplatesMap(final Map<CSARID, List<QName>> documentMap) {
		
		this.serviceTemplatesMap = documentMap;
	}
}
