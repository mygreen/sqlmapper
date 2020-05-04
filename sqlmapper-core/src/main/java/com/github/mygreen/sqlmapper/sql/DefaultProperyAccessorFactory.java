package com.github.mygreen.sqlmapper.sql;

import java.util.Map;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;

/**
 * 標準の{@link PropertyAccessorFactory}の実装。
 * 基本的には、{@link DirectFieldAccessor} を使用し、アクセッサメソッドがなくても値の参照ができるようにする。
 * また、{@link Map}を渡された場合は、{@link MapAcessor}を使用し、キー要素へのアクセス{@literal map[key]}を省略し、
 * {@literal key} だけでアクセスできるようにする。
 *
 *
 *
 * @author T.TSUCHIE
 *
 */
public class DefaultProperyAccessorFactory implements PropertyAccessorFactory {

    @SuppressWarnings("unchecked")
    @Override
    public PropertyAccessor create(final Object target) {

        if(Map.class.isAssignableFrom(target.getClass())) {
            return new MapAcessor((Map<String, Object>)target);
        }

        return new DirectFieldAccessor(target);
    }
}
