package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.JoinAssociation;
import com.github.mygreen.sqlmapper.core.query.JoinCondition;
import com.github.mygreen.sqlmapper.core.query.JoinType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.Getter;
import lombok.NonNull;

/**
 * 抽出を行うSQLを自動生成するクエリの実装です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoSelectImpl<T> implements AutoSelect<T> {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    @Getter
    private final Class<T> baseClass;

    @Getter
    private final EntityPath<T> entityPath;

    @Getter
    private final EntityMeta entityMeta;

    /**
     * エンティティタイプとメタ情報のマップ
     */
    @Getter
    private final Map<Class<?>, EntityMeta> entityMetaMap = new HashMap<>();

    /**
     * SQLのヒントです。
     */
    @Getter
    private String hint;

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

    /**
     * select句へ追加するプロパティです。
     */
    @Getter
    private final Set<PropertyPath<?>> includesProperties = new LinkedHashSet<>();

    /**
     * select句から除外するプロパティです。
     */
    @Getter
    private final Set<PropertyPath<?>> excludesProperties = new LinkedHashSet<>();

    /**
     * テーブルの結合条件の一覧です。
     */
    @SuppressWarnings("rawtypes")
    @Getter
    private final List<JoinCondition> joinConditions = new ArrayList<>();

    /**
     * エンティティの構成定義の一覧です
     */
    @SuppressWarnings("rawtypes")
    @Getter
    private final List<JoinAssociation> joinAssociations = new ArrayList<>();

    /**
     * 検索条件です。
     */
    @Getter
    private Predicate where;

    /**
     * ソート順です。
     */
    @Getter
    private List<OrderSpecifier> orders = new ArrayList<>();

    /**
     * 検索条件で指定したIDプロパティの値の配列です。
     */
    @Getter
    private Object[] idPropertyValues;

    /**
     * バージョンプロパティの値です。
     */
    @Getter
    private Object versionPropertyValue;

    /**
     * SELECT ～ FOR UPDATEのタイプです。
     */
    @Getter
    private SelectForUpdateType forUpdateType;

    /**
     * SELECT ～ FOR UPDATEでの待機時間 (秒単位) です。
     */
    @Getter
    private int forUpdateWaitSeconds = 0;

    /**
     * {@link AutoSelectImpl}を作成します。
     * @param context SqlMapperの設定情報。
     * @param entityPath マッピングするエンティティのメタモデル。
     */
    @SuppressWarnings("unchecked")
    public AutoSelectImpl(@NonNull SqlMapperContext context, @NonNull EntityPath<T> entityPath) {
        this.context = context;
        this.entityPath = entityPath;
        this.entityMeta = context.getEntityMetaFactory().create(entityPath.getType());
        this.baseClass = (Class<T>)entityMeta.getEntityType();

        this.entityMetaMap.put(entityPath.getType(), context.getEntityMetaFactory().create(entityPath.getType()));
    }

    @Override
    public AutoSelectImpl<T> hint(String hint) {
        this.hint = hint;
        return this;
    }

    @Override
    public AutoSelectImpl<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public AutoSelectImpl<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public AutoSelectImpl<T> includes(final PropertyPath<?>... properties) {

        for(PropertyPath<?> prop : properties) {
            this.includesProperties.add(prop);
        }

        return this;

    }

    @Override
    public AutoSelectImpl<T> excludes(final PropertyPath<?>... properties) {

        for(PropertyPath<?> prop : properties) {
            this.excludesProperties.add(prop);
        }

        return this;

    }

    @Override
    public <ENTITY extends EntityPath<?>> AutoSelectImpl<T> innerJoin(@NonNull ENTITY toEntityPath,
            @NonNull JoinCondition.Conditioner<ENTITY> conditioner) {

        JoinCondition<ENTITY> condition = new JoinCondition<>(JoinType.INNER, toEntityPath, conditioner);
        validateJoinCondition(condition);

        this.entityMetaMap.put(toEntityPath.getType(), context.getEntityMetaFactory().create(toEntityPath.getType()));
        this.joinConditions.add(condition);
        return this;
    }

    @Override
    public <ENTITY extends EntityPath<?>> AutoSelectImpl<T> leftJoin(@NonNull ENTITY toEntityPath,
            @NonNull JoinCondition.Conditioner<ENTITY> conditioner) {

        JoinCondition<ENTITY> condition = new JoinCondition<>(JoinType.LEFT_OUTER, toEntityPath, conditioner);
        validateJoinCondition(condition);

        this.entityMetaMap.put(toEntityPath.getType(), context.getEntityMetaFactory().create(toEntityPath.getType()));
        this.joinConditions.add(condition);

        return this;
    }

    /**
     * 結合条件の一覧に既に同じテーブルのエンティティ情報が含まれいえるか検査します。
     * <ul>
     *   <li>比較は、結合先のエンティティ情報({@link EntityPath})既に存在するかどうか。</li>
     *   <li>結合種別({@link JoinType}) は無視します。</li>
     * </ul>
     *
     * @param condition 結合条件
     * @return 同じエンティティの組み合わせが含まれているとき {@literal true} を返します
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティ（テーブル）を指定しているときにスローされます。
     */
    private void validateJoinCondition(JoinCondition<?> condition) {

        // 同じ組み合わせのエンティティが存在しかチェックします。
        for(JoinCondition<?> target : joinConditions) {
            if(target.getToEntity().equals(condition.getToEntity())) {

                // 同じ組み合わせの場合
                throw new IllegalOperateException(context.getMessageFormatter().create("query.existsSameJoinEntity")
                        .param("toEntity", condition.getToEntity().getPathMeta().getElement())
                        .format());
            }
        }

    }

    @Override
    public <E1, E2> AutoSelectImpl<T> associate(@NonNull EntityPath<E1> entityPath1, @NonNull EntityPath<E2> entityPath2,
            @NonNull JoinAssociation.Associator<E1, E2> associator) {

        JoinAssociation<E1, E2> association = new JoinAssociation<>(entityPath1, entityPath2, associator);

        // 同じ組み合わせのエンティティが存在しないかチェックします
        for(JoinAssociation<?, ?> target : joinAssociations) {
            if((target.getEntity1().equals(association.getEntity1()) && target.getEntity2().equals(association.getEntity2()))
                    || (target.getEntity1().equals(association.getEntity2()) && target.getEntity2().equals(association.getEntity1()))
                    ) {
                // 同じ組み合わせの場合 - エンティティ1、エンティティ2の順番は考慮しません。
                throw new IllegalOperateException(context.getMessageFormatter().create("query.existsSameAssociateEntity")
                        .param("entity1", association.getEntity1().getPathMeta().getElement())
                        .param("entity2", association.getEntity2().getPathMeta().getElement())
                        .format());
            }
        }

        this.joinAssociations.add(association);
        return this;
    }

    @Override
    public AutoSelectImpl<T> where(@NonNull Predicate where) {
        this.where = where;
        return this;
    }

    @Override
    public AutoSelectImpl<T> orderBy(OrderSpecifier... orders) {
        //TODO: 追加ではなく、上書き（直接変数に代入）する
        this.orders.addAll(Arrays.asList(orders));
        return this;
    }

    @Override
    public AutoSelectImpl<T> id(@NonNull final Object... idPropertyValues) {

        List<PropertyMeta> idPropertyMetaList = entityMeta.getIdPropertyMetaList();
        if(idPropertyMetaList.size() != idPropertyValues.length) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.noMatchIdPropertySize")
                    .paramWithClass("entityType", entityPath.getType())
                    .param("actualSize", idPropertyValues.length)
                    .param("expectedSize", idPropertyMetaList.size())
                    .format());
        }

        this.idPropertyValues = idPropertyValues;

        return this;
    }

    @Override
    public AutoSelectImpl<T> version(@NonNull final Object versionPropertyValue) {

        if(!entityMeta.hasVersionPropertyMeta()) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.noVersionProperty")
                    .paramWithClass("entityType", entityPath.getType())
                    .format());
        }

        this.versionPropertyValue = versionPropertyValue;

        return this;
    }

    @Override
    public AutoSelectImpl<T> forUpdate() {
        final Dialect dialect = context.getDialect();
        if(!dialect.isSupportedSelectForUpdate(SelectForUpdateType.NORMAL)) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportSelectForUpdate")
                    .paramWithClass("entityType", entityPath.getType())
                    .param("dialectName", dialect.getName())
                    .format());
        }

        this.forUpdateType = SelectForUpdateType.NORMAL;
        return this;
    }

    @Override
    public AutoSelectImpl<T> forUpdateNoWait() {

        final Dialect dialect = context.getDialect();
        if(!dialect.isSupportedSelectForUpdate(SelectForUpdateType.NOWAIT)) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportSelectForUpdateNowait")
                    .paramWithClass("entityType", entityPath.getType())
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
    public AutoSelectImpl<T> forUpdateWait(final int seconds) {

        final Dialect dialect = context.getDialect();
        if(!dialect.isSupportedSelectForUpdate(SelectForUpdateType.WAIT)) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportSelectForUpdateWait")
                    .paramWithClass("entityType", entityPath.getType())
                    .param("dialectName", dialect.getName())
                    .format());
        }

        this.forUpdateType = SelectForUpdateType.WAIT;
        this.forUpdateWaitSeconds = seconds;
        return this;
    }

    @Override
    public long getCount() {
        return new AutoSelectExecutor<>(this, true)
                .getCount();
    }

    @Override
    public T getSingleResult() {
        return  new AutoSelectExecutor<>(this, false)
                .getSingleResult(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelectImpl.this, entityMeta, entity));
                });
    }

    @Override
    public Optional<T> getOptionalResult() {
        return new AutoSelectExecutor<>(this, false)
                .getOptionalResult(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelectImpl.this, entityMeta, entity));
                });
    }

    @Override
    public List<T> getResultList() {
        return new AutoSelectExecutor<>(this, false)
                .getResultList(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelectImpl.this, entityMeta, entity));
                });
    }

    @Override
    public Stream<T> getResultStream() {
        return new AutoSelectExecutor<>(this, false)
                .getResultStream(entity -> {
                    context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelectImpl.this, entityMeta, entity));
                });
    }
}
