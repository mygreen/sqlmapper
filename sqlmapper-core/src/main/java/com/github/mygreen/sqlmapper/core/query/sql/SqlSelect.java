package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.splate.SqlTemplate;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

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

        return executor.getSingleResult(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelect.this, entityMeta, entity));
        });
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。1件も対象がないときは空を返します。
     */
    public Optional<T> getOptionalResult() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getOptionalResult(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelect.this, entityMeta, entity));
        });

    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return 1件も対象がないときは空のリストを返します。
     */
    public List<T> getResultList() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getResultList(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelect.this, entityMeta, entity));
        });

    }

    /**
     * 問い合わせ結果を{@link Stream} で取得します。
     * 問い合わせ結果全体のリストを作成しないため、問い合わせ結果が膨大になる場合でもメモリ消費量を抑えることが出来ます。
     *
     * @return 問い合わせの結果。
     */
    public Stream<T> getResultStream() {
        final SqlSelectExecutor<T> executor = new SqlSelectExecutor<>(this);
        executor.prepare();

        return executor.getResultStream(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(SqlSelect.this, entityMeta, entity));
        });

    }

}
