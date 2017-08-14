package org.opentosca.container.core.next.utils;

public final class Enums {

	/**
	 * A utility method for all enums for string to enum conversion
	 *
	 * @param c the Enum type
	 * @param value value as case insesitive string
	 * @return The corresponding enum, or null
	 */
	public static <T extends Enum<T>> T valueOf(final Class<T> c, final String value) {
		return valueOf(c, value, null);
	}
	
	/**
	 * A utility method for all enums for string to enum conversion
	 *
	 * @param c the Enum type
	 * @param value value as case insesitive string
	 * @param defaultValue a default value
	 * @return The corresponding enum, or null
	 */
	public static <T extends Enum<T>> T valueOf(final Class<T> c, final String value, final T defaultValue) {
		if ((c != null) && (value != null)) {
			try {
				return Enum.valueOf(c, value.trim().toUpperCase());
			}
			catch (final IllegalArgumentException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
