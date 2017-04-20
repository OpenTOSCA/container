package org.opentosca.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.tosca.extension.PlanTypes;
import org.opentosca.core.tosca.model.TPlan;

/**
 * Persistence of the implemented data structure.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
public class CsarIDToPlanTypeToPlanNameToPlan implements Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> {

	private Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> csarIDToPlanTypeToIntegerToPlanMap = new HashMap<>();


	@Override
	public void clear() {

		this.csarIDToPlanTypeToIntegerToPlanMap.clear();
	}

	@Override
	public boolean containsKey(final Object arg0) {

		return this.csarIDToPlanTypeToIntegerToPlanMap.containsKey(arg0);
	}

	@Override
	public boolean containsValue(final Object arg0) {

		return this.csarIDToPlanTypeToIntegerToPlanMap.containsValue(arg0);
	}

	@Override
	public Set<java.util.Map.Entry<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>>> entrySet() {

		return this.csarIDToPlanTypeToIntegerToPlanMap.entrySet();
	}

	@Override
	public Map<PlanTypes, LinkedHashMap<QName, TPlan>> get(final Object arg0) {

		return this.csarIDToPlanTypeToIntegerToPlanMap.get(arg0);
	}

	@Override
	public boolean isEmpty() {

		return this.csarIDToPlanTypeToIntegerToPlanMap.isEmpty();
	}

	@Override
	public Set<CSARID> keySet() {

		return this.csarIDToPlanTypeToIntegerToPlanMap.keySet();
	}

	@Override
	public Map<PlanTypes, LinkedHashMap<QName, TPlan>> put(final CSARID arg0, final Map<PlanTypes, LinkedHashMap<QName, TPlan>> arg1) {

		final Map<PlanTypes, LinkedHashMap<QName, TPlan>> result = this.csarIDToPlanTypeToIntegerToPlanMap.put(arg0, arg1);
		return result;
	}

	@Override
	public void putAll(final Map<? extends CSARID, ? extends Map<PlanTypes, LinkedHashMap<QName, TPlan>>> arg0) {

		this.csarIDToPlanTypeToIntegerToPlanMap.putAll(arg0);
	}

	@Override
	public Map<PlanTypes, LinkedHashMap<QName, TPlan>> remove(final Object arg0) {

		final Map<PlanTypes, LinkedHashMap<QName, TPlan>> result = this.csarIDToPlanTypeToIntegerToPlanMap.remove(arg0);
		return result;
	}

	@Override
	public int size() {

		return this.csarIDToPlanTypeToIntegerToPlanMap.size();
	}

	@Override
	public Collection<Map<PlanTypes, LinkedHashMap<QName, TPlan>>> values() {

		return this.csarIDToPlanTypeToIntegerToPlanMap.values();
	}

	public Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> getMap() {
		return this.csarIDToPlanTypeToIntegerToPlanMap;
	}

	public void setMap(final Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> map) {

		this.csarIDToPlanTypeToIntegerToPlanMap = map;
	}
}
