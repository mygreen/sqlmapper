package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.operation.QueryMeta;

/**
 * サブクエリの式を
 *
 * @author T.TSUCHIE
 * @param <T> サブクエリの返す型
 *
 */
public interface SubQueryExpression<T> extends Expression<T> {

    /**
     * クエリのメタ情報を取得します。
     * @return クエリのメタ情報
     */
    QueryMeta getQueryMeta();

    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    SubQueryExpression<T> where(Predicate where);

    /**
     * ソート順を指定します。
     * @param orderBy ソートするロパティの並び順情報
     * @return 自身のインスタンス。
     */
    SubQueryExpression<T> orderBy(OrderSpecifier... orders);

    /**
     * 抽出する行数を指定します。
     * @param limit 行数
     * @return このインスタンス自身
     */
    SubQueryExpression<T> limit(int limit);

    /**
     * 抽出するデータの開始位置を指定します。
     * @param offset 開始位置。
     * @return このインスタンス自身
     */
    SubQueryExpression<T> offset(int offset);

    /**
     * 指定のプロパティのみを挿入対象とします。
     * 指定しない場合は、すべてのカラムが抽出対象となります。
     *
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    SubQueryExpression<T> includes(final PropertyPath<?>... properties);

    /**
     * SQLの {@literal EXISTS} 条件として返します。
     * @return
     */
    BooleanExpression exists();

    /**
     * SQLの {@literal NOT EXISTS} 条件として返します。
     * @return
     */
    BooleanExpression notExists();
}
