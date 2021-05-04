package com.github.mygreen.sqlmapper.core.where.simple;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * SQLのWhere句の条件をANDやORでつなげていく組み立てるためのクラスです。
 *
 * @author T.TSUCHIE
 *
 */
public class SimpleWhereBuilder extends AbstractWhere<SimpleWhereBuilder> implements Where {

    /**
     * ORで区切られた塊
     */
    @Getter(AccessLevel.PROTECTED)
    private List<Where> childrenWhere = new ArrayList<>();

    @Override
    public void accept(final WhereVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * これまでに追加された条件とこれから追加される条件を {@literal OR} で結合します。
     * @return このインスタンス自身
     */
    public SimpleWhereBuilder or() {
        if(!terms.isEmpty()) {
            this.childrenWhere.add(putAsSimpleWhere());
        }
        return this;
    }

    /**
     * これまでに追加された条件と、引数で渡された条件全体を {@literal AND} で結合します。
     * @param where  {@literal AND}で結合される条件
     * @return このインスタンス自身
     */
    public SimpleWhereBuilder and(final AbstractWhere<?> where) {
        addTerm(new WhereTerm(where));
        return this;
    }
}
