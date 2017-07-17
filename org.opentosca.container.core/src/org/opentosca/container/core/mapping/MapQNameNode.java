package org.opentosca.container.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

/**
 * Generic Map of QName to Node. Intended for JPA but does not work.
 */
public class MapQNameNode implements Map<QName, Node> {

	private Map<QName, Node> qnameNode = new HashMap<>();


	@Override
	public void clear() {
		this.qnameNode.clear();

	}

	@Override
	public boolean containsKey(final Object key) {

		return this.qnameNode.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {

		return this.qnameNode.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<QName, Node>> entrySet() {

		return this.qnameNode.entrySet();
	}

	@Override
	public Node get(final Object key) {

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
	public Node put(final QName key, final Node value) {

		final Node result = this.qnameNode.put(key, value);
		return result;
	}

	@Override
	public void putAll(final Map<? extends QName, ? extends Node> m) {

		this.qnameNode.putAll(m);

	}

	@Override
	public Node remove(final Object key) {

		final Node result = this.qnameNode.remove(key);
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

	public void setQnameNode(final Map<QName, Node> map) {

		this.qnameNode = map;
	}
}
