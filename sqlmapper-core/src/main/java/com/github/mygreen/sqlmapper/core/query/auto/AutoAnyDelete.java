package com.github.mygreen.sqlmapper.core.query.auto;

import com.github.mygreen.sqlmapper.metamodel.Predicate;

/**
 * 任意の条件で削除を行うSQLを自動生成するクエリ。
 *
 * @author T.TSUCHIE
 * @param <T> 処理対象となるエンティティの型
 *
 */
public interface AutoAnyDelete<T> {

    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    AutoAnyDelete<T> where(Predicate where);

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    int execute();
}
