package com.github.mygreen.sqlmapper.core.where.simple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WhereTerm implements Term {

    @Getter
    private final AbstractWhere<?> where;

    @Override
    public void accept(SimpleWhereVisitor visitor) {
        visitor.visit(this);
    }
}
