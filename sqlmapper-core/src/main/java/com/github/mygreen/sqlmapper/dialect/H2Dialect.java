package com.github.mygreen.sqlmapper.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;

/**
 * H2用の方言を扱うクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class H2Dialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@code h2} を返します。
     */
    @Override
    public String getName() {
        return "h2";
    }

    @Override
    public boolean isSupportedGenerationType(GenerationType generationType) {
        switch(generationType) {
            case IDENTITY:
                return true;
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
        return new H2SequenceMaxValueIncrementer(dataSource, sequenceName);
    }


}
