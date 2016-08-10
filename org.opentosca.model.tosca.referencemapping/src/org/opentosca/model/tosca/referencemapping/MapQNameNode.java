package org.opentosca.model.tosca.referencemapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;

/**
 * Generic Map of QName to Node. Intended for JPA but does not work.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class MapQNameNode implements Map<QName, Node> {
	
	private Map<QName, Node> qnameNode = new HashMap<QName, Node>();
	
	
	@Override
	public void clear() {
		this.qnameNode.clear();
		
	}
	
	@Override
	public boolean containsKey(Object key) {
		
		return this.qnameNode.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		
		return this.qnameNode.containsValue(value);
	}
	
	@Override
	public Set<java.util.Map.Entry<QName, Node>> entrySet() {
		
		return this.qnameNode.entrySet();
	}
	
	@Override
	public Node get(Object key) {
		
		return this.qnameNode.get(key);
	}
	
	@Override
	public boolean isEmpty() {
		
		return this.qnameNode.isEmpty();
	}
	
	@Override
	public Set<QName> keySet() {
		
		return this.qnameNode.keySet();
	}
	
	@Override
	public Node put(QName key, Node value) {
		
		Node result = this.qnameNode.put(key, value);
		return result;
	}
	
	@Override
	public void putAll(Map<? extends QName, ? extends Node> m) {
		
		this.qnameNode.putAll(m);
		
	}
	
	@Override
	public Node remove(Object key) {
		
		Node result = this.qnameNode.remove(key);
		return result;
	}
	
	@Override
	public int size() {
		
		return this.qnameNode.size();
	}
	
	@Override
	public Collection<Node> values() {
		
		return this.qnameNode.values();
	}
	
	public Map<QName, Node> getQnameNode() {
		
		return this.qnameNode;
	}
	
	public void setQnameNode(Map<QName, Node> map) {
		
		this.qnameNode = map;
	}
}
