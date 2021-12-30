package com.github.mygreen.sqlmapper.core.query.auto;

import com.github.mygreen.sqlmapper.metamodel.Predicate;

/**
 * 任意の条件を指定して削除を行うSQLを自動生成するクエリです。
 *
 * @version 0.3
 * @author T.TSUCHIE
 * @param <T> 処理対象となるエンティティの型
 *
 */
public interface AutoAnyDelete<T> {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    AutoAnyDelete<T> queryTimeout(int seconds);

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
