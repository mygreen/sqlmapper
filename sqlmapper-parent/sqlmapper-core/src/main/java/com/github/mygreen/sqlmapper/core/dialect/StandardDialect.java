package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

/**
 * 標準のDBの方言定義。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class StandardDialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@literal "standard"} を返す。
     */
    @Override
    public String getName() {
        return "standard";
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal false}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal false}</li>
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
                return false;
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
     * @return シーケンスはサポートしないため {@literal null}を返す。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        // シーケンスはサポートしない
        return null;
    }

}
