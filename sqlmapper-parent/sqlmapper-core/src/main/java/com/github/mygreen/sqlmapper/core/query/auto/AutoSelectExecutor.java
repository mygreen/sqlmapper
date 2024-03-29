package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.mapper.AutoEntityRowMapper;
import com.github.mygreen.sqlmapper.core.mapper.EntityMappingCallback;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.FromClause;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.IllegalQueryException;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;
import com.github.mygreen.sqlmapper.core.query.JoinAssociation;
import com.github.mygreen.sqlmapper.core.query.JoinCondition;
import com.github.mygreen.sqlmapper.core.query.OrderByClause;
import com.github.mygreen.sqlmapper.core.query.SelectClause;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhere;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhereVisitor;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereBuilder;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereVisitor;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.PathMeta;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;


/**
 * 抽出を行うSQLを自動生成するクエリを実行します。
 * {@link AutoSelectImpl}のクエリ実行処理の移譲先です。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public class AutoSelectExecutor<T> {

    /**
     * クエリ情報
     */
    private final AutoSelectImpl<T> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * SELECT COUNT(*)～で行数を取得する場合に<code>true</code>
     */
    private final boolean counting;

    /**
     * select句です。
     */
    private SelectClause selectClause = new SelectClause();

    /**
     * from句です。
     */
    private FromClause fromClause = new FromClause();

    /**
     * where句です。
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * order by句です。
     */
    private final OrderByClause orderByClause = new OrderByClause();

    /**
     * テーブルの別名を管理します。
     */
    private final TableNameResolver tableNameResolver = new TableNameResolver();

    /**
     * for update句です。
     */
    private String forUpdateClause;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータです。
     */
    private final List<Object> paramValues = new ArrayList<>();

    /**
     * 抽出対象のプロパティ情報とプロパティのマッピング先のエンティティのタイプ情報
     */
    private Map<PropertyMeta, Class<?>> targetPropertyMetaEntityTypeMap;

    /**
     * インスタンスの作成
     *
     * @param query クエリ情報
     * @param counting カウント用のクエリかどうか
     */
    public AutoSelectExecutor(AutoSelectImpl<T> query, boolean counting) {
        this.query = query;
        this.counting = counting;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {

        prepareTableAlias();
        prepareTargetColumn();
        prepareTargetTable();
        prepareIdVersion();
        prepareCondition();
        prepareOrderBy();
        prepareForUpdate();

        prepareSql();
    }

    /**
     * テーブルの別名を準備します。
     */
    private void prepareTableAlias() {

        // FROM句指定のテーブル
        tableNameResolver.prepareTableAlias(query.getEntityPath());

        // JOINテーブルのエイリアス
        for(JoinCondition<?> condition : query.getJoinConditions()) {
            tableNameResolver.prepareTableAlias(condition.getToEntity());
        }

        // 構成定義のバリデーション
        for(JoinAssociation<?, ?> association : query.getJoinAssociations()) {
            validateJoinAssociation(association);
        }
    }

    /**
     * 構成定義が抽出対象のテーブルのエンティティかどうか
     *
     * @param association 構成定義情報
     */
    private void validateJoinAssociation(JoinAssociation<?, ?> association) {

        // 参照対象のエンティティかチェックする
        boolean foundEntity1 = false;
        boolean foundEntity2 = false;

        if(association.getEntity1().getType().equals(query.getEntityMeta().getEntityType())) {
            foundEntity1 = true;
        }

        if(association.getEntity2().getType().equals(query.getEntityMeta().getEntityType())) {
            foundEntity2 = true;
        }

        // 結合情報で定義されているエンティティかチェックします。
        for(JoinCondition<?> condition : query.getJoinConditions()) {
            if(association.getEntity1().getType().equals(condition.getToEntity().getType())) {
                foundEntity1 = true;
            }

            if(association.getEntity2().getType().equals(condition.getToEntity().getType())) {
                foundEntity2 = true;
            }

            if(foundEntity1 && foundEntity2) {
                continue;
            }

        }

        if(!foundEntity1 || !foundEntity2) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.noExistsTargetAssociateEntity")
                    .param("entity1", association.getEntity1().getType())
                    .param("entity2", association.getEntity2().getType())
                    .format());
            }
        }


    /**
     * 抽出対象のエンティティやカラム情報を準備します。
     * {@link SelectClause}を準備します。
     */
    private void prepareTargetColumn() {

        if(counting) {
            // 件数取得の場合
            String sql = context.getDialect().getCountSql();
            selectClause.addSql(sql);

        } else {

            // 抽出対象のプロパティが参照対象のテーブルに存在するかチェックする
            validateTargetProperty(query.getIncludesProperties());
            validateTargetProperty(query.getExcludesProperties());

            // 参照対象のプロパティと所属するエンティティのマップ
            final Map<PropertyMeta, Class<?>> selectedPropertyMetaMap = new LinkedHashMap<>();

            // ベースとなるエンティティのカラム指定の場合
            for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

                if(!isTargetProperty(propertyMeta)) {
                    // 抽出対象のプロパティでない場合はスキップします。
                    continue;
                }

                String tableAlias = tableNameResolver.getTableAlias(query.getEntityPath());
                selectClause.addSql(tableAlias, propertyMeta.getColumnMeta().getName());
                selectedPropertyMetaMap.put(propertyMeta, query.getBaseClass());

            }

            // 結合しているエンティティの場合
            for(JoinCondition<?> jc : query.getJoinConditions()) {
                EntityPath<?> joinEntity = jc.getToEntity();
                EntityMeta joinEntityMeta = query.getEntityMetaMap().get(joinEntity.getType());

                for(PropertyMeta propertyMeta : joinEntityMeta.getAllColumnPropertyMeta()) {
                    final String propertyName = propertyMeta.getName();
                    final PropertyPath<?> propertyPath = joinEntity.getPropertyPath(propertyName);

                    if(propertyMeta.isTransient()) {
                        continue;
                    }

                    if(query.getExcludesProperties().contains(propertyPath)) {
                        continue;
                    }

                    if(!query.getIncludesProperties().isEmpty()
                            && !query.getIncludesProperties().contains(propertyPath)) {
                        continue;
                    }

                    String tableAlias = tableNameResolver.getTableAlias(joinEntity);
                    selectClause.addSql(tableAlias, propertyMeta.getColumnMeta().getName());
                    selectedPropertyMetaMap.put(propertyMeta, joinEntity.getType());

                }
            }

            this.targetPropertyMetaEntityTypeMap = Collections.unmodifiableMap(selectedPropertyMetaMap);

        }

    }

    /**
     * 対象のプロパティが参照対象のテーブルのエンティティに所属するかチェックします。
     *
     * @param properties チェック対象のプロパティ一覧
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティ（テーブル）を指定しているときにスローされます。
     */
    private void validateTargetProperty(final Collection<PropertyPath<?>> properties) {

        for(PropertyPath<?> prop : properties) {

            // チェックしたエンティのクラスタイプ
            Set<Class<?>> checkedClassTypes = new LinkedHashSet<>();

            // 参照元のエンティティのチェック
            EntityPath<?> parentPath = (EntityPath<?>)prop.getPathMeta().getParent();
            if(query.getEntityPath().equals(parentPath)) {
                continue;
            }
            checkedClassTypes.add(query.getEntityPath().getType());

            // 結合先のエンティティかチェック
            boolean foundInJoinedEntity = false;
            for(JoinCondition<?> condition : query.getJoinConditions()) {
                EntityPath<?> joinEntityPath = condition.getToEntity();
                if(joinEntityPath.equals(parentPath)) {
                    foundInJoinedEntity = true;
                    continue;
                }
                checkedClassTypes.add(joinEntityPath.getType());
            }

            if(foundInJoinedEntity) {
                Class<?>[] classTypes = checkedClassTypes.toArray(new Class[checkedClassTypes.size()]);
                throw new IllegalOperateException(context.getMessageFormatter().create("noAnyIncludeProperty")
                        .paramWithClass("classTypes", classTypes)
                        .param("entityClass", parentPath.getPathMeta().getType())
                        .param("properyName", prop.getPathMeta().getElement())
                        .format());
            }

        }

    }

    /**
     * 抽出対象のプロパティか判定します。
     * @param propertyMeta プロパティ情報
     * @return 抽出対象のとき、{@literal true} を返します。
     */
    private boolean isTargetProperty(final PropertyMeta propertyMeta) {

        if(propertyMeta.isId()) {
            return true;
        }

        if(propertyMeta.isTransient()) {
            return false;
        }

        if(query.getIncludesProperties().isEmpty() && query.getExcludesProperties().isEmpty()) {
            return true;
        }

        final String propertyName = propertyMeta.getName();
        final PropertyPath<?> propertyPath = query.getEntityPath().findPropertyPath(propertyName);

        if(query.getIncludesProperties().contains(propertyPath)) {
            return true;
        }

        if(query.getExcludesProperties().contains(propertyPath)) {
            return false;
        }

        // 抽出対象が指定されているときは、その他はすべて抽出対象外とする。
        return query.getIncludesProperties().isEmpty();

    }

    /**
     * 抽出対象のテーブルや結合対象のテーブルの準備を行います。
     * {@link FromClause}の準備を行います。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareTargetTable() {

        // from句の指定
        fromClause.addSql(query.getEntityMeta().getTableMeta().getFullName(), tableNameResolver.getTableAlias(query.getEntityPath()));

        for(JoinCondition jc : query.getJoinConditions()) {

            // 結合対象のテーブル情報の取得
            EntityPath<?> joinEntity = jc.getToEntity();
            EntityMeta joinEntityMeta = query.getEntityMetaMap().get(joinEntity.getType());
            String tableName = joinEntityMeta.getTableMeta().getFullName();
            String tableAlias = tableNameResolver.getTableAlias(joinEntity);

            Predicate where = jc.getConditioner().build(joinEntity);

            // テーブルの結合条件の評価
            MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(query.getEntityMetaMap(), context.getDialect(), context.getEntityMetaFactory()
                    ,tableNameResolver);
            visitor.visit(new MetamodelWhere(where));
            String condition = visitor.getCriteria();

            // JOIN句の追加
            fromClause.addSql(jc.getType(), tableName, tableAlias, condition);

            // 結合条件にプレースホルダーがあるとき、パラメータの値を追加する
            paramValues.addAll(visitor.getParamValues());

        }

    }



    /**
     * IDプロパティ及びバージョンを準備します。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareIdVersion() {

        if(query.getIdPropertyValues() == null && query.getVersionPropertyValue() == null) {
            // 主キーとバージョンキーの両方の指定がない場合はスキップする。
            return;

        } else if(query.getIdPropertyValues() == null && query.getVersionPropertyValue() != null) {
            // 主キーが指定されず、バージョンだけ指定されている場合
            throw new IllegalOperateException(context.getMessageFormatter().create("query.emptyIdWithVersion")
                    .format());
        }

        final SimpleWhereBuilder where = new SimpleWhereBuilder();
        final String tableAliasName = tableNameResolver.getTableAlias(query.getEntityPath());

        // IDの条件指定
        for(int i=0; i < query.getIdPropertyValues().length; i++) {
            PropertyMeta propertyMeta = query.getEntityMeta().getIdPropertyMetaList().get(i);
            String exp = String.format("%s.%s = ?", tableAliasName, propertyMeta.getColumnMeta().getName());

            ValueType valueType = propertyMeta.getValueType();
            Object value = valueType.getSqlParameterValue(query.getIdPropertyValues()[i]);

            where.exp(exp, value);
        }

        // バージョンの指定
        if(query.getVersionPropertyValue() != null) {

            PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
            String exp = String.format("%s.%s = ?", tableAliasName, propertyMeta.getColumnMeta().getName());

            ValueType valueType = propertyMeta.getValueType();
            Object value = valueType.getSqlParameterValue(query.getVersionPropertyValue());

            where.exp(exp, value);
        }

        SimpleWhereVisitor visitor = new SimpleWhereVisitor();
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());
        this.paramValues.addAll(visitor.getParamValues());

    }

    /**
     * 条件文の組み立て
     */
    private void prepareCondition() {

        if(query.getWhere() == null) {
            return;
        }

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(query.getEntityMetaMap(), context.getDialect(), context.getEntityMetaFactory(),
                tableNameResolver);
        visitor.visit(new MetamodelWhere(query.getWhere()));

        this.whereClause.addSql(visitor.getCriteria());
        this.paramValues.addAll(visitor.getParamValues());

    }

    /**
     * ORDER BY句の準備をします。
     */
    private void prepareOrderBy() {

        if (query.getOrders().isEmpty()) {
            return;
        }

        for(OrderSpecifier order : query.getOrders()) {
            PathMeta pathMeta = order.getPath().getPathMeta();
            Path<?> rootPath = pathMeta.findRootPath();
            String propertyName = pathMeta.getElement();
            Optional<PropertyMeta> propertyMeta = query.getEntityMeta().findPropertyMeta(propertyName);
            if(propertyMeta.isEmpty()) {
                throw new IllegalQueryException("unknwon property : " + propertyName);
            }

            String tableName = tableNameResolver.getTableAlias(rootPath);
            String columnName;
            if(tableName != null) {
                columnName = tableName + "." + propertyMeta.get().getColumnMeta().getName();
            } else {
                columnName = propertyMeta.get().getColumnMeta().getName();;
            }

            orderByClause.addSql(columnName + " " + order.getOrder().name());
        }

    }

    /**
     * FOR UPDATE句の準備をします。
     */
    private void prepareForUpdate() {

        if(query.getForUpdateType() == null) {
            this.forUpdateClause = "";
            return;
        }

        // LIMIT句を指定していないかのチェック
        if(query.getLimit() > 0 || query.getOffset() >= 0) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notSupportPaginationWithForUpdate")
                    .format());
        }

        final Dialect dialect = context.getDialect();
        this.forUpdateClause = dialect.getForUpdateSql(query.getForUpdateType(), query.getForUpdateWaitSeconds());

    }

    /**
     * 実行するSQLの組み立て
     */
    private void prepareSql() {

        final Dialect dialect = context.getDialect();

        final String hintComment;
        if(StringUtils.hasLength(query.getHint())) {
            hintComment = dialect.getHintComment(query.getHint());
        } else {
            hintComment = "";
        }

        String sql = "select "
                + hintComment
                + selectClause.toSql()
                + fromClause.toSql()
                + whereClause.toSql()
                + orderByClause.toSql()
                + forUpdateClause;

        if(query.getLimit() > 0 || query.getOffset() >= 0) {
            sql = dialect.convertLimitSql(sql, query.getOffset(), query.getLimit());
        }

        this.executedSql = sql;

    }

    /**
     * 件数カウントするクエリを実行します。
     *
     * @return 件数カウント
     */
    public long getCount() {
        prepare();

        return getJdbcTemplate().queryForObject(executedSql, Long.class, paramValues.toArray());
    }

    /**
     * 1件だけヒットすることを前提として検索クエリを実行します。
     *
     * @param callback エンティティマッピング後のコールバック処理
     * @return エンティティのベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    public T getSingleResult(EntityMappingCallback<T> callback) {
        prepare();

        AutoEntityRowMapper<T> rowMapper = new AutoEntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaEntityTypeMap,
                query.getJoinAssociations(), Optional.ofNullable(callback));
        return getJdbcTemplate().queryForObject(executedSql, rowMapper, paramValues.toArray());
    }

    /**
     * 1件だけヒットすることを前提として検索クエリを実行します。
     *
     * @param callback エンティティマッピング後のコールバック処理。
     * @return エンティティのベースオブジェクト。1件も対象がないときは空を返します。
     * @throws IncorrectResultSizeDataAccessException 2件以上見つかった場合にスローされます。
     */
    public Optional<T> getOptionalResult(EntityMappingCallback<T> callback) {
        prepare();

        AutoEntityRowMapper<T> rowMapper = new AutoEntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaEntityTypeMap,
                query.getJoinAssociations(), Optional.ofNullable(callback));
        final List<T> ret = getJdbcTemplate().query(executedSql, rowMapper, paramValues.toArray());
        if(ret.isEmpty()) {
            return Optional.empty();
        } else if(ret.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, ret.size());
        } else {
            return Optional.of(ret.get(0));
        }
    }

    /**
     * 検索クエリを実行します。
     *
     * @param callback エンティティマッピング後のコールバック処理。
     * @return 検索してヒットした複数のベースオブジェクト。1件も対象がないときは空のリストを返します。
     */
    public List<T> getResultList(EntityMappingCallback<T> callback) {
        prepare();

        AutoEntityRowMapper<T> rowMapper = new AutoEntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaEntityTypeMap,
                query.getJoinAssociations(), Optional.ofNullable(callback));
        return getJdbcTemplate().query(executedSql, rowMapper, paramValues.toArray());
    }

    /**
     * 結果を {@link Stream} で返す検索クエリを実行します。
     * @param callback エンティティマッピング後のコールバック処理。
     * @return 問い合わせの結果
     */
    public Stream<T> getResultStream(EntityMappingCallback<T> callback) {
        prepare();

        AutoEntityRowMapper<T> rowMapper = new AutoEntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaEntityTypeMap,
                query.getJoinAssociations(), Optional.ofNullable(callback));
        return getJdbcTemplate().queryForStream(executedSql, rowMapper, paramValues.toArray());
    }

    /**
     * {@link JdbcTemplate}を取得します。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateBuilder.create(context.getDataSource(), context.getJdbcTemplateProperties())
                .queryTimeout(query.getQueryTimeout())
                .fetchSize(query.getFetchSize())
                .maxRows(query.getMaxRows())
                .build();
    }

}
