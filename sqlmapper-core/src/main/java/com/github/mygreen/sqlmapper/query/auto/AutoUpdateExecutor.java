package com.github.mygreen.sqlmapper.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.query.SetClause;
import com.github.mygreen.sqlmapper.query.WhereClause;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.where.SimpleWhere;
import com.github.mygreen.sqlmapper.where.WhereVisitor;
import com.github.mygreen.sqlmapper.where.WhereVisitorParamContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutoUpdateExecutor extends QueryExecutorBase {

    private final AutoUpdate<?> query;

    /**
     * SET句
     */
    private final SetClause setClause = new SetClause();

    /**
     * WHERE句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private final MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /**
     * クエリ条件のパラメータに関する情報
     */
    private final WhereVisitorParamContext paramContext = new WhereVisitorParamContext(paramSource);

    /**
     * 更新対象のプロパティの個数
     */
    private int targetPropertyCount = 0;

    public AutoUpdateExecutor(AutoUpdate<?> query) {
        super(query.getContext());
        this.query = query;
    }

    @Override
    public void prepare() {
        prepareSetClause();
        prepareCondition();

        prepareSql();

        completed();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareSetClause() {

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
            final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());

            if(propertyMeta.isId() || !propertyMeta.getColumnMeta().isUpdatable()) {
                continue;
            }

            if (propertyMeta.isVersion() && !query.isIncludeVersion()) {
                continue;
            }

            if(query.getExcludesProperties().contains(propertyName)) {
                continue;
            }

            if(!query.getIncludesProperties().isEmpty() && !query.getIncludesProperties().contains(propertyName)) {
                continue;
            }

            if (query.isExcludesNull() && propertyValue == null) {
                continue;
            }

            if (query.getBeforeStates() != null) {
                final Object oldValue = query.getBeforeStates().get(propertyName);
                if (propertyValue == oldValue) {
                    continue;
                }
                if (propertyValue != null && propertyValue.equals(oldValue)) {
                    continue;
                }
            }

            this.targetPropertyCount++;


            // SET句の組み立て
            final String paramName = "_" + propertyName;
            setClause.addSql(propertyMeta.getColumnMeta().getName(), ":" + paramName);

            ValueType valueType = context.getDialect().getValueType(propertyMeta);
            valueType.bindValue(propertyValue, paramSource, paramName);

        }

        // バージョンカラムの更新用のSET句の準備
        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
            final String columnName = propertyMeta.getColumnMeta().getName();
            setClause.addSql(columnName, columnName + " + 1");
        }
    }

    private void prepareCondition() {

        final SimpleWhere where = new SimpleWhere();

        // WHERE句の準備 - 主キー
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
            final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            where.eq(propertyMeta.getName(), propertyValue);
        }

        // WHERE句の準備 - バージョンキー
        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();

            final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            where.eq(propertyMeta.getName(), propertyValue);
        }

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta(), paramContext);
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());

    }

    /**
     * 実行するSQLを組み立てます
     */
    private void prepareSql() {
        final String sql = "UPDATE "
                + setClause.toSql()
                + whereClause.toSql();

        this.executedSql = sql;
    }

    /**
     * 更新処理を実行します。
     * @return 更新したレコード件数です。
     */
    public int execute() {
        assertNotCompleted("execute");

        if(targetPropertyCount > 0) {
            log.warn(context.getMessageBuilder().create("query.skipUpdateWithNoProperty").format());
            return 0;
        }

        final int rows = context.getNamedParameterJdbcTemplate().update(executedSql, paramSource);
        if(isOptimisticLock()) {
            validateRows(rows);
        }

        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            incrementVresion();
        }

        return rows;

    }

    /**
     * 楽観的同時実行制御を行っている場合は<code>true</code>を返します。
     * @return 楽観的同時実行制御を行っている場合は<code>true</code>
     */
    private boolean isOptimisticLock() {
        return !query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta();
    }

    /**
     * バージョンキーをインクリメントする。
     */
    private void incrementVresion() {

        final Object entity = query.getEntity();

        PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
        Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, entity);

        propertyValue = NumberConvertUtils.incrementNumber(propertyMeta.getPropertyType(), propertyValue);
        PropertyValueInvoker.setPropertyValue(propertyMeta, entity, propertyValue);

    }

    /**
     * 更新対象のレコード数のチェック
     * @param rows 更新したレコード数
     */
    private void validateRows(final int rows) {
        if(!query.isSuppresOptimisticLockException() && rows == 0) {
            throw new OptimisticLockingFailureException(context.getMessageBuilder().create("query.alreadyUpdate")
                    .var("entity", query.getEntity())
                    .format());
        }
    }
}
