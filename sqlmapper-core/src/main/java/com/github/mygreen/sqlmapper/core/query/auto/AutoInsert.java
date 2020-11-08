package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.HashSet;
import java.util.Set;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostInsertEvent;
import com.github.mygreen.sqlmapper.core.event.PreInsertEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;

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
    private final Set<String> includesProperties = new HashSet<>();

    /**
     * 挿入対象から除外するプロパティ一覧
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> excludesProperties = new HashSet<>();

    public AutoInsert(@NonNull SqlMapperContext context, @NonNull T entity) {
        super(context);
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());
    }

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(insertable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param propertyNames 挿入対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoInsert<T> includes(final CharSequence... propertyNames) {

        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageFormatter().create("query.noIncludeProperty")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("propertyName", nameStr)
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
    public AutoInsert<T> excludes(final CharSequence... propertyNames) {

        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageFormatter().create("entity.noIncludeProperty")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("propertyName", nameStr)
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

        context.getApplicationEventPublisher().publishEvent(new PreInsertEvent(this, entityMeta, entity));

        final AutoInsertExecutor executor = new AutoInsertExecutor(this);
        executor.prepare();
        final int result = executor.execute();

        context.getApplicationEventPublisher().publishEvent(new PostInsertEvent(this, entityMeta, entity));
        return result;

    }

}