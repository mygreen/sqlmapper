package com.github.mygreen.sqlmapper.query.sql;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.event.PostListSelectEvent;
import com.github.mygreen.sqlmapper.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.IterationCallback;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * SQLテンプレートによる検索です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティ情報
 */
public class SqlSelect<T> extends SqlTemplateQuerySupport<T> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<T> baseClass;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    public SqlSelect(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass,
            @NonNull SqlTemplate template, @NonNull SqlTemplateContext parameter) {
        super(context, template, parameter);
        this.baseClass = baseClass;
        this.entityMeta = context.getEntityMetaFactory().create(baseClass);
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    public T getSingleResult() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();
        final T result = executor.getSingleResult();

        context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(this, entityMeta, result));
        return result;
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。1件も対象がないときは空を返します。
     */
    public Optional<T> getOptionalResult() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();
        final Optional<T> result = executor.getOptionalResult();

        // 値が存在する場合のみイベントを実行する。
        result.ifPresent(e ->
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(this, entityMeta, e)));

        return result;

    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return 1件も対象がないときは空のリストを返します。
     */
    public List<T> getResultList() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();
        final List<T> result = executor.getResultList();

        context.getApplicationEventPublisher().publishEvent(new PostListSelectEvent(this, entityMeta, result));
        return result;

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

        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();
        return executor.iterate(callback);

    }

}
