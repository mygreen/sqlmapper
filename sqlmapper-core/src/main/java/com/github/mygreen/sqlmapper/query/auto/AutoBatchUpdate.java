package com.github.mygreen.sqlmapper.query.auto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.QueryBase;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AutoBatchUpdate<T> extends QueryBase<T> {

    @Getter(AccessLevel.PACKAGE)
    private final T[] entities;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * バージョンプロパティを更新対象に含めるかどうか。
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean includeVersion;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean suppresOptimisticLockException = false;

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

    public AutoBatchUpdate(@NonNull SqlMapperContext context, @NonNull T[] entities) {
        super(context);

        if(entities.length == 0) {
            throw new IllegalOperateException(context.getMessageBuilder().create("query.notEmptyEntity")
                    .format());
        }

        this.entities = entities;
        this.entityMeta = context.getEntityMetaFactory().create(entities[0].getClass());

        validateTarget();
    }

    @SuppressWarnings("unchecked")
    public AutoBatchUpdate(@NonNull SqlMapperContext context, @NonNull Collection<T> entities) {
        this(context, (T[])entities.toArray());
    }

    private void validateTarget() {
        // 主キーを持つかどうかのチェック
        if(entityMeta.getIdPropertyMetaList().isEmpty()) {
            throw new IllegalOperateException(context.getMessageBuilder().create("query.requiredId")
                    .varWithClass("entityType", entityMeta.getEntityType())
                    .format());
        }
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
     * バージョンプロパティを通常の更新対象に含め、バージョンチェックの対象外とします。
     * <p>
     * このメソッドが呼び出されると、<code>update</code>文の<code>where</code>句にはバージョンのチェックが含まれなくなり、
     * バージョンプロパティは通常のプロパティと同じように更新対象に含められます ({@link #excludesNull()}や{@link #changedFrom(Object)}等も同じように適用されます)。
     * </p>
     *
     * @return このインスタンス自身
     */
    public AutoBatchUpdate<T> includesVersion() {
        this.includeVersion = true;
        return this;
    }

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    public AutoBatchUpdate<T> suppresOptimisticLockException() {
        this.suppresOptimisticLockException = true;
        return this;
    }

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(updatable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param propertyNames 挿入対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoBatchUpdate<T> includes(final CharSequence... propertyNames) {
        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageBuilder().create("query.noIncludeProperty")
                        .varWithClass("classType", entityMeta.getEntityType())
                        .var("properyName", nameStr)
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
    public AutoBatchUpdate<T> excludes(final CharSequence... propertyNames) {
        for(CharSequence name : propertyNames) {
            final String nameStr = name.toString();
            if(entityMeta.getPropertyMeta(nameStr).isEmpty()) {
                throw new IllegalOperateException(context.getMessageBuilder().create("entity.prop.noInclude")
                        .varWithClass("classType", entityMeta.getEntityType())
                        .var("properyName", nameStr)
                        .format());
            }

            this.excludesProperties.add(nameStr);
        }

        return this;
    }

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("executeBatchUpdate");

        AutoBatchUpdateExecutor executor = new AutoBatchUpdateExecutor(this);

        try {
            executor.prepare();
            return executor.execute();

        } finally {
            completed();
        }
    }


}