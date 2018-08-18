package org.opentosca.container.util.converter;

import java.util.function.Function;

/**
 * 
 * @author Stefan
 *
 * @param <T> The input data type
 * @param <R> The output data type
 */
public interface AbstractConverter<T, R extends AbstractConversionResult<?>> extends Function<T, R> {

}
