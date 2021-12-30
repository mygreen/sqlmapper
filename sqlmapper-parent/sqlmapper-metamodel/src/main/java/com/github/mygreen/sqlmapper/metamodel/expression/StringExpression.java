package com.github.mygreen.sqlmapper.metamodel.expression;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.StringOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.LikeOp;

/**
 * 文字列型の式を表現します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public abstract class StringExpression extends ComparableExpression<String> {

    public StringExpression(Expression<String> mixin) {
        super(mixin);
    }

    /**
     * {@literal 左辺 LIKE 右辺} として比較する式を作成します。
     * @param str LIKE検索対象の文字列。特殊文字のエスケープは予め実施しておく必要があります。
     * @return {@literal 左辺 LIKE 右辺}
     */
    public BooleanExpression like(String str) {
        return like(Constant.createString(str));
    }

    /**
     * {@literal 左辺 LIKE 右辺} として比較する式を作成します。
     * @since 0.3
     * @param str LIKE検索対象の文字列。特殊文字のエスケープは予め実施しておく必要があります。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE 右辺}
     */
    public BooleanExpression like(String str, char escape) {
        return like(Constant.createString(str), escape);
    }

    /**
     * {@literal 左辺 LIKE 右辺} として比較する式を作成します。
     * @param str LIKE検索対象の文字列。特殊文字のエスケープは予め実施しておく必要があります。
     * @return {@literal 左辺 LIKE 右辺}
     */
    public BooleanExpression like(Expression<String> str) {
        return new BooleanOperation(LikeOp.LIKE, mixin, str);
    }

    /**
     * {@literal 左辺 LIKE 右辺} として比較する式を作成します。
     * @since 0.3
     * @param str LIKE検索対象の文字列。特殊文字のエスケープは予め実施しておく必要があります。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE 右辺}
     */
    public BooleanExpression like(Expression<String> str, char escape) {
        return new BooleanOperation(LikeOp.LIKE, mixin, str,  Constant.createChar(escape));
    }

    /**
     * 部分一致 {@literal 左辺 LIKE %右辺%} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @return {@literal 左辺 LIKE %右辺%}
     */
    public BooleanExpression contains(String str) {
        return contains(Constant.createString(str));
    }

    /**
     * 部分一致 {@literal 左辺 LIKE %右辺%} として比較する式を作成します。
     * @since 0.3
     * @param str 検索対象の文字列。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE %右辺%}
     */
    public BooleanExpression contains(String str, char escape) {
        return contains(Constant.createString(str), escape);
    }

    /**
     * 部分一致 {@literal 左辺 LIKE %右辺%} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @return {@literal 左辺 LIKE %右辺%}
     */
    protected BooleanExpression contains(Expression<String> str) {
        return new BooleanOperation(LikeOp.CONTAINS, mixin, str);
    }

    /**
     * 部分一致 {@literal 左辺 LIKE %右辺%} として比較する式を作成します。
     * @since 0.3
     * @param str 検索対象の文字列。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE %右辺%}
     */
    protected BooleanExpression contains(Expression<String> str, char escape) {
        return new BooleanOperation(LikeOp.CONTAINS, mixin, str,  Constant.createChar(escape));
    }

    /**
     * 前方一致 {@literal 左辺 LIKE %右辺} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @return {@literal 左辺 LIKE %右辺}
     */
    public BooleanExpression starts(String str) {
        return starts(Constant.createString(str));
    }

    /**
     * 前方一致 {@literal 左辺 LIKE %右辺} として比較する式を作成します。
     * @since 0.3
     * @param str 検索対象の文字列。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE %右辺}
     */
    public BooleanExpression starts(String str, char escape) {
        return starts(Constant.createString(str), escape);
    }

    /**
     * 前方一致 {@literal 左辺 LIKE %右辺} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @return {@literal 左辺 LIKE %右辺}
     */
    protected BooleanExpression starts(Expression<String> str) {
        return new BooleanOperation(LikeOp.STARTS, mixin, str);
    }

    /**
     * 前方一致 {@literal 左辺 LIKE %右辺} として比較する式を作成します。
     * @since 0.3
     * @param str 検索対象の文字列。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE %右辺}
     */
    protected BooleanExpression starts(Expression<String> str, char escape) {
        return new BooleanOperation(LikeOp.STARTS, mixin, str, Constant.createChar(escape));
    }


    /**
     * 後方一致 {@literal 左辺 LIKE 右辺%} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @return {@literal 左辺 LIKE 右辺%}
     */
    public BooleanExpression ends(String str) {
        return ends(Constant.createString(str));
    }

    /**
     * 後方一致 {@literal 左辺 LIKE 右辺%} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE 右辺%}
     */
    public BooleanExpression ends(String str, char escape) {
        return ends(Constant.createString(str), escape);
    }

    /**
     * 後方一致 {@literal 左辺 LIKE 右辺%} として比較する式を作成します。
     * @param str 検索対象の文字列。
     * @return {@literal 左辺 LIKE 右辺%}
     */
    protected BooleanExpression ends(Expression<String> str) {
        return new BooleanOperation(LikeOp.ENDS, mixin, str);
    }

    /**
     * 後方一致 {@literal 左辺 LIKE 右辺%} として比較する式を作成します。
     * @since 0.3
     * @param str 検索対象の文字列。
     * @param escape エスケープ文字。
     * @return {@literal 左辺 LIKE 右辺%}
     */
    protected BooleanExpression ends(Expression<String> str, char escape) {
        return new BooleanOperation(LikeOp.ENDS, mixin, str, Constant.createChar(escape));
    }

    /**
     * 小文字に変換する関数 {@literal LOWER(左辺)} を返します。
     * @return {@literal LOWER(左辺)}
     */
    public StringExpression lower() {
        return new StringOperation(FunctionOp.LOWER, mixin);
    }

    /**
     * 大文字に変換する関数 {@literal UPPER(左辺)} を返します。
     * @return {@literal UPPER(左辺)}
     */
    public StringExpression upper() {
        return new StringOperation(FunctionOp.UPPER, mixin);
    }

    /**
     * 文字列を結合します。
     * @since 0.3
     * @param str 結合する文字列。
     * @return {@literal CONCAT(左辺, 右辺)}
     */
    public StringExpression concat(String str) {
        return new StringOperation(FunctionOp.CONCAT, mixin, Constant.createString(str));
    }

    /**
     * 文字列を結合します。
     * @since 0.3
     * @param str 結合する文字列。
     * @return {@literal CONCAT(左辺, 右辺)}
     */
    public StringExpression concat(Expression<String> str) {
        return new StringOperation(FunctionOp.CONCAT, mixin, str);
    }
}
