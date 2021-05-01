package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostInsertEvent;
import com.github.mygreen.sqlmapper.core.event.PreInsertEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.Getter;
import lombok.NonNull;

/**
 * 挿入を行うSQLを自動生成するクエリの実装です。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoInsertImpl<T> implements AutoInsert<T> {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    /**
     * 挿入対象のエンティティのインスタンス
     */
    @Getter
    private final T entity;

    /**
     * エンティティのメタ情報
     */
    @Getter
    private final EntityMeta entityMeta;

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

    public AutoInsertImpl(@NonNull SqlMapperContext context, @NonNull T entity) {
        this.context = context;
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());
    }

    @Override
    public AutoInsertImpl<T> includes(final PropertyPath<?>... properties) {

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
    public AutoInsertImpl<T> excludes(final PropertyPath<?>... properties) {

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
    public int execute() {

        context.getApplicationEventPublisher().publishEvent(new PreInsertEvent(this, entityMeta, entity));

        final int result = new AutoInsertExecutor(this)
                .execute();

        context.getApplicationEventPublisher().publishEvent(new PostInsertEvent(this, entityMeta, entity));
        return result;

    }

}
