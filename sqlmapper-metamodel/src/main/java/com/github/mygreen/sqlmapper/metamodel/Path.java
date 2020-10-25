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
     * @return パスのメタ情報
     */
    PathMeta getPathMeta();



}
