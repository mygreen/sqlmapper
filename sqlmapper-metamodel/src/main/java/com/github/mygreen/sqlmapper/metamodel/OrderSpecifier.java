package com.github.mygreen.sqlmapper.metamodel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * パスの並び順を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class OrderSpecifier {

    /**
     * 並び順
     */
    @Getter
    private final OrderType order;

    /**
     * 対象のパス
     */
    @Getter
    private final Path<?> path;

    /**
     * {@inheritDoc}
     * {@literal 対象のパスの評価結果 並び順}
     */
    public String toString() {
        return path.toString() + " " + order.name();
    }
}
