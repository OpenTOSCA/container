/**
 *
 */
package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;

/**
 * @author kalmankepes
 *
 */
public class AbstractPolicyTypeImpl extends AbstractPolicyType {

    private final TPolicyType policyType;
    private final DefinitionsImpl defs;

    public AbstractPolicyTypeImpl(final TPolicyType element, final DefinitionsImpl definitionsImpl) {
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
        if (getTargetNamespace() != null && !getTargetNamespace().equals("")) {
            namespace = getTargetNamespace();
        } else {
            namespace = this.defs.getTargetNamespace();
        }
        return new QName(namespace, getName());
    }

    @Override
    public AbstractProperties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }
}
