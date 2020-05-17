package com.github.mygreen.sqlmapper.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.lang.Nullable;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.type.standard.BooleanType;
import com.github.mygreen.sqlmapper.type.standard.OracleBooleanType;

public class OracleDialect extends DialectSupport {

    private final OracleBooleanType objectiveBooleanType = new OracleBooleanType(false);

    private final OracleBooleanType primitiveBooleanType = new OracleBooleanType(true);

    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public boolean isSupportedGenerationType(GenerationType generationType) {
        switch(generationType) {
            case IDENTITY:
                return false;
            case SEQUENCE:
                return true;
            case TABLE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        return new OracleSequenceMaxValueIncrementer(dataSource, sequenceName);
    }

    /**
     * {@inheritDoc}
     * OracleDBの場合、{@literal boolean/Boolean}のとき、整数型に変換する {@link OracleBooleanType} に変換します。
     */
    @Override
    public ValueType<?> getValueType(@Nullable ValueType<?> valueType) {
        if(valueType == null) {
            return null;
        }

        if(valueType instanceof BooleanType) {
            if(((BooleanType)valueType).isForPrimitive()) {
                return primitiveBooleanType;
            } else {
                return objectiveBooleanType;
            }
        }
        return valueType;
    }

    @Override
    public String getHintComment(final String hint) {
        return "/*+ " + hint + " */ ";
    }

    @Override
    public String convertLimitSql(String sql, int offset, int limit) {
        StringBuilder buf = new StringBuilder(sql.length() + 100);
        sql = sql.trim();
        String lowerSql = sql.toLowerCase();
        boolean isForUpdate = false;
        if (lowerSql.endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }
        buf.append("select * from ( select temp_.*, rownum rownumber_ from ( ");
        buf.append(sql);
        buf.append(" ) temp_ ) where");
        boolean hasOffset = offset > 0;
        if (hasOffset) {
            buf.append(" rownumber_ > ");
            buf.append(offset);
        }
        if (limit > 0) {
            if (hasOffset) {
                buf.append(" and");
            }
            buf.append(" rownumber_ <= ");
            buf.append(offset + limit);
        }
        if (isForUpdate) {
            buf.append(" for update");
        }
        return buf.toString();
    }

    @Override
    public boolean isSupportedSelectForUpdate(final SelectForUpdateType type) {
        // 全てのタイプをサポートする
        return true;
    }

    @Override
    public String getForUpdateSql(final SelectForUpdateType type, final int waitSeconds) {

        StringBuilder buf = new StringBuilder(20)
                .append(" FOR UPDATE");

        switch(type) {
            case NORMAL:
                break;
            case NOWAIT:
                buf.append(" NOWAIT");
                break;
            case WAIT:
                buf.append(" WAIT ").append(waitSeconds);
                break;
        }

        return buf.toString();
    }
}
