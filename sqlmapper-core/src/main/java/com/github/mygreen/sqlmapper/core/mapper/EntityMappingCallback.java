package com.github.mygreen.sqlmapper.core.mapper;

/**
 * {@link EntityRowMapper}のコールバック。
 * JDBCの結果をエンティティにマッピングした後に呼び出される処理です。
 *
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface EntityMappingCallback<T> {

    /**
     * コールバック処理
     * @param entity マッピングしたエンティティのインスタンス
     */
    void call(T entity);

}
