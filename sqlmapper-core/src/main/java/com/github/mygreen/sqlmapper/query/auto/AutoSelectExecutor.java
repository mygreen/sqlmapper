package com.github.mygreen.sqlmapper.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.mapper.EntityIterationResultSetExtractor;
import com.github.mygreen.sqlmapper.mapper.EntityRowMapper;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.query.FromClause;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.IterationCallback;
import com.github.mygreen.sqlmapper.query.OrderByClause;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.query.SelectClause;
import com.github.mygreen.sqlmapper.query.WhereClause;
import com.github.mygreen.sqlmapper.util.QueryUtils;
import com.github.mygreen.sqlmapper.where.SimpleWhere;
import com.github.mygreen.sqlmapper.where.WhereVisitor;
import com.github.mygreen.sqlmapper.where.WhereVisitorParamContext;


public class AutoSelectExecutor<T> extends QueryExecutorBase {

    private final AutoSelect<T> query;

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
     * クエリのパラメータ
     */
    private final MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /**
     * クエリ条件のパラメータに関する情報
     */
    private final WhereVisitorParamContext paramContext = new WhereVisitorParamContext(paramSource);

    /**
     * 抽出対象のプロパティ情報
     */
    private PropertyMeta[] targetPropertyMetaList;

    public AutoSelectExecutor(AutoSelect<T> query, boolean counting) {
        super(query.getContext());
        this.query = query;
        this.counting = counting;
    }

    @Override
    public void prepare() {
        prepareTarget();
        prepareIdVersion();
        prepareCondition();
        prepareOrderBy();

        completed();
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

                if(query.getExcludesProperties().contains(propertyName)) {
                    continue;
                }

                if(!query.getIncludesProperties().isEmpty() && !query.getIncludesProperties().contains(propertyName)) {
                    continue;
                }

                selectClause.addSql(propertyMeta.getColumnMeta().getName());
                selectedPropertyMetaList.add(propertyMeta);

                if(propertyMeta.isId()) {
                    // 主キーのインデックスの位置を保存
                    idIndexList.add(selectedPropertyMetaList.size()-1);
                }

            }

            targetPropertyMetaList = selectedPropertyMetaList.toArray(new PropertyMeta[selectedPropertyMetaList.size()]);

        }

        // from句の指定
        fromClause.addSql(query.getEntityMeta().getTableMeta().getFullName(), null);

    }

    /**
     * IDプロパティ及びバージョンを準備します。
     */
    private void prepareIdVersion() {

        if(query.getIdPropertyValues() == null && query.getVersionPropertyValue() != null) {
            // 主キーが指定されず、バージョンだけ指定されている場合
            throw new IllegalOperateException(context.getMessageBuilder().create("query.emptyIdWithVersion")
                    .format());
        }

        final SimpleWhere where = new SimpleWhere();

        // IDの条件指定
        for(int i=0; i < query.getIdPropertyValues().length; i++) {
            PropertyMeta propertyMeta = query.getEntityMeta().getIdPropertyMetaList().get(i);
            where.eq(propertyMeta.getName(), query.getIdPropertyValues()[i]);
        }

        // バージョンの指定
        if(query.getVersionPropertyValue() != null) {
            where.eq(query.getEntityMeta().getVersionPropertyMeta().get().getName(), query.getVersionPropertyValue());
        }

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta(), paramContext);
        where.accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());


    }

    /**
     * 条件文の組み立て
     */
    private void prepareCondition() {

        if(query.getCriteria() == null) {
            return;
        }

        WhereVisitor visitor = new WhereVisitor(query.getEntityMeta(), paramContext);
        query.getCriteria().accept(visitor);

        this.whereClause.addSql(visitor.getCriteria());

    }

    /**
     * ORDER BY句の準備をします。
     */
    private void prepareOrderBy() {

        if (StringUtils.isEmpty(query.getOrderBy())) {
            return;
        }

        orderByClause.addSql(QueryUtils.convertCriteria(query.getOrderBy(), query.getEntityMeta()));

    }

    public long getCount() {
        assertNotCompleted("getCount");

        final String sql = buildSql();
        return context.getNamedParameterJdbcTemplate().queryForObject(sql, paramSource, Long.class);


    }

    public T getSingleResult() {
        assertNotCompleted("getSingleResult");

        final String sql = buildSql();

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList);
        return context.getNamedParameterJdbcTemplate().queryForObject(sql, paramSource, rowMapper);
    }

    public Optional<T> getOptionalResult() {
        assertNotCompleted("getOptionalResult");

        final String sql = buildSql();

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList);
        final List<T> ret = context.getNamedParameterJdbcTemplate().query(sql, paramSource, rowMapper);
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    public List<T> getResultList() {
        assertNotCompleted("getResultList");

        final String sql = buildSql();

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList);
        return context.getNamedParameterJdbcTemplate().query(sql, paramSource, rowMapper);
    }

    public <R> R iterate(IterationCallback<T, R> callback) {

        assertNotCompleted("iterate");

        final String sql = buildSql();

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getBaseClass(), targetPropertyMetaList);
        ResultSetExtractor<R> extractor = new EntityIterationResultSetExtractor<T,R>(rowMapper, callback);

        return context.getNamedParameterJdbcTemplate().query(sql, paramSource, extractor);

    }

    /**
     * クエリの組み立て
     * @return クエリ
     */
    private String buildSql() {

        final Dialect dialect = context.getDialect();

        final String hintComment;
        if(!StringUtils.isEmpty(query.getHint())) {
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
                ;

        if(query.getLimit() > 0 || query.getLimit() == 0 && query.getOffset() > 0) {
            sql = dialect.convertLimitSql(sql, query.getOffset(), query.getLimit());
        }

        return sql;

    }
}
