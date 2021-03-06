package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostBatchInsertEvent;
import com.github.mygreen.sqlmapper.core.event.PreBatchInsertEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * SQLを自動で生成するバッチ挿入です。
 * <p>主キーが識別子（IDENTITY）による自動生成の場合は、バッチ実行ではなく1件ずつ処理されるので注意してください。</p>
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティタイプ
 */
public class AutoBatchInsert<T> extends QuerySupport<T> {

    @Getter(AccessLevel.PACKAGE)
    private final T[] entities;

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

    public AutoBatchInsert(@NonNull SqlMapperContext context, @NonNull T[] entities) {
        super(context);

        if(entities.length == 0) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notEmptyEntity")
                    .format());
        }

        this.entities = entities;
        this.entityMeta = context.getEntityMetaFactory().create(entities[0].getClass());;
    }

    @SuppressWarnings("unchecked")
    public AutoBatchInsert(@NonNull SqlMapperContext context, @NonNull Collection<T> entities) {
        this(context, (T[])entities.toArray());
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
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(insertable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoBatchInsert<T> includes(final PropertyPath<?>... properties) {

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
    public AutoBatchInsert<T> excludes(final PropertyPath<?>... properties) {

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
    public int[] execute() {

        context.getApplicationEventPublisher().publishEvent(new PreBatchInsertEvent(this, entityMeta, entities));

        final AutoBatchInsertExecutor executor = new AutoBatchInsertExecutor(this);
        executor.prepare();
        final int[] result = executor.execute();

        context.getApplicationEventPublisher().publishEvent(new PostBatchInsertEvent(this, entityMeta, entities));
        return result;
    }
}
