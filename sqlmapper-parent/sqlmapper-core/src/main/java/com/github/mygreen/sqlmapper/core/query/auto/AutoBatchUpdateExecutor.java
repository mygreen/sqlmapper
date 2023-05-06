package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;
import com.github.mygreen.sqlmapper.core.query.SetClause;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * バッチ更新を行うSQLを自動生成するクエリを実行します。
 * {@link AutoBatchUpdateImpl}のクエリ実行処理の移譲先です。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class AutoBatchUpdateExecutor {

    /**
     * クエリ情報
     */
    private final AutoBatchUpdateImpl<?> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

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

    /**
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public AutoBatchUpdateExecutor(AutoBatchUpdateImpl<?> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    @SuppressWarnings("unchecked")
    private void prepare() {
        this.batchParams = new List[query.getEntitySize()];

        prepareSetClause();
        prepareWhereClause();

        prepareSql();
    }

    /**
     * SQLのUPDATE文のSET句を準備します。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareSetClause() {

        final int dataSize = query.getEntitySize();

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

            if(!isTargetProperty(propertyMeta)) {
                continue;
            }

            this.targetPropertyCount++;


            // SET句の組み立て
            setClause.addSql(propertyMeta.getColumnMeta().getName(), "?");

            final ValueType valueType = propertyMeta.getValueType();

            // 各レコードのパラメータを作成する。
            for(int i=0; i < dataSize; i++) {

                final List<Object> params = QueryUtils.get(batchParams, i);
                final Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity(i));
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

    /**
     * 更新対象のプロパティか判定します。
     * @param propertyMeta プロパティ情報
     * @return 更新対象のとき、{@literal true} を返します。
     */
    private boolean isTargetProperty(final PropertyMeta propertyMeta) {

        // 主キーは検索条件に入れるので対象外
        if(propertyMeta.isId() || !propertyMeta.getColumnMeta().isUpdatable()) {
            return false;
        }

        /*
         * バージョンキーは通常は更新対象となるため、通常の条件では対象外。
         * ただし、includeVersion = true のときは更新対象とする。
         */
        if(propertyMeta.isVersion() && !query.isIncludeVersion()) {
            return false;
        }

        if(!propertyMeta.getColumnMeta().isUpdatable()) {
            return false;
        }

        if(propertyMeta.isTransient()) {
            return false;
        }

        final String propertyName = propertyMeta.getName();
        if(query.getIncludesProperties().contains(propertyName)) {
            return true;
        }

        if(query.getExcludesProperties().contains(propertyName)) {
            return false;
        }

        // 更新対象が指定されているときは、その他はすべて更新対象外とする。
        return query.getIncludesProperties().isEmpty();

    }

    /**
     * SQLのUPDATE文のWHERE句を準備します。
     */
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
                final Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity(i));
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
                final Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity(i));
                params.add(valueType.getSqlParameterValue(propertyValue));
            }
        }

    }

    /**
     * 実行するSQLを組み立てます
     */
    private void prepareSql() {
        final String sql = "update "
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
        prepare();

        if(targetPropertyCount == 0) {
            log.warn(context.getMessageFormatter().create("query.skipUpdateWithNoProperty").format());
            return new int[query.getEntitySize()];
        }

        final List<Object[]> batchArgs = QueryUtils.convertBatchArgs(batchParams);
        context.getSqlLogger().outBatch(executedSql, batchArgs);

        int[] res = getJdbcTemplate().batchUpdate(executedSql, batchArgs);

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
     * {@link JdbcTemplate}を取得します。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateBuilder.create(context.getDataSource(), context.getJdbcTemplateProperties())
                .queryTimeout(query.getQueryTimeout())
                .build();
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
        Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, entity);

        propertyValue = NumberConvertUtils.incrementNumber(propertyMeta.getPropertyType(), propertyValue);
        PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, entity, propertyValue);

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
