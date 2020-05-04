package com.github.mygreen.sqlmapper.sql;

import org.springframework.beans.PropertyAccessor;

/**
 * SQLのパラメータとして渡したオブジェクトにアクセスするための {@link PropertyAccessor} のインスタンスを作成します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface PropertyAccessorFactory {

    /**
     * {@link PropertyAccessor} のインスタンスを作成します。
     * @param target アクセス対象のオブジェクト
     * @return {@link PropertyAccessor} のインスタンス
     */
    PropertyAccessor create(Object target);
}
