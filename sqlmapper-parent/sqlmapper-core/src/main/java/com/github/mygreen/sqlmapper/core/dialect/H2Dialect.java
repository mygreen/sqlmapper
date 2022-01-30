package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.id.CustomH2SequenceMaxValueIncrementer;

/**
 * H2用の方言の定義。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class H2Dialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@literal "h2"} を返します。
     */
    @Override
    public String getName() {
        return "h2";
    }

    /**
     * {@inheritDoc}
     *
     * @return
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
     * @return {@link CustomH2SequenceMaxValueIncrementer} のインスタンスを返します。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
//        return new H2SequenceMaxValueIncrementer(dataSource, sequenceName);
        return new CustomH2SequenceMaxValueIncrementer(dataSource, sequenceName);

    }

}
