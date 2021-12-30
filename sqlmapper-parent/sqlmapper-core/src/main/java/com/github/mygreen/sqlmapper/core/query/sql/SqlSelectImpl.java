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
 * SQLテンプレートによる抽出を行うクエリの実装です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象のエンティティの型
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

    @Getter
    private Integer queryTimeout;

    @Getter
    private Integer fetchSize;

    @Getter
    private Integer maxRows;

    /**
     * 取得するレコード数の上限値です。
     * <p>負の値の時は無視します。
     */
    @Getter
    private int limit = -1;

    /**
     * 取得するレコード数の開始位置です。
     * <p>負の値の時は無視します。
     */
    @Getter
    private int offset = -1;

    public SqlSelectImpl(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass,
            @NonNull SqlTemplate template, @NonNull SqlTemplateContext parameter) {

        this.context = context;
        this.template = template;
        this.parameter = parameter;

        this.baseClass = baseClass;
        this.entityMeta = context.getEntityMetaFactory().create(baseClass);
    }

    @Override
    public SqlSelectImpl<T> queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public SqlSelectImpl<T> fetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    @Override
    public SqlSelectImpl<T> maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    @Override
    public SqlSelectImpl<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public SqlSelectImpl<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public T getSingleResult() {
        return new SqlSelectExecutor<>(this)
                .getSingleResult(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
                });
    }

    @Override
    public Optional<T> getOptionalResult() {
        return new SqlSelectExecutor<>(this)
                .getOptionalResult(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
                });
    }

    @Override
    public List<T> getResultList() {
        return new SqlSelectExecutor<>(this)
                .getResultList(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
                });
    }

    @Override
    public Stream<T> getResultStream() {
        return new SqlSelectExecutor<>(this)
                .getResultStream(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelectImpl.this, entityMeta, entity));
                });
    }

}
