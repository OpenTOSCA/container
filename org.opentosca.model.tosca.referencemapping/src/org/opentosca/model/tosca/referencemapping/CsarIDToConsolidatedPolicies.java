package org.opentosca.model.tosca.referencemapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This class stores the mapping for Consolidated Policies. A CSARID is mapped
 * to the amount of Templates (ServiceTemplate or NodeTemplate) which can
 * contain Policies and the actual Consolidated Policies.
 * 
 * 
 * @author endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class CsarIDToConsolidatedPolicies implements
		Map<CSARID, Map<QName, ConsolidatedPolicies>> {

	private final Logger LOG = LoggerFactory
			.getLogger(CsarIDToConsolidatedPolicies.class);

	private Map<CSARID, Map<QName, ConsolidatedPolicies>> csarIDToTemplateToConsolidatedPolicies = new HashMap<CSARID, Map<QName, ConsolidatedPolicies>>();

	/**
	 * Puts the Consolidated Policies of a ServiceTemplate or NodeTemplate into
	 * the storage.
	 * 
	 * @param csarID
	 *            the CSARID
	 * @param templateID
	 *            the QName of a ServiceTemplate or NodeTemplate
	 * @param policies
	 *            the ConsolidatedPolicies object
	 */
	public void put(CSARID csarID, QName templateID,
			ConsolidatedPolicies policies) {

		if (!csarIDToTemplateToConsolidatedPolicies.containsKey(csarID)) {
			csarIDToTemplateToConsolidatedPolicies.put(csarID,
					new HashMap<QName, ConsolidatedPolicies>());
		}

		if (null != csarIDToTemplateToConsolidatedPolicies.get(csarID).get(
				templateID)) {
			LOG.warn("There are Consolidated Policies stored already for the CSARID \""
					+ csarID
					+ " and TemplateID \""
					+ templateID
					+ "\". Thus do overwrite the Consolidated Policies.");
		}

		csarIDToTemplateToConsolidatedPolicies.get(csarID).put(templateID,
				policies);

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
	public ConsolidatedPolicies get(CSARID csarID, QName templateID) {

		if (!csarIDToTemplateToConsolidatedPolicies.containsKey(csarID)) {
			LOG.error("There are no informations stored for the CSARID \""
					+ csarID + "\".");
		} else if (!csarIDToTemplateToConsolidatedPolicies.get(csarID)
				.containsKey(templateID)) {
			LOG.error("There are no informations stored for the CSARID \""
					+ csarID + "\" and the TemplateID \"" + templateID + "\".");
		} else {
			return csarIDToTemplateToConsolidatedPolicies.get(csarID).get(
					templateID);
		}

		return null;

	}

	public List<QName> getTemplateIDs(CSARID csarID) {

		List<QName> list = new ArrayList<>();

		if (csarIDToTemplateToConsolidatedPolicies.containsKey(csarID)) {
			list.addAll(csarIDToTemplateToConsolidatedPolicies.get(csarID)
					.keySet());
		}

		return list;
	}

	@Override
	public int size() {
		return csarIDToTemplateToConsolidatedPolicies.size();
	}

	@Override
	public boolean isEmpty() {
		return csarIDToTemplateToConsolidatedPolicies.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return csarIDToTemplateToConsolidatedPolicies.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return containsValue(value);
	}

	@Deprecated
	@Override
	public Map<QName, ConsolidatedPolicies> get(Object key) {
		return csarIDToTemplateToConsolidatedPolicies.get(key);
	}

	@Deprecated
	@Override
	public Map<QName, ConsolidatedPolicies> put(CSARID key,
			Map<QName, ConsolidatedPolicies> value) {
		return csarIDToTemplateToConsolidatedPolicies.put(key, value);
	}

	@Override
	public Map<QName, ConsolidatedPolicies> remove(Object key) {
		
		if (key instanceof CSARID){
			CSARID csarID = (CSARID) key;
			if (csarIDToTemplateToConsolidatedPolicies.containsKey(csarID)) {
				return csarIDToTemplateToConsolidatedPolicies.remove(csarID);
			}
		}
		
		return null;
	}

	@Deprecated
	@Override
	public void putAll(
			Map<? extends CSARID, ? extends Map<QName, ConsolidatedPolicies>> m) {
		csarIDToTemplateToConsolidatedPolicies.putAll(m);
	}

	@Override
	public void clear() {
		csarIDToTemplateToConsolidatedPolicies.clear();
	}

	@Override
	public Set<CSARID> keySet() {
		return csarIDToTemplateToConsolidatedPolicies.keySet();
	}

	@Override
	public Collection<Map<QName, ConsolidatedPolicies>> values() {
		return csarIDToTemplateToConsolidatedPolicies.values();
	}

	@Override
	public Set<java.util.Map.Entry<CSARID, Map<QName, ConsolidatedPolicies>>> entrySet() {
		return csarIDToTemplateToConsolidatedPolicies.entrySet();
	}

}
