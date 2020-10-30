package com.github.mygreen.sqlmapper.core.where.simple;

/**
 * 値を伴う演算子を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface ValueOperator {

    /**
     * 条件分を組み立てるVisitorを受け入れます。
     * @param visitor 条件分を組み立てるVisitor
     */
    void accept(SimpleWhereVisitor visitor);
}
