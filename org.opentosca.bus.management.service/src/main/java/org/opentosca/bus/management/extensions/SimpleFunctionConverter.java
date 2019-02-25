package org.opentosca.bus.management.extensions;

import java.util.function.Function;

import org.apache.camel.Exchange;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.TypeConversionException;
import org.apache.camel.TypeConverter;
import org.eclipse.jdt.annotation.Nullable;

public class SimpleFunctionConverter<O, F> implements TypeConverter {

    private final Function<F, O> conversion;
    private final Class<F> fromType;
    private final Class<O> toType;
    private final boolean allowNull;
    
    public SimpleFunctionConverter(Function<F, O> conversion, Class<F> fromType, Class<O> toType, boolean allowNull) {
        this.conversion = conversion;
        this.fromType = fromType;
        this.toType = toType;
        this.allowNull = allowNull;
    }
    
    @Override
    public boolean allowNull() {
        return allowNull;
    }

    @Override
    @Nullable
    public <T> T convertTo(Class<T> type, Object value) throws TypeConversionException {
        if (value == null && !allowNull) {
            return null;
        }
        if (!fromType.isAssignableFrom(value.getClass())) {
            return null;
        } 
        O result = conversion.apply(fromType.cast(value));
        if (type.isAssignableFrom(toType)) {
            return type.cast(result);
        }
        return null;
    }

    @Override
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        return convertTo(type, value);
    }

    @Override
    public <T> T mandatoryConvertTo(Class<T> type, Object value) throws TypeConversionException,
                                                                 NoTypeConversionAvailableException {
        if (value == null && !allowNull) {
            throw new NoTypeConversionAvailableException(value, type);
        }
        if (!fromType.isAssignableFrom(value.getClass())) {
            throw new NoTypeConversionAvailableException(value, type);
        } 
        O result = conversion.apply(fromType.cast(value));
        if (type.isAssignableFrom(toType)) {
            return type.cast(result);
        }
        throw new NoTypeConversionAvailableException(value, type);
    }

    @Override
    public <T> T mandatoryConvertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException,
                                                                                    NoTypeConversionAvailableException {
        return mandatoryConvertTo(type, value);
    }

    @Override
    public <T> T tryConvertTo(Class<T> type, Object value) {
        try {
            return convertTo(type, value);
        } catch (TypeConversionException swallow) {
            return null;
        }
    }

    @Override
    public <T> T tryConvertTo(Class<T> type, Exchange exchange, Object value) {
        return tryConvertTo(type, value);
    }

}
