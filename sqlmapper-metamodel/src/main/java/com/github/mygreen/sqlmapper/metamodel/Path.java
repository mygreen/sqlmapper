package com.github.mygreen.sqlmapper.metamodel;

import com.github.mygreen.sqlmapper.metamodel.expression.Expression;

/**
 * パスを表現するルートクラス
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> パスのクラスタイプ
 */
public interface Path<T> extends Expression<T> {

    /**
     * パスのメタ情報を取得します。
     * @return パスのメタ情報
     */
    PathMeta getPathMeta();



}
