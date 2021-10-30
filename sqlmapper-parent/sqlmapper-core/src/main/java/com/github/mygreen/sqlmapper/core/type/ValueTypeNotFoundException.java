package com.github.mygreen.sqlmapper.core.type;

import com.github.mygreen.sqlmapper.core.SqlMapperException;
import com.github.mygreen.sqlmapper.core.meta.PropertyBase;

import lombok.Getter;

/**
 * 対応する{@link ValueType}が見つからない場合にスローされる例外です。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class ValueTypeNotFoundException extends SqlMapperException {

    /**
     * エラー対象のプロパティ情報です。
     */
    @Getter
    private final PropertyBase property;

    /**
     * インスタンスを作成します。
     * @param property プロパティ情報
     * @param message メッセージ
     */
    public ValueTypeNotFoundException(PropertyBase property, String message) {
        super(message);
        this.property = property;
    }
}
