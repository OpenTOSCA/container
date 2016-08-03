package org.opentosca.model.tosca.referencemapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TBoundaryDefinitions.Policies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class stores the mapping for Consolidated Policies. A CSARID is mapped
 * to the amount of Templates (ServiceTemplate or NodeTemplate) which can
 * contain Policies and the actual Consolidated Policies.
 * 
 * 
 * @author endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class CsarIDToPolicies implements Map<CSARID, Map<QName, Policies>> {

    private final Logger LOG = LoggerFactory.getLogger(CsarIDToPolicies.class);

    private Map<CSARID, Map<QName, Policies>> csarIDToTemplateToPolicies = new HashMap<CSARID, Map<QName, Policies>>();

    /**
     * Puts the Consolidated Policies of a ServiceTemplate or NodeTemplate into
     * the storage.
     * 
     * @param csarID
     *            the CSARID
     * @param templateID
     *            the QName of a ServiceTemplate or NodeTemplate
     * @param policies
     *            the Policies object
     */
    public void put(CSARID csarID, QName templateID, Policies policies) {

	if (!csarIDToTemplateToPolicies.containsKey(csarID)) {
	    csarIDToTemplateToPolicies.put(csarID, new HashMap<QName, Policies>());
	}

	if (null != csarIDToTemplateToPolicies.get(csarID).get(templateID)) {
	    LOG.warn("There are Consolidated Policies stored already for the CSARID \"" + csarID + " and TemplateID \""
		+ templateID + "\". Thus do overwrite the Consolidated Policies.");
	}

	csarIDToTemplateToPolicies.get(csarID).put(templateID, policies);

    }

    /**
     * Returns the requested Consolidated Policies.
     * 
     * @param csarID
     *            The CSARID.
     * @param templateID
     *            The QName pointing to the template.
     * @return the Consolidated Policies or null of none are found.
     */
    public Policies get(CSARID csarID, QName templateID) {

	if (!csarIDToTemplateToPolicies.containsKey(csarID)) {
	    LOG.error("There are no informations stored for the CSARID \"" + csarID + "\".");
	} else if (!csarIDToTemplateToPolicies.get(csarID).containsKey(templateID)) {
	    LOG.error("There are no informations stored for the CSARID \"" + csarID + "\" and the TemplateID \""
		+ templateID + "\".");
	} else {
	    return csarIDToTemplateToPolicies.get(csarID).get(templateID);
	}

	return null;

    }

    public List<QName> getTemplateIDs(CSARID csarID) {

	List<QName> list = new ArrayList<>();

	if (csarIDToTemplateToPolicies.containsKey(csarID)) {
	    list.addAll(csarIDToTemplateToPolicies.get(csarID).keySet());
	}

	return list;
    }

    @Override
    public int size() {
	return csarIDToTemplateToPolicies.size();
    }

    @Override
    public boolean isEmpty() {
	return csarIDToTemplateToPolicies.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
	return csarIDToTemplateToPolicies.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return containsValue(value);
    }

    @Deprecated
    @Override
    public Map<QName, Policies> get(Object key) {
	return csarIDToTemplateToPolicies.get(key);
    }

    @Deprecated
    @Override
    public Map<QName, Policies> put(CSARID key, Map<QName, Policies> value) {
	return csarIDToTemplateToPolicies.put(key, value);
    }

    @Override
    public Map<QName, Policies> remove(Object key) {

	if (key instanceof CSARID) {
	    CSARID csarID = (CSARID) key;
	    if (csarIDToTemplateToPolicies.containsKey(csarID)) {
		return csarIDToTemplateToPolicies.remove(csarID);
	    }
	}

	return null;
    }

    @Deprecated
    @Override
    public void putAll(Map<? extends CSARID, ? extends Map<QName, Policies>> m) {
	csarIDToTemplateToPolicies.putAll(m);
    }

    @Override
    public void clear() {
	csarIDToTemplateToPolicies.clear();
    }

    @Override
    public Set<CSARID> keySet() {
	return csarIDToTemplateToPolicies.keySet();
    }

    @Override
    public Collection<Map<QName, Policies>> values() {
	return csarIDToTemplateToPolicies.values();
    }

    @Override
    public Set<java.util.Map.Entry<CSARID, Map<QName, Policies>>> entrySet() {
	return csarIDToTemplateToPolicies.entrySet();
    }

}
