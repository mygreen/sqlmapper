package com.github.mygreen.sqlmapper.query.auto;

import java.util.Collection;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.event.PostBatchDeleteEvent;
import com.github.mygreen.sqlmapper.event.PreBatchDeleteEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.QueryBase;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AutoBatchDelete<T> extends QueryBase<T> {

    @Getter(AccessLevel.PACKAGE)
    private final T[] entities;

    /**
     * エンティティ情報
     */
    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * バージョンプロパティを無視して削除するかどうか。
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean ignoreVersion = false;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean suppresOptimisticLockException = false;

    public AutoBatchDelete(@NonNull SqlMapperContext context, @NonNull T[] entities) {
        super(context);

        if(entities.length == 0) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notEmptyEntity")
                    .format());
        }

        this.entities = entities;
        this.entityMeta = context.getEntityMetaFactory().create(entities[0].getClass());

        validateTarget();
    }

    @SuppressWarnings("unchecked")
    public AutoBatchDelete(@NonNull SqlMapperContext context, @NonNull Collection<T> entities) {
        this(context, (T[])entities.toArray());
    }

    private void validateTarget() {
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
    T getEntity(final int index) {
        return entities[index];
    }

    /**
     * 処理対象のエンティティの個数を取得します。
     * @return エンティティの個数
     */
    int getEntitySize() {
        return entities.length;
    }

    /**
     * バージョンプロパティを無視して削除します。
     *
     * @return このインスタンス自身
     */
    public AutoBatchDelete<T> ignoreVersion() {
        this.ignoreVersion = true;
        return this;
    }

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    public AutoBatchDelete<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("executeBatchDelete");
        context.getApplicationEventPublisher().publishEvent(new PreBatchDeleteEvent(this, entityMeta, entities));

        final AutoBatchDeleteExecutor executor = new AutoBatchDeleteExecutor(this);
        try {
            executor.prepare();
            final int result = executor.execute();

            context.getApplicationEventPublisher().publishEvent(new PostBatchDeleteEvent(this, entityMeta, entities));
            return result;

        } finally {
            completed();
        }

    }
}
