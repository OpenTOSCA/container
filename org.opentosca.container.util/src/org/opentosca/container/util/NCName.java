package org.opentosca.container.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NCName {

	public static final Pattern ILLEGAL_CHARACTERS_PATTERN = Pattern
			.compile("[\\s\\:\\@\\$\\%\\&\\/\\+\\,\\;\\!\"\\#\'\\(\\)\\*\\<\\=\\>\\?\\[\\]\\\\^\\`\\{\\|\\}\\~]");
	public static final Pattern ILLEGAL_START_CHARACTERS_PATTERN = Pattern.compile("^[\\.\\d\\-]+");

	// To check validity
	public static final Pattern NAME_PATTERN = Pattern.compile("[^\\.\\d\\-][\\w]+");

	public static final String OUTPUT_FORMAT = "%s:%s";

	private String prefix;
	private String ncName;

	@Override
	public String toString() {
		return String.format(OUTPUT_FORMAT, this.prefix, this.ncName);
	}

	public NCName(final String prefix, final String ncName) {
		Objects.requireNonNull(prefix);
		Objects.requireNonNull(ncName);

		String prefixResult = prefix;
		String ncNameResult = ncName;

		// Step 1
		prefixResult = removeWhiteSpaces(prefixResult);
		ncNameResult = removeWhiteSpaces(ncNameResult);
		
		// Step 2
		prefixResult = replaceInvalidCharacters(prefixResult);
		ncNameResult = replaceInvalidCharacters(ncNameResult);

		// Step 3
		prefixResult = removeInvalidStartCharacters(prefixResult);
		ncNameResult = removeInvalidStartCharacters(ncNameResult);

		// Step 4
		checkNCNameValidity(prefixResult);
		checkNCNameValidity(ncNameResult);

		this.prefix = prefixResult;
		this.ncName = ncNameResult;
	}

	private String removeInvalidStartCharacters(final String input) {
		String output = input;

		Matcher match = ILLEGAL_START_CHARACTERS_PATTERN.matcher(output);

		output = match.replaceFirst("");

		return output;
	}

	private String removeWhiteSpaces(final String input) {
		String output = input;
		Pattern whiteSpacePattern = Pattern.compile("\\s");

		Matcher match = whiteSpacePattern.matcher(output);

		output = match.replaceAll("");

		return output;
	}

	private String replaceInvalidCharacters(final String input) {
		String output = input;

		Matcher match = ILLEGAL_CHARACTERS_PATTERN.matcher(output);

		output = match.replaceAll("_");

		return output;
	}

	private void checkNCNameValidity(String input) {

		if (input.length() == 0) {
			throw new NCNameException("Resulting NCName was empty");
		}
	}

	public class NCNameException extends RuntimeException {

		/**
		 * Gernerated serialVersionUID
		 */
		private static final long serialVersionUID = -4052808822350498637L;

		protected NCNameException(String msg) {
			super(msg);
		}

	}
}
