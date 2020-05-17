package com.github.mygreen.sqlmapper.query.sql;

import java.util.Map;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.query.QueryBase;
import com.github.mygreen.sqlmapper.sql.MapAcessor;
import com.github.mygreen.sqlmapper.sql.Node;
import com.github.mygreen.sqlmapper.sql.SqlContext;

public class SqlUpdate<T> extends QueryBase<T> {

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
    private Object[] paramValues;


    public SqlUpdate(SqlMapperContext context, Node node, Object parameter) {
        super(context);
        this.node = node;
        this.parameter = parameter;
    }

    public SqlUpdate(SqlMapperContext context, Node node) {
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
        this.paramValues = sqlContext.getBindParams().toArray();
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
     * 更新クエリを実行します。
     * @return 更新したレコード件数を返します。
     */
    public int execute() {
        assertNotCompleted("executeSqlUpdate");

        prepare();

        try {
            int rows = context.getJdbcTemplate().update(executedSql, paramValues);
            return rows;
        } finally {
            completed();
        }

    }
}
