package com.github.mygreen.sqlmapper.query;

import com.github.mygreen.sqlmapper.SqlMapperContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * クエリを組み立てるためのサポートクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 *
 */
@RequiredArgsConstructor
public abstract class QuerySupport<T> {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    protected final SqlMapperContext context;

}
