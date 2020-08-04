package com.github.mygreen.sqlmapper.query.auto;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.event.PostListSelectEvent;
import com.github.mygreen.sqlmapper.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.IterationCallback;
import com.github.mygreen.sqlmapper.query.QueryBase;
import com.github.mygreen.sqlmapper.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.where.Where;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AutoSelect<T> extends QueryBase<T> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<T> baseClass;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * ヒントです。
     */
    @Getter(AccessLevel.PACKAGE)
    private String hint;

    /**
     * リミットです。
     */
    @Getter(AccessLevel.PACKAGE)
    private int limit = -1;

    /**
     * オフセットです。
     */
    @Getter(AccessLevel.PACKAGE)
    private int offset = -1;

    /**
     * select句へ追加するプロパティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> includesProperties = new HashSet<>();

    /**
     * select句から除外するプロパティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> excludesProperties = new HashSet<>();

    /**
     * クライテリアです。
     */
    @Getter(AccessLevel.PACKAGE)
    private Where criteria;

    /**
     * ソート順です。
     */
    @Getter(AccessLevel.PACKAGE)
    private String orderBy = "";

    /**
     * 検索条件で指定したIDプロパティの値の配列です。
     */
    @Getter(AccessLevel.PACKAGE)
    private Object[] idPropertyValues;

    /**
     * バージョンプロパティの値です。
     */
    @Getter(AccessLevel.PACKAGE)
    private Object versionPropertyValue;

    /**
     * SELECT ～ FOR UPDATEのタイプです。
     */
    @Getter(AccessLevel.PACKAGE)
    private SelectForUpdateType forUpdateType;

    /**
     * SELECT ～ FOR UPDATEでの待機時間 (秒単位) です。
     */
    @Getter(AccessLevel.PACKAGE)
    private int forUpdateWaitSeconds = 0;

    /**
     * {@link AutoSelect}を作成します。
     * @param context SQLのマッピングに必要な情報。
     * @param baseClass 抽出対象のテーブルにマッピングするエンティティのベースクラス。
     */
    public AutoSelect(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass) {
        super(context);
        this.baseClass = baseClass;
        this.entityMeta = context.getEntityMetaFactory().create(baseClass);
    }

    /**
     * ヒントを設定します。
     * @param hint ヒント
     * @return このインスタンス自身
     */
    public AutoSelect<T> hint(String hint) {
        this.hint = hint;
        return this;
    }

    /**
     * 抽出する行数を指定します。
     * @param limit 行数
     * @return このインスタンス自身
     */
    public AutoSelect<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 抽出するデータの開始位置を指定します。
     * @param offset 開始位置。
     * @return このインスタンス自身
     */
    public AutoSelect<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(insertable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param propertyNames 挿入対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoSelect<T> includes(final CharSequence... propertyNames) {

        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageFormatter().create("query.noIncludeProperty")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("properyName", nameStr)
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
    public AutoSelect<T> excludes(final CharSequence... propertyNames) {

        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageFormatter().create("entity.prop.noInclude")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("properyName", nameStr)
                        .format());
            }


            this.excludesProperties.add(nameStr);
        }

        return this;

    }

    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    public AutoSelect<T> where(@NonNull Where where) {
        this.criteria = where;
        return this;
    }

    /**
     * ソート順を指定します。
     * @param orderBy ソートするカラム
     * @return 自身のインスタンス。
     */
    public AutoSelect<T> orderBy(@NonNull CharSequence orderBy) {
        this.orderBy = orderBy.toString();
        return this;
    }

    /**
     * WHERE句の条件にIdプロパティ(主キー)を指定します。
     *
     * @param idPropertyValues IDプロパティの値。エンティティに定義している順で指定する必要があります。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException 指定したIDの個数とエンティティの個数と一致しないときにスローされます。
     */
    public AutoSelect<T> id(@NonNull final Object... idPropertyValues) {

        List<PropertyMeta> idPropertyMetaList = entityMeta.getIdPropertyMetaList();
        if(idPropertyMetaList.size() != idPropertyValues.length) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.noMatchIdPropertySize")
                    .paramWithClass("entityType", baseClass)
                    .param("actualSize", idPropertyValues.length)
                    .param("expectedSize", idPropertyMetaList.size())
                    .format());
        }

        this.idPropertyValues = idPropertyValues;

        return this;
    }

    /**
     * WHERE句の条件にバージョンプロパティを指定します。
     *
     * @param versionPropertyValue バージョンプロパティの値。
     * @return 自身のインスタンス
     * @throws IllegalOperateException エンティティにバージョンキーが定義されていないときにスローされます。
     */
    public AutoSelect<T> version(@NonNull final Object versionPropertyValue) {

        if(!entityMeta.hasVersionPropertyMeta()) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.noVersionProperty")
                    .paramWithClass("entityType", baseClass)
                    .format());
        }

        this.versionPropertyValue = versionPropertyValue;

        return this;
    }

    /**
     * {@literal FOR UPDATE} を追加します。
     * @return このインスタンス自身。
     * @throws IllegalOperateException DBMSがこの操作をサポートしていない場合にスローされます。
     */
    public AutoSelect<T> forUpdate() {
        final Dialect dialect = context.getDialect();
        if(!dialect.isSupportedSelectForUpdate(SelectForUpdateType.NORMAL)) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportSelectForUpdate")
                    .paramWithClass("entityType", baseClass)
                    .param("dialectName", dialect.getName())
                    .format());
        }

        this.forUpdateType = SelectForUpdateType.NORMAL;
        return this;
    }

    /**
     * {@literal FOR UPDATE NOWAIT} を追加します。
     * @return このインスタンス自身。
     * @throws IllegalOperateException DBMSがこの操作をサポートしていない場合にスローされます。
     */
    public AutoSelect<T> forUpdateNoWait() {

        final Dialect dialect = context.getDialect();
        if(!dialect.isSupportedSelectForUpdate(SelectForUpdateType.NOWAIT)) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportSelectForUpdateNowait")
                    .paramWithClass("entityType", baseClass)
                    .param("dialectName", dialect.getName())
                    .format());
        }

        this.forUpdateType = SelectForUpdateType.NOWAIT;
        return this;
    }

    /**
     * {@literal FOR UPDATE WAIT} を追加します。
     * @param seconds  ロックを獲得できるまでの最大待機時間(秒単位)
     * @return このインスタンス自身。
     * @throws IllegalOperateException DBMSがこの操作をサポートしていない場合にスローされます。
     */
    public AutoSelect<T> forUpdateWait(final int seconds) {

        final Dialect dialect = context.getDialect();
        if(!dialect.isSupportedSelectForUpdate(SelectForUpdateType.WAIT)) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportSelectForUpdateWait")
                    .paramWithClass("entityType", baseClass)
                    .param("dialectName", dialect.getName())
                    .format());
        }

        this.forUpdateType = SelectForUpdateType.WAIT;
        this.forUpdateWaitSeconds = seconds;
        return this;
    }

    /**
     * SQLが返す結果セットの行数を返します。
     * @return SQLが返す結果セットの行数
     */
    public long getCount() {
        assertNotCompleted("getCount");

        AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, true);
        try {
            executor.prepare();
            return executor.getCount();

        } finally {
            completed();
        }
    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    public T getSingleResult() {
        assertNotCompleted("getSingleResult");

        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
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

        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
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

        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
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

        AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
        try{
            executor.prepare();
            return executor.iterate(callback);
        } finally {
            completed();
        }

    }
}
