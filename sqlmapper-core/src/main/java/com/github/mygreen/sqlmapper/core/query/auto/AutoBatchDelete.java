package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;

/**
 * バッチ削除を行うSQLを自動生成するクエリです。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T>
 */
public interface AutoBatchDelete<T> {

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
