package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;

/**
 * バッチ削除を行うSQLを自動生成するクエリです。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public interface AutoBatchDelete<T> {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    AutoBatchDelete<T> queryTimeout(int seconds);

    /**
     * バージョンプロパティを無視して削除します。
     *
     * @return このインスタンス自身
     */
    AutoBatchDelete<T> ignoreVersion();

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    AutoBatchDelete<T> suppresOptimisticLockException();

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    int execute();

}
