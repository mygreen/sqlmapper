package com.github.mygreen.sqlmapper.core.meta;

import com.github.mygreen.sqlmapper.core.SqlMapperException;

import lombok.Getter;

/**
 * エンティティ情報が不正な場合にスローされる例外です。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class InvalidEntityException extends SqlMapperException {

    /**
     * エラー対象のエンティティクラス
     */
    @Getter
    private final Class<?> entityClass;

    /**
     * インスタンスを作成する
     * @param entityClass エラー対象のエンティティクラス
     * @param message エラーメッセージ
     */
    public InvalidEntityException(final Class<?> entityClass, final String message) {
        super(message);
        this.entityClass = entityClass;
    }
}
