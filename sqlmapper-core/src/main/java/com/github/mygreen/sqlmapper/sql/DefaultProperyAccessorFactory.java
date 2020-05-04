package com.github.mygreen.sqlmapper.sql;

import java.util.Map;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;

/**
 * デフォルトの{@link PropertyAccessorFactory}。
 *
 * @author T.TSUCHIE
 *
 */
public class DefaultProperyAccessorFactory implements PropertyAccessorFactory {

    @SuppressWarnings("unchecked")
    @Override
    public PropertyAccessor create(final Object target) {

        if(Map.class.isAssignableFrom(target.getClass())) {
            return new RootMapAcessor((Map<String, Object>)target);
        }

        return new DirectFieldAccessor(target);
    }
}
