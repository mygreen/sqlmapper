package com.github.mygreen.sqlmapper.core.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.meta.StoredPropertyMeta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ストアドプロシージャ／ファンクションのResultSetをBeanにマッピングします。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> マッピング先のクラスタイプ
 */
@Slf4j
@RequiredArgsConstructor
public class StoredResultSetRowMapper<T> implements RowMapper<T> {

    private final StoredPropertyMeta propertyMeta;

    @SuppressWarnings("unchecked")
    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        // マッピングするエンティティのインスタンスの準備
        final T bean = BeanUtils.instantiateClass((Class<T>)propertyMeta.getComponentType().get());

        final ResultSetMetaData rsmd = rs.getMetaData();
        final int columnCount = rsmd.getColumnCount();
        for(int i=0; i < columnCount; i++) {
            String columnName = rsmd.getColumnName(i+1);

            Optional<PropertyMeta> nestedPropertyMeta = propertyMeta.findNestedColumnPropertyMeta(columnName);
            if(nestedPropertyMeta.isEmpty()) {
                if(log.isDebugEnabled()) {
                    log.debug("not found nestedPropertyMeta for column name '{}' in {}.", columnName, propertyMeta.getDeclaringClass().getSimpleName());
                }
                continue;
            }

            Object propertyValue = nestedPropertyMeta.get().getValueType().getValue(rs, i+1);
            PropertyValueInvoker.setEmbeddedPropertyValue(nestedPropertyMeta.get(), bean, propertyValue);
        }

        return bean;
    }

}
