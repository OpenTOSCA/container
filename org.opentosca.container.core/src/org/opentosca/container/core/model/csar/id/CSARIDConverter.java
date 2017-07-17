package org.opentosca.container.core.model.csar.id;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used to convert {@link CSARID} to String, and {@link String}
 * back to {@link CSARID} when persisting {@link CSARID} fields with JPA. The
 * conversion needs to be done, as we cannot directly query for {@link CSARID}
 * in JPQL.
 */
public class CSARIDConverter implements Converter {

	private static final long serialVersionUID = -3390146119281040955L;


	@Override
	public Object convertDataValueToObjectValue(final Object arg0, final Session arg1) {
		return (arg0 != null) ? new CSARID(((String) arg0)) : null;
	}

	@Override
	public Object convertObjectValueToDataValue(final Object arg0, final Session arg1) {
		return (arg0 != null) ? ((CSARID) arg0).toString() : null;
	}

	@Override
	public void initialize(final DatabaseMapping arg0, final Session arg1) {
	}

	@Override
	public boolean isMutable() {
		return false;
	}
}
