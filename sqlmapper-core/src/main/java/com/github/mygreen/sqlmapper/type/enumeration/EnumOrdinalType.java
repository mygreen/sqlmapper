package com.github.mygreen.sqlmapper.type.enumeration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.type.SqlValueConversionException;
import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.NonNull;

public class EnumOrdinalType<T extends Enum<T>> implements ValueType<T> {

    private final Class<T> enumType;

    private final MessageFormatter messageFormatter;

    /**
     * キーが文字列、値が列挙型のマップ
     */
    private final Map<Integer, Enum<?>> toObjectMap;

    public EnumOrdinalType(@NonNull Class<T> enumClass, MessageFormatter messageFormatter) {
        this.enumType = enumClass;
        this.messageFormatter = messageFormatter;
        this.toObjectMap = createToObjectMap(enumClass);
    }

    private static <T extends Enum<T>> Map<Integer, Enum<?>> createToObjectMap(final Class<T> enumClass) {

        final EnumSet<T> set = EnumSet.allOf(enumClass);

        final Map<Integer, Enum<?>> map = new HashMap<>();
        for(T e : set) {
            final int key = e.ordinal();
            map.put(key, e);

        }

        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue(ResultSet rs, int columnIndex) throws SQLException {

        int sqlValue = rs.getInt(columnIndex);
        if(rs.wasNull()) {
            return null;
        }

        T value = (T)toObjectMap.get(sqlValue);
        if(value == null) {
            throw new SqlValueConversionException(enumType, sqlValue, messageFormatter.create("typeValue.conversionFail")
                    .param("value", sqlValue)
                    .paramWithClass("classType", enumType)
                    .format());
        }

        return value;

    }

    @Override
    public Object getSqlParameterValue(T value) {
        return value != null ? value.ordinal() : null;

    }

    @Override
    public String getEmbeddedValue(T value) {
        return value != null ? String.valueOf(value.ordinal()) : null;
    }
}
