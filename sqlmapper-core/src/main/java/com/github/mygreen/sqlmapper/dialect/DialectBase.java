package com.github.mygreen.sqlmapper.dialect;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.type.ValueType;

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

    @Override
    public ValueType<?> getValueType(PropertyMeta propertyMeta) {
        return propertyMeta.getValueType();
    }

}
