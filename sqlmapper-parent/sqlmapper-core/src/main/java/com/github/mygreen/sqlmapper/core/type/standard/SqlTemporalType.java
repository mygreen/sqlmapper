package com.github.mygreen.sqlmapper.core.type.standard;

import java.util.Date;

import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.type.ValueType;

/**
 * {@link Date}を継承している時制の型を表すインタフェース。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 * @param <T> {@link Date}を継承している時制の型
 */
public interface SqlTemporalType<T extends Date> extends ValueType<T> {

    /**
     * 対応する日時型を取得します。
     * @return 対応する日時型
     */
    Temporal.TemporalType getTemporalType();

    /**
     * {@literal java.util.Date} 型を対応する時制型に変換します。
     *
     * @since 0.3
     * @param utilDate 変換対象の日時型
     * @return 対応する時制オブジェクト。
     */
    T convertTo(Date utilDate);
}
