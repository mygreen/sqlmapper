package com.github.mygreen.sqlmapper.core.where;

/**
 * Where句をSQLに変換するためのVisitor
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface WhereVisitor {

    /**
     * {@link Where} を処理します。
     * @param where 条件式
     * @throws IllegalArgumentException 不明な{@link Where}な場合にスローします。
     */
    void visit(Where where);

}
