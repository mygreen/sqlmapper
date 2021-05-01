package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

import lombok.Getter;
import lombok.NonNull;

/**
 * SSQLテンプレートによる抽出を行うクエリの実装です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティ情報
 */
public class SqlSelectImpl<T> implements SqlSelect<T> {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    /**
     * SQLテンプレートです。
     */
    @Getter
    private final SqlTemplate template;

    /**
     * SQLテンプレートのパラメータです。
     */
    @Getter
    private final SqlTemplateContext parameter;

    @Getter
    private final Class<T> baseClass;

    @Getter
    private final EntityMeta entityMeta;

    public SqlSelectImpl(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass,
            @NonNull SqlTemplate template, @NonNull SqlTemplateContext parameter) {

        this.context = context;
        this.template = template;
        this.parameter = parameter;

        this.baseClass = baseClass;
        this.entityMeta = context.getEntityMetaFactory().create(baseClass);
    }

    @Override
    public T getSingleResult() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getSingleResult(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
        });
    }

    @Override
    public Optional<T> getOptionalResult() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getOptionalResult(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
        });

    }

    @Override
    public List<T> getResultList() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getResultList(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
        });

    }

    @Override
    public Stream<T> getResultStream() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getResultStream(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
        });

    }

}
