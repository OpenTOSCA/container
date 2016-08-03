package org.opentosca.model.tosca.referencemapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;

/**
 * Persistence of the implemented data structure.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CsarIDToPlanTypeToPlanNameToPlan implements Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> {

    private Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> csarIDToPlanTypeToIntegerToPlanMap = new HashMap<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>>();

    @Override
    public void clear() {

	csarIDToPlanTypeToIntegerToPlanMap.clear();
    }

    @Override
    public boolean containsKey(Object arg0) {

	return csarIDToPlanTypeToIntegerToPlanMap.containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {

	return csarIDToPlanTypeToIntegerToPlanMap.containsValue(arg0);
    }

    @Override
    public Set<java.util.Map.Entry<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>>> entrySet() {

	return csarIDToPlanTypeToIntegerToPlanMap.entrySet();
    }

    @Override
    public Map<PlanTypes, LinkedHashMap<QName, TPlan>> get(Object arg0) {

	return csarIDToPlanTypeToIntegerToPlanMap.get(arg0);
    }

    @Override
    public boolean isEmpty() {

	return csarIDToPlanTypeToIntegerToPlanMap.isEmpty();
    }

    @Override
    public Set<CSARID> keySet() {

	return csarIDToPlanTypeToIntegerToPlanMap.keySet();
    }

    @Override
    public Map<PlanTypes, LinkedHashMap<QName, TPlan>> put(CSARID arg0,
	Map<PlanTypes, LinkedHashMap<QName, TPlan>> arg1) {

	Map<PlanTypes, LinkedHashMap<QName, TPlan>> result = csarIDToPlanTypeToIntegerToPlanMap.put(arg0, arg1);
	return result;
    }

    @Override
    public void putAll(Map<? extends CSARID, ? extends Map<PlanTypes, LinkedHashMap<QName, TPlan>>> arg0) {

	csarIDToPlanTypeToIntegerToPlanMap.putAll(arg0);
    }

    @Override
    public Map<PlanTypes, LinkedHashMap<QName, TPlan>> remove(Object arg0) {

	Map<PlanTypes, LinkedHashMap<QName, TPlan>> result = csarIDToPlanTypeToIntegerToPlanMap.remove(arg0);
	return result;
    }

    @Override
    public int size() {

	return csarIDToPlanTypeToIntegerToPlanMap.size();
    }

    @Override
    public Collection<Map<PlanTypes, LinkedHashMap<QName, TPlan>>> values() {

	return csarIDToPlanTypeToIntegerToPlanMap.values();
    }

    public Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> getMap() {
	return csarIDToPlanTypeToIntegerToPlanMap;
    }

    public void setMap(Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> map) {

	csarIDToPlanTypeToIntegerToPlanMap = map;
    }
}
