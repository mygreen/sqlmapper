package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostBatchUpdateEvent;
import com.github.mygreen.sqlmapper.core.event.PreBatchUpdateEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.Getter;
import lombok.NonNull;

/**
 * バッチ更新を行うSQLを自動生成するクエリの実装です。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoBatchUpdateImpl<T> extends QuerySupport<T> implements AutoBatchUpdate<T> {

    @Getter
    private final T[] entities;

    @Getter
    private final EntityMeta entityMeta;

    /**
     * バージョンプロパティを更新対象に含めるかどうか。
     */
    @Getter
    private boolean includeVersion;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter
    private boolean suppresOptimisticLockException = false;

    /**
     * 挿入対象とするプロパティ一覧
     */
    @Getter
    private final Set<String> includesProperties = new LinkedHashSet<>();

    /**
     * 挿入対象から除外するプロパティ一覧
     */
    @Getter
    private final Set<String> excludesProperties = new LinkedHashSet<>();

    public AutoBatchUpdateImpl(@NonNull SqlMapperContext context, @NonNull T[] entities) {
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
    public AutoBatchUpdateImpl(@NonNull SqlMapperContext context, @NonNull Collection<T> entities) {
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

    @Override
    public AutoBatchUpdateImpl<T> includesVersion() {
        this.includeVersion = true;
        return this;
    }

    @Override
    public AutoBatchUpdateImpl<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    @Override
    public AutoBatchUpdateImpl<T> includes(final PropertyPath<?>... properties) {

        for(PropertyPath<?> prop : properties) {
            String propertyName = prop.getPathMeta().getElement();

            // 挿入対象のプロパティの親のチェック
            EntityPath<?> parentPath = (EntityPath<?>)prop.getPathMeta().getParent();
            if(!entityMeta.getEntityType().equals(parentPath.getType())) {
                throw new IllegalOperateException(context.getMessageFormatter().create("query.noIncludeProperty")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("properyName", propertyName)
                        .format());
            }

            this.includesProperties.add(propertyName);
        }

        return this;
    }

    @Override
    public AutoBatchUpdateImpl<T> excludes(final PropertyPath<?>... properties) {

        for(PropertyPath<?> prop : properties) {
            String propertyName = prop.getPathMeta().getElement();

            // 除外対象のプロパティの親のチェック
            EntityPath<?> parentPath = (EntityPath<?>)prop.getPathMeta().getParent();
            if(!entityMeta.getEntityType().equals(parentPath.getType())) {
                throw new IllegalOperateException(context.getMessageFormatter().create("query.noIncludeProperty")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("properyName", propertyName)
                        .format());

            }

            this.excludesProperties.add(propertyName);
        }

        return this;
    }

    @Override
    public int[] execute() {

        context.getApplicationEventPublisher().publishEvent(new PreBatchUpdateEvent(this, entityMeta, entities));

        final AutoBatchUpdateExecutor executor = new AutoBatchUpdateExecutor(this);

        executor.prepare();
        final int[] result= executor.execute();

        context.getApplicationEventPublisher().publishEvent(new PostBatchUpdateEvent(this, entityMeta, entities));
        return result;
    }


}
