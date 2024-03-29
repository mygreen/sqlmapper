package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostDeleteEvent;
import com.github.mygreen.sqlmapper.core.event.PreDeleteEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;

import lombok.Getter;
import lombok.NonNull;

/**
 * 削除を行うSQLを自動生成するクエリの実装です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoDeleteImpl<T> implements AutoDelete<T> {

    /**
     * SqlMapperの設定情報
     */
    @Getter
    private final SqlMapperContext context;

    /**
     * 削除対象のエンティティ
     */
    @Getter
    private final T entity;

    /**
     * エンティティ情報
     */
    @Getter
    private final EntityMeta entityMeta;

    /**
     * クエリのタイムアウト時間 [msec]
     */
    @Getter
    private Integer queryTimeout;

    /**
     * バージョンプロパティを無視して削除するかどうか。
     */
    @Getter
    private boolean ignoreVersion = false;

    /**
     * バージョンチェックを行った場合に、削除行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter
    private boolean suppresOptimisticLockException = false;

    public AutoDeleteImpl(@NonNull SqlMapperContext context, @NonNull T entity) {
        this.context = context;
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());

        // 処理対象の情報の整合性などのチェックを行う
        validateTarget();
    }

    private void validateTarget() {

        // 読み取り専用かどうかのチェック
        if(entityMeta.getTableMeta().isReadOnly()) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.readOnlyEntity")
                    .paramWithClass("entityType", entityMeta.getEntityType())
                    .format());
        }

        // 主キーを持つかどうかのチェック
        if(entityMeta.getIdPropertyMetaList().isEmpty()) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.requiredId")
                    .paramWithClass("entityType", entityMeta.getEntityType())
                    .format());
        }
    }

    @Override
    public AutoDeleteImpl<T> queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public AutoDeleteImpl<T> ignoreVersion() {
        this.ignoreVersion = true;
        return this;
    }

    @Override
    public AutoDeleteImpl<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    @Override
    public int execute() {

        context.getApplicationEventPublisher().publishEvent(new PreDeleteEvent(this, entityMeta, entity));

        final int result = new AutoDeleteExecutor(this)
                .execute();

        context.getApplicationEventPublisher().publishEvent(new PostDeleteEvent(this, entityMeta, entity));
        return result;

    }

}
