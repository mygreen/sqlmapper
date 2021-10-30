package com.github.mygreen.sqlmapper.core.meta;

import java.util.Collection;
import java.util.Optional;

import org.springframework.util.LinkedCaseInsensitiveMap;

import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class StoredParamMeta {

    /**
     * パラメータのタイプです
     */
    @Getter
    private final Class<?> paramType;

    /**
     * 匿名パラメータ(INのみ)かどうか。
     */
    @Getter
    private final boolean anonymouse;

    /**
     * 値の変換処理。
     * <p>匿名パラメータのときのみ値を持つ。
     */
    @Getter
    private Optional<ValueType<?>> valueType;

    /**
     * プロパティ情報
     * <p>key=プロパティ名</p>
     */
    private LinkedCaseInsensitiveMap<StoredPropertyMeta> propertyMetaMap = new LinkedCaseInsensitiveMap<>();

    public void setValueType(@NonNull ValueType<?> valueType) {
        this.valueType = Optional.of(valueType);
    }

    /**
     * プロパティ情報を追加します。
     * @param propertyMeta プロパティ情報。
     */
    public void addPropertyMeta(@NonNull StoredPropertyMeta propertyMeta) {

        propertyMetaMap.put(propertyMeta.getName(), propertyMeta);

    }

    /**
     * プロパティメタ情報を取得します。
     * @param propertyName プロパティ名
     * @return プロパティメタ情報
     */
    public Optional<StoredPropertyMeta> getPropertyMeta(@NonNull String propertyName) {

        return Optional.ofNullable(propertyMetaMap.get(propertyName));

    }

    /**
     * プロパティメタデータがあるかどうかを返します。
     * @param propertyName プロパティ名
     * @return {@literal true}のときプロパティメタデータがあります。
     */
    public boolean hasPropertyMeta(String propertyName) {
        return propertyMetaMap.containsKey(propertyName);
    }

    /**
     * 全てのプロパティメタ情報の一覧を返します。
     * @return プロパティメタ情報の一覧
     */
    public Collection<StoredPropertyMeta> getAllPropertyMeta() {
        return propertyMetaMap.values();
    }

}
