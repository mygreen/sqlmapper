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

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.event.PostSelectEvent;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.JoinAssociation;
import com.github.mygreen.sqlmapper.core.query.JoinCondition;
import com.github.mygreen.sqlmapper.core.query.JoinType;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * SQLを自動で生成する検索です。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoSelect<T> extends QuerySupport<T> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<T> baseClass;

    @Getter(AccessLevel.PACKAGE)
    private final EntityPath<T> entityPath;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * エンティティタイプとメタ情報のマップ
     */
    @Getter(AccessLevel.PACKAGE)
    private final Map<Class<?>, EntityMeta> entityMetaMap = new HashMap<>();

    /**
     * SQLのヒントです。
     */
    @Getter(AccessLevel.PACKAGE)
    private String hint;

    /**
     * 取得するレコード数の上限値です。
     * <p>負の値の時は無視します。
     */
    @Getter(AccessLevel.PACKAGE)
    private int limit = -1;

    /**
     * 取得するレコード数の開始位置です。
     * <p>負の値の時は無視します。
     */
    @Getter(AccessLevel.PACKAGE)
    private int offset = -1;

    /**
     * select句へ追加するプロパティです。
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<PropertyPath<?>> includesProperties = new LinkedHashSet<>();

    /**
     * select句から除外するプロパティです。
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<PropertyPath<?>> excludesProperties = new LinkedHashSet<>();

    /**
     * テーブルの結合条件の一覧です。
     */
    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.PACKAGE)
    private final List<JoinCondition> joinConditions = new ArrayList<>();

    /**
     * エンティティの構成定義の一覧です
     */
    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.PACKAGE)
    private final List<JoinAssociation> joinAssociations = new ArrayList<>();

    /**
     * 検索条件です。
     */
    @Getter(AccessLevel.PACKAGE)
    private Predicate where;

    /**
     * ソート順です。
     */
    @Getter(AccessLevel.PACKAGE)
    private List<OrderSpecifier> orders = new ArrayList<>();

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
     * @param context SqlMapperの設定情報。
     * @param entityPath マッピングするエンティティのメタモデル。
     */
    @SuppressWarnings("unchecked")
    public AutoSelect(@NonNull SqlMapperContext context, @NonNull EntityPath<T> entityPath) {
        super(context);
        this.entityPath = entityPath;
        this.entityMeta = context.getEntityMetaFactory().create(entityPath.getType());
        this.baseClass = (Class<T>)entityMeta.getEntityType();

        this.entityMetaMap.put(entityPath.getType(), context.getEntityMetaFactory().create(entityPath.getType()));
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
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoSelect<T> includes(final PropertyPath<?>... properties) {

        for(PropertyPath<?> prop : properties) {
            //TODO: 結合条件とかあるので、チェックは後からにする
            final String propName = prop.getPathMeta().getElement();
            if(entityMeta.getPropertyMeta(propName).isEmpty()) {
                throw new IllegalOperateException(context.getMessageFormatter().create("query.noIncludeProperty")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("properyName", propName)
                        .format());
            }
            //TODO: 追加ではなく、上書き（直接変数に代入）する
            this.includesProperties.add(prop);
        }


        return this;

    }

    /**
     * 指定のプロパティを挿入対象から除外します。
     *
     * @param properties 除外対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    public AutoSelect<T> excludes(final PropertyPath<?>... properties) {

        for(PropertyPath<?> prop : properties) {
            //TODO: 結合条件とかあるので、チェックは後からにする
            final String propName = prop.toString();
            if(entityMeta.getPropertyMeta(propName).isEmpty()) {
                throw new IllegalOperateException(context.getMessageFormatter().create("entity.prop.noInclude")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("properyName", propName)
                        .format());
            }


            //TODO: 追加ではなく、上書き（直接変数に代入）する
            this.excludesProperties.add(prop);
        }

        return this;

    }

    /**
     * FROM句で指定したテーブルと内部結合（{@literal INNERT JOIN}）する条件を指定します。
     *
     * @param <ENTITY> 結合先のテーブルのエンティティタイプ
     * @param toEntityPath 結合先テーブルのエンティティ情報
     * @param conditioner 結合条件の組み立て
     * @return 自身のインスタンス
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティ（テーブル）を指定しているときにスローされます。
     */
    public <ENTITY extends EntityPath<?>> AutoSelect<T> innerJoin(@NonNull ENTITY toEntityPath,
            @NonNull JoinCondition.Conditioner<ENTITY> conditioner) {

        JoinCondition<ENTITY> condition = new JoinCondition<>(JoinType.INNER, toEntityPath, conditioner);
        validateJoinCondition(condition);

        this.entityMetaMap.put(toEntityPath.getType(), context.getEntityMetaFactory().create(toEntityPath.getType()));
        this.joinConditions.add(condition);
        return this;
    }

    /**
     * FROM句で指定したテーブルと左外部結合（{@literal LEFT OUTER JOIN}）する条件を指定します。
     *
     * @param <ENTITY> 結合先のテーブルのエンティティタイプ
     * @param toEntityPath 結合先テーブルのエンティティ情報
     * @param conditioner 結合条件の組み立て
     * @return 自身のインスタンス
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティ（テーブル）を指定しているときにスローされます。
     */
    public <ENTITY extends EntityPath<?>> AutoSelect<T> leftJoin(@NonNull ENTITY toEntityPath,
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
    @SuppressWarnings("rawtypes")
    private void validateJoinCondition(JoinCondition condition) {

        // 同じ組み合わせのエンティティが存在しかいかチェックします。
        for(JoinCondition target : joinConditions) {
            if(target.getToEntity().equals(condition.getToEntity())) {

                // 同じ組み合わせの場合
                throw new IllegalOperateException(context.getMessageFormatter().create("query.existsSameJoinEntity")
                        .param("toEntity", condition.getToEntity().getPathMeta().getElement())
                        .format());
            }
        }

        // TODO: 結合元のエンティティが結合条件として存在するかチェックします。
        // 順番に依存するのであとからチェック？

    }

    /**
     * テーブル結合の際に複数のテーブルのエンティティの構成定義を指定します。
     *
     * @param <E1> エンティティタイプ1
     * @param <E2> エンティティタイプ2
     * @param entityPath1 エンティティ情報1
     * @param entityPath2 エンティティ情報2
     * @param associator エンティティの構成定義
     * @return 自身のインスタンス
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティの構成定義を指定しているときにスローされます。
     */
    public <E1, E2> AutoSelect<T> associate(@NonNull EntityPath<E1> entityPath1, @NonNull EntityPath<E2> entityPath2,
            @NonNull JoinAssociation.Associator<E1, E2> associator) {

        JoinAssociation<E1, E2> association = new JoinAssociation<>(entityPath1, entityPath2, associator);
        validateJoinAssociation(association);

        this.joinAssociations.add(association);
        return this;
    }

    @SuppressWarnings("rawtypes")
    private void validateJoinAssociation(JoinAssociation association) {

        // 同じ組み合わせのエンティティが存在しないかチェックします
        for(JoinAssociation target : joinAssociations) {
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

        //TODO: 結合情報で定義されいるエンティティかチェックします。
        //順番に依存するので後からチェック？

    }

    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    public AutoSelect<T> where(@NonNull Predicate where) {
        this.where = where;
        return this;
    }

    /**
     * ソート順を指定します。
     * @param orderBy ソートするロパティの並び順情報
     * @return 自身のインスタンス。
     */
    public AutoSelect<T> orderBy(OrderSpecifier... orders) {
        //TODO: 追加ではなく、上書き（直接変数に代入）する
        this.orders.addAll(Arrays.asList(orders));
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
                    .paramWithClass("entityType", entityPath.getType())
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
                    .paramWithClass("entityType", entityPath.getType())
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
                    .paramWithClass("entityType", entityPath.getType())
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
    public AutoSelect<T> forUpdateWait(final int seconds) {

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

    /**
     * SQLが返す結果セットの行数を返します。
     * @return SQLが返す結果セットの行数
     */
    public long getCount() {
        AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, true);
        executor.prepare();
        return executor.getCount();

    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    public T getSingleResult() {
        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
        executor.prepare();

        return executor.getSingleResult(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelect.this, entityMeta, entity));
        });

    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。1件も対象がないときは空を返します。
     */
    public Optional<T> getOptionalResult() {
        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
        executor.prepare();

        return executor.getOptionalResult(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelect.this, entityMeta, entity));
        });

    }

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return 1件も対象がないときは空のリストを返します。
     */
    public List<T> getResultList() {
        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
        executor.prepare();

        return executor.getResultList(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelect.this, entityMeta, entity));
        });

    }

    /**
     * 問い合わせ結果を{@link Stream} で取得します。
     * 問い合わせ結果全体のリストを作成しないため、問い合わせ結果が膨大になる場合でもメモリ消費量を抑えることが出来ます。
     *
     * @return 問い合わせの結果。
     */
    public Stream<T> getResultStream() {
        final AutoSelectExecutor<T> executor = new AutoSelectExecutor<>(this, false);
        executor.prepare();
        return executor.getResultStream(entity -> {
            context.getApplicationEventPublisher().publishEvent(new PostSelectEvent(AutoSelect.this, entityMeta, entity));
        });

    }
}
