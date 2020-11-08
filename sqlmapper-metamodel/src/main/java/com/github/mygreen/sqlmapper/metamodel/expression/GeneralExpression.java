package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

/**
 * 一般的な型に対する式
 *
 *
 * @author T.TSUCHIE
 * @param <T> 一般的な型
 *
 */
public abstract class GeneralExpression<T> extends DslExpression<T> {

    public GeneralExpression(Expression<T> mixin) {
        super(mixin);
    }

    public BooleanExpression eq(T right) {
        if(right == null) {
            return isNull();
        }
        return eq(Constant.create(right));
    }

    public BooleanExpression eq(Expression<? extends T> right) {
        if(right == null) {
            return isNull();
        }
        return new BooleanOperation(ComparisionOp.EQ, mixin, right);
    }

    public BooleanExpression ne(T right) {
        if(right == null) {
            return isNotNull();
        }
        return ne(Constant.create(right));
    }

    public BooleanExpression ne(Expression<? extends T> right) {
        if(right == null) {
            return isNotNull();
        }
        return new BooleanOperation(ComparisionOp.NE, mixin, right);
    }

    public BooleanExpression isNull() {
        return new BooleanOperation(UnaryOp.IS_NULL, mixin);
    }

    public BooleanExpression isNotNull() {
        return new BooleanOperation(UnaryOp.IS_NOT_NULL, mixin);
    }

    @SuppressWarnings("unchecked")
    public BooleanExpression in(T... right) {
        if(right.length == 1) {
            return eq(right[0]);
        }
        return in(Arrays.asList(right));
    }

    public BooleanExpression in(Collection<? extends T> right) {
        if(right.size() == 1) {
            return eq(right.iterator().next());
        }

        return new BooleanOperation(ComparisionOp.IN, mixin,
                Constant.create(Collections.unmodifiableCollection(right), true));
    }

    public BooleanExpression in(SubQueryExpression<T> right) {
        return new BooleanOperation(ComparisionOp.IN, mixin, right);
    }

    @SuppressWarnings("unchecked")
    public BooleanExpression notIn(T... right) {
        if(right.length == 1) {
            return ne(right[0]);
        }
        return notIn(Arrays.asList(right));
    }

    public BooleanExpression notIn(Collection<? extends T> right) {
        if(right.size() == 1) {
            return ne(right.iterator().next());
        }

        return new BooleanOperation(ComparisionOp.NOT_IN, mixin,
                Constant.create(Collections.unmodifiableCollection(right), true));
    }

    public BooleanExpression notIn(SubQueryExpression<T> right) {
        return new BooleanOperation(ComparisionOp.NOT_IN, mixin, right);
    }

 }
