package org.opentosca.container.core.common.jpa;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used to convert {@link Path} to String, and {@link String} back
 * to {@link Path} when persisting {@link Path} fields with JPA. The conversion
 * needs to be done, as we cannot directly query for {@link Path} in JPQL.
 */
public class PathConverter implements Converter {
	
	private static final long serialVersionUID = 3747978557147488965L;
	
	
	@Override
	public Object convertDataValueToObjectValue(final Object arg0, final Session arg1) {
		return (arg0 != null) ? Paths.get((String) arg0) : null;
	}
	
	@Override
	public Object convertObjectValueToDataValue(final Object arg0, final Session arg1) {
		return (arg0 != null) ? ((Path) arg0).toString() : null;
	}
	
	@Override
	public void initialize(final DatabaseMapping arg0, final Session arg1) {
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
}
