package com.github.mygreen.sqlmapper.core.where.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

/**
 * 入力された項目をANDやORでつなげていくような検索条件を組み立てるクラスです。
 *
 * @author T.TSUCHIE
 *
 */
public class SimpleWhereBuilder extends AbstractWhere<SimpleWhereBuilder> {

    /**
     * ORで区切られた塊
     */
    private List<Where> children = new ArrayList<>();

    /**
     * これまでに追加された条件とこれから追加される条件をORで結合します。
     * @return このインスタンス自身
     */
    public SimpleWhereBuilder or() {
        this.children.add(putAsSimplexWhere());
        return this;
    }

    /**
     * これまでに追加された条件と、引数で渡された条件全体をANDで結合します。
     * @param where  ANDで結合される条件
     * @return このインスタンス自身
     */
    public SimpleWhereBuilder and(final Where where) {
        super.operators.add(new WhereValueOperator(where));
        return this;
    }

    /**
     * ORで区切られた条件の塊を取得します。
     * @return OR条件の塊。
     */
    protected List<Where> getChildrenWhere() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void accept(WhereVisitor visitor) {
        visitor.visit(this);
    }

}
