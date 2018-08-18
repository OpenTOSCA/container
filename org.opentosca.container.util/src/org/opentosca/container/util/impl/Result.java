package org.opentosca.container.util.impl;

import org.opentosca.container.util.AbstractResult;

public class Result<T> implements AbstractResult<T> {

	private T result;

	protected Result(T result) {
		this.result = result;
	}
	
	@Override
	public T getResult() {
		return this.result;
	}
	
}
