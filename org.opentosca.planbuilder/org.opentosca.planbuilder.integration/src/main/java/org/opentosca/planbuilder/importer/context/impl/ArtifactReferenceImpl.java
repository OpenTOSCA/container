package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.tosca.ns._2011._12.TArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;

/**
 * <p>
 * This class implements AbstractArtifactReference
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class ArtifactReferenceImpl extends AbstractArtifactReference {

    private final TArtifactReference ref;

    /**
     * Constructor
     *
     * @param ref a JAXB TArtifactReference
     */
    public ArtifactReferenceImpl(final TArtifactReference ref) {
        this.ref = ref;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReference() {
        if (!this.getIncludePatterns().isEmpty()) {
            String reference = this.ref.getReference();
            if (this.getIncludePatterns().size() == 1) {
                reference += "/" + this.getIncludePatterns().get(0);
                return reference;
            }
            return reference;
        } else {
            return this.ref.getReference();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getIncludePatterns() {
        final List<String> patterns = new ArrayList<>();
        for (final Object obj : this.ref.getIncludeOrExclude()) {
            if (obj instanceof TArtifactReference.Include) {
                patterns.add(((TArtifactReference.Include) obj).getPattern());
            }
        }
        return patterns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getExcludePatterns() {
        final List<String> patterns = new ArrayList<>();
        for (final Object obj : this.ref.getIncludeOrExclude()) {
            if (obj instanceof TArtifactReference.Exclude) {
                patterns.add(((TArtifactReference.Exclude) obj).getPattern());
            }
        }
        return patterns;
    }
}
