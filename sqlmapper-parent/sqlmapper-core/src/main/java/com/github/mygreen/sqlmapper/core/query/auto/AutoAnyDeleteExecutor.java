package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.query.WhereClause;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhere;
import com.github.mygreen.sqlmapper.core.where.metamodel.MetamodelWhereVisitor;

/**
 * 任意の条件を指定して削除を行うSQLを自動生成するクエリを実行します。
 * {@link AutoAnyDeleteImpl}のクエリ実行処理の移譲先です。
 *
 * @author T.TSUCHIE
 *
 */
public class AutoAnyDeleteExecutor {

    /**
     * クエリ情報
     */
    private final AutoAnyDeleteImpl<?> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * where句
     */
    private final WhereClause whereClause = new WhereClause();

    /**
     * テーブルの別名を管理します。
     */
    private final TableNameResolver tableNameResolver = new TableNameResolver();

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータです。
     */
    private final List<Object> paramValues = new ArrayList<>();

    /**
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public AutoAnyDeleteExecutor(AutoAnyDeleteImpl<?> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {

        prepareCondition();
        prepareSql();
    }

    /**
     * 条件文の組み立てを行います
     */
    private void prepareCondition() {

        if(query.getWhere() == null) {
            return;
        }

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(
                Map.of(query.getBaseClass(), query.getEntityMeta()),
                context.getDialect(),
                context.getEntityMetaFactory(),
                tableNameResolver);
        visitor.visit(new MetamodelWhere(query.getWhere()));

        this.whereClause.addSql(visitor.getCriteria());
        this.paramValues.addAll(visitor.getParamValues());
    }

    /**
     * 実行するSQLを組み立てます。
     */
    private void prepareSql() {

        final String sql = "delete from "
                + query.getEntityMeta().getTableMeta().getFullName()
                + whereClause.toSql();

        this.executedSql = sql;
    }

    /**
     * 削除処理を実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {
        prepare();
        return context.getJdbcTemplate().update(executedSql, paramValues.toArray());
    }

}
