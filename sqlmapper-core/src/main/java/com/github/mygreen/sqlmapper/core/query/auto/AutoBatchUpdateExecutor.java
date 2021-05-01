package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;
import com.github.mygreen.sqlmapper.core.query.SetClause;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutoBatchUpdateExecutor extends QueryExecutorSupport {

    private final AutoBatchUpdateImpl<?> query;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ - エンティティごとの設定
     */
    private List<Object>[] batchParams;

    /**
     * SET句
     */
    private final SetClause setClause = new SetClause();

    /**
     * WHERE句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * 更新対象のプロパティの個数
     */
    private int targetPropertyCount = 0;

    public AutoBatchUpdateExecutor(AutoBatchUpdateImpl<?> query) {
        super(query.getContext());
        this.query = query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void prepare() {
        this.batchParams = new List[query.getEntitySize()];

        prepareSetClause();
        prepareWhereClause();

        prepareSql();

        completed();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareSetClause() {

        final int dataSize = query.getEntitySize();

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {
            final String propertyName = propertyMeta.getName();
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

            this.targetPropertyCount++;


            // SET句の組み立て
            setClause.addSql(propertyMeta.getColumnMeta().getName(), "?");

            final ValueType valueType = propertyMeta.getValueType();

            // 各レコードのパラメータを作成する。
            for(int i=0; i < dataSize; i++) {

                final List<Object> params = QueryUtils.get(batchParams, i);
                final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity(i));
                params.add(valueType.getSqlParameterValue(propertyValue));

            }

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

        final int dataSize = query.getEntitySize();

        // WHERE句の準備 - 主キー
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {

            whereClause.addAndSql(propertyMeta.getColumnMeta().getName() + " = ?");

            final ValueType valueType = propertyMeta.getValueType();

            // 各レコードのパラメータを作成する。
            for(int i=0; i < dataSize; i++) {
                final List<Object> params = QueryUtils.get(batchParams, i);
                final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity(i));
                params.add(valueType.getSqlParameterValue(propertyValue));

            }
        }

        // WHERE句の準備 - バージョンキー
        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            final PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();

            whereClause.addAndSql(propertyMeta.getColumnMeta().getName() + " = ?");

            final ValueType valueType = propertyMeta.getValueType();

            // 各レコードのパラメータを作成する。
            for(int i=0; i < dataSize; i++) {
                final List<Object> params = QueryUtils.get(batchParams, i);
                final Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity(i));
                params.add(valueType.getSqlParameterValue(propertyValue));
            }
        }

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
    public int[] execute() {
        assertNotCompleted("executeBatchUpdate");

        if(targetPropertyCount == 0) {
            log.warn(context.getMessageFormatter().create("query.skipUpdateWithNoProperty").format());
            return new int[query.getEntitySize()];
        }

        int[] res = context.getJdbcTemplate().batchUpdate(executedSql, QueryUtils.convertBatchArgs(batchParams));

        final int dataSize = query.getEntitySize();
        for(int i=0; i < dataSize; i++) {
            final Object entity = query.getEntity(i);

            if(isOptimisticLock()) {
                validateRows(entity, res[i]);
            }

            if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
                incrementVresion(entity);
            }
        }

        return res;

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
     * @param entity エンティティのインスタンス
     */
    private void incrementVresion(final Object entity) {

        PropertyMeta propertyMeta = query.getEntityMeta().getVersionPropertyMeta().get();
        Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, entity);

        propertyValue = NumberConvertUtils.incrementNumber(propertyMeta.getPropertyType(), propertyValue);
        PropertyValueInvoker.setPropertyValue(propertyMeta, entity, propertyValue);

    }

    /**
     * 更新対象のレコード数のチェック
     * @param entity エンティティのインスタンス
     * @param rows 更新したレコード数
     */
    private void validateRows(final Object entity, final int rows) {
        if(!query.isSuppresOptimisticLockException() && rows == 0) {
            throw new OptimisticLockingFailureException(context.getMessageFormatter().create("query.alreadyUpdate")
                    .param("entity", entity)
                    .format());
        }
    }
}
