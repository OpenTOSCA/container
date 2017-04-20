package org.opentosca.core.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.tosca.model.TBoundaryDefinitions.Policies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class stores the mapping for Consolidated Policies. A CSARID is mapped
 * to the amount of Templates (ServiceTemplate or NodeTemplate) which can
 * contain Policies and the actual Consolidated Policies.
 *
 * @author endrescn@studi.informatik.uni-stuttgart.de
 */
public class CsarIDToPolicies implements Map<CSARID, Map<QName, Policies>> {

	private final Logger LOG = LoggerFactory.getLogger(CsarIDToPolicies.class);

	private final Map<CSARID, Map<QName, Policies>> csarIDToTemplateToPolicies = new HashMap<>();


	/**
	 * Puts the Consolidated Policies of a ServiceTemplate or NodeTemplate into
	 * the storage.
	 *
	 * @param csarID the CSARID
	 * @param templateID the QName of a ServiceTemplate or NodeTemplate
	 * @param policies the Policies object
	 */
	public void put(final CSARID csarID, final QName templateID, final Policies policies) {

		if (!this.csarIDToTemplateToPolicies.containsKey(csarID)) {
			this.csarIDToTemplateToPolicies.put(csarID, new HashMap<QName, Policies>());
		}

		if (null != this.csarIDToTemplateToPolicies.get(csarID).get(templateID)) {
			this.LOG.warn("There are Consolidated Policies stored already for the CSARID \"" + csarID + " and TemplateID \"" + templateID + "\". Thus do overwrite the Consolidated Policies.");
		}

		this.csarIDToTemplateToPolicies.get(csarID).put(templateID, policies);

	}

	/**
	 * Returns the requested Consolidated Policies.
	 *
	 * @param csarID The CSARID.
	 * @param templateID The QName pointing to the template.
	 * @return the Consolidated Policies or null of none are found.
	 */
	public Policies get(final CSARID csarID, final QName templateID) {

		if (!this.csarIDToTemplateToPolicies.containsKey(csarID)) {
			this.LOG.error("There are no informations stored for the CSARID \"" + csarID + "\".");
		} else if (!this.csarIDToTemplateToPolicies.get(csarID).containsKey(templateID)) {
			this.LOG.error("There are no informations stored for the CSARID \"" + csarID + "\" and the TemplateID \"" + templateID + "\".");
		} else {
			return this.csarIDToTemplateToPolicies.get(csarID).get(templateID);
		}

		return null;

	}

	public List<QName> getTemplateIDs(final CSARID csarID) {

		final List<QName> list = new ArrayList<>();

		if (this.csarIDToTemplateToPolicies.containsKey(csarID)) {
			list.addAll(this.csarIDToTemplateToPolicies.get(csarID).keySet());
		}

		return list;
	}

	@Override
	public int size() {
		return this.csarIDToTemplateToPolicies.size();
	}

	@Override
	public boolean isEmpty() {
		return this.csarIDToTemplateToPolicies.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return this.csarIDToTemplateToPolicies.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return this.containsValue(value);
	}

	@Deprecated
	@Override
	public Map<QName, Policies> get(final Object key) {
		return this.csarIDToTemplateToPolicies.get(key);
	}

	@Deprecated
	@Override
	public Map<QName, Policies> put(final CSARID key, final Map<QName, Policies> value) {
		return this.csarIDToTemplateToPolicies.put(key, value);
	}

	@Override
	public Map<QName, Policies> remove(final Object key) {

		if (key instanceof CSARID) {
			final CSARID csarID = (CSARID) key;
			if (this.csarIDToTemplateToPolicies.containsKey(csarID)) {
				return this.csarIDToTemplateToPolicies.remove(csarID);
			}
		}

		return null;
	}

	@Deprecated
	@Override
	public void putAll(final Map<? extends CSARID, ? extends Map<QName, Policies>> m) {
		this.csarIDToTemplateToPolicies.putAll(m);
	}

	@Override
	public void clear() {
		this.csarIDToTemplateToPolicies.clear();
	}

	@Override
	public Set<CSARID> keySet() {
		return this.csarIDToTemplateToPolicies.keySet();
	}

	@Override
	public Collection<Map<QName, Policies>> values() {
		return this.csarIDToTemplateToPolicies.values();
	}

	@Override
	public Set<java.util.Map.Entry<CSARID, Map<QName, Policies>>> entrySet() {
		return this.csarIDToTemplateToPolicies.entrySet();
	}

}
