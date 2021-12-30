package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Collection;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostBatchDeleteEvent;
import com.github.mygreen.sqlmapper.core.event.PreBatchDeleteEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;

import lombok.Getter;
import lombok.NonNull;


/**
 * バッチ削除を行うSQLを自動生成するクエリの実装です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoBatchDeleteImpl<T> implements AutoBatchDelete<T> {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    @Getter
    private final T[] entities;

    /**
     * エンティティ情報
     */
    @Getter
    private final EntityMeta entityMeta;

    @Getter
    private Integer queryTimeout;

    /**
     * バージョンプロパティを無視して削除するかどうか。
     */
    @Getter
    private boolean ignoreVersion = false;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter
    private boolean suppresOptimisticLockException = false;

    public AutoBatchDeleteImpl(@NonNull SqlMapperContext context, @NonNull T[] entities) {
        this.context = context;

        if(entities.length == 0) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notEmptyEntity")
                    .format());
        }

        this.entities = entities;
        this.entityMeta = context.getEntityMetaFactory().create(entities[0].getClass());

        validateTarget();
    }

    @SuppressWarnings("unchecked")
    public AutoBatchDeleteImpl(@NonNull SqlMapperContext context, @NonNull Collection<T> entities) {
        this(context, (T[])entities.toArray());
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

    /**
     * インデックスを指定して処理対象のエンティティを取得します。
     * @param index インデックス
     * @return エンティティ
     */
    public T getEntity(final int index) {
        return entities[index];
    }

    /**
     * 処理対象のエンティティの個数を取得します。
     * @return エンティティの個数
     */
    public int getEntitySize() {
        return entities.length;
    }

    @Override
    public AutoBatchDeleteImpl<T> queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public AutoBatchDeleteImpl<T> ignoreVersion() {
        this.ignoreVersion = true;
        return this;
    }

    @Override
    public AutoBatchDeleteImpl<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    @Override
    public int execute() {

        context.getApplicationEventPublisher().publishEvent(new PreBatchDeleteEvent(this, entityMeta, entities));

        final int result = new AutoBatchDeleteExecutor(this)
                .execute();

        context.getApplicationEventPublisher().publishEvent(new PostBatchDeleteEvent(this, entityMeta, entities));
        return result;

    }
}
