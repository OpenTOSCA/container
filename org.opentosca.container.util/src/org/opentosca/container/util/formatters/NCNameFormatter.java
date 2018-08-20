package org.opentosca.container.util.formatters;

import org.opentosca.container.util.formatter.impl.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Formats a given String into a valid NCName format.
 * {@link https://stackoverflow.com/questions/1631396/what-is-an-xsncname-type-and-when-should-it-be-used}
 * @author Stefan
 *
 */
public class NCNameFormatter extends Formatter<String> {
	
	private final static Logger LOG = LoggerFactory.getLogger(NCNameFormatter.class);

	@Override
	public NCNameFormattingResult format(final String input) {
		String output = input;
		output.replaceAll("\\.", "_");
		output.replaceAll(" ", "_");
		output.replaceAll(":", "_");
		
		NCNameFormattingResult result = new NCNameFormattingResult(output);
		LOG.debug("Successfully formatted input {} to output {}", input, output);
		return result;
	}

	
	public class NCNameFormattingResult extends FormattingResult {

		protected NCNameFormattingResult(String result) {
			super(result);
		}
	}
}
