package com.github.mygreen.sqlmapper.metamodel.expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.CustomFunctionOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

/**
 * 汎用的な型に対する式。
 * <p>{@literal byte[]} 型など専用の式の型がないときに用います。
 *
 * @version 0.3
 * @author T.TSUCHIE
 * @param <T> 式のタイプ
 *
 */
public abstract class GeneralExpression<T> extends DslExpression<T> {

    public GeneralExpression(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * {@literal 左辺 = 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 = 右辺}
     */
    public BooleanExpression eq(T right) {
        if(right == null) {
            return isNull();
        }
        return eq(Constant.create(right));
    }

    /**
     * {@literal 左辺 = 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 = 右辺}
     */
    public BooleanExpression eq(Expression<? extends T> right) {
        if(right == null) {
            return isNull();
        }
        return new BooleanOperation(ComparisionOp.EQ, mixin, right);
    }

    /**
     * {@literal 左辺 <> 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 <> 右辺}
     */
    public BooleanExpression ne(T right) {
        if(right == null) {
            return isNotNull();
        }
        return ne(Constant.create(right));
    }

    /**
     * {@literal 左辺 <> 右辺} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 <> 右辺}
     */
    public BooleanExpression ne(Expression<? extends T> right) {
        if(right == null) {
            return isNotNull();
        }
        return new BooleanOperation(ComparisionOp.NE, mixin, right);
    }

    /**
     * {@literal 左辺 IS NULL} として比較する式を作成します。
     * @return {@literal 左辺 IS NULL}
     */
    public BooleanExpression isNull() {
        return new BooleanOperation(UnaryOp.IS_NULL, mixin);
    }

    /**
     * {@literal 左辺 IS NOT NULL} として比較する式を作成します。
     * @return {@literal 左辺 IS NOT NULL}
     */
    public BooleanExpression isNotNull() {
        return new BooleanOperation(UnaryOp.IS_NOT_NULL, mixin);
    }

    /**
     * {@literal 左辺 IN (右辺1, 右辺2, 右辺3, ...)} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 IN (右辺1, 右辺2, 右辺3, ...)}
     */
    @SuppressWarnings("unchecked")
    public BooleanExpression in(T... right) {
        if(right.length == 1) {
            return eq(right[0]);
        }
        return in(Arrays.asList(right));
    }

    /**
     * {@literal 左辺 IN (右辺[0], 右辺[1], 右辺[2], ...)} として比較する式を作成します。
     * @param right 右辺。指定した右辺は各要素として展開されて処理されます。
     * @return {@literal 左辺 IN (右辺[0], 右辺[1], 右辺[2], ...)}
     */
    public BooleanExpression in(Collection<? extends T> right) {
        if(right.size() == 1) {
            return eq(right.iterator().next());
        }

        return new BooleanOperation(ComparisionOp.IN, mixin,
                Constant.createCollection(Collections.unmodifiableCollection(right)));
    }

    /**
     * {@literal 左辺 IN (右辺)} として比較する式を作成します。
     * @param right 右辺。実行する際にはサブクエリとして展開されて処理されます。
     * @return {@literal 左辺 IN (右辺)}
     */
    public BooleanExpression in(SubQueryExpression<T> right) {
        return new BooleanOperation(ComparisionOp.IN, mixin, right);
    }

    /**
     * {@literal 左辺 NOT IN (右辺1, 右辺2, 右辺3, ...)} として比較する式を作成します。
     * @param right 右辺
     * @return {@literal 左辺 NOT IN (右辺1, 右辺2, 右辺3, ...)}
     */
    @SuppressWarnings("unchecked")
    public BooleanExpression notIn(T... right) {
        if(right.length == 1) {
            return ne(right[0]);
        }
        return notIn(Arrays.asList(right));
    }

    /**
     * {@literal 左辺 NOT IN (右辺[0], 右辺[1], 右辺[2], ...)} として比較する式を作成します。
     * @param right 右辺。指定した右辺は各要素として展開されて処理されます。
     * @return {@literal 左辺 NOT IN (右辺[0], 右辺[1], 右辺[2], ...)}
     */
    public BooleanExpression notIn(Collection<? extends T> right) {
        if(right.size() == 1) {
            return ne(right.iterator().next());
        }

        return new BooleanOperation(ComparisionOp.NOT_IN, mixin,
                Constant.createCollection(Collections.unmodifiableCollection(right)));
    }

    /**
     * {@literal 左辺 NOT IN (右辺)} として比較する式を作成します。
     * @param right 右辺。実行する際にはサブクエリとして展開されて処理されます。
     * @return {@literal 左辺 NOT IN (右辺)}
     */
    public BooleanExpression notIn(SubQueryExpression<T> right) {
        return new BooleanOperation(ComparisionOp.NOT_IN, mixin, right);
    }

    /**
     * 任意の関数の式を作成します。
     * <p>関数中には変数が使用できます。
     * <ul>
     *  <li>{@literal $left} : 左辺。クエリ実行時には展開されます。</li>
     *  <li>{@literal ?} : プレースホルダー。クエリ実行時に展開されます。
     *   <br>引数{@literal args} の個数と一致させる必要があります。
     *  </li>
     * </ul>
     *
     * <p>例：{@literal sample_func($left, ?, ?)}
     *
     * @since 0.3
     * @param query 関数の書式。
     * @param args 関数に渡すバインド変数を指定します。定数、{@literal Expression} を指定します。
     * @return 関数式。
     */
    @SuppressWarnings("rawtypes")
    public CustomFuntionExpression function(String query, Object... args) {
        return new CustomFunctionOperation(mixin, query, args);
    }

 }
