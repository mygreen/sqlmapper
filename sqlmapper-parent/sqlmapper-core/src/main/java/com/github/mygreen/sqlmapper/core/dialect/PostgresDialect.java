package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;


/**
 * PostgreSQL用の方言の定義
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PostgresDialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@literal "pgsql"} を返します。
     */
    @Override
    public String getName() {
        return "pgsql";
    }

    /**
     * {@inheritDoc}
     *
     * <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal true}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal true}</li>
     *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>{@link GenerationType#UUID} : {@literal true}</li>
     * </ul>
     */
    @Override
    public boolean supportsGenerationType(GenerationType generationType) {
        switch(generationType) {
            case IDENTITY:
                return true;
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
     * @return {@link PostgresSequenceMaxValueIncrementer} のインスタンスを返します。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        return new PostgresSequenceMaxValueIncrementer(dataSource, sequenceName);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal true} を返します。
     */
    @Override
    public boolean needsParameterForResultSet() {
        return true;
    }
}
