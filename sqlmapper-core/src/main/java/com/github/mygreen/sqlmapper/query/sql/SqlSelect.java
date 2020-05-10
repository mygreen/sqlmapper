package com.github.mygreen.sqlmapper.query.sql;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.event.PostListSelectEvent;
import com.github.mygreen.sqlmapper.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.IterationCallback;
import com.github.mygreen.sqlmapper.query.QueryBase;
import com.github.mygreen.sqlmapper.sql.Node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class SqlSelect<T> extends QueryBase<T> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<T> baseClass;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * パラメータです。
     */
    @Getter(AccessLevel.PACKAGE)
    private final Object parameter;

    /**
     * SQLの解析ノードです。
     */
    @Getter(AccessLevel.PACKAGE)
    private final Node node;

    public SqlSelect(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass, @NonNull Node node, Object parameter) {
        super(context);
        this.baseClass = baseClass;
        this.node = node;
        this.parameter = parameter;
        this.entityMeta = context.getEntityMetaFactory().create(baseClass);
    }

    public SqlSelect(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass, @NonNull Node node) {
        this(context, baseClass, node, null);
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    public T getSingleResult() {
        assertNotCompleted("getSingleResult");

        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        try {
            executor.prepare();
            final T result = executor.getSingleResult();

            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(this, entityMeta, result));
            return result;

        } finally {
            completed();
        }
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。1件も対象がないときは空を返します。
     */
    public Optional<T> getOptionalResult() {
        assertNotCompleted("getOptionalResult");

        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        try{
            executor.prepare();
            final Optional<T> result = executor.getOptionalResult();

            // 値が存在する場合のみイベントを実行する。
            result.ifPresent(e ->
                context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(this, entityMeta, e)));

            return result;

        } finally {
            completed();
        }
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return 1件も対象がないときは空のリストを返します。
     */
    public List<T> getResultList() {
        assertNotCompleted("getResultList");

        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        try{
            executor.prepare();
            final List<T> result = executor.getResultList();

            context.getApplicationEventPublisher().publishEvent(new PostListSelectEvent(this, entityMeta, result));
            return result;

        } finally {
            completed();
        }
    }

    /**
     * 問い合わせ結果を一件ごとにコールバックに通知します。
     * 問い合わせ結果全体のリストを作成しないため、問い合わせ結果が膨大になる場合でもメモリ消費量を抑えることが出来ます。
     *
     * @param <R> 戻り値の型
     * @param callback コールバック
     * @return コールバックが最後に返した結果
     */
    public <R> R iterate(IterationCallback<T, R> callback) {

        assertNotCompleted("iterate");

        SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        try{
            executor.prepare();
            return executor.iterate(callback);
        } finally {
            completed();
        }

    }




}
