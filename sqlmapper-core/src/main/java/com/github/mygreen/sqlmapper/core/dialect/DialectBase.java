package com.github.mygreen.sqlmapper.core.dialect;

import org.springframework.lang.Nullable;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.core.type.ValueType;

/**
 * {@link Dialect}のベースとなるクラス。
 * 多くのDBに共通する設定はこのクラスで実装し、異なる部分を継承先で実装します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class DialectBase implements Dialect {

    /**
     * {@inheritDoc}
     *
     * @return {@link GenerationType#TABLE} を返します。
     */
    @Override
    public GenerationType getDefaultGenerationType() {
        return GenerationType.TABLE;
    }

    @Override
    public ValueType<?> getValueType(@Nullable ValueType<?> valueType) {
        return valueType;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal "count(*)"} を返します。
     */
    @Override
    public String getCountSql() {
        return "count(*)";
    }

    /**
     * {@inheritDoc}
     *
     * @return 空文字({@literal ""})を返します。
     */
    @Override
    public String getHintComment(final String hint) {
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link SelectForUpdateType#NORMAL} を返します。
     */
    @Override
    public boolean isSupportedSelectForUpdate(final SelectForUpdateType type) {
        return type == SelectForUpdateType.NORMAL;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal "for update"} を返します。
     */
    @Override
    public String getForUpdateSql(final SelectForUpdateType type, final int waitSeconds) {
        return " for update";
    }

    @Override
    public String convertLimitSql(String sql, int offset, int limit) {

        StringBuilder buf = new StringBuilder(sql.length() + 20);
        buf.append(sql);
        if (offset > 0) {
            buf.append(" limit ");
            buf.append(limit);
            buf.append(" offset ");
            buf.append(offset);
        } else {
            buf.append(" limit ");
            buf.append(limit);
        }

        return buf.toString();

    }

}
