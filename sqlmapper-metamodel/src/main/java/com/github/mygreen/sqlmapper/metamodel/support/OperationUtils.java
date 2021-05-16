package com.github.mygreen.sqlmapper.metamodel.support;

import com.github.mygreen.sqlmapper.metamodel.operator.BooleanOp;
import com.github.mygreen.sqlmapper.metamodel.operator.ComparisionOp;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

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
        }

        return false;
    }
}
