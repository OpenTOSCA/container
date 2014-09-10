package org.opentosca.model.tosca.referencemapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TDefinitions;

/**
 * Maps a CSARID to a list of TOSCA Definitions.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARIDToDefinitionsMap implements Map<CSARID, List<TDefinitions>> {

	private Map<CSARID, List<TDefinitions>> definitionsMap = new HashMap<CSARID, List<TDefinitions>>();

	@Override
	public void clear() {
		this.definitionsMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {

		return this.definitionsMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {

		return this.definitionsMap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<CSARID, List<TDefinitions>>> entrySet() {

		return this.definitionsMap.entrySet();
	}

	@Override
	public List<TDefinitions> get(Object key) {

		return this.definitionsMap.get(key);
	}

	@Override
	public boolean isEmpty() {

		return this.definitionsMap.isEmpty();
	}

	@Override
	public Set<CSARID> keySet() {

		return this.definitionsMap.keySet();
	}

	@Override
	public List<TDefinitions> put(CSARID key, List<TDefinitions> value) {

		List<TDefinitions> result = this.definitionsMap.put(key, value);
		return result;
	}

	@Override
	public void putAll(Map<? extends CSARID, ? extends List<TDefinitions>> m) {

		this.definitionsMap.putAll(m);

	}

	@Override
	public List<TDefinitions> remove(Object key) {

		List<TDefinitions> result = this.definitionsMap.remove(key);
		return result;
	}

	@Override
	public int size() {

		return this.definitionsMap.size();
	}

	@Override
	public Collection<List<TDefinitions>> values() {

		return this.definitionsMap.values();
	}

	public Map<CSARID, List<TDefinitions>> getDefinitionsMap() {

		return this.definitionsMap;
	}

	public void setDefinitionsMap(Map<CSARID, List<TDefinitions>> documentMap) {

		this.definitionsMap = documentMap;
	}

}
