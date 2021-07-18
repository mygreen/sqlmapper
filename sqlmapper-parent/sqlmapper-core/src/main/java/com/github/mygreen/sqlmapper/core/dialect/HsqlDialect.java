package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HsqlSequenceMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

/**
 * HSQL用の方言の定義。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class HsqlDialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@literal "hsql"} を返します。
     */
    @Override
    public String getName() {
        return "hsql";
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
     * @return {@link HsqlSequenceMaxValueIncrementer} のインスタンスを返します。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        return new HsqlSequenceMaxValueIncrementer(dataSource, sequenceName);
    }

}
