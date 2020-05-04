package com.github.mygreen.sqlmapper.type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.lob.LobHandler;

import com.github.mygreen.sqlmapper.annotation.Convert;
import com.github.mygreen.sqlmapper.annotation.Enumerated;
import com.github.mygreen.sqlmapper.annotation.Temporal;
import com.github.mygreen.sqlmapper.localization.MessageBuilder;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.type.enumeration.EnumOrdinalType;
import com.github.mygreen.sqlmapper.type.enumeration.EnumStringType;
import com.github.mygreen.sqlmapper.type.lob.LobByteArrayType;
import com.github.mygreen.sqlmapper.type.lob.LobStringType;
import com.github.mygreen.sqlmapper.type.standard.SqlDateType;
import com.github.mygreen.sqlmapper.type.standard.SqlTimeType;
import com.github.mygreen.sqlmapper.type.standard.SqlTimestampType;
import com.github.mygreen.sqlmapper.type.standard.UtilDateType;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * {@link ValueType} を管理するためのクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ValueTypeResolver {

    @Getter
    @Setter
    @Autowired
    private ApplicationContext applicationContext;

    @Getter
    @Setter
    @Autowired
    private MessageBuilder messageBuilder;

    @Getter
    @Setter
    @Autowired
    private LobHandler lobHandler;

    /**
     * クラスタイプで関連付けられた{@link ValueType}のマップ
     */
    private Map<Class<?>, ValueType<?>> typeMap = new ConcurrentHashMap<>();

    /**
     * パスで関連づけられた{@link ValueType}のマップ
     */
    private Map<String, ValueTypeHolder> pathMap = new ConcurrentHashMap<>();

    /**
     * プロパティメタ情報に対する値の変換処理を取得する。
     * @param propertyMeta プロパティメタ情報
     * @return 対応する {@link ValueType}の実装。
     * @throws ValueTypeNotFoundException 対応する {@link ValueType} が見つからない場合。
     */
    public ValueType<?> getValueType(@NonNull PropertyMeta propertyMeta) {

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

        throw new ValueTypeNotFoundException(propertyMeta, messageBuilder.create("typeValue.notFound")
                .varWithClass("entityClass", propertyMeta.getDeclaringClass())
                .var("property", propertyMeta.getName())
                .varWithClass("propertyType", propertyType)
                .format());

    }

    /**
     * 値の変換処理を取得します。
     * @param requiredType プロパティのクラスタイプ。
     * @param propertyPath プロパティのパス。
     * @return 対応する変換処理の実装を返します。見つからない場合は {@literal null} を返しまsう。
     */
    public ValueType<?> getValueType(@NonNull Class<?> requiredType, String propertyPath) {

        // 完全なパスで比較
        if(pathMap.containsKey(propertyPath)) {
            return pathMap.get(propertyPath).get(requiredType);
        }

        // インデックスを除去した形式で比較
        final List<String> strippedPaths = new ArrayList<>();
        addStrippedPropertyPaths(strippedPaths, "", propertyPath);
        for(String strippedPath : strippedPaths) {
            ValueType<?> valueType = pathMap.get(strippedPath).get(requiredType);
            if(valueType != null) {
                return valueType;
            }
        }

        // 見つからない場合は、クラスタイプで比較
        if(typeMap.containsKey(requiredType)) {
            return typeMap.get(requiredType);
        }

        return null;

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

        throw new ValueTypeNotFoundException(propertyMeta, messageBuilder.create("typeValue.notFoundLob")
                .varWithClass("entityClass", propertyMeta.getDeclaringClass())
                .var("property", propertyMeta.getName())
                .varWithClass("propertyType", propertyType)
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
                return new EnumOrdinalType(propertyType, messageBuilder);
            } else if(enumType == Enumerated.EnumType.STRING) {
                return new EnumStringType(propertyType, messageBuilder);
            }
        }

        // デフォルトの場合
        return new EnumStringType(propertyType, messageBuilder);

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

    /**
     * プロパティのパスを指定して{@link ValeType} を登録します。
     * <p>SQLテンプレート中の変数（プロパティパス／式）を元に関連付ける再に使用します。
     *
     * @param <T> 関連付ける型
     * @param propertyPath プロパティパス／式
     * @param type 関連付けるクラスタイプ
     * @param valueType {@link TypeValue}の実装
     */
    public <T> void register(@NonNull String propertyPath, @NonNull Class<T> type, @NonNull ValueType<T> valueType) {
        this.pathMap.put(propertyPath, new ValueTypeHolder(type, valueType));
    }

    /**
     * パスからリストのインデックス([1])やマップのキー([key])を除去したものを構成する。
     * <p>SpringFrameworkの「PropertyEditorRegistrySupport#addStrippedPropertyPaths(...)」の処理</p>
     * @param strippedPaths 除去したパス
     * @param nestedPath 現在のネストしたパス
     * @param propertyPath 処理対象のパス
     */
    public void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {

        final int startIndex = propertyPath.indexOf('[');
        if (startIndex != -1) {
            final int endIndex = propertyPath.indexOf(']');
            if (endIndex != -1) {
                final String prefix = propertyPath.substring(0, startIndex);
                final String key = propertyPath.substring(startIndex, endIndex + 1);
                final String suffix = propertyPath.substring(endIndex + 1, propertyPath.length());

                // Strip the first key.
                strippedPaths.add(nestedPath + prefix + suffix);

                // Search for further keys to strip, with the first key stripped.
                addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);

                // Search for further keys to strip, with the first key not stripped.
                addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
            }
        }
    }

    @RequiredArgsConstructor
    private static class ValueTypeHolder {

        private final Class<?> registeredType;

        private final ValueType<?> valueType;

        ValueType<?> get(final Class<?> requiredType) {
            if(registeredType.isAssignableFrom(requiredType)) {
                return valueType;
            }

            return null;
        }

    }

}
