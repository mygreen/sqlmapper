package com.github.mygreen.sqlmapper.core.where;

/**
 * Where句を組み立てるインタフェースです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface Where {

    /**
     * 条件分を組み立てるVisitorを受け入れます。
     * @param visitor 条件分を組み立てるVisitor
     */
    void accept(WhereVisitor visitor);
}
