package com.github.mygreen.sqlmapper.core.id;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * {@link GenerationType#TABLE}方式でIDの値を自動生成するIDジェネレータです。
 * <p>サポートするIDのクラスタイプは、{@code long/Long/int/Integer/String}です。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class TableIdGenerator implements IdGenerator {

    private final TableIdIncrementer incrementer;

    /**
     * 生成するIDのタイプ
     */
    @Getter
    private final Class<?> requiredType;

    /**
     * シーケンス名
     */
    @Getter
    private final String sequenceName;

    /**
     * 文字列にマッピングするときのフォーマッター
     */
    @Setter
    private NumberFormat formatter;

    /**
     * サポートしているクラスタイプ
     */
    private static List<Class<?>> SUPPORTED_TYPE_LIST = List.of(
            long.class, Long.class, int.class, Integer.class, String.class);

    @Override
    public boolean isSupportedType(Class<?> type) {
        return SUPPORTED_TYPE_LIST.contains(type);
    }

    @Override
    public Class<?>[] getSupportedTypes() {
        return SUPPORTED_TYPE_LIST.toArray(new Class[SUPPORTED_TYPE_LIST.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @throws DataIntegrityViolationException コンストラクタで指定された引数 {@literal requiredType} がサポート対象外の場合。
     */
    @Override
    public Object generateValue() {
        long value = incrementer.nextValue(sequenceName);

        if(requiredType == long.class || requiredType == Long.class) {
            return value;

        } else if(requiredType == int.class || requiredType == Integer.class) {
            return Long.valueOf(value).intValue();

        } else if(requiredType == String.class) {
            if(formatter == null) {
                return String.valueOf(value);
            } else {
                synchronized (formatter) {
                    return formatter.format(value);
                }
            }

        }

        throw new DataIntegrityViolationException("not supported java type : " + requiredType.getName());

    }
}
