package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.mapper.EntityMappingCallback;
import com.github.mygreen.sqlmapper.core.mapper.EntityRowMapper;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.FromClause;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.OrderByClause;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.core.query.SelectClause;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhere;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhereVisitor;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereBuilder;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereVisitor;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;


public class AutoSelectExecutor<T> extends QueryExecutorSupport<AutoSelect<T>> {

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
     * 抽出対象のプロパティ情報
     */
    private PropertyMeta[] targetPropertyMetaList;

    /**
     * インスタンスの作成
     *
     * @param query クエリ情報
     * @param counting カウント用のクエリかどうか
     */
    public AutoSelectExecutor(AutoSelect<T> query, boolean counting) {
        super(query);
        this.counting = counting;
    }

    @Override
    public void prepare() {

        prepareTableAlias();
        prepareTarget();
        prepareIdVersion();
        prepareCondition();
        prepareOrderBy();
        prepareForUpdate();

        prepareSql();

        completed();
    }

    /**
     * テーブルの別名を準備します。
     */
    private void prepareTableAlias() {

        // FROM句指定のテーブル
        tableNameResolver.prepareTableAlias(query.getEntityPath());

        //TODO: JOINテーブルのエイリアス
    }

    /**
     * 抽出対象のエンティティやカラム情報を準備します。
     * {@link SelectClause}を準備します。
     */
    private void prepareTarget() {

        if(counting) {
            // 件数取得の場合
            String sql = context.getDialect().getCountSql();
            selectClause.addSql(sql);

        } else {

            List<PropertyMeta> selectedPropertyMetaList = new ArrayList<>();
            List<Integer> idIndexList = new ArrayList<Integer>();

            // 通常のカラム指定の場合
            for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {
                final String propertyName = propertyMeta.getName();

                if(propertyMeta.isTransient()) {
                    continue;
                }

                //TODO: 他のテーブルのプロパティの可能性があるのでエンティティの比較も行うべき
                if(QueryUtils.containsByPropertyName(query.getExcludesProperties(), propertyName)) {
                    continue;
                }

                if(!query.getIncludesProperties().isEmpty()
                        && !QueryUtils.containsByPropertyName(query.getIncludesProperties(), propertyName)) {
                    continue;
                }

                //TODO: JOINしているときは、他のテーブルのカラムの可能性がある。
                String tableAlias = tableNameResolver.getTableAlias(query.getEntityPath());
                selectClause.addSql(tableAlias, propertyMeta.getColumnMeta().getName());
                selectedPropertyMetaList.add(propertyMeta);

                if(propertyMeta.isId()) {
                    // 主キーのインデックスの位置を保存
                    idIndexList.add(selectedPropertyMetaList.size()-1);
                }

            }

            targetPropertyMetaList = selectedPropertyMetaList.toArray(new PropertyMeta[selectedPropertyMetaList.size()]);

        }

        // from句の指定
        fromClause.addSql(query.getEntityMeta().getTableMeta().getFullName(), tableNameResolver.getTableAlias(query.getEntityPath()));

    }

    /**
     * IDプロパティ及びバージョンを準備します。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareIdVersion() {

        if(query.getIdPropertyValues() == null && query.getVersionPropertyValue() != null) {
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

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(query.getEntityMeta(), context.getDialect(), tableNameResolver);
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
            String propertyName = order.getPath().getPathMeta().getElement();
            Optional<PropertyMeta> propertyMeta = query.getEntityMeta().getPropertyMeta(propertyName);

            String tableAlias = tableNameResolver.getTableAlias(order.getPath().getPathMeta().getParent());
            if(!StringUtils.hasLength(tableAlias)) {
                //TODO: 例外処理

            }

            propertyMeta.ifPresent(p -> {
                String orderBy = String.format("%s.%s %s", tableAlias, p.getColumnMeta().getName(), order.getOrder().name());
                orderByClause.addSql(orderBy);
            });
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
        if(query.getLimit() > 0 || query.getOffset() > 0) {
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

        String sql = "SELECT "
                + hintComment
                + selectClause.toSql()
                + fromClause.toSql()
                + whereClause.toSql()
                + orderByClause.toSql()
                + forUpdateClause;

        if(query.getLimit() > 0 || query.getLimit() == 0 && query.getOffset() > 0) {
            sql = dialect.convertLimitSql(sql, query.getOffset(), query.getLimit());
        }

        this.executedSql = sql;

    }


    public long getCount() {
        assertNotCompleted("getCount");

        return context.getJdbcTemplate().queryForObject(executedSql, Long.class, paramValues.toArray());


    }

    public T getSingleResult(EntityMappingCallback<T> callback) {
        assertNotCompleted("getSingleResult");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList,
                Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForObject(executedSql, rowMapper, paramValues.toArray());
    }

    public Optional<T> getOptionalResult(EntityMappingCallback<T> callback) {
        assertNotCompleted("getOptionalResult");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList,
                Optional.ofNullable(callback));
        final List<T> ret = context.getJdbcTemplate().query(executedSql, rowMapper, paramValues.toArray());
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    public List<T> getResultList(EntityMappingCallback<T> callback) {
        assertNotCompleted("getResultList");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList,
                Optional.ofNullable(callback));
        return context.getJdbcTemplate().query(executedSql, rowMapper, paramValues.toArray());
    }

    public Stream<T> getResultStream(EntityMappingCallback<T> callback) {
        assertNotCompleted("getResultStream");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList,
                Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForStream(executedSql, rowMapper, paramValues.toArray());

    }

}
