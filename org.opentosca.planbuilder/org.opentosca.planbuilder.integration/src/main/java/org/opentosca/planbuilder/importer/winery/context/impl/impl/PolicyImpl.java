package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;

import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyImpl extends AbstractPolicy {

    private final static Logger LOG = LoggerFactory.getLogger(PolicyImpl.class);

    private final org.eclipse.winery.model.tosca.TPolicy policy;
    private final DefinitionsImpl defs;

    public PolicyImpl(final org.eclipse.winery.model.tosca.TPolicy policy, final DefinitionsImpl definitions) {
        this.policy = policy;
        this.defs = definitions;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractPolicy#getName()
     */
    @Override
    public String getName() {
        return this.policy.getName();
    }

    @Override
    public TPolicyType getType() {
        if (this.policy == null) {
            LOG.debug("Internal policy is null");
        }
        if (this.policy.getPolicyType() == null) {
            LOG.debug("Internal policyType is null");
        }
        for (final TPolicyType policyType : this.defs.getAllPolicyTypes()) {
            if (policyType.equals(this.policy.getPolicyType())) {
                return policyType;
            }
        }
        return null;
    }

    @Override
    public TPolicyTemplate getTemplate() {
        if (this.policy == null) {
            LOG.debug("Internal policy is null");
        }
        if (this.policy.getPolicyRef() == null) {
            LOG.debug("Internal policyRef is null");
        }
        for (final TPolicyTemplate policyTemplate : this.defs.getAllPolicyTemplates()) {
            if (policyTemplate.getId().equals(this.policy.getPolicyRef().getLocalPart())) {
                return policyTemplate;
            }
        }
        return null;
    }

    @Override
    public TEntityTemplate.Properties getProperties() {
        return this.policy.getProperties();
    }
}
