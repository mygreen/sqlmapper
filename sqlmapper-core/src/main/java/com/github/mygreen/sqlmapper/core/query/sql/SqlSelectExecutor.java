package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.sqlmapper.core.mapper.EntityMappingCallback;
import com.github.mygreen.sqlmapper.core.mapper.SimpleEntityRowMapper;
import com.github.mygreen.sqlmapper.core.query.QueryExecutorSupport;

public class SqlSelectExecutor<T> extends QueryExecutorSupport<SqlSelectImpl<T>> {

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private Object[] paramValues;

    public SqlSelectExecutor(SqlSelectImpl<T> query) {
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

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForObject(executedSql, rowMapper, paramValues);
    }

    public Optional<T> getOptionalResult(EntityMappingCallback<T> callback) {
        assertNotCompleted("getOptionalResult");

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        final List<T> ret = context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    public List<T> getResultList(EntityMappingCallback<T> callback) {
        assertNotCompleted("getResultList");

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
    }

    public Stream<T> getResultStream(EntityMappingCallback<T> callback) {
        assertNotCompleted("getResultStream");

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForStream(executedSql, rowMapper, paramValues);
    }

}
