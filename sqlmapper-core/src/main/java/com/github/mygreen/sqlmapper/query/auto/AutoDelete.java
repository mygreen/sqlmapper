package com.github.mygreen.sqlmapper.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.QueryBase;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AutoDelete<T> extends QueryBase<T> {

    /**
     * 削除対象のエンティティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final T entity;

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

    public AutoDelete(@NonNull SqlMapperContext context, @NonNull T entity) {
        super(context);
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());

        // 処理対象の情報の整合性などのチェックを行う
        validateTarget();
    }

    private void validateTarget() {
        // 主キーを持つかどうかのチェック
        if(entityMeta.getIdPropertyMetaList().isEmpty()) {
            throw new IllegalOperateException(context.getMessageBuilder().create("query.requiredId")
                    .varWithClass("entityType", entityMeta.getEntityType())
                    .format());
        }
    }

    /**
     * バージョンプロパティを無視して削除します。
     *
     * @return このインスタンス自身
     */
    public AutoDelete<T> ignoreVersion() {
        this.ignoreVersion = true;
        return this;
    }

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    public AutoDelete<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("executeDelete");

        AutoDeleteExecutor executor = new AutoDeleteExecutor(this);
        try {
            executor.prepare();
            return executor.execute();

        } finally {
            completed();
        }

    }

}
