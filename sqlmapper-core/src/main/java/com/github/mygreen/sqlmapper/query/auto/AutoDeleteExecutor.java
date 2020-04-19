package com.github.mygreen.sqlmapper.query.auto;

import static com.github.mygreen.sqlmapper.util.QueryUtils.*;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.query.WhereClause;
import com.github.mygreen.sqlmapper.type.ValueType;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AutoDeleteExecutor<T> extends QueryExecutorBase {

    private final AutoDelete<T> query;

    /**
     * where句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * クエリのパラメータ
     */
    private final MapSqlParameterSource paramSource = new MapSqlParameterSource();

    public AutoDeleteExecutor(AutoDelete<T> query) {
        super(query.getContext());
        this.query = query;
    }

    @Override
    public void prepare() {

        prepareCondition();

        completed();

    }

    /**
     * 条件文の組み立てを行います
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareCondition() {
        // 主キーを条件分として組み立てます
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {

            final String propertyName = propertyMeta.getName();
            final String paramName = "_" + propertyName;

            whereClause.addAndSql(EQ(propertyMeta.getColumnMeta().getName(), ":" + paramName));

            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            ValueType valueType = context.getDialect().getValueType(propertyMeta);
            valueType.bindValue(propertyValue, paramSource, paramName);


        }

        // 楽観的排他チェックを行うときは、バージョンキーも条件に加えます。
        if(isOptimisticLock()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();

            final String propertyName = propertyMeta.getName();
            final String paramName = "_" + propertyName;

            whereClause.addAndSql(EQ(propertyMeta.getColumnMeta().getName(), ":" + paramName));

            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity());
            ValueType valueType = context.getDialect().getValueType(propertyMeta);
            valueType.bindValue(propertyValue, paramSource, paramName);

        }
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

        assertNotCompleted("execute");

        final String sql = "DELETE FROM "
                + query.getEntityMeta().getTableMeta().getFullName()
                + whereClause.toSql();

        final int rows = context.getNamedParameterJdbcTemplate().update(sql, paramSource);
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
        if(!query.isSuppresOptimisticLockException() && rows == 0) {
            throw new OptimisticLockingFailureException(context.getMessageBuilder().create("query.alreadyUpdate")
                    .var("entity", query.getEntity())
                    .format());
        }
    }
}
