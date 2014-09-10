package org.opentosca.util.jpa.converters;

import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used to convert QNames to String, and Strings back to QNames
 * when persisting QName fields with JPA. The conversion needs to be done, as we
 * cannot directly query for QNames in JPQL.
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
public class QNameConverter implements org.eclipse.persistence.mappings.converters.Converter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5695923859083900495L;
	
	
	@Override
	/**
	 * This method converts from String to QName
	 */
	public Object convertDataValueToObjectValue(Object arg0, Session arg1) {
		return (arg0 != null) ? QName.valueOf((String) arg0) : null;
	}
	
	@Override
	/**
	 * This method converts from QName to String. Returns null, if the 
	 */
	public Object convertObjectValueToDataValue(Object arg0, Session arg1) {
		return (arg0 != null) ? ((QName) arg0).toString() : null;
	}
	
	@Override
	public void initialize(DatabaseMapping arg0, Session arg1) {
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
}
