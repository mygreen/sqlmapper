package com.github.mygreen.sqlmapper.core.dialect;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

/**
 * 古いOracleDBの方言です。
 * <p>Oracle11以前が対象です。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class OracleLegacyDialect extends OracleDialect {

    /**
     * {@inheritDoc}
     *
     * @return <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal false}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal true}</li>
     *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>{@link GenerationType#UUID} : {@literal true}</li>
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
            case UUID:
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal ROWNUMBER}を使用し、疑似的にLIMIT句を表現します。
     * @throws IllegalArgumentException 引数{@literal offset} または {@literal limit} の値の何れかが 0より小さい場合にスローされます。
     */
    @Override
    public String convertLimitSql(String sql, int offset, int limit) {

        if(offset < 0 && limit < 0) {
            throw new IllegalArgumentException("Either offset or limit should be greather than 0.");
        }

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

        if(offset >= 0 && limit >= 0) {
            buf.append(" rownumber_ between ")
                .append(offset + 1)
                .append(" and ")
                .append(offset + limit);

        } else if(offset >= 0) {
            buf.append(" rownumber_ >= ")
                .append(offset + 1);

        } else if(limit >= 0) {
            buf.append(" rownumber_ <= ")
                .append(limit);
        }

        if (isForUpdate) {
            buf.append(" for update");
        }

        return buf.toString();
    }

}
