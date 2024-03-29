package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereBuilder;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereVisitor;

/**
 * 削除を行うSQLを自動生成するクエリを実行します。
 * {@link AutoDeleteImpl}のクエリ実行処理の移譲先です。
 *
 * @author T.TSUCHIE
 *
 */
public class AutoDeleteExecutor {

    /**
     * クエリ情報
     */
    private final AutoDeleteImpl<?> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * where句
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
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public AutoDeleteExecutor(AutoDeleteImpl<?> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {

        prepareWhereClause();
        prepareSql();
    }

    /**
     * 条件文の組み立てを行います
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareWhereClause() {

        final SimpleWhereBuilder where = new SimpleWhereBuilder();

        // 主キーを条件分として組み立てます
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
            String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

            Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity());
            ValueType valueType = propertyMeta.getValueType();
            Object value = valueType.getSqlParameterValue(propertyValue);

            where.exp(exp, value);
        }

        // 楽観的排他チェックを行うときは、バージョンキーも条件に加えます。
        if(isOptimisticLock()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
            String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

            Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity());
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
     * 実行するSQLを組み立てます。
     */
    private void prepareSql() {

        final String sql = "delete from "
                + query.getEntityMeta().getTableMeta().getFullName()
                + whereClause.toSql();

        this.executedSql = sql;
    }

    /**
     * 楽観的同時実行制御を行っている場合は<code>true</code>を返します。
     * @return 楽観的同時実行制御を行っている場合は<code>true</code>
     */
    private boolean isOptimisticLock() {
        return !query.isIgnoreVersion() && query.getEntityMeta().hasVersionPropertyMeta();
    }

    /**
     * 削除処理を実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        prepare();

        final int rows = getJdbcTemplate().update(executedSql, paramValues.toArray());
        if(isOptimisticLock()) {
            validateRows(rows);
        }
        return rows;

    }

    /**
     * {@link JdbcTemplate}を取得します。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateBuilder.create(context.getDataSource(), context.getJdbcTemplateProperties())
                .queryTimeout(query.getQueryTimeout())
                .build();
    }

    /**
     * 更新対象のレコード数のチェック
     * @param rows 更新したレコード数
     */
    private void validateRows(final int rows) {
        if(!query.isSuppresOptimisticLockException() && rows == 0) {
            throw new OptimisticLockingFailureException(context.getMessageFormatter().create("query.alreadyDelete")
                    .param("entity", query.getEntity())
                    .format());
        }
    }
}
