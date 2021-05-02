package com.github.mygreen.sqlmapper.core.type;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.lob.LobHandler;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.core.annotation.Convert;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated;
import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.type.enumeration.EnumOrdinalType;
import com.github.mygreen.sqlmapper.core.type.enumeration.EnumStringType;
import com.github.mygreen.sqlmapper.core.type.lob.LobByteArrayType;
import com.github.mygreen.sqlmapper.core.type.lob.LobStringType;
import com.github.mygreen.sqlmapper.core.type.standard.SqlDateType;
import com.github.mygreen.sqlmapper.core.type.standard.SqlTimeType;
import com.github.mygreen.sqlmapper.core.type.standard.SqlTimestampType;
import com.github.mygreen.sqlmapper.core.type.standard.UtilDateType;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * {@link ValueType} を管理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ValueTypeRegistry {

    @Getter
    @Setter
    @Autowired
    private ApplicationContext applicationContext;

    @Getter
    @Setter
    @Autowired
    private MessageFormatter messageFormatter;

    @Getter
    @Setter
    @Autowired
    private LobHandler lobHandler;

    /**
     * クラスタイプで関連付けられた{@link ValueType}のマップ
     */
    private Map<Class<?>, ValueType<?>> typeMap = new ConcurrentHashMap<>();

    /**
     * プロパティメタ情報に対する値の変換処理を取得する。
     * @param propertyMeta プロパティメタ情報
     * @return 対応する {@link ValueType}の実装。
     * @throws ValueTypeNotFoundException 対応する {@link ValueType} が見つからない場合。
     */
    public ValueType<?> findValueType(@NonNull PropertyMeta propertyMeta) {

        Optional<Convert> convertAnno = propertyMeta.getAnnotation(Convert.class);
        if(convertAnno.isPresent()) {
            return getValueType(propertyMeta, convertAnno.get());
        }

        final Class<?> propertyType = propertyMeta.getPropertyType();

        if(propertyMeta.isLob()) {
            return getLobType(propertyMeta);
        }

        if(typeMap.containsKey(propertyType)) {
            return typeMap.get(propertyType);
        }

        if(propertyType.isEnum()) {
            return getEnumType(propertyMeta);
        }

        if(Date.class.isAssignableFrom(propertyType)) {
            return getUtilDateType(propertyMeta);
        }

        throw new ValueTypeNotFoundException(propertyMeta, messageFormatter.create("typeValue.notFound")
                .paramWithClass("entityClass", propertyMeta.getDeclaringClass())
                .param("property", propertyMeta.getName())
                .paramWithClass("propertyType", propertyType)
                .format());

    }

    /**
     * アノテーション {@link Convert} から {@link ValueType} を取得する。
     * @param propertyMeta 対象となるプロパティメタ情報
     * @param convertAnno 変換規則を指定するアノテーション
     * @return {@link ValueType}のインスタンス。
     */
    protected ValueType<?> getValueType(final PropertyMeta propertyMeta, final Convert convertAnno) {

        if(convertAnno.name().isEmpty()) {
            return BeanUtils.instantiateClass(convertAnno.converter());
        } else {
            return applicationContext.getBean(convertAnno.name(), convertAnno.converter());
        }
    }

    /**
     * ラージオブジェクト用の{@link ValueType} を取得する。
     * @param propertyMeta 対象となるプロパティメタ情報
     * @return {@link ValueType}のインスタンス。
     */
    protected ValueType<?> getLobType(final PropertyMeta propertyMeta) {

        final Class<?> propertyType = propertyMeta.getPropertyType();
        if(String.class.isAssignableFrom(propertyType)) {
            return new LobStringType(lobHandler);

        } else if (byte[].class.isAssignableFrom(propertyType)) {
            return new LobByteArrayType(lobHandler);
        }

        throw new ValueTypeNotFoundException(propertyMeta, messageFormatter.create("typeValue.notFoundLob")
                .paramWithClass("entityClass", propertyMeta.getDeclaringClass())
                .param("property", propertyMeta.getName())
                .paramWithClass("propertyType", propertyType)
                .format());

    }

    /**
     * 列挙型用の{@link ValueType} を取得する。
     * @param propertyMeta 対象となるプロパティメタ情報
     * @return {@link ValueType}のインスタンス。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected ValueType<?> getEnumType(final PropertyMeta propertyMeta) {

        Optional<Enumerated> enumeratedAnno = propertyMeta.getAnnotation(Enumerated.class);
        Class<?> propertyType = propertyMeta.getPropertyType();

        if(enumeratedAnno.isPresent()) {
            final Enumerated.EnumType enumType = enumeratedAnno.get().value();
            if(enumType == Enumerated.EnumType.ORDINAL) {
                return new EnumOrdinalType(propertyType, messageFormatter);
            } else if(enumType == Enumerated.EnumType.STRING) {
                return new EnumStringType(propertyType, messageFormatter);
            }
        }

        // デフォルトの場合
        return new EnumStringType(propertyType, messageFormatter);

    }

    /**
     * 時制の型が不明な {@link java.util.Date} の ValueType} を取得する。
     * @param propertyMeta 対象となるプロパティメタ情報
     * @return {@link ValueType}のインスタンス。
     */
    protected ValueType<?> getUtilDateType(final PropertyMeta propertyMeta) {

        Optional<Temporal> temporalAnno = propertyMeta.getAnnotation(Temporal.class);

        if(temporalAnno.isPresent()) {
            final Temporal.TemporalType temporalType = temporalAnno.get().value();
            if(temporalType == Temporal.TemporalType.TIMESTAMP) {
                return new UtilDateType(new SqlTimestampType());
            } else if(temporalType == Temporal.TemporalType.DATE) {
                return new UtilDateType(new SqlDateType());

            } else if(temporalType == Temporal.TemporalType.TIME) {
                return new UtilDateType(new SqlTimeType());

            }
        }

        // デフォルトの場合
        return new UtilDateType(new SqlTimestampType());

    }

    /**
     * {@link ValeType} を登録します。
     * @param <T> 関連付ける型
     * @param type 関連付けるクラスタイプ
     * @param valueType {@link ValueType}の実装
     */
    public <T> void register(@NonNull Class<T> type, @NonNull ValueType<T> valueType) {
        this.typeMap.put(type, valueType);
    }
}
