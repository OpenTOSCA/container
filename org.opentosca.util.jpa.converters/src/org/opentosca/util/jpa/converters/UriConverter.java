package org.opentosca.util.jpa.converters;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used to convert URIs to String, and Strings back to URIs when
 * persisting URI fields with JPA. The conversion needs to be done, as we cannot
 * directly query for URI in JPQL.
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
public class UriConverter implements org.eclipse.persistence.mappings.converters.Converter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5695923859083900495L;
	
	
	@Override
	/**
	 * This method converts from String to URI
	 */
	public Object convertDataValueToObjectValue(Object arg0, Session arg1) {
		try {
			return new URI((String) arg0);
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	@Override
	/**
	 * This method converts from URI to String. Returns null, if the 
	 */
	public Object convertObjectValueToDataValue(Object arg0, Session arg1) {
		return (arg0 != null) ? ((URI) arg0).toString() : null;
	}
	
	@Override
	public void initialize(DatabaseMapping arg0, Session arg1) {
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
}
