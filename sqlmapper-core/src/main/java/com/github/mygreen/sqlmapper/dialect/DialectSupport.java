package com.github.mygreen.sqlmapper.dialect;

import org.springframework.lang.Nullable;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.type.ValueType;

/**
 * {@link Dialect}のベースとなるクラス。
 * 多くのDBに共通する設定はこのクラスで実装し、異なる部分を継承先で実装します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class DialectSupport implements Dialect {

    /**
     * {@inheritDoc}
     *
     * @return {@link GenerationType.TABLE} を返します。
     */
    @Override
    public GenerationType getDefaultGenerationType() {
        return GenerationType.TABLE;
    }

    @Override
    public ValueType<?> getValueType(@Nullable ValueType<?> valueType) {
        return valueType;
    }

    @Override
    public String getCountSql() {
        return "COUNT(*)";
    }

    @Override
    public String getHintComment(final String hint) {
        return "";
    }

    @Override
    public boolean isSupportedSelectForUpdate(final SelectForUpdateType type) {
        return type == SelectForUpdateType.NORMAL;
    }

    @Override
    public String getForUpdateSql(final SelectForUpdateType type, final int waitSeconds) {
        return " FOR UPDATE";
    }

}
