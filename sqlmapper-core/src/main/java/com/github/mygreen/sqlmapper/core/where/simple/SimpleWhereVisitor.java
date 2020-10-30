package com.github.mygreen.sqlmapper.core.where.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.core.query.IllegalQueryException;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

import lombok.RequiredArgsConstructor;

/**
 * {@link SimpleWhere} で構築した条件分を走査しSQLを組み立てるVisitor。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class SimpleWhereVisitor implements WhereVisitor {

    /**
     * 検索対象となるテーブルのエンティティ情報
     */
    private final EntityMeta entityMeta;

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

    @Override
    public void visit(final Where where) {
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

        for(ValueOperator operator : where.getOperators()) {
            if(criteria.length() > 0) {
                criteria.append(" AND ");
            }

            operator.accept(this);
        }

    }

    /**
     * {@link SimpleWhereBuilder} を処理します。
     * <p>条件式を {@literal OR}でつなげます。</p>
     * @param where 条件式
     */
    public void visit(final SimpleWhereBuilder where) {

        List<String> result = new ArrayList<>();

        // 演算子の式がまだ持っているときは、子要素に追加する。
        where.or();

        // 子要素の式を組み立てる。
        if(!where.getChildrenWhere().isEmpty()) {
            for(Where subWhere : where.getChildrenWhere()) {
                SimpleWhereVisitor subVisitor = new SimpleWhereVisitor(entityMeta);
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
                    criteria.append(" OR ");
                }
                criteria.append("(").append(sql).append(")");
            }
        }

    }

    /**
     * {@link WhereValueOperator} を処理します。
     * <p>条件式を括弧で囲みます。{@literal (A AND B)} </p>
     * @param valueOperator 条件式
     */
    public void visit(WhereValueOperator valueOperator) {

        Where subWhere = valueOperator.getWhere();

        SimpleWhereVisitor subVisitor = new SimpleWhereVisitor(entityMeta);
        subVisitor.visit(subWhere);

        criteria.append("(")
            .append(subVisitor.getCriteria())
            .append(")");

        paramValues.addAll(subVisitor.getParamValues());

    }

    /**
     * {@link SingleValueOperator} を処理します。
     * @param valueOperator 演算式
     * @throws IllegalQueryException エンティティ情報の中に含まれないプロパティ名を指定した場合にスローされます。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void visit(final SingleValueOperator valueOperator) {

        String propertyName = valueOperator.getPropertyName();
        Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        ValueType valueType = propertyMeta.get().getValueType();
        paramValues.add(valueType.getSqlParameterValue(valueOperator.getValue()));

        String columnName = propertyMeta.get().getColumnMeta().getName();
        String sql = valueOperator.getSql(columnName, "?");
        criteria.append(sql);

    }

    /**
     * {@link EmptyValueOperator} を処理します。
     * @param valueOperator 演算式
     * @throws IllegalQueryException エンティティ情報の中に含まれないプロパティ名を指定した場合にスローされます。
     */
    public void visit(final EmptyValueOperator valueOperator) {

        String propertyName = valueOperator.getPropertyName();
        Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        String columnName = propertyMeta.get().getColumnMeta().getName();
        String sql = valueOperator.getSql(columnName);
        criteria.append(sql);

    }

    /**
     * {@link MultiValueOperator} を処理します。
     * @param valueOperator 演算式
     * @throws IllegalQueryException エンティティ情報の中に含まれないプロパティ名を指定した場合にスローされます。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void visit(final MultiValueOperator valueOperator) {

        String propertyName = valueOperator.getPropertyName();
        Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        String[] varName = new String[valueOperator.getValueSize()];
        ValueType valueType = propertyMeta.get().getValueType();
        for(int i=0; i < valueOperator.getValueSize(); i++) {
            varName[i] = "?";
            paramValues.add(valueType.getSqlParameterValue(valueOperator.getValue(i)));
        }

        String columnName = propertyMeta.get().getColumnMeta().getName();
        String sql = valueOperator.getSql(columnName, varName);
        criteria.append(sql);

    }
}
