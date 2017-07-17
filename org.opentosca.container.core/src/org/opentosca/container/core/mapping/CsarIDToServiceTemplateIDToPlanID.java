package org.opentosca.container.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Maps a CSARID to a Map of Service Template QNames which maps to a list of
 * TOSCA Plan QNames.
 */
public class CsarIDToServiceTemplateIDToPlanID implements Map<CSARID, Map<QName, List<QName>>> {
	
	private Map<CSARID, Map<QName, List<QName>>> csarIDToServiceTemplateIDToPlanIDMap = new HashMap<>();
	
	
	@Override
	public void clear() {
		
		this.csarIDToServiceTemplateIDToPlanIDMap.clear();
		
	}
	
	@Override
	public boolean containsKey(final Object arg0) {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.containsKey(arg0);
	}
	
	@Override
	public boolean containsValue(final Object arg0) {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.containsValue(arg0);
	}
	
	@Override
	public Set<java.util.Map.Entry<CSARID, Map<QName, List<QName>>>> entrySet() {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.entrySet();
	}
	
	@Override
	public Map<QName, List<QName>> get(final Object arg0) {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.get(arg0);
	}
	
	@Override
	public boolean isEmpty() {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.isEmpty();
	}
	
	@Override
	public Set<CSARID> keySet() {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.keySet();
	}
	
	@Override
	public Map<QName, List<QName>> put(final CSARID arg0, final Map<QName, List<QName>> arg1) {
		
		final Map<QName, List<QName>> result = this.csarIDToServiceTemplateIDToPlanIDMap.put(arg0, arg1);
		
		return result;
	}
	
	@Override
	public void putAll(final java.util.Map<? extends CSARID, ? extends java.util.Map<QName, List<QName>>> arg0) {
		
		this.csarIDToServiceTemplateIDToPlanIDMap.putAll(arg0);
		
	}
	
	@Override
	public Map<QName, List<QName>> remove(final Object arg0) {
		
		final Map<QName, List<QName>> result = this.csarIDToServiceTemplateIDToPlanIDMap.remove(arg0);
		
		return result;
	}
	
	@Override
	public int size() {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.size();
	}
	
	@Override
	public Collection<Map<QName, List<QName>>> values() {
		
		return this.csarIDToServiceTemplateIDToPlanIDMap.values();
	}
	
	public Map<CSARID, Map<QName, List<QName>>> getMap() {
		return this.csarIDToServiceTemplateIDToPlanIDMap;
	}
	
	public void setMap(final Map<CSARID, Map<QName, List<QName>>> map) {
		
		this.csarIDToServiceTemplateIDToPlanIDMap = map;
	}
	
}
