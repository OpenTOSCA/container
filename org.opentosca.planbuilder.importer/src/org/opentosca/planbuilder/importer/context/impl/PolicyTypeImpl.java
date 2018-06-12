/**
 *
 */
package org.opentosca.planbuilder.importer.context.impl;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;

/**
 * @author kalmankepes
 *
 */
public class PolicyTypeImpl extends AbstractPolicyType {

    private final TPolicyType policyType;
    private final DefinitionsImpl defs;

    public PolicyTypeImpl(final TPolicyType element, final DefinitionsImpl definitionsImpl) {
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
    public QName getId() {
        String namespace;
        if (this.getTargetNamespace() != null && !this.getTargetNamespace().equals("")) {
            namespace = this.getTargetNamespace();
        } else {
            namespace = this.defs.getTargetNamespace();
        }
        return new QName(namespace, this.getName());
    }

    @Override
    public AbstractProperties getProperties() {
        if (this.policyType.getAny() != null && !this.policyType.getAny().isEmpty()) {
            return new PropertiesImpl(this.policyType.getAny().get(0));
        } else {
            return null;
        }
    }
}
