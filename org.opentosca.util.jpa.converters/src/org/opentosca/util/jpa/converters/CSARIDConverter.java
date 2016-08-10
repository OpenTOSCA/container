package org.opentosca.util.jpa.converters;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.opentosca.core.model.csar.id.CSARID;

/**
 * This class is used to convert {@link CSARID} to String, and {@link String}
 * back to {@link CSARID} when persisting {@link CSARID} fields with JPA. The
 * conversion needs to be done, as we cannot directly query for {@link CSARID}
 * in JPQL. <br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CSARIDConverter implements Converter {
	
	private static final long serialVersionUID = -3390146119281040955L;
	
	
	/**
	 * Converts from {@link String} to {@link CSARID}.
	 */
	@Override
	public Object convertDataValueToObjectValue(Object arg0, Session arg1) {
		return (arg0 != null) ? new CSARID(((String) arg0)) : null;
	}
	
	/**
	 * Converts from {@link CSARID} to {@link String}.
	 */
	@Override
	public Object convertObjectValueToDataValue(Object arg0, Session arg1) {
		return (arg0 != null) ? ((CSARID) arg0).toString() : null;
	}
	
	@Override
	public void initialize(DatabaseMapping arg0, Session arg1) {
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
}
