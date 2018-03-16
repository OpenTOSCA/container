package org.opentosca.planbuilder.importer.context.impl;

import org.oasis_open.docs.tosca.ns._2011._12.TPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyImpl extends AbstractPolicy {

    private final static Logger LOG = LoggerFactory.getLogger(PolicyImpl.class);

    private final TPolicy policy;
    private final DefinitionsImpl defs;

    public PolicyImpl(final TPolicy policy, final DefinitionsImpl definitions) {
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
    public AbstractPolicyType getType() {
        if (this.policy == null) {
            LOG.debug("Internal policy is null");
        }
        if (this.policy.getPolicyType() == null) {
            LOG.debug("Internal policyType is null");
        }
        for (final AbstractPolicyType policyType : this.defs.getAllPolicyTypes()) {
            if (policyType.getId().equals(this.policy.getPolicyType())) {
                return policyType;
            }

        }
        return null;
    }

    @Override
    public AbstractPolicyTemplate getTemplate() {
        if (this.policy == null) {
            LOG.debug("Internal policy is null");
        }
        if (this.policy.getPolicyRef() == null) {
            LOG.debug("Internal policyRef is null");
        }
        for (final AbstractPolicyTemplate policyTemplate : this.defs.getAllPolicyTemplates()) {
            if (policyTemplate.getId().equals(this.policy.getPolicyRef().getLocalPart())) {
                return policyTemplate;
            }
        }
        return null;
    }

    @Override
    public AbstractProperties getProperties() {
        if (this.policy.getAny() != null && !this.policy.getAny().isEmpty()) {
            return new PropertiesImpl(this.policy.getAny().get(0));
        } else {
            return null;
        }
    }
}
