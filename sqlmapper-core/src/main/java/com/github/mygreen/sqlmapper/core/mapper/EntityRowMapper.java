package com.github.mygreen.sqlmapper.core.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.JoinAssociation;

/**
 * 1レコード分のエンティティをマッピングする。
 *
 * @author T.TSUCHIE
 *
 */
public class EntityRowMapper<T> implements RowMapper<T> {

    /**
     * エンティティクラスです。
     */
    private final Class<T> baseEntityClass;

    /**
     * 抽出対象のプロパティと所属するエンティティの暮らすタイプのマップ情報です。
     */
    private final Map<PropertyMeta, Class<?>> propertyMetaEntityTypeMap;

    /**
     * 結合する際の構成情報です
     */
    @SuppressWarnings("rawtypes")
    private final List<JoinAssociation> joinAssociations;

    /**
     * エンティティマッピング後のコールバック処理
     */
    private final Optional<EntityMappingCallback<T>> callback;

    /**
     * マッピング対象のプロパティのリスト
     * SELECT文の指定順になっている。
     */
    private PropertyMeta[] propertyMetaList;
    /**
     * 取得するカラムが確定している場合は、プロパティ情報を指定する。
     * @param baseEntityClass ベースとなるエンティティクラス
     * @param propertyMetaList 取得対象のプロパティ情報
     * @param callback エンティティマッピング後のコールバック処理
     */
    @SuppressWarnings("rawtypes")
    public EntityRowMapper(Class<T> baseEntityClass, Map<PropertyMeta, Class<?>> propertyMetaEntityTypeMap,
            List<JoinAssociation> joinAssociations, Optional<EntityMappingCallback<T>> callback) {
        this.baseEntityClass = baseEntityClass;
        this.propertyMetaEntityTypeMap = propertyMetaEntityTypeMap;
        this.joinAssociations = joinAssociations;
        this.callback = callback;

        this.propertyMetaList = propertyMetaEntityTypeMap.keySet().toArray(new PropertyMeta[propertyMetaEntityTypeMap.size()]);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        // マッピングするエンティティのインスタンスの準備
        Map<Class<?>, Object> entityInstanceMap = new HashMap<>();
        for(Class<?> entityClass : propertyMetaEntityTypeMap.values()) {
            Object entityObject = BeanUtils.instantiateClass(entityClass);
            entityInstanceMap.put(entityClass, entityObject);
        }

        for(int i=0; i < propertyMetaList.length; i++) {
            PropertyMeta propertyMeta = propertyMetaList[i];
            if(propertyMeta == null) {
                // 任意のSQL実行時にエンティティのプロパティにマッピング対象でないカラムがある場合ので無視する。
                continue;
            }
            Object propertyValue = propertyMeta.getValueType().getValue(rs, i + 1);


            Object entityObject = entityInstanceMap.get(propertyMetaEntityTypeMap.get(propertyMeta));
            PropertyValueInvoker.setPropertyValue(propertyMeta, entityObject, propertyValue);
        }

        // 結合テーブルの構成定義の呼び出し
        for(JoinAssociation association : joinAssociations) {
            Object entityObject1 = entityInstanceMap.get(association.getEntity1().getType());
            Object entityObject2 = entityInstanceMap.get(association.getEntity2().getType());
            association.getAssociator().accept(entityObject1, entityObject2);
        }

        final T baseEntity = (T)entityInstanceMap.get(baseEntityClass);

        // コールバック処理の実行
        callback.ifPresent(c -> c.call(baseEntity));

        return baseEntity;
    }

}
