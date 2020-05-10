package com.github.mygreen.sqlmapper.query.sql;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.mapper.EntityIterationResultSetExtractor;
import com.github.mygreen.sqlmapper.mapper.EntityRowMapper;
import com.github.mygreen.sqlmapper.query.IterationCallback;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.sql.MapAcessor;
import com.github.mygreen.sqlmapper.sql.SqlContext;

public class SqlSelectExecutor<T> extends QueryExecutorBase {

    private final SqlSelect<T> query;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private MapSqlParameterSource paramSource;

    public SqlSelectExecutor(SqlSelect<T> query) {
        super(query.getContext());
        this.query = query;
    }

    @Override
    public void prepare() {

        prepareSql();


        completed();

    }

    private void prepareSql() {

        final Dialect dialect = context.getDialect();

        final SqlContext sqlContext = new SqlContext();
        sqlContext.setDialect(dialect);
        sqlContext.setPropertyAccessor(createPropertyAccessor());
        sqlContext.setValueTypeRegistry(context.getValueTypeRegistry());

        query.getNode().accept(sqlContext);

        this.executedSql = sqlContext.getSql();

        this.paramSource = sqlContext.getBindParameter().getParamSource();

    }

    /**
     * SQLテンプレート中のバインド変数にアクセスするためのアクセッサ。
     * @return
     */
    @SuppressWarnings("unchecked")
    private PropertyAccessor createPropertyAccessor() {
        Object parameter = query.getParameter();
        if(parameter == null) {
            return null;
        }

        if(Map.class.isAssignableFrom(parameter.getClass())) {
            return new MapAcessor((Map<String, Object>)parameter);
        }

        return new DirectFieldAccessor(parameter);

    }

    public T getSingleResult() {
        assertNotCompleted("getSingleResult");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta());
        return context.getNamedParameterJdbcTemplate().queryForObject(executedSql, paramSource, rowMapper);
    }

    public Optional<T> getOptionalResult() {
        assertNotCompleted("getOptionalResult");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta());
        final List<T> ret = context.getNamedParameterJdbcTemplate().query(executedSql, paramSource, rowMapper);
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    public List<T> getResultList() {
        assertNotCompleted("getResultList");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta());
        return context.getNamedParameterJdbcTemplate().query(executedSql, paramSource, rowMapper);
    }

    public <R> R iterate(IterationCallback<T, R> callback) {

        assertNotCompleted("iterate");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta());
        ResultSetExtractor<R> extractor = new EntityIterationResultSetExtractor<T,R>(rowMapper, callback);

        return context.getNamedParameterJdbcTemplate().query(executedSql, paramSource, extractor);

    }

}
