package com.github.mygreen.sqlmapper.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;

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
    private final Class<T> entityClass;

    /**
     * マッピング対象のプロパティのリスト
     * SELECT文の指定順になっている。
     */
    private PropertyMeta[] propertyMetaList;

    /**
     * マッピング対象のエンティティ情報
     */
    private EntityMeta entityMeta;

    /**
     * 取得するカラムが確定している場合は、プロパティ情報を指定する。
     * @param entityClass エンティティクラス
     * @param propertyMetaList 取得対象のプロパティ情報
     */
    public EntityRowMapper(Class<T> entityClass, PropertyMeta[] propertyMetaList) {
        this.entityClass = entityClass;
        this.propertyMetaList = propertyMetaList;
    }

    /**
     * 任意のSQL実行時のように、取得するカラムが未定のとき
     * @param entityMeta エンティティのメタ情報
     */
    @SuppressWarnings("unchecked")
    public EntityRowMapper(EntityMeta entityMeta) {
        this.entityClass = (Class<T>)entityMeta.getEntityType();
        this.entityMeta = entityMeta;
    }

    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        final T entity = BeanUtils.instantiateClass(entityClass);

        if(propertyMetaList == null) {
            preparePropertryMetaList(rs.getMetaData());
        }

        for(int i=0; i < propertyMetaList.length; i++) {
            PropertyMeta propertyMeta = propertyMetaList[i];
            if(propertyMeta == null) {
                // 任意のSQL実行時にエンティティのプロパティにマッピング対象でないカラムがある場合ので無視する。
                continue;
            }
            Object propertyValue = propertyMeta.getValueType().getValue(rs, i + 1);
            PropertyValueInvoker.setPropertyValue(propertyMeta, entity, propertyValue);
        }

        return entity;
    }

    /**
     * プロパティ情報の準備をする。
     * @param rsmd SQLの結果セットのメタデータ
     * @throws SQLException
     */
    private void preparePropertryMetaList(final ResultSetMetaData rsmd) throws SQLException {

        final int count = rsmd.getColumnCount();

        this.propertyMetaList = new PropertyMeta[count];

        for(int i=0; i < count; i++) {
            String columnName = rsmd.getColumnName(i+1);

            Optional<PropertyMeta> propertyMeta = entityMeta.getColumnPropertyMeta(columnName);

            if(propertyMeta.isPresent()) {
                propertyMetaList[i] = propertyMeta.get();
            }

            // 任意のSQLの場合、カラム名ではなくプロパティ名で定義されている場合がある
            propertyMeta = entityMeta.getPropertyMeta(columnName);
            if(propertyMeta.isPresent()) {
                propertyMetaList[i] = propertyMeta.get();
            }
        }

    }

}
