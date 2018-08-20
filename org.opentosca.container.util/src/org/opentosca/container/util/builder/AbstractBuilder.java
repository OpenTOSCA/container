package org.opentosca.container.util.builder;

import org.opentosca.container.util.AbstractBuildingResult;

public interface AbstractBuilder<T> {

	/**
	 * Performs the building process of the building object.
	 * 
	 * @return the result of the building process as {@link AbstractBuildingResult}
	 *         object.
	 */

	// Additional Non-JavaDoc comment:
	// The building process itself does not need any external information. Therefore
	// no input parameters are specified.
	// All data required for the building process is set by setter-methods defined
	// within the Builder object.
	AbstractBuildingResult<T> build();
}
