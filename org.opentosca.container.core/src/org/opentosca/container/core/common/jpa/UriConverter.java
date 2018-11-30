package org.opentosca.container.core.common.jpa;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used to convert URIs to String, and Strings back to URIs when persisting URI fields
 * with JPA. The conversion needs to be done, as we cannot directly query for URI in JPQL.
 */
public class UriConverter implements Converter {

    public static final String name = "URIConverter";
    private static final long serialVersionUID = 5695923859083900495L;


    @Override
    public Object convertDataValueToObjectValue(final Object arg0, final Session arg1) {
        try {
            return new URI((String) arg0);
        }
        catch (final URISyntaxException e) {
            return null;
        }
    }

    @Override
    public Object convertObjectValueToDataValue(final Object arg0, final Session arg1) {
        return arg0 != null ? ((URI) arg0).toString() : null;
    }

    @Override
    public void initialize(final DatabaseMapping arg0, final Session arg1) {}

    @Override
    public boolean isMutable() {
        return false;
    }
}
