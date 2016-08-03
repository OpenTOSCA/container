package org.opentosca.model.tosca.referencemapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.consolidatedtosca.PublicPlanTypes;

/**
 * Persistence of the implemented data structure.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CsarIDToPlanTypeToIntegerToPublicPlan implements
		Map<CSARID, Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>> {

	private Map<CSARID, Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>> csarIDToPlanTypeToIntegerToPublicPlanMap = new HashMap<CSARID, Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>>();

	@Override
	public void clear() {

		this.csarIDToPlanTypeToIntegerToPublicPlanMap.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap
				.containsValue(arg0);
	}

	@Override
	public Set<java.util.Map.Entry<CSARID, Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>>> entrySet() {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.entrySet();
	}

	@Override
	public Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> get(
			Object arg0) {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.get(arg0);
	}

	@Override
	public boolean isEmpty() {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.isEmpty();
	}

	@Override
	public Set<CSARID> keySet() {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.keySet();
	}

	@Override
	public Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> put(
			CSARID arg0,
			Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> arg1) {

		Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> result = this.csarIDToPlanTypeToIntegerToPublicPlanMap
				.put(arg0, arg1);
		return result;
	}

	@Override
	public void putAll(
			Map<? extends CSARID, ? extends Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>> arg0) {

		this.csarIDToPlanTypeToIntegerToPublicPlanMap.putAll(arg0);
	}

	@Override
	public Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> remove(
			Object arg0) {

		Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> result = this.csarIDToPlanTypeToIntegerToPublicPlanMap
				.remove(arg0);
		return result;
	}

	@Override
	public int size() {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.size();
	}

	@Override
	public Collection<Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>> values() {

		return this.csarIDToPlanTypeToIntegerToPublicPlanMap.values();
	}

	public Map<CSARID, Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>> getMap() {
		return this.csarIDToPlanTypeToIntegerToPublicPlanMap;
	}

	public void setMap(
			Map<CSARID, Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>> map) {

		this.csarIDToPlanTypeToIntegerToPublicPlanMap = map;
	}
}
