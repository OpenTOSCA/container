package org.opentosca.model.tosca.referencemapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.model.csar.id.CSARID;

/**
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author "Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de"
 * 
 */
public class ReferenceMap implements Map<CSARID, MapQNameNode> {
	
	private Map<CSARID, MapQNameNode> csarIDToMapQNameNode = new HashMap<CSARID, MapQNameNode>();
	
	@Override
	public void clear() {
		
		this.csarIDToMapQNameNode.clear();
	}
	
	@Override
	public boolean containsKey(Object key) {
		
		return this.csarIDToMapQNameNode.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		
		return this.csarIDToMapQNameNode.containsValue(value);
	}
	
	@Override
	public Set<java.util.Map.Entry<CSARID, MapQNameNode>> entrySet() {
		
		return this.csarIDToMapQNameNode.entrySet();
	}
	
	@Override
	public MapQNameNode get(Object key) {
		
		return this.csarIDToMapQNameNode.get(key);
	}
	
	@Override
	public boolean isEmpty() {
		
		return this.csarIDToMapQNameNode.isEmpty();
	}
	
	@Override
	public Set<CSARID> keySet() {
		
		return this.csarIDToMapQNameNode.keySet();
	}
	
	@Override
	public MapQNameNode put(CSARID key, MapQNameNode value) {
		
		MapQNameNode result = this.csarIDToMapQNameNode.put(key, value);
		return result;
	}
	
	@Override
	public void putAll(Map<? extends CSARID, ? extends MapQNameNode> m) {
		
		this.csarIDToMapQNameNode.putAll(m);
	}
	
	@Override
	public MapQNameNode remove(Object key) {
		
		MapQNameNode result = this.csarIDToMapQNameNode.remove(key);
		return result;
	}
	
	@Override
	public int size() {
		
		return this.csarIDToMapQNameNode.size();
	}
	
	@Override
	public Collection<MapQNameNode> values() {
		
		return this.csarIDToMapQNameNode.values();
	}
	
	public Map<CSARID, MapQNameNode> getCsarIDToMapQNameNode() {
		
		return this.csarIDToMapQNameNode;
	}
	
	public void setCsarIDToMapQNameNode(Map<CSARID, MapQNameNode> referenceMap) {
		
		this.csarIDToMapQNameNode = referenceMap;
	}
}