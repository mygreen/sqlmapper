package com.github.mygreen.sqlmapper.metamodel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * パスの並び順
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
}
