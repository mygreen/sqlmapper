package com.github.mygreen.sqlmapper.core.where.simple;

import com.github.mygreen.sqlmapper.core.where.Where;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 条件式事態を保持する演算子。
 * <p>入れ子の上限式を表現する。</p>
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class WhereValueOperator implements ValueOperator {

    /**
     * 入れ子の条件式
     */
    @Getter
    private final Where where;

    @Override
    public void accept(SimpleWhereVisitor visitor) {
        visitor.visit(this);
    }
}
