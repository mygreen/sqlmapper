package com.github.mygreen.sqlmapper.query.auto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.event.PostBatchInsertEvent;
import com.github.mygreen.sqlmapper.event.PreBatchInsertEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.QueryBase;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AutoBatchInsert<T> extends QueryBase<T> {

    @Getter(AccessLevel.PACKAGE)
    private final T[] entities;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * 挿入対象とするプロパティ一覧
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> includesProperties = new HashSet<>();

    /**
     * 挿入対象から除外するプロパティ一覧
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> excludesProperties = new HashSet<>();

    public AutoBatchInsert(@NonNull SqlMapperContext context, @NonNull T[] entities) {
        super(context);

        if(entities.length == 0) {
            throw new IllegalOperateException(context.getMessageBuilder().create("query.notEmptyEntity")
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
     * @param propertyNames 挿入対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoBatchInsert<T> includes(final CharSequence... propertyNames) {

        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageBuilder().create("query.noIncludeProperty")
                        .varWithClass("classType", entityMeta.getEntityType())
                        .var("propertyName", nameStr)
                        .format());
            }


            this.includesProperties.add(nameStr);
        }

        return this;

    }

    /**
     * 指定のプロパティを挿入対象から除外します。
     *
     * @param propertyNames 除外対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoBatchInsert<T> excludes(final CharSequence... propertyNames) {

        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageBuilder().create("entity.noIncludeProperty")
                        .varWithClass("classType", entityMeta.getEntityType())
                        .var("propertyName", nameStr)
                        .format());
            }


            this.excludesProperties.add(nameStr);
        }

        return this;

    }

    /**
     * クエリを実行します。
     * @return 更新した行数。
     */
    public int execute() {

        assertNotCompleted("executeBatchInsert");
        context.getApplicationEventPublisher().publishEvent(new PreBatchInsertEvent(this, entityMeta, entities));

        final AutoBatchInsertExecutor executor = new AutoBatchInsertExecutor(this);
        try {
            executor.prepare();
            final int result = executor.execute();

            context.getApplicationEventPublisher().publishEvent(new PostBatchInsertEvent(this, entityMeta, entities));
            return result;

        } finally {
            completed();
        }
    }
}
