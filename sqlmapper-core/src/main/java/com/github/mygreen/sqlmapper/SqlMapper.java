package com.github.mygreen.sqlmapper;

import com.github.mygreen.sqlmapper.query.auto.AutoInsert;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * JDBCによるSQLの実行を管理するクラスです。
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class SqlMapper {

    @Getter
    private final SqlMapperContext context;

    /**
     * エンティティを挿入す。
     * @param <T> エンティティタイプ
     * @param entity エンティティのインスタンス
     * @return 挿入用のクエリ
     */
    public <T> AutoInsert<T> insert(@NonNull T entity) {
        return new AutoInsert<T>(context, entity);
    }
}
