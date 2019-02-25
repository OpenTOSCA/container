package org.opentosca.planbuilder.model.tosca;

import java.util.List;

/**
 * <p>
 * This class represents a TOSCA ArtifactReference
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class AbstractArtifactReference {

    /**
     * Returns the reference
     *
     * @return a String containing a path to some artifact
     */
    public abstract String getReference();

    /**
     * Returns a List of Strings, containing Include Pattern declarations
     *
     * @return a List of Strings
     */
    public abstract List<String> getIncludePatterns();

    /**
     * Returns a List of Strings, containing Exclude Pattern declarations
     *
     * @return a List of Strings
     */
    public abstract List<String> getExcludePatterns();

}
