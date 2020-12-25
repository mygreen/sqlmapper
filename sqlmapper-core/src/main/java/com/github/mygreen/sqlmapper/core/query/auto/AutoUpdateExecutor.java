package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.core.query.SetClause;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereBuilder;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereVisitor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutoUpdateExecutor extends QueryExecutorSupport<AutoUpdate<?>> {

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
     * クエリのパラメータです。
     */
    private final List<Object> paramValues = new ArrayList<>();

    /**
     * 更新対象のプロパティの個数
     */
    private int targetPropertyCount = 0;

    public AutoUpdateExecutor(AutoUpdate<?> query) {
        super(query);
    }

    @Override
    public void prepare() {
        prepareSetClause();
        prepareWhereClause();

        prepareSql();

        completed();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareSetClause() {

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
            final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());

            // 主キーは検索条件に入れるので対象外
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
            setClause.addSql(propertyMeta.getColumnMeta().getName(), "?");

            final ValueType valueType = propertyMeta.getValueType();
            paramValues.add(valueType.getSqlParameterValue(propertyValue));

        }

        // バージョンカラムの更新用のSET句の準備
        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
            final String columnName = propertyMeta.getColumnMeta().getName();
            setClause.addSql(columnName, columnName + " + 1");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareWhereClause() {

        final SimpleWhereBuilder where = new SimpleWhereBuilder();

        // WHERE句の準備 - 主キー
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
            String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            ValueType valueType = propertyMeta.getValueType();
            Object value = valueType.getSqlParameterValue(propertyValue);

            where.exp(exp, value);
        }

        // WHERE句の準備 - バージョンキー
        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
            String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            ValueType valueType = propertyMeta.getValueType();
            Object value = valueType.getSqlParameterValue(propertyValue);

            where.exp(exp, value);
        }

        SimpleWhereVisitor visitor = new SimpleWhereVisitor();
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());
        this.paramValues.addAll(visitor.getParamValues());

    }

    /**
     * 実行するSQLを組み立てます
     */
    private void prepareSql() {
        final String sql = "UPDATE "
                + query.getEntityMeta().getTableMeta().getFullName()
                + setClause.toSql()
                + whereClause.toSql();

        this.executedSql = sql;
    }

    /**
     * 更新処理を実行します。
     * @return 更新したレコード件数です。
     */
    public int execute() {
        assertNotCompleted("executeUpdate");

        if(targetPropertyCount == 0) {
            log.warn(context.getMessageFormatter().create("query.skipUpdateWithNoProperty").format());
            return 0;
        }

        final int rows = context.getJdbcTemplate().update(executedSql, paramValues.toArray());
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
            throw new OptimisticLockingFailureException(context.getMessageFormatter().create("query.alreadyUpdate")
                    .param("entity", query.getEntity())
                    .format());
        }
    }
}
