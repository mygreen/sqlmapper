package com.github.mygreen.sqlmapper.type.enumeration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.github.mygreen.sqlmapper.localization.MessageBuilder;
import com.github.mygreen.sqlmapper.type.SqlValueConversionException;
import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.NonNull;

public class EnumStringType<T extends Enum<T>> implements ValueType<T> {

    private final Class<T> enumType;

    private final MessageBuilder messageBuilder;

    /**
     * キーが文字列、値が列挙型のマップ
     */
    private final Map<String, Enum<?>> toObjectMap;

    public EnumStringType(@NonNull Class<T> enumClass, MessageBuilder messageBuilder) {
        this.enumType = enumClass;
        this.messageBuilder = messageBuilder;
        this.toObjectMap = createToObjectMap(enumClass);
    }

    private static <T extends Enum<T>> Map<String, Enum<?>> createToObjectMap(final Class<T> enumClass) {

        final EnumSet<T> set = EnumSet.allOf(enumClass);

        final Map<String, Enum<?>> map = new HashMap<>();
        for(T e : set) {
            final String key = e.name();
            map.put(key, e);

        }

        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue(ResultSet rs, int columnIndex) throws SQLException {

        String sqlValue = rs.getString(columnIndex);
        if(sqlValue == null) {
            return null;
        }

        T value = (T)toObjectMap.get(sqlValue);
        if(value == null) {
            throw new SqlValueConversionException(enumType, sqlValue, messageBuilder.create("typeValue.conversionFail")
                    .var("value", sqlValue)
                    .varWithClass("classType", enumType)
                    .format());
        }

        return value;

    }

    @Override
    public Object getSqlParameterValue(T value) {

        String sqlType = (value != null ? value.name() : null);
        return sqlType;

    }

    @Override
    public String getAsText(T value) {
        return value.name();
    }
}
