package com.github.mygreen.sqlmapper.core.where.simple;

/**
 * 式の項を表現するインタフェース。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface Term {

    /**
     * 条件分を組み立てるVisitorを受け入れます。
     * @param visitor 条件分を組み立てるVisitor
     */
    void accept(SimpleWhereVisitor visitor);
}
