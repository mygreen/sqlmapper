package com.github.mygreen.sqlmapper.core.type;

import com.github.mygreen.sqlmapper.core.SqlMapperException;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;

import lombok.Getter;

/**
 * 対応する{@link ValueType}が見つからない場合にストローされます。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ValueTypeNotFoundException extends SqlMapperException {

    @Getter
    private final PropertyMeta propertyMeta;

    /**
     * インスタンスを作成します。
     * @param propertyMeta プロパティのメタ情報
     * @param message メッセージ
     */
    public ValueTypeNotFoundException(PropertyMeta propertyMeta, String message) {
        super(message);
        this.propertyMeta = propertyMeta;
    }
}