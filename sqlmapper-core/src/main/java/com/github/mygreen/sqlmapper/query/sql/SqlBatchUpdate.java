package com.github.mygreen.sqlmapper.query.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.QueryBase;
import com.github.mygreen.sqlmapper.sql.MapAcessor;
import com.github.mygreen.sqlmapper.sql.Node;
import com.github.mygreen.sqlmapper.sql.SqlContext;

import lombok.NonNull;

public class SqlBatchUpdate<T> extends QueryBase<T> {

    /**
     * パラメータです。
     */
    private final T[] parameters;

    /**
     * SQLの解析ノードです。
     */
    private Node node;

    /**
     * 実行するSQLです
     */
    private String[] executedSqlList;

    /**
     * クエリのパラメータ
     */
    private List<Object[]> batchParams;

    public SqlBatchUpdate(@NonNull SqlMapperContext context, @NonNull Node node, @NonNull T[] parameters) {
        super(context);

        if(parameters.length == 0) {
            throw new IllegalOperateException(context.getMessageBuilder().create("query.notEmptyParameter")
                    .format());
        }

        this.node = node;
        this.parameters = parameters;
    }

    @SuppressWarnings("unchecked")
    public SqlBatchUpdate(@NonNull SqlMapperContext context, @NonNull Node node, @NonNull Collection<T> parameters) {
        this(context, node, (T[])parameters.toArray());
    }

    private void prepare() {

        prepareSql();

    }

    private void prepareSql() {

        final int size = parameters.length;
        this.executedSqlList = new String[size];
        this.batchParams = new ArrayList<>(size);

        for(int i=0; i < size; i++) {
            SqlContext sqlContext = new SqlContext();
            sqlContext.setDialect(context.getDialect());
            sqlContext.setPropertyAccessor(createPropertyAccessor(parameters[i]));
            sqlContext.setValueTypeRegistry(context.getValueTypeRegistry());

            node.accept(sqlContext);

            this.executedSqlList[i] = sqlContext.getSql();
            this.batchParams.add(sqlContext.getBindParams().toArray());

        }
    }

    /**
     * SQLテンプレート中のバインド変数にアクセスするためのアクセッサ。
     * @return
     */
    @SuppressWarnings("unchecked")
    private PropertyAccessor createPropertyAccessor(final T parameter) {

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
    public int[] execute() {
        assertNotCompleted("executeSqlUpdate");

        prepare();

        try {
            int size = parameters.length;
            int[] result = new int[size];

            for(int i=0; i < size; i++) {
                int rows = context.getJdbcTemplate().update(executedSqlList[i], batchParams.get(i));
                result[i] = rows;
            }

            return result;

        } finally {
            completed();
        }

    }

}
