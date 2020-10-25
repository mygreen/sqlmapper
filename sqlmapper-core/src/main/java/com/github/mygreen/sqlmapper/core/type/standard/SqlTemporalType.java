package com.github.mygreen.sqlmapper.type.standard;

import java.util.Date;

import com.github.mygreen.sqlmapper.annotation.Temporal;
import com.github.mygreen.sqlmapper.type.ValueType;

public interface SqlTemporalType<T extends Date> extends ValueType<T> {

    /**
     * 対応する日時型を取得します。
     * @return 対応する日時型
     */
    Temporal.TemporalType getTemporalType();
}
