package com.github.mygreen.sqlmapper.core.query.auto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.SqlMapperException;
import com.github.mygreen.sqlmapper.core.mapper.SingleColumnRowMapper;
import com.github.mygreen.sqlmapper.core.mapper.StoredResultSetRowMapper;
import com.github.mygreen.sqlmapper.core.meta.StoredParamMeta;
import com.github.mygreen.sqlmapper.core.meta.StoredPropertyMeta;
import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * ストアド（プロシージャ／ファンクション）のサポートクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public abstract class AutoStoredExecutorSupport {

    protected final SqlMapperContext context;

    /**
     * ストアド実行時のSQLパラメータタイプを組み立てます。
     * @param paramMeta パラメータ情報。
     * @return SQLのパラメータ情報
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected SqlParameter[] createSqlParameterTypes(final StoredParamMeta paramMeta) {

        List<SqlParameter> paramTypes = new ArrayList<>();

        if(paramMeta.isAnonymouse()) {
            paramTypes.add(new SqlParameter(paramMeta.getValueType().get().getSqlType(context.getDialect())));

        } else {
            for(StoredPropertyMeta propertyMeta : paramMeta.getAllPropertyMeta()) {
                if(propertyMeta.isIn()) {
                    ValueType<?> valueType = propertyMeta.getValueType();
                    paramTypes.add(new SqlParameter(propertyMeta.getName(), valueType.getSqlType(context.getDialect())));

                } else if(propertyMeta.isOut()) {
                    ValueType<?> valueType = propertyMeta.getValueType();
                    paramTypes.add(new SqlOutParameter(propertyMeta.getName(), valueType.getSqlType(context.getDialect()),
                            new RowMapper<Object>() {

                                @Override
                                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return valueType.getValue(rs, rs.findColumn(propertyMeta.getName()));
                                }
                            }));

                } else if(propertyMeta.isInOut()) {
                    ValueType<?> valueType = propertyMeta.getValueType();
                    paramTypes.add(new SqlInOutParameter(propertyMeta.getName(), valueType.getSqlType(context.getDialect()),
                            new RowMapper<Object>() {

                                @Override
                                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return valueType.getValue(rs, rs.findColumn(propertyMeta.getName()));
                                }
                            }));

                } else if(propertyMeta.isResultSet()) {
                    if(propertyMeta.isSingleValue()) {
                        paramTypes.add(new SqlReturnResultSet(propertyMeta.getName(),
                                new SingleColumnRowMapper(propertyMeta.getValueType())));
                    } else {
                        // JavaBeanの形式の場合
                        paramTypes.add(new SqlReturnResultSet(propertyMeta.getName(),
                                new StoredResultSetRowMapper(propertyMeta)));
                    }

                }
            }

        }

        return paramTypes.toArray(new SqlParameter[paramTypes.size()]);

    }

    /**
     * クエリ実行時のパラメータの値を作成します。
     * @param paramMeta パラメータ情報。
     * @param parameter パラメータオブジェクト。{@literal null} は許可しません。
     * @return クエリ実行時に渡すパラメータの値。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Object[] createParameterValues(final StoredParamMeta paramMeta, @NonNull final Object parameter) {

        List<Object> paramValues = new ArrayList<>();

        if(paramMeta.isAnonymouse()) {
            ValueType valueType = paramMeta.getValueType().get();
            paramValues.add(valueType.getSqlParameterValue(parameter));
        } else {
            for(StoredPropertyMeta propertyMeta : paramMeta.getAllPropertyMeta()) {
                if(propertyMeta.isIn() || propertyMeta.isInOut()) {
                    ValueType valueType = propertyMeta.getValueType();
                    paramValues.add(valueType.getSqlParameterValue(propertyMeta.getPropertyValue(parameter)));

                }
            }
        }

        return paramValues.toArray(new Object[paramValues.size()]);

    }

    /**
     * パラメータにOUT/INOUT/ResultSetパラメータが含まれるかどうか
     * @return
     */
    protected boolean containsResultParam(final StoredParamMeta paramMeta, final Optional<Object> parameter) {
        if(parameter.isEmpty()) {
            return false;
        }

        for(StoredPropertyMeta propertyMeta : paramMeta.getAllPropertyMeta()) {
            if(propertyMeta.isInOut() || propertyMeta.isOut() || propertyMeta.isResultSet()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 戻り値を処理します。
     * @param paramMeta パラメータ情報。
     * @param parameter パラメータオブジェクト。{@literal null} は許可しません。
     * @param out クエリ実行時の結果マップ。
     */
    @SuppressWarnings({"rawtypes", "unused"})
    protected void doResultValue(final StoredParamMeta paramMeta, @NonNull final Object parameter, final Map<String, Object> out) {

        for(StoredPropertyMeta propertyMeta : paramMeta.getAllPropertyMeta()) {
            if(propertyMeta.isOut() || propertyMeta.isInOut() || propertyMeta.isResultSet()) {
                ValueType valueType = propertyMeta.getValueType();
                Object value = out.get(propertyMeta.getName());
                if(value == null) {
                    continue;
                }

                if(propertyMeta.isSingleValue()) {
                    if((value instanceof List) && !List.class.isAssignableFrom(propertyMeta.getPropertyType())) {
                        // DBの戻り値はリストだが、マッピング先はリストでない場合、1つ目の要素をマッピングする。
                        propertyMeta.setPropertyValue(parameter, ((List)value).get(0));
                    } else {
                        propertyMeta.setPropertyValue(parameter, value);
                    }
                } else if(propertyMeta.getPropertyType().isAssignableFrom(value.getClass())) {
                    // 値が、マッピング先の子クラスの場合はそのまま設定する
                    propertyMeta.setPropertyValue(parameter, value);
                } else {
                    // マッピング先のタイプと取得元のタイプが一致しない場合
                    throw new SqlMapperException(context.getMessageFormatter().create("storedResult.notMatchType")
                            .paramWithClass("paramType", paramMeta.getParamType())
                            .param("property", propertyMeta.getName())
                            .paramWithClass("propertyType", propertyMeta.getPropertyType())
                            .paramWithClass("resultSetType", value.getClass())
                            .format());
                }
            }

        }
    }

}
