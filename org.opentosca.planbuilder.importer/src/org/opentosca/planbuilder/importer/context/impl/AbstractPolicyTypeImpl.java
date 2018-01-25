/**
 * 
 */
package org.opentosca.planbuilder.importer.context.impl;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyType;

/**
 * @author kalmankepes
 *
 */
public class AbstractPolicyTypeImpl extends AbstractPolicyType {

	private TPolicyType policyType;
	private DefinitionsImpl defs;

	public AbstractPolicyTypeImpl(TPolicyType element, DefinitionsImpl definitionsImpl) {
		this.policyType = element;
		this.defs = definitionsImpl;
	}

	@Override
	public String getName() {
		return this.policyType.getName();
	}

	@Override
	public String getPolicyLanguage() {
		return this.policyType.getPolicyLanguage();
	}

	@Override
	public String getTargetNamespace() {

		if (this.policyType.getTargetNamespace() != null) {
			return this.policyType.getTargetNamespace();
		} else {
			return this.defs.getTargetNamespace();
		}
	}

	@Override
	public QName getID() {
		String namespace;
		if ((this.getTargetNamespace() != null) && !this.getTargetNamespace().equals("")) {
			namespace = this.getTargetNamespace();
		} else {
			namespace = this.defs.getTargetNamespace();
		}
		return new QName(namespace, this.getName());
	}

}
