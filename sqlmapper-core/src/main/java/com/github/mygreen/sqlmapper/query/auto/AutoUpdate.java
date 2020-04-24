package com.github.mygreen.sqlmapper.query.auto;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.QueryBase;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AutoUpdate<T> extends QueryBase<T> {

    /**
     * 削除対象のエンティティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final T entity;

    /**
     * エンティティ情報
     */
    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * バージョンプロパティを更新対象に含めるかどうか。
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean includeVersion;

    /**
     * null値のプロパティを更新から除外する
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean excludesNull;

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも{@link OptimisticLockingFailureException}スローしないなら<code>true</code>
     */
    @Getter(AccessLevel.PACKAGE)
    private boolean suppresOptimisticLockException = false;

    /**
     * 更新対象とするプロパティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> includesProperties = new HashSet<>();

    /**
     * 更新対象から除外するプロパティ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<String> excludesProperties = new HashSet<>();

    /**
     * 更新前のプロパティの状態を保持するマップ。
     * <p>
     * key=プロパティ名、value=プロパティの値。
     * </p>
     */
    @Getter(AccessLevel.PACKAGE)
    private Map<String, Object> beforeStates = Collections.emptyMap();

    public AutoUpdate(SqlMapperContext context, T entity) {
        super(context);
        this.entity = entity;
        this.entityMeta = context.getEntityMetaFactory().create(entity.getClass());

        // 処理対象の情報の整合性などのチェックを行う
        validateTarget();
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
     * バージョンプロパティを通常の更新対象に含め、バージョンチェックの対象外とします。
     * <p>
     * このメソッドが呼び出されると、<code>update</code>文の<code>where</code>句にはバージョンのチェックが含まれなくなり、
     * バージョンプロパティは通常のプロパティと同じように更新対象に含められます ({@link #excludesNull()}や{@link #changedFrom(Object)}等も同じように適用されます)。
     * </p>
     *
     * @return このインスタンス自身
     */
    public AutoUpdate<T> includesVersion() {
        this.includeVersion = true;
        return this;
    }

    /**
     * <code>null</code>値のプロパティを更新対象から除外します。
     * @return このインスタンス自身
     */
    public AutoUpdate<T> excludesNull() {
        this.excludesNull = true;
        return this;
    }

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    public AutoUpdate<T> suppresOptimisticLockException() {
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
    public AutoUpdate<T> includes(final CharSequence... propertyNames) {
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
    public AutoUpdate<T> excludes(final CharSequence... propertyNames) {
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
     * beforeから変更のあったプロパティだけを更新対象とします
     * @param beforeEntity 変更前の状態を持つエンティティ
     * @return このインスタンス自身
     */
    public AutoUpdate<T> changedFrom(@NonNull final T beforeEntity) {
        this.beforeStates = new HashMap<String, Object>(entityMeta.getPropertyMetaSize());

        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
            final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, beforeEntity);

            this.beforeStates.put(propertyName, propertyValue);
        }

        return this;
    }

    /**
     * beforeから変更のあったプロパティだけを更新対象とします。
     * <p>引数 {@literal beforeStates} のサイズが {@literal 0} のときは何もしません。
     * @param beforeStates 変更前の状態を持つマップ。（key=プロパティ名、value=プロパティ値）
     * @return このインスタンス自身。
     */
    public AutoUpdate<T> changedFrom(final Map<String, Object> beforeStates) {
        if(!beforeStates.isEmpty()) {
            this.beforeStates = Map.copyOf(beforeStates);
        }

        return this;
    }

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("execute");

        AutoUpdateExecutor executor = new AutoUpdateExecutor(this);

        try {
            executor.prepare();
            return executor.execute();

        } finally {
            completed();
        }
    }



}
