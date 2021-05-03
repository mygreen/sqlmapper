package com.github.mygreen.sqlmapper.metamodel.support;

import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.QueryMeta;
import com.github.mygreen.sqlmapper.metamodel.operation.SubQueryOperation;

/**
 * サブクエリ式を組み立てるときのヘルパークラス。
 *
 * @author T.TSUCHIE
 *
 */
public class SubQueryHelper {

    /**
     * 抽出対象のエンティティを指定して、サブクエリのインスタンスを作成します。
     *
     * @param entityPath 抽出対象のテーブルのエンティティ
     * @return サブクエリの式
     */
    @SuppressWarnings("rawtypes")
    public static SubQueryExpression<?> from(final EntityPath<?> entityPath) {
        QueryMeta queryMeta = new QueryMeta();
        queryMeta.setEntityPath(entityPath);
        return new SubQueryOperation(queryMeta);
    }

    /**
     * 抽出対象のエンティティ（テーブル）とプロパティ（カラム）指定して、サブクエリのインスタンスを作成します。
     *
     * @param <T> 抽出対象のプロパティ（カラム）の型
     * @param entityPath 抽出対象のテーブルのエンティティ
     * @param include 抽出対象のカラムのプロパティ
     * @return サブクエリの式
     */
    public static <T> SubQueryExpression<T> from(final EntityPath<?> entityPath, final PropertyPath<T> include) {
        QueryMeta queryMeta = new QueryMeta();
        queryMeta.setEntityPath(entityPath);
        queryMeta.addInclude(include);
        return new SubQueryOperation<>(queryMeta);
    }


}
