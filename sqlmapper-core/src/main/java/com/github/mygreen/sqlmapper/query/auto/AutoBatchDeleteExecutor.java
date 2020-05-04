package com.github.mygreen.sqlmapper.query.auto;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.query.WhereClause;
import com.github.mygreen.sqlmapper.where.WhereBuilder;
import com.github.mygreen.sqlmapper.where.WhereVisitor;
import com.github.mygreen.sqlmapper.where.NamedParameterContext;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AutoBatchDeleteExecutor extends QueryExecutorBase {

    private final AutoBatchDelete<?> query;

    /**
     * where句
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
    private final NamedParameterContext paramContext = new NamedParameterContext(paramSource);

    public AutoBatchDeleteExecutor(AutoBatchDelete<?> query) {
        super(query.getContext());
        this.query = query;
    }

    @Override
    public void prepare() {

        prepareWhereClause();
        prepareSql();

        completed();

    }

    /**
     * 条件文の組み立てを行います
     */
    private void prepareWhereClause() {

        final WhereBuilder where = new WhereBuilder();

        final int dataSize = query.getEntitySize();

        // 各レコードを１つの条件分でつなげる
        for(int i=0; i < dataSize; i++) {
            final Object entity = query.getEntity(i);

            // 主キーを条件分として組み立てます
            for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
                Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, entity);
                where.eq(propertyMeta.getName(), propertyValue);
            }

            // 楽観的排他チェックを行うときは、バージョンキーも条件に加えます。
            if(isOptimisticLock()) {
                final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
                Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, entity);
                where.eq(propertyMeta.getName(), propertyValue);
            }

            where.or();

        }

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta(), paramContext);
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());
    }

    /**
     * 実行するSQLを組み立てます。
     */
    public void prepareSql() {

        final String sql = "DELETE FROM "
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

        assertNotCompleted("executeBatchDelete");

        final int rows = context.getNamedParameterJdbcTemplate().update(executedSql, paramSource);
        if(isOptimisticLock()) {
            validateRows(rows);
        }
        return rows;


    }

    /**
     * 更新対象のレコード数のチェック
     * @param rows 更新したレコード数
     */
    private void validateRows(final int rows) {
        if(!query.isSuppresOptimisticLockException() && rows != query.getEntitySize()) {
            throw new OptimisticLockingFailureException(context.getMessageBuilder().create("query.alreadyBatchUpdate")
                    .format());
        }
    }
}
