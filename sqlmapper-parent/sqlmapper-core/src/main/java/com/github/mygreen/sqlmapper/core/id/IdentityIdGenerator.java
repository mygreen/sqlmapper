package com.github.mygreen.sqlmapper.core.id;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * IDENTITYによる生成は、実際にはJdbcTemplateで行います。
 * <p>このクラスでは、{@link #generateValue(Number)} による変換だけ行います。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class IdentityIdGenerator implements IdGenerator {

    /**
     * サポートしているクラスタイプ
     */
    private static final List<Class<?>> SUPPORTED_TYPE_LIST = List.of(
            long.class, Long.class, int.class, Integer.class, String.class);

    /**
     * 生成する識別子のタイプ
     */
    private final Class<?> requiredType;

    /**
     * 文字列にマッピングするときのフォーマッター
     */
    @Setter
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
     * @throws UnsupportedOperationException このメソッドを呼び出したときに必ずスローされます。
     */
    @Override
    public Object generateValue() {
        throw new UnsupportedOperationException("this method is not supported.");
    }

    /**
     * 生成したIDをプロパティのクラス型に変換する。
     * @param value 生成したID
     * @return プロパティのクラス型に変換したID。
     * @throws DataIntegrityViolationException サポート対象外のクラスタイプの場合にスローされます。
     */
    public Object generateValue(final Number value) {

        if(requiredType == long.class || requiredType == Long.class
                || requiredType == int.class || requiredType == Integer.class) {
            return NumberConvertUtils.convertNumber(requiredType, value);

        } else if(requiredType == String.class) {

            if(formatter == null) {
                return value.toString();
            } else {
                synchronized (formatter) {
                    return formatter.format(value);
                }
            }

        }

        throw new DataIntegrityViolationException("not supported java type : " + requiredType.getName());

    }
}
