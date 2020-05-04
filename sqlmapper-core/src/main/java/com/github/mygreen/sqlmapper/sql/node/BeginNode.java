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

import org.springframework.core.style.ToStringCreator;

import com.github.mygreen.sqlmapper.sql.SqlContext;
import com.github.mygreen.sqlmapper.sql.SqlContextImpl;

/**
 * {@code BGEIN} コメントに対応する{@link Node}です。
 *
 * @author higa
 */
public class BeginNode extends ContainerNode {

    public BeginNode() {
    }

    @Override
    public void accept(final SqlContext ctx) {
        SqlContext childCtx = new SqlContextImpl(ctx);
        super.accept(childCtx);
        if (childCtx.isEnabled()) {
            ctx.addSql(childCtx.getSql(), childCtx.getBindVariables(), childCtx.getBindVariableTypes());
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("children", children)
                .toString();
    }
}