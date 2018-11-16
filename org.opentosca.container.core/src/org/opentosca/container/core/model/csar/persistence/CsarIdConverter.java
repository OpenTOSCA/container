package org.opentosca.container.core.model.csar.persistence;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.opentosca.container.core.model.csar.CsarId;

public class CsarIdConverter implements Converter {

    private static final long serialVersionUID = -2552365749611257786L;

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        // input is the CsarId from the Entity
        return objectValue == null ? null : ((CsarId)objectValue).csarName();
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        // input is the value from the database
        return dataValue == null ? null : new CsarId((String) dataValue);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {}
}
