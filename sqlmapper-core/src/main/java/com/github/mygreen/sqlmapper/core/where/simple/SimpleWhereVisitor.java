package com.github.mygreen.sqlmapper.core.where.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

/**
 *
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SimpleWhereVisitor implements WhereVisitor {

    /**
     * SQL中のパラメータ変数。
     *
     */
    private List<Object> paramValues = new ArrayList<>();

    /**
     * 組み立てたクライテリア
     */
    private StringBuilder criteria = new StringBuilder();

    /**
     * 組み立てたクライテリアを取得します。
     * @return クライテリア
     */
    public String getCriteria() {
        return criteria.toString();
    }

    /**
     * SQLのパラメータ変数を取得します。
     * SQLのプレースホルダ―順に設定されています。
     * @return SQL中のパラメータ変数。
     */
    public List<Object> getParamValues() {
        return Collections.unmodifiableList(paramValues);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException サポート対象外の引数を指定したときスローされます。
     */
    @Override
    public void visit(Where where) {
        if(where instanceof SimpleWhere) {
            visit((SimpleWhere) where);
        } else if(where instanceof SimpleWhereBuilder) {
            visit((SimpleWhereBuilder) where);
        } else {
            throw new IllegalArgumentException("unknown where class : " + where.getClass().getName());
        }
    }

    /**
     * {@link SimpleWhere} を処理します。
     * <p>条件式を {@literal AND}でつなげます。</p>
     * @param where 条件式
     */
    public void visit(final SimpleWhere where) {

        for(Term term : where.getTerms()) {
            if(criteria.length() > 0) {
                criteria.append(" and ");
            }

            term.accept(this);
        }

    }

    /**
     * {@link SimpleWhereBuilder} を処理します。
     * <p>条件式を {@literal OR}でつなげます。</p>
     * @param where 条件式
     */
    public void visit(final SimpleWhereBuilder where) {

        List<String> result = new ArrayList<>();

        // 式の項がまだ持っているときは、子要素に追加する。
        where.or();

        // 子要素の式を組み立てる。
        if(!where.getChildrenWhere().isEmpty()) {
            for(Where subWhere : where.getChildrenWhere()) {
                SimpleWhereVisitor subVisitor = new SimpleWhereVisitor();
                subVisitor.visit(subWhere);

                String sql = subVisitor.getCriteria();
                if(!sql.isEmpty()) {
                    result.add(sql);
                    paramValues.addAll(subVisitor.getParamValues());
                }
            }
        }

        if(result.size() == 1) {
            // 1つしかない場合は、そのまま設定する。
            criteria.append(result.get(0));
        } else {
            for(String sql : result) {
                if(criteria.length() > 0) {
                    criteria.append(" or ");
                }
                criteria.append("(").append(sql).append(")");
            }
        }

    }

    /**
     * 式({@link Exp})を処理します。
     * @param exp 処理対象の式。
     */
    public void visit(Exp exp) {

        criteria.append(exp.getExp());

        for(int i=0; i < exp.valuesSize(); i++) {
            paramValues.add(exp.getValueAt(i));
        }

    }

    /**
     * {@link WhereTerm}を処理します。
     * SQL式は括弧{@literal (...)}で囲みます。
     * @param whereTerm 処理対象のwhere句
     */
    public void visit(WhereTerm whereTerm) {

        Where subWhere = whereTerm.getWhere();

        SimpleWhereVisitor subVisitor = new SimpleWhereVisitor();
        subVisitor.visit(subWhere);

        criteria.append("(")
            .append(subVisitor.getCriteria())
            .append(")");

        paramValues.addAll(subVisitor.getParamValues());


    }
}
