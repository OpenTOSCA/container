package org.opentosca.container.util.builder.impl;

import org.opentosca.container.util.AbstractBuildingResult;
import org.opentosca.container.util.builder.AbstractBuilder;
import org.opentosca.container.util.impl.Result;

public abstract class Builder<T> implements AbstractBuilder<T> {

	public abstract BuildingResult<T> build();

	public class BuildingResult<S> extends Result<S> implements AbstractBuildingResult<S> {

		protected BuildingResult(S result) {
			super(result);
		}

	}
}
