package org.opentosca.container.util.builders;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opentosca.container.util.builder.impl.Builder;
import org.opentosca.container.util.builder.impl.Builder.BuildingResult;

/**
 * Builds a fully qualified and valid NCName string with the format <ncNamePrefix>:<ncName>
 * Also checks for invalid characters and throws a {@link NCNameBuildingException} if found.
 * @author Stefan
 *
 */
public class NCNameBuilder extends Builder<String> {
	
	private String ncPrefix;
	private String ncName;
	
	// TODO there might be missing some valid characters in these patterns.
	private Pattern ncPrefixPattern = Pattern.compile("[\\w]+");
	private Pattern ncNamePattern = Pattern.compile("[\\w]+");
	
	/**
	 * Sets the NCName prefix.
	 * @param ncPrefix the prefix to set.
	 */
	public void setNCPrefix(String ncPrefix) {
		Objects.requireNonNull(ncPrefix);
		Matcher match = ncPrefixPattern.matcher(ncPrefix);
		
		if(!match.matches()) {
			throw new NCNameBuildingException("NCprefix contains invalid characters or is empty.");
		}
		this.ncPrefix = ncPrefix;
	}
	
	/**
	 * Sets the NCname
	 * @param ncName the NCName to set.
	 */
	public void setNCName(String ncName) {
		Objects.requireNonNull(ncName);
		Matcher match = ncNamePattern.matcher(ncName);
		
		if(!match.matches()) {
			throw new NCNameBuildingException("NCname contains invalid characters or is empty.");
		}
		this.ncName = ncName;
	}

	@Override
	public NCNameBuildingResult build() {
		Objects.requireNonNull(this.ncName, "NCName cannot be null during build process");
		Objects.requireNonNull(this.ncPrefix, "NCName prefix cannot be null during build process");
		String outputFormat = "%s:%s";
		
		String output = String.format(outputFormat, this.ncPrefix, this.ncName);
		
		NCNameBuildingResult result = new NCNameBuildingResult(output);
		return result;
	}


	public class NCNameBuildingResult extends BuildingResult<String> {

		protected NCNameBuildingResult(String result) {
			super(result);
		}
		
	}
	
	public class NCNameBuildingException extends RuntimeException {

		/**
		 * Generated serialVersionUID
		 */
		private static final long serialVersionUID = 6745709031748943083L;
		
		public NCNameBuildingException(String msg) {
			super(msg);
		}
		
	}
}
