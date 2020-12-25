package com.github.mygreen.sqlmapper.core.where.simple;

import org.springframework.util.Assert;

import lombok.Getter;

/**
 * 式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class Exp implements Term {

    /**
     * 式となるSQL
     */
    @Getter
    private final String exp;

    /**
     * パラメータへの設定値
     */
    private final Object[] values;

    /**
     * インスタンスを作成します。
     * @param exp 式
     * @param values パラメータ
     * @throws IllegalArgumentException 引数 {@literal exp}の値が空のときスローされます。
     */
    Exp(String exp, Object... values) {
        Assert.hasLength(exp, "exp should not be empty.");

        this.exp = exp;
        this.values = values;
    }

    /**
     * 指定したインデックス番号のパラメータの設定値を取得する。
     * @param index インデックス番号。0から始まる。
     * @return
     */
    public Object getValueAt(int index) {
        return values[index];
    }

    /**
     * パラメータのサイズを取得します。
     * @return 0以上の値を返します。
     */
    public int valuesSize() {
        if(values == null) {
            return 0;
        } else {
            return values.length;
        }
    }

    @Override
    public void accept(SimpleWhereVisitor visitor) {
        visitor.visit(this);
    }

}
