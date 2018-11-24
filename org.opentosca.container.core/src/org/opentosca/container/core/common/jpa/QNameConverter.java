package org.opentosca.container.core.common.jpa;

import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used to convert QNames to String, and Strings back to QNames when persisting QName
 * fields with JPA. The conversion needs to be done, as we cannot directly query for QNames in JPQL.
 */
public class QNameConverter implements org.eclipse.persistence.mappings.converters.Converter {

    private static final long serialVersionUID = 5695923859083900495L;
    public static final String name = "QNameConverter";


    @Override
    public Object convertDataValueToObjectValue(final Object arg0, final Session arg1) {
        return arg0 != null ? QName.valueOf((String) arg0) : null;
    }

    @Override
    public Object convertObjectValueToDataValue(final Object arg0, final Session arg1) {
        return arg0 != null ? ((QName) arg0).toString() : null;
    }

    @Override
    public void initialize(final DatabaseMapping arg0, final Session arg1) {}

    @Override
    public boolean isMutable() {
        return false;
    }
}
