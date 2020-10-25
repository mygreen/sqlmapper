package com.github.mygreen.sqlmapper.core.query;

/**
 * 問い合わせ結果を一件ずつ反復するためのインターフェースです。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティのタイプ
 * @param <R> 戻り値のタイプ
 */
@FunctionalInterface
public interface IterationCallback<T, R> {

    /**
     * 問い合わせ結果に含まれるエンティティ１件ごとに通知されます。
     *
     * @param entity エンティティ
     * @param context 反復コンテキスト
     * @return 結果
     */
    R iterate(T entity, IterationContext context);
}
