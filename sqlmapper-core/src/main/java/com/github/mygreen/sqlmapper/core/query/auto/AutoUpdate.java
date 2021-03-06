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
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * SQLを自動生成する更新です。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoUpdate<T> extends QuerySupport<T> {

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
     * バージョンプロパティを更新対象に含めるかどうか。
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean includeVersion;

    /**
     * null値のプロパティを更新から除外する
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean excludesNull;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean suppresOptimisticLockException = false;

    /**
     * 更新対象とするプロパティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> includesProperties = new LinkedHashSet<>();

    /**
     * 更新対象から除外するプロパティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> excludesProperties = new LinkedHashSet<>();

    /**
     * 更新前のプロパティの状態を保持するマップ。
     * <p>
     * key=プロパティ名、value=プロパティの値。
     * </p>
     */
    @Getter(AccessLevel.PACKAGE)
    private Map<String, Object> beforeStates = Collections.emptyMap();

    public AutoUpdate(SqlMapperContext context, T entity) {
        super(context);
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

    /**
     * バージョンプロパティを通常の更新対象に含め、バージョンチェックの対象外とします。
     * <p>
     * このメソッドが呼び出されると、<code>update</code>文の<code>where</code>句にはバージョンのチェックが含まれなくなり、
     * バージョンプロパティは通常のプロパティと同じように更新対象に含められます ({@link #excludesNull()}や{@link #changedFrom(Object)}等も同じように適用されます)。
     * </p>
     *
     * @return このインスタンス自身
     */
    public AutoUpdate<T> includesVersion() {
        this.includeVersion = true;
        return this;
    }

    /**
     * <code>null</code>値のプロパティを更新対象から除外します。
     * @return このインスタンス自身
     */
    public AutoUpdate<T> excludesNull() {
        this.excludesNull = true;
        return this;
    }

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    public AutoUpdate<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(updatable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param properties 更新対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoUpdate<T> includes(final PropertyPath<?>... properties) {

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

    /**
     * 指定のプロパティを更新対象から除外します。
     *
     * @param properties 除外対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoUpdate<T> excludes(final PropertyPath<?>... properties) {

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

    /**
     * beforeから変更のあったプロパティだけを更新対象とします
     * @param beforeEntity 変更前の状態を持つエンティティ
     * @return このインスタンス自身
     */
    public AutoUpdate<T> changedFrom(@NonNull final T beforeEntity) {
        this.beforeStates = new HashMap<String, Object>(entityMeta.getPropertyMetaSize());

        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
            final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, beforeEntity);

            this.beforeStates.put(propertyName, propertyValue);
        }

        return this;
    }

    /**
     * beforeから変更のあったプロパティだけを更新対象とします。
     * <p>引数 {@literal beforeStates} のサイズが {@literal 0} のときは何もしません。
     * @param beforeStates 変更前の状態を持つマップ。（key=プロパティ名、value=プロパティ値）
     * @return このインスタンス自身。
     */
    public AutoUpdate<T> changedFrom(final Map<String, Object> beforeStates) {
        if(!beforeStates.isEmpty()) {
            this.beforeStates = Map.copyOf(beforeStates);
        }

        return this;
    }

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    public int execute() {

        context.getApplicationEventPublisher().publishEvent(new PreUpdateEvent(this, entityMeta, entity));

        final AutoUpdateExecutor executor = new AutoUpdateExecutor(this);

        executor.prepare();
        final int result = executor.execute();

        context.getApplicationEventPublisher().publishEvent(new PostUpdateEvent(this, entityMeta, entity));
        return result;

    }

}
