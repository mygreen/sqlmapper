package com.github.mygreen.sqlmapper.core.id;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * {@link GenerationType#SEQUENCE}方式でIDの値を自動生成するIDジェネレータです。
 * <p>サポートするIDのクラスタイプは、{@code long/Long/int/Integer/String}です。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class SequenceIdGenerator implements IdGenerator {

    /**
     * サポートしているクラスタイプ
     */
    private static final List<Class<?>> SUPPORTED_TYPE_LIST = List.of(
            long.class, Long.class, int.class, Integer.class, String.class);

    /**
     * シーケンスをインクリメント処理します
     */
    @Getter
    private final DataFieldMaxValueIncrementer incrementer;

    /**
     * 生成するIDのタイプ
     */
    @Getter
    private final Class<?> requiredType;

    /**
     * 文字列にマッピングするときのフォーマッター
     */
    @Setter
    @Getter
    private NumberFormat formatter;

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
        if(requiredType == long.class || requiredType == Long.class) {
            return incrementer.nextLongValue();

        } else if(requiredType == int.class || requiredType == Integer.class) {
            return incrementer.nextIntValue();

        } else if(requiredType == String.class) {
            if(formatter == null) {
                return incrementer.nextStringValue();
            } else {
                synchronized (formatter) {
                    return formatter.format(incrementer.nextLongValue());
                }
            }

        }

        throw new DataIntegrityViolationException("not supported java type : " + requiredType.getName());

    }

}
