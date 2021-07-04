package com.github.mygreen.sqlmapper.core.query;

import com.github.mygreen.sqlmapper.metamodel.EntityPath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * テーブル結合のエンティティ構成の定義を保持します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <E1> エンティティタイプ1
 * @param <E2> エンティティタイプ2
 */
@RequiredArgsConstructor
public class JoinAssociation<E1, E2> {

    /**
     * エンティティ情報1
     */
    @Getter
    private final EntityPath<E1> entity1;

    /**
     * エンティティ情報2
     */
    @Getter
    private final EntityPath<E2> entity2;

    /**
     * エンティティ構成の定義
     */
    @Getter
    private final Associator<E1, E2> associator;

    /**
     * テーブル結合する際のエンティティの構成を定義します。
     *
     * @author T.TSUCHIE
     *
     * @param <E1> エンティティタイプ1
     * @param <E2> エンティティタイプ2
     */
    public interface Associator<E1, E2> {

        /**
         * エンティティの構成を行います。
         * @param entity1 エンティティ1
         * @param entity2 エンティティ2
         */
        void associate(E1 entity1, E2 entity2);
    }

}
