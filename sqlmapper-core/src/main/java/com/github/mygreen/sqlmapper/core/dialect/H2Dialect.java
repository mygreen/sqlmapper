package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

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
     * @return {@literal h2} を返します。
     */
    @Override
    public String getName() {
        return "h2";
    }

    /**
     * {@inheritDoc}
     *
     * <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal true}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal true}</li>
     *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>その他 : {@literal false}</li>
     * </ul>
     */
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

    /**
     * {@inheritDoc}
     *
     * @return {@link H2SequenceMaxValueIncrementer} のインスタンスを返します。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        return new H2SequenceMaxValueIncrementer(dataSource, sequenceName);
    }

    @Override
    public String convertLimitSql(String sql, int offset, int limit) {

        StringBuilder buf = new StringBuilder(sql.length() + 20);
        buf.append(sql);
        if (offset > 0) {
            buf.append(" LIMIT ");
            buf.append(limit);
            buf.append(" OFFSET ");
            buf.append(offset);
        } else {
            buf.append(" LIMIT ");
            buf.append(limit);
        }

        return buf.toString();

    }


}
