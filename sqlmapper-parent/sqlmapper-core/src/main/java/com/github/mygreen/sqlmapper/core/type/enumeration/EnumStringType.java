package com.github.mygreen.sqlmapper.core.type.enumeration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.type.SqlValueConversionException;
import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.NonNull;

/**
 * 列挙型の名称とのマッピングを処理します。
 * <p>JDBCの型としては、{@link String} 型として処理を行います。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 列挙型のタイプ
 */
public class EnumStringType<T extends Enum<T>> implements ValueType<T> {

    /**
     * マッピング対象の列挙型のクラス
     */
    @Getter
    private final Class<T> enumType;

    /**
     * メッセージフォーマッター
     */
    private final MessageFormatter messageFormatter;

    /**
     * キーが文字列、値が列挙型のマップ
     */
    private final Map<String, Enum<?>> toObjectMap;

    /**
     * マッピング対象の列挙型を指定してインスタンスを作成します。
     * @param enumClass 列挙型のクラス
     * @param messageFormatter メッセージフォーマッター
     */
    public EnumStringType(@NonNull Class<T> enumClass, MessageFormatter messageFormatter) {
        this.enumType = enumClass;
        this.messageFormatter = messageFormatter;
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
            throw new SqlValueConversionException(enumType, sqlValue, messageFormatter.create("typeValue.conversionFail")
                    .param("value", sqlValue)
                    .paramWithClass("classType", enumType)
                    .format());
        }

        return value;

    }

    @Override
    public Object getSqlParameterValue(T value) {
        return value != null ? value.name() : null;

    }

    @Override
    public String getEmbeddedValue(T value) {
        return value != null ? value.name() : null;
    }

    @Override
    public int getSqlType(Dialect dialect) {
        return Types.VARCHAR;
    }
}
