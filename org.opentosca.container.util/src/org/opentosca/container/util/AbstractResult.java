package org.opentosca.container.util;

/**
 * Contains the result of any process.
 * @author Stefan
 *
 * @param <T> The type of the resulting data.
 */
public interface AbstractResult<T> {

	/**
	 * 
	 * @return The result of the process.
	 */
	T getResult();
}
