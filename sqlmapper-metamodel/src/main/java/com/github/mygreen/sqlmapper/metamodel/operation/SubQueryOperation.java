package com.github.mygreen.sqlmapper.metamodel.operation;

import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.BooleanExpression;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

/**
 * サブクエリ式の実装。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <Q> クエリの実装クラス
 */
public class SubQueryOperation<T, Q extends SubQueryExpression<T>> implements SubQueryExpression<T> {

    /**
     * クエリの情報
     */
    private final QueryMeta queryMeta;

    public SubQueryOperation(QueryMeta queryMeta) {
        this.queryMeta = queryMeta;
    }

    @Override
    public QueryMeta getQueryMeta() {
        return queryMeta;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class<? extends T> getType() {

        if(queryMeta.getIncludesProperties().size() == 1) {
            // 抽出対象のカラムが指定されている場合は、そのプロパティの型を返す。
            return (Class)queryMeta.getIncludesProperties().stream().findFirst().get().getType();
        }

        // 不明または型が存在しない場合
        return (Class)Void.class;
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

    @Override
    public BooleanExpression exists() {
        return new BooleanOperation(UnaryOp.EXISTS, this);
    }

    @Override
    public BooleanExpression notExists() {
        return new BooleanOperation(UnaryOp.NOT_EXISTS, this);
    }

    @Override
    public SubQueryExpression<T> where(Predicate where) {
        this.queryMeta.setWhere(where);
        return this;
    }

    @Override
    public SubQueryExpression<T> orderBy(OrderSpecifier... orders) {
        this.queryMeta.addOrder(orders);
        return this;
    }

    @Override
    public SubQueryExpression<T> limit(int limit) {
        this.queryMeta.setLimit(limit);
        return this;
    }

    @Override
    public SubQueryExpression<T> offset(int offset) {
        this.queryMeta.setOffset(offset);
        return this;
    }

    @Override
    public SubQueryExpression<T> includes(final PropertyPath<?>... properties) {
        this.queryMeta.addInclude(properties);
        return this;
    }

}
