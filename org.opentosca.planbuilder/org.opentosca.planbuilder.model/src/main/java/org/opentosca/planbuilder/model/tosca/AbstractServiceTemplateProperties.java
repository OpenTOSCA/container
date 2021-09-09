/**
 *
 */
package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPropertyMapping;

/**
 * <p>
 * This class represents TOSCA ServiceTemplate Properties
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class AbstractServiceTemplateProperties {

    /**
     * Returns the PropertyMappings of this ServiceTemplate Properties
     *
     * @return a List of AbstractPropertyMappings
     */
    public abstract List<TPropertyMapping> getPropertyMappings();

    /**
     * Returns the Properties of this ServiceTemplate Properties
     *
     * @return an AbstractProperties of this ServiceTemplate Properties
     */
    public abstract TBoundaryDefinitions.Properties getProperties();
}
