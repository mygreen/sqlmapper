package com.github.mygreen.sqlmapper.core.id;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

import lombok.RequiredArgsConstructor;

/**
 * {@link GenerationType#UUID}方式でIDの値を自動生成するIDジェネレータです。
 * <p>サポートするIDのクラスタイプは、{@link UUID} / {@link String} です。
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

    /**
     * 生成するIDのクラスタイプ
     */
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
    public Object generateValue(final IdGenerationContext context) {

        UUID value = UUID.randomUUID();

        if(requiredType == UUID.class) {
            return value;

        } else if(requiredType == String.class) {
            return value.toString();
        }

        throw new DataIntegrityViolationException("not supported java type : " + requiredType.getName());
    }
}
