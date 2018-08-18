package org.opentosca.container.util.converter;

/**
 * 
 * @author Stefan
 *
 * @param <S> The input data type
 * @param <T> The output data type
 */
public interface AbstractConverter<S, T> {

	
	AbstractConversionResult<T> convert(S input);
}
