package org.opentosca.container.util.formatters;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opentosca.container.util.formatter.impl.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Formats a given String into a valid NCName format.
 * {@link https://stackoverflow.com/questions/1631396/what-is-an-xsncname-type-and-when-should-it-be-used}
 * 
 * @author Stefan
 *
 */
public class NCNameFormatter extends Formatter<String> {

	private final static Logger LOG = LoggerFactory.getLogger(NCNameFormatter.class);

	@Override
	public NCNameFormattingResult format(final String input) {
		Objects.requireNonNull(input);
		// Empty NCName is not allowed.
		if (input.length() == 0) {
			throw new NCNameFormattingException("Unable to format valid NCName from empty String.");
		}

		String output = input;

		// An NCName cannot begin with these characters.
		Pattern invalidStartPattern = Pattern.compile("^[\\.\\d\\-]+");
		Matcher matcher = invalidStartPattern.matcher(output);
		output = matcher.replaceFirst("");

		// Replace invalid characters.

		// Remove white spaces.
		output = output.replaceAll("\\s", "");
		// Replace every other invalid character.
		output = output.replaceAll("\\W", "_");

		if (output.length() == 0) {
			throw new NCNameFormattingException("Resulting NCName string was empty.");
		}

		NCNameFormattingResult result = new NCNameFormattingResult(output);
		NCNameFormatter.LOG.debug("Successfully formatted input {} to output {}", input, output);
		return result;
	}

	public class NCNameFormattingResult extends FormattingResult {

		protected NCNameFormattingResult(String result) {
			super(result);
		}
	}

	public class NCNameFormattingException extends RuntimeException {

		NCNameFormattingException(String string) {
			super(string);
		}

		/**
		 * Generated serialVersionUID
		 */
		private static final long serialVersionUID = 3611670635611371154L;

	}
}
