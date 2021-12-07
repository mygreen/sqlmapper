package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
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
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereBuilder;
import com.github.mygreen.sqlmapper.core.where.simple.SimpleWhereVisitor;

import lombok.extern.slf4j.Slf4j;

/**
 * 更新を行うSQLを自動生成するクエリを実行します。
 * {@link AutoUpdateImpl}のクエリ実行処理の移譲先です。
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class AutoUpdateExecutor {

    /**
     * クエリ情報
     */
    private final AutoUpdateImpl<?> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

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

    /**
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public AutoUpdateExecutor(AutoUpdateImpl<?> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {
        prepareSetClause();
        prepareWhereClause();

        prepareSql();

    }

    /**
     * UPDATE文のSET句を準備します。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareSetClause() {

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

            final Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity());
            if(!isTargetProperty(propertyMeta, propertyValue)) {
                continue;
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

    /**
     * 更新対象のプロパティか判定します。
     * @param propertyMeta プロパティ情報
     * @param propertyValue 更新対象の値
     * @return 更新対象のとき、{@literal true} を返します。
     */
    private boolean isTargetProperty(final PropertyMeta propertyMeta, final Object propertyValue) {

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

        // nullは対象外とするとき
        if(query.isExcludesNull() && propertyValue == null) {
            return false;
        }

        // 比較対象と同じ値は更新対象外
        if(query.getBeforeStates() != null) {
            final Object oldValue = query.getBeforeStates().get(propertyName);
            if (propertyValue == oldValue) {
                return false;
            }
            if (propertyValue != null && propertyValue.equals(oldValue)) {
                return false;
            }
        }

        // 更新対象が指定されているときは、その他はすべて更新対象外とする。
        return query.getIncludesProperties().isEmpty();

    }

    /**
     * UPDATE文のWHERE句を準備します。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareWhereClause() {

        final SimpleWhereBuilder where = new SimpleWhereBuilder();

        // WHERE句の準備 - 主キー
        for(PropertyMeta propertyMeta : query.getEntityMeta().getIdPropertyMetaList()) {
            String exp = String.format("%s = ?", propertyMeta.getColumnMeta().getName());

            Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, query.getEntity());
            ValueType valueType = propertyMeta.getValueType();
            Object value = valueType.getSqlParameterValue(propertyValue);

            where.exp(exp, value);
        }

        // WHERE句の準備 - バージョンキー
        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
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
     *
     * @return 更新したレコード件数です。更新対象のプロパティ（カラム）がない場合は {@literal 0} を返します。
     * @throws OptimisticLockingFailureException 楽観的排他制御を行う場合に該当するレコードが存在しない場合にスローされます。
     */
    public int execute() {
        prepare();

        if(targetPropertyCount == 0) {
            log.warn(context.getMessageFormatter().create("query.skipUpdateWithNoProperty").format());
            return 0;
        }

        final int rows = getJdbcTemplate().update(executedSql, paramValues.toArray());
        if(isOptimisticLock()) {
            validateRows(rows);
        }

        if(!query.isIncludeVersion() && query.getEntityMeta().hasVersionPropertyMeta()) {
            incrementVresion();
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
        Object propertyValue = PropertyValueInvoker.getEmbeddedPropertyValue(propertyMeta, entity);

        propertyValue = NumberConvertUtils.incrementNumber(propertyMeta.getPropertyType(), propertyValue);
        PropertyValueInvoker.setEmbeddedPropertyValue(propertyMeta, entity, propertyValue);

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
