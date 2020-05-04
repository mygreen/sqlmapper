package com.github.mygreen.sqlmapper.sql;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyEditorRegistry;

/**
 * SQLのパラメータとして渡したオブジェクトにアクセスするための {@link PropertyAccessor} のインスタンスを作成します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface PropertyAccessorFactory<T extends PropertyAccessor&PropertyEditorRegistry> {

    /**
     * {@link PropertyAccessor} のインスタンスを作成します。
     * @param target アクセス対象のオブジェクト
     * @return {@link PropertyAccessor} のインスタンス
     */
    T create(Object target);
}
