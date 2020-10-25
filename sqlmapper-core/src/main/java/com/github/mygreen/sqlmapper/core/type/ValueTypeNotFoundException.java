package com.github.mygreen.sqlmapper.type;

import com.github.mygreen.sqlmapper.SqlMapperException;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;

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
