package org.opentosca.container.util.formatter;

/**
 * Interface to build the skeleton of a Formatter.
 * 
 * @author Stefan
 *
 * @param <T>
 */
public interface AbstractFormatter<T> {

	AbstractFormattingResult<T> format(final T input);
}
