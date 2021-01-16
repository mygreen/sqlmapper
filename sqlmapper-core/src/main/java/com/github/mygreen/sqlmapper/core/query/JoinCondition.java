package com.github.mygreen.sqlmapper.core.query;

import java.util.function.Function;

import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * テーブルの結合情報を保持します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <ENTITY> 結合先のエンティティタイプ
 */
@RequiredArgsConstructor
public class JoinCondition<ENTITY extends EntityPath<?>> {

    /**
     * テーブルの結合種別
     */
    @Getter
    private final JoinType type;

    /**
     * 結合先テーブルのエンティティ情報
     */
    @Getter
    private final ENTITY toEntity;

    /**
     * 結合条件の組み立て
     */
    @Getter
    private final Function<ENTITY, Predicate> conditioner;


}
