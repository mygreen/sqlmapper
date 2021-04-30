package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;

/**
 * SQLを自動生成する削除です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public interface AutoDelete<T> {

    /**
     * バージョンプロパティを無視して削除します。
     *
     * @return このインスタンス自身
     */
    AutoDelete<T> ignoreVersion();

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    AutoDelete<T> suppresOptimisticLockException();

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    int execute();

}
