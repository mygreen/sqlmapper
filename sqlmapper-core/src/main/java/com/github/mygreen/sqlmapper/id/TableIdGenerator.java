package com.github.mygreen.sqlmapper.id;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class TableIdGenerator implements IdGenerator {

    private final TableIdIncrementer incrementer;

    /**
     * 生成する識別子のタイプ
     */
    private final Class<?> requiredType;

    /**
     * シーケンス名
     */
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
