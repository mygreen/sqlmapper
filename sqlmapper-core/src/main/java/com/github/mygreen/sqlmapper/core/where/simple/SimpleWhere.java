package com.github.mygreen.sqlmapper.core.where.simple;

import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

/**
 * 入力された項目を {@literal AND} でつなげていく条件を組み立てるクラスです。
 *
 * @author T.TSUCHIE
 *
 */
public class SimpleWhere extends AbstractWhere<SimpleWhere> implements Where {

    @Override
    public void accept(WhereVisitor visitor) {
        visitor.visit(this);
    }
}
