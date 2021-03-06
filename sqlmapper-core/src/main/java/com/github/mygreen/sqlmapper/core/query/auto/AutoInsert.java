package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostInsertEvent;
import com.github.mygreen.sqlmapper.core.event.PreInsertEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * SQLで自動で作成する挿入です。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoInsert<T> extends QuerySupport<T> {

    /**
     * 挿入対象のエンティティのインスタンス
     */
    @Getter(AccessLevel.PACKAGE)
    private final T entity;

    /**
     * エンティティのメタ情報
     */
    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * 挿入対象とするプロパティ一覧
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> includesProperties = new LinkedHashSet<>();

    /**
     * 挿入対象から除外するプロパティ一覧
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> excludesProperties = new LinkedHashSet<>();

    public AutoInsert(@NonNull SqlMapperContext context, @NonNull T entity) {
        super(context);
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());
    }

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(insertable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoInsert<T> includes(final PropertyPath<?>... properties) {

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
     * 指定のプロパティを挿入対象から除外します。
     *
     * @param properties 除外対象のプロパティ情報。
     * @return 自身のインスタンス。
     */
    public AutoInsert<T> excludes(final PropertyPath<?>... properties) {

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
     * クエリを実行します。
     * @return 更新した行数。
     */
    public int execute() {

        context.getApplicationEventPublisher().publishEvent(new PreInsertEvent(this, entityMeta, entity));

        final AutoInsertExecutor executor = new AutoInsertExecutor(this);
        executor.prepare();
        final int result = executor.execute();

        context.getApplicationEventPublisher().publishEvent(new PostInsertEvent(this, entityMeta, entity));
        return result;

    }

}
