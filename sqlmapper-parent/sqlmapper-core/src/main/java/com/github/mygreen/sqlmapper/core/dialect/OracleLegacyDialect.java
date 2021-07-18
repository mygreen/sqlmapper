package com.github.mygreen.sqlmapper.core.dialect;

import org.springframework.lang.Nullable;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.type.standard.BooleanType;
import com.github.mygreen.sqlmapper.core.type.standard.NumberableBooleanType;

/**
 * 古いOracleDBの方言です。
 * <p>Oracle11以前が対象です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class OracleLegacyDialect extends OracleDialect {

    private final NumberableBooleanType objectiveBooleanType = new NumberableBooleanType(false);

    private final NumberableBooleanType primitiveBooleanType = new NumberableBooleanType(true);

    /**
     * {@inheritDoc}
     *
     * @return <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal false}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal true}</li>
     *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>その他 : {@literal false}</li>
     * </ul>
     */
    @Override
    public boolean supportsGenerationType(GenerationType generationType) {
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

    /**
     * {@inheritDoc}
     *
     * @return 与えられた値が {@literal boolean/Boolean}のとき、整数型に変換する {@link NumberableBooleanType} に変換します。
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

    /**
     * {@inheritDoc}
     *
     * @return {@literal ROWNUMBER}を使用し、疑似的にLIMIT句を表現します。
     */
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

}
