package org.opentosca.container.util.formatter.impl;

import org.opentosca.container.util.formatter.AbstractFormatter;
import org.opentosca.container.util.formatter.AbstractFormattingResult;
import org.opentosca.container.util.impl.Result;

/**
 * Using this class ensures a proper formatting of any given data type into a specified form by providing a resulting object of the formatted data.
 * The formatted data is contained within the corresponding FormattingResult data type.
 * The output data type of a Formatter is always the same as the input data type.
 * @author Stefan
 *
 * @param <T> The input data type.
 */
public abstract class Formatter<T> implements AbstractFormatter<T> {

	public abstract FormattingResult format(T input);
	
	/**
	 * Contains the result of the formatting process.
	 * This class has to be public but its constructor may only be called by the Formatter itself.
	 * @author Stefan
	 *
	 */
	public class FormattingResult extends Result<T> implements AbstractFormattingResult<T> {

		protected FormattingResult(T result) {
			super(result);
		}
	}

}
