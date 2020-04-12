package com.github.mygreen.sqlmapper.id;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

/**
 * UUIDを生成します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class UUIDGenerator implements IdGenerator {

    /**
     * サポートしているクラスタイプ
     */
    private static final List<Class<?>> SUPPORTED_TYPE_LIST = List.of(UUID.class, String.class);

    private final Class<?> requiredType;

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

        UUID value = UUID.randomUUID();

        if(requiredType == UUID.class) {
            return value;

        } else if(requiredType == String.class) {
            return value.toString();
        }

        throw new DataIntegrityViolationException("not supported java type : " + requiredType.getName());
    }
}
