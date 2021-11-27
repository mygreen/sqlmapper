package com.github.mygreen.sqlmapper.metamodel.support;

import com.github.mygreen.sqlmapper.metamodel.expression.Expression;
import com.github.mygreen.sqlmapper.metamodel.operator.ArithmeticOp;
import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;
import com.github.mygreen.sqlmapper.metamodel.operator.UnaryOp;

/**
 * 演算操作に関するユーティリティ
 *
 *
 * @author T.TSUCHIE
 *
 */
public class OperationUtils {

    /**
     * 式の評価結果を括弧で囲むか判定する。
     * @param parentOp 親ノードの演算子
     * @param childOp 子ノードの演算子
     * @return 括弧で囲むときは {@literal true} を返す。
     */
    public static boolean isEnclosedParenthesis(Operator parentOp, Operator childOp) {

        if(parentOp.getPriority() < childOp.getPriority()) {
            // 親ノードの演算子の優先度が高い(値が小さい)とき
            return true;
        } else if(parentOp == BooleanOp.OR && childOp == BooleanOp.AND) {
            // 親ノードがORで子ノードがANDのとき
            // 実際は括弧で囲まなくてもよいが見やすさのために囲む
            return true;
        } else if(parentOp instanceof BooleanOp && childOp == ComparisionOp.BETWEEN) {
            // 親ノードがOR/ANDで子ノードがBETWEENのとき
            // 実際は括弧で囲まなくてもよいが見やすさのために囲む
            return true;
        } else if(parentOp == UnaryOp.NOT || parentOp == UnaryOp.EXISTS || parentOp == UnaryOp.NOT_EXISTS) {
            // 親ノードが NOT /EXISTS / NOT_EXISTS 演算子のとき
            return true;
        } else if(parentOp.getClass().equals(childOp.getClass())) {
            // 親ノードと子ノードが同じ演算子グループの場合、それぞれの優先度に従う
            return parentOp.getPriority() < childOp.getPriority();
        } else if(childOp instanceof ArithmeticOp) {
            // 子ノードが算術演算子の場合、必ず囲む。
            return true;
        }

        return false;
    }

    /**
     * 式ノードをデバッグ情報形式の文字列に変換する。
     * @param exp 式ノード
     * @return デバッグ情報
     */
    public static String toDebugString(final Expression<?> exp) {

        DebugVisitor visitor = new DebugVisitor();
        DebugVisitorContext context = new DebugVisitorContext();
        exp.accept(visitor, context);

        return context.getCriteria();

    }
}
