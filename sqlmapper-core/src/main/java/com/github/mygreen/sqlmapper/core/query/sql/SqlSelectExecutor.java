package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.mapper.EntityMappingCallback;
import com.github.mygreen.sqlmapper.core.mapper.SimpleEntityRowMapper;

public class SqlSelectExecutor<T> {

    /**
     * クエリ情報
     */
    private final SqlSelectImpl<T> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private Object[] paramValues;

    public SqlSelectExecutor(SqlSelectImpl<T> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {
        prepareSql();
    }

    private void prepareSql() {

        final ProcessResult result = query.getTemplate().process(query.getParameter());
        this.executedSql = result.getSql();
        this.paramValues = result.getParameters().toArray();

    }

    public T getSingleResult(EntityMappingCallback<T> callback) {
        prepare();

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForObject(executedSql, rowMapper, paramValues);
    }

    public Optional<T> getOptionalResult(EntityMappingCallback<T> callback) {
        prepare();

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        final List<T> ret = context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    public List<T> getResultList(EntityMappingCallback<T> callback) {
        prepare();

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
    }

    public Stream<T> getResultStream(EntityMappingCallback<T> callback) {
        prepare();

        SimpleEntityRowMapper<T> rowMapper = new SimpleEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForStream(executedSql, rowMapper, paramValues);
    }

}
