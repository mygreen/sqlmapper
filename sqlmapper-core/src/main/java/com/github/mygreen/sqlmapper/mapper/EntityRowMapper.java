package com.github.mygreen.sqlmapper.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;

import lombok.RequiredArgsConstructor;

/**
 * 1レコード分のエンティティをマッピングする。
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class EntityRowMapper<T> implements RowMapper<T> {

    /**
     * エンティティクラスです。
     */
    private final Class<T> entityClass;

    /**
     * マッピング対象のプロパティのリスト
     * SELECT文の指定順になっている。
     */
    private final PropertyMeta[] propertyMetaList;

    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        final T entity = BeanUtils.instantiateClass(entityClass);

        for(int i=0; i < propertyMetaList.length; i++) {
            PropertyMeta propertyMeta = propertyMetaList[i];
            Object propertyValue = propertyMeta.getValueType().getValue(rs, i + 1);
            PropertyValueInvoker.setPropertyValue(propertyMeta, entity, propertyValue);
        }

        return entity;
    }

}
