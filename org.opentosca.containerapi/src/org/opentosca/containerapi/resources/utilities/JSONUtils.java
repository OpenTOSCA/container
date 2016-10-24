package org.opentosca.containerapi.resources.utilities;

public class JSONUtils {
	
	
	public static String withoutQuotationMarks(String str) {
		return str.substring(1, str.length() - 1);
	}
}
