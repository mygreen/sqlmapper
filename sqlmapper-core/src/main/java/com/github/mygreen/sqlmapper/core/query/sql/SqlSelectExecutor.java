package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.sqlmapper.core.mapper.EntityIterationResultSetExtractor;
import com.github.mygreen.sqlmapper.core.mapper.EntityMappingCallback;
import com.github.mygreen.sqlmapper.core.mapper.EntityRowMapper;
import com.github.mygreen.sqlmapper.core.query.IterationCallback;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;

public class SqlSelectExecutor<T> extends QueryExecutorSupport<SqlSelect<T>> {

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private Object[] paramValues;

    public SqlSelectExecutor(SqlSelect<T> query) {
        super(query);
    }

    @Override
    public void prepare() {

        prepareSql();

        completed();

    }

    private void prepareSql() {

        final ProcessResult result = query.getTemplate().process(query.getParameter());
        this.executedSql = result.getSql();
        this.paramValues = result.getParameters().toArray();

    }

    public T getSingleResult(EntityMappingCallback<T> callback) {
        assertNotCompleted("getSingleResult");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForObject(executedSql, rowMapper, paramValues);
    }

    public Optional<T> getOptionalResult(EntityMappingCallback<T> callback) {
        assertNotCompleted("getOptionalResult");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        final List<T> ret = context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    public List<T> getResultList(EntityMappingCallback<T> callback) {
        assertNotCompleted("getResultList");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
    }

    public <R> R iterate(IterationCallback<T, R> callback, EntityMappingCallback<T> rowCallback) {

        assertNotCompleted("iterate");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(rowCallback));
        ResultSetExtractor<R> extractor = new EntityIterationResultSetExtractor<T,R>(rowMapper, callback);

        return context.getJdbcTemplate().query(executedSql, extractor, paramValues);

    }

    public Stream<T> getResultStream(EntityMappingCallback<T> callback) {
        assertNotCompleted("getResultStream");

        EntityRowMapper<T> rowMapper = new EntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForStream(executedSql, rowMapper, paramValues);
    }

}
