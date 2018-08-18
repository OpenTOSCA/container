package org.opentosca.container.util.converter.impl;

import org.opentosca.container.util.converter.AbstractConversionResult;
import org.opentosca.container.util.converter.AbstractConverter;
import org.opentosca.container.util.impl.Result;

/**
 * A Converter is used to convert one data type into another.
 * For example converting a time stamp (long) into a datetime object.
 * 
 * @author Stefan
 *
 * @param <T> The input data type
 * @param <S> The output data type
 */
public abstract class Converter<T, S> implements AbstractConverter<T, S> {

	@Override
	public abstract ConversionResult<S> convert(T input);
	
	public class ConversionResult<O> extends Result<O> implements AbstractConversionResult<O> {

		protected ConversionResult(O result) {
			super(result);
		}

	}

}
