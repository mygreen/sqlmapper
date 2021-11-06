package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostUpdateEvent;
import com.github.mygreen.sqlmapper.core.event.PreUpdateEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.Getter;
import lombok.NonNull;

/**
 * 更新を行うSQLを自動生成するクエリの実装です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoUpdateImpl<T> implements AutoUpdate<T> {

    /**
     * SqlMapperの設定情報。
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

    @Getter
    private Integer queryTimeout;

    /**
     * バージョンプロパティを更新対象に含めるかどうか。
     */
    @Getter
    private boolean includeVersion;

    /**
     * null値のプロパティを更新から除外する
     */
    @Getter
    private boolean excludesNull;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter
    private boolean suppresOptimisticLockException = false;

    /**
     * 更新対象とするプロパティ
     */
    @Getter
    private final Set<String> includesProperties = new LinkedHashSet<>();

    /**
     * 更新対象から除外するプロパティ
     */
    @Getter
    private final Set<String> excludesProperties = new LinkedHashSet<>();

    /**
     * 更新前のプロパティの状態を保持するマップ。
     * <p>
     * key=プロパティ名、value=プロパティの値。
     * </p>
     */
    @Getter
    private Map<String, Object> beforeStates = Collections.emptyMap();

    public AutoUpdateImpl(SqlMapperContext context, T entity) {
        this.context = context;
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());

        // 処理対象の情報の整合性などのチェックを行う
        validateTarget();
    }

    private void validateTarget() {
        // 主キーを持つかどうかのチェック
        if(entityMeta.getIdPropertyMetaList().isEmpty()) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.requiredId")
                    .paramWithClass("entityType", entityMeta.getEntityType())
                    .format());
        }
    }

    @Override
    public AutoUpdateImpl<T> queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public AutoUpdateImpl<T> includesVersion() {
        this.includeVersion = true;
        return this;
    }

    @Override
    public AutoUpdateImpl<T> excludesNull() {
        this.excludesNull = true;
        return this;
    }

    @Override
    public AutoUpdateImpl<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    @Override
    public AutoUpdateImpl<T> includes(final PropertyPath<?>... properties) {

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
    public AutoUpdateImpl<T> excludes(final PropertyPath<?>... properties) {

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
    public AutoUpdateImpl<T> changedFrom(@NonNull final T beforeEntity) {
        this.beforeStates = new HashMap<String, Object>(entityMeta.getPropertyMetaSize());

        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
            final Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, beforeEntity);

            this.beforeStates.put(propertyName, propertyValue);
        }

        return this;
    }

    @Override
    public AutoUpdateImpl<T> changedFrom(final Map<String, Object> beforeStates) {
        if(!beforeStates.isEmpty()) {
            this.beforeStates = Map.copyOf(beforeStates);
        }

        return this;
    }

    @Override
    public int execute() {

        context.getApplicationEventPublisher().publishEvent(new PreUpdateEvent(this, entityMeta, entity));

        final int result = new AutoUpdateExecutor(this)
                .execute();

        context.getApplicationEventPublisher().publishEvent(new PostUpdateEvent(this, entityMeta, entity));
        return result;

    }

}
