package org.opentosca.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * Generic Map of QName to Document. Intended for JPA but does not work.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
public class MapQNameDocument implements Map<QName, Document> {

	private Map<QName, Document> map = new HashMap<>();
	
	
	@Override
	public void clear() {

		this.map.clear();

	}

	@Override
	public boolean containsKey(final Object key) {

		return this.map.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {

		return this.map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<QName, Document>> entrySet() {

		return this.map.entrySet();
	}

	@Override
	public Document get(final Object key) {

		return this.map.get(key);
	}

	@Override
	public boolean isEmpty() {

		return this.map.isEmpty();
	}

	@Override
	public Set<QName> keySet() {

		return this.map.keySet();
	}

	@Override
	public Document put(final QName key, final Document value) {

		final Document result = this.map.put(key, value);
		return result;
	}

	@Override
	public void putAll(final Map<? extends QName, ? extends Document> m) {

		this.map.putAll(m);

	}

	@Override
	public Document remove(final Object key) {

		final Document result = this.map.remove(key);
		return result;
	}

	@Override
	public int size() {

		return this.map.size();
	}

	@Override
	public Collection<Document> values() {

		return this.map.values();
	}

	public Map<QName, Document> getMap() {

		return this.map;
	}

	public void setMap(final Map<QName, Document> map) {

		this.map = map;
	}
}
