package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

public interface AutoBatchUpdate<T> {

    /**
     * バージョンプロパティを通常の更新対象に含め、バージョンチェックの対象外とします。
     * <p>
     * このメソッドが呼び出されると、<code>update</code>文の<code>where</code>句にはバージョンのチェックが含まれなくなり、
     * バージョンプロパティは通常のプロパティと同じように更新対象に含められます ({@link #excludesNull()}や{@link #changedFrom(Object)}等も同じように適用されます)。
     * </p>
     *
     * @return このインスタンス自身
     */
    AutoBatchUpdate<T> includesVersion();

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    AutoBatchUpdate<T> suppresOptimisticLockException();

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(updatable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param properties 更新対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoBatchUpdate<T> includes(PropertyPath<?>... properties);

    /**
     * 指定のプロパティを更新対象から除外します。
     *
     * @param properties 除外対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoBatchUpdate<T> excludes(PropertyPath<?>... properties);

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    int[] execute();
}
