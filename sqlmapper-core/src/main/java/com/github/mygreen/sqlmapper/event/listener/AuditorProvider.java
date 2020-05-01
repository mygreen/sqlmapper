package com.github.mygreen.sqlmapper.event.listener;

import java.util.Optional;

/**
 * 現在のエンティティの監査人（作成者／更新者）情報を提供するためのインタフェース。
 *
 * @param <T> エンティティの監査人のクラスタイプ
 * @author T.TSUCHIE
 *
 */
public interface AuditorProvider<T> {

    /**
     * 現在の監査人を取得します。
     *
     * @return 現在の監査人。
     */
    Optional<T> getCurrentAuditor();
}
