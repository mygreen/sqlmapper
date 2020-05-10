package com.github.mygreen.sqlmapper.query.sql;

import java.util.Map;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.query.QueryBase;
import com.github.mygreen.sqlmapper.sql.MapAcessor;
import com.github.mygreen.sqlmapper.sql.Node;
import com.github.mygreen.sqlmapper.sql.SqlContext;

public class SqlCount<T> extends QueryBase<T> {

    /**
     * パラメータです。
     */
    private Object parameter;

    /**
     * SQLの解析ノードです。
     */
    private Node node;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private MapSqlParameterSource paramSource;


    public SqlCount(SqlMapperContext context, Node node, Object parameter) {
        super(context);
        this.node = node;
        this.parameter = parameter;
    }

    public SqlCount(SqlMapperContext context, Node node) {
        this(context, node, null);
    }

    private void prepare() {

        prepareSql();

    }

    private void prepareSql() {

        SqlContext sqlContext = new SqlContext();
        sqlContext.setDialect(context.getDialect());
        sqlContext.setPropertyAccessor(createPropertyAccessor());
        sqlContext.setValueTypeRegistry(context.getValueTypeRegistry());

        node.accept(sqlContext);

        this.executedSql = sqlContext.getSql();
        this.paramSource = sqlContext.getBindParameter().getParamSource();
    }

    /**
     * SQLテンプレート中のバインド変数にアクセスするためのアクセッサ。
     * @return
     */
    @SuppressWarnings("unchecked")
    private PropertyAccessor createPropertyAccessor() {

        if(parameter == null) {
            return null;
        }

        if(Map.class.isAssignableFrom(parameter.getClass())) {
            return new MapAcessor((Map<String, Object>)parameter);
        }

        return new DirectFieldAccessor(parameter);

    }

    /**
     * カウント用のクエリを実行します。
     * @return カウント結果
     */
    public long getCount() {
        assertNotCompleted("getCount");

        prepare();

        try {
            return context.getNamedParameterJdbcTemplate().queryForObject(executedSql, paramSource, Long.class);
        } finally {
            completed();
        }

    }
}
