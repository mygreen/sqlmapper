package com.github.mygreen.sqlmapper.dialect;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;

/**
 * Dialectのベース
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class DialectBase implements Dialect {

    /**
     * {@inheritDoc}
     *
     * @return {@link GenerationType.TABLE} を返します。
     */
    @Override
    public GenerationType getDefaultGenerationType() {
        return GenerationType.TABLE;
    }

}
