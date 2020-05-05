/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.github.mygreen.sqlmapper.sql.node;

import org.springframework.beans.PropertyAccessor;
import org.springframework.core.style.ToStringCreator;

import com.github.mygreen.sqlmapper.sql.SqlContext;
import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.Getter;

/**
 * （コメントによる定義の）バインド変数のための{@link Node}です。
 *
 * @author higa
 * @author T.TSUCHIE
 */
public class BindVariableNode extends AbstractNode {

    /**
     * 式
     */
    @Getter
    private final String expression;

    /**
     * Creates a <code>BindVariableNode</code> from a string expression.
     *
     * @param expression string expression
     */
    public BindVariableNode(final String expression) {
        this.expression = expression;
    }

	@SuppressWarnings("rawtypes")
    @Override
    public void accept(final SqlContext ctx) {

	    final PropertyAccessor accessor = ctx.getPropertyAccessor();
	    Object value = accessor.getPropertyValue(expression);
	    Class<?> clazz = accessor.getPropertyType(expression);

        final String paramName = ctx.createArgName();
	    ValueType valueType = ctx.getValueTypeRegistry().findValueType(clazz, expression);
	    ctx.addSql(":" + paramName, value, paramName, valueType);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("expression", expression)
                .append("children", children)
                .toString();
    }
}